package net.id.pulseflux.util.transfer;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.NbtCompound;

import javax.annotation.Nullable;
import java.util.Optional;

public class SingleFluidStorage extends SingleVariantStorage<FluidVariant> {

    private final Optional<String> name;
    private final long capacity;
    private final boolean allowInsertion, allowExtraction;

    public SingleFluidStorage() {
        this(FluidConstants.BUCKET);
    }

    public SingleFluidStorage(long capacity) {
        this(null, capacity, true, true);
    }

    public SingleFluidStorage(String name, long capacity) {
        this(name, capacity, true, true);
    }

    public SingleFluidStorage(@Nullable String name, long capacity, boolean allowInsertion, boolean allowExtraction) {
        this.name = Optional.ofNullable(name);
        this.capacity = capacity;
        this.allowInsertion = allowInsertion;
        this.allowExtraction = allowExtraction;
    }

    @Override
    public boolean supportsInsertion() {
        return allowInsertion;
    }

    @Override
    public long insert(FluidVariant insertedVariant, long maxAmount, TransactionContext transaction) {
        return super.insert(insertedVariant, maxAmount, transaction);
    }

    @Override
    public boolean supportsExtraction() {
        return allowExtraction;
    }



    @Override
    protected FluidVariant getBlankVariant() {
        return FluidVariant.blank();
    }

    @Override
    protected long getCapacity(FluidVariant variant) {
        return capacity;
    }

    public void save(NbtCompound nbt) {
        var tag = nbt;

        if (name.isPresent()) {
            tag = new NbtCompound();
            nbt.put(name.get(), tag);
        }

        tag.put("fluid", getResource().toNbt());
        tag.putLong("amount", amount);
    }

    public void load(NbtCompound nbt) {
        nbt = name.map(nbt::getCompound).orElse(nbt);
        variant = FluidVariant.fromNbt((NbtCompound) nbt.get("fluid"));
        amount = nbt.getLong("amount");
    }
}
