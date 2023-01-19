package net.id.pulseflux.systems;

import net.id.incubus_core.systems.Simulation;

/**
 * An object that bears some form of energy.
 */
public interface EnergyHolder {

    /**
     * Query the stored energy of the holder.
     */
    long queryEnergy();

    /**
     * @param maxDraw The amount to try and be extracted
     * @return How much power could be drawn
     */
    long drawEnergy(long maxDraw, Simulation simulation);

    /**
     * @param maxInsert The amount to try and be inserted
     * @return How much power was unable to be inserted
     */
    long insertEnergy(long maxInsert, Simulation simulation);


    /**
     * Whether this object can receive energy.
     * False if and only if this object is entirely unable to accept power, and will always return the amount attempted to be inserted.
     */
    boolean allowInsertion();

    /**
     * Whether this object can provide energy.
     * False if and only if this object is entirely unable to provide power, and will always return zero on draw.
     */
    boolean allowExtraction();
}
