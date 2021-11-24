package net.id.pulseflux.block.property;

import com.google.common.collect.ImmutableMap;
import net.id.incubus_core.systems.PulseIo;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.Direction;

public class DirectionalIoProperty {
    public static final ImmutableMap<Direction, EnumProperty<PulseIo.IoType>> IO_PROPERTIES = new ImmutableMap.Builder<Direction, EnumProperty<PulseIo.IoType>>()
            .put(Direction.NORTH, EnumProperty.of("north", PulseIo.IoType.class))
            .put(Direction.EAST, EnumProperty.of("east", PulseIo.IoType.class))
            .put(Direction.SOUTH, EnumProperty.of("south", PulseIo.IoType.class))
            .put(Direction.WEST, EnumProperty.of("west", PulseIo.IoType.class))
            .put(Direction.UP, EnumProperty.of("up", PulseIo.IoType.class))
            .put(Direction.DOWN, EnumProperty.of("down", PulseIo.IoType.class))
            .build();
}
