package net.id.pulseflux.network;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;
import java.util.function.Predicate;

public class FluidNetwork extends TransferNetwork<FluidNetwork> implements SingleSlotStorage<FluidVariant> {

    public FluidNetwork(World world, UUID networkId) {
        super(world, networkId);
    }

    public FluidNetwork(World world, NbtCompound nbt) {
        super(world, nbt);
    }

    @Override
    void revalidateCapacity() {

    }

    @Override
    void yieldTo(TransferNetwork<?> network, NetworkManager manager) {

    }

    @Override
    void postAppend(BlockPos pos) {

    }

    @Override
    public boolean isComponentValid(BlockPos pos, BlockState state) {
        return false;
    }

    @Override
    void postRemove() {

    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        return 0;
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        return 0;
    }

    @Override
    public boolean isResourceBlank() {
        return false;
    }

    @Override
    public FluidVariant getResource() {
        return null;
    }

    @Override
    public long getAmount() {
        return 0;
    }

    @Override
    public long getCapacity() {
        return 0;
    }
}
