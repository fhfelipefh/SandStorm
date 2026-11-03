package com.fhfelipefh.sandstorm.client.compat.jei.category;

import com.fhfelipefh.sandstorm.client.compat.jei.SandStormJeiRecipeTypes;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.network.chat.Component;

public class DesalinationFilterCategory extends AbstractMachineCategory {
    public DesalinationFilterCategory(IGuiHelper guiHelper) {
        super(
                SandStormJeiRecipeTypes.DESALINATION_FILTER,
                Component.translatable("block.sandstorm.desalination_filter"),
                guiHelper.createDrawableItemLike(SandStormBlocks.DESALINATION_FILTER),
                124,
                72
        );
    }
}

