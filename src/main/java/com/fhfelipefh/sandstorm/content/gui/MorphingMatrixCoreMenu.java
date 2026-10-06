package com.fhfelipefh.sandstorm.content.gui;

import com.fhfelipefh.sandstorm.content.block.MorphingMatrixCoreBlock;
import com.fhfelipefh.sandstorm.content.block.entity.MorphingMatrixCoreBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class MorphingMatrixCoreMenu extends AbstractContainerMenu {

    public static final int BUTTON_SCAN = 0;
    public static final int BUTTON_HOLOGRAM = 1;
    public static final int BUTTON_LIQUEFY = 2;
    public static final int BUTTON_SOLIDIFY = 3;

    private final Container container;
    private final ContainerData data;
    private final MorphingMatrixCoreBlockEntity blockEntity;

    public MorphingMatrixCoreMenu(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, null, new SimpleContainer(1), new SimpleContainerData(6));
    }

    public MorphingMatrixCoreMenu(int syncId, Inventory playerInventory, MorphingMatrixCoreBlockEntity blockEntity, Container container, ContainerData data) {
        this(SandStormMenus.MORPHING_MATRIX_CORE_MENU, syncId, playerInventory, blockEntity, container, data);
    }

    public MorphingMatrixCoreMenu(MenuType<?> menuType, int syncId, Inventory playerInventory, MorphingMatrixCoreBlockEntity blockEntity, Container container, ContainerData data) {
        super(menuType, syncId);
        checkContainerSize(container, 1);
        checkContainerDataCount(data, 6);
        this.container = container;
        this.data = data;
        this.blockEntity = blockEntity;

        this.addSlot(new Slot(container, 0, 80, 62) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                if (stack.isEmpty()) {
                    return false;
                }
                Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
                if (id != null && id.getNamespace().equals("sandstorm")) {
                    String path = id.getPath();
                    return path.equals("morphing_alloy_block")
                            || path.equals("morphing_alloy_door")
                            || path.equals("morphing_alloy_window")
                            || path.equals("nanite_gallium_composite")
                            || path.equals("gallium_ingot");
                }
                return false;
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 86 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 144));
        }

        this.addDataSlots(data);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (this.blockEntity != null && player.level() instanceof ServerLevel serverLevel) {
            if (id == BUTTON_SCAN) {
                this.blockEntity.scanAndSaveStructure(serverLevel);
                return true;
            }
            if (id == BUTTON_HOLOGRAM) {
                boolean nextHolo = !this.blockEntity.isHologramActive();
                this.blockEntity.setHologramActive(nextHolo);
                BlockPos pos = this.blockEntity.getBlockPos();
                serverLevel.setBlock(pos, serverLevel.getBlockState(pos).setValue(MorphingMatrixCoreBlock.HOLOGRAM, nextHolo), 3);
                return true;
            }
            if (id == BUTTON_LIQUEFY) {
                this.blockEntity.startLiquefaction();
                return true;
            }
            if (id == BUTTON_SOLIDIFY) {
                this.blockEntity.startSolidification();
                return true;
            }
        }
        return false;
    }

    public int getState() {
        return this.data.get(0);
    }

    public int getReserveBlocks() {
        return (this.data.get(1) & 0xFFFF) | ((this.data.get(2) & 0xFFFF) << 16);
    }

    public int getMaxReserveBlocks() {
        return MorphingMatrixCoreBlockEntity.MAX_RESERVE;
    }

    public int getSavedCount() {
        return (this.data.get(3) & 0xFFFF) | ((this.data.get(4) & 0xFFFF) << 16);
    }

    public boolean isHologramActive() {
        return this.data.get(5) == 1;
    }

    public int getReserveScaled(int maxWidth) {
        int reserves = getReserveBlocks();
        int max = getMaxReserveBlocks();
        if (max <= 0) {
            return 0;
        }
        return (int) Math.min((long) maxWidth, ((long) reserves * maxWidth) / max);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemStack = stackInSlot.copy();

            if (index == 0) {
                if (!this.moveItemStackTo(stackInSlot, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (this.slots.get(0).mayPlace(stackInSlot)) {
                    if (!this.moveItemStackTo(stackInSlot, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 28) {
                    if (!this.moveItemStackTo(stackInSlot, 28, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(stackInSlot, 1, 28, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stackInSlot.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stackInSlot);
        }
        return itemStack;
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.blockEntity != null) {
            return this.blockEntity.getBlockPos().distToCenterSqr(player.position()) <= 64.0;
        }
        return this.container.stillValid(player);
    }
}
