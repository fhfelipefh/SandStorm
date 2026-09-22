package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.content.quest.QuestData;
import com.fhfelipefh.sandstorm.content.quest.QuestRegistry;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SandwormSizeScalingTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldValidateDefaultAttributesIncludeScale() {
        AttributeSupplier supplier = SandwormEntity.createAttributes().build();
        assertEquals(300.0, supplier.getBaseValue(Attributes.MAX_HEALTH), 0.001);
        assertEquals(18.0, supplier.getBaseValue(Attributes.ATTACK_DAMAGE), 0.001);
        assertEquals(12.0, supplier.getBaseValue(Attributes.ARMOR), 0.001);
        assertEquals(0.32, supplier.getBaseValue(Attributes.MOVEMENT_SPEED), 0.001);
        assertEquals(1.0, supplier.getBaseValue(Attributes.SCALE), 0.001);
    }

    @Test
    void shouldValidateDimensionsScalePerTier() {
        EntityDimensions baseDimensions = EntityDimensions.scalable(3.8f, 10.0f);
        assertEquals(3.8f, baseDimensions.width(), 0.001f);
        assertEquals(10.0f, baseDimensions.height(), 0.001f);

        float juvenileScale = 0.6f;
        EntityDimensions juvenileDims = baseDimensions.scale(juvenileScale);
        assertEquals(3.8f * juvenileScale, juvenileDims.width(), 0.001f);
        assertEquals(10.0f * juvenileScale, juvenileDims.height(), 0.001f);

        float adultScale = 1.0f;
        EntityDimensions adultDims = baseDimensions.scale(adultScale);
        assertEquals(3.8f, adultDims.width(), 0.001f);
        assertEquals(10.0f, adultDims.height(), 0.001f);

        float colossalScale = 1.5f;
        EntityDimensions colossalDims = baseDimensions.scale(colossalScale);
        assertEquals(3.8f * colossalScale, colossalDims.width(), 0.001f);
        assertEquals(10.0f * colossalScale, colossalDims.height(), 0.001f);

        float titanScale = 2.0f;
        EntityDimensions titanDims = baseDimensions.scale(titanScale);
        assertEquals(3.8f * titanScale, titanDims.width(), 0.001f);
        assertEquals(10.0f * titanScale, titanDims.height(), 0.001f);
    }

    @Test
    void shouldValidateSpawnDistributionWeightings() {
        int juvenileCount = 0;
        int adultCount = 0;
        int colossalCount = 0;
        int titanCount = 0;

        for (int roll = 0; roll < 100; roll++) {
            int size = roll < 25 ? 1 : (roll < 80 ? 2 : (roll < 95 ? 3 : 4));
            switch (size) {
                case 1 -> juvenileCount++;
                case 2 -> adultCount++;
                case 3 -> colossalCount++;
                case 4 -> titanCount++;
            }
        }

        assertEquals(25, juvenileCount);
        assertEquals(55, adultCount);
        assertEquals(15, colossalCount);
        assertEquals(5, titanCount);
    }

    @Test
    void shouldValidateSizeDropBounds() {
        int juvenileMinChitin = 1;
        int juvenileMaxChitin = 1 + 2;
        int adultMinChitin = 3;
        int adultMaxChitin = 3 + 2;
        int colossalMinChitin = 5;
        int colossalMaxChitin = 5 + 3;
        int titanMinChitin = 6;
        int titanMaxChitin = 6 + 5;

        assertTrue(juvenileMinChitin >= 1 && juvenileMaxChitin <= 3);
        assertTrue(adultMinChitin >= 3 && adultMaxChitin <= 5);
        assertTrue(colossalMinChitin >= 5 && colossalMaxChitin <= 8);
        assertTrue(titanMinChitin >= 6 && titanMaxChitin <= 11);
    }

    @Test
    void shouldValidateLootQuestRegistrationsForSandwormDrops() {
        QuestData sandwormHarvest = QuestRegistry.getQuest("sandworm_harvest");
        assertNotNull(sandwormHarvest);
        assertEquals("sandworm_chitin", sandwormHarvest.requiredItemId().getPath());
    }
}
