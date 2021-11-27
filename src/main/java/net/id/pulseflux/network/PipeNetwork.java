package net.id.pulseflux.network;

import net.minecraft.nbt.NbtCompound;

import java.util.UUID;

public class PipeNetwork {
    public UUID networkID;

    public PipeNetwork(UUID networkID) {
        this.networkID = networkID;
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
