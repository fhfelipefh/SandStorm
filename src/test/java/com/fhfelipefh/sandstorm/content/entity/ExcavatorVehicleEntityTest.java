package com.fhfelipefh.sandstorm.content.entity;

import com.fhfelipefh.sandstorm.component.EnergyStorageComponent;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExcavatorVehicleEntityTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    private static Unsafe getUnsafe() throws Exception {
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        return (Unsafe) f.get(null);
    }

    @Test
    void shouldBuildExcavatorAttributes() {
        AttributeSupplier.Builder builder = ExcavatorVehicleEntity.createAttributes();
        assertNotNull(builder);

        AttributeSupplier supplier = builder.build();
        assertEquals(150.0, supplier.getBaseValue(Attributes.MAX_HEALTH), 0.001);
        assertEquals(0.28, supplier.getBaseValue(Attributes.MOVEMENT_SPEED), 0.001);
        assertEquals(20.0, supplier.getBaseValue(Attributes.ARMOR), 0.001);
        assertEquals(0.9, supplier.getBaseValue(Attributes.KNOCKBACK_RESISTANCE), 0.001);
        assertEquals(1.25, supplier.getBaseValue(Attributes.STEP_HEIGHT), 0.001);
    }

    @Test
    void shouldDefineStandardEnergySpecifications() {
        assertEquals(50000L, ExcavatorVehicleEntity.DEFAULT_BATTERY_CAPACITY);
        assertEquals(1L, ExcavatorVehicleEntity.TRAVEL_ENERGY_COST);
        assertEquals(10L, ExcavatorVehicleEntity.EXCAVATION_ENERGY_COST);
    }

    @Test
    void shouldManageBatteryEnergyStorageLifecycle() {
        EnergyStorageComponent component = new EnergyStorageComponent(
                ExcavatorVehicleEntity.DEFAULT_BATTERY_CAPACITY,
                500L,
                500L
        );
        assertEquals(50000L, component.getCapacity());
        assertEquals(0L, component.getStoredEnergy());

        component.setStoredEnergy(ExcavatorVehicleEntity.DEFAULT_BATTERY_CAPACITY);
        assertEquals(50000L, component.getStoredEnergy());
        assertTrue(component.hasEnergy(ExcavatorVehicleEntity.TRAVEL_ENERGY_COST));
        assertTrue(component.hasEnergy(ExcavatorVehicleEntity.EXCAVATION_ENERGY_COST));

        long travelExtracted = component.extractEnergy(ExcavatorVehicleEntity.TRAVEL_ENERGY_COST);
        assertEquals(1L, travelExtracted);
        assertEquals(49999L, component.getStoredEnergy());

        long excavationExtracted = component.extractEnergy(ExcavatorVehicleEntity.EXCAVATION_ENERGY_COST);
        assertEquals(10L, excavationExtracted);
        assertEquals(49989L, component.getStoredEnergy());

        component.setStoredEnergy(5L);
        assertFalse(component.hasEnergy(ExcavatorVehicleEntity.EXCAVATION_ENERGY_COST));
        assertTrue(component.hasEnergy(ExcavatorVehicleEntity.TRAVEL_ENERGY_COST));
    }

    @Test
    void shouldDefineSeismicVibrationOutput() throws Exception {
        Unsafe unsafe = getUnsafe();
        ExcavatorVehicleEntity vehicle = (ExcavatorVehicleEntity) unsafe.allocateInstance(ExcavatorVehicleEntity.class);
        assertEquals(4.5f, vehicle.getSeismicVibrationOutput(), 0.001f);
    }

    @Test
    void shouldCalculatePassengerAttachmentPoint() throws Exception {
        Unsafe unsafe = getUnsafe();
        ExcavatorVehicleEntity vehicle = (ExcavatorVehicleEntity) unsafe.allocateInstance(ExcavatorVehicleEntity.class);

        Method method = ExcavatorVehicleEntity.class.getDeclaredMethod(
                "getPassengerAttachmentPoint",
                Entity.class,
                EntityDimensions.class,
                float.class
        );
        method.setAccessible(true);

        Vec3 attachment = (Vec3) method.invoke(vehicle, null, null, 1.0f);
        assertEquals(0.0, attachment.x, 0.001);
        assertEquals(0.85, attachment.y, 0.001);
        assertEquals(-0.15, attachment.z, 0.001);

        Vec3 scaled = (Vec3) method.invoke(vehicle, null, null, 2.0f);
        assertEquals(0.0, scaled.x, 0.001);
        assertEquals(1.7, scaled.y, 0.001);
        assertEquals(-0.3, scaled.z, 0.001);
    }

    @Test
    void shouldValidateExcavationHardnessLimits() throws Exception {
        Unsafe unsafe = getUnsafe();
        ExcavatorVehicleEntity vehicle = (ExcavatorVehicleEntity) unsafe.allocateInstance(ExcavatorVehicleEntity.class);

        assertTrue(vehicle.canExcavate(Blocks.SAND.defaultBlockState(), BlockPos.ZERO));
        assertTrue(vehicle.canExcavate(Blocks.GRAVEL.defaultBlockState(), BlockPos.ZERO));
        assertTrue(vehicle.canExcavate(Blocks.STONE.defaultBlockState(), BlockPos.ZERO));
        assertTrue(vehicle.canExcavate(Blocks.SANDSTONE.defaultBlockState(), BlockPos.ZERO));
        assertTrue(vehicle.canExcavate(Blocks.IRON_ORE.defaultBlockState(), BlockPos.ZERO));

        assertFalse(vehicle.canExcavate(Blocks.OBSIDIAN.defaultBlockState(), BlockPos.ZERO));
        assertFalse(vehicle.canExcavate(Blocks.CRYING_OBSIDIAN.defaultBlockState(), BlockPos.ZERO));
        assertFalse(vehicle.canExcavate(Blocks.BEDROCK.defaultBlockState(), BlockPos.ZERO));
        assertFalse(vehicle.canExcavate(Blocks.BARRIER.defaultBlockState(), BlockPos.ZERO));
    }

    @Test
    void shouldProvideInitialEnergyStorage() throws Exception {
        Unsafe unsafe = getUnsafe();
        ExcavatorVehicleEntity vehicle = (ExcavatorVehicleEntity) unsafe.allocateInstance(ExcavatorVehicleEntity.class);

        Field energyField = ExcavatorVehicleEntity.class.getDeclaredField("energyStorage");
        energyField.setAccessible(true);
        EnergyStorageComponent comp = new EnergyStorageComponent(ExcavatorVehicleEntity.DEFAULT_BATTERY_CAPACITY, 500L, 500L);
        comp.setStoredEnergy(ExcavatorVehicleEntity.DEFAULT_BATTERY_CAPACITY);
        energyField.set(vehicle, comp);

        assertNotNull(vehicle.getEnergyStorage());
        assertEquals(50000L, vehicle.getEnergyStorage().getStoredEnergy());
    }
}
