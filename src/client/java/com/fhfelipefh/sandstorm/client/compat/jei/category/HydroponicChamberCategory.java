package com.fhfelipefh.sandstorm.client.compat.jei.category;

import com.fhfelipefh.sandstorm.client.compat.jei.SandStormJeiRecipeTypes;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.network.chat.Component;

public class HydroponicChamberCategory extends AbstractMachineCategory {
    public HydroponicChamberCategory(IGuiHelper guiHelper) {
        super(
                SandStormJeiRecipeTypes.HYDROPONIC_CHAMBER,
                Component.translatable("block.sandstorm.hydroponic_chamber"),
                guiHelper.createDrawableItemLike(SandStormBlocks.HYDROPONIC_CHAMBER),
                124,
                72
        );
    }
}

