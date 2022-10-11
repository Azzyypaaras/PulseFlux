package net.id.pulseflux.block.transport;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.id.pulseflux.block.base.PFBlockEntity;
import net.id.pulseflux.block.PulseFluxBlockEntities;
import net.id.pulseflux.network.FluidNetwork;
import net.id.pulseflux.network.NetworkManager;
import net.id.pulseflux.network.TransferNetwork;
import net.id.pulseflux.util.TransferHelper;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class FluidPipeBlockEntity extends PFBlockEntity {

    @NotNull
    private Optional<FluidNetwork> parentNetwork = Optional.empty();
    @NotNull
    private Optional<UUID> networkId = Optional.empty();

    public FluidPipeBlockEntity(BlockPos pos, BlockState state) {
        super(PulseFluxBlockEntities.WOODEN_FLUID_PIPE_TYPE, pos, state);
    }

    @Override
    protected void tick(BlockPos pos, BlockState state) {
        if(world == null)
            return;

        if(world.isClient())
            return;

        if(parentNetwork.isPresent()) {
            var network = parentNetwork.get();
            var storageConnections = getConnections(true, true);

            for (Direction connection : storageConnections) {
                if (connection == Direction.DOWN) {
                    var storage = FluidStorage.SIDED.find(world, pos.offset(connection), connection);
                    if (storage != null)
                        StorageUtil.move(network, storage, (FluidVariant) -> true, Long.MAX_VALUE, null);
                }
                if (connection == Direction.UP) {
                    var storage = FluidStorage.SIDED.find(world, pos.offset(connection), connection);
                    if (storage != null)
                        StorageUtil.move(storage, network, (FluidVariant) -> true, Long.MAX_VALUE, null);
                }
            }
        }
        else {
            var manager = NetworkManager.getNetworkManager(world);

            networkId.ifPresentOrElse(
                    uuid -> {
                        parentNetwork = manager.tryFetchNetwork(uuid);
                        networkId = parentNetwork.map(network -> network.networkId);
                    },
                    () ->{
                        parentNetwork = Optional.of(manager.joinOrCreateNetwork(world, pos));
                        networkId = parentNetwork.map(network -> network.networkId);
                    }
                    );
            markDirty();
            NetworkManager.sync(world);
            sync();
        }
    }

    /**
     * For your sanity, I would suggest against requesting storage connections without excluding self connections.
     * @param noSelf Whether connections leading to components of the network this pipe is part of should be included
     * @param storageOnly Only include connections leading to {@link Storage<FluidVariant>}
     */
    public List<Direction> getConnections(boolean noSelf, boolean storageOnly) {
        return Direction.stream()
                .filter(dir -> getCachedState().get(PipeBlock.CONNECTIONS.get(dir)))
                .filter(dir -> {
                    var offPos = pos.offset(dir);
                    var be = world.getBlockEntity(offPos);
                    if(noSelf) {
                        var parentNetwork = getParentNetwork().orElse(null);
                        if(parentNetwork != null && be instanceof FluidPipeBlockEntity pipe && pipe.getParentNetwork().orElse(null) == parentNetwork)
                            return false;
                    }
                    if(storageOnly) {
                        Storage<FluidVariant> storage;
                        if(be != null) {
                            storage = FluidStorage.SIDED.find(world, offPos, null, be, dir);
                        }
                        else {
                            storage = FluidStorage.SIDED.find(world, offPos, dir);
                        }
                        return storage != null;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    public void trySwitchNetwork(@NotNull FluidNetwork network, @NotNull NetworkManager manager) {
        if(manager.world != this.world) {
            throw new IllegalStateException("Cable tried to switch network at an invalid time! - " + pos.toString() + " - network - " + parentNetwork.map(TransferNetwork::toString).orElse("NONE"));
        }
        this.parentNetwork = Optional.of(network);
        this.networkId = Optional.of(network.networkId);
        markDirty();
        sync();
    }

    public @NotNull Optional<FluidNetwork> getParentNetwork() {
        return parentNetwork;
    }

    @Override
    public void markRemoved() {
        parentNetwork.ifPresent(TransferNetwork::requestNetworkRevalidation);
        super.markRemoved();
    }

    @Override
    public void save(NbtCompound nbt) {
        super.save(nbt);
        networkId.ifPresent(id -> nbt.putUuid("parent", id));
    }

    @Override
    public void load(NbtCompound nbt) {
        super.load(nbt);
        networkId = Optional.ofNullable(nbt.getUuid("parent"));
    }

    @Override
    public void saveClient(NbtCompound nbt) {
        networkId.ifPresent(id -> nbt.putUuid("parent", id));
    }

    @Override
    public void loadClient(NbtCompound nbt) {
        networkId = nbt.contains("parent") ? Optional.ofNullable(nbt.getUuid("parent")) : Optional.empty();
    }
}
