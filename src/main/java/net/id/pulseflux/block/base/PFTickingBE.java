package net.id.pulseflux.block.base;

import net.id.incubus_core.be.IncubusBaseBE;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class PFTickingBE extends IncubusBaseBE {

    public PFTickingBE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static <B extends BlockEntity> void tick(World world, BlockPos pos, BlockState state, B be) {
        ((PFTickingBE) be).tick(pos, state);
    }

    protected abstract void tick(BlockPos pos, BlockState state);
}
