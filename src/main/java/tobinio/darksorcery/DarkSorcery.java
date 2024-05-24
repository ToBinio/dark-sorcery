package tobinio.darksorcery;

import net.fabricmc.api.ModInitializer;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Recipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tobinio.darksorcery.blocks.ModBlocks;
import tobinio.darksorcery.fluids.ModFluids;
import tobinio.darksorcery.items.ModItemGroups;
import tobinio.darksorcery.items.ModItems;
import tobinio.darksorcery.recipe.AltarRecipe;
import tobinio.darksorcery.tags.ModTags;

import java.util.HashMap;

public class DarkSorcery implements ModInitializer {
    public static final String MOD_ID = "dark-sorcery";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    //wip
    public static HashMap<Item, AltarRecipe> recipes = new HashMap<>();

    @Override
    public void onInitialize() {
        ModBlocks.initialize();
        ModItems.initialize();
        ModItemGroups.initialize();
        ModTags.initialize();
        ModFluids.initialize();

        recipes.put(Items.DIRT, new AltarRecipe(Items.DIRT, Items.DIAMOND, 10_000, 120));
    }
}