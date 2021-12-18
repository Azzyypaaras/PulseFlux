package net.id.pulseflux.client.render;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.id.pulseflux.block.PulseFluxBlocks;
import net.minecraft.client.render.RenderLayer;

import static net.id.pulseflux.blockentity.PulseFluxBlockEntities.*;
import static net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry.*;

public class PulseFluxRenderers {

    public static void init() {
        register(WORKSHOP_DIODE_ENTITY_TYPE, PulseRenderer::new);
        register(CREATIVE_PULSE_SOURCE_ENTITY_BLOCK_ENTITY_TYPE, PulseRenderer::new);
        BlockRenderLayerMap.INSTANCE.putBlock(PulseFluxBlocks.WOODEN_FLUID_PIPE, RenderLayer.getCutout());
    }

}
