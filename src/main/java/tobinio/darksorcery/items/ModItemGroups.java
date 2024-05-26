package tobinio.darksorcery.items;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import tobinio.darksorcery.DarkSorcery;
import tobinio.darksorcery.blocks.ModBlocks;

/**
 * Created: 21.04.24
 *
 * @author Tobias Frischmann
 */
public class ModItemGroups {
    public static final String ITEM_GROUP_KEY = "itemGroup.%s.dark-sorcery".formatted(DarkSorcery.MOD_ID);
    public static final ItemGroup ITEM_GROUP = Registry.register(Registries.ITEM_GROUP, new Identifier(DarkSorcery.MOD_ID, "item_group"), FabricItemGroup.builder()
            .displayName(Text.translatable(ITEM_GROUP_KEY))
            .icon(() -> new ItemStack(ModItems.TINTED_GLASS_BOTTLE))
            .entries((displayContext, entries) -> {
                entries.add(ModItems.TINTED_GLASS_BOTTLE);
                entries.add(ModItems.BLOODY_TINTED_GLASS_BOTTLE);
                entries.add(ModItems.BLOOD_BLADE);
                entries.add(ModItems.BLOOD_STAFF);
                entries.add(ModItems.BLOOD_BUCKET);

                entries.add(ModBlocks.BLOOD_FUNNEL);
                entries.add(ModBlocks.ALTAR);
                entries.add(ModBlocks.ALTAR_BLOOD_CONTAINER);
            })
            .build());

    public static void initialize() {
    }
}
