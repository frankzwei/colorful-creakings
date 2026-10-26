package me.ultrusmods.colorfulcreakings.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.ultrusmods.colorfulcreakings.block.ColoredResinBlocks;
import me.ultrusmods.colorfulcreakings.block.ColoredResinBlocks.ColoredResinBlockSet;
import me.ultrusmods.colorfulcreakings.config.NaturalHeartConfig;
import me.ultrusmods.colorfulcreakings.data.CreakingColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CreakingHeartBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.treedecorators.CreakingHeartDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CreakingHeartDecorator.class)
public abstract class CreakingHeartDecoratorMixin {

    @WrapOperation(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/feature/treedecorators/TreeDecorator$Context;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"))
    private void colorfulcreakings$chooseNaturalHeart(TreeDecorator.Context context, BlockPos pos, BlockState state, Operation<Void> original) {
        if (state.is(Blocks.CREAKING_HEART) && context.level().getBiome(pos).is(Biomes.PALE_GARDEN)) {
            CreakingColor color = NaturalHeartConfig.choose(context.random());
            if (color != CreakingColor.ORANGE) {
                ColoredResinBlockSet blockSet = ColoredResinBlocks.BLOCK_SET_MAP.get(color);
                if (blockSet != null) {
                    state = blockSet.creakingHeart().defaultBlockState()
                            .setValue(CreakingHeartBlock.AXIS, state.getValue(CreakingHeartBlock.AXIS))
                            .setValue(CreakingHeartBlock.STATE, state.getValue(CreakingHeartBlock.STATE))
                            .setValue(CreakingHeartBlock.NATURAL, state.getValue(CreakingHeartBlock.NATURAL));
                }
            }
        }

        original.call(context, pos, state);
    }
}