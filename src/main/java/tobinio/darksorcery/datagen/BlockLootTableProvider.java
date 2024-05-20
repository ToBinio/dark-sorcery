package tobinio.darksorcery.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import tobinio.darksorcery.blocks.ModBlocks;

/**
 * Created: 20.05.24
 *
 * @author Tobias Frischmann
 */
public class BlockLootTableProvider extends FabricBlockLootTableProvider {
    protected BlockLootTableProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        addDrop(ModBlocks.ALTAR);
        addDrop(ModBlocks.BLOOD_FUNNEL);
    }
}
