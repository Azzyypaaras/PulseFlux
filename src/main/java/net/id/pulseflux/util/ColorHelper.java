package net.id.pulseflux.util;

import net.minecraft.util.math.Vec3f;

import static net.id.incubus_core.render.RenderHelper.*;

public class ColorHelper {

    public static Vec3f hexToVec(int hex) {
        return new Vec3f(rfh(hex) / 255, gfh(hex) / 255, bfh(hex) / 255);
    }
}
