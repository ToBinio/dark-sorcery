package tobinio.darksorcery;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import tobinio.darksorcery.blockHighlight.BlockHighlightManager;
import tobinio.darksorcery.blocks.ModBlocks;
import tobinio.darksorcery.blocks.altar.AltarBlockRenderer;
import tobinio.darksorcery.blocks.altar.BloodFunnelBlockRenderer;
import tobinio.darksorcery.blocks.bloodfunnel.BloodFunnelBlock;
import tobinio.darksorcery.fluids.ModFluids;
import tobinio.darksorcery.mixin.client.WorldRendererAccessor;

public class DarkSorceryClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.BLOOD, ModFluids.FLOWING_BLOOD, new SimpleFluidRenderHandler(new Identifier("minecraft:block/water_still"), new Identifier("minecraft:block/water_flow"), Colors.RED));
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), ModFluids.BLOOD, ModFluids.FLOWING_BLOOD);

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ALTAR_BLOOD_CONTAINER, RenderLayer.getCutout());

        BlockEntityRendererFactories.register(ModBlocks.ALTAR_ENTITY_TYPE, AltarBlockRenderer::new);
        BlockEntityRendererFactories.register(ModBlocks.BLOOD_FUNNEL_ENTITY_TYPE, BloodFunnelBlockRenderer::new);

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            BlockHighlightManager.INSTANCE.tick();
        });

        WorldRenderEvents.AFTER_ENTITIES.register((context) -> {
            MatrixStack matrixStack = context.matrixStack();
            Vec3d camera = context.camera().getPos();

            matrixStack.push();
            matrixStack.translate(-camera.x, -camera.y, -camera.z);

            for (BlockPos blockPos : BlockHighlightManager.INSTANCE.getHighlighted()) {
                WorldRendererAccessor.callDrawCuboidShapeOutline(matrixStack, context.consumers()
                        .getBuffer(RenderLayer.LINES), BloodFunnelBlock.VOXEL_SHAPE, blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1, 1, 1, 1);
            }

            matrixStack.pop();
        });
    }
}