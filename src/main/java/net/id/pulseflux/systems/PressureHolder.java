package net.id.pulseflux.systems;

public interface PressureHolder {


    /**
     * Gauge, not absolute
     */
    long queryPressure();
}
