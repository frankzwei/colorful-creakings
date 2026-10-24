package me.ultrusmods.colorfulcreakings.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import me.ultrusmods.colorfulcreakings.block.ColoredCreakingHeartBlock;
import me.ultrusmods.colorfulcreakings.block.ColoredResinBlocks;
import me.ultrusmods.colorfulcreakings.network.CreakingColorS2CPacket;
import me.ultrusmods.colorfulcreakings.register.ColorfulCreakingsAttachments;
import me.ultrusmods.colorfulcreakings.tag.ColorfulCreakingsBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.creaking.Creaking;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.CreakingHeartBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

@Mixin(CreakingHeartBlockEntity.class)
public abstract class CreakingHeartBlockEntityMixin extends BlockEntity {

    public CreakingHeartBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Inject(method = "spawnProtector", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;gameEvent(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/Holder;Lnet/minecraft/world/phys/Vec3;)V"))
    private static void changeCreakingColor(ServerLevel level,
            CreakingHeartBlockEntity creakingHeart, CallbackInfoReturnable<Creaking> cir,
            @Local Creaking creaking) {
        if (creakingHeart.getBlockState().getBlock()
                instanceof ColoredCreakingHeartBlock coloredCreakingHeartBlock) {
            ColorfulCreakingsAttachments.CREAKING_COLOR.setAttachment(creaking,
                    coloredCreakingHeartBlock.getColor());
            ChunkSource chunkSource = level.getChunkSource();
            if (chunkSource instanceof ServerChunkCache chunkCache) {
                chunkCache.sendToTrackingPlayersAndSelf(creaking,
                        new ClientboundCustomPayloadPacket(new CreakingColorS2CPacket(
                                creaking.getId(), coloredCreakingHeartBlock.getColor())));
            }
        }
    }

    // Preserve vanilla 26.3 resin traversal. Scope the heart's resin color to
    // the existing traversal callback, which is now compiled as a static lambda.
    @Unique
    private static final ThreadLocal<Block> colorfulcreakings$resinClump = new ThreadLocal<>();

    @WrapOperation(
            method = "spreadResin",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;breadthFirstTraversal(Lnet/minecraft/core/BlockPos;IILjava/util/function/BiConsumer;Ljava/util/function/Function;)I")
    )
    private int colorfulcreakings$withHeartResinColor(
            BlockPos startPos, int maxDepth, int maxCount,
            BiConsumer<BlockPos, Consumer<BlockPos>> neighbourProvider,
            Function<BlockPos, BlockPos.TraversalNodeStatus> nodeProcessor,
            Operation<Integer> original) {
        Block previous = colorfulcreakings$resinClump.get();
        try {
            if (this.getBlockState().getBlock() instanceof ColoredCreakingHeartBlock coloredHeart) {
                colorfulcreakings$resinClump.set(
                        ColoredResinBlocks.BLOCK_SET_MAP.get(coloredHeart.getColor()).clump());
            } else {
                colorfulcreakings$resinClump.remove();
            }
            return original.call(startPos, maxDepth, maxCount, neighbourProvider, nodeProcessor);
        } finally {
            if (previous != null) {
                colorfulcreakings$resinClump.set(previous);
            } else {
                colorfulcreakings$resinClump.remove();
            }
        }
    }

    @ModifyExpressionValue(
            method = "lambda$spreadResin$1",
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/Blocks;RESIN_CLUMP:Lnet/minecraft/world/level/block/Block;")
    )
    private static Block colorfulcreakings$useColoredResin(Block original) {
        Block colored = colorfulcreakings$resinClump.get();
        return colored != null ? colored : original;
    }

    @ModifyExpressionValue(
            method = "lambda$spreadResin$1",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z", ordinal = 1)
    )
    private static boolean allowAllResinClumps(boolean original, @Local BlockState state) {
        return original || state.is(ColorfulCreakingsBlockTags.RESIN_CLUMP);
    }

    @Inject(method = "emitParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/creaking/Creaking;getBoundingBox()Lnet/minecraft/world/phys/AABB;"))
    private void changeTrailColor(ServerLevel level, int count, boolean reverseDirection, CallbackInfo ci, @Local(ordinal = 1) LocalIntRef i) {
        if (this.getBlockState().getBlock() instanceof ColoredCreakingHeartBlock coloredCreakingHeartBlock) {
            if (i.get() == 0xFC7812) { // Checking if it's the orange trail
                i.set(coloredCreakingHeartBlock.getColor().getDyeColor().getTextureDiffuseColor());
            }
        }

    }
}