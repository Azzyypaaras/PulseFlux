package net.id.pulseflux;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.id.pulseflux.arrp.PulseFluxRecipes;
import net.id.pulseflux.arrp.PulseFluxResources;
import net.id.pulseflux.block.PulseFluxBlockEntities;
import net.id.pulseflux.block.PulseFluxBlocks;
import net.id.pulseflux.render.client.PulseFluxRenderers;
import net.id.pulseflux.render.client.RenderStage;
import net.id.pulseflux.render.client.UnboundEffectManager;
import net.id.pulseflux.item.PulseFluxItemGroups;
import net.id.pulseflux.item.PulseFluxItems;
import net.id.pulseflux.network.Reconstructors;
import net.id.pulseflux.registry.PulseFluxRegistries;
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

		WorldRenderEvents.BEFORE_ENTITIES.register(context -> UnboundEffectManager.render(context, null, null, RenderStage.PRE_ENTITIES));
		WorldRenderEvents.AFTER_ENTITIES.register(context -> UnboundEffectManager.render(context, null, null, RenderStage.POST_ENTITIES));
		WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((context, hit) -> UnboundEffectManager.render(context, hit, null, RenderStage.PRE_OUTLINES));
		WorldRenderEvents.BLOCK_OUTLINE.register((context, outline) -> UnboundEffectManager.render(context, null, outline, RenderStage.OUTLINES));
		WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> UnboundEffectManager.render(context, null, null, RenderStage.TRANSLUCENT));
		WorldRenderEvents.LAST.register(context -> UnboundEffectManager.render(context, null, null, RenderStage.LAST));

		ClientTickEvents.END_CLIENT_TICK.register(UnboundEffectManager::update);
	}
}
