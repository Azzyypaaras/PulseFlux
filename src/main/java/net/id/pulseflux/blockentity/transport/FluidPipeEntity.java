package net.id.pulseflux.blockentity.transport;

import net.id.pulseflux.blockentity.PFBlockEntity;
import net.id.pulseflux.blockentity.PulseFluxBlockEntities;
import net.id.pulseflux.network.test.NetworkStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public class FluidPipeEntity extends PFBlockEntity {
    private UUID connectedNetworkID;

    public FluidPipeEntity(BlockPos pos, BlockState state) {
        this(PulseFluxBlockEntities.FLUID_PIPE_BLOCK_ENTITY_TYPE, Material.WOOD, pos, state);
    }

    public FluidPipeEntity(BlockEntityType<?> type, Material material, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void tick(BlockPos pos, BlockState state) {
    }

    @Override
    protected boolean initialize(World world, BlockPos pos, BlockState state) {
        initialized = true;
        //temporary, we probably shouldn't register a new network unless we need to
        if (!world.isClient)
            System.out.println(NetworkStorage.createNewNetwork(pos));
        return true;
    }

    @Override
    public void load(NbtCompound nbt) {
        super.load(nbt);
        if (nbt.contains("connectedNetworkID"))
            nbt.getUuid("connectedNetworkID");
    }

    @Override
    public void save(NbtCompound nbt) {
        super.save(nbt);
        if (connectedNetworkID != null)
            nbt.putUuid("connectedNetworkID", connectedNetworkID);
    }

    @Override
    public void saveClient(NbtCompound nbt) {
        if (connectedNetworkID != null)
            nbt.putUuid("connectedNetworkID", connectedNetworkID);
    }

    @Override
    public void loadClient(NbtCompound nbt) {
        if (nbt.contains("connectedNetworkID"))
            nbt.getUuid("connectedNetworkID");
    }
}