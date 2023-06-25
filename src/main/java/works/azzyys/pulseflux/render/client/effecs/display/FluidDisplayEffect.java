package works.azzyys.pulseflux.render.client.effecs.display;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import works.azzyys.pulseflux.PulseFlux;
import works.azzyys.pulseflux.network.FluidNetwork;
import works.azzyys.pulseflux.network.NetworkManager;
import works.azzyys.pulseflux.render.client.effecs.UnboundEffect;
import works.azzyys.pulseflux.render.server.PulseFluxEffectIdentifiers;

import java.util.Optional;
import java.util.UUID;

public class FluidDisplayEffect extends UnboundEffect {

    private static final Identifier BACKGROUND = PulseFlux.locate("textures/ufs/network_displays/fluid_iui_background.png");
    private static final Identifier CORNER = PulseFlux.locate("textures/ufs/network_displays/fluid_iui_corner.png");
    private static final Identifier EDGES = PulseFlux.locate("textures/ufs/network_displays/fluid_iui_edges.png");
    private static final Identifier OVERLAY = PulseFlux.locate("textures/ufs/network_displays/fluid_iui_overlay.png");
    private final UUID networkId;
    private final Optional<FluidNetwork> network = Optional.empty();
    private final Vector3f pos;
    private short deathTicks = 16;
    private boolean finalizing;

    public FluidDisplayEffect(UUID networkId, Vector3f pos, UUID id) {
        super(id);
        this.networkId = networkId;
        this.pos = pos;
    }

    @Override
    public void update(MinecraftClient client) {
        super.update(client);
        var world = client.world;

        if (deathTicks == 0)
            markRemoved();

        if (finalizing)
            deathTicks--;

        if (age > maxAge())
            finalizing = true;

        if (network.isEmpty())
            NetworkManager.getNetworkManager(world).tryFetchNetwork(networkId);
    }

    @Override
    public void render(@NotNull WorldRenderContext ctx, @Nullable HitResult hit, WorldRenderContext.@Nullable BlockOutlineContext outline) {
        var tickDelta = ctx.tickDelta();
        var vector = new Vector3f(pos).add(0, 0.5F, 0);
        setUpForCamera(vector, ctx.camera());

        // All the fancy animations
        var bobbing = (Math.sin((age + ctx.tickDelta()) / 20) - 0.5) / 18;
        var startup = MathHelper.clampedLerp(0F, 1F, (Math.pow(age + tickDelta, 0.3) * 4) / 9F);
        var alphaMult = MathHelper.clampedLerp(0F, 1F, (age + tickDelta) / 8F);
        var squeeze = alphaMult;

        var deathProgress = deathTicks - tickDelta;
        var stop = deathProgress <= 1;

        var shutdown = MathHelper.clampedLerp(0F, 1F, deathProgress / 15F);
        var yOffset = (1 - (1 * startup * shutdown)) * 0.75F;

        if (stop) {
            alphaMult = 0;
        }
        else if (deathProgress < 20) {
            alphaMult *= Math.max(0, shutdown - 0.1);
        }


        var consumers = ctx.consumers();
        var matrices = ctx.matrixStack();
        var overlay = OverlayTexture.DEFAULT_UV;
        var light = LightmapTextureManager.MAX_LIGHT_COORDINATE;

        if (consumers == null)
            return;

        // Setup
        matrices.push();
        matrices.translate(vector.x, vector.y - yOffset + bobbing, vector.z);
        rotateToVectorOrientation(vector, matrices);

        matrices.scale(1F, squeeze, 1);
        matrices.scale(0.6F, 0.6F, 0.6F);
        matrices.translate(-0.75F, -0.5F, 0);

        var positions = matrices.peek().getPositionMatrix();
        var normals = matrices.peek().getNormalMatrix();
        var color = 0xFFFFFF + (Math.round(255 * alphaMult) << 24);

        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();

        // Background
        var consumer = consumers.getBuffer(RenderLayer.getEntityTranslucent(BACKGROUND));
        consumer.vertex(positions, 0, 0, 0).color(1f, 1f, 1f, 0.75f * alphaMult).texture(0, 1).overlay(overlay).light(light).normal(normals, 0, 1, 1).next();
        consumer.vertex(positions, 1.5F, 0, 0).color(1f, 1f, 1f, 0.65f * alphaMult).texture(1, 1).overlay(overlay).light(light).normal(normals, 0, 1, 1).next();
        consumer.vertex(positions, 1.5F, 1, 0).color(1f, 1f, 1f, 0.9f * alphaMult).texture(1, 0).overlay(overlay).light(light).normal(normals, 0, 1, 1).next();
        consumer.vertex(positions, 0, 1, 0).color(1f, 1f, 1f, alphaMult).texture(0, 0).overlay(overlay).light(light).normal(normals, 0, 1, 1).next();

        consumers.getBuffer(RenderLayer.getEntityTranslucent(OVERLAY));
        consumer.vertex(positions, 0, 0, 0).color(color).texture(0, 1).overlay(overlay).light(light).normal(normals, 0, 1, 1).next();
        consumer.vertex(positions, 1.5F, 0, 0).color(color).texture(1, 1).overlay(overlay).light(light).normal(normals, 0, 1, 1).next();
        consumer.vertex(positions, 1.5F, 1, 0).color(color).texture(1, 0).overlay(overlay).light(light).normal(normals, 0, 1, 1).next();
        consumer.vertex(positions, 0, 1, 0).color(color).texture(0, 0).overlay(overlay).light(light).normal(normals, 0, 1, 1).next();

        consumers.getBuffer(RenderLayer.getEntityTranslucent(EDGES));
        consumer.vertex(positions, 0, 0, 0).color(color).texture(0, 1).overlay(overlay).light(light).normal(normals, 0, 1, 1).next();
        consumer.vertex(positions, 1.5F, 0, 0).color(color).texture(1, 1).overlay(overlay).light(light).normal(normals, 0, 1, 1).next();
        consumer.vertex(positions, 1.5F, 1, 0).color(color).texture(1, 0).overlay(overlay).light(light).normal(normals, 0, 1, 1).next();
        consumer.vertex(positions, 0, 1, 0).color(color).texture(0, 0).overlay(overlay).light(light).normal(normals, 0, 1, 1).next();

        consumer = consumers.getBuffer(RenderLayer.getEntityTranslucent(CORNER));
        consumer.vertex(positions, 0, 1 - 0.1875F, 0.01F).color(color).texture(0, 1).overlay(overlay).light(light).normal(normals, 0, 1, 1).next();
        consumer.vertex(positions, 0.1875F, 1 - 0.1875F, 0.01F).color(color).texture(1, 1).overlay(overlay).light(light).normal(normals, 0, 1, 1).next();
        consumer.vertex(positions, 0.1875F, 1, 0.01F).color(color).texture(1, 0).overlay(overlay).light(light).normal(normals, 0, 1, 1).next();
        consumer.vertex(positions, 0, 1, 0.01F).color(color).texture(0, 0).overlay(overlay).light(light).normal(normals, 0, 1, 1).next();

        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();

        matrices.translate(0, 1, 0.01);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation((float) (Math.PI)));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotation((float) (Math.PI)));
        matrices.scale(0.01334F, 0.01334F, 1);

        // The actual UI
        //MinecraftClient.getInstance().textRenderer.draw(matrices, "balls", 1, 1, 0xFFFFFF);

        var name = "NO CONNECTION";
        var type = ".......";
        if (network.isPresent()) {
            name = network.get().tryGetName().orElse("unnamed");
            type = "fluid network";
        }

        MinecraftClient.getInstance().textRenderer.draw(name, 16, 11, 0xFFFFFF, false, positions, ctx.consumers(), false, 0, light);

        matrices.scale(0.5F, 0.5F, 1);

        MinecraftClient.getInstance().textRenderer.draw(type, 32, 40, 0xFFFFFF, false, positions, ctx.consumers(), false, 0, light);


        matrices.pop();
    }

    @Override
    public long maxAge() {
        return 600;
    }

    @Override
    public boolean requestRemoval() {
        finalizing = true;
        return true;
    }

    @Override
    public Identifier getName() {
        return PulseFluxEffectIdentifiers.FLUID_NETWORK_DISPLAY;
    }

    @Override
    public Identifier getCategory() {
        return PulseFluxEffectIdentifiers.IUI_DISPLAYS;
    }
}
