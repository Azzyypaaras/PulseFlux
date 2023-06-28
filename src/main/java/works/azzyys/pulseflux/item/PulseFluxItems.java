package works.azzyys.pulseflux.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.id.incubus_core.util.RegistryQueue;
import net.id.incubus_core.util.RegistryQueue.Action;
import works.azzyys.pulseflux.block.PulseFluxBlocks;
import works.azzyys.pulseflux.item.debug.NetworkDebuggerItem;
import works.azzyys.pulseflux.item.tool.WrenchItem;
import works.azzyys.pulseflux.registry.PulseFluxRegistryQueues;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import works.azzyys.pulseflux.PulseFlux;

import static works.azzyys.pulseflux.arrp.AssetGen.*;
import static works.azzyys.pulseflux.arrp.PulseFluxResources.EN_US;

@SuppressWarnings("unused")
public class PulseFluxItems {

    private static Action<Item> generateLocale(String name) {
        return ((identifier, item) -> EN_US.item(identifier, name));
    }

    private static final Action<Item> generateAssets = (id, item) -> createItemModel(id);
    private static final Action<Item> generateHeldAssets = (id, item) -> createHeldItemModel(id);

    private static final Action<Item> generateBlockAssets = (id, item) -> createBlockItemModel(id);


    public static FabricItemSettings simpleItem() {
        return new FabricItemSettings();
    }
    public static FabricItemSettings unstackable() {
        return new FabricItemSettings().maxCount(1);
    }


    /**
     * 0. LOGISTICS
     */

    public static final Action<Item> logistics = PulseFluxItemGroups.LOGISTICS.grouper;

    public static final BlockItem WOODEN_FLUID_PIPE = add("wooden_fluid_pipe", PulseFluxBlocks.WOODEN_FLUID_PIPE, simpleItem(), logistics);


    /**
     * 1. STORAGE
     */

    public static final Action<Item> storage = PulseFluxItemGroups.LOGISTICS.grouper;

    /**
     * 1.1 fluid storage
     */

    public static final BlockItem STONE_BASIN = add("stone_basin", PulseFluxBlocks.STONE_BASIN, simpleItem(), storage, generateBlockAssets);

    public static final BlockItem RESERVOIR = add("reservoir", PulseFluxBlocks.RESERVOIR, simpleItem(), storage);


    /**
     * 2. MACHINES
     */

    public static final Action<Item> machines = PulseFluxItemGroups.MACHINES.grouper;


    public static final BlockItem TREETAP = add("treetap", PulseFluxBlocks.TREETAP, simpleItem(), machines);


    /**
     * 3. DECORATION
     */

    public static final Action<Item> decorations = PulseFluxItemGroups.DECORATION.grouper;

    public static final BlockItem TREATED_WOOD_PLANKS = add("treated_wood_planks", PulseFluxBlocks.TREATED_WOOD_PLANKS, simpleItem(), decorations, generateBlockAssets);

    public static final BlockItem VARNISHED_WOOD_PLANKS = add("varnished_wood_planks", PulseFluxBlocks.VARNISHED_WOOD_PLANKS, simpleItem(), decorations, generateBlockAssets);

    public static final BlockItem TEMPERED_STONE = add("tempered_stone", PulseFluxBlocks.TEMPERED_STONE, simpleItem(), decorations, generateBlockAssets);
    public static final BlockItem TEMPERED_STONE_COLUMN = add("tempered_stone_column", PulseFluxBlocks.TEMPERED_STONE_COLUMN, simpleItem(), decorations, generateBlockAssets);
    public static final BlockItem TEMPERED_STONE_BRICKS = add("tempered_stone_bricks", PulseFluxBlocks.TEMPERED_STONE_BRICKS, simpleItem(), decorations, generateBlockAssets);
    public static final BlockItem TEMPERED_STONE_TILE = add("tempered_stone_tile", PulseFluxBlocks.TEMPERED_STONE_TILE, simpleItem(), decorations, generateBlockAssets);


    /**
     * 4. TOOLS
     */

    public static final Action<Item> tools = PulseFluxItemGroups.TOOLS.grouper;
    public static final Action<Item> opOnly = (id, item) -> PulseFluxItemGroups.TOOLS.add(DeferredItemGroupBuilder.EntryData.of(item, (featureSet, opd) -> opd));

    public static final WrenchItem MANUAL_WRENCH = add("manual_wrench", new WrenchItem(PulseFluxToolMaterials.HSLA_STEEL, 9, -3.1F, unstackable()), tools, generateHeldAssets, generateLocale("Workshop Hammer"));

    public static final NetworkDebuggerItem NETWORK_DEBUGGER_ITEM = add("network_debugging_tool", new NetworkDebuggerItem(unstackable()), opOnly, generateAssets, generateLocale("Transfer Network Debug Tool"));


    /**
     * 5. RESOURCES
     */

    public static final Action<Item> resources = PulseFluxItemGroups.RESOURCES.grouper;

    public static final Item HSLA_STEEL_BLOCK = add("hsla_steel_block", PulseFluxBlocks.HSLA_STEEL_BLOCK, simpleItem(), resources, generateBlockAssets);
    public static final Item HSLA_STEEL_INGOT = add("hsla_steel_ingot", new Item(simpleItem()), resources, generateAssets, generateLocale("HSLA Steel Ingot"));
    public static final Item HSLA_STEEL_NUGGET = add("hsla_steel_nugget", new Item(simpleItem()), resources, generateAssets, generateLocale("HSLA Steel Nugget"));


    public static void init() {
        PulseFluxRegistryQueues.ITEM.register();
    }

    @SafeVarargs
    private static <V extends Item> V add(String id, V item, RegistryQueue.Action<? super V>... additionalActions) {
        return PulseFluxRegistryQueues.ITEM.add(PulseFlux.locate(id), item, additionalActions);
    }

    @SafeVarargs
    private static BlockItem add(String id, Block block, Item.Settings settings, RegistryQueue.Action<? super BlockItem>... additionalActions) {
        return add(id, new BlockItem(block, settings), additionalActions);
    }
}
