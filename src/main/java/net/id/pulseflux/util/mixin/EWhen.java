package net.id.pulseflux.util.mixin;

import net.devtech.arrp.json.blockstate.JWhen;

import java.util.List;

public interface EWhen {

    EWhen add(String key, String state);

    List<String> getAND();
}
