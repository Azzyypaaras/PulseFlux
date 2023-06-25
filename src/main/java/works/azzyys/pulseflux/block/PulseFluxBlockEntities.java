package works.azzyys.pulseflux.block;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.id.incubus_core.util.RegistryQueue;
import net.id.incubus_core.util.RegistryQueue.Action;
import works.azzyys.pulseflux.block.fluid_storage.BasinBlockEntity;
import works.azzyys.pulseflux.block.fluid_storage.ReservoirBlockEntity;
import works.azzyys.pulseflux.block.misc.TreetapBlockEntity;
import works.azzyys.pulseflux.block.transport.FluidPipeBlockEntity;
import works.azzyys.pulseflux.registry.PulseFluxRegistryQueues;
import works.azzyys.pulseflux.systems.PFLookups;
import works.azzyys.pulseflux.systems.PressureHolder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.Direction;
import works.azzyys.pulseflux.PulseFlux;

import java.util.function.BiFunction;

public class PulseFluxBlockEntities {

    private static final Action<BlockEntityType<?>> treetapStorage = ((identifier, blockEntityType) -> FluidStorage.SIDED.registerForBlockEntity(((blockEntity, direction) -> {
            if (direction == Direction.DOWN) {
                return ((TreetapBlockEntity) blockEntity).getTank();
            }
            return null;
        }), blockEntityType));
    private static final Action<BlockEntityType<?>> pressureProvider = (identifier, blockEntityType) -> PFLookups.PRESSURE.registerForBlockEntity((entity, direction) -> (PressureHolder) entity, blockEntityType);

    private static final Action<BlockEntityType<?>> simpleTank = ((identifier, blockentityType) -> FluidStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> ((ReservoirBlockEntity) blockEntity).getTank(), blockentityType));


    /**
     * 2. STORAGE
     */

    /**
     * 2.1 fluid storage
     */

    public static final BlockEntityType<ReservoirBlockEntity> RESERVOIR_TYPE = add("reservoir", create(ReservoirBlockEntity::new, PulseFluxBlocks.RESERVOIR), simpleTank);

    public static final BlockEntityType<BasinBlockEntity> STONE_BASIN_TYPE = add("stone_basin", create(BasinBlockEntity::new, PulseFluxBlocks.STONE_BASIN), BasinBlockEntity.lookup, pressureProvider);

    public static final BlockEntityType<FluidPipeBlockEntity> WOODEN_FLUID_PIPE_TYPE = add("wooden_fluid_pipe", create(FluidPipeBlockEntity::new, PulseFluxBlocks.WOODEN_FLUID_PIPE));



    public static final BlockEntityType<TreetapBlockEntity> TREETAP_TYPE = add("treetap", create(TreetapBlockEntity::new, PulseFluxBlocks.TREETAP), treetapStorage);
    
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
        return PulseFluxRegistryQueues.BLOCK_ENTITY_TYPE.add(PulseFlux.locate(id + "_block_entity_type"), be, additionalActions);
    }

    private static void fluidStorage(BiFunction<? super BlockEntity, Direction, Storage<FluidVariant>> provider, BlockEntityType<?> type) {
        FluidStorage.SIDED.registerForBlockEntity(provider, type);
    }
}
