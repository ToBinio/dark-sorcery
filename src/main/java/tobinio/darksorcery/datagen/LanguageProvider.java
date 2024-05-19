package tobinio.darksorcery.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import tobinio.darksorcery.blocks.ModBlocks;
import tobinio.darksorcery.items.ModItemGroups;
import tobinio.darksorcery.items.ModItems;

/**
 * Created: 19.05.24
 *
 * @author Tobias Frischmann
 */
public class LanguageProvider extends FabricLanguageProvider {
    protected LanguageProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {
        translationBuilder.add(ModItems.BLOOD_BLADE, "Blood Blade");
        translationBuilder.add(ModItems.BLOOD_STAFF, "Blood Staff (name wip)");
        translationBuilder.add(ModItems.TINTED_GLASS_BOTTLE, "Tinted Bottle");
        translationBuilder.add(ModItems.BLOODY_TINTED_GLASS_BOTTLE, "Bloody Tinted Bottle");

        translationBuilder.add(ModItemGroups.ITEM_GROUP_KEY, "Dark Sorcery");

        translationBuilder.add(ModBlocks.ALTAR, "Altar");
        translationBuilder.add(ModBlocks.BLOOD_FUNNEL, "Blood Funnel");
    }
}
