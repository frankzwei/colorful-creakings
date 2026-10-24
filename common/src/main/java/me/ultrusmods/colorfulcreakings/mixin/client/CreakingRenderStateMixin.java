package me.ultrusmods.colorfulcreakings.mixin.client;

import me.ultrusmods.colorfulcreakings.client.CreakingAdditionalRenderState;
import me.ultrusmods.colorfulcreakings.data.CreakingColor;
import net.minecraft.client.renderer.entity.state.CreakingRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(CreakingRenderState.class)
public class CreakingRenderStateMixin implements CreakingAdditionalRenderState {

    @Unique
    private CreakingColor colorfulCreakingsMod$creakingColor = CreakingColor.ORANGE;
    @Unique
    private boolean colorfulCreakingsMod$jeb = false;

    @Override
    public CreakingColor getCreakingColor() {
        return this.colorfulCreakingsMod$creakingColor;
    }

    @Override
    public void setCreakingColor(CreakingColor creakingColor) {
        this.colorfulCreakingsMod$creakingColor = creakingColor;
    }

    @Override
    public boolean isJeb() {
        return this.colorfulCreakingsMod$jeb;
    }

    @Override
    public void setJeb(boolean jeb) {
        this.colorfulCreakingsMod$jeb = jeb;
    }
}