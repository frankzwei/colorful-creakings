package me.ultrusmods.colorfulcreakings.client;

import java.util.function.Function;

import com.mojang.blaze3d.vertex.PoseStack;
import me.ultrusmods.colorfulcreakings.data.CreakingColor;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.monster.creaking.CreakingModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.CreakingRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;

public class ColoredCreakingEyesRenderer extends RenderLayer<CreakingRenderState, CreakingModel> {

    private final Function<Identifier, RenderType> bufferProvider;
    private final CreakingModel model;

    public ColoredCreakingEyesRenderer(
            RenderLayerParent<CreakingRenderState, CreakingModel> renderer,
            Function<Identifier, RenderType> bufferProvider,
            EntityModelSet modelSet) {
        super(renderer);
        this.bufferProvider = bufferProvider;
        this.model = new CreakingModel(modelSet.bakeLayer(
                ColorfulCreakingsModelLayers.COLORED_CREAKING_EYES));
    }

    public static int getTextureColor(CreakingRenderState state) {
        if (!((CreakingAdditionalRenderState) state).isJeb()) {
            return 0xFFFFFFFF;
        }

        int k = Mth.floor(state.ageInTicks);
        int i1 = DyeColor.values().length;

        float f = ((float) (k % 25) + Mth.frac(state.ageInTicks)) / 25.0F;

        int l = k / 25;
        int j1 = l % i1;
        int l1 = DyeColor.byId(j1).getTextureDiffuseColor();

        int k1 = (l + 1) % i1;
        int i2 = DyeColor.byId(k1).getTextureDiffuseColor();

        return ARGB.srgbLerp(f, l1, i2);
    }

    public Identifier getTexture(CreakingRenderState state) {
        if (((CreakingAdditionalRenderState) state).isJeb()) {
            return CreakingColor.WHITE.getEyesTexture();
        }

        return ((CreakingAdditionalRenderState)state).getCreakingColor().getEyesTexture();
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords,
            CreakingRenderState state, float yRot, float xRot) {
        Identifier texture = this.getTexture(state);
        int color = getTextureColor(state);

        this.model.setupAnim(state);
        if (state.eyesGlowing) {
            collector.order(1).submitModel(this.model, state, poseStack,
                    this.bufferProvider.apply(texture), lightCoords,
                    LivingEntityRenderer.getOverlayCoords(state, 0.0F), color,
                    null, state.outlineColor);
        } else {
            coloredCutoutModelCopyLayerRender(this.model, texture, poseStack, collector, lightCoords, state, color, 1);
        }
    }
}