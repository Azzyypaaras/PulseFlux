package net.id.pulseflux.registry;

import net.id.incubus_core.util.RegistryQueue;
import net.id.pulseflux.network.TransferNetwork.NetworkReconstructor;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;

public class PulseFluxRegistryQueues {
    public static final RegistryQueue<Block> BLOCK = new RegistryQueue<>(Registries.BLOCK, 256);
    public static final RegistryQueue<BlockEntityType<?>> BLOCK_ENTITY_TYPE = new RegistryQueue<>(Registries.BLOCK_ENTITY_TYPE, 256);
    public static final RegistryQueue<EntityType<?>> ENTITY_TYPE = new RegistryQueue<>(Registries.ENTITY_TYPE, 32);
    public static final RegistryQueue<Item> ITEM = new RegistryQueue<>(Registries.ITEM, 512);
    public static final RegistryQueue<Fluid> FLUID = new RegistryQueue<>(Registries.FLUID, 8);
    public static final RegistryQueue<NetworkReconstructor> NETWORK_RECONSTRUCTOR = new RegistryQueue<>(PulseFluxRegistries.NETWORK_RECONSTRUCTOR, 2);
}
