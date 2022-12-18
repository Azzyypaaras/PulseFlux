package net.id.pulseflux.client.render;

import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.id.pulseflux.block.PulseFluxBlockEntities;
import net.id.pulseflux.block.fluid_storage.BasinBlockEntityRenderer;

import static net.id.pulseflux.block.PulseFluxBlockEntities.*;
import static net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry.*;

public class PulseFluxRenderers {

    public static void init() {
        BlockEntityRendererRegistry.register(STONE_BASIN_TYPE, BasinBlockEntityRenderer::new);
    }

}
