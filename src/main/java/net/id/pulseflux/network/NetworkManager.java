package net.id.pulseflux.network;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.id.pulseflux.block.transport.LogisticComponentBlock;
import net.id.pulseflux.block.transport.PipeBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.level.LevelProperties;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static net.id.pulseflux.PulseFlux.*;
import static net.id.pulseflux.PulseFluxComponents.*;
import static net.minecraft.client.render.WorldRenderer.DIRECTIONS;

public class NetworkManager implements AutoSyncedComponent, ServerTickingComponent {

    public final World world;
    public final Object2ObjectOpenHashMap<UUID, TransferNetwork<?, ?>> managedNetworks = new Object2ObjectOpenHashMap<>(32);

    public NetworkManager(World world) {
        this.world = world;
        if(world.getLevelProperties() instanceof LevelProperties)
            LOG.info((!world.isClient() ? "Initializing 'ServerLevel" : "Pairing 'Client") + "[" + ((LevelProperties) world.getLevelProperties()).getLevelName() + "]'/" + world.getRegistryKey().getValue().toString() + " network manager");
    }

    public static @NotNull NetworkManager getNetworkManager(World world) {
        return NETWORK_MANAGER_KEY.get(world);
    }

    public static void sync(World world) {
        NETWORK_MANAGER_KEY.sync(world);
    }

    @Override
    public void serverTick() {
        for (TransferNetwork<?, ?> network : managedNetworks.values()) {
            if(!network.isComponentless()) {
                network.tick();
            }
            else if(network.removeIfEmpty()) {
                managedNetworks.remove(network.networkId);
                network.postRemove();
                continue;
            }

            if(!network.invalidComponents.isEmpty()) {

                managedNetworks.remove(network.networkId);

                Queue<BlockPos> nextGen = new LinkedList<>();
                Set<BlockPos> traversedBlocks = new HashSet<>();
                List<TransferNetwork<?, ?>> newNetworks = new ArrayList<>();

                for (BlockPos invalidatedComponent : network.invalidComponents) {
                    traversedBlocks.add(invalidatedComponent);

                    for (Direction baseDir : DIRECTIONS) {

                        BlockPos start = invalidatedComponent.offset(baseDir);
                        traversedBlocks.add(start);
                        nextGen.add(start);

                        while(!nextGen.isEmpty()) {
                            BlockPos next = nextGen.poll();
                            BlockState state = world.getBlockState(next);
                            Block block = state.getBlock();

                            if(block instanceof LogisticComponentBlock component && network.isComponentValid(next, state)) {
                                TransferNetwork<?, ?> newNetwork = joinOrCreateNetwork(world, next);
                                component.switchNetwork(next, newNetwork, this);

                                if(!managedNetworks.containsValue(newNetwork)) {
                                    managedNetworks.put(newNetwork.networkId, newNetwork);
                                }

                                if(!newNetworks.contains(newNetwork) && network != newNetwork) {
                                    newNetworks.add(newNetwork);
                                }

                                for (Direction headDir : DIRECTIONS) {
                                    BlockPos head = next.offset(headDir);

                                    if(!traversedBlocks.contains(head)) {
                                        traversedBlocks.add(head);
                                        nextGen.add(head);
                                    }
                                }
                            }
                        }
                    }
                }

                network.processDescendants(newNetworks, this);
            }
        }
    }

    public <T extends TransferNetwork<T, ?>> Optional<T> tryFetchNetwork(UUID networkId) {
        return (Optional<T>) Optional.ofNullable(managedNetworks.get(networkId));
    }

    public @NotNull <T extends TransferNetwork<T, ?>> T joinOrCreateNetwork(@NotNull World world, @NotNull BlockPos pos) {

        Iterator<UUID> networkIds = managedNetworks.keySet().iterator();
        List<T> adjNetworks = new ArrayList<>();
        BlockState component = world.getBlockState(pos);
        LogisticComponentBlock<T> componentBlock = (LogisticComponentBlock<T>) component.getBlock();

        while(networkIds.hasNext() && adjNetworks.size() < 6) {
            UUID networkId = networkIds.next();
            var network = managedNetworks.get(networkId);

            if(network.containsComponent(pos, component)) {
                return (T) network;
            }
            else {
                searchForNeighbours: {
                    for (Direction direction : DIRECTIONS) {
                        var offPos = pos.offset(direction);
                        var neighbourComponent = world.getBlockState(offPos);

                        if(neighbourComponent.getBlock() instanceof LogisticComponentBlock<?> neighbourBlock) {
                            if(componentBlock.isCompatibleWith(network) && network.containsComponent(offPos, neighbourComponent)) {
                                if(neighbourBlock instanceof PipeBlock<?> neighbourPipe && !neighbourPipe.canConnectTo(world, component, pos, direction.getOpposite())) {
                                    continue;
                                }

                                if(componentBlock instanceof PipeBlock<T> pipe && !pipe.canConnectTo(world, neighbourComponent, offPos, direction)) {
                                    continue;
                                }

                                adjNetworks.add((T) network);
                                break searchForNeighbours;
                            }
                        }
                    }
                }

            }
        }

        if(!adjNetworks.isEmpty()) {
            T survivor;
            if(adjNetworks.size() == 1) {
                survivor = adjNetworks.get(0);
                survivor.appendComponent(pos);
            }
            else {
                survivor = adjNetworks.remove(0);
                adjNetworks.stream().filter(network -> network != survivor).forEach(network -> network.yieldTo(survivor, this));
                survivor.appendComponent(pos);
            }
            return survivor;
        }

        T network = componentBlock.createNetwork(world, UUID.randomUUID());
        network.setName(network.getOrCreateName(world, pos));
        managedNetworks.put(network.networkId, network);
        if(!world.isClient()) {
            LOG.info("Created new Transfer Network at " + pos);
        }
        network.appendComponent(pos);
        return network;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        if(world.isClient())
            return;

        int savedNetworks = tag.getInt("size");

        for (int i = 0; i < savedNetworks; i++) {
            var id = tag.getUuid("id_" + i);
            var reconstructor = Reconstructors.getReconstructor(Identifier.tryParse(tag.getString("reconstructor_" + i)));

            TransferNetwork<?, ?> network = reconstructor.assemble(world, id, tag.getCompound("networkData_" + i));

            if(network.getConnectedComponents() < 1 && network.removeIfEmpty()) {
                LOG.error("Network " + network.networkId.toString() + " is empty, skipping!");
                continue;
            }

            managedNetworks.put(network.networkId, network);
            LOG.info("Network loaded: " + network);
        }

        sync(world);
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        List<UUID> uuids = managedNetworks.keySet().stream().toList();
        tag.putInt("size", uuids.size());

        for (int i = 0; i < uuids.size(); i++) {
            UUID networkId = uuids.get(i);

            var network = managedNetworks.get(networkId);

            tag.putUuid("id_" + i, networkId);
            tag.put("networkData_" + i, network.save(new NbtCompound()));
            tag.putString("reconstructor_" + i, Reconstructors.getId(network.getReconstructor()).toString());
        }
    }
}
