package net.id.pulseflux.blockentity;

import com.google.common.collect.ImmutableList;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.id.incubus_core.systems.Material;
import net.id.incubus_core.systems.Polarity;
import net.id.incubus_core.systems.PulseIo;
import net.id.incubus_core.systems.Simulation;
import net.id.pulseflux.block.property.DirectionalIoProperty;
import net.id.pulseflux.block.pulse.BaseDiodeBlock;
import net.id.pulseflux.logistics.IoProvider;
import net.id.pulseflux.util.LogisticsHelper;
import net.id.pulseflux.util.RelativeObjectData;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static net.id.pulseflux.block.pulse.BaseDiodeBlock.*;

public class BaseDiodeEntity extends PulseBlockEntity implements IoProvider {

    @NotNull private Optional<RelativeObjectData<PulseIo>> child = Optional.empty();
    private int renderTicks = -25;


    public BaseDiodeEntity(Material material, BlockPos pos, BlockState state) {
        super(PulseFluxBlockEntities.WORKSHOP_DIODE_ENTITY_TYPE, material, pos, state, 80);
    }

    @Override
    boolean initialize(World world, BlockPos pos, BlockState state) {
        child = getIoDir(state, IoType.OUTPUT).flatMap(dir -> LogisticsHelper.seekPulseIo(IoType.INPUT, world, pos, dir));
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
        }
    }

    @Override
    public @NotNull IoType getIoCapabilities(Direction direction) {
        return getCachedState().get(DirectionalIoProperty.IO_PROPERTIES.get(direction));
    }

    public static FabricBlockEntityTypeBuilder.Factory<BaseDiodeEntity> factory(Material material) {
        return ((blockPos, blockState) -> new BaseDiodeEntity(material, blockPos, blockState));
    }

    @Override
    public @NotNull List<Direction> getInputs(Type type) {
        return BaseDiodeBlock.getIoDir(getCachedState(), IoType.INPUT)
                .map(ImmutableList::of)
                .orElse(ImmutableList.of());
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
    public @NotNull Category getDeviceCategory() {
        return Category.CONNECTOR;
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
