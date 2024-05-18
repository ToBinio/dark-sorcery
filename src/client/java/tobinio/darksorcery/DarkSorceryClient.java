package tobinio.darksorcery;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import tobinio.darksorcery.blocks.ModBlocks;
import tobinio.darksorcery.blocks.altar.AltarBlockRenderer;
import tobinio.darksorcery.blocks.altar.BloodFunnelBlockRenderer;
import tobinio.darksorcery.fluids.ModFluids;

public class DarkSorceryClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.BLOOD, ModFluids.FLOWING_BLOOD, new SimpleFluidRenderHandler(
                new Identifier("minecraft:block/water_still"),
                new Identifier("minecraft:block/water_flow"),
                Colors.RED
        ));

        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), ModFluids.BLOOD, ModFluids.FLOWING_BLOOD);

        BlockEntityRendererFactories.register(ModBlocks.ALTAR_ENTITY_TYPE, AltarBlockRenderer::new);
        BlockEntityRendererFactories.register(ModBlocks.BLOOD_FUNNEL_ENTITY_TYPE, BloodFunnelBlockRenderer::new);
    }
}