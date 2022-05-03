package net.id.pulseflux.util;

import java.util.ArrayList;
import java.util.List;

public class Shorthands {

    @SafeVarargs
    public static <T> List<T> list(T... elements) {
        return new ArrayList<T>(List.of(elements));
    }
}
