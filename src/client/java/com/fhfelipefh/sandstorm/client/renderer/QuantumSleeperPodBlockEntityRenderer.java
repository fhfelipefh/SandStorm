package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.block.QuantumSleeperPodBlock;
import com.fhfelipefh.sandstorm.content.block.entity.QuantumSleeperPodBlockEntity;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class QuantumSleeperPodBlockEntityRenderer implements BlockEntityRenderer<QuantumSleeperPodBlockEntity, QuantumSleeperPodRenderState> {
    private static final Identifier TEXTURE = SandStormMod.id("textures/entity/quantum_sleeper_pod/quantum_chamber.png");
    private static final Identifier SUIT_TEXTURE = SandStormMod.id("textures/entity/equipment/humanoid/space_suit.png");
    private static final Identifier STEVE_TEXTURE = Identifier.withDefaultNamespace("textures/entity/player/wide/steve.png");

    private final QuantumSleeperPodModel model;
    private final SpaceSuitArmorModel suitModel;
    private final HumanoidModel<HumanoidRenderState> steveModel;

    public QuantumSleeperPodBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new QuantumSleeperPodModel(QuantumSleeperPodModel.createBodyLayer().bakeRoot());
        this.suitModel = new SpaceSuitArmorModel(SpaceSuitArmorModel.createBodyLayer().bakeRoot());

        MeshDefinition steveMesh = HumanoidModel.createMesh(new CubeDeformation(0.0f), 0.0f);
        ModelPart stevePart = LayerDefinition.create(steveMesh, 64, 64).bakeRoot();
        this.steveModel = new HumanoidModel<>(stevePart);
    }

    @Override
    public QuantumSleeperPodRenderState createRenderState() {
        return new QuantumSleeperPodRenderState();
    }

    @Override
    public int getViewDistance() {
        return 128;
    }

    @Override
    public void extractRenderState(QuantumSleeperPodBlockEntity entity, QuantumSleeperPodRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(entity, state, crumblingOverlay);
        BlockState blockState = entity.getBlockState();
        if (blockState.hasProperty(QuantumSleeperPodBlock.FACING)) {
            state.facing = blockState.getValue(QuantumSleeperPodBlock.FACING);
        }
        if (blockState.hasProperty(QuantumSleeperPodBlock.ACTIVE)) {
            state.active = blockState.getValue(QuantumSleeperPodBlock.ACTIVE);
        }
        if (blockState.hasProperty(QuantumSleeperPodBlock.OPEN)) {
            state.open = blockState.getValue(QuantumSleeperPodBlock.OPEN);
        }
        state.hasClone = entity.hasClone();
        state.bioNutrients = entity.getBioNutrients();
        state.energy = entity.getStoredEnergy();
        state.ownerUuid = entity.getOwnerUuid();
        state.customPodName = entity.getCustomPodName();

        Level level = entity.getLevel();
        state.animationTicks = level != null ? (level.getGameTime() + partialTick) : partialTick;
        state.doorProgress = state.open ? 1.0f : 0.0f;
    }

    @Override
    public void submit(QuantumSleeperPodRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.0, 0.5);
        float yRot = -state.facing.toYRot();
        poseStack.rotateDegrees(Axis.YP, yRot);

        int light = state.lightCoords != 0 ? state.lightCoords : 15728880;

        collector.submitModelPart(this.model.getBase(), poseStack, RenderTypes.entityCutout(TEXTURE), light, OverlayTexture.NO_OVERLAY, null);
        collector.submitModelPart(this.model.getTopCap(), poseStack, RenderTypes.entityCutout(TEXTURE), light, OverlayTexture.NO_OVERLAY, null);
        collector.submitModelPart(this.model.getSpine(), poseStack, RenderTypes.entityCutout(TEXTURE), light, OverlayTexture.NO_OVERLAY, null);
        collector.submitModelPart(this.model.getCornerStruts(), poseStack, RenderTypes.entityCutout(TEXTURE), light, OverlayTexture.NO_OVERLAY, null);

        if (state.hasClone) {
            float breath = Mth.sin(state.animationTicks * 0.05f) * 0.015f;

            poseStack.pushPose();
            poseStack.translate(0.0, 1.235, 0.0);
            poseStack.rotateDegrees(Axis.XP, 180.0f);
            poseStack.scale(0.74f, 0.74f, 0.74f);

            this.steveModel.head.xRot = 0.02f + breath;
            this.steveModel.head.yRot = 0.0f;
            this.steveModel.head.zRot = 0.0f;
            this.steveModel.body.xRot = 0.0f;

            this.steveModel.rightArm.setPos(-2.4f, 2.0f, 0.0f);
            this.steveModel.leftArm.setPos(2.4f, 2.0f, 0.0f);
            this.steveModel.rightArm.xRot = 0.0f;
            this.steveModel.leftArm.xRot = 0.0f;
            this.steveModel.rightArm.yRot = 0.0f;
            this.steveModel.leftArm.yRot = 0.0f;
            this.steveModel.rightArm.zRot = 0.0f;
            this.steveModel.leftArm.zRot = 0.0f;

            this.steveModel.rightLeg.xRot = 0.0f;
            this.steveModel.leftLeg.xRot = 0.0f;

            this.suitModel.head.xRot = this.steveModel.head.xRot;
            this.suitModel.head.yRot = this.steveModel.head.yRot;
            this.suitModel.head.zRot = this.steveModel.head.zRot;
            this.suitModel.body.xRot = this.steveModel.body.xRot;

            this.suitModel.rightArm.setPos(-2.4f, 2.0f, 0.0f);
            this.suitModel.leftArm.setPos(2.4f, 2.0f, 0.0f);
            this.suitModel.rightArm.xRot = 0.0f;
            this.suitModel.leftArm.xRot = 0.0f;
            this.suitModel.rightArm.yRot = 0.0f;
            this.suitModel.leftArm.yRot = 0.0f;
            this.suitModel.rightArm.zRot = 0.0f;
            this.suitModel.leftArm.zRot = 0.0f;

            this.suitModel.rightLeg.xRot = 0.0f;
            this.suitModel.leftLeg.xRot = 0.0f;

            this.suitModel.showAllSlots();

            collector.submitModelPart(this.steveModel.head, poseStack, RenderTypes.entityCutout(STEVE_TEXTURE), light, OverlayTexture.NO_OVERLAY, null);
            collector.submitModelPart(this.steveModel.body, poseStack, RenderTypes.entityCutout(STEVE_TEXTURE), light, OverlayTexture.NO_OVERLAY, null);
            collector.submitModelPart(this.steveModel.rightArm, poseStack, RenderTypes.entityCutout(STEVE_TEXTURE), light, OverlayTexture.NO_OVERLAY, null);
            collector.submitModelPart(this.steveModel.leftArm, poseStack, RenderTypes.entityCutout(STEVE_TEXTURE), light, OverlayTexture.NO_OVERLAY, null);
            collector.submitModelPart(this.steveModel.rightLeg, poseStack, RenderTypes.entityCutout(STEVE_TEXTURE), light, OverlayTexture.NO_OVERLAY, null);
            collector.submitModelPart(this.steveModel.leftLeg, poseStack, RenderTypes.entityCutout(STEVE_TEXTURE), light, OverlayTexture.NO_OVERLAY, null);

            collector.submitModelPart(this.suitModel.head, poseStack, RenderTypes.armorCutoutNoCull(SUIT_TEXTURE), light, OverlayTexture.NO_OVERLAY, null);
            collector.submitModelPart(this.suitModel.body, poseStack, RenderTypes.armorCutoutNoCull(SUIT_TEXTURE), light, OverlayTexture.NO_OVERLAY, null);
            collector.submitModelPart(this.suitModel.rightArm, poseStack, RenderTypes.armorCutoutNoCull(SUIT_TEXTURE), light, OverlayTexture.NO_OVERLAY, null);
            collector.submitModelPart(this.suitModel.leftArm, poseStack, RenderTypes.armorCutoutNoCull(SUIT_TEXTURE), light, OverlayTexture.NO_OVERLAY, null);
            collector.submitModelPart(this.suitModel.rightLeg, poseStack, RenderTypes.armorCutoutNoCull(SUIT_TEXTURE), light, OverlayTexture.NO_OVERLAY, null);
            collector.submitModelPart(this.suitModel.leftLeg, poseStack, RenderTypes.armorCutoutNoCull(SUIT_TEXTURE), light, OverlayTexture.NO_OVERLAY, null);

            poseStack.popPose();
        }

        collector.submitModelPart(this.model.getSideGlass(), poseStack, RenderTypes.entityTranslucentCull(TEXTURE), light, OverlayTexture.NO_OVERLAY, null);

        float doorSlide = (state.doorProgress * 5.5f) / 16.0f;

        poseStack.pushPose();
        poseStack.translate(-doorSlide, 0.0f, 0.0f);
        collector.submitModelPart(this.model.getLeftDoor(), poseStack, RenderTypes.entityTranslucentCull(TEXTURE), light, OverlayTexture.NO_OVERLAY, null);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(doorSlide, 0.0f, 0.0f);
        collector.submitModelPart(this.model.getRightDoor(), poseStack, RenderTypes.entityTranslucentCull(TEXTURE), light, OverlayTexture.NO_OVERLAY, null);
        poseStack.popPose();

        poseStack.popPose();
    }
}
