package net.id.pulseflux.network;

import net.id.pulseflux.network.TransferNetwork.NetworkReconstructor;
import net.id.pulseflux.registry.PulseFluxRegistries;
import net.id.pulseflux.registry.PulseFluxRegistryQueues;
import net.minecraft.util.Identifier;

import static net.id.pulseflux.PulseFlux.locate;

public class Reconstructors {

    public static final NetworkReconstructor<FluidNetwork> FLUID_RECONSTRUCTOR = add("fluid_network_reconstructor", ((world, id, nbt) -> new FluidNetwork(world, nbt)));

    public static void register() {
        PulseFluxRegistryQueues.NETWORK_RECONSTRUCTOR.register();
    }

    public static Identifier getId(NetworkReconstructor<?> reconstructor) {
        return PulseFluxRegistries.NETWORK_RECONSTRUCTOR.getId(reconstructor);
    }

    public static NetworkReconstructor<?> getReconstructor(Identifier id) {
        return PulseFluxRegistries.NETWORK_RECONSTRUCTOR.get(id);
    }

    private static <N extends TransferNetwork<N>> NetworkReconstructor<N> add(String id, NetworkReconstructor<N> reconstructor) {
        return PulseFluxRegistryQueues.NETWORK_RECONSTRUCTOR.add(locate(id), reconstructor);
    }
}
