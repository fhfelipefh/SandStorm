package com.fhfelipefh.sandstorm.content.storage;

import net.minecraft.world.item.ItemStack;

public record StoredItemEntry(ItemStack template, long count) {

    public boolean matches(ItemStack other) {
        if (other == null || other.isEmpty()) {
            return false;
        }
        return ItemStack.isSameItemSameComponents(this.template, other);
    }
}
