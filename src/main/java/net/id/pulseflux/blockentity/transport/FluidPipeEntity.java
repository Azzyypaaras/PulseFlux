package net.id.pulseflux.blockentity.transport;

import net.id.pulseflux.blockentity.PulseFluxBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class FluidPipeEntity extends BlockEntity {
    UUID connectedNetworkID;

    public FluidPipeEntity(BlockPos pos, BlockState state) {
        this(PulseFluxBlockEntities.FLUID_PIPE_BLOCK_ENTITY_TYPE, pos, state);
    }

    public FluidPipeEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("connectedNetworkID"))
            nbt.getUuid("connectedNetworkID");
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        if (connectedNetworkID != null)
            nbt.putUuid("connectedNetworkID", connectedNetworkID);
        return super.writeNbt(nbt);
    }
}