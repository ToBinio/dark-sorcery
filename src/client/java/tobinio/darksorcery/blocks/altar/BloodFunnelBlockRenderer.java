package tobinio.darksorcery.blocks.altar;

import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluids;
import tobinio.darksorcery.blocks.bloodfunnel.BloodFunnelEntity;

/**
 * Created: 10.05.24
 *
 * @author Tobias Frischmann
 */
public class BloodFunnelBlockRenderer implements BlockEntityRenderer<BloodFunnelEntity> {


    private final static float[][] VERTEXES = new float[][]{
            //x, y
            {2 / 16f, 2 / 16f}, // Top-left
            {2 / 16f, 14 / 16f},  // Bottom-left
            {14 / 16f, 14 / 16f}, // Bottom-right
            {14 / 16f, 2 / 16f}, // Top-right
    };

    private final static float MIN_HEIGHT = 5 / 16f;
    private final static float MAX_HEIGHT = 8 / 16f;

    public BloodFunnelBlockRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(BloodFunnelEntity entity, float tickDelta, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, int overlay) {

        var fillPercentage = (float) entity.storage.getAmount() / entity.storage.getCapacity();

        if (fillPercentage == 0) {
            return;
        }

        matrices.push();

        var sprite = FluidVariantRendering.getSprite(entity.storage.variant);
        assert sprite != null;
        var spriteColor = FluidVariantRendering.getColor(entity.storage.variant);

        var entry = matrices.peek();
        var modelMatrix = entry.getPositionMatrix();
        var normalMatrix = entry.getNormalMatrix();

        var consumer = vertexConsumers.getBuffer(RenderLayer.getTranslucent());


        for (float[] pos : VERTEXES) {
            consumer.vertex(modelMatrix, pos[0], MIN_HEIGHT + (MAX_HEIGHT - MIN_HEIGHT) * fillPercentage, pos[1])
                    .color(spriteColor)
                    .texture(sprite.getFrameU(pos[0]), sprite.getFrameV(pos[1]))
                    .light(light)
                    .overlay(overlay)
                    .normal(normalMatrix, 0, 1, 0)
                    .next();
        }

        matrices.pop();
    }
}
