package net.id.pulseflux.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.id.incubus_core.util.RegistryQueue;
import net.id.pulseflux.PulseFlux;
import net.id.pulseflux.arrp.DataGen;
import net.id.pulseflux.block.PulseFluxBlocks;
import net.id.pulseflux.item.debug.NetworkDebuggerItem;
import net.id.pulseflux.item.tool.WrenchItem;
import net.id.pulseflux.registry.PulseFluxRegistryQueues;
import net.id.incubus_core.util.RegistryQueue.Action;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static net.id.pulseflux.arrp.PulseFluxResources.*;
import static net.id.pulseflux.arrp.AssetGen.*;

import static net.id.pulseflux.PulseFlux.locate;

public class PulseFluxItems {

    private static Action<Item> generateLocale(String name) {
        return ((identifier, item) -> EN_US.item(identifier, name));
    }

    private static final Action<Item> generateAssets = (id, item) -> createItemModel(id);
    private static final Action<Item> generateHeldAssets = (id, item) -> createHeldItemModel(id);

    private static final Action<Item> generateBlockAssets = (id, item) -> createBlockItemModel(id);


    public static final FabricItemSettings resource = new FabricItemSettings().group(PulseFluxItemGroups.RESOURCES);
    public static final FabricItemSettings logistics = new FabricItemSettings().group(PulseFluxItemGroups.LOGISTICS);
    public static final FabricItemSettings machines = new FabricItemSettings().group(PulseFluxItemGroups.MACHINES);
    public static final FabricItemSettings tools = new FabricItemSettings().group(PulseFluxItemGroups.TOOLS).maxCount(1);
    public static final FabricItemSettings decorations = new FabricItemSettings().group(PulseFluxItemGroups.DECORATION);


    /**
     * LOGISTICS
     */

    public static final BlockItem WORKSHOP_DIODE = add("workshop_diode", PulseFluxBlocks.WORKSHOP_DIODE, logistics);

    public static final BlockItem CREATIVE_PULSE_SOURCE = add("creative_pulse_source", PulseFluxBlocks.CREATIVE_PULSE_SOURCE, logistics);

    public static final BlockItem WOODEN_FLUID_PIPE = add("wooden_fluid_pipe", PulseFluxBlocks.WOODEN_FLUID_PIPE, logistics);


    /**
     * STORAGE
     */

    public static final BlockItem RESERVOIR = add("reservoir", PulseFluxBlocks.RESERVOIR, logistics);


    /**
     * MACHINES
     */

    public static final BlockItem TREETAP = add("treetap", PulseFluxBlocks.TREETAP, machines);


    /**
     * DECORATION
     */

    public static final BlockItem TREATED_WOOD_PLANKS = add("treated_wood_planks", PulseFluxBlocks.TREATED_WOOD_PLANKS, decorations, generateBlockAssets);

    public static final BlockItem VARNISHED_WOOD_PLANKS = add("varnished_wood_planks", PulseFluxBlocks.VARNISHED_WOOD_PLANKS, decorations, generateBlockAssets);


    /**
     * TOOLS
     */

    public static final WrenchItem MANUAL_WRENCH = add("manual_wrench", new WrenchItem(PulseFluxToolMaterials.HSLA_STEEL, 9, -3.1F, tools), generateHeldAssets, generateLocale("Workshop Hammer"));

    public static final NetworkDebuggerItem NETWORK_DEBUGGER_ITEM = add("network_debugging_tool", new NetworkDebuggerItem(tools), generateAssets, generateLocale("Transfer Network Debug Tool"));


    /**
     * RESOURCES
     */

    public static final Item HSLA_STEEL_BLOCK = add("hsla_steel_block", PulseFluxBlocks.HSLA_STEEL_BLOCK, resource, generateBlockAssets);
    public static final Item HSLA_STEEL_INGOT = add("hsla_steel_ingot", new Item(resource), generateAssets, generateLocale("HSLA Steel Ingot"));
    public static final Item HSLA_STEEL_NUGGET = add("hsla_steel_nugget", new Item(resource), generateAssets, generateLocale("HSLA Steel Nugget"));



    public static void init() {
        PulseFluxRegistryQueues.ITEM.register();
    }

    @SafeVarargs
    private static <V extends Item> V add(String id, V item, RegistryQueue.Action<? super V>... additionalActions) {
        return PulseFluxRegistryQueues.ITEM.add(locate(id), item, additionalActions);
    }

    @SafeVarargs
    private static BlockItem add(String id, Block block, Item.Settings settings, RegistryQueue.Action<? super BlockItem>... additionalActions) {
        return add(id, new BlockItem(block, settings), additionalActions);
    }
}
