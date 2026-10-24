package me.ultrusmods.colorfulcreakings;

import me.ultrusmods.colorfulcreakings.network.CreakingColorS2CPacket;
import me.ultrusmods.colorfulcreakings.register.ColorfulCreakingBlocks;
import me.ultrusmods.colorfulcreakings.register.ColorfulCreakingsAttachments;
import me.ultrusmods.colorfulcreakings.register.ColorfulCreakingsBlockEntities;
import me.ultrusmods.colorfulcreakings.register.ColorfulCreakingsCreativeTabItems;
import me.ultrusmods.colorfulcreakings.register.ColorfulCreakingsItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.entity.monster.creaking.Creaking;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

public class ColorfulCreakingsModFabric implements ModInitializer {
    private static ResourceKey<CreativeModeTab> creativeTabKey(String name) {
        return ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.withDefaultNamespace(name));
    }


    @Override
    public void onInitialize() {
        ColorfulCreakingsMod.init();
        ColorfulCreakingBlocks.register();
        ColorfulCreakingsItems.register();
        ColorfulCreakingsBlockEntities.register();
        ColorfulCreakingsAttachments.register();

        CreativeModeTabEvents.modifyOutputEvent(creativeTabKey("building_blocks")).register(output -> {
            ColorfulCreakingsCreativeTabItems.addBeforeOrangeResinBuildingBlocks(output::insertBefore);
            ColorfulCreakingsCreativeTabItems.addAfterOrangeResinBuildingBlocks(output::insertAfter);
        });

        CreativeModeTabEvents.modifyOutputEvent(creativeTabKey("ingredients")).register(output -> {
            ColorfulCreakingsCreativeTabItems.addBeforeOrangeResinIngredients(output::insertBefore);
            ColorfulCreakingsCreativeTabItems.addAfterOrangeResinIngredients(output::insertAfter);
        });

        CreativeModeTabEvents.modifyOutputEvent(creativeTabKey("spawn_eggs")).register(output -> {
            ColorfulCreakingsCreativeTabItems.addBeforeCreakingHeart(output::insertBefore);
            ColorfulCreakingsCreativeTabItems.addAfterCreakingHeart(output::insertAfter);
        });

        CreativeModeTabEvents.modifyOutputEvent(creativeTabKey("natural_blocks")).register(output -> {
            ColorfulCreakingsCreativeTabItems.addBeforeNaturalBlocks(output::insertBefore);
            ColorfulCreakingsCreativeTabItems.addAfterNaturalBlocks(output::insertAfter);
        });

        EntityTrackingEvents.START_TRACKING.register((entity, player) -> {
            if (entity instanceof Creaking creaking
                    && ColorfulCreakingsAttachments.CREAKING_COLOR.hasAttachment(creaking)) {
                ServerPlayNetworking.send(player, new CreakingColorS2CPacket(creaking.getId(),
                        ColorfulCreakingsAttachments.CREAKING_COLOR.getAttachment(creaking)));
            }
        });

        PayloadTypeRegistry.clientboundPlay().register(CreakingColorS2CPacket.TYPE,
                CreakingColorS2CPacket.STREAM_CODEC);
    }
}