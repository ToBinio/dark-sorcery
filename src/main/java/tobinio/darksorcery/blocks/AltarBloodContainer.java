package tobinio.darksorcery.blocks;

import net.minecraft.block.Block;

/**
 * Created: 25.05.24
 *
 * @author Tobias Frischmann
 */
public class AltarBloodContainer extends Block {

    private final long storage;

    public AltarBloodContainer(Settings settings, long storage) {
        super(settings);
        this.storage = storage;
    }

    public long getStorage() {
        return storage;
    }
}
