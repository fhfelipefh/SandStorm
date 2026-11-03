package com.fhfelipefh.sandstorm.client.compat.jei.category;

import com.fhfelipefh.sandstorm.client.compat.jei.SandStormJeiRecipeTypes;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.network.chat.Component;

public class MegastructureConstructorCategory extends AbstractMachineCategory {
    public MegastructureConstructorCategory(IGuiHelper guiHelper) {
        super(
                SandStormJeiRecipeTypes.MEGASTRUCTURE_CONSTRUCTOR,
                Component.translatable("block.sandstorm.megastructure_constructor"),
                guiHelper.createDrawableItemLike(SandStormBlocks.MEGASTRUCTURE_CONSTRUCTOR),
                124,
                72
        );
    }
}

