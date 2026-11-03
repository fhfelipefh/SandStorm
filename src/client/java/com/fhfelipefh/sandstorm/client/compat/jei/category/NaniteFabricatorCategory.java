package com.fhfelipefh.sandstorm.client.compat.jei.category;

import com.fhfelipefh.sandstorm.client.compat.jei.SandStormJeiRecipeTypes;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.network.chat.Component;

public class NaniteFabricatorCategory extends AbstractMachineCategory {
    public NaniteFabricatorCategory(IGuiHelper guiHelper) {
        super(
                SandStormJeiRecipeTypes.NANITE_FABRICATOR,
                Component.translatable("block.sandstorm.nanite_fabricator"),
                guiHelper.createDrawableItemLike(SandStormBlocks.NANITE_FABRICATOR),
                124,
                72
        );
    }
}

