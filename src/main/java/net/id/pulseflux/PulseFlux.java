package net.id.pulseflux;

import net.fabricmc.api.ModInitializer;
import net.id.pulseflux.arrp.PulseFluxRecipes;
import net.id.pulseflux.block.PulseFluxBlocks;
import net.id.pulseflux.blockentity.PulseFluxBlockEntities;
import net.id.pulseflux.item.PulseFluxItems;
import net.id.pulseflux.arrp.PulseFluxResources;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PulseFlux implements ModInitializer {

	public static final String MOD_ID = "pulseflux";
	public static final Logger LOG = LogManager.getLogger(MOD_ID);

	public static Identifier locate(String location) {
		return new Identifier(MOD_ID, location);
	}

	@Override
	public void onInitialize() {
		PulseFluxBlocks.init();
		PulseFluxItems.init();
		PulseFluxBlockEntities.init();
		PulseFluxRecipes.init();
		PulseFluxResources.init();
	}
}
