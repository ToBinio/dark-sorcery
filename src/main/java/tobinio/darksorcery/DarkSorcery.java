package tobinio.darksorcery;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tobinio.darksorcery.blocks.ModBlocks;
import tobinio.darksorcery.items.ModItemGroups;
import tobinio.darksorcery.items.ModItems;
import tobinio.darksorcery.tags.ModTags;

public class DarkSorcery implements ModInitializer {
    public static final String MOD_ID = "dark-sorcery";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModBlocks.initialize();
        ModItems.initialize();
        ModItemGroups.initialize();
        ModTags.initialize();
    }
}