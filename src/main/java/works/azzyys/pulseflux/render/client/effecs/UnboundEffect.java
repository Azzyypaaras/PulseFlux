package works.azzyys.pulseflux.render.client.effecs;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class UnboundEffect {

    protected long age;
    protected boolean removed;

    protected final UUID id;

    protected UnboundEffect(UUID id) {
        this.id = id;
    }

    abstract void render(@NotNull WorldRenderContext ctx, @Nullable HitResult hit, @Nullable WorldRenderContext.BlockOutlineContext outline);

    public void update(MinecraftClient client) {
        age++;
    }

    public boolean shouldUpdateInUnloadedWorld() {
        return false;
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

    public UUID getId() {
        return id;
    }

    abstract Identifier getName();

    abstract Identifier getCategory();

    
}
