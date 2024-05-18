package tobinio.darksorcery;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import tobinio.darksorcery.blocks.ModBlocks;
import tobinio.darksorcery.blocks.altar.AltarBlockRenderer;
import tobinio.darksorcery.blocks.altar.BloodFunnelBlockRenderer;

public class DarkSorceryClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererFactories.register(ModBlocks.ALTAR_ENTITY_TYPE, AltarBlockRenderer::new);
        BlockEntityRendererFactories.register(ModBlocks.BLOOD_FUNNEL_ENTITY_TYPE, BloodFunnelBlockRenderer::new);
    }
}