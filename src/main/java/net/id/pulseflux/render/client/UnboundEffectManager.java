package net.id.pulseflux.render.client;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.id.incubus_core.annotations.NonnullByDefault;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Environment(EnvType.CLIENT)
public class UnboundEffectManager {


    private static final Map<Identifier, UnboundEffectRenderer.Factory<?>> FACTORIES = new HashMap<>();
    private static final Map<RenderStage, List<UnboundEffectRenderer>> ACTIVE_EFFECTS;
    private static RenderStage currentStage;

    public static boolean render(@NonnullByDefault WorldRenderContext ctx, @Nullable HitResult hit, @Nullable WorldRenderContext.BlockOutlineContext outline, RenderStage stage) {
        currentStage = stage;

        var effects = ACTIVE_EFFECTS.get(stage);
        for (UnboundEffectRenderer effect : effects) {
            effect.render(ctx, hit, outline);
        }

        currentStage = RenderStage.NONE;

        return true;
    }

    public static void update(MinecraftClient client) {
        for (List<UnboundEffectRenderer> effectList : ACTIVE_EFFECTS.values()) {
            var removed = new ArrayList<UnboundEffectRenderer>();

            for (UnboundEffectRenderer effect : effectList) {
                if (effect.isFinished()) {
                    removed.add(effect);
                }
                else {
                    effect.update(client);
                }
            }

            effectList.removeAll(removed);
        }
    }

    public RenderStage getCurrentStage() {
        return currentStage;
    }

    public void register(UnboundEffectRenderer.Factory<?> factory, Identifier id) {
        FACTORIES.put(id, factory);
    }

    static {
        var effectBuilder = ImmutableMap.<RenderStage, List<UnboundEffectRenderer>>builder();
        Arrays.stream(RenderStage.values())
                .filter(renderStage -> renderStage != RenderStage.NONE)
                .forEach(renderStage -> effectBuilder.put(renderStage, new ArrayList<>()));
        ACTIVE_EFFECTS = effectBuilder.build();
    }
}
