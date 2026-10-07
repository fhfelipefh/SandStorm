package com.fhfelipefh.sandstorm.content.gui;

import com.fhfelipefh.sandstorm.content.block.entity.AtmosphericTerraformerBlockEntity;
import com.fhfelipefh.sandstorm.content.world.biosphere.BiosphereType;
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

public class AtmosphericTerraformerMenu extends AbstractContainerMenu {
    public static final int DATA_COUNT = 16;
    public static final int BUTTON_TOGGLE_LIGHTNING = 0;
    public static final int BUTTON_CYCLE_TIER = 1;

    private final AtmosphericTerraformerBlockEntity blockEntity;
    private final Container container;
    private final ContainerData data;

    public AtmosphericTerraformerMenu(int syncId, Inventory playerInventory) {
        this(SandStormMenus.ATMOSPHERIC_TERRAFORMER_MENU, syncId, playerInventory, null, new SimpleContainer(AtmosphericTerraformerBlockEntity.CONTAINER_SIZE), new SimpleContainerData(DATA_COUNT));
    }

    public AtmosphericTerraformerMenu(int syncId, Inventory playerInventory, AtmosphericTerraformerBlockEntity blockEntity, ContainerData data) {
        this(SandStormMenus.ATMOSPHERIC_TERRAFORMER_MENU, syncId, playerInventory, blockEntity, blockEntity, data);
    }

    public AtmosphericTerraformerMenu(MenuType<?> menuType, int syncId, Inventory playerInventory, AtmosphericTerraformerBlockEntity blockEntity, Container container, ContainerData data) {
        super(menuType, syncId);
        checkContainerSize(container, AtmosphericTerraformerBlockEntity.CONTAINER_SIZE);
        checkContainerDataCount(data, DATA_COUNT);
        this.blockEntity = blockEntity;
        this.container = container;
        this.data = data;

        this.addSlot(new Slot(container, AtmosphericTerraformerBlockEntity.SLOT_WATER_IN, 24, 25));
        this.addSlot(new Slot(container, AtmosphericTerraformerBlockEntity.SLOT_WATER_OUT, 24, 53) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new Slot(container, AtmosphericTerraformerBlockEntity.SLOT_MINERAL, 48, 25));
        this.addSlot(new Slot(container, AtmosphericTerraformerBlockEntity.SLOT_SEEDS, 68, 25));
        this.addSlot(new Slot(container, AtmosphericTerraformerBlockEntity.SLOT_SAPLINGS, 88, 25));
        this.addSlot(new Slot(container, AtmosphericTerraformerBlockEntity.SLOT_FUEL, 48, 53));
        this.addSlot(new Slot(container, AtmosphericTerraformerBlockEntity.SLOT_UPGRADE, 68, 53));

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }

        this.addDataSlots(data);
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.blockEntity != null && this.blockEntity.getLevel() != null) {
            return this.blockEntity.getLevel().getBlockEntity(this.blockEntity.getBlockPos()) == this.blockEntity
                    && player.distanceToSqr(this.blockEntity.getBlockPos().getX() + 0.5, this.blockEntity.getBlockPos().getY() + 0.5, this.blockEntity.getBlockPos().getZ() + 0.5) <= 64.0;
        }
        return this.container.stillValid(player);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (this.blockEntity != null) {
            if (id == BUTTON_TOGGLE_LIGHTNING) {
                this.blockEntity.toggleLightning();
                return true;
            }
            if (id == BUTTON_CYCLE_TIER) {
                this.blockEntity.cycleTier();
                return true;
            }
        }
        return false;
    }

    public int getEnergy() {
        return (this.data.get(0) & 0xFFFF) | ((this.data.get(1) & 0xFFFF) << 16);
    }

    public int getMaxEnergy() {
        return (this.data.get(2) & 0xFFFF) | ((this.data.get(3) & 0xFFFF) << 16);
    }

    public int getWaterAmount() {
        return (this.data.get(4) & 0xFFFF) | ((this.data.get(5) & 0xFFFF) << 16);
    }

    public int getMaxWater() {
        return (this.data.get(6) & 0xFFFF) | ((this.data.get(7) & 0xFFFF) << 16);
    }

    public int getMineralUnits() {
        return this.data.get(8);
    }

    public int getSeedUnits() {
        return this.data.get(9);
    }

    public int getSaplingCount() {
        return this.data.get(10);
    }

    public int getTier() {
        return this.data.get(11);
    }

    public boolean isActive() {
        return this.data.get(12) == 1;
    }

    public boolean isLightningEnabled() {
        return this.data.get(13) == 1;
    }

    public int getDissipatingTicks() {
        return this.data.get(14);
    }

    public boolean isDissipating() {
        return getDissipatingTicks() > 0;
    }

    public BiosphereType getActiveBiosphereType() {
        int ord = this.data.get(15);
        BiosphereType[] types = BiosphereType.values();
        if (ord >= 0 && ord < types.length) {
            return types[ord];
        }
        return BiosphereType.PRIMORDIAL_OASIS;
    }

    public int getRadius() {
        return switch (getTier()) {
            case 2 -> 48;
            case 3 -> 80;
            default -> 24;
        };
    }

    public int getEnergyScaled(int pixels) {
        int max = getMaxEnergy();
        if (max <= 0) {
            return 0;
        }
        return (int) ((long) getEnergy() * pixels / max);
    }

    public int getWaterScaled(int pixels) {
        int max = getMaxWater();
        if (max <= 0) {
            return 0;
        }
        return (int) ((long) getWaterAmount() * pixels / max);
    }

    public AtmosphericTerraformerBlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemStack = stackInSlot.copy();
            int containerSlots = AtmosphericTerraformerBlockEntity.CONTAINER_SIZE;
            int totalSlots = this.slots.size();

            if (index < containerSlots) {
                if (!this.moveItemStackTo(stackInSlot, containerSlots, totalSlots, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(stackInSlot, 0, containerSlots, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemStack;
    }
}
