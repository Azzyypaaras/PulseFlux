package net.id.pulseflux.block.transport;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.id.incubus_core.systems.Material;
import net.id.pulseflux.blockentity.transport.FluidPipeEntity;
import net.id.pulseflux.network.FluidNetwork;
import net.id.pulseflux.network.NetworkManager;
import net.id.pulseflux.network.TransferNetwork;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class FluidPipeBlock extends PipeBlock<FluidNetwork>{

    public FluidPipeBlock(Settings settings, Material material) {
        super(settings, FluidStorage.SIDED);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }

    @Override
    public void switchNetwork(BlockPos pos, FluidNetwork network, NetworkManager manager) {
        var world = manager.world;
        if(world.getBlockEntity(pos) instanceof FluidPipeEntity pipe) {

        }
    }

    @Override
    public FluidNetwork createNetwork(World world, UUID id) {
        return new FluidNetwork(world, id);
    }
}
