package net.id.pulseflux.registry;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.id.pulseflux.PulseFlux;
import net.id.pulseflux.network.TransferNetwork.NetworkReconstructor;
import net.minecraft.registry.Registry;

import static net.id.pulseflux.network.TransferNetwork.NetworkReconstructor;

public class PulseFluxRegistries {
    public static void init() {}

    public static final Registry<NetworkReconstructor<?>> NETWORK_RECONSTRUCTOR = (Registry<NetworkReconstructor<?>>) (Object) FabricRegistryBuilder.createSimple(NetworkReconstructor.class, PulseFlux.locate("network_reconstructor")).buildAndRegister();
}
