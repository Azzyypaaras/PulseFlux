package net.id.pulseflux.blockentity;

import net.id.incubus_core.util.RegistryQueue;
import net.id.pulseflux.registry.PulseFluxRegistryQueues;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;

import static net.id.pulseflux.PulseFlux.locate;

public class PulseFluxBlockEntities {

    public static void init() {
        PulseFluxRegistryQueues.BLOCK_ENTITY_TYPE.register();
    }

    @SafeVarargs
    private static <V extends BlockEntityType<?>> V add(String id, V block, RegistryQueue.Action<? super V>... additionalActions) {
        return PulseFluxRegistryQueues.BLOCK_ENTITY_TYPE.add(locate(id), block, additionalActions);
    }
}
