package works.azzyys.pulseflux.arrp;

import net.devtech.arrp.json.blockstate.JState;
import net.devtech.arrp.json.blockstate.JWhen;
import net.devtech.arrp.json.models.JModel;
import works.azzyys.pulseflux.util.mixin.EWhen;
import net.minecraft.util.Identifier;

import static net.devtech.arrp.json.blockstate.JState.when;

public class AssetGen {

    public static void createItemModel(Identifier item) {
        var id = PulseFluxResources.splicePath(item, "item");
        PulseFluxResources.PACK.addModel(JModel.model("minecraft:item/generated").textures(JModel.textures().layer0(id.toString())), id);
    }

    public static void createHeldItemModel(Identifier item) {
        var id = PulseFluxResources.splicePath(item, "item");
        PulseFluxResources.PACK.addModel(JModel.model("minecraft:item/handheld").textures(JModel.textures().layer0(id.toString())), id);
    }

    public static void createBlockItemModel(Identifier blockItem) {
        var parentId = PulseFluxResources.splicePath(blockItem, "block");
        var id = PulseFluxResources.splicePath(blockItem, "item");
        PulseFluxResources.PACK.addModel(JModel.model(parentId), id);
    }

    public static void createBlockAssets(Identifier block) {
        var id = PulseFluxResources.splicePath(block, "block");
        PulseFluxResources.PACK.addModel(JModel.model("minecraft:block/cube_all").textures(JModel.textures().var("all", id.toString())), id);
        PulseFluxResources.PACK.addBlockState(JState.state(JState.variant(JState.model(id))), block);
    }

    public static void createColumnAssets(Identifier block) {
        var id = PulseFluxResources.splicePath(block, "block");
        PulseFluxResources.PACK.addModel(JModel.model("minecraft:block/cube_column")
                .textures(JModel.textures().var("side", id + "_side").var("end", id + "_top")), id);
        PulseFluxResources.PACK.addBlockState(JState.state(
                JState.variant()
                        .put("axis", "x", JState.model(id).x(90).y(90))
                        .put("axis", "y", JState.model(id))
                        .put("axis", "z", JState.model(id).x(90))
        ), block);
    }

    public static void createDiodeAssets(Identifier block) {
        var id = PulseFluxResources.splicePath(block, "block");
        var frameId = PulseFluxResources.appendPath(id, "_frame");
        var panelId = PulseFluxResources.appendPath(id, "_panel");
        var inputId = PulseFluxResources.appendPath(id, "_input");
        var outputId = PulseFluxResources.appendPath(id, "_output");
        PulseFluxResources.PACK.addModel(JModel.model("pulseflux:base/base_diode_frame").textures(JModel.textures().var("frame", id.toString())), frameId);
        PulseFluxResources.PACK.addModel(JModel.model("pulseflux:base/base_diode_panel").textures(JModel.textures().var("panel", id.toString())), panelId);
        PulseFluxResources.PACK.addModel(JModel.model("pulseflux:base/base_diode_input").textures(JModel.textures().var("input", id.toString())), inputId);
        PulseFluxResources.PACK.addModel(JModel.model("pulseflux:base/base_diode_output").textures(JModel.textures().var("output", id.toString())), outputId);
        PulseFluxResources.PACK.addModel(JModel.model("pulseflux:item/base_diode_item").textures(JModel.textures().var("all", id.toString())), PulseFluxResources.splicePath(block, "item"));
        PulseFluxResources.PACK.addBlockState(JState.state(
                //Frame
                JState.multipart(JState.model(frameId)),
                //Plain panels
                JState.multipart(JState.model(panelId)).when(when().add("north", "none")),
                JState.multipart(JState.model(panelId).y(90)).when(when().add("east", "none")),
                JState.multipart(JState.model(panelId).y(180)).when(when().add("south", "none")),
                JState.multipart(JState.model(panelId).y(270)).when(when().add("west", "none")),
                JState.multipart(JState.model(panelId).x(270)).when(when().add("up", "none")),
                JState.multipart(JState.model(panelId).x(90)).when(when().add("down", "none")),
                //Input panels
                JState.multipart(JState.model(inputId)).when(when().add("north", "input")),
                JState.multipart(JState.model(inputId).y(90)).when(when().add("east", "input")),
                JState.multipart(JState.model(inputId).y(180)).when(when().add("south", "input")),
                JState.multipart(JState.model(inputId).y(270)).when(when().add("west", "input")),
                JState.multipart(JState.model(inputId).x(270)).when(when().add("up", "input")),
                JState.multipart(JState.model(inputId).x(90)).when(when().add("down", "input")),
                //Output panels
                JState.multipart(JState.model(outputId)).when(when().add("north", "output")),
                JState.multipart(JState.model(outputId).y(90)).when(when().add("east", "output")),
                JState.multipart(JState.model(outputId).y(180)).when(when().add("south", "output")),
                JState.multipart(JState.model(outputId).y(270)).when(when().add("west", "output")),
                JState.multipart(JState.model(outputId).x(270)).when(when().add("up", "output")),
                JState.multipart(JState.model(outputId).x(90)).when(when().add("down", "output"))
        ), block);
    }

    public static void createPipeAssets(Identifier block) {
        var id = PulseFluxResources.splicePath(block, "block");
        var armId = PulseFluxResources.appendPath(id, "_arm");
        var coreId = PulseFluxResources.appendPath(id, "_core");
        var straightId = PulseFluxResources.appendPath(id, "_straight");
        PulseFluxResources.PACK.addModel(JModel.model("pulseflux:base/base_pipe_arm").textures(JModel.textures().var("all", armId.toString())), armId);
        PulseFluxResources.PACK.addModel(JModel.model("pulseflux:base/base_pipe_core").textures(JModel.textures().var("all", coreId.toString())), coreId);
        PulseFluxResources.PACK.addModel(JModel.model("pulseflux:base/base_pipe_straight").textures(JModel.textures().var("all", straightId.toString())), straightId);
        PulseFluxResources.PACK.addModel(JModel.model("pulseflux:base/base_pipe_straight").textures(JModel.textures().var("all", straightId.toString())), PulseFluxResources.splicePath(block, "item"));
        PulseFluxResources.PACK.addBlockState(JState.state(
                //core
                JState.multipart(JState.model(coreId)).when((JWhen) ewhen().add("straight", "false")),
                //arms
                JState.multipart(JState.model(armId)).when((JWhen) ewhen().add("straight", "false").add("north", "true")),
                JState.multipart(JState.model(armId).y(90)).when((JWhen) ewhen().add("straight", "false").add("east", "true")),
                JState.multipart(JState.model(armId).y(180)).when((JWhen) ewhen().add("straight", "false").add("south", "true")),
                JState.multipart(JState.model(armId).y(270)).when((JWhen) ewhen().add("straight", "false").add("west", "true")),
                JState.multipart(JState.model(armId).x(270)).when((JWhen) ewhen().add("straight", "false").add("up", "true")),
                JState.multipart(JState.model(armId).x(90)).when((JWhen) ewhen().add("straight", "false").add("down", "true")),
                //straights
                JState.multipart(JState.model(straightId)).when((JWhen) ewhen().add("straight", "true").add("axis", "z")),
                JState.multipart(JState.model(straightId).y(90)).when((JWhen) ewhen().add("straight", "true").add("axis", "x")),
                JState.multipart(JState.model(straightId).x(270)).when((JWhen) ewhen().add("straight", "true").add("axis", "y"))
        ), block);
    }

    public static EWhen ewhen() {
        return (EWhen) when();
    }
}
