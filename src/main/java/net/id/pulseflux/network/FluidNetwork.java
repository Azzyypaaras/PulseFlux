package net.id.pulseflux.network;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;
import java.util.function.Predicate;

public class FluidNetwork extends TransferNetwork<FluidNetwork> implements SingleSlotStorage<FluidVariant> {

    public FluidNetwork(World world, UUID networkId, Predicate componentValidator) {
        super(world, networkId, componentValidator);
    }

    public FluidNetwork(World world, NbtCompound nbt, Predicate componentValidator) {
        super(world, nbt, componentValidator);
    }

    @Override
    void revalidateCapacity() {

    }

    @Override
    void yieldTo(FluidNetwork network, NetworkManager manager) {

    }

    @Override
    void postAppend(BlockPos pos) {

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
