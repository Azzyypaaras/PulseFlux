package works.azzyys.pulseflux.event;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public record SwapSignature(World world, BlockPos pos, BlockState oldState, BlockState newState, int flags) {
}
