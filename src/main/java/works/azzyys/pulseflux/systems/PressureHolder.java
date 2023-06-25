package works.azzyys.pulseflux.systems;

public interface PressureHolder {


    /**
     * Gauge, not absolute
     */
    long queryPressure();
}
