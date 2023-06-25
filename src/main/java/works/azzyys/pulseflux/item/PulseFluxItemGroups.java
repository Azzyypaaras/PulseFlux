package works.azzyys.pulseflux.item;

import works.azzyys.pulseflux.PulseFlux;
import works.azzyys.pulseflux.arrp.PulseFluxResources;

public class PulseFluxItemGroups {

    public static final DeferredItemGroupBuilder LOGISTICS =
            DeferredItemGroupBuilder.of(PulseFlux.locate("logistics"));

    public static final DeferredItemGroupBuilder MACHINES =
            DeferredItemGroupBuilder.of(PulseFlux.locate("machines"));


    public static final DeferredItemGroupBuilder TOOLS =
            DeferredItemGroupBuilder.of(PulseFlux.locate("tools"));


    public static final DeferredItemGroupBuilder RESOURCES =
            DeferredItemGroupBuilder.of(PulseFlux.locate("resources"));


    public static final DeferredItemGroupBuilder DECORATION =
            DeferredItemGroupBuilder.of(PulseFlux.locate("decoration"));

    public static void build() {
        LOGISTICS.build(PulseFluxItems.WOODEN_FLUID_PIPE.getDefaultStack());
        MACHINES.build(PulseFluxItems.STONE_BASIN.getDefaultStack());
        TOOLS.build(PulseFluxItems.MANUAL_WRENCH.getDefaultStack());
        RESOURCES.build(PulseFluxItems.HSLA_STEEL_INGOT.getDefaultStack());
        DECORATION.build(PulseFluxItems.TREATED_WOOD_PLANKS.getDefaultStack());
    }

    static {
        PulseFluxResources.EN_US.itemGroup(PulseFlux.locate("logistics"), "§6PF Logistics");
        PulseFluxResources.EN_US.itemGroup(PulseFlux.locate("machines"), "§6PF Machines");
        PulseFluxResources.EN_US.itemGroup(PulseFlux.locate("tools"), "§6PF Tools");
        PulseFluxResources.EN_US.itemGroup(PulseFlux.locate("resources"), "§6PF Resources");
        PulseFluxResources.EN_US.itemGroup(PulseFlux.locate("decoration"), "§6PF Decorations");
    }
}
