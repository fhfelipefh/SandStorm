package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.AutonomousSonicTurretBlockEntity;
import com.fhfelipefh.sandstorm.content.gui.AutonomousSonicTurretMenu;
import com.fhfelipefh.sandstorm.content.network.ConfigureTurretPayload;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import io.netty.buffer.Unpooled;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.SimpleContainer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AutonomousSonicTurretTest {

    @BeforeAll
    static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void shouldVerifyTurretSpecifications() {
        assertEquals(20.0, AutonomousSonicTurretBlockEntity.RANGE, 0.001);
        assertEquals(400, AutonomousSonicTurretBlockEntity.ENERGY_PER_SHOT);
        assertEquals(16.0f, AutonomousSonicTurretBlockEntity.DAMAGE_AMOUNT, 0.001f);
        assertEquals(20, AutonomousSonicTurretBlockEntity.COOLDOWN_TICKS);
    }

    @Test
    void shouldRegisterBlockAndBlockEntity() {
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, SandStormMod.id("autonomous_sonic_turret"));
        assertNotNull(key);
        assertEquals("autonomous_sonic_turret", key.identifier().getPath());
        assertNotNull(AutonomousSonicTurretBlock.FACING);
        assertNotNull(AutonomousSonicTurretBlock.POWERED);
    }

    @Test
    void shouldVerifyTargetManagementAndDefaultHostiles() {
        BlockPos pos = new BlockPos(10, 64, 10);
        AutonomousSonicTurretBlockEntity turret = new AutonomousSonicTurretBlockEntity(BlockEntityTypes.BARREL, pos, Blocks.BARREL.defaultBlockState());

        assertEquals(0, turret.getFilterMode());
        assertEquals(0, turret.getTargetingStrategy());
        assertTrue(turret.getTargetEntityIds().contains("minecraft:zombie"));
        assertTrue(turret.getTargetEntityIds().contains("minecraft:skeleton"));
        assertTrue(turret.getTargetEntityIds().contains("sandstorm:sandworm"));

        turret.setFilterMode(1);
        assertEquals(1, turret.getFilterMode());

        turret.setTargetingStrategy(2);
        assertEquals(2, turret.getTargetingStrategy());

        turret.toggleTargetEntity("minecraft:cow");
        assertTrue(turret.getTargetEntityIds().contains("minecraft:cow"));

        turret.toggleTargetEntity("minecraft:cow");
        assertFalse(turret.getTargetEntityIds().contains("minecraft:cow"));

        turret.setTargetEntityIds(List.of("minecraft:creeper", "minecraft:spider"));
        assertEquals(2, turret.getTargetEntityIds().size());
        assertTrue(turret.getTargetEntityIds().contains("minecraft:creeper"));
        assertTrue(turret.getTargetEntityIds().contains("minecraft:spider"));
        assertFalse(turret.getTargetEntityIds().contains("minecraft:zombie"));
    }

    @Test
    void shouldSerializeAndDeserializeConfigureTurretPayload() {
        BlockPos pos = new BlockPos(25, 70, -15);
        List<String> targets = List.of("minecraft:zombie", "sandstorm:sandworm", "minecraft:enderman");
        ConfigureTurretPayload payload = new ConfigureTurretPayload(pos, 1, 2, targets);

        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), null);
        ConfigureTurretPayload.STREAM_CODEC.encode(buf, payload);

        ConfigureTurretPayload decoded = ConfigureTurretPayload.STREAM_CODEC.decode(buf);
        assertEquals(pos, decoded.pos());
        assertEquals(1, decoded.filterMode());
        assertEquals(2, decoded.targetingStrategy());
        assertEquals(3, decoded.selectedEntityTypes().size());
        assertTrue(decoded.selectedEntityTypes().contains("minecraft:zombie"));
        assertTrue(decoded.selectedEntityTypes().contains("sandstorm:sandworm"));
        assertTrue(decoded.selectedEntityTypes().contains("minecraft:enderman"));
    }

    @Test
    void shouldInitializeMenuAndDataSlots() {
        SimpleContainer container = new SimpleContainer(1);
        SimpleContainerData data = new SimpleContainerData(11);
        data.set(0, 5000);
        data.set(1, 10000);
        data.set(2, 5);
        data.set(3, 20);
        data.set(4, 1);
        data.set(5, 0);
        data.set(6, 1);
        data.set(7, 8);
        data.set(8, 100);
        data.set(9, 64);
        data.set(10, 200);

        AutonomousSonicTurretMenu menu = new AutonomousSonicTurretMenu(null, 1, new Inventory(null, null), container, data);

        assertEquals(5000, menu.getEnergy());
        assertEquals(10000, menu.getMaxEnergy());
        assertEquals(5, menu.getCooldown());
        assertEquals(20, menu.getMaxCooldown());
        assertTrue(menu.isWptConnected());
        assertEquals(0, menu.getFilterMode());
        assertEquals(1, menu.getTargetingStrategy());
        assertEquals(8, menu.getTargetCount());
        assertEquals(new BlockPos(100, 64, 200), menu.getTurretPos());
        assertEquals(50, menu.getEnergyScaled(100));
        assertEquals(0, menu.getProgressScaled(100));
        assertTrue(menu.isProcessing());
    }

    @Test
    void shouldVerifyAimTrackingAndRotationSpecifications() {
        assertEquals(10.0f, AutonomousSonicTurretBlockEntity.ROTATION_SPEED, 0.001f);
        assertEquals(12.0f, AutonomousSonicTurretBlockEntity.AIM_TOLERANCE, 0.001f);

        BlockPos pos = new BlockPos(0, 64, 0);
        AutonomousSonicTurretBlockEntity turret = new AutonomousSonicTurretBlockEntity(BlockEntityTypes.BARREL, pos, Blocks.BARREL.defaultBlockState());

        assertEquals(0.0f, turret.getCurrentYaw(), 0.001f);
        assertEquals(0.0f, turret.getCurrentPitch(), 0.001f);
        assertEquals(0.0f, turret.getPrevYaw(), 0.001f);
        assertEquals(0.0f, turret.getPrevPitch(), 0.001f);
        assertEquals(0.0f, turret.getTargetYaw(), 0.001f);
        assertEquals(0.0f, turret.getTargetPitch(), 0.001f);
        assertFalse(turret.hasTarget());
        assertEquals(0, turret.getShootFlashTicks());
        assertEquals(-1, turret.getTargetEntityId());
    }
}
