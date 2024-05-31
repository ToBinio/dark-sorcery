package tobinio.darksorcery.blocks.altar;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.Vec3d;
import tobinio.darksorcery.blocks.AltarBloodContainer;
import tobinio.darksorcery.tags.ModTags;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created: 31.05.24
 *
 * @author Tobias Frischmann
 */
public class AltarTowers {

    private List<List<BlockState>> towersBlocks;

    public AltarTowers(AltarEntity altar) {
        towersBlocks = new ArrayList<>();

        for (Vec3d towerLocation : altar.getRotatedTowerOffsets()) {
            var towerPos = altar.getPos()
                    .add((int) Math.round(towerLocation.x), (int) Math.round(towerLocation.y), (int) Math.round(towerLocation.z));

            List<BlockState> blocks = new ArrayList<>();

            while (true) {
                BlockState block = altar.getWorld().getBlockState(towerPos);

                if (!block.isIn(ModTags.ALTAR_TOWER_BLOCKS)) {
                    break;
                }

                blocks.add(block);

                towerPos = towerPos.up();
            }

            towersBlocks.add(blocks);
        }
    }

    public int getLevel() {

        if (towersBlocks.stream().anyMatch(List::isEmpty)) {
            return 0;
        }

        if (countBlocksIn(ModTags.ALTAR_TIER5_BLOCKS) > 10) return 5;
        if (countBlocksIn(ModTags.ALTAR_TIER5_BLOCKS, ModTags.ALTAR_TIER4_BLOCKS) > 10) return 4;
        if (countBlocksIn(ModTags.ALTAR_TIER5_BLOCKS, ModTags.ALTAR_TIER4_BLOCKS, ModTags.ALTAR_TIER3_BLOCKS) > 10)
            return 3;
        if (countBlocksIn(ModTags.ALTAR_TIER5_BLOCKS, ModTags.ALTAR_TIER4_BLOCKS, ModTags.ALTAR_TIER3_BLOCKS, ModTags.ALTAR_TIER2_BLOCKS) > 10)
            return 2;
        if (countBlocksIn(ModTags.ALTAR_TIER5_BLOCKS, ModTags.ALTAR_TIER4_BLOCKS, ModTags.ALTAR_TIER3_BLOCKS, ModTags.ALTAR_TIER2_BLOCKS, ModTags.ALTAR_TIER1_BLOCKS) > 10)
            return 1;
        return 0;
    }

    @SafeVarargs
    private long countBlocksIn(TagKey<Block>... tags) {
        return towersBlocks.stream().flatMap(List::stream).filter(block -> {
            for (TagKey<Block> tag : tags) {
                if (block.isIn(tag)) {
                    return true;
                }
            }

            return false;
        }).count();
    }

    public List<Integer> getHeights() {
        return towersBlocks.stream().map(List::size).collect(Collectors.toList());
    }

    public List<Long> getBloodCapacityPerLayer() {

        List<Long> capacities = new ArrayList<>();

        int maxHeight = getHeights().stream().max(Integer::compareTo).orElse(0);

        for (int i = 0; i < maxHeight; i++) {

            long capacity = 0L;

            for (List<BlockState> towersBlock : towersBlocks) {

                if (towersBlock.size() <= i) {
                    continue;
                }

                var block = towersBlock.get(i);

                if (block.getBlock() instanceof AltarBloodContainer altarBloodContainer) {
                    capacity += altarBloodContainer.getStorage();
                }
            }

            capacities.add(capacity);
        }

        return capacities;
    }
}
