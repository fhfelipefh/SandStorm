package com.fhfelipefh.sandstorm.content.world;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExhibitionGalleryManagerTest {

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

        assertNotNull(SandStormItems.TECH_DISC);
        assertNotNull(SandStormBlocks.SEISMIC_DAMPENER_PAVING);
    }

    @Test
    void commandsShouldBeRegistered() {
        CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();
        ExhibitionGalleryManager.registerCommands(dispatcher);

        CommandNode<CommandSourceStack> sandstormNode = dispatcher.getRoot().getChild("sandstorm");
        assertNotNull(sandstormNode);
        assertNotNull(sandstormNode.getChild("gallery"));
        assertNotNull(sandstormNode.getChild("showcase_all"));

        assertNotNull(dispatcher.getRoot().getChild("sandstorm_gallery"));
        assertNotNull(dispatcher.getRoot().getChild("gallery_all"));
        assertNotNull(dispatcher.getRoot().getChild("showcase_all"));
        assertNotNull(dispatcher.getRoot().getChild("build_gallery"));
    }

    @Test
    void gatherAllEntriesShouldContainItemsBlocksEntitiesAndStructures() {
        List<ExhibitionGalleryManager.RawDisplayEntry> entries = ExhibitionGalleryManager.gatherAllEntries();
        assertFalse(entries.isEmpty());
        assertTrue(entries.size() > 50);

        boolean hasCastle = entries.stream().anyMatch(e -> e.name().contains("Castelo Colossal"));
        boolean hasSandworm = entries.stream().anyMatch(e -> e.name().contains("Verme de Areia"));
        boolean hasItem = entries.stream().anyMatch(e -> e.name().startsWith("Item: "));
        boolean hasBlock = entries.stream().anyMatch(e -> e.name().startsWith("Bloco: "));

        assertTrue(hasCastle);
        assertTrue(hasSandworm);
        assertTrue(hasItem);
        assertTrue(hasBlock);
    }

    @Test
    void allEntriesShouldHavePositiveRadius() {
        List<ExhibitionGalleryManager.RawDisplayEntry> entries = ExhibitionGalleryManager.gatherAllEntries();
        for (ExhibitionGalleryManager.RawDisplayEntry entry : entries) {
            assertTrue(entry.radius() > 0);
            assertNotNull(entry.action());
        }
    }
}
