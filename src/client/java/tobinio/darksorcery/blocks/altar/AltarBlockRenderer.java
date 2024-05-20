package tobinio.darksorcery.blocks.altar;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

/**
 * Created: 10.05.24
 *
 * @author Tobias Frischmann
 */
public class AltarBlockRenderer implements BlockEntityRenderer<AltarEntity> {


    public AltarBlockRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(AltarEntity entity, float tickDelta, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();

        ItemVariant itemVariant = entity.itemStorage.getResource();

        if (!itemVariant.isBlank()) {
            double offset = Math.sin((entity.getWorld().getTime() + tickDelta) / 8.0) / 8.0;

            matrices.translate(0.5, 1 + offset, 0.5);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((entity.getWorld().getTime() + tickDelta) * 4));

            MinecraftClient.getInstance()
                    .getItemRenderer()
                    .renderItem(itemVariant.toStack(), ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, entity.getWorld(), 0);
        }

        matrices.pop();
    }
}
