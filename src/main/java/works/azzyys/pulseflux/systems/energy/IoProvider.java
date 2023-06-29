package works.azzyys.pulseflux.systems.energy;

import net.id.incubus_core.systems.Simulation;
import works.azzyys.pulseflux.util.ColorHelper;
import works.azzyys.pulseflux.util.RelativeObjectData;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

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


            //if(pulseIo instanceof BlockEntityClientSerializable syncable) {
            //    syncable.sync();
            //}
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
        PULSE(ColorHelper.hexToNormalizedRGB(0x78ffcb), ColorHelper.hexToNormalizedRGB(0xff7e3d), ColorHelper.hexToNormalizedRGB(0xfff478), 1F);

        public final Vector3f inputColor, outputColor, mixedColor;
        public final float scale;

        Type(Vector3f inputColor, Vector3f outputColor, Vector3f mixedColor, float scale) {
            this.inputColor = inputColor;
            this.outputColor = outputColor;
            this.mixedColor = mixedColor;
            this.scale = scale;
        }
    }
}
