package me.ultrusmods.colorfulcreakings.client;

import me.ultrusmods.colorfulcreakings.Constants;
import net.minecraft.client.model.monster.creaking.CreakingModel;
import net.minecraft.client.renderer.entity.CreakingRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.entity.EntityTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public class ColorfulCreakingsClientEventsNeoForge {

    @SubscribeEvent
    public static void registerEntityLayers(EntityRenderersEvent.RegisterLayerDefinitions e) {
        e.registerLayerDefinition(ColorfulCreakingsModelLayers.COLORED_CREAKING_EYES,
                CreakingModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void onAddRendererLayer(EntityRenderersEvent.AddLayers e) {
        if (e.getEntityTypes().contains(EntityTypes.CREAKING)) {
            EntityRenderer<?, ?> renderer = e.getRenderer(EntityTypes.CREAKING);
            if (renderer instanceof CreakingRenderer<?> creakingRenderer) {
                creakingRenderer.addLayer(new ColoredCreakingEyesRenderer(creakingRenderer,
                        RenderTypes::eyes, e.getEntityModels()));
            }
        }
    }
}