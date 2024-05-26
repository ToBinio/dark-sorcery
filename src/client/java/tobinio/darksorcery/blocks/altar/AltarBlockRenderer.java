package tobinio.darksorcery.blocks.altar;

import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import java.util.List;

/**
 * Created: 10.05.24
 *
 * @author Tobias Frischmann
 */
public class AltarBlockRenderer implements BlockEntityRenderer<AltarEntity> {

    private final static float ZERO = 3 / 16f;
    private final static float ONE = 13 / 16f;

    private final static float[][] TOP_VERTEXES = new float[][]{
            //x, y
            {ZERO, ZERO}, // Top-left
            {ZERO, ONE},  // Bottom-left
            {ONE, ONE}, // Bottom-right
            {ONE, ZERO}, // Top-right
    };

    private final static float[][][] SIDE_VERTEXES = new float[][][]{
            //x, z, u, v
            {
                    {ONE, ZERO, 0, 0},
                    {ONE, ZERO, 1, 0},
                    {ZERO, ZERO, 1, 1},
                    {ZERO, ZERO, 0, 1},
            },
            {
                    {ZERO, ONE, 0, 0},
                    {ZERO, ONE, 1, 0},
                    {ONE, ONE, 1, 1},
                    {ONE, ONE, 0, 1},
            },
            {
                    {ZERO, ZERO, 0, 0},
                    {ZERO, ZERO, 1, 0},
                    {ZERO, ONE, 1, 1},
                    {ZERO, ONE, 0, 1},
            },
            {
                    {ONE, ONE, 0, 0},
                    {ONE, ONE, 1, 0},
                    {ONE, ZERO, 1, 1},
                    {ONE, ZERO, 0, 1},
            },
    };


    public AltarBlockRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(AltarEntity entity, float tickDelta, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, int overlay) {
        drawItem(entity, tickDelta, matrices, vertexConsumers, light, overlay);

        if (!entity.fluidStorage.variant.isBlank()) {
            drawFluids(entity, matrices, vertexConsumers, light, overlay);
        }
    }

    private static void drawFluids(AltarEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
            int light, int overlay) {
        matrices.push();

        var sprite = FluidVariantRendering.getSprite(entity.fluidStorage.variant);
        var spriteColor = FluidVariantRendering.getColor(entity.fluidStorage.variant);

        var consumer = vertexConsumers.getBuffer(RenderLayer.getTranslucent());

        List<Vec3d> rotatedTowerLocations = entity.getRotatedTowerLocations();

        for (int towerIndex = 0; towerIndex < AltarEntity.TOWER_LOCATIONS.size(); towerIndex++) {

            var towerLocation = rotatedTowerLocations.get(towerIndex);
            float towerHeight = entity.getTowerFilledHeights()[towerIndex];

            matrices.push();
            matrices.translate(towerLocation.getX(), towerLocation.getY(), towerLocation.getZ());

            var entry = matrices.peek();
            var modelMatrix = entry.getPositionMatrix();
            var normalMatrix = entry.getNormalMatrix();

            for (float[] pos : TOP_VERTEXES) {
                consumer.vertex(modelMatrix, pos[0], towerHeight, pos[1])
                        .color(spriteColor)
                        .texture(sprite.getFrameU(pos[0]), sprite.getFrameV(pos[1]))
                        .light(light)
                        .overlay(overlay)
                        .normal(normalMatrix, 0, 1, 0)
                        .next();
            }

            for (int i = TOP_VERTEXES.length - 1; i >= 0; i--) {
                float[] pos = TOP_VERTEXES[i];

                consumer.vertex(modelMatrix, pos[0], towerHeight, pos[1])
                        .color(spriteColor)
                        .texture(sprite.getFrameU(pos[0]), sprite.getFrameV(pos[1]))
                        .light(light)
                        .overlay(overlay)
                        .normal(normalMatrix, 0, 1, 0)
                        .next();
            }

            for (float[][] sideVertex : SIDE_VERTEXES) {
                for (int i = 0; i < sideVertex.length; i++) {
                    float[] pos = sideVertex[i];

                    float height = (i == 0 || i == 3) ? towerHeight : 0;

                    consumer.vertex(modelMatrix, pos[0], height, pos[1])
                            .color(spriteColor)
                            .texture(sprite.getFrameU(pos[2]), sprite.getFrameV(pos[3]))
                            .light(light)
                            .overlay(overlay)
                            .normal(normalMatrix, 0, 1, 0)
                            .next();
                }
            }

            matrices.pop();
        }

        matrices.pop();
    }

    private static void drawItem(AltarEntity entity, float tickDelta, MatrixStack matrices,
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
