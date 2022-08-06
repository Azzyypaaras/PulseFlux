package net.id.pulseflux.block.base;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class PFRedstoneBlock extends PFBlock {

    public static final BooleanProperty POWERED = Properties.POWERED;

    public PFRedstoneBlock(Settings settings, boolean loggable) {
        super(settings, loggable);
        setDefaultState(getDefaultState().with(POWERED, false));
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        boolean powered = world.isReceivingRedstonePower(pos);
        if (powered != state.get(POWERED)) {
            world.setBlockState(pos, state.with(POWERED, powered), Block.NOTIFY_ALL);
            pulseUpdate(state, world, pos, powered);
        }
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
    }

    protected void pulseUpdate(BlockState state, World world, BlockPos pos, boolean on) {}

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(POWERED);
    }
}
