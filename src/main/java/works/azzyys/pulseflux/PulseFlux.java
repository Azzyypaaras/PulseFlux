package works.azzyys.pulseflux;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import works.azzyys.pulseflux.arrp.PulseFluxRecipes;
import works.azzyys.pulseflux.arrp.PulseFluxResources;
import works.azzyys.pulseflux.block.PulseFluxBlockEntities;
import works.azzyys.pulseflux.block.PulseFluxBlocks;
import works.azzyys.pulseflux.render.client.PulseFluxRenderers;
import works.azzyys.pulseflux.render.client.effecs.UnboundEffectManager;
import works.azzyys.pulseflux.item.PulseFluxItemGroups;
import works.azzyys.pulseflux.item.PulseFluxItems;
import works.azzyys.pulseflux.network.Reconstructors;
import works.azzyys.pulseflux.registry.PulseFluxRegistries;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.SplittableRandom;

public class PulseFlux implements ModInitializer, ClientModInitializer {

	public static final String MOD_ID = "pulseflux";
	public static final Logger LOG = LogManager.getLogger(MOD_ID);
	public static final SplittableRandom random = new SplittableRandom(System.currentTimeMillis());

	public static Identifier locate(String location) {
		return new Identifier(MOD_ID, location);
	}

	@Override
	public void onInitialize() {
		PulseFluxRegistries.init();
		PulseFluxBlocks.init();
		PulseFluxItems.init();
		PulseFluxItemGroups.build();
		PulseFluxBlockEntities.init();
		PulseFluxBlockEntities.postInit();
		PulseFluxRecipes.init();
		PulseFluxResources.init();
		Reconstructors.register();
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void onInitializeClient() {
		PulseFluxRenderers.init();

		UnboundEffectManager.init();

		PulseFluxRenderers.initUnboundEffects();

	}
}
