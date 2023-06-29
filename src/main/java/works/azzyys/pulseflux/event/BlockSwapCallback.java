package works.azzyys.pulseflux.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface BlockSwapCallback {

    Event<BlockSwapCallback> EVENT = EventFactory.createArrayBacked(BlockSwapCallback.class,
            (listeners) -> (world, pos, oldState, newState, flags) -> {
                for (BlockSwapCallback listener : listeners) {
                    listener.detectSwap(world, pos, oldState, newState, flags);
                }
            });

    void detectSwap(World world, BlockPos pos, BlockState oldState, BlockState newState, int flags);
}
