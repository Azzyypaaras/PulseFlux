package net.id.pulseflux.arrp;

import net.devtech.arrp.json.blockstate.JState;
import net.devtech.arrp.json.blockstate.JVariant;
import net.minecraft.util.Identifier;

public class GenShorthands {

    public static JVariant variant(Identifier id) {
        return JState.variant(JState.model(id));
    }
}
