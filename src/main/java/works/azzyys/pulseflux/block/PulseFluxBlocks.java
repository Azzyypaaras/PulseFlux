package works.azzyys.pulseflux.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.id.incubus_core.systems.DefaultMaterials;
import net.id.incubus_core.util.RegistryQueue;
import works.azzyys.pulseflux.block.fluid_storage.BasinBlock;
import works.azzyys.pulseflux.block.fluid_storage.ReservoirBlock;
import works.azzyys.pulseflux.block.misc.TreetapBlock;
import works.azzyys.pulseflux.block.transport.FluidPipeBlock;
import works.azzyys.pulseflux.registry.PulseFluxRegistryQueues;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import works.azzyys.pulseflux.PulseFlux;
import works.azzyys.pulseflux.arrp.PulseFluxResources;
import works.azzyys.pulseflux.arrp.assets.Devices;

import static works.azzyys.pulseflux.block.PulseFluxBlockActions.*;

public class PulseFluxBlocks {

    private static RegistryQueue.Action<Block> generateLocale(String name) {
        return ((identifier, block) -> PulseFluxResources.EN_US.block(identifier, name));
    }

    public static FabricBlockSettings treatedWood() {
        return FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).mapColor(MapColor.BROWN).strength(2.5F, 5.0F);
    }

    public static FabricBlockSettings varnishedWood() {
        return FabricBlockSettings.copyOf(Blocks.OAK_PLANKS);
    }


    /**
     * 0. POWER
     */


    /**
     * 1. LOGISTICS
     */

    public static final FluidPipeBlock WOODEN_FLUID_PIPE = add("wooden_fluid_pipe", new FluidPipeBlock(FabricBlockSettings.copyOf(Blocks.OAK_LOG), DefaultMaterials.IRON, 81000), translucentRenderLayer, generatePipeAssets, useWrench, selfDrop, generateLocale("Wooden Pipe"));

    /**
     * 2. STORAGE
     */

    private static final RegistryQueue.Action<Block> generateBasinAssets = ((identifier, basinBlock) -> Devices.createBasinAssets(identifier));

    /**
     * 2.1 fluid storage
     */

    public static final ReservoirBlock RESERVOIR = add("reservoir", new ReservoirBlock(FabricBlockSettings.copyOf(Blocks.ANDESITE)), cutoutRenderLayer);

    public static final BasinBlock STONE_BASIN = add("stone_basin", new BasinBlock(FabricBlockSettings.copyOf(Blocks.ANDESITE)), cutoutRenderLayer, selfDrop, generateBasinAssets, generateLocale("Stone Basin"));



    /**
     * 3. MISC
     */

    public static final TreetapBlock TREETAP = add("treetap", new TreetapBlock(varnishedWood()), selfDrop, generateLocale("Treetap"), translucentRenderLayer);


    /**
     * 4. DECORATION
     */

    public static final Block TREATED_WOOD_PLANKS = add("treated_wood_planks", new Block(treatedWood()), planks, generateAssets, selfDrop, generateLocale("Treated Wood Planks"));

    public static final Block VARNISHED_WOOD_PLANKS = add("varnished_wood_planks", new Block(varnishedWood()), planks, flammablePlanks, generateAssets, selfDrop, generateLocale("Varnished Wood Planks"));


    /**
     * 5. RESOURCES
     */

    public static final Block HSLA_STEEL_BLOCK = add("hsla_steel_block", new Block(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)), generateAssets, selfDrop, generateLocale("HSLA Steel Block"));



    public static void init() {
        PulseFluxRegistryQueues.BLOCK.register();
    }

    @SafeVarargs
    private static <V extends Block> V add(String id, V block, RegistryQueue.Action<? super V>... additionalActions) {
        return PulseFluxRegistryQueues.BLOCK.add(PulseFlux.locate(id), block, additionalActions);
    }
}
