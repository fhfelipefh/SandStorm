package com.fhfelipefh.sandstorm.client.compat.jei.category;

import com.fhfelipefh.sandstorm.client.compat.jei.BrackishExtractionRecipe;
import com.fhfelipefh.sandstorm.client.compat.jei.SandStormJeiRecipeTypes;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.network.chat.Component;

import java.util.List;

public class BrackishExtractionCategory extends AbstractRecipeCategory<BrackishExtractionRecipe> {
    public BrackishExtractionCategory(IGuiHelper guiHelper) {
        super(
                SandStormJeiRecipeTypes.BRACKISH_EXTRACTION,
                Component.translatable("jei.sandstorm.brackish_extraction"),
                guiHelper.createDrawableItemLike(SandStormItems.BRACKISH_WATER_BOTTLE),
                140,
                72
        );
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BrackishExtractionRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(14, 18)
                .setStandardSlotBackground()
                .add(recipe.bottleInput());
        builder.addInputSlot(52, 18)
                .setStandardSlotBackground()
                .add(recipe.blockInput());
        builder.addOutputSlot(106, 18)
                .setOutputSlotBackground()
                .add(recipe.output());
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, BrackishExtractionRecipe recipe, IFocusGroup focuses) {
        builder.addRecipePlusSignWidget().setPosition(35, 20);
        builder.addRecipeArrowWidget().setPosition(76, 19);
        builder.addText(List.of(Component.translatable("jei.sandstorm.brackish_extraction.hint1")), 128, 10)
                .setPosition(6, 44)
                .setColor(0xFF00B0FF);
        builder.addText(List.of(Component.translatable("jei.sandstorm.brackish_extraction.hint2")), 128, 10)
                .setPosition(6, 56)
                .setColor(0xFF78909C);
    }
}
