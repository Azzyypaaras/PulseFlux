package works.azzyys.pulseflux.mixin.arrp;

import net.devtech.arrp.json.blockstate.JWhen;
import works.azzyys.pulseflux.util.mixin.EWhen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

@Mixin(JWhen.class)
public class JWhenMixin implements EWhen {

    @Unique
    private final List<String> AND = new ArrayList<>();

    @Unique
    @Override
    public EWhen add(String key, String state) {
        AND.add(key + ":" + state);
        return this;
    }

    @Unique
    @Override
    public List<String> getAND() {
        return AND;
    }
}