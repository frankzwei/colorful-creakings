package me.ultrusmods.colorfulcreakings.item;

import me.ultrusmods.colorfulcreakings.Constants;
import me.ultrusmods.colorfulcreakings.block.ColoredResinBlocks;
import me.ultrusmods.colorfulcreakings.data.CreakingColor;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.EnumMap;
import java.util.Map;

public class ColoredResinItems {
    public static final Map<CreakingColor, ColoredResinItemSet> ITEM_SET_MAP = new EnumMap<>(CreakingColor.class);


    public record ColoredResinItemSet(CreakingColor color, Item baseBlock, Item bricks, Item brickStairs,
                                      Item brickSlab, Item brickWall, Item chiseledBricks, Item clump, Item brick, Item creakingHeart) {

        public static ColoredResinItemSet create(CreakingColor color) {
            var blockSet = ColoredResinBlocks.BLOCK_SET_MAP.get(color);
            var baseItemProperties = new Item.Properties().useBlockDescriptionPrefix();
            baseItemProperties.setId(resinItemId(color, "resin_block"));
            var brickProperties = new Item.Properties().useBlockDescriptionPrefix();
            brickProperties.setId(resinItemId(color, "resin_bricks"));
            var brickStairsProperties = new Item.Properties().useBlockDescriptionPrefix();
            brickStairsProperties.setId(resinItemId(color, "resin_brick_stairs"));
            var brickSlabProperties = new Item.Properties().useBlockDescriptionPrefix();
            brickSlabProperties.setId(resinItemId(color, "resin_brick_slab"));
            var brickWallProperties = new Item.Properties().useBlockDescriptionPrefix();
            brickWallProperties.setId(resinItemId(color, "resin_brick_wall"));
            var chiseledBricksProperties = new Item.Properties().useBlockDescriptionPrefix();
            chiseledBricksProperties.setId(resinItemId(color, "chiseled_resin_bricks"));
            var clumpProperties = new Item.Properties().useItemDescriptionPrefix();
            clumpProperties.setId(resinItemId(color, "resin_clump"));
            var brickItemProperties = new Item.Properties();
            brickItemProperties.setId(resinItemId(color, "resin_brick"));
            var creakingHeartProperties = new Item.Properties();
            creakingHeartProperties.setId(resinItemId(color, "creaking_heart"));

            ColoredResinItemSet coloredResinItemSet = new ColoredResinItemSet(color,
                    new BlockItem(blockSet.baseBlock(), baseItemProperties),
                    new BlockItem(blockSet.bricks(), brickProperties),
                    new BlockItem(blockSet.brickStairs(), brickStairsProperties),
                    new BlockItem(blockSet.brickSlab(), brickSlabProperties),
                    new BlockItem(blockSet.brickWall(), brickWallProperties),
                    new BlockItem(blockSet.chiseledBricks(), chiseledBricksProperties),
                    new BlockItem(blockSet.clump(), clumpProperties.useItemDescriptionPrefix()),
                    new Item(brickItemProperties),
                    new BlockItem(blockSet.creakingHeart(), creakingHeartProperties));
            ITEM_SET_MAP.put(color, coloredResinItemSet);
            return coloredResinItemSet;
        }

        public static ColoredResinItemSet createOrangeBase() {
            ColoredResinItemSet coloredResinItemSet = new ColoredResinItemSet(
                    CreakingColor.ORANGE,
                    Items.RESIN_BLOCK,
                    Items.RESIN_BRICKS,
                    Items.RESIN_BRICK_STAIRS,
                    Items.RESIN_BRICK_SLAB,
                    Items.RESIN_BRICK_WALL,
                    Items.CHISELED_RESIN_BRICKS,
                    Items.RESIN_CLUMP,
                    Items.RESIN_BRICK,
                    Items.CREAKING_HEART
            );
            ITEM_SET_MAP.put(CreakingColor.ORANGE, coloredResinItemSet);
            return coloredResinItemSet;
        }

        public void register() {
            Registry.register(BuiltInRegistries.ITEM, resinItemId(color, "resin_block"), baseBlock);
            Registry.register(BuiltInRegistries.ITEM, resinItemId(color, "resin_bricks"), bricks);
            Registry.register(BuiltInRegistries.ITEM, resinItemId(color, "resin_brick_stairs"), brickStairs);
            Registry.register(BuiltInRegistries.ITEM, resinItemId(color, "resin_brick_slab"), brickSlab);
            Registry.register(BuiltInRegistries.ITEM, resinItemId(color, "resin_brick_wall"), brickWall);
            Registry.register(BuiltInRegistries.ITEM, resinItemId(color, "chiseled_resin_bricks"), chiseledBricks);
            Registry.register(BuiltInRegistries.ITEM, resinItemId(color, "resin_clump"), clump);
            Registry.register(BuiltInRegistries.ITEM, resinItemId(color, "resin_brick"), brick);
            Registry.register(BuiltInRegistries.ITEM, resinItemId(color, "creaking_heart"), creakingHeart);
        }

    }


    private static ResourceKey<Item> resinItemId(CreakingColor color, String name) {
        return ResourceKey.create(Registries.ITEM, Constants.id(color.getSerializedName() + "_" + name));
    }
}
