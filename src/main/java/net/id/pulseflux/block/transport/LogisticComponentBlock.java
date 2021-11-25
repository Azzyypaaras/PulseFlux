package net.id.pulseflux.block.transport;

import net.id.pulseflux.block.PFBlock;
import net.id.pulseflux.block.PFBlockWithEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class LogisticComponentBlock extends PFBlockWithEntity {

    public LogisticComponentBlock(Settings settings, boolean loggable) {
        super(settings, loggable);
    }

    public boolean isConnectedToComponent(World world, BlockPos pos, Direction direction) {
        return false;
    }
}
