package works.azzyys.pulseflux.packets;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.registry.RegistryKeys;
import works.azzyys.pulseflux.render.client.RenderStage;
import works.azzyys.pulseflux.render.client.effecs.UnboundEffect;
import works.azzyys.pulseflux.render.client.effecs.UnboundEffectManager;
import works.azzyys.pulseflux.render.client.effecs.display.FluidDisplayEffect;
import works.azzyys.pulseflux.render.server.PulseFluxEffectIdentifiers;
import works.azzyys.pulseflux.util.Shorthands;

public class PulseFluxClientPackets {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(PulseFluxPackets.CREATE_NETWORK_IUI, ((client, handler, buf, responseSender) -> {
            var network = buf.readUuid();

            var pos = Shorthands.vecFromPacket(buf);
            var world = buf.readRegistryKey(RegistryKeys.WORLD);
            var id = buf.readUuid();

            client.execute(() -> {
                UnboundEffectManager.track(new FluidDisplayEffect(network, pos, id), world, RenderStage.POST_ENTITIES);
            });
        }));

        ClientPlayNetworking.registerGlobalReceiver(PulseFluxPackets.DISMISS_ALL_IUI, ((client, handler, buf, responseSender) -> {
            client.execute(() -> {
                UnboundEffectManager.getByCategory(PulseFluxEffectIdentifiers.IUI_DISPLAYS)
                        .stream()
                        .map(UnboundEffect::getId)
                        .forEach(UnboundEffectManager::remove);
            });
        }));
    }
}
