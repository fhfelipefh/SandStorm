package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.AutoAssemblyLineMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class AutoAssemblyLineScreen extends BaseMachineScreen<AutoAssemblyLineMenu> {
    public AutoAssemblyLineScreen(AutoAssemblyLineMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }
}
