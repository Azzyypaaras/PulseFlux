package net.id.pulseflux.client.render;

import static net.id.pulseflux.blockentity.PulseFluxBlockEntities.*;
import static net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry.*;

public class PulseFluxRenderers {

    public static void init() {
        register(WORKSHOP_DIODE_TYPE, PulseRenderer::new);
        register(CREATIVE_PULSE_SOURCE_TYPE, PulseRenderer::new);
    }

}
