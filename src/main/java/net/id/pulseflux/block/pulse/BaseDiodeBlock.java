package net.id.pulseflux.block.pulse;

import net.id.incubus_core.systems.DefaultMaterials;
import net.id.pulseflux.block.base.PFBlockWithEntity;
import net.id.pulseflux.block.base.PFBlockEntity;
import net.id.pulseflux.util.LogisticsHelper;
import net.id.pulseflux.util.RelativeObjectData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static net.id.pulseflux.systems.PulseIo.IoType;
import static net.id.pulseflux.block.property.DirectionalIoProperty.*;

public class BaseDiodeBlock extends PFBlockWithEntity {

    public BaseDiodeBlock(Settings settings) {
        super(settings, true);
        IO_PROPERTIES.values().forEach(property -> setDefaultState(getDefaultState().with(property, IoType.NONE)));
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return PFBlockEntity::tick;
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        var state = super.getPlacementState(ctx);
        if(state != null) {
            var facingDir = ctx.getPlayerLookDirection().getOpposite();
            state = state.with(IO_PROPERTIES.get(facingDir), IoType.OUTPUT);

            var searchDirs = new ArrayList<>(Arrays.asList(Direction.values()));
            searchDirs.remove(facingDir.getOpposite());
            searchDirs.add(0, facingDir.getOpposite());

            var input = LogisticsHelper.seekPulseIo(IoType.OUTPUT, ctx.getWorld(), ctx.getBlockPos(), searchDirs)
                    .map(RelativeObjectData::direction)
                    .filter(direction -> direction != facingDir)
                    .orElse(facingDir.getOpposite());

            state = state.with(IO_PROPERTIES.get(input), IoType.INPUT);
        }
        return state;
    }

    public static Optional<Direction> getIoDir(BlockState state, IoType type) {
        for (Direction direction : Direction.values()) {
            if(state.get(IO_PROPERTIES.get(direction)) == type)
                return Optional.of(direction);
        }
        return Optional.empty();
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BaseDiodeBlockEntity(DefaultMaterials.IRON, pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        IO_PROPERTIES.values().forEach(builder::add);
    }
}
