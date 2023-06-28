package works.azzyys.pulseflux.arrp;

import works.azzyys.pulseflux.PulseFlux;
import net.minecraft.item.Item;
import works.azzyys.pulseflux.item.PulseFluxItems;

public class PulseFluxBuiltInRecipes {

    public static void init() {
        generate9x("hsla_steel_block_workbench", PulseFluxItems.HSLA_STEEL_INGOT, PulseFluxItems.HSLA_STEEL_BLOCK, true);
        generate9x("hsla_steel_nugget_workbench", PulseFluxItems.HSLA_STEEL_NUGGET, PulseFluxItems.HSLA_STEEL_INGOT, true);
    }

    private static void generate9x(String name, Item ingredient, Item result, boolean allowDissasembly) {
        var id = PulseFlux.locate(name);
        DataGen.generate9xRecipe(id, ingredient, result);
        if(allowDissasembly) {
            DataGen.generateDissasemblyRecipe(id, result, ingredient, 9);
        }
    }

    private static void generate4x(String name, Item ingredient, Item result, boolean allowDissasembly) {
        var id = PulseFlux.locate(name);
        DataGen.generate4xRecipe(id, ingredient, result);
        if(allowDissasembly) {
            DataGen.generateDissasemblyRecipe(id, result, ingredient, 4);
        }
    }
}
