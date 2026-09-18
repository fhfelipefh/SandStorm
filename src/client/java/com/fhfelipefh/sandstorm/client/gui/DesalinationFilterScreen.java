package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.DesalinationFilterMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class DesalinationFilterScreen extends BaseMachineScreen<DesalinationFilterMenu> {
    public DesalinationFilterScreen(DesalinationFilterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }
}
