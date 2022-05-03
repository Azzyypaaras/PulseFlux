package net.id.pulseflux.registry;

import net.id.incubus_core.util.RegistryQueue;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

import static net.id.pulseflux.network.TransferNetwork.NetworkReconstructor;

public class PulseFluxRegistryQueues {
    public static final RegistryQueue<Block> BLOCK = new RegistryQueue<>(Registry.BLOCK, 256);
    public static final RegistryQueue<BlockEntityType<?>> BLOCK_ENTITY_TYPE = new RegistryQueue<>(Registry.BLOCK_ENTITY_TYPE, 256);
    public static final RegistryQueue<EntityType<?>> ENTITY_TYPE = new RegistryQueue<>(Registry.ENTITY_TYPE, 32);
    public static final RegistryQueue<Item> ITEM = new RegistryQueue<>(Registry.ITEM, 512);
    public static final RegistryQueue<Fluid> FLUID = new RegistryQueue<>(Registry.FLUID, 8);
    public static final RegistryQueue<NetworkReconstructor<?>> NETWORK_RECONSTRUCTOR = new RegistryQueue<>(PulseFluxRegistries.NETWORK_RECONSTRUCTOR, 2);
}
