package me.ultrusmods.colorfulcreakings.datagen;

import me.ultrusmods.colorfulcreakings.Constants;
import me.ultrusmods.colorfulcreakings.block.ColoredResinBlocks;
import me.ultrusmods.colorfulcreakings.data.CreakingColor;
import me.ultrusmods.colorfulcreakings.item.ColoredResinItems;
import me.ultrusmods.colorfulcreakings.tag.ColorfulCreakingsBlockTags;
import me.ultrusmods.colorfulcreakings.tag.ColorfulCreakingsItemTags;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator.Pack;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.*;
import net.minecraft.client.data.models.model.*;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.tags.BlockItemTagAppender;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CreakingHeartBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.CreakingHeartState;

import java.util.EnumMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class ColorfulCreakingsDataGenerator implements DataGeneratorEntrypoint {

    public static EnumMap<CreakingColor, BlockFamily> BLOCK_FAMILIES
            = new EnumMap<>(CreakingColor.class);

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        Pack pack = generator.createPack();
        pack.addProvider(ColorfulCreakingsModelProvider::new);
        pack.addProvider(ColorfulCreakingsBlockTagProvider::new);
        pack.addProvider(ColorfulCreakingsItemTagProvider::new);
        pack.addProvider(ColorfulCreakingsRecipeProviderRunner::new);
    }

    public static final class ColorfulCreakingsModelProvider extends FabricModelProvider {

        public ColorfulCreakingsModelProvider(FabricPackOutput output) {
            super(output);
        }

        @Override
        public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {
            ColoredResinBlocks.BLOCK_SET_MAP.forEach((creakingColor, blocks) -> {
                if (creakingColor == CreakingColor.ORANGE) {
                    return;
                }

                blockModelGenerators.createMultifaceBlockStates(blocks.clump());
                blockModelGenerators.createTrivialCube(blocks.baseBlock());
                BlockFamily blockFamily = BlockFamilies.familyBuilder(blocks.bricks())
                        .stairs(blocks.brickStairs())
                        .slab(blocks.brickSlab())
                        .wall(blocks.brickWall())
                        .chiseled(blocks.chiseledBricks())
                        .getFamily();
                blockModelGenerators.family(blocks.bricks()).generateFor(blockFamily);
                BLOCK_FAMILIES.put(creakingColor, blockFamily);

                generateCreakingHeartBlockModel(blocks.creakingHeart(), blockModelGenerators);
            });
        }

        public void generateCreakingHeartBlockModel(Block block, BlockModelGenerators generators) {
            if (block == Blocks.CREAKING_HEART) {
                Constants.LOG.error("Creaking heart block is wrong");
            }

            Function<TexturedModel.Provider, Identifier> function = provider -> provider
                    .updateTexture(mapping -> mapping.put(
                            TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "_active")))
                    .updateTexture(mapping -> mapping.put(
                            TextureSlot.END, TextureMapping.getBlockTexture(block, "_top_active")))
                    .createWithSuffix(block, "_active", generators.modelOutput);

            Function<TexturedModel.Provider, Identifier> regular = provider -> provider
                    .updateTexture(mapping -> mapping.put(
                            TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "_active")))
                    .updateTexture(mapping -> mapping.put(
                            TextureSlot.END, TextureMapping.getBlockTexture(block, "_top_active")))
                    .create(block, generators.modelOutput);
            regular.apply(TexturedModel.COLUMN_ALT);
            regular.apply(TexturedModel.COLUMN_HORIZONTAL_ALT);

            Identifier topActive = function.apply(TexturedModel.COLUMN_ALT);
            Identifier sideActive = function.apply(TexturedModel.COLUMN_HORIZONTAL_ALT);
            Identifier topNormal = Identifier.withDefaultNamespace("block/creaking_heart");
            Identifier sideNormal = Identifier.withDefaultNamespace("block/creaking_heart_horizontal");

            MultiVariant topInactiveVariant = BlockModelGenerators.plainVariant(topNormal);
            MultiVariant sideInactiveVariant = BlockModelGenerators.plainVariant(sideNormal)
                    .with(BlockModelGenerators.X_ROT_90);
            MultiVariant sideInactiveXVariant = BlockModelGenerators.plainVariant(sideNormal)
                    .with(BlockModelGenerators.X_ROT_90)
                    .with(BlockModelGenerators.Y_ROT_90);

            MultiVariant topActiveVariant = BlockModelGenerators.plainVariant(topActive);
            MultiVariant sideActiveVariant = BlockModelGenerators.plainVariant(sideActive)
                    .with(BlockModelGenerators.X_ROT_90);
            MultiVariant sideActiveXVariant = BlockModelGenerators.plainVariant(sideActive)
                    .with(BlockModelGenerators.X_ROT_90)
                    .with(BlockModelGenerators.Y_ROT_90);

            generators.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                    .with(PropertyDispatch.initial(BlockStateProperties.AXIS,
                                CreakingHeartBlock.STATE)
                            .select(Direction.Axis.Y, CreakingHeartState.UPROOTED,
                                    topInactiveVariant)
                            .select(Direction.Axis.Z, CreakingHeartState.UPROOTED,
                                    sideInactiveVariant)
                            .select(Direction.Axis.X, CreakingHeartState.UPROOTED,
                                    sideInactiveXVariant)
                            .select(Direction.Axis.Y, CreakingHeartState.DORMANT,
                                    topInactiveVariant)
                            .select(Direction.Axis.Z, CreakingHeartState.DORMANT,
                                    sideInactiveVariant)
                            .select(Direction.Axis.X, CreakingHeartState.DORMANT,
                                    sideInactiveXVariant)
                            .select(Direction.Axis.Y, CreakingHeartState.AWAKE,
                                    topActiveVariant)
                            .select(Direction.Axis.Z, CreakingHeartState.AWAKE,
                                    sideActiveVariant)
                            .select(Direction.Axis.X, CreakingHeartState.AWAKE,
                                    sideActiveXVariant)));
        }

        @Override
        public void generateItemModels(ItemModelGenerators itemModelGenerators) {
            ColoredResinItems.ITEM_SET_MAP.forEach((creakingColor, itemSet) -> {
                if (creakingColor == CreakingColor.ORANGE) {
                    return;
                }

                itemModelGenerators.generateFlatItem(itemSet.brick(), ModelTemplates.FLAT_ITEM);
                itemModelGenerators.generateFlatItem(itemSet.clump(), ModelTemplates.FLAT_ITEM);
            });
        }
    }

    public static final class ColorfulCreakingsBlockTagProvider
            extends FabricTagsProvider.BlockTagsProvider {

        public ColorfulCreakingsBlockTagProvider(FabricPackOutput output,
                CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            BlockItemTagAppender<Block> stairs = this.builder(BlockTags.STAIRS);
            BlockItemTagAppender<Block> slabs = this.builder(BlockTags.SLABS);
            BlockItemTagAppender<Block> walls = this.builder(BlockTags.WALLS);
            BlockItemTagAppender<Block> combinationStepSound
                    = this.builder(BlockTags.COMBINATION_STEP_SOUND_BLOCKS);
            BlockItemTagAppender<Block> pickaxe = this.builder(BlockTags.MINEABLE_WITH_PICKAXE);
            BlockItemTagAppender<Block> axe = this.builder(BlockTags.MINEABLE_WITH_AXE);
            BlockItemTagAppender<Block> resinClumps
                    = this.builder(ColorfulCreakingsBlockTags.RESIN_CLUMP);
            BlockItemTagAppender<Block> storage
                    = this.builder(ConventionalBlockTags.STORAGE_BLOCKS_RESIN);
            BlockItemTagAppender<Block> replaceable = this.builder(BlockTags.REPLACEABLE);

            ColoredResinBlocks.BLOCK_SET_MAP.forEach((creakingColor, blocks) -> {
                if (creakingColor == CreakingColor.ORANGE) {
                    return;
                }

                stairs.add(blockKey(blocks.brickStairs()));
                slabs.add(blockKey(blocks.brickSlab()));
                walls.add(blockKey(blocks.brickWall()));
                combinationStepSound.add(blockKey(blocks.clump()));
                pickaxe.add(blockKey(blocks.bricks()), blockKey(blocks.brickStairs()),
                        blockKey(blocks.brickSlab()), blockKey(blocks.brickWall()),
                        blockKey(blocks.chiseledBricks()));
                axe.add(blockKey(blocks.creakingHeart()));
                storage.add(blockKey(blocks.baseBlock()));
                replaceable.add(blockKey(blocks.clump()));
            });

            ColoredResinBlocks.BLOCK_SET_MAP.forEach((creakingColor, blocks) -> {
                resinClumps.add(blockKey(blocks.clump()));
            });

        }

        private static ResourceKey<Block> blockKey(Block block) {
            return BuiltInRegistries.BLOCK.getResourceKey(block).orElseThrow();
        }
    }

    public static final class ColorfulCreakingsItemTagProvider
                extends FabricTagsProvider.ItemTagsProvider {

        public ColorfulCreakingsItemTagProvider(FabricPackOutput output,
                CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            BlockItemTagAppender<Item> clumps = this.builder(ColorfulCreakingsItemTags.RESIN_CLUMPS);
            BlockItemTagAppender<Item> bricks = this.builder(ColorfulCreakingsItemTags.RESIN_BRICKS);
            BlockItemTagAppender<Item> storage = this.builder(ConventionalItemTags.STORAGE_BLOCKS_RESIN);
            BlockItemTagAppender<Item> resinClumps = this.builder(ConventionalItemTags.RESIN_CLUMPS);
            BlockItemTagAppender<Item> resinBricks = this.builder(ConventionalItemTags.RESIN_BRICKS);

            resinClumps.addTag(ColorfulCreakingsItemTags.RESIN_CLUMPS);
            resinBricks.addTag(ColorfulCreakingsItemTags.RESIN_BRICKS);
            ColoredResinItems.ITEM_SET_MAP.forEach((creakingColor, itemSet) -> {
                clumps.add(itemKey(itemSet.clump()));
                bricks.add(itemKey(itemSet.brick()));
                storage.add(itemKey(itemSet.baseBlock()));
            });
        }

        private static ResourceKey<Item> itemKey(Item item) {
            return BuiltInRegistries.ITEM.getResourceKey(item).orElseThrow();
        }
    }

    public static final class ColorfulCreakingsRecipeProviderRunner extends FabricRecipeProvider {

        public ColorfulCreakingsRecipeProviderRunner(FabricPackOutput output,
                CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected RecipeProvider createRecipeProvider(Provider registries,
                BootstrapContext<Recipe<?>> recipes, BootstrapContext<Advancement> advancements) {
            return new ColorfulCreakingsRecipeProvider(recipes, advancements);
        }

        @Override
        public String getName() {
            return "colorfulcreakings:recipes";
        }

        private static class ColorfulCreakingsRecipeProvider extends RecipeProvider {

            public ColorfulCreakingsRecipeProvider(BootstrapContext<Recipe<?>> recipeOutput,
                    BootstrapContext<Advancement> advancementOutput) {
                super(recipeOutput, advancementOutput);
            }

            @Override
            public void buildRecipes() {
                ColoredResinItems.ITEM_SET_MAP.forEach((creakingColor, itemSet) -> {
                    if (creakingColor == CreakingColor.ORANGE) {
                        return;
                    }

                    SimpleCookingRecipeBuilder
                            .smelting(Ingredient.of(itemSet.clump()), RecipeCategory.MISC,
                                    CookingBookCategory.MISC, itemSet.brick(), 0.1F, 200)
                            .unlockedBy("has_resin_clump", this.has(itemSet.clump()))
                            .save(this.output);

                    this.stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS,
                            itemSet.brickSlab(), itemSet.bricks(), 2);
                    this.stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS,
                            itemSet.brickStairs(), itemSet.bricks());
                    this.stonecutterResultFromBase(RecipeCategory.DECORATIONS,
                            itemSet.brickWall(), itemSet.bricks());
                    this.stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS,
                            itemSet.chiseledBricks(), itemSet.bricks());

                    this.twoByTwoPacker(RecipeCategory.BUILDING_BLOCKS, itemSet.bricks(),
                            itemSet.brick());
                    this.nineBlockStorageRecipes(RecipeCategory.MISC, itemSet.clump(),
                            RecipeCategory.BUILDING_BLOCKS, itemSet.baseBlock());
                    this.shaped(RecipeCategory.MISC, itemSet.creakingHeart())
                            .define('R', itemSet.baseBlock())
                            .define('L', Blocks.PALE_OAK_LOG)
                            .pattern(" L ")
                            .pattern(" R ")
                            .pattern(" L ")
                            .unlockedBy("has_resin_block", this.has(itemSet.baseBlock()))
                            .save(this.output);

                    TagKey<Item> dyeTag = TagKey.create(Registries.ITEM,
                            Identifier.fromNamespaceAndPath("c", "dyes/"
                                    + creakingColor.getSerializedName()));

                    this.shaped(RecipeCategory.MISC, itemSet.brick(), 8)
                            .define('#', ColorfulCreakingsItemTags.RESIN_BRICKS)
                            .define('X', dyeTag)
                            .pattern("###")
                            .pattern("#X#").pattern("###")
                            .group("dyed_resin_brick")
                            .unlockedBy("has_resin_brick",
                                    this.has(ColorfulCreakingsItemTags.RESIN_BRICKS))
                            .save(this.output,
                                    BuiltInRegistries.ITEM.getKey(itemSet.brick()).getPath()
                                            + "_dyeing");
                    this.shaped(RecipeCategory.MISC, itemSet.clump(), 8)
                            .define('#', ColorfulCreakingsItemTags.RESIN_CLUMPS)
                            .define('X', dyeTag)
                            .pattern("###")
                            .pattern("#X#")
                            .pattern("###")
                            .group("dyed_resin")
                            .unlockedBy("has_resin",
                                    this.has(ColorfulCreakingsItemTags.RESIN_CLUMPS))
                            .save(this.output,
                                    BuiltInRegistries.ITEM.getKey(itemSet.clump()).getPath()
                                            + "_dyeing");

                    BlockFamily blockFamily = BLOCK_FAMILIES.get(creakingColor);
                    this.generateRecipes(blockFamily, FeatureFlagSet.of(FeatureFlags.VANILLA));
                });
            }
        }
    }
}