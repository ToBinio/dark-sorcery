package tobinio.darksorcery.fluids;

import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import tobinio.darksorcery.DarkSorcery;

/**
 * Created: 18.05.24
 *
 * @author Tobias Frischmann
 */
public class ModFluids {

    public static FlowableFluid BLOOD = Registry.register(Registries.FLUID, new Identifier(DarkSorcery.MOD_ID, "blood"), new BloodFluid.Still());
    public static FlowableFluid FLOWING_BLOOD = Registry.register(Registries.FLUID, new Identifier(DarkSorcery.MOD_ID, "flowing_blood"), new BloodFluid.Flowing());
    public static Item BLOOD_BUCKET = Registry.register(Registries.ITEM, new Identifier(DarkSorcery.MOD_ID, "blood_bucket"), new BucketItem(BLOOD, new Item.Settings().recipeRemainder(Items.BUCKET)
            .maxCount(1)));

    public static void initialize() {
    }
}
