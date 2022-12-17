package net.id.pulseflux.block;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.id.incubus_core.systems.Lookups;
import net.id.incubus_core.systems.PressureIo;
import net.id.incubus_core.util.RegistryQueue;
import net.id.incubus_core.util.RegistryQueue.Action;
import net.id.pulseflux.block.fluid_storage.BasinBlockEntity;
import net.id.pulseflux.block.fluid_storage.ReservoirBlockEntity;
import net.id.pulseflux.block.misc.TreetapBlockEntity;
import net.id.pulseflux.block.transport.FluidPipeBlockEntity;
import net.id.pulseflux.registry.PulseFluxRegistryQueues;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.Direction;

import java.util.function.BiFunction;

import static net.id.pulseflux.PulseFlux.locate;
import static net.id.pulseflux.block.PulseFluxBlocks.*;

public class PulseFluxBlockEntities {

    private static final Action<BlockEntityType<?>> treetapStorage = ((identifier, blockEntityType) -> FluidStorage.SIDED.registerForBlockEntity(((blockEntity, direction) -> {
            if (direction == Direction.DOWN) {
                return ((TreetapBlockEntity) blockEntity).getTank();
            }
            return null;
        }), blockEntityType));
    //private static final Action<BlockEntityType<?>> pressureProvider = (identifier, blockEntityType) -> Lookups.PRESSURE.registerForBlockEntities((entity, direction) -> (PressureIo) entity);

    private static final Action<BlockEntityType<?>> simpleTank = ((identifier, blockentityType) -> FluidStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> ((ReservoirBlockEntity) blockEntity).getTank(), blockentityType));


    /**
     * 2. STORAGE
     */

    /**
     * 2.1 fluid storage
     */

    public static final BlockEntityType<ReservoirBlockEntity> RESERVOIR_TYPE = add("reservoir", create(ReservoirBlockEntity::new, RESERVOIR), simpleTank);

    public static final BlockEntityType<BasinBlockEntity> STONE_BASIN_TYPE = add("stone_basin", create(BasinBlockEntity::new, STONE_BASIN), BasinBlockEntity.lookup);

    public static final BlockEntityType<FluidPipeBlockEntity> WOODEN_FLUID_PIPE_TYPE = add("wooden_fluid_pipe", create(FluidPipeBlockEntity::new, WOODEN_FLUID_PIPE));



    public static final BlockEntityType<TreetapBlockEntity> TREETAP_TYPE = add("treetap", create(TreetapBlockEntity::new, TREETAP), treetapStorage);
    
    public static void init() {
        PulseFluxRegistryQueues.BLOCK_ENTITY_TYPE.register();
    }

    public static void postInit() {
        fluidStorage(((blockEntity, direction) -> {
            if(blockEntity instanceof FluidPipeBlockEntity pipe) {
                return pipe.tankReference;
            }
            return null;
        }), WOODEN_FLUID_PIPE_TYPE);
    }

    public static <T extends BlockEntity> BlockEntityType<T> create(FabricBlockEntityTypeBuilder.Factory<T> factory, Block... blocks) {
        return FabricBlockEntityTypeBuilder.create(factory, blocks).build();
    }

    @SafeVarargs
    private static <V extends BlockEntityType<?>> V add(String id, V be, RegistryQueue.Action<? super V>... additionalActions) {
        return PulseFluxRegistryQueues.BLOCK_ENTITY_TYPE.add(locate(id + "_block_entity_type"), be, additionalActions);
    }

    private static void fluidStorage(BiFunction<? super BlockEntity, Direction, Storage<FluidVariant>> provider, BlockEntityType<?> type) {
        FluidStorage.SIDED.registerForBlockEntity(provider, type);
    }
}
