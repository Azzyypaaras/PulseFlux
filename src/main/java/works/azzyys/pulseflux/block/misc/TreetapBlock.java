package works.azzyys.pulseflux.block.misc;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import works.azzyys.pulseflux.block.base.PFBlockWithEntity;
import works.azzyys.pulseflux.block.transport.FluidPipeBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class TreetapBlock extends PFBlockWithEntity {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty SYPHON = BooleanProperty.of("syphon");
    public static final Map<Direction, VoxelShape> SHAPES;

    public TreetapBlock(Settings settings) {
        super(settings, false);
        setDefaultState(getDefaultState().with(SYPHON, false));
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        var state = super.getPlacementState(ctx);
        state = state.with(FACING, ctx.getPlayerFacing().getOpposite());
        return getSyphonState(state, ctx.getBlockPos(), ctx.getWorld());
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return getSyphonState(state, pos, (World) world);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        world.setBlockState(pos, getSyphonState(state, pos, world), Block.NOTIFY_ALL);
    }

    public BlockState getSyphonState(BlockState state, BlockPos pos, World world) {
        boolean connect = false;
        var floorPos = pos.down();
        var floor = world.getBlockState(floorPos);
        var lookup = !world.isClient() ? FluidStorage.SIDED.find(world, pos, Direction.UP) : null;

        if(floor.getBlock() instanceof FluidPipeBlock pipe) {
            connect = pipe.isDirectionOpen(floor, Direction.UP);
        }
        else if(lookup != null){
            connect = lookup.supportsInsertion();
        }

        return state.with(SYPHON, connect);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES.get(state.get(FACING));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TreetapBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING, SYPHON);
    }

    static {
        //noinspection unchecked
        SHAPES = (Map<Direction, VoxelShape>) (Object) ImmutableMap.builder()
                .put(Direction.WEST, Block.createCuboidShape(8, 0, 4, 16, 6,12))
                .put(Direction.EAST, Block.createCuboidShape(0, 0, 4, 8, 6,12))
                .put(Direction.NORTH, Block.createCuboidShape(4, 0, 8, 12, 6,16))
                .put(Direction.SOUTH, Block.createCuboidShape(4, 0, 0, 12, 6,8))
                .build();
    }
}
