package tobinio.darksorcery.recipe;

import net.minecraft.item.Item;

/**
 * Created: 23.05.24
 *
 * @author Tobias Frischmann
 */
public record AltarRecipe(Item input, Item output, int bloodConsumption, int time) {

}
