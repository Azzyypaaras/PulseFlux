package works.azzyys.pulseflux.systems;

/**
 * An object capable of storing and translating thermal energy.
 */
public interface ThermalBody extends EnergyHolder {

    /**
     * The ratio of energy required to heat up the body by a degree, per unit of weight
     */
    float specificHeat();

    /**
     * I am legally obligated to not refer to this as kilograms
     */
    float mass();

    /**
     * Janky hack mate
     * @return the temperature of the object, in degrees Celsius, not kelvin, NOT KELVIN.
     */
    default float getTemperatureCelsius() {
        return getTemperatureKelvin() + 273.15F;
    }

    /**
     * I would not override this
     */
    default float getTemperatureKelvin() {
        return queryEnergy() / mass() / specificHeat();
    }
}
