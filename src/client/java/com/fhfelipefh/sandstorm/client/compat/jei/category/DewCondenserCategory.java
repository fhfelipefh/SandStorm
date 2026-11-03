package com.fhfelipefh.sandstorm.client.compat.jei.category;

import com.fhfelipefh.sandstorm.client.compat.jei.SandStormJeiRecipeTypes;
import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.recipe.MachineRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.network.chat.Component;

import java.util.List;

public class DewCondenserCategory extends AbstractRecipeCategory<MachineRecipe> {
    public DewCondenserCategory(IGuiHelper guiHelper) {
        super(
                SandStormJeiRecipeTypes.DEW_CONDENSER,
                Component.translatable("block.sandstorm.dew_condenser"),
                guiHelper.createDrawableItemLike(SandStormBlocks.DEW_CONDENSER),
                124,
                72
        );
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MachineRecipe recipe, IFocusGroup focuses) {
        if (recipe.getSlot0InputCount() > 0) {
            builder.addInputSlot(20, 23)
                    .setStandardSlotBackground()
                    .addItemStacks(recipe.getSlot0Inputs());
        }
        builder.addOutputSlot(84, 23)
                .setOutputSlotBackground()
                .add(recipe.getOutput());
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, MachineRecipe recipe, IFocusGroup focuses) {
        int ticks = Math.max(1, recipe.getProcessTicks());
        builder.addAnimatedRecipeArrowWidget(ticks).setPosition(50, 23);
        String info = (ticks / 20) + "s  |  Ambient";
        builder.addText(List.of(Component.literal(info)), 120, 10)
                .setPosition(14, 56)
                .setColor(0xFF78909C);
    }
}

