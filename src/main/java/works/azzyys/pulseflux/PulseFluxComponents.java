package works.azzyys.pulseflux;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import works.azzyys.pulseflux.automata.AutomataManager;
import works.azzyys.pulseflux.network.NetworkManager;

public class PulseFluxComponents implements WorldComponentInitializer {

    public static final ComponentKey<NetworkManager> NETWORK_MANAGER_KEY = ComponentRegistry.getOrCreate(PulseFlux.locate("network_manager"), NetworkManager.class);
    public static final ComponentKey<AutomataManager> AUTOMATA_MANAGER_KEY = ComponentRegistry.getOrCreate(PulseFlux.locate("automata_manager"), AutomataManager.class);

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        registry.register(NETWORK_MANAGER_KEY, NetworkManager::new);
        registry.register(AUTOMATA_MANAGER_KEY, AutomataManager::new);
    }
}
