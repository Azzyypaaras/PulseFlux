package works.azzyys.pulseflux.util;

import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.List;

public class Shorthands {

    @SafeVarargs
    public static <T> List<T> list(T... elements) {
        return new ArrayList<T>(List.of(elements));
    }

    public static Box pixelBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return new Box(minX / 16, minY / 16, minZ / 16, maxX / 16, maxY / 16, maxZ / 16);
    }

    public static Box pixelBoxInclusive(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return new Box(minX / 16, minY / 16, minZ / 16, maxX / 16 + 0.005, maxY / 16 + 0.005, maxZ / 16 + 0.005);
    }
}
