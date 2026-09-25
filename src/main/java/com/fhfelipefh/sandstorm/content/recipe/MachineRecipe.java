package com.fhfelipefh.sandstorm.content.recipe;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class MachineRecipe {
    private final Supplier<ItemStack> outputSupplier;
    private final Supplier<Component> titleSupplier;
    private final List<Supplier<ItemStack>> slot0Suppliers;
    private final List<Supplier<ItemStack>> slot1Suppliers;
    private final int energyCost;
    private final int processTicks;

    public MachineRecipe(Supplier<ItemStack> outputSupplier, List<Supplier<ItemStack>> slot0Suppliers, List<Supplier<ItemStack>> slot1Suppliers, int energyCost, int processTicks) {
        this(outputSupplier, null, slot0Suppliers, slot1Suppliers, energyCost, processTicks);
    }

    public MachineRecipe(Supplier<ItemStack> outputSupplier, Supplier<Component> titleSupplier, List<Supplier<ItemStack>> slot0Suppliers, List<Supplier<ItemStack>> slot1Suppliers, int energyCost, int processTicks) {
        this.outputSupplier = outputSupplier;
        this.titleSupplier = titleSupplier;
        this.slot0Suppliers = Collections.unmodifiableList(slot0Suppliers);
        this.slot1Suppliers = Collections.unmodifiableList(slot1Suppliers);
        this.energyCost = energyCost;
        this.processTicks = processTicks;
    }

    public Component getTitle() {
        if (titleSupplier != null) {
            Component title = titleSupplier.get();
            if (title != null) {
                return title;
            }
        }
        return getOutput().getHoverName();
    }

    public ItemStack getOutput() {
        return outputSupplier.get().copy();
    }

    public List<ItemStack> getSlot0Inputs() {
        return slot0Suppliers.stream().map(Supplier::get).map(ItemStack::copy).toList();
    }

    public List<ItemStack> getSlot1Inputs() {
        return slot1Suppliers.stream().map(Supplier::get).map(ItemStack::copy).toList();
    }

    public int getSlot0InputCount() {
        return slot0Suppliers.size();
    }

    public int getSlot1InputCount() {
        return slot1Suppliers.size();
    }

    public int getEnergyCost() {
        return energyCost;
    }

    public int getProcessTicks() {
        return processTicks;
    }
}
