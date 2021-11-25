package net.id.pulseflux;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import net.id.pulseflux.network.NetworkManager;

public class PulseFluxComponents implements WorldComponentInitializer {

    public static final ComponentKey<NetworkManager> NETWORK_MANAGER_KEY = ComponentRegistry.getOrCreate(PulseFlux.locate("network_manager"), NetworkManager.class);

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        registry.register(NETWORK_MANAGER_KEY, NetworkManager::new);
    }
}
