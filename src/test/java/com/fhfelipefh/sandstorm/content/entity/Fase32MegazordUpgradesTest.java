package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.content.command.SandstormDebugCommand;
import com.fhfelipefh.sandstorm.content.quest.QuestData;
import com.fhfelipefh.sandstorm.content.quest.QuestRegistry;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.SharedConstants;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Fase32MegazordUpgradesTest {

    @BeforeAll
    static void init() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        for (Item item : BuiltInRegistries.ITEM) {
            if (!item.builtInRegistryHolder().areComponentsBound()) {
                item.builtInRegistryHolder().bindComponents(DataComponentMap.EMPTY);
            }
        }
    }

    @Test
    void shouldRegisterPhase32ItemKeys() {
        ResourceKey<Item> flightModule = SandStormMod.itemKey("megazord_flight_module");
        ResourceKey<Item> submersibleHull = SandStormMod.itemKey("megazord_submersible_hull");
        ResourceKey<Item> tacticalOverdrive = SandStormMod.itemKey("megazord_tactical_overdrive");
        ResourceKey<Item> vectoredThruster = SandStormMod.itemKey("vectored_thruster");
        ResourceKey<Item> hydroBallastPump = SandStormMod.itemKey("hydro_ballast_pump");

        assertNotNull(flightModule);
        assertNotNull(submersibleHull);
        assertNotNull(tacticalOverdrive);
        assertNotNull(vectoredThruster);
        assertNotNull(hydroBallastPump);

        assertEquals("megazord_flight_module", flightModule.identifier().getPath());
        assertEquals("megazord_submersible_hull", submersibleHull.identifier().getPath());
        assertEquals("megazord_tactical_overdrive", tacticalOverdrive.identifier().getPath());
        assertEquals("vectored_thruster", vectoredThruster.identifier().getPath());
        assertEquals("hydro_ballast_pump", hydroBallastPump.identifier().getPath());
    }

    @Test
    void shouldValidateMegazordVariantResolution() {
        assertEquals(MegazordVariant.STANDARD, MegazordVariant.resolve(false, false));
        assertEquals(MegazordVariant.AERO_STRIKER, MegazordVariant.resolve(true, false));
        assertEquals(MegazordVariant.ABYSSAL_SUB, MegazordVariant.resolve(false, true));
        assertEquals(MegazordVariant.APEX_DOMINATOR, MegazordVariant.resolve(true, true));

        assertEquals(MegazordVariant.STANDARD, MegazordVariant.fromIndex(0));
        assertEquals(MegazordVariant.AERO_STRIKER, MegazordVariant.fromIndex(1));
        assertEquals(MegazordVariant.ABYSSAL_SUB, MegazordVariant.fromIndex(2));
        assertEquals(MegazordVariant.APEX_DOMINATOR, MegazordVariant.fromIndex(3));
        assertEquals(MegazordVariant.STANDARD, MegazordVariant.fromIndex(99));

        assertEquals("standard", MegazordVariant.STANDARD.getId());
        assertEquals("aero_striker", MegazordVariant.AERO_STRIKER.getId());
        assertEquals("abyssal_sub", MegazordVariant.ABYSSAL_SUB.getId());
        assertEquals("apex_dominator", MegazordVariant.APEX_DOMINATOR.getId());
    }

    @Test
    void shouldValidateMegazordModuleSpecifications() {
        assertEquals(100000L, MegazordEntity.DEFAULT_BATTERY_CAPACITY);
        assertEquals(250000L, MegazordEntity.OVERDRIVE_BATTERY_CAPACITY);
        assertEquals(16.0, MegazordEntity.SHOCKWAVE_RADIUS, 0.001);
        assertEquals(24.0, MegazordEntity.OVERDRIVE_SHOCKWAVE_RADIUS, 0.001);
        assertEquals(25.0f, MegazordEntity.DEFAULT_SHOCKWAVE_DAMAGE, 0.001f);
        assertEquals(45.0f, MegazordEntity.OVERDRIVE_SHOCKWAVE_DAMAGE, 0.001f);
    }

    @Test
    void shouldValidateMegazordAttributesWithFlight() {
        AttributeSupplier.Builder builder = MegazordEntity.createAttributes();
        assertNotNull(builder);

        AttributeSupplier supplier = builder.build();
        assertEquals(500.0, supplier.getBaseValue(Attributes.MAX_HEALTH), 0.001);
        assertEquals(0.26, supplier.getBaseValue(Attributes.MOVEMENT_SPEED), 0.001);
        assertEquals(0.50, supplier.getBaseValue(Attributes.FLYING_SPEED), 0.001);
        assertEquals(25.0, supplier.getBaseValue(Attributes.ARMOR), 0.001);
        assertEquals(1.0, supplier.getBaseValue(Attributes.KNOCKBACK_RESISTANCE), 0.001);
        assertEquals(30.0, supplier.getBaseValue(Attributes.ATTACK_DAMAGE), 0.001);
    }

    @Test
    void shouldValidatePhase32DataAndRecipeFilesExist() {
        assertTrue(new File("src/main/resources/assets/sandstorm/items/megazord_flight_module.json").exists());
        assertTrue(new File("src/main/resources/assets/sandstorm/items/megazord_submersible_hull.json").exists());
        assertTrue(new File("src/main/resources/assets/sandstorm/items/megazord_tactical_overdrive.json").exists());
        assertTrue(new File("src/main/resources/assets/sandstorm/items/vectored_thruster.json").exists());
        assertTrue(new File("src/main/resources/assets/sandstorm/items/hydro_ballast_pump.json").exists());

        assertTrue(new File("src/main/resources/assets/sandstorm/models/item/megazord_flight_module.json").exists());
        assertTrue(new File("src/main/resources/assets/sandstorm/models/item/megazord_submersible_hull.json").exists());
        assertTrue(new File("src/main/resources/assets/sandstorm/models/item/megazord_tactical_overdrive.json").exists());
        assertTrue(new File("src/main/resources/assets/sandstorm/models/item/vectored_thruster.json").exists());
        assertTrue(new File("src/main/resources/assets/sandstorm/models/item/hydro_ballast_pump.json").exists());

        assertTrue(new File("src/main/resources/data/sandstorm/recipe/megazord_flight_module.json").exists());
        assertTrue(new File("src/main/resources/data/sandstorm/recipe/megazord_submersible_hull.json").exists());
        assertTrue(new File("src/main/resources/data/sandstorm/recipe/megazord_tactical_overdrive.json").exists());
        assertTrue(new File("src/main/resources/data/sandstorm/recipe/vectored_thruster.json").exists());
        assertTrue(new File("src/main/resources/data/sandstorm/recipe/hydro_ballast_pump.json").exists());
    }

    @Test
    void shouldIncludePhase32InDebugCommandSuite() {
        List<String> phases = SandstormDebugCommand.getSupportedPhases();
        assertTrue(phases.contains("32"));
        assertTrue(phases.contains("all"));

        List<String> spawnables = SandstormDebugCommand.getSupportedSpawnables();
        assertTrue(spawnables.contains("megazord"));
        assertTrue(spawnables.contains("megazord_flight"));
        assertTrue(spawnables.contains("megazord_sub"));
        assertTrue(spawnables.contains("megazord_apex"));
    }

    @Test
    void shouldValidateQuestRegistrationForMegazordUpgrades() {
        QuestData quest = QuestRegistry.getQuest("megazord_upgrades");
        assertNotNull(quest);
        assertEquals(4, quest.chapter());
        assertEquals(SandStormMod.id("megazord_flight_module"), quest.requiredItemId());
        assertTrue(quest.prerequisiteIds().contains("mecha_assembly"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "src/main/resources/assets/sandstorm/textures/item/megazord_flight_module.png",
            "src/main/resources/assets/sandstorm/textures/item/megazord_submersible_hull.png",
            "src/main/resources/assets/sandstorm/textures/item/megazord_tactical_overdrive.png",
            "src/main/resources/assets/sandstorm/textures/item/vectored_thruster.png",
            "src/main/resources/assets/sandstorm/textures/item/hydro_ballast_pump.png"
    })
    void shouldValidatePhase32PngIntegrity(String path) throws IOException {
        File file = new File(path);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] header = new byte[8];
            int read = fis.read(header);
            assertEquals(8, read);
            byte[] expectedPngHeader = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
            assertArrayEquals(expectedPngHeader, header);
        }
    }
}
