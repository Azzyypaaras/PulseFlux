package works.azzyys.pulseflux.render.client.effecs;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.UUID;

public abstract class UnboundEffect {

    protected long age;
    protected boolean removed;

    protected final UUID id;

    protected UnboundEffect(UUID id) {
        this.id = id;
    }

    public abstract void render(@NotNull WorldRenderContext ctx, @Nullable HitResult hit, @Nullable WorldRenderContext.BlockOutlineContext outline);

    public void update(MinecraftClient client) {
        if (!client.isPaused())
            age++;
    }

    /**
     * Should this effect update if it is not in the same world as the client
     */
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

    public boolean isRemoved() {
        return removed;
    }

    /**
     * @return Whether the removal request was accepted
     */
    public abstract boolean requestRemoval();

    public UUID getId() {
        return id;
    }


    public abstract Identifier getName();

    public abstract Identifier getCategory();

    public static Vector3f setUpForCamera(Vector3f vector, Camera camera) {
        return vector.sub(camera.getPos().toVector3f());
    }

    public static void rotateToVectorOrientation(Vector3f vector, MatrixStack matrices, boolean adjusted) {
        var r = vector.length();
        var orientationX = Math.acos(vector.z / Math.sqrt(vector.z * vector.z + vector.x * vector.x)) * (vector.x < 0 ? -1 : 1);
        var orientationY = Math.acos(vector.y / r);

        matrices.multiply(RotationAxis.POSITIVE_Y.rotation((float) (orientationX + Math.PI)) );
        matrices.multiply(RotationAxis.NEGATIVE_X.rotation((float) (orientationY + (adjusted ? -Math.PI / 2 : 0))));
    }

    public static void rotateToVectorOrientation(Vector3f vector, MatrixStack matrices) {
        rotateToVectorOrientation(vector, matrices, true);
    }
}
