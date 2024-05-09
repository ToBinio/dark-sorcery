package tobinio.darksorcery.tags;

import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import tobinio.darksorcery.DarkSorcery;

/**
 * Created: 09.05.24
 *
 * @author Tobias Frischmann
 */
public class ModTags {

    public static final TagKey<Block> ALTAR_TOWER_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier(DarkSorcery.MOD_ID, "altar_tower"));
    public static final TagKey<Block> ALTAR_TIER1_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier(DarkSorcery.MOD_ID, "altar_tier1"));
    public static final TagKey<Block> ALTAR_TIER2_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier(DarkSorcery.MOD_ID, "altar_tier2"));
    public static final TagKey<Block> ALTAR_TIER3_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier(DarkSorcery.MOD_ID, "altar_tier3"));
    public static final TagKey<Block> ALTAR_TIER4_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier(DarkSorcery.MOD_ID, "altar_tier4"));
    public static final TagKey<Block> ALTAR_TIER5_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier(DarkSorcery.MOD_ID, "altar_tier5"));

    public static final TagKey<Block> ALTAR_GROUND = TagKey.of(RegistryKeys.BLOCK, new Identifier(DarkSorcery.MOD_ID, "altar_ground"));


    public static void initialize() {
    }
}
