package net.id.pulseflux.block.transport;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class PipeBlock extends LogisticComponentBlock {

    public static final EnumProperty<Direction.Axis> LINEAR_AXIS = EnumProperty.of("linear_axis", Direction.Axis.class);
    public static final Map<Direction, BooleanProperty> CONNECTIONS;
    public static final BooleanProperty STRAIGHT = BooleanProperty.of("straight");

    public final BlockApiLookup<? extends Storage<?>, Direction> lookup;

    public PipeBlock(Settings settings, BlockApiLookup<? extends Storage<?>, Direction> lookup) {
        super(settings, true);
        this.lookup = lookup;
        for (Direction direction : Direction.values()) {
            setDefaultState(getDefaultState().with(CONNECTIONS.get(direction), false));
        }
        var defaultState = getDefaultState();
        defaultState = defaultState.with(CONNECTIONS.get(Direction.NORTH), true).with(CONNECTIONS.get(Direction.SOUTH), true);
        defaultState = defaultState.with(LINEAR_AXIS, Direction.Axis.Z);
        setDefaultState(defaultState);
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        var world = ctx.getWorld();
        var clickDirection = ctx.getPlacementDirections()[0];
        var pos = ctx.getBlockPos();

        var offPos = pos.offset(clickDirection);
        var offBlock = world.getBlockState(offPos);
        var lookupResult = lookup.find(world, offPos, clickDirection.getOpposite());

        if(offBlock.getBlock() instanceof PipeBlock pipe && pipe.lookup == lookup) {

        }
        else if(lookupResult instanceof Storage<?> storage) {
            if(storage.supportsInsertion() || storage.supportsExtraction()) {

            }
        }
        return super.getPlacementState(ctx);
    }

    public boolean notifyConnectionAttempt(World world, BlockState state, BlockPos pos, Direction direction) {
        if(state.get(STRAIGHT)) {

            var linearAxis = state.get(LINEAR_AXIS);

            if(direction.getAxis() != linearAxis) {
                state = state.with(STRAIGHT, false);

                for (Direction dir : Direction.values()) {
                    if(dir.getAxis() == linearAxis) {
                        var offPos = pos.offset(dir);
                        var offState = world.getBlockState(offPos);
                        if(!canConnectTo(world, offState, pos, dir)) {
                            state = state.with(CONNECTIONS.get(dir), false);
                        }
                    }
                }

                state = state.with(CONNECTIONS.get(direction), true);
                world.setBlockState(pos, state, Block.NOTIFY_ALL);

                return true;
            }
        }

    }

    public boolean canConnectTo(World world, BlockState state, BlockPos pos, Direction direction) {
        if(state.getBlock() instanceof  PipeBlock pipe)
            return pipe.lookup == lookup;

        var lookupResult = lookup.find(world, pos, direction.getOpposite());
        return lookupResult != null && (lookupResult.supportsExtraction() || lookupResult.supportsInsertion());
    }

    @Override
    public boolean isConnectedToComponent(World world, BlockPos pos, Direction direction) {
        return super.isConnectedToComponent(world, pos, direction);
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
