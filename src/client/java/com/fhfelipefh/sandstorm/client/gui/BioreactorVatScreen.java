package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.BioreactorVatMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class BioreactorVatScreen extends BaseMachineScreen<BioreactorVatMenu> {
    public BioreactorVatScreen(BioreactorVatMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }
}
