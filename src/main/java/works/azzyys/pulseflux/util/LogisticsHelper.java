package works.azzyys.pulseflux.util;

import com.google.common.collect.ImmutableList;
import works.azzyys.pulseflux.systems.PulseIo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import works.azzyys.pulseflux.systems.PulseLookups;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class LogisticsHelper {

    public static Optional<RelativeObjectData<PulseIo>> seekPulseIo(PulseIo.IoType type, World world, BlockPos start) {
        return seekPulseIo(type, world, start, Arrays.asList(Direction.values()));
    }

    public static Optional<RelativeObjectData<PulseIo>> seekPulseIo(PulseIo.IoType type, World world, BlockPos start, Direction direction) {
        return seekPulseIo(type, world, start, ImmutableList.of(direction));
    }

    public static Optional<RelativeObjectData<PulseIo>> seekPulseIo(PulseIo.IoType type, World world, BlockPos start, List<Direction> searchDirs) {
        return seekPulseIo(type, world, start, searchDirs, 9);
    }

    public static Optional<RelativeObjectData<PulseIo>> seekPulseIo(PulseIo.IoType type, World world, BlockPos start, List<Direction> searchDirs, int range) {
        return searchDirs.stream().map(direction -> {
            PulseIo io;

            var pos = start;
            for (int i = 1; i <= range; i++) {

                pos = pos.offset(direction);
                io = PulseLookups.PULSE.find(world, pos, direction.getOpposite());

                if(io != null && io.getIoCapabilities(direction.getOpposite()) == type) {
                    return new RelativeObjectData<>(io, direction, i);
                }

                var state = world.getBlockState(pos);

                if(state.getMaterial().blocksLight() && (state.isSideSolidFullSquare(world, pos, direction) || state.isSideSolidFullSquare(world, pos, direction.getOpposite()))) {
                    return null;
                }

            }

            return null;
        }).filter(Objects::nonNull).findFirst();
    }

}
