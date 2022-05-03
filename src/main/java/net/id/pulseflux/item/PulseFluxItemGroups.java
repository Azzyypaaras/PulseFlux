package net.id.pulseflux.item;


import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.id.pulseflux.PulseFlux;
import net.id.pulseflux.block.PulseFluxBlocks;
import net.id.pulseflux.arrp.PulseFluxResources;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class PulseFluxItemGroups {

    public static final ItemGroup LOGISTICS = FabricItemGroupBuilder.build(
            PulseFlux.locate("logistics"),
            (() -> new ItemStack(PulseFluxBlocks.WORKSHOP_DIODE))
    );

    public static final ItemGroup MACHINES = FabricItemGroupBuilder.build(
            PulseFlux.locate("machines"),
            (() -> new ItemStack(PulseFluxBlocks.WORKSHOP_DIODE))
    );

    public static final ItemGroup TOOLS = FabricItemGroupBuilder.build(
            PulseFlux.locate("tools"),
            (() -> new ItemStack(PulseFluxItems.NETWORK_DEBUGGER_ITEM))
    );


    public static final ItemGroup RESOURCES = FabricItemGroupBuilder.build(
            PulseFlux.locate("resources"),
            (() -> new ItemStack(PulseFluxItems.HSLA_STEEL_INGOT))
    );

    public static final ItemGroup DECORATION = FabricItemGroupBuilder.build(
            PulseFlux.locate("decoration"),
            (() -> new ItemStack(PulseFluxItems.TREATED_WOOD_PLANKS))
    );

    static {
        PulseFluxResources.EN_US.itemGroup(PulseFlux.locate("logistics"), "§6PF Logistics");
        PulseFluxResources.EN_US.itemGroup(PulseFlux.locate("machines"), "§6PF Machines");
        PulseFluxResources.EN_US.itemGroup(PulseFlux.locate("tools"), "§6PF Tools");
        PulseFluxResources.EN_US.itemGroup(PulseFlux.locate("resources"), "§6PF Resources");
        PulseFluxResources.EN_US.itemGroup(PulseFlux.locate("decoration"), "§6PF Decorations");
    }
}
