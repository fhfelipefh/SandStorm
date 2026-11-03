package com.fhfelipefh.sandstorm.client.compat.jei.category;

import com.fhfelipefh.sandstorm.client.compat.jei.SandStormJeiRecipeTypes;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.network.chat.Component;

public class MaterialDeconstructorCategory extends AbstractMachineCategory {
    public MaterialDeconstructorCategory(IGuiHelper guiHelper) {
        super(
                SandStormJeiRecipeTypes.MATERIAL_DECONSTRUCTOR,
                Component.translatable("block.sandstorm.material_deconstructor"),
                guiHelper.createDrawableItemStack(SandStormBlocks.MATERIAL_DECONSTRUCTOR.asItem().getDefaultInstance()),
                124,
                70
        );
    }
}

