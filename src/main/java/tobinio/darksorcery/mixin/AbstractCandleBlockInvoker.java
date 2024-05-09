package tobinio.darksorcery.mixin;

import net.minecraft.block.AbstractCandleBlock;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Created: 09.05.24
 *
 * @author Tobias Frischmann
 */
@Mixin (AbstractCandleBlock.class)
public interface AbstractCandleBlockInvoker {

    @Invoker ("spawnCandleParticles")
    static void invokeSpawnCandleParticles(World world, Vec3d vec3d, Random random) {
        throw new AssertionError();
    }
}
