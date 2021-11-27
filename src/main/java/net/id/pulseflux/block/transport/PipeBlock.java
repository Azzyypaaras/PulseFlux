package net.id.pulseflux.block.transport;

import com.google.common.collect.ImmutableMap;
import net.id.pulseflux.blockentity.transport.FluidPipeEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class PipeBlock extends Block implements BlockEntityProvider {
    public static final Map<Direction, BooleanProperty> CONNECTIONS;

    public PipeBlock(Settings settings) {
        super(settings);
        for (Direction direction : Direction.values()) {
            setDefaultState(getDefaultState().with(CONNECTIONS.get(direction), false));
        }
        var defaultState = getDefaultState();
        defaultState = defaultState.with(CONNECTIONS.get(Direction.NORTH), true).with(CONNECTIONS.get(Direction.SOUTH), true);
        setDefaultState(defaultState);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            Direction side = hit.getSide();
            world.setBlockState(pos, state.with(CONNECTIONS.get(side), player.isSneaking()));
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FluidPipeEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        CONNECTIONS.values().forEach(builder::add);
    }
    public BlockRenderType getRenderType(BlockState blockState_1) {
        return BlockRenderType.MODEL;
    }

    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 6.0, 0.0, 16.0, 12.0, 16.0);
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    static {
        //noinspection unchecked
        CONNECTIONS = (Map<Direction, BooleanProperty>) (Object) ImmutableMap.builder()
                .put(Direction.NORTH, BooleanProperty.of("north"))
                .put(Direction.EAST, BooleanProperty.of("east"))
                .put(Direction.SOUTH, BooleanProperty.of("south"))
                .put(Direction.WEST, BooleanProperty.of("west"))
                .put(Direction.UP, BooleanProperty.of("up"))
                .put(Direction.DOWN, BooleanProperty.of("down"))
                .build();
    }
}
