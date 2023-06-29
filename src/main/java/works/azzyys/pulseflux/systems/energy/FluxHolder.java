package works.azzyys.pulseflux.systems.energy;

import net.minecraft.util.math.MathHelper;

/**
 * An object capable of storing, providing, and otherwise interacting with Flux Motive (Fm) and Dissonance (mms).
 */
public interface FluxHolder extends EnergyHolder{

    float queryDissonance();

    default float normalize(int dissonance) {
        return MathHelper.clamp(dissonance / 1000F, 0F, 1F);
    }

    default int toMilliShift(float dissonance) {
        return (int) (dissonance * 1000);
    }
}