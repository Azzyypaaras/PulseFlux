package works.azzyys.pulseflux.util.mixin;

import java.util.List;

public interface EWhen {

    EWhen add(String key, String state);

    List<String> getAND();
}
