package tobinio.darksorcery.mixin.client;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Created: 20.05.24
 *
 * @author Tobias Frischmann
 */
@Mixin (net.minecraft.client.render.WorldRenderer.class)
public interface WorldRendererAccessor {

    @Invoker
    static void callDrawCuboidShapeOutline(MatrixStack matrices, VertexConsumer vertexConsumer, VoxelShape shape,
            double offsetX, double offsetY, double offsetZ, float red, float green, float blue, float alpha) {
        throw new UnsupportedOperationException();
    }
}
