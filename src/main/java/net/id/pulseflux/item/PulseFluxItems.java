package net.id.pulseflux.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.id.incubus_core.util.RegistryQueue;
import net.id.pulseflux.PulseFlux;
import net.id.pulseflux.arrp.DataGen;
import net.id.pulseflux.block.PulseFluxBlocks;
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
    private static final Action<Item> generateBlockAssets = (id, item) -> createBlockItemModel(id);


    public static final FabricItemSettings resource = new FabricItemSettings().group(PulseFluxItemGroups.RESOURCES);
    public static final FabricItemSettings logistics = new FabricItemSettings().group(PulseFluxItemGroups.LOGISTICS);

    /**
     * LOGISTICS
     */

    public static final BlockItem WORKSHOP_DIODE = add("workshop_diode", PulseFluxBlocks.WORKSHOP_DIODE, logistics);

    public static final BlockItem CREATIVE_PULSE_SOURCE = add("creative_pulse_source", PulseFluxBlocks.CREATIVE_PULSE_SOURCE, logistics);



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
