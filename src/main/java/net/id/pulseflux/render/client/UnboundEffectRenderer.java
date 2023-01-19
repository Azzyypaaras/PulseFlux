package net.id.pulseflux.render.client;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class UnboundEffectRenderer {

    protected long age;
    protected boolean removed;

    abstract void render(@NotNull WorldRenderContext ctx, @Nullable HitResult hit, @Nullable WorldRenderContext.BlockOutlineContext outline);

    public void update(MinecraftClient client) {
        age++;
    }

    /**
     * Negative numbers indicate an effect with no defined end
     */
    public long maxAge() {
        return -1;
    }

    public boolean isFinished() {
        return maxAge() > 0 && age > -1;
    }

    public void markRemoved() {
        removed = true;
    }

    @FunctionalInterface
    public interface Factory<E extends UnboundEffectRenderer> {
        E create(PacketByteBuf byteBuf);
    }
}
