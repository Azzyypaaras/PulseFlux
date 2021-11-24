package net.id.pulseflux.mixin.world;

import net.id.pulseflux.systems.Polarity;
import net.id.pulseflux.systems.IoProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldChunk.class)
public class WorldChunkMixin {

    @Shadow @Final
    World world;

    @Inject(method = "removeBlockEntity(Lnet/minecraft/util/math/BlockPos;)V", at = @At("HEAD"))
    public void notifyRemoval(BlockPos pos, CallbackInfo ci) {
        updateChildren(world.getBlockEntity(pos));
    }

    @Inject(method = "removeBlockEntity(Lnet/minecraft/block/entity/BlockEntity;)V", at = @At("HEAD"))
    public void notifyRemoval(BlockEntity blockEntity, CallbackInfo ci) {
        updateChildren(blockEntity);
    }

    private static void updateChildren(BlockEntity be) {
        if(be instanceof IoProvider provider && !be.getWorld().isClient()) {
            var children = provider.getChildren();
            children.forEach(child -> provider.requestUpdate(child.object(), 0, 0, 0, Polarity.NONE));
        }
    }
}
