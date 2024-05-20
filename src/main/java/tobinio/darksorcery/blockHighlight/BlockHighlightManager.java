package tobinio.darksorcery.blockHighlight;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created: 20.05.24
 *
 * @author Tobias Frischmann
 */
@Environment (EnvType.CLIENT)
public class BlockHighlightManager {
    public static final BlockHighlightManager INSTANCE = new BlockHighlightManager();

    private List<HighlightedEntry> tobeHighlighted = new ArrayList<>();

    public void tick() {
        tobeHighlighted.removeIf(highlightedEntry -> highlightedEntry.timeToHighlight <= 0);

        for (HighlightedEntry highlightedEntry : tobeHighlighted) {
            highlightedEntry.timeToHighlight--;
        }
    }

    public List<BlockPos> getHighlighted() {
        return tobeHighlighted.stream().map(HighlightedEntry::getPos).collect(Collectors.toList());
    }

    public void highlightBlock(BlockPos pos, int timeToHighlight) {
        tobeHighlighted.add(new HighlightedEntry(pos, timeToHighlight));
    }

    private class HighlightedEntry {
        private final BlockPos pos;
        private int timeToHighlight;

        public HighlightedEntry(BlockPos pos, int timeToHighlight) {
            this.pos = pos;
            this.timeToHighlight = timeToHighlight;
        }

        public BlockPos getPos() {
            return pos;
        }

        public int getTimeToHighlight() {
            return timeToHighlight;
        }
    }
}
