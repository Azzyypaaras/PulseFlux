package net.id.pulseflux.arrp;

import net.devtech.arrp.json.blockstate.JState;
import net.devtech.arrp.json.models.JModel;
import net.minecraft.util.Identifier;

import static net.id.pulseflux.arrp.PulseFluxResources.*;

public class AssetGen {
    public static void createItemModel(Identifier item) {
        var id = splicePath(item, "item");
        PACK.addModel(JModel.model("minecraft:item/generated").textures(JModel.textures().layer0(id.toString())), id);
    }

    public static void createBlockItemModel(Identifier blockItem) {
        var parentId = splicePath(blockItem, "block");
        var id = splicePath(blockItem, "item");
        PACK.addModel(JModel.model(parentId), id);
    }

    public static void createBlockAssets(Identifier block) {
        var id = splicePath(block, "block");
        PACK.addModel(JModel.model("minecraft:block/cube_all").textures(JModel.textures().var("all", id.toString())), id);
        PACK.addBlockState(JState.state(JState.variant(JState.model(id))), block);
    }

    public static void createDiodeAssets(Identifier block) {
        JState.multipart();
    }
}
