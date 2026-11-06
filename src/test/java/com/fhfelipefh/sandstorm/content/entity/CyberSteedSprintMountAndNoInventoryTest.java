package com.fhfelipefh.sandstorm.content.entity;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CyberSteedSprintMountAndNoInventoryTest {

    private static final Path STEED_SOURCE = Path.of(
            "src", "main", "java", "com", "fhfelipefh", "sandstorm",
            "content", "entity", "CyberSteedEntity.java"
    );

    private static final Path MIXIN_SOURCE = Path.of(
            "src", "main", "java", "com", "fhfelipefh", "sandstorm",
            "mixin", "AbstractHorseMixin.java"
    );

    @BeforeAll
    static void init() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void testPickRadiusExpandsHitboxBeyondSingleBlock() throws Exception {
        Method getPickRadius = CyberSteedEntity.class.getDeclaredMethod("getPickRadius");
        assertNotNull(getPickRadius);
        assertEquals(float.class, getPickRadius.getReturnType());

        String content = Files.readString(STEED_SOURCE);
        assertTrue(content.contains("return 1.25f;"));
    }

    @Test
    void testSprintMountPriorityInCyberSteedEntity() throws IOException {
        String content = Files.readString(STEED_SOURCE);
        int sprintIndex = content.indexOf("if (!this.isVehicle() && player.isSprinting())");
        assertTrue(sprintIndex >= 0);

        String sprintBlock = content.substring(sprintIndex, sprintIndex + 650);
        assertTrue(sprintBlock.contains("this.setOrderedToSit(false)"));
        assertTrue(sprintBlock.contains("player.startRiding(this)"));
        assertTrue(sprintBlock.contains("return InteractionResult.SUCCESS;"));
    }

    @Test
    void testSitStandToggleRestrictedToEmptyHandOrRepairToolWhenStationary() throws IOException {
        String content = Files.readString(STEED_SOURCE);
        int secIndex = content.indexOf("if (player.isSecondaryUseActive())");
        assertTrue(secIndex >= 0);

        String secBlock = content.substring(secIndex, secIndex + 250);
        assertTrue(secBlock.contains("canToggleStandby"));
        assertTrue(secBlock.contains("this.setOrderedToSit(!this.isOrderedToSit())"));
        assertFalse(secBlock.contains("cargoInventory"));

        int helperIndex = content.indexOf("boolean canToggleStandby(Player player, ItemStack itemStack)");
        assertTrue(helperIndex >= 0);
        String helperBlock = content.substring(helperIndex, helperIndex + 200);
        assertTrue(helperBlock.contains("!player.isSprinting()"));
        assertTrue(helperBlock.contains("itemStack.isEmpty() || itemStack.is(SandStormItems.REPAIR_TOOL)"));
    }

    @Test
    void testSittingCyberSteedWakesUpAndMountsOnSingleInteraction() throws IOException {
        String content = Files.readString(STEED_SOURCE);
        int rideIndex = content.indexOf("if (!this.isVehicle()) {");
        assertTrue(rideIndex >= 0);

        String rideBlock = content.substring(rideIndex, rideIndex + 650);
        assertTrue(rideBlock.contains("if (this.isOrderedToSit())"));
        assertTrue(rideBlock.contains("this.setOrderedToSit(false)"));
        assertTrue(rideBlock.contains("player.startRiding(this)"));
    }

    @Test
    void testAbstractHorseMixinPriorityMountWhenSprinting() throws IOException {
        assertTrue(Files.exists(MIXIN_SOURCE));
        String mixinContent = Files.readString(MIXIN_SOURCE);

        assertTrue(mixinContent.contains("@Inject(method = \"mobInteract\", at = @At(\"HEAD\"), cancellable = true)"));
        assertTrue(mixinContent.contains("sandstorm$priorityMountWhenRushing"));
        assertTrue(mixinContent.contains("player.isSprinting()"));
        assertTrue(mixinContent.contains("this.doPlayerRide(player)"));
        assertTrue(mixinContent.contains("cir.setReturnValue(InteractionResult.SUCCESS)"));
    }

    @Test
    void testAbstractHorseMixinSuppressesAccidentalInventoryScreen() throws IOException {
        String mixinContent = Files.readString(MIXIN_SOURCE);

        assertTrue(mixinContent.contains("@Redirect("));
        assertTrue(mixinContent.contains("sandstorm$suppressInventoryScreenWhenMounting"));
        assertTrue(mixinContent.contains("if (player.isSprinting())"));
        assertTrue(mixinContent.contains("return false;"));
        assertTrue(mixinContent.contains("isEquippableInSlot(main, EquipmentSlot.BODY)"));
    }

    @Test
    void testWeaponsAndToolsNeverHijackMountingForSitOrInventory() throws IOException {
        String steedContent = Files.readString(STEED_SOURCE);
        int helperIndex = steedContent.indexOf("boolean canToggleStandby(Player player, ItemStack itemStack)");
        assertTrue(helperIndex >= 0);
        String helperBlock = steedContent.substring(helperIndex, helperIndex + 200);
        assertTrue(helperBlock.contains("itemStack.isEmpty() || itemStack.is(SandStormItems.REPAIR_TOOL)"));

        String mixinContent = Files.readString(MIXIN_SOURCE);
        assertTrue(mixinContent.contains("!main.isEmpty() && !horse.isEquippableInSlot(main, EquipmentSlot.BODY)"));
    }
}
