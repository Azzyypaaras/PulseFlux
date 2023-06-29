package works.azzyys.pulseflux.systems.energy;

public interface PressureHolder {


    /**
     * Gauge, not absolute
     */
    long queryPressure();
}
