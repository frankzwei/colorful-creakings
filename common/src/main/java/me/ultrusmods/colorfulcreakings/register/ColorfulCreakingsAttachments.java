package me.ultrusmods.colorfulcreakings.register;

import me.ultrusmods.colorfulcreakings.Constants;
import me.ultrusmods.colorfulcreakings.attachment.CommonAttachment;
import me.ultrusmods.colorfulcreakings.data.CreakingColor;
import me.ultrusmods.colorfulcreakings.platform.Services;

public class ColorfulCreakingsAttachments {
    public static CommonAttachment<CreakingColor> CREAKING_COLOR = CommonAttachment.<CreakingColor>builder(Constants.id("creaking_color"))
            .codec(CreakingColor.CODEC)
            .defaultValue(() -> CreakingColor.ORANGE)
            .build();

    public static void register() {
        Services.PLATFORM.registerAttachmentType(CREAKING_COLOR);
    }
}