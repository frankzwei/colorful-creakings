package me.ultrusmods.colorfulcreakings;


import me.ultrusmods.colorfulcreakings.network.CreakingColorS2CPacket;
import me.ultrusmods.colorfulcreakings.register.ColorfulCreakingBlocks;
import me.ultrusmods.colorfulcreakings.register.ColorfulCreakingsAttachments;
import me.ultrusmods.colorfulcreakings.register.ColorfulCreakingsBlockEntities;
import me.ultrusmods.colorfulcreakings.register.ColorfulCreakingsItems;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(Constants.MOD_ID)
public class ColorfulCreakingsModNeoForge {

    public ColorfulCreakingsModNeoForge(IEventBus eventBus) {
        ColorfulCreakingsMod.init();
        eventBus.addListener(this::onRegisterEvent);
        eventBus.addListener(this::registerPacket);
    }

    private void onRegisterEvent(RegisterEvent event) {
        if (event.getRegistryKey() == Registries.BLOCK) {
            ColorfulCreakingBlocks.register();
        } else if (event.getRegistryKey() == Registries.ITEM) {
            ColorfulCreakingsItems.register();
        } else if (event.getRegistryKey() == Registries.BLOCK_ENTITY_TYPE) {
            ColorfulCreakingsBlockEntities.register();
        } else if (event.getRegistryKey() == NeoForgeRegistries.Keys.ATTACHMENT_TYPES) {
            ColorfulCreakingsAttachments.register();
        }
    }

    private void registerPacket(RegisterPayloadHandlersEvent event) {
        event.registrar(Constants.MOD_ID)
                .versioned("1.0.0")
                .playToClient(CreakingColorS2CPacket.TYPE, CreakingColorS2CPacket.STREAM_CODEC,
                        (packet, context) -> packet.handle());
    }
}