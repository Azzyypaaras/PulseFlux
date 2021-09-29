package net.id.pulseflux.block.pulse;

import net.id.pulseflux.block.PFBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class BaseDiodeBlock extends PFBlock {

    public BaseDiodeBlock(Settings settings) {
        super(settings, false);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }
}
