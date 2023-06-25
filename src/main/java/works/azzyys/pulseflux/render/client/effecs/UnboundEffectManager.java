package works.azzyys.pulseflux.render.client.effecs;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.id.incubus_core.annotations.NonnullByDefault;
import works.azzyys.pulseflux.render.client.RenderStage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Environment(EnvType.CLIENT)
public class UnboundEffectManager {

    private static final Map<RenderStage, Map<RegistryKey<World>, List<UnboundEffect>>> ACTIVE_EFFECTS;
    private static final Map<UUID, UnboundEffect> EFFECTS_BY_ID;
    private static RenderStage currentStage;

    public static void init() {
        WorldRenderEvents.BEFORE_ENTITIES.register(context -> UnboundEffectManager.render(context, null, null, RenderStage.PRE_ENTITIES));
        WorldRenderEvents.AFTER_ENTITIES.register(context -> UnboundEffectManager.render(context, null, null, RenderStage.POST_ENTITIES));
        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((context, hit) -> UnboundEffectManager.render(context, hit, null, RenderStage.PRE_OUTLINES));
        WorldRenderEvents.BLOCK_OUTLINE.register((context, outline) -> UnboundEffectManager.render(context, null, outline, RenderStage.OUTLINES));
        WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> UnboundEffectManager.render(context, null, null, RenderStage.TRANSLUCENT));
        WorldRenderEvents.LAST.register(context -> UnboundEffectManager.render(context, null, null, RenderStage.LAST));

        ClientTickEvents.END_CLIENT_TICK.register(UnboundEffectManager::update);
    }

    public static boolean render(@NonnullByDefault WorldRenderContext ctx, @Nullable HitResult hit, @Nullable WorldRenderContext.BlockOutlineContext outline, RenderStage stage) {
        currentStage = stage;
        var world = MinecraftClient.getInstance().world != null ? MinecraftClient.getInstance().world.getRegistryKey() : null;

        render: {
            if (world != null) {
                var effects = ACTIVE_EFFECTS.get(stage).get(world);

                if (effects == null)
                    break render;

                for (UnboundEffect effect : effects) {
                    effect.render(ctx, hit, outline);
                }
            }
        }

        currentStage = RenderStage.NONE;

        return true;
    }

    public static void update(MinecraftClient client) {

        var world = MinecraftClient.getInstance().world != null ? MinecraftClient.getInstance().world.getRegistryKey() : null;

        for (Map<RegistryKey<World>, List<UnboundEffect>> worldEffects : ACTIVE_EFFECTS.values()) {

            for (Map.Entry<RegistryKey<World>, List<UnboundEffect>> effectList : worldEffects.entrySet()) {
                var removed = new ArrayList<UnboundEffect>();

                for (UnboundEffect effect : effectList.getValue()) {

                    if (!effectList.getKey().equals(world) && !effect.shouldUpdateInUnloadedWorld())
                        continue;

                    if (effect.isFinished()) {
                        removed.add(effect);
                    }
                    else {
                        effect.update(client);
                    }
                }

                effectList.getValue().removeAll(removed);
                removed.stream().map(UnboundEffect::getId).forEach(EFFECTS_BY_ID::remove);
            }
        }
    }

    public static <E extends UnboundEffect> E track(E effect, RegistryKey<World> world, RenderStage stage) {
        ACTIVE_EFFECTS.get(stage).computeIfAbsent(world, (key) -> new ArrayList<>()).add(effect);
        EFFECTS_BY_ID.put(effect.id, effect);
        return effect;
    }

    public Optional<UnboundEffect> getById(UUID uuid) {
        return Optional.ofNullable(EFFECTS_BY_ID.get(uuid));
    }

    public List<UnboundEffect> getByName(Identifier name) {
        return EFFECTS_BY_ID
                .values()
                .stream()
                .filter(effect -> effect.getName().equals(name))
                .toList();
    }

    public List<UnboundEffect> getByCategory(Identifier category) {
        return EFFECTS_BY_ID
                .values()
                .stream()
                .filter(effect -> effect.getCategory().equals(category))
                .toList();
    }

    public static void remove(UUID uuid) {
        ACTIVE_EFFECTS.values()
                .forEach(map -> {

                    UnboundEffect removed = null;
                    for (List<UnboundEffect> list : map.values()) {

                        for (UnboundEffect unboundEffect : list) {
                            if (unboundEffect.getId().equals(uuid)) {
                                removed = unboundEffect;
                                break;
                            }
                        }

                        list.remove(removed);
                        EFFECTS_BY_ID.remove(uuid);

                    }
                });
    }

    public RenderStage getCurrentStage() {
        return currentStage;
    }

    static {
        var effectBuilder = ImmutableMap.<RenderStage, Map<RegistryKey<World>, List<UnboundEffect>>>builder();
        Arrays.stream(RenderStage.values())
                .filter(renderStage -> renderStage != RenderStage.NONE)
                .forEach(renderStage -> effectBuilder.put(renderStage, new HashMap<>()));
        ACTIVE_EFFECTS = effectBuilder.build();

        EFFECTS_BY_ID = new HashMap<>();
    }
}
