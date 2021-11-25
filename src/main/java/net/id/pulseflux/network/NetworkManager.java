package net.id.pulseflux.network;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.id.pulseflux.PulseFlux;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.minecraft.world.level.LevelProperties;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static net.id.pulseflux.PulseFlux.*;
import static net.id.pulseflux.PulseFluxComponents.*;

public class NetworkManager implements AutoSyncedComponent, ServerTickingComponent {

    public final World world;
    public final Object2ObjectOpenHashMap<UUID, LogisticNetwork<?>> managedNetworks = new Object2ObjectOpenHashMap<>(32);

    public NetworkManager(World world) {
        this.world = world;
        if(world.getLevelProperties() instanceof LevelProperties)
            LOG.info((!world.isClient() ? "Initializing 'ServerLevel" : "Pairing 'Client") + "[" + ((LevelProperties) world.getLevelProperties()).getLevelName() + "]'/" + world.getRegistryKey().getValue().toString() + " network manager");
    }

    public static @NotNull NetworkManager getNetworkManager(World world) {
        return NETWORK_MANAGER_KEY.get(world);
    }

    @Override
    public void serverTick() {

    }

    @Override
    public void readFromNbt(NbtCompound tag) {

    }

    @Override
    public void writeToNbt(NbtCompound tag) {

    }
}
