package works.azzyys.pulseflux.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.id.incubus_core.systems.DefaultMaterials;
import net.id.incubus_core.util.RegistryQueue;
import net.minecraft.block.*;
import net.minecraft.sound.BlockSoundGroup;
import works.azzyys.pulseflux.block.fluid_storage.BasinBlock;
import works.azzyys.pulseflux.block.fluid_storage.ReservoirBlock;
import works.azzyys.pulseflux.block.misc.TreetapBlock;
import works.azzyys.pulseflux.block.transport.FluidPipeBlock;
import works.azzyys.pulseflux.registry.PulseFluxRegistryQueues;
import works.azzyys.pulseflux.PulseFlux;
import works.azzyys.pulseflux.arrp.PulseFluxResources;
import works.azzyys.pulseflux.arrp.assets.Devices;

import static works.azzyys.pulseflux.block.PulseFluxBlockActions.*;

public class PulseFluxBlocks {

    private static RegistryQueue.Action<Block> generateLocale(String name) {
        return ((identifier, block) -> PulseFluxResources.EN_US.block(identifier, name));
    }


    /**
     * 0. POWER
     */


    /**
     * 1. LOGISTICS
     */

    public static final FluidPipeBlock WOODEN_FLUID_PIPE = add("wooden_fluid_pipe", new FluidPipeBlock(FabricBlockSettings.copyOf(Blocks.OAK_LOG), DefaultMaterials.IRON, 81000), generatePipeAssets, useWrench, selfDrop, generateLocale("Wooden Pipe"));

    /**
     * 2. STORAGE
     */

    private static final RegistryQueue.Action<Block> generateBasinAssets = ((identifier, basinBlock) -> Devices.createBasinAssets(identifier));

    /**
     * 2.1 fluid storage
     */

    public static final ReservoirBlock RESERVOIR = add("reservoir", new ReservoirBlock(FabricBlockSettings.copyOf(Blocks.ANDESITE)), cutoutRenderLayer, usePickaxe);

    public static final BasinBlock STONE_BASIN = add("stone_basin", new BasinBlock(FabricBlockSettings.copyOf(Blocks.ANDESITE)), cutoutRenderLayer, usePickaxe, selfDrop, generateBasinAssets, generateLocale("Stone Basin"));



    /**
     * 3. MISC
     */

    public static final TreetapBlock TREETAP = add("treetap", new TreetapBlock(varnishedWood()), selfDrop, generateLocale("Treetap"), cutoutRenderLayer, useAxe);


    /**
     * 4. DECORATION
     */


    public static FabricBlockSettings varnishedWood() {
        return FabricBlockSettings.copyOf(Blocks.OAK_PLANKS);
    }


    /**
     * 4.1 wood
     */

    public static final Block TREATED_WOOD_PLANKS = add("treated_wood_planks", new Block(of(Blocks.DARK_OAK_PLANKS, 2.5F, 5.0F)), planks, generateAssets, useAxe, selfDrop, generateLocale("Treated Wood Planks"));

    public static final Block VARNISHED_WOOD_PLANKS = add("varnished_wood_planks", new Block(of(Blocks.SPRUCE_PLANKS)), planks, generateAssets, useAxe, selfDrop, generateLocale("Varnished Wood Planks"));


    /**
     * 4.2 rocks
     */

    public static final PillarBlock TEMPERED_STONE = add("tempered_stone", new PillarBlock(of(Blocks.DEEPSLATE, BlockSoundGroup.BASALT)), generateColumnAssets, usePickaxe, selfDrop, generateLocale("Tempered Stone"));
    public static final PillarBlock TEMPERED_STONE_COLUMN = add("tempered_stone_column", new PillarBlock(of(TEMPERED_STONE)), generateColumnAssets, usePickaxe, selfDrop, generateLocale("Tempered Stone Column"));
    public static final Block TEMPERED_STONE_BRICKS = add("tempered_stone_bricks", new Block(of(TEMPERED_STONE)), generateAssets, usePickaxe, selfDrop, generateLocale("Tempered Stone Bricks"));
    public static final Block TEMPERED_STONE_TILE = add("tempered_stone_tile", new Block(of(TEMPERED_STONE)), generateAssets, usePickaxe, selfDrop, generateLocale("Tempered Stone Tile"));


    /**
     * 5. RESOURCES
     */

    public static final Block HSLA_STEEL_BLOCK = add("hsla_steel_block", new Block(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)), generateAssets, usePickaxe, selfDrop, generateLocale("HSLA Steel Block"));


    public static FabricBlockSettings of(AbstractBlock block) {
        return FabricBlockSettings.copyOf(block);
    }

    public static FabricBlockSettings of(AbstractBlock block, BlockSoundGroup sounds) {
        return FabricBlockSettings.copyOf(block).sounds(sounds);
    }

    public static FabricBlockSettings of(AbstractBlock block, float hardness, float resistance) {
        return FabricBlockSettings.copyOf(block).strength(hardness, resistance);
    }

    public static FabricBlockSettings of(AbstractBlock block, float hardness, float resistance, BlockSoundGroup sounds) {
        return FabricBlockSettings.copyOf(block).strength(hardness, resistance).sounds(sounds);
    }

    public static void init() {
        PulseFluxRegistryQueues.BLOCK.register();
    }

    @SafeVarargs
    private static <V extends Block> V add(String id, V block, RegistryQueue.Action<? super V>... additionalActions) {
        return PulseFluxRegistryQueues.BLOCK.add(PulseFlux.locate(id), block, additionalActions);
    }
}
