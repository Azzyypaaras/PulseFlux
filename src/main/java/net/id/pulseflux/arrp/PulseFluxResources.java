package net.id.pulseflux.arrp;

import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.blockstate.JState;
import net.devtech.arrp.json.lang.JLang;
import net.devtech.arrp.json.models.JModel;
import net.id.pulseflux.PulseFlux;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static net.id.pulseflux.PulseFlux.*;

public class PulseFluxResources {

    public static final RuntimeResourcePack PACK = RuntimeResourcePack.create(MOD_ID + ":resources");
    public static final JLang EN_US = JLang.lang();

    public static void init() {

        PACK.addLang(locate("en_us"), EN_US);

        RRPCallback.BEFORE_VANILLA.register(a -> a.add(PACK));
    }

    public static Identifier splicePath(Identifier id, String insert) {
        return new Identifier(id.getNamespace(), insert + "/" + id.getPath());
    }

    public static Identifier appendPath(Identifier id, String insert) {
        return new Identifier(id.getNamespace(), id.getPath() + insert);
    }

    public static Identifier getBlockId(Block block) {
        return getId(Registry.BLOCK, block);
    }

    public static Identifier getItemId(Item item) {
        return getId(Registry.ITEM, item);
    }

    public static <T> Identifier getId(Registry<T> registry, T object) {
        return registry.getId(object);
    }
}
