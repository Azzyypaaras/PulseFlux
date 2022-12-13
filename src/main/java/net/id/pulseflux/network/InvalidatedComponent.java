package net.id.pulseflux.network;

import net.minecraft.util.math.BlockPos;

public record InvalidatedComponent(Reason reason, BlockPos component) {

    public enum Reason {
        REMOVED,
        WRENCHED,
        DISABLED
    }
}
