package me.ultrusmods.colorfulcreakings.mixin.client;

import me.ultrusmods.colorfulcreakings.client.ColoredCreakingEyesRenderer;
import me.ultrusmods.colorfulcreakings.client.CreakingAdditionalRenderState;
import me.ultrusmods.colorfulcreakings.platform.Services;
import me.ultrusmods.colorfulcreakings.register.ColorfulCreakingsAttachments;
import net.minecraft.client.model.monster.creaking.CreakingModel;
import net.minecraft.client.renderer.entity.CreakingRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.CreakingRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.monster.creaking.Creaking;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreakingRenderer.class)
public abstract class CreakingRendererMixin<T extends Creaking> extends MobRenderer<T, CreakingRenderState, CreakingModel> {

    @Shadow @Final private static Identifier EYES_TEXTURE_LOCATION;

    public CreakingRendererMixin(EntityRendererProvider.Context context, CreakingModel model, float shadow) {
        super(context, model, shadow);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void changeCreakingRenderingFeatures(EntityRendererProvider.Context context,
            CallbackInfo callback) {
        this.layers.removeFirst();
        if (Services.PLATFORM.getPlatformName().equals("Fabric")) {
            this.addLayer(new ColoredCreakingEyesRenderer(this, RenderTypes::eyes, context.getModelSet()));
        }
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/monster/creaking/Creaking;Lnet/minecraft/client/renderer/entity/state/CreakingRenderState;F)V"
            , at = @At("TAIL"))
    public void addColorCreakingRenderState(T entity, CreakingRenderState state,
            float partialTicks, CallbackInfo callback) {
        CreakingAdditionalRenderState additional = (CreakingAdditionalRenderState) state;
        additional.setCreakingColor(
                ColorfulCreakingsAttachments.CREAKING_COLOR.getAttachment(entity));
        additional.setJeb(entity.getCustomName() != null
                && entity.getCustomName().getString().equals("jeb_"));
    }
}