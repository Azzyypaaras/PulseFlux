package net.id.pulseflux.block.fluid_storage;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class BasinBlockEntityRenderer implements BlockEntityRenderer<BasinBlockEntity> {

    @Override
    public void render(BasinBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var tankFluid = entity.tank.variant;

        if (!tankFluid.isBlank()) {
            var tankSprites = FluidVariantRendering.getSprites(tankFluid);

            if (tankSprites == null)
                return;
        }
    }
}
