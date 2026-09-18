package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.NaniteFabricatorMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class NaniteFabricatorScreen extends BaseMachineScreen<NaniteFabricatorMenu> {
    public NaniteFabricatorScreen(NaniteFabricatorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }
}
