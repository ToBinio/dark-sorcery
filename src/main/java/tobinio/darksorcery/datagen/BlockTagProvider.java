package tobinio.darksorcery.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryWrapper;
import tobinio.darksorcery.tags.ModTags;

import java.util.concurrent.CompletableFuture;

/**
 * Created: 09.05.24
 *
 * @author Tobias Frischmann
 */
public class BlockTagProvider extends FabricTagProvider.BlockTagProvider {

    public BlockTagProvider(FabricDataOutput output,
            CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(ModTags.ALTAR_GROUND).add(Blocks.CALCITE);

        getOrCreateTagBuilder(ModTags.ALTAR_TOWER_BLOCKS).add(Blocks.BLACKSTONE)
                .add(Blocks.GILDED_BLACKSTONE)
                .add(Blocks.GOLD_BLOCK)
                .add(Blocks.EMERALD_BLOCK)
                .add(Blocks.DIAMOND_BLOCK);

        getOrCreateTagBuilder(ModTags.ALTAR_TIER1_BLOCKS).add(Blocks.BLACKSTONE);
        getOrCreateTagBuilder(ModTags.ALTAR_TIER2_BLOCKS).add(Blocks.GILDED_BLACKSTONE);
        getOrCreateTagBuilder(ModTags.ALTAR_TIER3_BLOCKS).add(Blocks.GOLD_BLOCK);
        getOrCreateTagBuilder(ModTags.ALTAR_TIER4_BLOCKS).add(Blocks.EMERALD_BLOCK);
        getOrCreateTagBuilder(ModTags.ALTAR_TIER5_BLOCKS).add(Blocks.DIAMOND_BLOCK);
    }
}
