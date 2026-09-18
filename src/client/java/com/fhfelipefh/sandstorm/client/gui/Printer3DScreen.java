package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.Printer3DMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class Printer3DScreen extends BaseMachineScreen<Printer3DMenu> {
    public Printer3DScreen(Printer3DMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }
}
