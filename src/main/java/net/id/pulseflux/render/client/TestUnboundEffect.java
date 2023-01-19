package net.id.pulseflux.render.client;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Optional;

public class TestUnboundEffect extends UnboundEffectRenderer {

    @Override
    void render(@NotNull WorldRenderContext ctx, @Nullable HitResult hit, @Nullable WorldRenderContext.BlockOutlineContext outline) {
        var vector = new Vector3f(0, 70, 0);
        var camera = ctx.camera().getPos();
        var consumers = ctx.consumers();

        vector = vector.sub(camera.toVector3f());

        var overlay = OverlayTexture.DEFAULT_UV;
        var light = LightmapTextureManager.MAX_LIGHT_COORDINATE;

        var x = vector.x;
        var y = vector.y;
        var z = vector.z;

        if (consumers == null)
            return;

        var matrices = ctx.matrixStack();
        var consumer = consumers.getBuffer(RenderLayer.getTranslucentNoCrumbling());
        var matrix = matrices.peek().getPositionMatrix();
        consumer.vertex(matrix, x, y, z).color(255, 255, 255, 255).texture(0, 0).overlay(overlay).light(light).normal(0, 1, 0).next();
        consumer.vertex(matrix, x, y, z + 1).color(255, 255, 255, 255).texture(0, 1).overlay(overlay).light(light).normal(0, 1, 0).next();
        consumer.vertex(matrix, x + 1, y, z + 1).color(255, 255, 255, 255).texture(1, 1).overlay(overlay).light(light).normal(0, 1, 0).next();
        consumer.vertex(matrix, x + 1, y, z).color(255, 255, 255, 255).texture(1, 0).overlay(overlay).light(light).normal(0, 1, 0).next();
    }

    @Override
    public long maxAge() {
        return 200;
    }
}
