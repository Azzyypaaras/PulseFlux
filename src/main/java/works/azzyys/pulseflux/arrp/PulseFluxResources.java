package works.azzyys.pulseflux.arrp;

import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.lang.JLang;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import works.azzyys.pulseflux.PulseFlux;

public class PulseFluxResources {

    public static final RuntimeResourcePack PACK = RuntimeResourcePack.create(PulseFlux.MOD_ID + ":resources");
    public static final JLang EN_US = JLang.lang();

    public static void init() {

        TagGen.addTags();

        PACK.addLang(PulseFlux.locate("en_us"), EN_US);

        RRPCallback.BEFORE_VANILLA.register(a -> a.add(PACK));

        //PACK.dump();
    }

    public static Identifier splicePath(Identifier id, String insert) {
        return new Identifier(id.getNamespace(), insert + "/" + id.getPath());
    }

    public static Identifier appendPath(Identifier id, String insert) {
        return new Identifier(id.getNamespace(), id.getPath() + insert);
    }

    public static Identifier vanilla(String path) {
        return new Identifier("minecraft", path);
    }

    public static Identifier fabric(String path) {
        return new Identifier("fabric", path);
    }

    public static Identifier common(String path) {
        return new Identifier("c", path);
    }

    public static Identifier getBlockId(Block block) {
        return getId(Registries.BLOCK, block);
    }

    public static Identifier getItemId(Item item) {
        return getId(Registries.ITEM, item);
    }

    public static <T> Identifier getId(Registry<T> registry, T object) {
        return registry.getId(object);
    }
}
