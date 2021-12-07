package net.id.pulseflux.blockentity.transport;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.impl.transfer.TransferApiImpl;
import net.id.incubus_core.systems.Material;
import net.id.pulseflux.blockentity.PFBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Iterator;

public class FluidPipeEntity extends PFBlockEntity {

    public FluidPipeEntity(BlockEntityType<?> type, Material material , BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void tick(BlockPos pos, BlockState state) {}

    @Override
    protected boolean initialize(World world, BlockPos pos, BlockState state) {

        initialized = true;
        return true;
    }

    @Override
    public void saveClient(NbtCompound nbt) {

    }

    @Override
    public void loadClient(NbtCompound nbt) {

    }
}
