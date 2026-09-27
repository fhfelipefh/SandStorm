package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.ChemicalRefineryMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ChemicalRefineryScreen extends BaseMachineScreen<ChemicalRefineryMenu> {
    public ChemicalRefineryScreen(ChemicalRefineryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }
}
