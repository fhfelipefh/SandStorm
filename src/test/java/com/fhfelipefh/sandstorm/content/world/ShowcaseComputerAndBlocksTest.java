package com.fhfelipefh.sandstorm.content.world;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.LegacyRustBlock;
import com.fhfelipefh.sandstorm.content.command.SandstormDebugCommand;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.storage.QuantumDiskStorage;
import com.fhfelipefh.sandstorm.content.storage.StoredItemEntry;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
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
        assertNotNull(sandstorm.getChild("vault"));
        assertNotNull(sandstorm.getChild("carbon"));
        assertNotNull(sandstorm.getChild("extractor"));
        assertNotNull(sandstorm.getChild("furnace"));
        assertNotNull(sandstorm.getChild("induction"));
        assertNotNull(sandstorm.getChild("steed"));
        assertNotNull(sandstorm.getChild("horse"));
        assertNotNull(sandstorm.getChild("sandworm"));
        assertNotNull(sandstorm.getChild("worm"));

        CommandNode<CommandSourceStack> sandstormDebug = dispatcher.getRoot().getChild("sandstorm_debug");
        assertNotNull(sandstormDebug);
        assertNotNull(sandstormDebug.getChild("showcase"));
        assertNotNull(sandstormDebug.getChild("computer"));
        assertNotNull(sandstormDebug.getChild("vault"));
        assertNotNull(sandstormDebug.getChild("carbon"));
        assertNotNull(sandstormDebug.getChild("extractor"));
        assertNotNull(sandstormDebug.getChild("furnace"));
        assertNotNull(sandstormDebug.getChild("induction"));
        assertNotNull(sandstormDebug.getChild("steed"));
        assertNotNull(sandstormDebug.getChild("horse"));
        assertNotNull(sandstormDebug.getChild("sandworm"));
        assertNotNull(sandstormDebug.getChild("worm"));

        CommandNode<CommandSourceStack> sandstormComputer = dispatcher.getRoot().getChild("sandstorm_computer");
        assertNotNull(sandstormComputer);

        CommandNode<CommandSourceStack> sandstormVault = dispatcher.getRoot().getChild("sandstorm_vault");
        assertNotNull(sandstormVault);

        CommandNode<CommandSourceStack> sandstormCarbon = dispatcher.getRoot().getChild("sandstorm_carbon");
        assertNotNull(sandstormCarbon);

        CommandNode<CommandSourceStack> sandstormExtractor = dispatcher.getRoot().getChild("sandstorm_extractor");
        assertNotNull(sandstormExtractor);

        CommandNode<CommandSourceStack> sandstormFurnace = dispatcher.getRoot().getChild("sandstorm_furnace");
        assertNotNull(sandstormFurnace);

        CommandNode<CommandSourceStack> sandstormSteed = dispatcher.getRoot().getChild("sandstorm_steed");
        assertNotNull(sandstormSteed);

        CommandNode<CommandSourceStack> sandstormHorse = dispatcher.getRoot().getChild("sandstorm_horse");
        assertNotNull(sandstormHorse);

        CommandNode<CommandSourceStack> sandstormSandworm = dispatcher.getRoot().getChild("sandstorm_sandworm");
        assertNotNull(sandstormSandworm);

        CommandNode<CommandSourceStack> sandstormWorm = dispatcher.getRoot().getChild("sandstorm_worm");
        assertNotNull(sandstormWorm);
    }

    @Test
    void shouldIncludeComputerInDebugFacilities() {
        List<String> facilities = SandstormDebugCommand.getSupportedFacilities();
        assertTrue(facilities.contains("computer"));
        assertTrue(facilities.contains("vault"));
        assertTrue(facilities.contains("carbon_foundry"));
        assertTrue(facilities.contains("carbon"));
        assertTrue(facilities.contains("extractor"));
        assertTrue(facilities.contains("furnace"));
    }

    @Test
    void shouldVerifyAllQuantumBlocksHaveSpecificExplanations() throws Exception {
        Method explanationMethod = ShowcaseAutomation.class.getDeclaredMethod("getBlockExplanation", Block.class);
        explanationMethod.setAccessible(true);

        String controllerExp = (String) explanationMethod.invoke(null, SandStormBlocks.QUANTUM_NETWORK_CONTROLLER);
        String driveExp = (String) explanationMethod.invoke(null, SandStormBlocks.QUANTUM_DISK_DRIVE);
        String terminalExp = (String) explanationMethod.invoke(null, SandStormBlocks.QUANTUM_ACCESS_TERMINAL);
        String cableExp = (String) explanationMethod.invoke(null, SandStormBlocks.QUANTUM_NETWORK_CABLE);
        String vaultControllerExp = (String) explanationMethod.invoke(null, SandStormBlocks.VAULT_DOOR_CONTROLLER);
        String vaultWallPanelExp = (String) explanationMethod.invoke(null, SandStormBlocks.VAULT_WALL_PANEL);
        String vaultWallSeamExp = (String) explanationMethod.invoke(null, SandStormBlocks.VAULT_WALL_SEAM);
        String carbonExtractorExp = (String) explanationMethod.invoke(null, SandStormBlocks.ATMOSPHERIC_CARBON_EXTRACTOR);
        String electricFurnaceExp = (String) explanationMethod.invoke(null, SandStormBlocks.ELECTRIC_ARC_FURNACE);
        String sandstoneCrateExp = (String) explanationMethod.invoke(null, SandStormBlocks.SANDSTONE_CRATE);
        String synapticCondenserExp = (String) explanationMethod.invoke(null, SandStormBlocks.SYNAPTIC_CONDENSER);
        String synapticRepairerExp = (String) explanationMethod.invoke(null, SandStormBlocks.SYNAPTIC_REPAIRER);

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

        assertNotNull(vaultControllerExp);
        assertFalse(vaultControllerExp.equals("Tecnologia Estrutural SandStorm"));
        assertTrue(vaultControllerExp.length() <= 45);

        assertNotNull(vaultWallPanelExp);
        assertFalse(vaultWallPanelExp.equals("Tecnologia Estrutural SandStorm"));
        assertTrue(vaultWallPanelExp.length() <= 45);

        assertNotNull(vaultWallSeamExp);
        assertFalse(vaultWallSeamExp.equals("Tecnologia Estrutural SandStorm"));
        assertTrue(vaultWallSeamExp.length() <= 45);

        assertNotNull(carbonExtractorExp);
        assertFalse(carbonExtractorExp.equals("Tecnologia Estrutural SandStorm"));
        assertTrue(carbonExtractorExp.length() <= 45);

        assertNotNull(electricFurnaceExp);
        assertFalse(electricFurnaceExp.equals("Tecnologia Estrutural SandStorm"));
        assertTrue(electricFurnaceExp.length() <= 45);

        assertNotNull(sandstoneCrateExp);
        assertFalse(sandstoneCrateExp.equals("Tecnologia Estrutural SandStorm"));
        assertTrue(sandstoneCrateExp.length() <= 45);

        assertNotNull(synapticCondenserExp);
        assertFalse(synapticCondenserExp.equals("Tecnologia Estrutural SandStorm"));
        assertTrue(synapticCondenserExp.length() <= 45);

        assertNotNull(synapticRepairerExp);
        assertFalse(synapticRepairerExp.equals("Tecnologia Estrutural SandStorm"));
        assertTrue(synapticRepairerExp.length() <= 45);
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
                .filter(b -> !b.equals(SandStormBlocks.MEGASTRUCTURE_CONSTRUCTOR) && !b.equals(SandStormBlocks.VAULT_DOOR_DUMMY))
                .toList();

        assertFalse(blocks.isEmpty());
        assertTrue(blocks.contains(SandStormBlocks.QUANTUM_NETWORK_CONTROLLER));
        assertTrue(blocks.contains(SandStormBlocks.QUANTUM_DISK_DRIVE));
        assertTrue(blocks.contains(SandStormBlocks.QUANTUM_ACCESS_TERMINAL));
        assertTrue(blocks.contains(SandStormBlocks.QUANTUM_NETWORK_CABLE));
        assertTrue(blocks.contains(SandStormBlocks.VAULT_DOOR_CONTROLLER));
        assertTrue(blocks.contains(SandStormBlocks.VAULT_WALL_PANEL));
        assertTrue(blocks.contains(SandStormBlocks.VAULT_WALL_SEAM));
        assertTrue(blocks.contains(SandStormBlocks.ELECTRIC_FENCE_PYLON));
        assertTrue(blocks.contains(SandStormBlocks.SUBSPACE_GATEWAY));
        assertTrue(blocks.contains(SandStormBlocks.SEISMIC_DAMPENER_PAVING));
        assertTrue(blocks.contains(SandStormBlocks.SALT_BRICKS));
        assertTrue(blocks.contains(SandStormBlocks.ATMOSPHERIC_CARBON_EXTRACTOR));
        assertTrue(blocks.contains(SandStormBlocks.ELECTRIC_ARC_FURNACE));
        assertTrue(blocks.contains(SandStormBlocks.SANDSTONE_CRATE));
        assertTrue(blocks.contains(SandStormBlocks.SYNAPTIC_CONDENSER));
        assertTrue(blocks.contains(SandStormBlocks.SYNAPTIC_REPAIRER));
    }

    @Test
    void shouldEnsureBiospheresShowcaseDoesNotOverlapBlocks() {
        List<Block> blocks = BuiltInRegistries.BLOCK.stream()
                .filter(b -> BuiltInRegistries.BLOCK.getKey(b).getNamespace().equals(SandStormMod.MOD_ID))
                .filter(b -> !(b instanceof LegacyRustBlock))
                .filter(b -> !b.equals(SandStormBlocks.MEGASTRUCTURE_CONSTRUCTOR) && !b.equals(SandStormBlocks.VAULT_DOOR_DUMMY))
                .toList();
        int cols = 8;
        int spacing = 5;
        int startZ = 20;
        int totalRows = (blocks.size() + cols - 1) / cols;
        int maxBlockZ = startZ + (totalRows * spacing) + 4;
        assertEquals(maxBlockZ, ShowcaseAutomation.getCheckerboardMaxZ());

        int frontZ = ShowcaseAutomation.getBiosphereFrontZ();
        int domeRadius = 24;
        int nearestDomeEdgeZ = frontZ - domeRadius;

        assertTrue(nearestDomeEdgeZ > maxBlockZ, "Biospheres front edge must be strictly past block checkerboard");
        assertTrue(nearestDomeEdgeZ - maxBlockZ >= 16, "Must maintain at least 16 blocks buffer between exhibition and biospheres");
    }

    @Test
    void shouldExposeShowcaseItemCleanupAndSafeBlockSetters() throws Exception {
        Method cleanupMethod = ShowcaseAutomation.class.getMethod("cleanupShowcaseDroppedItems", ServerLevel.class);
        assertNotNull(cleanupMethod);

        Method setAirSafeMethod = ShowcaseAutomation.class.getMethod("setAirSafe", ServerLevel.class, BlockPos.class);
        assertNotNull(setAirSafeMethod);

        Method setChestSafeMethod = ShowcaseAutomation.class.getMethod("setChestSafe", ServerLevel.class, BlockPos.class, BlockState.class);
        assertNotNull(setChestSafeMethod);
    }

    @Test
    void shouldRegisterCinematicCommands() {
        CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();
        ShowcaseAutomation.registerCommands(dispatcher);
        assertNotNull(dispatcher.getRoot().getChild("sandstorm_showcase_cinematic"));
        assertNotNull(dispatcher.getRoot().getChild("sandstorm_cinematic"));
        assertNotNull(dispatcher.getRoot().getChild("sandstorm").getChild("cinematic"));
    }
}
