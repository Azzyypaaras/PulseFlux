package net.id.pulseflux.block.fluid_storage;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.id.pulseflux.util.render.RenderShortcuts;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BasinBlockEntityRenderer implements BlockEntityRenderer<BasinBlockEntity> {

    public BasinBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {

    }

    @Override
    public void render(BasinBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

        var tankFluid = entity.tank.variant;
        var tankDrops = entity.tank.amount;
        var outputFluid = entity.output.variant;
        var outputDrops = entity.output.amount;
        var outputCapacity = entity.output.getCapacity();
        var outputFillPercent = (float) entity.outputDroplets / outputCapacity;
        var lastOutputFillPercent = (float) entity.lastOutputDroplets / outputCapacity;
        var interpFillPercent = MathHelper.lerp(tickDelta, lastOutputFillPercent, outputFillPercent);

        var firstHeight = 0F;

        if (!tankFluid.isBlank() && tankDrops > 0) {
            var color = FluidVariantRendering.getColor(tankFluid, entity.getWorld(), entity.getPos());
            firstHeight = RenderShortcuts.interpTank(entity.tankDroplets, entity.lastTankDroplets, entity.tank.getCapacity(), 0.9334F - 0.3F * interpFillPercent, tickDelta);
            RenderShortcuts.renderFluidSurface(tankFluid, color, false, 0, firstHeight, 0, matrices.peek().getPositionMatrix(), vertexConsumers, light, overlay);
        }

        if (!outputFluid.isBlank() && outputDrops > 0) {
            var color = FluidVariantRendering.getColor(outputFluid, entity.getWorld(), entity.getPos());
            var secondHeight = firstHeight + RenderShortcuts.interpTank(entity.outputDroplets, entity.lastOutputDroplets, entity.output.getCapacity(), 0.2F, tickDelta);
            RenderShortcuts.renderFluidSurface(outputFluid, color, false, 0, secondHeight, 0, matrices.peek().getPositionMatrix(), vertexConsumers, light, overlay);
        }
    }
}
