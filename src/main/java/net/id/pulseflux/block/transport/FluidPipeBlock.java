package net.id.pulseflux.block.transport;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.id.incubus_core.systems.Material;
import net.id.incubus_core.systems.MaterialProvider;
import net.id.pulseflux.network.FluidNetwork;
import net.id.pulseflux.network.NetworkManager;
import net.id.pulseflux.network.TransferNetwork;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class FluidPipeBlock extends PipeBlock<FluidNetwork> implements MaterialProvider {

    private final Material material;
    private final long volume;

    public FluidPipeBlock(Settings settings, Material material, long volume) {
        super(settings, FluidStorage.SIDED);
        this.material = material;
        this.volume = volume;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FluidPipeBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return FluidPipeBlockEntity::tick;
    }

    @Override
    public void switchNetwork(BlockPos pos, FluidNetwork network, NetworkManager manager) {
        if(manager.world.getBlockEntity(pos) instanceof FluidPipeBlockEntity pipe) {
            pipe.trySwitchNetwork(network, manager);
        }
    }

    @Override
    public Optional<FluidNetwork> getParentNetwork(World world, BlockPos pos) {
        return ((FluidPipeBlockEntity) world.getBlockEntity(pos)).getParentNetwork();
    }

    @Override
    public boolean isCompatibleWith(TransferNetwork<?, ?> network) {
        return network instanceof FluidNetwork;
    }

    @Override
    public FluidNetwork createNetwork(World world, UUID id) {
        return new FluidNetwork(world, id);
    }

    @Override
    public Material getMaterial(@Nullable Direction direction) {
        return material;
    }

    public long volume() {
        return volume;
    }
}
