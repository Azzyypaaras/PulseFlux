package net.id.pulseflux.network;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.id.pulseflux.PulseFlux;
import net.id.pulseflux.block.transport.LogisticComponentBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.level.LevelProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.id.pulseflux.PulseFlux.*;
import static net.id.pulseflux.PulseFluxComponents.*;
import static net.minecraft.client.render.WorldRenderer.DIRECTIONS;

public class NetworkManager implements AutoSyncedComponent, ServerTickingComponent {

    public final World world;
    public final Object2ObjectOpenHashMap<UUID, TransferNetwork<?>> managedNetworks = new Object2ObjectOpenHashMap<>(32);

    public NetworkManager(World world) {
        this.world = world;
        if(world.getLevelProperties() instanceof LevelProperties)
            LOG.info((!world.isClient() ? "Initializing 'ServerLevel" : "Pairing 'Client") + "[" + ((LevelProperties) world.getLevelProperties()).getLevelName() + "]'/" + world.getRegistryKey().getValue().toString() + " network manager");
    }

    public static @NotNull NetworkManager getNetworkManager(World world) {
        return NETWORK_MANAGER_KEY.get(world);
    }

    @Override
    public void serverTick() {
        for (TransferNetwork<?> network : managedNetworks.values()) {
            if(!network.isEmpty()) {
                network.tick();
            }
            else if(network.removeIfEmpty()) {
                managedNetworks.remove(network.networkId);
                network.postRemove();
                continue;
            }

            if(!network.invalidComponents.isEmpty()) {

                Queue<BlockPos> nextGen = new LinkedList<>();
                Set<BlockPos> traversedBlocks = new HashSet<>();

                for (BlockPos invalidCable : network.invalidComponents) {
                    traversedBlocks.add(invalidCable);

                    for (Direction baseDir : DIRECTIONS) {

                        BlockPos start = invalidCable.offset(baseDir);
                        traversedBlocks.add(start);
                        nextGen.add(start);

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
            }
        }
    }

    public @NotNull TransferNetwork<?> joinOrCreateNetwork(@NotNull World world, @NotNull BlockPos pos) {

        Iterator<UUID> networkIds = managedNetworks.keySet().iterator();
        List<TransferNetwork<?>> adjNetworks = new ArrayList<>();
        BlockState component = world.getBlockState(pos);
        LogisticComponentBlock componentBlock = (LogisticComponentBlock) component.getBlock();

        while(networkIds.hasNext() && adjNetworks.size() < 6) {
            UUID networkId = networkIds.next();
            var network = managedNetworks.get(networkId);

            if(network.containsComponent(pos, component)) {
                return network;
            }
            else {
                for (Direction direction : DIRECTIONS) {
                    var offPos = pos.offset(direction);

                    if(network.containsComponent(offPos, world.getBlockState(offPos))) {
                        adjNetworks.add(network);
                    }
                }

            }
        }

        if(!adjNetworks.isEmpty()) {
            TransferNetwork<?> survivor;
            if(adjNetworks.size() == 1) {
                survivor = adjNetworks.get(0);
                survivor.appendComponent(pos);
            }
            else {
                survivor = componentBlock.createNetwork(world, UUID.randomUUID());
                adjNetworks.stream().filter(network -> network != survivor).forEach(network -> network.yieldTo(survivor, this));
                survivor.appendComponent(pos);
                managedNetworks.put(survivor.networkId, survivor);
            }
            return survivor;
        }

        TransferNetwork<?> network = componentBlock.createNetwork(world, UUID.randomUUID());
        managedNetworks.put(network.networkId, network);
        if(!world.isClient()) {
            LOG.info("Created new Power Network at " + pos.toString());
        }
        network.appendComponent(pos);
        return network;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {

    }

    @Override
    public void writeToNbt(NbtCompound tag) {

    }
}
