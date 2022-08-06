package net.id.pulseflux.block.storage;

import net.id.pulseflux.block.base.PFBlockWithEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class ReservoirBlock extends PFBlockWithEntity {

    public ReservoirBlock(Settings settings) {
        super(settings, false);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }
}
