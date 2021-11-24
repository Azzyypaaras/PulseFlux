package net.id.pulseflux.systems;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.id.pulseflux.systems.Polarity;
import net.id.pulseflux.systems.PulseIo;
import net.id.incubus_core.systems.Simulation;
import net.id.pulseflux.util.ColorHelper;
import net.id.pulseflux.util.RelativeObjectData;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public interface IoProvider {

    @NotNull List<Direction> getInputs(Type type);

    @NotNull default List<RelativeObjectData<PulseIo>> getChildren() {
        return Collections.emptyList();
    }

    @NotNull List<Direction> getOutputs(Type type);

    default boolean canUpdateChildren() {
        return false;
    }

    default void requestUpdate(PulseIo start, long inductance, long frequency, double dissonance, Polarity polarity) {
        Queue<PulseIo> updateQueue = new LinkedList<>();
        updateQueue.add(start);

        while (!updateQueue.isEmpty()) {
            var pulseIo = updateQueue.poll();
            if(pulseIo instanceof IoProvider provider) {

                if(!provider.canUpdateChildren()) {
                    var children = provider.getChildren();
                    children.stream().map(RelativeObjectData::object).forEach(updateQueue::add);
                }
            }
            pulseIo.transferFrequency(frequency, Simulation.ACT);
            pulseIo.transferInductance(inductance, Simulation.ACT);
            pulseIo.setPolarity(polarity);

            if(pulseIo instanceof BlockEntityClientSerializable syncable) {
                syncable.sync();
            }
        }
    }

    default @NotNull List<Direction> getMixed(Type type) {
        return getInputs(type)
                .stream()
                .filter(getOutputs(type)::contains)
                .toList();
    }

    int getRenderProgress();

    default int getMaxRenderProgress() {
        return 40;
    }

    enum Type {
        PULSE(ColorHelper.hexToVec(0x78ffcb), ColorHelper.hexToVec(0xff7e3d), ColorHelper.hexToVec(0xfff478), 1F);

        public final Vec3f inputColor, outputColor, mixedColor;
        public final float scale;

        Type(Vec3f inputColor, Vec3f outputColor, Vec3f mixedColor, float scale) {
            this.inputColor = inputColor;
            this.outputColor = outputColor;
            this.mixedColor = mixedColor;
            this.scale = scale;
        }
    }
}
