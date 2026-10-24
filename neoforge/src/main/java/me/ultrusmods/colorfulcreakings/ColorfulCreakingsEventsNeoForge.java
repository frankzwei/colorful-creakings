package me.ultrusmods.colorfulcreakings;

import me.ultrusmods.colorfulcreakings.network.CreakingColorS2CPacket;
import me.ultrusmods.colorfulcreakings.register.ColorfulCreakingsAttachments;
import me.ultrusmods.colorfulcreakings.register.ColorfulCreakingsCreativeTabItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.creaking.Creaking;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.SubscribeEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.StartTracking;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = Constants.MOD_ID)
public class ColorfulCreakingsEventsNeoForge {
    private static ResourceKey<CreativeModeTab> creativeTabKey(String name) {
        return ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.withDefaultNamespace(name));
    }


    @SubscribeEvent
    private static void onBuildCreativeModeTabContents(BuildCreativeModeTabContentsEvent e) {
        if (e.getTabKey().equals(creativeTabKey("building_blocks"))) {
            ColorfulCreakingsCreativeTabItems.addBeforeOrangeResinBuildingBlocks((itemStack, itemStack2) -> {
                e.insertBefore(itemStack, itemStack2, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            });

            ColorfulCreakingsCreativeTabItems.addAfterOrangeResinBuildingBlocks((itemStack, itemStack2) -> {
                e.insertAfter(itemStack, itemStack2, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            });
        } else if (e.getTabKey().equals(creativeTabKey("ingredients"))) {
            ColorfulCreakingsCreativeTabItems.addBeforeOrangeResinIngredients((itemStack, itemStack2) -> {
                e.insertBefore(itemStack, itemStack2, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            });
            ColorfulCreakingsCreativeTabItems.addAfterOrangeResinIngredients((itemStack, itemStack2) -> {
                e.insertAfter(itemStack, itemStack2, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            });
        } else if (e.getTabKey().equals(creativeTabKey("spawn_eggs"))) {
            ColorfulCreakingsCreativeTabItems.addBeforeCreakingHeart((itemStack, itemStack2) -> {
                e.insertBefore(itemStack, itemStack2, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            });
            ColorfulCreakingsCreativeTabItems.addAfterCreakingHeart((itemStack, itemStack2) -> {
                e.insertAfter(itemStack, itemStack2, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            });
        } else if (e.getTabKey().equals(creativeTabKey("natural_blocks"))) {
            ColorfulCreakingsCreativeTabItems.addBeforeNaturalBlocks((itemStack, itemStack2) -> {
                e.insertBefore(itemStack, itemStack2, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            });
            ColorfulCreakingsCreativeTabItems.addAfterNaturalBlocks((itemStack, itemStack2) -> {
                e.insertAfter(itemStack, itemStack2, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            });
        }
    }

    @EventBusSubscriber(modid = Constants.MOD_ID)
    public static final class ForgeEvents {

        @SubscribeEvent
        private static void onStartTracking(StartTracking e) {
            Entity entity = e.getTarget();
            Player player = e.getEntity();
            if (player instanceof ServerPlayer serverPlayer
                    && entity instanceof Creaking creaking
                    && ColorfulCreakingsAttachments.CREAKING_COLOR.hasAttachment(creaking)) {
                PacketDistributor.sendToPlayer(serverPlayer,
                        new CreakingColorS2CPacket(creaking.getId(),
                                ColorfulCreakingsAttachments.CREAKING_COLOR.getAttachment(
                                        creaking)));
            }
        }
    }
}