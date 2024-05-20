package tobinio.darksorcery.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import tobinio.darksorcery.fluids.ModFluids;
import tobinio.darksorcery.items.ModItems;

/**
 * Created: 20.04.24
 *
 * @author Tobias Frischmann
 */
public class ModelProvider extends FabricModelProvider {
    public ModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.BLOOD_BLADE, Models.GENERATED);
        itemModelGenerator.register(ModItems.BLOOD_STAFF, Models.GENERATED);
        itemModelGenerator.register(ModItems.TINTED_GLASS_BOTTLE, Models.GENERATED);
        itemModelGenerator.register(ModItems.BLOODY_TINTED_GLASS_BOTTLE, Models.GENERATED);
        itemModelGenerator.register(ModItems.BLOOD_BUCKET, Models.GENERATED);
    }
}
