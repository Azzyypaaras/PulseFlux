package works.azzyys.pulseflux.util;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Box;
import org.joml.Vector3f;

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

    public static Vector3f vecFromPacket(PacketByteBuf buf) {
        return new Vector3f(
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat()
        );
    }

    public static void vecToPacket(Vector3f vector, PacketByteBuf buf) {
        buf.writeFloat(vector.x);
        buf.writeFloat(vector.y);
        buf.writeFloat(vector.z);
    }
}
