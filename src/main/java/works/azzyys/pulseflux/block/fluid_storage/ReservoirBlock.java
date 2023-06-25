package works.azzyys.pulseflux.block.fluid_storage;

import works.azzyys.pulseflux.block.base.PFBlockWithEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ReservoirBlock extends PFBlockWithEntity {

    public ReservoirBlock(Settings settings) {
        super(settings, false);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        var reservoir = (ReservoirBlockEntity) world.getBlockEntity(pos);
        if(reservoir.onPlayerUse(player)) {

            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ReservoirBlockEntity(pos, state);
    }
}
