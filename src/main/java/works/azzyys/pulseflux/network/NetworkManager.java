package works.azzyys.pulseflux.network;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import works.azzyys.pulseflux.block.transport.LogisticComponentBlock;
import works.azzyys.pulseflux.block.transport.PipeBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.level.LevelProperties;
import org.jetbrains.annotations.NotNull;
import works.azzyys.pulseflux.PulseFlux;
import works.azzyys.pulseflux.PulseFluxComponents;

import java.util.*;

import static net.minecraft.client.render.WorldRenderer.DIRECTIONS;

public final class NetworkManager implements AutoSyncedComponent, CommonTickingComponent {

    public final World world;
    public final Object2ObjectOpenHashMap<UUID, TransferNetwork<?>> managedNetworks = new Object2ObjectOpenHashMap<>(32);
    private boolean hardSyncScheduled = false;

    public NetworkManager(World world) {
        this.world = world;
        if(world.getLevelProperties() instanceof LevelProperties)
            PulseFlux.LOG.info((!world.isClient() ? "Initializing 'ServerLevel" : "Pairing 'Client") + "[" + ((LevelProperties) world.getLevelProperties()).getLevelName() + "]'/" + world.getRegistryKey().getValue().toString() + " network manager");
    }

    public static @NotNull NetworkManager getNetworkManager(World world) {
        return PulseFluxComponents.NETWORK_MANAGER_KEY.get(world);
    }

    public static void sync(World world) {
        PulseFluxComponents.NETWORK_MANAGER_KEY.sync(world);
    }

    @Override
    public void tick() {
        var managed = managedNetworks.clone();
        boolean shouldSync = false;

        for (TransferNetwork<?> network : managed.values()) {
            if(!network.isComponentless()) {
                network.tick();
            }
            else if(network.removeIfEmpty()) {
                managedNetworks.remove(network.networkId);
                network.postRemove();
                shouldSync = true;
                hardSyncScheduled = true;
                continue;
            }

            if(!network.invalidComponents.isEmpty()) {

                managedNetworks.remove(network.networkId);

                Queue<BlockPos> nextGen = new LinkedList<>();
                Set<BlockPos> traversedBlocks = new HashSet<>();
                List<TransferNetwork<?>> newNetworks = new ArrayList<>();
                List<InvalidatedComponent> exceptionalComponents = new ArrayList<>();

                for (InvalidatedComponent invalidatedComponent : network.invalidComponents) {

                    var invalidPos = invalidatedComponent.component();
                    var reason = invalidatedComponent.reason();

                    if (reason == InvalidatedComponent.Reason.WRENCHED) {
                        exceptionalComponents.add(invalidatedComponent);
                    }

                    traversedBlocks.add(invalidPos);

                    for (Direction baseDir : DIRECTIONS) {

                        BlockPos start = invalidPos.offset(baseDir);
                        nextGen.add(start);
                        traversedBlocks.add(start);

                        while(!nextGen.isEmpty()) {
                            BlockPos next = nextGen.poll();
                            BlockState state = world.getBlockState(next);
                            Block block = state.getBlock();

                            if(block instanceof LogisticComponentBlock component && network.isComponentValid(next, state)) {
                                TransferNetwork<?> newNetwork = joinOrCreateNetwork(world, next);
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

                for (InvalidatedComponent exception : exceptionalComponents) {
                    if (exception.reason() == InvalidatedComponent.Reason.WRENCHED) {
                        var pos = exception.component();

                        if (world.getBlockState(pos).getBlock() instanceof LogisticComponentBlock componentBlock) {
                            var exceptionalNetwork = joinOrCreateNetwork(world, pos);
                            componentBlock.switchNetwork(pos, exceptionalNetwork, this);

                            if(!newNetworks.contains(exceptionalNetwork)) {
                                newNetworks.add(exceptionalNetwork);
                            }
                        }
                    }
                }

                network.processDescendants(newNetworks, this);
                shouldSync = true;
                hardSyncScheduled = true;
            }
        }

        if (world.getTime() % 20 == 0 || shouldSync)
            sync(world);
    }

    public <T extends TransferNetwork<T>> Optional<T> tryFetchNetwork(UUID networkId) {
        return (Optional<T>) Optional.ofNullable(managedNetworks.get(networkId));
    }

    public @NotNull <T extends TransferNetwork<T>> T joinOrCreateNetwork(@NotNull World world, @NotNull BlockPos pos) {

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
                hardSyncScheduled = true;
            }
            sync(world);
            return survivor;
        }

        T network = componentBlock.createNetwork(world, UUID.randomUUID());
        network.setName(network.getOrCreateName(world, pos));
        managedNetworks.put(network.networkId, network);
        if(!world.isClient()) {
            PulseFlux.LOG.debug("Created new Transfer Network at " + pos);
        }
        network.appendComponent(pos);

        hardSyncScheduled = true;
        sync(world);

        return network;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        boolean synchronizing = tag.contains("hardSync");
        boolean hardSync = tag.getBoolean("hardSync");

        if (synchronizing) {
            applySynchronization(tag, hardSync);
        }
        else if(!world.isClient()) {
            load(tag, true, true);
        }
    }

    public void applySynchronization(NbtCompound tag, boolean hard) {
        int savedNetworks = tag.getInt("size");

        if (hard) {
            load(tag, false, false);
        }
        else {
            for (int i = 0; i < savedNetworks; i++) {
                var id = tag.getUuid("id_" + i);
                var reconstructor = Reconstructors.getReconstructor(Identifier.tryParse(tag.getString("reconstructor_" + i)));
                var networkData = tag.getCompound("networkData_" + i);
                var network = managedNetworks.get(id);
                if (network != null) {
                    network.softSync(networkData);
                }
                else {
                    TransferNetwork<?> newNetwork = reconstructor.assemble(world, id, networkData);
                    if (newNetwork.getConnectedComponents() > 0)
                        managedNetworks.put(id, newNetwork);
                }
            }
        }

        hardSyncScheduled = false;
    }

    public void load(NbtCompound tag, boolean log, boolean sync) {
        int savedNetworks = tag.getInt("size");

        managedNetworks.clear();
        for (int i = 0; i < savedNetworks; i++) {
            var id = tag.getUuid("id_" + i);
            var reconstructor = Reconstructors.getReconstructor(Identifier.tryParse(tag.getString("reconstructor_" + i)));

            TransferNetwork<?> network = reconstructor.assemble(world, id, tag.getCompound("networkData_" + i));

            if(network.getConnectedComponents() < 1 && network.removeIfEmpty()) {
                if (log)
                    PulseFlux.LOG.debug("Empty network found - " + network + " - Skipping!");
                continue;
            }

            managedNetworks.put(network.networkId, network);
            if (log)
                PulseFlux.LOG.debug("Network loaded: " + network);
        }

        if (sync)
            sync(world);
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        boolean softSyncWrite = false;
        if (tag.contains("hardSync") && !tag.getBoolean("hardSync")) {
            softSyncWrite = true;
        }

        List<UUID> uuids = managedNetworks.keySet().stream().toList();
        tag.putInt("size", uuids.size());

        for (int i = 0; i < uuids.size(); i++) {
            UUID networkId = uuids.get(i);

            var network = managedNetworks.get(networkId);

            tag.putUuid("id_" + i, networkId);
            tag.put("networkData_" + i, network.save(new NbtCompound(), softSyncWrite));
            tag.putString("reconstructor_" + i, Reconstructors.getId(network.getReconstructor()).toString());
        }
    }

    @Override
    public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity recipient) {
        NbtCompound tag = new NbtCompound();
        tag.putBoolean("hardSync", hardSyncScheduled);
        this.writeToNbt(tag);
        buf.writeNbt(tag);

        if (hardSyncScheduled)
            hardSyncScheduled = false;
    }
}
