package net.id.pulseflux.arrp;

import net.devtech.arrp.json.blockstate.JState;
import net.devtech.arrp.json.models.JModel;
import net.minecraft.util.Identifier;

import static net.id.pulseflux.arrp.PulseFluxResources.*;
import static net.devtech.arrp.json.blockstate.JState.when;

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
        var id = splicePath(block, "block");
        var frameId = appendPath(id, "_frame");
        var panelId = appendPath(id, "_panel");
        var inputId = appendPath(id, "_input");
        var outputId = appendPath(id, "_output");
        PACK.addModel(JModel.model("pulseflux:base/base_diode_frame").textures(JModel.textures().var("frame", id.toString())), frameId);
        PACK.addModel(JModel.model("pulseflux:base/base_diode_panel").textures(JModel.textures().var("panel", id.toString())), panelId);
        PACK.addModel(JModel.model("pulseflux:base/base_diode_input").textures(JModel.textures().var("input", id.toString())), inputId);
        PACK.addModel(JModel.model("pulseflux:base/base_diode_output").textures(JModel.textures().var("output", id.toString())), outputId);
        PACK.addModel(JModel.model("pulseflux:item/base_diode_item").textures(JModel.textures().var("all", id.toString())), splicePath(block, "item"));
        PACK.addBlockState(JState.state(
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
        var armId = appendPath(block, "arm");
        var coreId = appendPath(block, "core");
        var straightId = appendPath(block, "straight");
        PACK.addModel(JModel.model("pulseflux:base/base_pipe_arm").textures(JModel.textures().var("all", armId.toString())), armId);
        PACK.addModel(JModel.model("pulseflux:base/base_pipe_core").textures(JModel.textures().var("all", coreId.toString())), coreId);
        PACK.addModel(JModel.model("pulseflux:base/base_pipe_straight").textures(JModel.textures().var("all", straightId.toString())), straightId);
        PACK.addBlockState(JState.state(
                //core
                JState.multipart(JState.model(coreId)).when(when().add("straight", "false")),
                //arms
                JState.multipart(JState.model(armId)).when(when().add("straight", "false").add("north", "true")),
                JState.multipart(JState.model(armId).y(90)).when(when().add("straight", "false").add("east", "true")),
                JState.multipart(JState.model(armId).y(180)).when(when().add("straight", "false").add("south", "true")),
                JState.multipart(JState.model(armId).y(270)).when(when().add("straight", "false").add("west", "true")),
                JState.multipart(JState.model(armId).x(270)).when(when().add("straight", "false").add("up", "true")),
                JState.multipart(JState.model(armId).x(90)).when(when().add("straight", "false").add("down", "true")),
                //straights
                JState.multipart(JState.model(straightId)).when(when().add("straight", "true").add("linear_axis", "z")),
                JState.multipart(JState.model(straightId).y(90)).when(when().add("straight", "true").add("linear_axis", "x")),
                JState.multipart(JState.model(straightId).x(270)).when(when().add("straight", "true").add("linear_axis", "y"))
        ), block);
    }
}
