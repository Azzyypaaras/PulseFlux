package net.id.pulseflux.block.transport;

import net.id.pulseflux.block.base.PFBlockEntity;
import net.id.pulseflux.block.PulseFluxBlockEntities;
import net.id.pulseflux.network.FluidNetwork;
import net.id.pulseflux.network.NetworkManager;
import net.id.pulseflux.network.TransferNetwork;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

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

        if(parentNetwork.isEmpty() && !world.isClient()) {
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
