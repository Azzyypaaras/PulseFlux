package net.id.pulseflux.mixin.arrp;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.devtech.arrp.json.blockstate.JWhen;
import net.id.pulseflux.util.mixin.EWhen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Type;

@Mixin(JWhen.Serializer.class)
public class EWhenMixin {

    @Inject(method = "serialize(Lnet/devtech/arrp/json/blockstate/JWhen;Ljava/lang/reflect/Type;Lcom/google/gson/JsonSerializationContext;)Lcom/google/gson/JsonElement;", at = @At("HEAD"), remap = false, cancellable = true)
    public void serialize(JWhen src, Type typeOfSrc, JsonSerializationContext context, CallbackInfoReturnable<JsonElement> cir) {
        if (src instanceof EWhen) {
            var when = (EWhen) src;

            var AND = when.getAND();

            if (!AND.isEmpty()) {
                JsonObject json = new JsonObject();
                for (String condition : AND) {
                    var strings = condition.split(":");
                    json.addProperty(strings[0], strings[1]);
                }

                cir.setReturnValue(json);
                cir.cancel();
            }
        }
    }
}
