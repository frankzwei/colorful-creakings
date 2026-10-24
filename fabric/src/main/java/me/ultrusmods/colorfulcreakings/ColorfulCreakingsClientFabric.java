package me.ultrusmods.colorfulcreakings;

import me.ultrusmods.colorfulcreakings.client.ColorfulCreakingsModelLayers;
import me.ultrusmods.colorfulcreakings.network.CreakingColorS2CPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.model.monster.creaking.CreakingModel;

public class ColorfulCreakingsClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(CreakingColorS2CPacket.TYPE,
                (payload, context) -> payload.handle());
        ModelLayerRegistry.registerModelLayer(ColorfulCreakingsModelLayers.COLORED_CREAKING_EYES,
                CreakingModel::createBodyLayer);
    }
}