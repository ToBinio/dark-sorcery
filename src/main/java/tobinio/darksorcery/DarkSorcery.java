package tobinio.darksorcery;

import net.fabricmc.api.ModInitializer;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
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

    @Override
    public void onInitialize() {
        ModBlocks.initialize();
        ModItems.initialize();
        ModItemGroups.initialize();
        ModTags.initialize();
        ModFluids.initialize();

        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(MOD_ID, "altar"), AltarRecipe.AltarRecipeSerializer.INSTANCE);
        Registry.register(Registries.RECIPE_TYPE, new Identifier(MOD_ID, "altar"), AltarRecipe.Type.INSTANCE);
    }
}