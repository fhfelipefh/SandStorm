package com.fhfelipefh.sandstorm.content.survival;

import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgRoutine;
import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgSpecialty;
import com.fhfelipefh.sandstorm.content.gui.CyborgTelemetryMenu;
import com.fhfelipefh.sandstorm.content.item.CyberneticCommandUplinkItem;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.SharedConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.Item;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Fase26AutonomousCyborgsTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "cyborg_excavator",
            "cyborg_builder",
            "cyborg_harvester"
    })
    void shouldRegisterCyborgEntityKeys(String entityPath) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, SandStormMod.id(entityPath));
        assertNotNull(key);
        assertEquals("sandstorm", key.identifier().getNamespace());
        assertEquals(entityPath, key.identifier().getPath());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "cybernetic_command_uplink",
            "cyborg_excavator_spawn_egg",
            "cyborg_builder_spawn_egg",
            "cyborg_harvester_spawn_egg"
    })
    void shouldRegisterItemKeys(String itemPath) {
        ResourceKey<Item> key = SandStormMod.itemKey(itemPath);
        assertNotNull(key);
        assertEquals("sandstorm", key.identifier().getNamespace());
        assertEquals(itemPath, key.identifier().getPath());
    }

    @Test
    void shouldValidateCyborgRoutineEnums() {
        assertEquals(CyborgRoutine.AUTONOMOUS_WORK, CyborgRoutine.fromOrdinal(0));
        assertEquals(CyborgRoutine.FOLLOW_OPERATOR, CyborgRoutine.fromOrdinal(1));
        assertEquals(CyborgRoutine.PATROL_PERIMETER, CyborgRoutine.fromOrdinal(2));
        assertEquals(CyborgRoutine.RETURN_TO_DOCK, CyborgRoutine.fromOrdinal(3));
        assertEquals(CyborgRoutine.AUTONOMOUS_WORK, CyborgRoutine.fromOrdinal(99));
    }

    @Test
    void shouldValidateCyborgSpecialtyEnums() {
        assertEquals(CyborgSpecialty.EXCAVATOR, CyborgSpecialty.fromOrdinal(0));
        assertEquals(CyborgSpecialty.BUILDER, CyborgSpecialty.fromOrdinal(1));
        assertEquals(CyborgSpecialty.HARVESTER, CyborgSpecialty.fromOrdinal(2));
        assertEquals(CyborgSpecialty.EXCAVATOR, CyborgSpecialty.fromOrdinal(99));
    }

    @Test
    void shouldValidateCyberneticCommandUplinkModes() {
        CyberneticCommandUplinkItem.UplinkMode mode = CyberneticCommandUplinkItem.UplinkMode.MINING;
        mode = mode.cycle();
        assertEquals(CyberneticCommandUplinkItem.UplinkMode.BUILDING, mode);
        mode = mode.cycle();
        assertEquals(CyberneticCommandUplinkItem.UplinkMode.HARVESTING, mode);
        mode = mode.cycle();
        assertEquals(CyberneticCommandUplinkItem.UplinkMode.MINING, mode);
    }

    @Test
    void shouldVerifyTelemetryMenuSlotsAndDataChannels() {
        SimpleContainer container = new SimpleContainer(CyborgTelemetryMenu.CYBORG_SLOTS);
        SimpleContainerData data = new SimpleContainerData(8);
        data.set(0, 35000);
        data.set(1, 50000);
        data.set(2, 2500);
        data.set(3, 4000);
        data.set(4, 95);
        data.set(5, 0);
        data.set(6, 2);
        data.set(7, 1);

        CyborgTelemetryMenu menu = new CyborgTelemetryMenu(null, 1, new Inventory(null, null), container, data);

        assertEquals(CyborgRoutine.AUTONOMOUS_WORK, menu.getRoutine());
        assertEquals(2, menu.getVisorColor());
        assertEquals(35000, menu.getEnergy());
        assertEquals(2500, menu.getCoolant());
        assertEquals(95, menu.getIntegrity());
        assertEquals(CyborgSpecialty.BUILDER, menu.getSpecialty());
        assertEquals(54, menu.slots.size());

        assertTrue(menu.clickMenuButton(null, 1));
        assertEquals(CyborgRoutine.FOLLOW_OPERATOR, menu.getRoutine());

        assertTrue(menu.clickMenuButton(null, 2));
        assertEquals(CyborgRoutine.PATROL_PERIMETER, menu.getRoutine());

        assertTrue(menu.clickMenuButton(null, 3));
        assertEquals(CyborgRoutine.RETURN_TO_DOCK, menu.getRoutine());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "cybernetic_command_uplink.json",
            "cyborg_excavator_spawn_egg.json",
            "cyborg_builder_spawn_egg.json",
            "cyborg_harvester_spawn_egg.json"
    })
    void shouldVerifyItemDefinitionFilesExist(String filename) {
        File file = new File("src/main/resources/assets/sandstorm/items/" + filename);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "cybernetic_command_uplink.json",
            "cyborg_excavator_spawn_egg.json",
            "cyborg_builder_spawn_egg.json",
            "cyborg_harvester_spawn_egg.json"
    })
    void shouldVerifyItemModelFilesExist(String filename) {
        File file = new File("src/main/resources/assets/sandstorm/models/item/" + filename);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "cybernetic_command_uplink.json",
            "cyborg_excavator_spawn_egg.json",
            "cyborg_builder_spawn_egg.json",
            "cyborg_harvester_spawn_egg.json"
    })
    void shouldVerifyRecipeFilesExist(String filename) {
        File file = new File("src/main/resources/data/sandstorm/recipe/" + filename);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "src/main/resources/assets/sandstorm/textures/item/cybernetic_command_uplink.png",
            "src/main/resources/assets/sandstorm/textures/item/cyborg_excavator_spawn_egg.png",
            "src/main/resources/assets/sandstorm/textures/item/cyborg_builder_spawn_egg.png",
            "src/main/resources/assets/sandstorm/textures/item/cyborg_harvester_spawn_egg.png",
            "src/main/resources/assets/sandstorm/textures/entity/cyborg/cyborg_excavator.png",
            "src/main/resources/assets/sandstorm/textures/entity/cyborg/cyborg_builder.png",
            "src/main/resources/assets/sandstorm/textures/entity/cyborg/cyborg_harvester.png"
    })
    void shouldVerifyTextureFilesHaveValidPngHeader(String texturePath) throws IOException {
        File file = new File(texturePath);
        assertTrue(file.exists());
        byte[] header = new byte[8];
        try (FileInputStream fis = new FileInputStream(file)) {
            assertEquals(8, fis.read(header));
        }
        byte[] expected = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
        assertArrayEquals(expected, header);
    }
}
