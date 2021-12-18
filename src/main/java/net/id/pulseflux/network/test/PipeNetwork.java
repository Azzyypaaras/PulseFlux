package net.id.pulseflux.network.test;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.UUID;

public class PipeNetwork {
    public UUID networkID;
    HashSet<BlockPos> connectedPipes = new HashSet<>();

    public PipeNetwork(UUID networkID) {
        this.networkID = networkID;
    }

    public boolean addPipeToNetwork(BlockPos pos) {
        return connectedPipes.add(pos);
    }

    public static PipeNetwork readFromNbt(NbtCompound tag) {
        UUID networkID = tag.getUuid("networkID");
        PipeNetwork pipeNetwork = new PipeNetwork(networkID);
        return pipeNetwork;
    }

    public NbtCompound writeToNbt() {
        NbtCompound tag = new NbtCompound();
        tag.putUuid("networkID", networkID);
        return tag;
    }
}
