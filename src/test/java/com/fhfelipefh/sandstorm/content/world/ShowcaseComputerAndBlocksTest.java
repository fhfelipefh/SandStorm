package com.fhfelipefh.sandstorm.content.world;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.command.SandstormDebugCommand;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.storage.QuantumDiskStorage;
import com.fhfelipefh.sandstorm.content.storage.StoredItemEntry;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShowcaseComputerAndBlocksTest {

    @BeforeAll
    static void init() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();

        Field frozenField = MappedRegistry.class.getDeclaredField("frozen");
        frozenField.setAccessible(true);
        frozenField.set(BuiltInRegistries.BLOCK, false);
        frozenField.set(BuiltInRegistries.ITEM, false);
        frozenField.set(BuiltInRegistries.BLOCK_ENTITY_TYPE, false);
        frozenField.set(BuiltInRegistries.ENTITY_TYPE, false);
        frozenField.set(BuiltInRegistries.CREATIVE_MODE_TAB, false);

        Field holdersField = MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
        holdersField.setAccessible(true);
        holdersField.set(BuiltInRegistries.BLOCK, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.ITEM, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.BLOCK_ENTITY_TYPE, new IdentityHashMap<>());
        holdersField.set(BuiltInRegistries.ENTITY_TYPE, new IdentityHashMap<>());

        assertNotNull(SandStormBlocks.QUANTUM_NETWORK_CONTROLLER);
        assertNotNull(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_64K);

        DataComponentMap defaultComponents = DataComponentMap.builder()
                .set(DataComponents.MAX_STACK_SIZE, 64)
                .build();
        for (Item item : BuiltInRegistries.ITEM) {
            if (!item.builtInRegistryHolder().areComponentsBound()) {
                item.builtInRegistryHolder().bindComponents(defaultComponents);
            }
        }
        for (Block block : BuiltInRegistries.BLOCK) {
            Item blockItem = block.asItem();
            if (blockItem != null && !blockItem.builtInRegistryHolder().areComponentsBound()) {
                blockItem.builtInRegistryHolder().bindComponents(defaultComponents);
            }
        }
    }

    @Test
    void shouldRegisterComputerAndShowcaseCommands() {
        CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();
        ShowcaseAutomation.registerCommands(dispatcher);

        CommandNode<CommandSourceStack> sandstorm = dispatcher.getRoot().getChild("sandstorm");
        assertNotNull(sandstorm);
        assertNotNull(sandstorm.getChild("showcase"));
        assertNotNull(sandstorm.getChild("computer"));

        CommandNode<CommandSourceStack> sandstormDebug = dispatcher.getRoot().getChild("sandstorm_debug");
        assertNotNull(sandstormDebug);
        assertNotNull(sandstormDebug.getChild("showcase"));
        assertNotNull(sandstormDebug.getChild("computer"));

        CommandNode<CommandSourceStack> sandstormComputer = dispatcher.getRoot().getChild("sandstorm_computer");
        assertNotNull(sandstormComputer);
    }

    @Test
    void shouldIncludeComputerInDebugFacilities() {
        List<String> facilities = SandstormDebugCommand.getSupportedFacilities();
        assertTrue(facilities.contains("computer"));
    }

    @Test
    void shouldVerifyAllQuantumBlocksHaveSpecificExplanations() throws Exception {
        Method explanationMethod = ShowcaseAutomation.class.getDeclaredMethod("getBlockExplanation", Block.class);
        explanationMethod.setAccessible(true);

        String controllerExp = (String) explanationMethod.invoke(null, SandStormBlocks.QUANTUM_NETWORK_CONTROLLER);
        String driveExp = (String) explanationMethod.invoke(null, SandStormBlocks.QUANTUM_DISK_DRIVE);
        String terminalExp = (String) explanationMethod.invoke(null, SandStormBlocks.QUANTUM_ACCESS_TERMINAL);
        String cableExp = (String) explanationMethod.invoke(null, SandStormBlocks.QUANTUM_NETWORK_CABLE);

        assertNotNull(controllerExp);
        assertFalse(controllerExp.equals("Tecnologia Estrutural SandStorm"));
        assertTrue(controllerExp.length() <= 45);

        assertNotNull(driveExp);
        assertFalse(driveExp.equals("Tecnologia Estrutural SandStorm"));
        assertTrue(driveExp.length() <= 45);

        assertNotNull(terminalExp);
        assertFalse(terminalExp.equals("Tecnologia Estrutural SandStorm"));
        assertTrue(terminalExp.length() <= 45);

        assertNotNull(cableExp);
        assertFalse(cableExp.equals("Tecnologia Estrutural SandStorm"));
        assertTrue(cableExp.length() <= 45);
    }

    @Test
    void shouldValidateQuantumCartridgePrepopulation() {
        ItemStack cartridge = new ItemStack(SandStormItems.QUANTUM_STORAGE_CARTRIDGE_64K);
        List<StoredItemEntry> entries = List.of(
                new StoredItemEntry(new ItemStack(SandStormItems.TITANIUM_CHITIN_COMPOSITE), 1000L),
                new StoredItemEntry(new ItemStack(SandStormItems.PIEZO_QUARTZ_SHARD), 2000L),
                new StoredItemEntry(new ItemStack(SandStormItems.SUPERCONDUCTOR_TOROID), 500L),
                new StoredItemEntry(new ItemStack(SandStormItems.QUANTUM_PROCESSOR), 250L)
        );

        QuantumDiskStorage.saveStoredItems(cartridge, entries);

        List<StoredItemEntry> retrieved = QuantumDiskStorage.getStoredItems(cartridge);
        assertEquals(4, retrieved.size());
        assertEquals(3750L, QuantumDiskStorage.getTotalItemCount(cartridge));
        assertEquals(4, QuantumDiskStorage.getStoredTypeCount(cartridge));

        StoredItemEntry first = retrieved.get(0);
        assertTrue(first.template().is(SandStormItems.TITANIUM_CHITIN_COMPOSITE));
        assertEquals(1000L, first.count());
    }

    @Test
    void shouldEnsureAllModBlocksAreCoveredInShowcaseCheckerboard() {
        List<Block> blocks = BuiltInRegistries.BLOCK.stream()
                .filter(b -> BuiltInRegistries.BLOCK.getKey(b).getNamespace().equals(SandStormMod.MOD_ID))
                .filter(b -> !b.equals(SandStormBlocks.MEGASTRUCTURE_CONSTRUCTOR))
                .toList();

        assertFalse(blocks.isEmpty());
        assertTrue(blocks.contains(SandStormBlocks.QUANTUM_NETWORK_CONTROLLER));
        assertTrue(blocks.contains(SandStormBlocks.QUANTUM_DISK_DRIVE));
        assertTrue(blocks.contains(SandStormBlocks.QUANTUM_ACCESS_TERMINAL));
        assertTrue(blocks.contains(SandStormBlocks.QUANTUM_NETWORK_CABLE));
        assertTrue(blocks.contains(SandStormBlocks.ELECTRIC_FENCE_PYLON));
        assertTrue(blocks.contains(SandStormBlocks.SUBSPACE_GATEWAY));
        assertTrue(blocks.contains(SandStormBlocks.SEISMIC_DAMPENER_PAVING));
        assertTrue(blocks.contains(SandStormBlocks.SALT_BRICKS));
    }
}
