package works.azzyys.pulseflux.util;

import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;

public record RelativeObjectData<T>(T object, @NotNull Direction direction, int distance) {

    public boolean isValid() {
        return object != null;
    }
}
