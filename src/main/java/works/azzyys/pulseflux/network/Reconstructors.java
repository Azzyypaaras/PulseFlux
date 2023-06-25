package works.azzyys.pulseflux.network;

import works.azzyys.pulseflux.registry.PulseFluxRegistries;
import works.azzyys.pulseflux.registry.PulseFluxRegistryQueues;
import net.minecraft.util.Identifier;
import works.azzyys.pulseflux.PulseFlux;

public class Reconstructors {

    public static final TransferNetwork.NetworkReconstructor<FluidNetwork> FLUID_RECONSTRUCTOR = add("fluid_network_reconstructor", ((world, id, nbt) -> new FluidNetwork(world, nbt)));

    public static void register() {
        PulseFluxRegistryQueues.NETWORK_RECONSTRUCTOR.register();
    }

    public static Identifier getId(TransferNetwork.NetworkReconstructor<?> reconstructor) {
        return PulseFluxRegistries.NETWORK_RECONSTRUCTOR.getId(reconstructor);
    }

    public static TransferNetwork.NetworkReconstructor<?> getReconstructor(Identifier id) {
        return PulseFluxRegistries.NETWORK_RECONSTRUCTOR.get(id);
    }

    private static <N extends TransferNetwork<N>> TransferNetwork.NetworkReconstructor<N> add(String id, TransferNetwork.NetworkReconstructor<N> reconstructor) {
        return PulseFluxRegistryQueues.NETWORK_RECONSTRUCTOR.add(PulseFlux.locate(id), reconstructor);
    }
}
