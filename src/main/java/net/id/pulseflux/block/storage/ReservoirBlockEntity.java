package net.id.pulseflux.block.storage;

import net.id.incubus_core.be.IncubusBaseBE;
import net.id.pulseflux.block.PulseFluxBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class ReservoirBlockEntity extends IncubusBaseBE {


    public ReservoirBlockEntity(BlockPos pos, BlockState state) {
        super(PulseFluxBlockEntities.RESERVOIR_TYPE, pos, state);
    }



    @Override
    public void saveClient(NbtCompound nbt) {

    }

    @Override
    public void loadClient(NbtCompound nbt) {

    }
}
