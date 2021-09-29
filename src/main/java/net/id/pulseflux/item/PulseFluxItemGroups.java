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

    public static final ItemGroup RESOURCES = FabricItemGroupBuilder.build(
            PulseFlux.locate("resources"),
            (() -> new ItemStack(PulseFluxItems.HSLA_STEEL_INGOT))
    );

    static {
        PulseFluxResources.EN_US.itemGroup(PulseFlux.locate("logistics"), "PF Logistics");
        PulseFluxResources.EN_US.itemGroup(PulseFlux.locate("resources"), "PF Resources");
    }
}
