package works.azzyys.pulseflux.util.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class RenderShortcuts {

    /**
     * If that vertexconsumer doesn't follow the VCTOLN format you are big dum dum
     */
    public static void renderSurface(Sprite sprite, float x, float y, float z, int color, Matrix4f matrix, VertexConsumer consumer, int light, int overlay) {
        var u0 = sprite.getMinU();
        var u1 = sprite.getMaxU();
        var v0 = sprite.getMinV();
        var v1 = sprite.getMaxV();

        consumer.vertex(matrix, x, y, z).color(color).texture(u0, v0).overlay(overlay).light(light).normal(0, 1, 0).next();
        consumer.vertex(matrix, x, y, z + 1).color(color).texture(u0, v1).overlay(overlay).light(light).normal(0, 1, 0).next();
        consumer.vertex(matrix, x + 1, y, z + 1).color(color).texture(u1, v1).overlay(overlay).light(light).normal(0, 1, 0).next();
        consumer.vertex(matrix, x + 1, y, z).color(color).texture(u1, v0).overlay(overlay).light(light).normal(0, 1, 0).next();
    }

    /**
     * @return Whether rendering was successful
     */
    public static boolean renderFluidSurface(FluidVariant fluid, int color, boolean flowing, float x, float y, float z, Matrix4f matrix, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var sprites = FluidVariantRendering.getSprites(fluid);

        if (sprites == null)
            return false;

        var sprite = FluidVariantRendering.getSprites(fluid)[flowing ? 1 : 0];

        renderSurface(sprite, x, y, z, color, matrix, vertexConsumers.getBuffer(RenderLayer.getTranslucentNoCrumbling()), light, overlay);

        return true;
    }

    public static float interpTank(long curDroplets, long lastDroplets, long capacity, float cap, float tickDelta) {
        var lastFill = (float) lastDroplets / capacity;
        var curFill = (float) curDroplets / capacity;

        var fillLine = MathHelper.lerp(tickDelta, lastFill, curFill);
        return MathHelper.lerp(fillLine, 0f, cap);
    }
}
