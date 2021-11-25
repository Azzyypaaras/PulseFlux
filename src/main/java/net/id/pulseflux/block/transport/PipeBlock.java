package net.id.pulseflux.block.transport;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public abstract class PipeBlock extends LogisticComponentBlock {

    public PipeBlock(Settings settings) {
        super(settings, true);
    }

    @Override
    public boolean isConnectedToComponent(World world, BlockPos pos, Direction direction) {
        return super.isConnectedToComponent(world, pos, direction);
    }
}
