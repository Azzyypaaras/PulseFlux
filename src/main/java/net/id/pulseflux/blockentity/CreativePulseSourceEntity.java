package net.id.pulseflux.blockentity;

import com.google.common.collect.ImmutableList;
import net.id.incubus_core.systems.DefaultMaterials;
import net.id.pulseflux.block.property.DirectionalIoProperty;
import net.id.pulseflux.block.pulse.BaseDiodeBlock;
import net.id.pulseflux.systems.IoProvider;
import net.id.pulseflux.systems.Polarity;
import net.id.pulseflux.systems.PulseIo;
import net.id.pulseflux.util.LogisticsHelper;
import net.id.pulseflux.util.RelativeObjectData;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static net.id.pulseflux.block.pulse.BaseDiodeBlock.getIoDir;

public class CreativePulseSourceEntity extends PulseBlockEntity implements IoProvider {

    @NotNull private Optional<RelativeObjectData<PulseIo>> child = Optional.empty();
    private int renderTicks = -25;

    public CreativePulseSourceEntity(BlockPos pos, BlockState state) {
        super(PulseFluxBlockEntities.CREATIVE_PULSE_SOURCE_ENTITY_BLOCK_ENTITY_TYPE, DefaultMaterials.DIAMOND, pos, state, 20);
    }

    @Override
    boolean initialize(World world, BlockPos pos, BlockState state) {
        child = getIoDir(state, IoType.OUTPUT).flatMap(dir -> LogisticsHelper.seekPulseIo(IoType.INPUT, world, pos, dir));
        frequency = 100;
        inductance = 100;
        polarity = Polarity.NEUTRAL;
        return true;
    }

    @Override
    void tick(BlockPos pos, BlockState state) {
        if(renderTicks < getMaxRenderProgress())
            renderTicks++;

        if(allowTick()) {
            if(child.isEmpty() || !child.get().isValid()) {
                child = getIoDir(state, IoType.OUTPUT).flatMap(dir -> LogisticsHelper.seekPulseIo(IoType.INPUT, world, pos, dir));
            }
            else {
                if(!world.isClient()) {
                    requestUpdate(child.get().object(), inductance, frequency, dissonance, polarity);
                }
            }
        }
    }

    @Override
    public boolean canUpdateChildren() {
        return true;
    }

    @Override
    public void markRemoved() {
        super.markRemoved();
    }


    @Override
    public @NotNull IoType getIoCapabilities(Direction direction) {
        return getCachedState().get(DirectionalIoProperty.IO_PROPERTIES.get(direction));
    }

    @Override
    public @NotNull List<Direction> getInputs(Type type) {
        return Collections.emptyList();
    }

    @Override
    public @NotNull List<Direction> getOutputs(Type type) {
        return BaseDiodeBlock.getIoDir(getCachedState(), IoType.OUTPUT)
                .map(ImmutableList::of)
                .orElse(ImmutableList.of());
    }

    @Override
    public @NotNull List<RelativeObjectData<PulseIo>> getChildren() {
        return child.map(List::of).orElse(Collections.emptyList());
    }

    @Override
    public int getRenderProgress() {
        return Math.abs(renderTicks);
    }

    @Override
    public long getFailureDissonance() {
        return Long.MAX_VALUE;
    }

    @Override
    public @NotNull Category getDeviceCategory() {
        return Category.PRODUCER;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        renderTicks = nbt.getInt("renderProgress");
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt("renderProgress", renderTicks);
        return super.writeNbt(nbt);
    }

    @Override
    public void fromClientTag(NbtCompound nbt) {
        super.fromClientTag(nbt);
        renderTicks = nbt.getInt("renderProgress");
    }

    @Override
    public NbtCompound toClientTag(NbtCompound nbt) {
        super.toClientTag(nbt);
        nbt.putInt("renderProgress", renderTicks);
        return nbt;
    }
}
