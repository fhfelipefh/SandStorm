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

public class MolecularModifierCategory extends AbstractRecipeCategory<MachineRecipe> {
    public MolecularModifierCategory(IGuiHelper guiHelper) {
        super(
                SandStormJeiRecipeTypes.MOLECULAR_MODIFIER,
                Component.translatable("block.sandstorm.molecular_modifier"),
                guiHelper.createDrawableItemLike(SandStormBlocks.MOLECULAR_MODIFIER),
                144,
                72
        );
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MachineRecipe recipe, IFocusGroup focuses) {
        if (recipe.getSlot0InputCount() > 0) {
            builder.addInputSlot(16, 23)
                    .setStandardSlotBackground()
                    .addItemStacks(recipe.getSlot0Inputs());
        }
        if (recipe.getSlot1InputCount() > 0) {
            builder.addInputSlot(44, 23)
                    .setStandardSlotBackground()
                    .addItemStacks(recipe.getSlot1Inputs());
        }
        builder.addOutputSlot(106, 23)
                .setOutputSlotBackground()
                .add(recipe.getOutput());
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, MachineRecipe recipe, IFocusGroup focuses) {
        int ticks = Math.max(1, recipe.getProcessTicks());
        builder.addAnimatedRecipeArrowWidget(ticks).setPosition(72, 23);
        String info = recipe.getEnergyCost() + " J  |  " + (ticks / 20) + "s";
        builder.addText(List.of(Component.literal(info)), 130, 10)
                .setPosition(14, 56)
                .setColor(0xFF78909C);
    }
}
