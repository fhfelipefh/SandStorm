package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.MaterialDeconstructorMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class MaterialDeconstructorScreen extends BaseMachineScreen<MaterialDeconstructorMenu> {
    public MaterialDeconstructorScreen(MaterialDeconstructorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected String getMachineId() {
        return "material_deconstructor";
    }

    @Override
    protected void renderChassis(GuiGraphicsExtractor extractor) {
        super.renderChassis(extractor);
        int x = this.leftPos;
        int y = this.topPos;

        extractor.fill(x + 77, y + 40, x + 109, y + 46, 0xFF1E293B);
        extractor.fill(x + 78, y + 41, x + 108, y + 45, 0xFF05080E);
        int progress = this.menu.getProgressScaled(30);
        if (progress > 0) {
            extractor.fill(x + 78, y + 41, x + 78 + progress, y + 45, 0xFF00E5FF);
        }
    }
}

