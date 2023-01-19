package net.id.pulseflux.systems;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.id.pulseflux.PulseFlux;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PFLookups {

    public static final BlockApiLookup<ThermalBody, @NotNull Direction> THERMAL =
            BlockApiLookup.get(PulseFlux.locate("thermal_sided"), ThermalBody.class, Direction.class);

    public static final ItemApiLookup<ThermalBody, @Nullable ContainerItemContext> THERMAL_ITEM =
            ItemApiLookup.get(PulseFlux.locate("thermal_item"), ThermalBody.class, ContainerItemContext.class);

    public static final BlockApiLookup<FluxHolder, @NotNull Direction> FM =
            BlockApiLookup.get(PulseFlux.locate("flux_motive_sided"), FluxHolder.class, Direction.class);

    public static final ItemApiLookup<FluxHolder, @Nullable ContainerItemContext> FM_ITEM =
            ItemApiLookup.get(PulseFlux.locate("flux_motive_item"), FluxHolder.class, ContainerItemContext.class);

    public static final BlockApiLookup<PressureHolder, @NotNull Direction> PRESSURE =
            BlockApiLookup.get(PulseFlux.locate("pressure_sided"), PressureHolder.class, Direction.class);

    public static final ItemApiLookup<PressureHolder, @Nullable ContainerItemContext> PRESSURE_ITEM =
            ItemApiLookup.get(PulseFlux.locate("pressure_item"), PressureHolder.class, ContainerItemContext.class);
}
