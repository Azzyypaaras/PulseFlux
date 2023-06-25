package works.azzyys.pulseflux.render.client.effecs;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import works.azzyys.pulseflux.PulseFlux;
import works.azzyys.pulseflux.render.server.PulseFluxEffectIdentifiers;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.LightType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.UUID;

public class TestUnboundEffect extends UnboundEffect {

    public static final Identifier TEST = PulseFlux.locate("test");
    public static final Identifier TEXTURE = PulseFlux.locate("textures/bitchless.png");

    public TestUnboundEffect() {
        super(UUID.randomUUID());
    }

    @Override
    void render(@NotNull WorldRenderContext ctx, @Nullable HitResult hit, @Nullable WorldRenderContext.BlockOutlineContext outline) {
        var vector = new Vector3f(118.5F, 69.5F, -18.5F);
        var camera = ctx.camera().getPos();
        var consumers = ctx.consumers();
        var world = ctx.world();
        var pos = new BlockPos(vector.x, vector.y, vector.z);

        vector.sub(camera.toVector3f());

        var overlay = OverlayTexture.DEFAULT_UV;
        var light = LightmapTextureManager.pack(world.getLightLevel(LightType.BLOCK, pos), world.getLightLevel(LightType.SKY, pos));

        var r = vector.length();
        var orientationX = Math.acos(vector.z / Math.sqrt(vector.z * vector.z + vector.x * vector.x)) * (vector.x < 0 ? -1 : 1);
        var orientationY = Math.acos(vector.y / r);

        if (consumers == null)
            return;

        var matrices = ctx.matrixStack();

        matrices.push();

        matrices.translate(vector.x, vector.y, vector.z);

        matrices.multiply(RotationAxis.POSITIVE_Y.rotation((float) orientationX));
        matrices.multiply(RotationAxis.POSITIVE_X.rotation((float) (orientationY - Math.PI / 2)));

        matrices.scale(5, 5, 5);
        matrices.translate(-0.5F, -0.5F, 0);

        var consumer = consumers.getBuffer(RenderLayer.getEntityTranslucent(TEXTURE));

        var matrix = matrices.peek();
        var positions = matrix.getPositionMatrix();
        var normals = matrix.getNormalMatrix();

        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();

        consumer.vertex(positions, 0, 0, 0).color(1f, 1f, 1f, 1f).texture(1, 1).overlay(overlay).light(light).normal(normals, 0, 0, 0).next();
        consumer.vertex(positions, 1, 0, 0).color(1f, 1f, 1f, 1f).texture(0, 1).overlay(overlay).light(light).normal(normals, 1, 0, 0).next();
        consumer.vertex(positions, 1, 1, 0).color(1f, 1f, 1f, 1f).texture(0, 0).overlay(overlay).light(light).normal(normals, 1, 1, 0).next();
        consumer.vertex(positions, 0, 1, 0).color(1f, 1f, 1f, 1f).texture(1, 0).overlay(overlay).light(light).normal(normals, 0, 1, 0).next();

        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();

        matrices.pop();
    }

    @Override
    public long maxAge() {
        return -1;
    }

    @Override
    Identifier getName() {
        return TEST;
    }

    @Override
    Identifier getCategory() {
        return PulseFluxEffectIdentifiers.TESTING;
    }
}
