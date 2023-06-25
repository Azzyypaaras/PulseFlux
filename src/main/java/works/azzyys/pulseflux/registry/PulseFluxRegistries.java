package works.azzyys.pulseflux.registry;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import works.azzyys.pulseflux.PulseFlux;
import net.minecraft.registry.Registry;
import works.azzyys.pulseflux.network.TransferNetwork;

public class PulseFluxRegistries {
    public static void init() {}

    public static final Registry<TransferNetwork.NetworkReconstructor> NETWORK_RECONSTRUCTOR = FabricRegistryBuilder.createSimple(TransferNetwork.NetworkReconstructor.class, PulseFlux.locate("network_reconstructor")).buildAndRegister();

}
