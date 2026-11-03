package com.fhfelipefh.sandstorm.client.compat.jei.category;

import com.fhfelipefh.sandstorm.client.compat.jei.SandStormJeiRecipeTypes;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.network.chat.Component;

public class Printer3DCategory extends AbstractMachineCategory {
    public Printer3DCategory(IGuiHelper guiHelper) {
        super(
                SandStormJeiRecipeTypes.PRINTER_3D,
                Component.translatable("block.sandstorm.printer_3d"),
                guiHelper.createDrawableItemLike(SandStormBlocks.PRINTER_3D),
                124,
                72
        );
    }
}

