package works.azzyys.pulseflux.registry;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import works.azzyys.pulseflux.PulseFlux;
import net.minecraft.registry.Registry;
import works.azzyys.pulseflux.automata.AutomataShell;
import works.azzyys.pulseflux.network.TransferNetwork;

public class PulseFluxRegistries {
    public static void init() {}

    public static final Registry<TransferNetwork.NetworkReconstructor> NETWORK_RECONSTRUCTOR = FabricRegistryBuilder.createSimple(TransferNetwork.NetworkReconstructor.class, PulseFlux.locate("network_reconstructor")).buildAndRegister();
    public static final Registry<AutomataShell<?>> AUTOMATA_SHELLS = FabricRegistryBuilder.from(new SimpleRegistry<AutomataShell<?>>(RegistryKey.ofRegistry(PulseFlux.locate("automata_shell")), Lifecycle.stable(), true)).buildAndRegister();

}
