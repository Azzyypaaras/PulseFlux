package net.id.pulseflux.blockentity.transport;

import net.id.pulseflux.blockentity.PFBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FluidPipeEntity extends PFBlockEntity {

    public FluidPipeEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void tick(BlockPos pos, BlockState state) {}

    @Override
    protected boolean initialize(World world, BlockPos pos, BlockState state) {

        return true;
    }
}
