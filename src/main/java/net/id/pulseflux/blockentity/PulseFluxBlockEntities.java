package net.id.pulseflux.blockentity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.id.incubus_core.systems.DefaultMaterials;
import net.id.pulseflux.blockentity.misc.TreetapEntity;
import net.id.pulseflux.blockentity.pulse.BaseDiodeEntity;
import net.id.pulseflux.blockentity.pulse.CreativePulseSourceEntity;
import net.id.pulseflux.systems.Lookups;
import net.id.pulseflux.systems.PulseIo;
import net.id.incubus_core.util.RegistryQueue;
import net.id.pulseflux.registry.PulseFluxRegistryQueues;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.Direction;

import static net.id.pulseflux.PulseFlux.locate;
import static net.id.pulseflux.block.PulseFluxBlocks.*;
import static net.id.incubus_core.util.RegistryQueue.*;

public class PulseFluxBlockEntities {

    private static final Action<BlockEntityType<?>> pulseProvider = ((identifier, blockEntityType) -> Lookups.PULSE.registerForBlockEntity(((blockEntity, direction) -> (PulseIo) blockEntity), blockEntityType));
    private static final Action<BlockEntityType<?>> treetapStorage = ((identifier, blockEntityType) -> FluidStorage.SIDED.registerForBlockEntity(((blockEntity, direction) -> {
            if (direction == Direction.DOWN) {
                return ((TreetapEntity) blockEntity).getTank();
            }
            return null;
        }), blockEntityType));

    public static final BlockEntityType<BaseDiodeEntity> WORKSHOP_DIODE_TYPE = add("workshop_diode", create(BaseDiodeEntity.factory(DefaultMaterials.IRON), WORKSHOP_DIODE), pulseProvider);
    public static final BlockEntityType<CreativePulseSourceEntity> CREATIVE_PULSE_SOURCE_TYPE = add("creative_pulse_source", create(CreativePulseSourceEntity::new, CREATIVE_PULSE_SOURCE), pulseProvider);
    
    public static final BlockEntityType<TreetapEntity> TREETAP_TYPE = add("treetap", create(TreetapEntity::new, TREETAP), treetapStorage);
    
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
