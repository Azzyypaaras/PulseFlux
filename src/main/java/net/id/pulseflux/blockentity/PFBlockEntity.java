package net.id.pulseflux.blockentity;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.id.pulseflux.PulseFlux;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public abstract class PFBlockEntity extends BlockEntity implements BlockEntityClientSerializable {

    protected boolean initialized;
    private final int tickSpacing, tickOffset;

    public PFBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int tickSpacing) {
        super(type, pos, state);
        this.tickSpacing = tickSpacing;
        this.tickOffset = tickSpacing > 1 ? PulseFlux.random.nextInt(tickSpacing) : 0;
    }

    public PFBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        this(type, pos, state, 0);
    }

    public static <T extends BlockEntity> void tick(World world, BlockPos pos, BlockState state, T be) {
        var entity = (PFBlockEntity) be;
        if(!entity.initialized) {
            entity.initialized = entity.initialize(world, pos, state);
        }
        if(entity.hasInitialized()) {
            entity.tick(pos, state);
            if(!world.isClient()) {
                entity.tickServer(pos, state);
            }
        }
    }

    protected abstract void tick(BlockPos pos, BlockState state);

    public void tickServer(BlockPos pos, BlockState state) {}

    public boolean allowTick() {
        return tickSpacing == 0 || (world.getTime() + tickOffset) % tickSpacing == 0;
    }

    protected abstract boolean initialize(World world, BlockPos pos, BlockState state);

    public boolean hasInitialized() {
        return initialized;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putBoolean("initialized", initialized);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        initialized = nbt.getBoolean("initialized");
    }

    @Override
    public NbtCompound toClientTag(NbtCompound nbt) {
        nbt.putBoolean("initialized", initialized);
        return nbt;
    }

    @Override
    public void fromClientTag(NbtCompound nbt) {
        initialized = nbt.getBoolean("initialized");
    }
}
