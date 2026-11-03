package com.fhfelipefh.sandstorm.client.compat.jei.category;

import com.fhfelipefh.sandstorm.content.recipe.MachineRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.network.chat.Component;

import java.util.List;

public abstract class AbstractMachineCategory extends AbstractRecipeCategory<MachineRecipe> {
    public AbstractMachineCategory(IRecipeType<MachineRecipe> recipeType, Component title, IDrawable icon, int width, int height) {
        super(recipeType, title, icon, width, height);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MachineRecipe recipe, IFocusGroup focuses) {
        if (recipe.getSlot0InputCount() > 0) {
            builder.addInputSlot(16, 12)
                    .setStandardSlotBackground()
                    .addItemStacks(recipe.getSlot0Inputs());
        }
        if (recipe.getSlot1InputCount() > 0) {
            builder.addInputSlot(16, 34)
                    .setStandardSlotBackground()
                    .addItemStacks(recipe.getSlot1Inputs());
        }
        builder.addOutputSlot(82, 23)
                .setOutputSlotBackground()
                .add(recipe.getOutput());
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, MachineRecipe recipe, IFocusGroup focuses) {
        int ticks = Math.max(1, recipe.getProcessTicks());
        builder.addAnimatedRecipeArrowWidget(ticks).setPosition(46, 23);
        String info = recipe.getEnergyCost() + " J  |  " + (ticks / 20) + "s";
        builder.addText(List.of(Component.literal(info)), 120, 10)
                .setPosition(14, 56)
                .setColor(0xFF78909C);
    }
}
