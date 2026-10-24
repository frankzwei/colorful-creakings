package me.ultrusmods.colorfulcreakings.data;

import io.netty.buffer.ByteBuf;
import me.ultrusmods.colorfulcreakings.Constants;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.DyeColor;

import java.util.function.IntFunction;

public enum CreakingColor implements StringRepresentable {

    WHITE(0, "white", DyeColor.WHITE, Constants.id("textures/entity/creaking/white.png"), Constants.id("textures/entity/creaking/white_eyes.png")),
    LIGHT_GRAY(1, "light_gray", DyeColor.LIGHT_GRAY, Constants.id("textures/entity/creaking/light_gray.png"), Constants.id("textures/entity/creaking/light_gray_eyes.png")),
    GRAY(2, "gray", DyeColor.GRAY, Constants.id("textures/entity/creaking/gray.png"), Constants.id("textures/entity/creaking/gray_eyes.png")),
    BLACK(3, "black", DyeColor.BLACK, Constants.id("textures/entity/creaking/black.png"), Constants.id("textures/entity/creaking/black_eyes.png")),
    BROWN(4, "brown", DyeColor.BROWN, Constants.id("textures/entity/creaking/brown.png"), Constants.id("textures/entity/creaking/brown_eyes.png")),
    RED(5, "red", DyeColor.RED, Constants.id("textures/entity/creaking/red.png"), Constants.id("textures/entity/creaking/red_eyes.png")),
    ORANGE(6, "orange", DyeColor.ORANGE, Identifier.withDefaultNamespace("textures/entity/creaking/creaking.png"), Identifier.withDefaultNamespace("textures/entity/creaking/creaking_eyes.png")),
    YELLOW(7, "yellow", DyeColor.YELLOW, Constants.id("textures/entity/creaking/yellow.png"), Constants.id("textures/entity/creaking/yellow_eyes.png")),
    LIME(8, "lime", DyeColor.LIME, Constants.id("textures/entity/creaking/lime.png"), Constants.id("textures/entity/creaking/lime_eyes.png")),
    GREEN(9, "green", DyeColor.GREEN, Constants.id("textures/entity/creaking/green.png"), Constants.id("textures/entity/creaking/green_eyes.png")),
    CYAN(10, "cyan", DyeColor.CYAN, Constants.id("textures/entity/creaking/cyan.png"), Constants.id("textures/entity/creaking/cyan_eyes.png")),
    LIGHT_BLUE(11, "light_blue", DyeColor.LIGHT_BLUE, Constants.id("textures/entity/creaking/light_blue.png"), Constants.id("textures/entity/creaking/light_blue_eyes.png")),
    BLUE(12, "blue", DyeColor.BLUE, Constants.id("textures/entity/creaking/blue.png"), Constants.id("textures/entity/creaking/blue_eyes.png")),
    PURPLE(13, "purple", DyeColor.PURPLE, Constants.id("textures/entity/creaking/purple.png"), Constants.id("textures/entity/creaking/purple_eyes.png")),
    MAGENTA(14, "magenta", DyeColor.MAGENTA, Constants.id("textures/entity/creaking/magenta.png"), Constants.id("textures/entity/creaking/magenta_eyes.png")),
    PINK(15, "pink", DyeColor.PINK, Constants.id("textures/entity/creaking/pink.png"), Constants.id("textures/entity/creaking/pink_eyes.png"));

    private static final IntFunction<CreakingColor> BY_ID = ByIdMap.continuous(CreakingColor::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StringRepresentable.EnumCodec<CreakingColor> CODEC = StringRepresentable.fromEnum(CreakingColor::values);
    public static final StreamCodec<ByteBuf, CreakingColor> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, CreakingColor::getId);

    private final int id;
    private final String name;
    private final DyeColor dyeColor;
    private final Identifier texture;
    private final Identifier eyesTexture;

    CreakingColor(int id, String name, DyeColor dyeColor, Identifier texture, Identifier eyesTexture) {
        this.id = id;
        this.name = name;
        this.dyeColor = dyeColor;
        this.texture = texture;
        this.eyesTexture = eyesTexture;
    }

    public int getId() {
        return this.id;
    }

    public DyeColor getDyeColor() {
        return this.dyeColor;
    }

    public Identifier getTexture() {
        return this.texture;
    }

    public Identifier getEyesTexture() {
        return this.eyesTexture;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}