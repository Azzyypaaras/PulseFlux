package net.id.pulseflux.arrp;

import net.devtech.arrp.json.recipe.*;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import static net.id.pulseflux.arrp.PulseFluxResources.*;

public class DataGen {

    public static void generate9xRecipe(Identifier id, Item ingredient, Item result) {
        PACK.addRecipe(id, JRecipe.shaped(
                JPattern.pattern(
                        "###",
                        "###",
                        "###"
                ),
                JKeys.keys().key("#", JIngredient.ingredient().item(ingredient)),
                JResult.item(result)));
    }

    public static void generate4xRecipe(Identifier id, Item ingredient, Item result) {
        PACK.addRecipe(id, JRecipe.shaped(
                JPattern.pattern(
                        "##",
                        "##"
                ),
                JKeys.keys().key("#", JIngredient.ingredient().item(ingredient)),
                JResult.item(result)));
    }

    public static void generateDissasemblyRecipe(Identifier id, Item ingredient, Item result, int count) {
        PACK.addRecipe(appendPath(id, "_disassembly"), JRecipe.shapeless(
                JIngredients.ingredients().add(
                        JIngredient.ingredient().item(ingredient)
                ),
                JResult.itemStack(result, count)));
    }
}
