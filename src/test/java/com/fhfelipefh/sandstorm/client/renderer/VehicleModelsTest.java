package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.entity.MegazordVariant;
import net.minecraft.client.model.geom.ModelPart;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VehicleModelsTest {

    private static final byte[] PNG_SIGNATURE = new byte[]{
            (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
    };

    @Test
    void shouldBakeAndAnimateSandboardModel() {
        ModelPart root = SandboardModel.createBodyLayer().bakeRoot();
        assertNotNull(root);
        SandboardModel model = new SandboardModel(root);
        assertNotNull(model.getRoot());

        SandboardRenderState state = new SandboardRenderState();
        state.isRidden = true;
        state.animationTicks = 10.0f;
        model.setupAnim(state);

        state.isRidden = false;
        model.setupAnim(state);
    }

    @Test
    void shouldBakeAndAnimateExcavatorVehicleModel() {
        ModelPart root = ExcavatorVehicleModel.createBodyLayer().bakeRoot();
        assertNotNull(root);
        ExcavatorVehicleModel model = new ExcavatorVehicleModel(root);
        assertNotNull(model.getRoot());

        ExcavatorVehicleRenderState state = new ExcavatorVehicleRenderState();
        state.isVehicle = true;
        state.animationTicks = 25.0f;
        state.storedEnergy = 50000L;
        model.setupAnim(state);
    }

    @Test
    void shouldBakeAndAnimateMegazordModel() {
        ModelPart root = MegazordModel.createBodyLayer().bakeRoot();
        assertNotNull(root);
        MegazordModel model = new MegazordModel(root);
        assertNotNull(model.getRoot());

        MegazordRenderState state = new MegazordRenderState();
        state.variant = MegazordVariant.APEX_DOMINATOR;
        state.hasFlightModule = true;
        state.hasSubmersibleModule = true;
        state.hasOverdriveModule = true;
        state.animationTicks = 50.0f;
        state.walkAnimationSpeed = 1.0f;
        state.walkAnimationPos = 5.0f;
        model.setupAnim(state);
    }

    @Test
    void shouldValidateVehicleTexturesIntegrity() throws IOException {
        String[] texturePaths = {
                "src/main/resources/assets/sandstorm/textures/entity/sandboard/sandboard.png",
                "src/main/resources/assets/sandstorm/textures/entity/excavator_vehicle/excavator_vehicle.png",
                "src/main/resources/assets/sandstorm/textures/entity/megazord/megazord.png",
                "src/main/resources/assets/sandstorm/textures/entity/megazord/megazord_aero_striker.png",
                "src/main/resources/assets/sandstorm/textures/entity/megazord/megazord_abyssal_sub.png",
                "src/main/resources/assets/sandstorm/textures/entity/megazord/megazord_apex_dominator.png"
        };

        for (String path : texturePaths) {
            File file = new File(path);
            assertTrue(file.exists(), "Texture must exist: " + path);
            assertTrue(file.length() > 0, "Texture must not be empty: " + path);

            try (FileInputStream in = new FileInputStream(file)) {
                byte[] header = new byte[8];
                int read = in.read(header);
                assertTrue(read == 8, "Header read incomplete for: " + path);
                assertArrayEquals(PNG_SIGNATURE, header, "Invalid PNG signature: " + path);
            }
        }
    }
}
