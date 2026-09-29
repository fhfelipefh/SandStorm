package com.fhfelipefh.sandstorm.content.storage;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuantumDiskStorage {

    private static final String STORED_ITEMS_KEY = "StoredItems";
    private static final String ITEM_DATA_KEY = "item_data";
    private static final String STORED_COUNT_KEY = "stored_count";

    public static List<StoredItemEntry> getStoredItems(ItemStack cartridge) {
        if (cartridge == null || cartridge.isEmpty()) {
            return Collections.emptyList();
        }

        CustomData customData = cartridge.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();
        if (!tag.contains(STORED_ITEMS_KEY)) {
            return new ArrayList<>();
        }

        ListTag listTag = tag.getListOrEmpty(STORED_ITEMS_KEY);
        List<StoredItemEntry> entries = new ArrayList<>(listTag.size());

        for (int i = 0; i < listTag.size(); i++) {
            CompoundTag entryTag = listTag.getCompoundOrEmpty(i);
            if (entryTag.contains(ITEM_DATA_KEY)) {
                Tag dataTag = entryTag.get(ITEM_DATA_KEY);
                if (dataTag != null) {
                    ItemStack template = ItemStack.CODEC.parse(NbtOps.INSTANCE, dataTag).result().orElse(ItemStack.EMPTY);
                    long count = entryTag.getLongOr(STORED_COUNT_KEY, 0L);
                    if (!template.isEmpty() && count > 0) {
                        entries.add(new StoredItemEntry(template, count));
                    }
                }
            }
        }

        return entries;
    }

    public static long getTotalItemCount(ItemStack cartridge) {
        List<StoredItemEntry> entries = getStoredItems(cartridge);
        long sum = 0;
        for (StoredItemEntry entry : entries) {
            sum += entry.count();
        }
        return sum;
    }

    public static int getStoredTypeCount(ItemStack cartridge) {
        return getStoredItems(cartridge).size();
    }

    public static long insertItem(ItemStack cartridge, ItemStack toInsert, StorageCartridgeTier tier) {
        if (cartridge == null || cartridge.isEmpty() || toInsert == null || toInsert.isEmpty()) {
            return 0;
        }

        List<StoredItemEntry> entries = getStoredItems(cartridge);
        long currentTotal = 0;
        int matchingIndex = -1;

        for (int i = 0; i < entries.size(); i++) {
            StoredItemEntry entry = entries.get(i);
            currentTotal += entry.count();
            if (entry.matches(toInsert)) {
                matchingIndex = i;
            }
        }

        long availableSpace = (long) tier.getCapacity() - currentTotal;
        if (availableSpace <= 0) {
            return 0;
        }

        long countToInsert = Math.min((long) toInsert.getCount(), availableSpace);

        if (matchingIndex >= 0) {
            StoredItemEntry existing = entries.get(matchingIndex);
            entries.set(matchingIndex, new StoredItemEntry(existing.template(), existing.count() + countToInsert));
        } else {
            if (entries.size() >= tier.getMaxTypes()) {
                return 0;
            }
            entries.add(new StoredItemEntry(toInsert.copyWithCount(1), countToInsert));
        }

        saveStoredItems(cartridge, entries);
        return countToInsert;
    }

    public static ItemStack extractItem(ItemStack cartridge, ItemStack filterStack, int maxExtract) {
        if (cartridge == null || cartridge.isEmpty() || filterStack == null || filterStack.isEmpty() || maxExtract <= 0) {
            return ItemStack.EMPTY;
        }

        List<StoredItemEntry> entries = getStoredItems(cartridge);
        int matchingIndex = -1;

        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).matches(filterStack)) {
                matchingIndex = i;
                break;
            }
        }

        if (matchingIndex < 0) {
            return ItemStack.EMPTY;
        }

        StoredItemEntry entry = entries.get(matchingIndex);
        int extractAmount = (int) Math.min((long) maxExtract, Math.min((long) entry.template().getMaxStackSize(), entry.count()));
        if (extractAmount <= 0) {
            return ItemStack.EMPTY;
        }

        ItemStack extracted = entry.template().copyWithCount(extractAmount);
        long remaining = entry.count() - extractAmount;

        if (remaining <= 0) {
            entries.remove(matchingIndex);
        } else {
            entries.set(matchingIndex, new StoredItemEntry(entry.template(), remaining));
        }

        saveStoredItems(cartridge, entries);
        return extracted;
    }

    public static void saveStoredItems(ItemStack cartridge, List<StoredItemEntry> entries) {
        CustomData customData = cartridge.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag rootTag = customData.copyTag();

        ListTag listTag = new ListTag();
        for (StoredItemEntry entry : entries) {
            CompoundTag entryTag = new CompoundTag();
            Tag serializedItem = ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, entry.template()).result().orElse(null);
            if (serializedItem != null) {
                entryTag.put(ITEM_DATA_KEY, serializedItem);
                entryTag.putLong(STORED_COUNT_KEY, entry.count());
                listTag.add(entryTag);
            }
        }

        rootTag.put(STORED_ITEMS_KEY, listTag);
        cartridge.set(DataComponents.CUSTOM_DATA, CustomData.of(rootTag));
    }
}
