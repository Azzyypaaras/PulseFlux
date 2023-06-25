package works.azzyys.pulseflux.render.client;

import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import works.azzyys.pulseflux.block.fluid_storage.BasinBlockEntityRenderer;
import works.azzyys.pulseflux.render.client.effecs.TestUnboundEffect;
import works.azzyys.pulseflux.render.client.effecs.UnboundEffectManager;
import net.minecraft.world.World;

import static works.azzyys.pulseflux.block.PulseFluxBlockEntities.STONE_BASIN_TYPE;

public class PulseFluxRenderers {

    public static void init() {
        BlockEntityRendererRegistry.register(STONE_BASIN_TYPE, BasinBlockEntityRenderer::new);
    }

    public static void initUnboundEffects() {
        UnboundEffectManager.track(new TestUnboundEffect(), World.OVERWORLD, RenderStage.POST_ENTITIES);
    }

}
