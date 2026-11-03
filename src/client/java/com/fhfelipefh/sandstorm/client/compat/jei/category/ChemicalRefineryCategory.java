package com.fhfelipefh.sandstorm.client.compat.jei.category;

import com.fhfelipefh.sandstorm.client.compat.jei.SandStormJeiRecipeTypes;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.network.chat.Component;

public class ChemicalRefineryCategory extends AbstractMachineCategory {
    public ChemicalRefineryCategory(IGuiHelper guiHelper) {
        super(
                SandStormJeiRecipeTypes.CHEMICAL_REFINERY,
                Component.translatable("block.sandstorm.chemical_refinery"),
                guiHelper.createDrawableItemLike(SandStormBlocks.CHEMICAL_REFINERY),
                124,
                72
        );
    }
}

