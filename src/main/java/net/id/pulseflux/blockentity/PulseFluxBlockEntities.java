package net.id.pulseflux.blockentity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.id.incubus_core.systems.DefaultMaterials;
import net.id.pulseflux.blockentity.transport.FluidPipeEntity;
import net.id.pulseflux.systems.Lookups;
import net.id.pulseflux.systems.PulseIo;
import net.id.incubus_core.util.RegistryQueue;
import net.id.pulseflux.registry.PulseFluxRegistryQueues;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;

import static net.id.pulseflux.PulseFlux.locate;
import static net.id.pulseflux.block.PulseFluxBlocks.*;
import static net.id.incubus_core.util.RegistryQueue.*;

public class PulseFluxBlockEntities {

    private static final Action<BlockEntityType<?>> pulseProvider = ((identifier, blockEntityType) -> Lookups.PULSE.registerForBlockEntity(((blockEntity, direction) -> (PulseIo) blockEntity), blockEntityType));

    public static final BlockEntityType<BaseDiodeEntity> WORKSHOP_DIODE_ENTITY_TYPE = add("workshop_diode", create(BaseDiodeEntity.factory(DefaultMaterials.IRON), WORKSHOP_DIODE), pulseProvider);
    public static final BlockEntityType<CreativePulseSourceEntity> CREATIVE_PULSE_SOURCE_ENTITY_BLOCK_ENTITY_TYPE = add("creative_pulse_source", create(CreativePulseSourceEntity::new, CREATIVE_PULSE_SOURCE), pulseProvider);
    public static final BlockEntityType<FluidPipeEntity> FLUID_PIPE_BLOCK_ENTITY_TYPE = add("fluid_pipe", create(FluidPipeEntity::new, WOODEN_FLUID_PIPE));

    public static void init() {
        PulseFluxRegistryQueues.BLOCK_ENTITY_TYPE.register();
    }

    public static <T extends BlockEntity> BlockEntityType<T> create(FabricBlockEntityTypeBuilder.Factory<T> factory, Block... blocks) {
        return FabricBlockEntityTypeBuilder.create(factory, blocks).build();
    }

    @SafeVarargs
    private static <V extends BlockEntityType<?>> V add(String id, V be, RegistryQueue.Action<? super V>... additionalActions) {
        return PulseFluxRegistryQueues.BLOCK_ENTITY_TYPE.add(locate(id + "_block_entity_type"), be, additionalActions);
    }
}
