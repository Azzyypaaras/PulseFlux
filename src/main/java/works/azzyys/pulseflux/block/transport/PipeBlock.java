package works.azzyys.pulseflux.block.transport;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import works.azzyys.pulseflux.item.PulseFluxItems;
import works.azzyys.pulseflux.network.TransferNetwork;
import works.azzyys.pulseflux.util.BlockReference;
import works.azzyys.pulseflux.util.Shorthands;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class PipeBlock<T extends TransferNetwork<T>> extends LogisticComponentBlock<T> {

    public static final EnumProperty<Direction.Axis> LINEAR_AXIS = Properties.AXIS;
    public static final BooleanProperty STRAIGHT = BooleanProperty.of("straight");
    public static final Map<Direction, BooleanProperty> CONNECTIONS;
    public static final Map<Direction, VoxelShape> SHAPES;
    public static final List<Intercept> INTERCEPTS;
    public static final VoxelShape HEART = Block.createCuboidShape(5, 5, 5, 11, 11 ,11);

    public final BlockApiLookup<? extends Storage<?>, Direction> lookup;

    public PipeBlock(Settings settings, BlockApiLookup<? extends Storage<?>, Direction> lookup) {
        super(settings, true);
        this.lookup = lookup;
        for (Direction direction : Direction.values()) {
            setDefaultState(getDefaultState().with(CONNECTIONS.get(direction), false));
        }
        var defaultState = getDefaultState();
        //defaultState = defaultState.with(CONNECTIONS.get(Direction.NORTH), true).with(CONNECTIONS.get(Direction.SOUTH), true);
        defaultState = defaultState.with(LINEAR_AXIS, Direction.Axis.Z);
        setDefaultState(defaultState);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        var shape = HEART;

        for (Direction direction : Direction.values()) {
            if(state.get(CONNECTIONS.get(direction)))
                shape = VoxelShapes.union(shape, SHAPES.get(direction));
        }

        return shape;
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        var world = ctx.getWorld();
        var reference = BlockReference.of(super.getPlacementState(ctx), ctx.getBlockPos());

        if (reference.getState() == null || world == null)
            return null;

        var side = ctx.getSide();
        var alt = ctx.getPlayer().isSneaking();

        if (alt) {
            var serverPeek = new ArrayList<Direction>();

            for (Direction direction : DIRECTIONS) {
                var offPos = reference.pos.offset(direction);

                if (world.getBlockState(offPos).isOf(this)) {
                    reference.setProperty(CONNECTIONS.get(direction), true);
                }
                else if (!world.isAir(offPos)) {
                    serverPeek.add(direction);
                }
                else {
                    reference.setProperty(CONNECTIONS.get(direction), false);
                }
            }

            if(!world.isClient() && serverPeek.size() > 0) {
                for (Direction peekDir : serverPeek) {
                    var peek = lookup.find(world, reference.pos.offset(peekDir), peekDir);

                    if (peek != null)
                        reference.setProperty(CONNECTIONS.get(peekDir), true);
                }
            }

            updateLinear(reference);

            return reference.getState();
        }

        alignTo(reference, side, true);

        return reference.getState();
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        var tool = player.getStackInHand(hand);
        var reference = BlockReference.of(state, pos);

        if (!tool.isOf(PulseFluxItems.MANUAL_WRENCH))
            return ActionResult.PASS;

        var hitpos = hit.getPos().relativize(Vec3d.of(pos)).multiply(-1);

        Direction direction = null;

        for (Intercept intercept : INTERCEPTS) {
            if (intercept.box.contains(hitpos)) {
                direction = intercept.direction;
                break;
            }
        }

        if (direction == null)
            return ActionResult.PASS;


        var connection = CONNECTIONS.get(direction);
        var connected = reference.getProperty(connection);

        reference.setProperty(connection, !connected);
        updateLinear(reference);
        reference.update(world);

        postProcessWrenchHit(world, direction, reference, connected);

        return ActionResult.success(world.isClient());
    }

    abstract void postProcessWrenchHit(World world, Direction direction, BlockReference reference, boolean disconnecting);

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        for (Direction dir : Direction.values()) {
            if (state.get(CONNECTIONS.get(dir))) {
                var offPos = pos.offset(dir);
                var offBlock = world.getBlockState(offPos);
                if (offBlock.getBlock() instanceof PipeBlock pipe && pipe.lookup == lookup)
                    notifyConnectionAttempt(world, offBlock, offPos, dir.getOpposite());
            }
        }
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    public static void alignTo(BlockReference reference, Direction direction, boolean force) {
        for (Direction dir : Direction.values()) {
            if(force) {
                reference.setProperty(CONNECTIONS.get(dir), dir.getAxis() == direction.getAxis());
            }
            else if(dir.getAxis() == direction.getAxis()) {
                reference.setProperty(CONNECTIONS.get(dir), dir.getAxis() == direction.getAxis());
            }
        }
        reference.setProperty(LINEAR_AXIS, direction.getAxis());
    }

    public static void updateLinear(BlockReference reference) {
        var openDirections = new ArrayList<Direction>();

        for (Direction direction : Direction.values()) {
            if(reference.getProperty(CONNECTIONS.get(direction))) {
                openDirections.add(direction);
            }
        }

        if (openDirections.size() == 2 && openDirections.get(0).getAxis() == openDirections.get(1).getAxis()) {
            reference.setProperty(STRAIGHT, true);
            reference.setProperty(LINEAR_AXIS, openDirections.get(0).getAxis());
            return;
        }
        reference.setProperty(STRAIGHT, false);
    }

    public void notifyConnectionAttempt(World world, BlockState state, BlockPos pos, Direction facing) {
        if (state.get(STRAIGHT)) {
            var linearAxis = state.get(LINEAR_AXIS);

            if (facing.getAxis() != linearAxis) {
                state = state.with(STRAIGHT, false);

                for (Direction dir : Direction.values()) {
                    var offPos = pos.offset(dir);
                    var offState = world.getBlockState(offPos);
                    if(!canConnectTo(world, offState, offPos, dir)) {
                        state = state.with(CONNECTIONS.get(dir), false);
                    }
                }

                state = state.with(CONNECTIONS.get(facing), true);
                world.setBlockState(pos, state, Block.NOTIFY_ALL);
            }
        }
        else {

            var changedDirs = new ArrayList<Direction>();

            for (Direction dir : Direction.values()) {
                var offPos = pos.offset(dir);
                var offState = world.getBlockState(offPos);
                if(!canConnectTo(world, offState, offPos, dir)) {
                    state = state.with(CONNECTIONS.get(dir), false);
                }
                else if(dir != facing) {
                    changedDirs.add(dir);
                }
            }

            if(changedDirs.size() == 1 && changedDirs.get(0).getAxis() == facing.getAxis()) {
                state = state.with(STRAIGHT, true).with(LINEAR_AXIS, facing.getAxis());
                state = state.with(CONNECTIONS.get(changedDirs.get(0)), true);
                state = state.with(CONNECTIONS.get(changedDirs.get(0).getOpposite()), true);
            }

            state = state.with(CONNECTIONS.get(facing), true);
            world.setBlockState(pos, state, Block.NOTIFY_ALL);
        }
    }

    public boolean isDirectionOpen(BlockState state, Direction direction) {
        return state.get(CONNECTIONS.get(direction));
    }

    public boolean canConnectTo(World world, BlockState state, BlockPos pos, Direction direction) {
        if(state.getBlock() instanceof  PipeBlock pipe)
            return pipe.lookup == lookup && pipe.isDirectionOpen(state, direction.getOpposite());

        var lookupResult = lookup.find(world, pos, direction.getOpposite());
        return lookupResult != null && (lookupResult.supportsExtraction() || lookupResult.supportsInsertion());
    }

    @Override
    public boolean isConnectedToComponent(World world, BlockPos pos, Direction direction) {
        return isDirectionOpen(world.getBlockState(pos), direction);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(LINEAR_AXIS, STRAIGHT);
        for (Direction dir : Direction.values()) {
            builder.add(CONNECTIONS.get(dir));
        }
    }

    private record Intercept(Direction direction, Box box) {

        public BooleanProperty connection() {
            return CONNECTIONS.get(direction);
        }

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
        //noinspection unchecked
        SHAPES = (Map<Direction, VoxelShape>) (Object) ImmutableMap.builder()
                .put(Direction.WEST, Block.createCuboidShape(0, 5, 5, 5, 11 ,11))
                .put(Direction.EAST, Block.createCuboidShape(11, 5, 5, 16, 11 ,11))
                .put(Direction.NORTH, Block.createCuboidShape(5, 5, 0, 11, 11 ,5))
                .put(Direction.SOUTH, Block.createCuboidShape(5, 5, 11, 11, 11 ,16))
                .put(Direction.DOWN, Block.createCuboidShape(5, 0, 5, 11, 5 ,11))
                .put(Direction.UP, Block.createCuboidShape(5, 11, 5, 11, 16 ,11))
                .build();
        //noinspection unchecked
        INTERCEPTS = (List<Intercept>) (Object) ImmutableList.builder()
                .add(new Intercept(Direction.UP, Shorthands.pixelBox(4, 11, 4, 12, 16.25, 12)))
                .add(new Intercept(Direction.DOWN, Shorthands.pixelBox(4, 0, 4, 12, 5.25, 12)))
                .add(new Intercept(Direction.NORTH, Shorthands.pixelBox(4, 4, 0, 12, 12, 5.25)))
                .add(new Intercept(Direction.SOUTH, Shorthands.pixelBox(4, 4, 11, 12, 12, 16.25)))
                .add(new Intercept(Direction.WEST, Shorthands.pixelBox(0, 4, 4, 5.25, 12, 12)))
                .add(new Intercept(Direction.EAST, Shorthands.pixelBox(11, 4, 4, 16.25, 12, 12)))
                .build();
    }
}
