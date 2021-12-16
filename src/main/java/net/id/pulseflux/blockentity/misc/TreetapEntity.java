package net.id.pulseflux.blockentity.misc;

import net.id.pulseflux.blockentity.PFBlockEntity;
import net.id.pulseflux.blockentity.PulseFluxBlockEntities;
import net.id.pulseflux.util.transfer.SingleFluidStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TreetapEntity extends PFBlockEntity {

    private final SingleFluidStorage tank = new SingleFluidStorage();

    public TreetapEntity(BlockPos pos, BlockState state) {
        super(PulseFluxBlockEntities.TREETAP_TYPE, pos, state, 10);
    }

    @Override
    protected void tick(BlockPos pos, BlockState state) {
        if(allowTick()) {

        }
    }

    @Override
    public void save(NbtCompound nbt) {
        super.save(nbt);
        tank.save(nbt);
    }

    @Override
    public void load(NbtCompound nbt) {
        super.load(nbt);
        tank.load(nbt);
    }

    @Override
    public void saveClient(NbtCompound nbt) {
        save(nbt);
    }

    @Override
    public void loadClient(NbtCompound nbt) {
        load(nbt);
    }

    public SingleFluidStorage getTank() {
        return tank;
    }
}
