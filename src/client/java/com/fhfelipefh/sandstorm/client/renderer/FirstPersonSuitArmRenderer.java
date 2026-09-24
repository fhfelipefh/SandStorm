package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.item.SpaceSuitItem;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class FirstPersonSuitArmRenderer {
    private static final Identifier BASE_TEXTURE = SandStormMod.id("textures/entity/equipment/humanoid/space_suit.png");
    private static final Identifier GLOW_TEXTURE = SandStormMod.id("textures/entity/equipment/humanoid/space_suit_glow.png");

    private static final ModelPart ROOT = SpaceSuitArmorModel.createBodyLayer().bakeRoot();
    private static final ModelPart RIGHT_ARM = ROOT.getChild("right_arm");
    private static final ModelPart LEFT_ARM = ROOT.getChild("left_arm");

    public static Identifier getBaseTexture() {
        return BASE_TEXTURE;
    }

    public static Identifier getGlowTexture() {
        return GLOW_TEXTURE;
    }

    public static ModelPart getRightArm() {
        return RIGHT_ARM;
    }

    public static ModelPart getLeftArm() {
        return LEFT_ARM;
    }

    public static boolean hasSuitEquipped() {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || player.isSpectator()) {
            return false;
        }
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!chest.isEmpty() && (chest.is(SandStormItems.SPACE_SUIT_CHESTPLATE) || chest.getItem() instanceof SpaceSuitItem)) {
            return true;
        }
        return player.entityTags().contains("sandstorm.fused_suit");
    }

    public static boolean renderRightArm(PoseStack poseStack, SubmitNodeCollector collector, int packedLight) {
        if (!hasSuitEquipped()) {
            return false;
        }
        RIGHT_ARM.resetPose();
        RIGHT_ARM.visible = true;
        RIGHT_ARM.zRot = 0.1F;
        RenderType baseType = RenderTypes.armorCutoutNoCull(BASE_TEXTURE);
        RenderType glowType = RenderTypes.eyes(GLOW_TEXTURE);
        collector.submitModelPart(RIGHT_ARM, poseStack, baseType, packedLight, OverlayTexture.NO_OVERLAY, null);
        collector.submitModelPart(RIGHT_ARM, poseStack, glowType, 0x00F000F0, OverlayTexture.NO_OVERLAY, null);
        return true;
    }

    public static boolean renderLeftArm(PoseStack poseStack, SubmitNodeCollector collector, int packedLight) {
        if (!hasSuitEquipped()) {
            return false;
        }
        LEFT_ARM.resetPose();
        LEFT_ARM.visible = true;
        LEFT_ARM.zRot = -0.1F;
        RenderType baseType = RenderTypes.armorCutoutNoCull(BASE_TEXTURE);
        RenderType glowType = RenderTypes.eyes(GLOW_TEXTURE);
        collector.submitModelPart(LEFT_ARM, poseStack, baseType, packedLight, OverlayTexture.NO_OVERLAY, null);
        collector.submitModelPart(LEFT_ARM, poseStack, glowType, 0x00F000F0, OverlayTexture.NO_OVERLAY, null);
        return true;
    }
}
