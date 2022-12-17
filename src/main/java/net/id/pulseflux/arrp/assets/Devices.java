package net.id.pulseflux.arrp.assets;

import net.devtech.arrp.json.blockstate.JState;
import net.devtech.arrp.json.models.JModel;
import net.minecraft.util.Identifier;

import static net.id.pulseflux.arrp.GenShorthands.variant;
import static net.id.pulseflux.arrp.PulseFluxResources.*;

public class Devices {

    public static void createBasinAssets(Identifier basin) {
        var id = splicePath(basin, "block");
        PACK.addModel(JModel.model("pulseflux:base/device/basin").textures(JModel.textures()
                .var("side", appendPath(id, "_side").toString())
                .var("bottom", appendPath(id, "_bottom").toString())
                .var("inside", appendPath(id, "_inside").toString())
                .var("top", appendPath(id, "_top").toString()))
                , id);
        PACK.addBlockState(JState.state(variant(id)), basin);
    }
}
