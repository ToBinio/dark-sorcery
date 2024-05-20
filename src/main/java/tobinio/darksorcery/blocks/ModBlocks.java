package tobinio.darksorcery.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import tobinio.darksorcery.DarkSorcery;
import tobinio.darksorcery.blocks.altar.AltarBlock;
import tobinio.darksorcery.blocks.altar.AltarEntity;
import tobinio.darksorcery.blocks.bloodfunnel.BloodFunnelBlock;
import tobinio.darksorcery.blocks.bloodfunnel.BloodFunnelEntity;
import tobinio.darksorcery.fluids.ModFluids;

/**
 * Created: 19.04.24
 *
 * @author Tobias Frischmann
 */
public class ModBlocks {

    public static Block BLOOD = Registry.register(Registries.BLOCK, new Identifier(DarkSorcery.MOD_ID, "blood"), new FluidBlock(ModFluids.BLOOD, FabricBlockSettings.copy(Blocks.WATER)) {
    });

    public static Block BLOOD_FUNNEL = register(new BloodFunnelBlock(AbstractBlock.Settings.create()
            .hardness(1.5f)
            .requiresTool()), "blood_funnel", true);
    public static BlockEntityType<BloodFunnelEntity> BLOOD_FUNNEL_ENTITY_TYPE = registerBlockEntity(BlockEntityType.Builder.create(BloodFunnelEntity::new, BLOOD_FUNNEL)
            .build(), "blood_funnel_entity");

    public static Block ALTAR = register(new AltarBlock(AbstractBlock.Settings.create()
            .hardness(3f)
            .requiresTool()), "altar", true);
    public static BlockEntityType<AltarEntity> ALTAR_ENTITY_TYPE = registerBlockEntity(BlockEntityType.Builder.create(AltarEntity::new, ALTAR)
            .build(), "altar_entity");

    public static <T extends Block> T register(T block, String name, boolean shouldRegisterItem) {
        Identifier id = new Identifier(DarkSorcery.MOD_ID, name);

        if (shouldRegisterItem) {
            BlockItem blockItem = new BlockItem(block, new Item.Settings());
            Registry.register(Registries.ITEM, id, blockItem);
        }

        return Registry.register(Registries.BLOCK, id, block);
    }

    public static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(BlockEntityType<T> blockEntityType,
            String name) {
        Identifier id = new Identifier(DarkSorcery.MOD_ID, name);

        return Registry.register(Registries.BLOCK_ENTITY_TYPE, id, blockEntityType);
    }


    public static void initialize() {
        FluidStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.storage, BLOOD_FUNNEL_ENTITY_TYPE);

        FluidStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.fluidStorage, ALTAR_ENTITY_TYPE);
        ItemStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.itemStorage, ALTAR_ENTITY_TYPE);
    }
}
