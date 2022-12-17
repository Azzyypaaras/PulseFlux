package net.id.pulseflux.util;


import org.joml.Vector3f;

import static net.id.incubus_core.render.RenderHelper.*;

public class ColorHelper {

    public static Vector3f hexToNormalizedRGB(int hex) {
        return new Vector3f(rfh(hex) / 255, gfh(hex) / 255, bfh(hex) / 255);
    }
}
