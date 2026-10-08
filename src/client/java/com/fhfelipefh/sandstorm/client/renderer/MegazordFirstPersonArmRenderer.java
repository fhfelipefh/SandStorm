package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.entity.MegazordEntity;
import com.fhfelipefh.sandstorm.content.entity.MegazordPilotProfile;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public final class MegazordFirstPersonArmRenderer {
    private static final Identifier STANDARD_TEXTURE = SandStormMod.id("textures/entity/megazord/megazord.png");
    private static final Identifier AERO_TEXTURE = SandStormMod.id("textures/entity/megazord/megazord_aero_striker.png");
    private static final Identifier SUB_TEXTURE = SandStormMod.id("textures/entity/megazord/megazord_abyssal_sub.png");
    private static final Identifier APEX_TEXTURE = SandStormMod.id("textures/entity/megazord/megazord_apex_dominator.png");
    private static final ModelPart ROOT = createLayer().bakeRoot();
    private static final ModelPart RIGHT_ARM = ROOT.getChild("rightArm");
    private static final ModelPart LEFT_ARM = ROOT.getChild("leftArm");

    private MegazordFirstPersonArmRenderer() {
    }

    public static boolean renderRightArm(PoseStack poseStack, SubmitNodeCollector collector, int packedLight) {
        return renderArm(poseStack, collector, packedLight, RIGHT_ARM, false);
    }

    public static boolean renderLeftArm(PoseStack poseStack, SubmitNodeCollector collector, int packedLight) {
        return renderArm(poseStack, collector, packedLight, LEFT_ARM, true);
    }

    private static boolean renderArm(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, ModelPart arm, boolean left) {
        Minecraft client = Minecraft.getInstance();
        if (!(client.player != null && client.player.getVehicle() instanceof MegazordEntity megazord)
                || !client.options.getCameraType().isFirstPerson()
                || !MegazordPilotProfile.isPilot(client.player, megazord)) {
            return false;
        }

        arm.resetPose();
        arm.visible = true;
        float movement = Mth.sin(client.player.tickCount * 0.12f) * 0.025f;
        arm.xRot = movement;
        arm.yRot = left ? -0.08f : 0.08f;
        arm.zRot = left ? -0.06f : 0.06f;
        Identifier texture = textureFor(megazord);
        collector.submitModelPart(arm, poseStack, RenderTypes.armorCutoutNoCull(texture), packedLight, OverlayTexture.NO_OVERLAY, null);
        return true;
    }

    private static Identifier textureFor(MegazordEntity megazord) {
        return switch (megazord.getVariant()) {
            case AERO_STRIKER -> AERO_TEXTURE;
            case ABYSSAL_SUB -> SUB_TEXTURE;
            case APEX_DOMINATOR -> APEX_TEXTURE;
            default -> STANDARD_TEXTURE;
        };
    }

    private static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("rightArm", CubeListBuilder.create()
                .texOffs(154, 28).addBox(-8.0f, -2.0f, -4.0f, 9.0f, 5.0f, 8.0f)
                .texOffs(190, 28).addBox(-5.0f, 3.0f, -2.5f, 5.0f, 6.0f, 5.0f)
                .texOffs(212, 28).addBox(-6.5f, 9.0f, -3.5f, 7.0f, 8.0f, 7.0f)
                .texOffs(0, 45).addBox(-5.0f, 17.0f, -2.0f, 4.0f, 3.0f, 4.0f),
                PartPose.offset(-1.5f, -1.0f, 0.0f));
        root.addOrReplaceChild("leftArm", CubeListBuilder.create()
                .texOffs(48, 28).addBox(-1.0f, -2.0f, -4.0f, 9.0f, 5.0f, 8.0f)
                .texOffs(84, 28).addBox(0.0f, 3.0f, -2.5f, 5.0f, 6.0f, 5.0f)
                .texOffs(106, 28).addBox(-0.5f, 9.0f, -3.5f, 7.0f, 8.0f, 7.0f)
                .texOffs(136, 28).addBox(1.0f, 17.0f, -2.0f, 4.0f, 3.0f, 4.0f),
                PartPose.offset(1.5f, -1.0f, 0.0f));
        return LayerDefinition.create(mesh, 256, 256);
    }
}
