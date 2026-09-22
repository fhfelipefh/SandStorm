package com.fhfelipefh.sandstorm.content.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.level.block.Block;

public class SandstoneWorkbenchMenu extends CraftingMenu {
    private final ContainerLevelAccess access;
    private final Block targetBlock;

    public SandstoneWorkbenchMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL, null);
    }

    public SandstoneWorkbenchMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access, Block targetBlock) {
        super(containerId, playerInventory, access);
        this.access = access;
        this.targetBlock = targetBlock;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.targetBlock == null ? true : stillValid(this.access, player, this.targetBlock);
    }
}
