package net.id.pulseflux.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.id.incubus_core.systems.DefaultMaterials;
import net.id.incubus_core.util.RegistryQueue;
import net.id.pulseflux.arrp.TagGen;
import net.id.pulseflux.block.misc.TreetapBlock;
import net.id.pulseflux.block.pulse.BaseDiodeBlock;
import net.id.pulseflux.block.pulse.CreativePulseSourceBlock;
import net.id.pulseflux.block.transport.FluidPipeBlock;
import net.id.pulseflux.block.transport.PipeBlock;
import net.id.pulseflux.registry.PulseFluxRegistryQueues;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;

import static net.id.pulseflux.block.PulseFluxBlockActions.*;
import static net.id.pulseflux.PulseFlux.locate;
import static net.id.pulseflux.arrp.PulseFluxResources.*;

public class PulseFluxBlocks {

    private static RegistryQueue.Action<Block> generateLocale(String name) {
        return ((identifier, block) -> EN_US.block(identifier, name));
    }

    public static FabricBlockSettings treatedWood() {
        return FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).mapColor(MapColor.BROWN).strength(2.5F, 5.0F);
    }

    public static FabricBlockSettings varnishedWood() {
        return FabricBlockSettings.copyOf(Blocks.OAK_PLANKS);
    }


    /**
     * POWER
     */

    public static final BaseDiodeBlock WORKSHOP_DIODE = add("workshop_diode", new BaseDiodeBlock(FabricBlockSettings.copy(Blocks.IRON_BLOCK)), generateDiodeAssets, useWrench, selfDrop, generateLocale("Workshop Diode"));

    public static final CreativePulseSourceBlock CREATIVE_PULSE_SOURCE = add("creative_pulse_source", new CreativePulseSourceBlock(FabricBlockSettings.copy(Blocks.NETHERITE_BLOCK)), generateDiodeAssets, useWrench, tier(TagGen.Tier.NETHERITE), selfDrop, generateLocale("Creative Pulse Source"));


    /**
     * LOGISTICS
     */

    public static final FluidPipeBlock WOODEN_FLUID_PIPE = add("wooden_fluid_pipe", new FluidPipeBlock(FabricBlockSettings.copyOf(Blocks.OAK_LOG), DefaultMaterials.IRON, 81000), translucentRenderLayer, generatePipeAssets, useWrench, selfDrop, generateLocale("Wooden Pipe"));


    /**
     * MISC
     */

    public static final TreetapBlock TREETAP = add("treetap", new TreetapBlock(varnishedWood()), selfDrop, generateLocale("Treetap"), translucentRenderLayer);


    /**
     * DECORATION
     */

    public static final Block TREATED_WOOD_PLANKS = add("treated_wood_planks", new Block(treatedWood()), planks, generateAssets, selfDrop, generateLocale("Treated Wood Planks"));

    public static final Block VARNISHED_WOOD_PLANKS = add("varnished_wood_planks", new Block(varnishedWood()), planks, flammablePlanks, generateAssets, selfDrop, generateLocale("Varnished Wood Planks"));


    /**
     * RESOURCES
     */

    public static final Block HSLA_STEEL_BLOCK = add("hsla_steel_block", new Block(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)), generateAssets, selfDrop, generateLocale("HSLA Steel Block"));



    public static void init() {
        PulseFluxRegistryQueues.BLOCK.register();
    }

    @SafeVarargs
    private static <V extends Block> V add(String id, V block, RegistryQueue.Action<? super V>... additionalActions) {
        return PulseFluxRegistryQueues.BLOCK.add(locate(id), block, additionalActions);
    }
}
