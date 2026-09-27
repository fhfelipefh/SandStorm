package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class SpaceSuitArmorRenderer implements ArmorRenderer {
    private static final Identifier BASE_TEXTURE = SandStormMod.id("textures/entity/equipment/humanoid/space_suit.png");
    private static final Identifier GLOW_TEXTURE = SandStormMod.id("textures/entity/equipment/humanoid/space_suit_glow.png");

    private final SpaceSuitArmorModel helmetModel;
    private final SpaceSuitArmorModel chestModel;
    private final SpaceSuitArmorModel leggingsModel;
    private final SpaceSuitArmorModel bootsModel;

    public SpaceSuitArmorRenderer() {
        this.helmetModel = new SpaceSuitArmorModel(SpaceSuitArmorModel.createBodyLayer().bakeRoot());
        this.chestModel = new SpaceSuitArmorModel(SpaceSuitArmorModel.createBodyLayer().bakeRoot());
        this.leggingsModel = new SpaceSuitArmorModel(SpaceSuitArmorModel.createBodyLayer().bakeRoot());
        this.bootsModel = new SpaceSuitArmorModel(SpaceSuitArmorModel.createBodyLayer().bakeRoot());
    }

    public SpaceSuitArmorModel getModelForSlot(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> this.helmetModel;
            case CHEST -> this.chestModel;
            case LEGS -> this.leggingsModel;
            case FEET -> this.bootsModel;
            default -> this.chestModel;
        };
    }

    @Override
    public void render(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, ItemStack stack, HumanoidRenderState humanoidRenderState, EquipmentSlot slot, int light, HumanoidModel<HumanoidRenderState> contextModel) {
        SpaceSuitArmorModel slotModel = getModelForSlot(slot);
        slotModel.copyTransforms(contextModel);
        slotModel.setVisibleSlot(slot);

        RenderType baseRenderType = RenderTypes.armorCutoutNoCull(BASE_TEXTURE);
        RenderType glowRenderType = RenderTypes.eyes(GLOW_TEXTURE);

        submitNodeCollector.submitModel(slotModel, humanoidRenderState, poseStack, baseRenderType, light, OverlayTexture.NO_OVERLAY, -1);
        submitNodeCollector.submitModel(slotModel, humanoidRenderState, poseStack, glowRenderType, 0x00F000F0, OverlayTexture.NO_OVERLAY, -1);
    }
}
