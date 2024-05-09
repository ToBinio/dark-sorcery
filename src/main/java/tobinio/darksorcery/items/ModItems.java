package tobinio.darksorcery.items;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import tobinio.darksorcery.DarkSorcery;

/**
 * Created: 20.04.24
 *
 * @author Tobias Frischmann
 */
public class ModItems {

    public static final Item BLOOD_BLADE = register(new BloodBlade(ToolMaterials.IRON, 6, -3.1f, new FabricItemSettings()), "blood_blade");
    public static final Item TINTED_GLASS_BOTTLE = register(new TintedGlassBottle(new FabricItemSettings()), "tinted_glass_bottle");
    public static final Item BLOODY_TINTED_GLASS_BOTTLE = register(new BloodyTintedGlassBottle(new FabricItemSettings()), "bloody_tinted_glass_bottle");

    public static <T extends Item> T register(T item, String ID) {
        Identifier itemID = new Identifier(DarkSorcery.MOD_ID, ID);

        return Registry.register(Registries.ITEM, itemID, item);
    }

    public static void initialize() {
    }
}
