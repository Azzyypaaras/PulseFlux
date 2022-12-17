package net.id.pulseflux.arrp;

import net.devtech.arrp.json.loot.JLootTable;
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

    public static void lootSelfDrop(Identifier id) {
        PACK.addLootTable(splicePath(id, "blocks"), JLootTable.loot("minecraft:block").pool(
                JLootTable.pool()
                        .rolls(1)
                        .bonus(0)
                        .entry(JLootTable.entry()
                                .type("minecraft:item")
                                .name(id.toString()))
                        .condition(JLootTable.predicate("minecraft:survives_explosion"))
                )
        );
    }
}
