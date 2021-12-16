package net.id.pulseflux.network;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.HashMap;
import java.util.UUID;

public class NetworkStorage implements AutoSyncedComponent {
    public static final HashMap<UUID, PipeNetwork> NETWORKS = new HashMap<>();

    public static UUID createNewNetwork() {
        UUID networkID;
        do {
            networkID = UUID.randomUUID();
        } while (NETWORKS.containsKey(networkID));
        PipeNetwork pipeNetwork = new PipeNetwork(networkID);
        NETWORKS.put(networkID, pipeNetwork);
        return networkID;
    }

    public static PipeNetwork getNetwork(UUID networkID) {
        return NETWORKS.get(networkID);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        NbtList networkNBTList = tag.getList("networks", NbtElement.COMPOUND_TYPE);
        networkNBTList.forEach(nbtElement -> {
            PipeNetwork pipeNetwork = PipeNetwork.readFromNbt((NbtCompound) nbtElement);
            NETWORKS.put(pipeNetwork.networkID, pipeNetwork);
        });
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        NbtList networkNBTList = new NbtList();
        NETWORKS.values().forEach(pipeNetwork -> {
            networkNBTList.add(pipeNetwork.writeToNbt());
        });
        tag.put("networks", networkNBTList);
    }
}
