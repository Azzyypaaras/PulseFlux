package works.azzyys.pulseflux.block.fluid_storage;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.id.incubus_core.be.IncubusBaseBE;
import works.azzyys.pulseflux.block.PulseFluxBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class ReservoirBlockEntity extends IncubusBaseBE {

    private final SingleVariantStorage<FluidVariant> tank = new SingleVariantStorage<>() {
        @Override
        protected FluidVariant getBlankVariant() {
            return FluidVariant.blank();
        }

        @Override
        protected long getCapacity(FluidVariant variant) {
            return 8 * FluidConstants.BUCKET;
        }

        @Override
        protected void onFinalCommit() {
            markDirty();
        }
    };

    public ReservoirBlockEntity(BlockPos pos, BlockState state) {
        super(PulseFluxBlockEntities.RESERVOIR_TYPE, pos, state);
    }

    public SingleVariantStorage<FluidVariant> getTank() {
        return tank;
    }

    public boolean onPlayerUse(PlayerEntity player) {
        var handIo = ContainerItemContext.ofPlayerHand(player, Hand.MAIN_HAND).find(FluidStorage.ITEM);
        if (handIo != null) {
            // move from hand into this tank
            if (StorageUtil.move(handIo, tank, f -> true, Long.MAX_VALUE, null) > 0)
                return true;
            // move from this tank into hand
            if (StorageUtil.move(tank, handIo, f -> true, Long.MAX_VALUE, null) > 0)
                return true;
        }
        return false;
    }

    @Override
    public void save(NbtCompound nbt) {
        super.save(nbt);
        nbt.put("fluid", tank.getResource().toNbt());
        nbt.putLong("amount", tank.getAmount());
    }

    @Override
    public void load(NbtCompound nbt) {
        super.load(nbt);
        tank.variant = FluidVariant.fromNbt(nbt.getCompound("fluid"));
        tank.amount = nbt.getLong("amount");
    }

    @Override
    public void saveClient(NbtCompound nbt) {
        save(nbt);
    }

    @Override
    public void loadClient(NbtCompound nbt) {
        load(nbt);
    }
}
