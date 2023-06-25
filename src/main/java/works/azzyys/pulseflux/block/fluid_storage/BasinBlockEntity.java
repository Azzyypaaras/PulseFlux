package works.azzyys.pulseflux.block.fluid_storage;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.*;
import net.id.incubus_core.util.RegistryQueue;
import works.azzyys.pulseflux.block.PulseFluxBlockEntities;
import works.azzyys.pulseflux.block.base.PFTickingBE;
import works.azzyys.pulseflux.systems.PressureHolder;
import works.azzyys.pulseflux.util.transfer.SingleFluidStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class BasinBlockEntity extends PFTickingBE implements PressureHolder {

    long outputDroplets, tankDroplets, lastOutputDroplets, lastTankDroplets;

    final SingleFluidStorage tank = new SingleFluidStorage("tank", 5 * FluidConstants.BUCKET);
    final SingleFluidStorage output = new SingleFluidStorage("output", FluidConstants.BUCKET, false, true);

    public static final RegistryQueue.Action<BlockEntityType<?>> lookup = (id, type) -> FluidStorage.SIDED.registerForBlockEntity((entity, direction) -> {
        if (direction.getAxis().isHorizontal()) {
            return ((BasinBlockEntity) entity).tank;
        }
        return ((BasinBlockEntity) entity).output;
    }, type);


    public BasinBlockEntity(BlockPos pos, BlockState state) {
        super(PulseFluxBlockEntities.STONE_BASIN_TYPE, pos, state);
    }

    @Override
    protected void tick(BlockPos pos, BlockState state) {
        lastTankDroplets = tankDroplets;
        lastOutputDroplets = outputDroplets;

        if (tankDroplets != tank.getAmount()) {
            var dif = (tankDroplets - tank.getAmount()) * -1;
            if (Math.abs(dif) < 50) {
                tankDroplets = tank.getAmount();
            }
            else {
                tankDroplets += dif / 3;
            }
        }

        if (outputDroplets != output.getAmount()) {
            var dif = (outputDroplets - output.getAmount()) * -1;
            if (Math.abs(dif) < 50) {
                outputDroplets = output.getAmount();
            }
            else {
                outputDroplets += dif / 3;
            }
        }
    }

    public boolean onPlayerUse(PlayerEntity player) {
        var io = ContainerItemContext.ofPlayerHand(player, player.getActiveHand()).find(FluidStorage.ITEM);

        //if (output.getAmount() > 0) {
            var sound = FluidVariantAttributes.getFillSound(output.getResource());
            if (FluidStorageUtil.interactWithFluidStorage(output, player, player.getActiveHand())) {
                return true;
            }
        //}

        return FluidStorageUtil.interactWithFluidStorage(tank, player, player.getActiveHand());
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

    @Override
    public long queryPressure() {
        return 0;
    }
}
