package me.ultrusmods.colorfulcreakings.client;


import me.ultrusmods.colorfulcreakings.data.CreakingColor;

public interface CreakingAdditionalRenderState {
    CreakingColor getCreakingColor();
    void setCreakingColor(CreakingColor creakingColor);
    boolean isJeb();
    void setJeb(boolean jeb);
}