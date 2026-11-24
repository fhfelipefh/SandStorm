package com.fhfelipefh.sandstorm.client;

import com.fhfelipefh.sandstorm.content.item.EmpBlasterItem;
import com.fhfelipefh.sandstorm.content.item.GravitonLapseCannonItem;
import com.fhfelipefh.sandstorm.content.item.GravitonReversalCannonItem;
import com.fhfelipefh.sandstorm.content.item.GravitonVoidCannonItem;
import com.fhfelipefh.sandstorm.content.item.HeavyPlasmaCannonItem;
import com.fhfelipefh.sandstorm.content.item.PlasmaRifleItem;
import com.fhfelipefh.sandstorm.content.item.SonicCannonItem;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.SharedConstants;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WeaponPoseTest {

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
    void testWeaponsUseAnimationIsCrossbow() throws Exception {
        Unsafe unsafe = getUnsafe();

        PlasmaRifleItem rifle = (PlasmaRifleItem) unsafe.allocateInstance(PlasmaRifleItem.class);
        assertEquals(ItemUseAnimation.CROSSBOW, rifle.getUseAnimation(ItemStack.EMPTY));

        SonicCannonItem cannon = (SonicCannonItem) unsafe.allocateInstance(SonicCannonItem.class);
        assertEquals(ItemUseAnimation.CROSSBOW, cannon.getUseAnimation(ItemStack.EMPTY));

        HeavyPlasmaCannonItem heavyCannon = (HeavyPlasmaCannonItem) unsafe.allocateInstance(HeavyPlasmaCannonItem.class);
        assertEquals(ItemUseAnimation.CROSSBOW, heavyCannon.getUseAnimation(ItemStack.EMPTY));

        EmpBlasterItem emp = (EmpBlasterItem) unsafe.allocateInstance(EmpBlasterItem.class);
        assertEquals(ItemUseAnimation.CROSSBOW, emp.getUseAnimation(ItemStack.EMPTY));

        GravitonLapseCannonItem lapse = (GravitonLapseCannonItem) unsafe.allocateInstance(GravitonLapseCannonItem.class);
        assertEquals(ItemUseAnimation.CROSSBOW, lapse.getUseAnimation(ItemStack.EMPTY));

        GravitonReversalCannonItem reversal = (GravitonReversalCannonItem) unsafe.allocateInstance(GravitonReversalCannonItem.class);
        assertEquals(ItemUseAnimation.CROSSBOW, reversal.getUseAnimation(ItemStack.EMPTY));

        GravitonVoidCannonItem voidCannon = (GravitonVoidCannonItem) unsafe.allocateInstance(GravitonVoidCannonItem.class);
        assertEquals(ItemUseAnimation.CROSSBOW, voidCannon.getUseAnimation(ItemStack.EMPTY));
    }

    @Test
    void testHumanoidArmPoseCrossbowHoldExists() {
        HumanoidModel.ArmPose pose = HumanoidModel.ArmPose.CROSSBOW_HOLD;
        assertNotNull(pose);
        assertEquals("CROSSBOW_HOLD", pose.name());
    }

    @Test
    void testWeaponModelsHaveCustomDisplayTransformations() throws IOException {
        List<String> weapons = List.of(
                "plasma_rifle",
                "sonic_cannon",
                "heavy_plasma_cannon",
                "emp_blaster",
                "graviton_lapse_cannon",
                "graviton_reversal_cannon",
                "graviton_void_cannon"
        );
        Path modelDir = Path.of("src", "main", "resources", "assets", "sandstorm", "models", "item");

        for (String weapon : weapons) {
            Path modelPath = modelDir.resolve(weapon + ".json");
            assertTrue(Files.exists(modelPath), "Model file must exist: " + modelPath);

            try (FileReader reader = new FileReader(modelPath.toFile())) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                assertTrue(json.has("display"), "Weapon model must specify display: " + weapon);
                JsonObject display = json.getAsJsonObject("display");
                assertTrue(display.has("thirdperson_righthand"), "Must define thirdperson_righthand: " + weapon);
                assertTrue(display.has("firstperson_righthand"), "Must define firstperson_righthand: " + weapon);

                if (json.has("elements") && !weapon.equals("vibro_crysknife")) {
                    for (String handKey : List.of("thirdperson_righthand", "thirdperson_lefthand", "firstperson_righthand", "firstperson_lefthand")) {
                        JsonObject hand = display.getAsJsonObject(handKey);
                        assertNotNull(hand, "3D firearm must define " + handKey + ": " + weapon);
                        double yaw = hand.getAsJsonArray("rotation").get(1).getAsDouble();
                        assertTrue(Math.abs(yaw) < 30.0,
                                "3D firearm " + weapon + " must point forward in " + handKey + " (yaw=" + yaw + ")");
                    }
                }
            }
        }
    }
}
