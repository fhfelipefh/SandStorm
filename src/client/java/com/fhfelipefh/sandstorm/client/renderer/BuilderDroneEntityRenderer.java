package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.entity.BuilderDroneEntity;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class BuilderDroneEntityRenderer extends MobRenderer<BuilderDroneEntity, BuilderDroneRenderState, BuilderDroneModel> {
    private static final Identifier TEXTURE = SandStormMod.id("textures/entity/builder_drone/builder_drone.png");

    public BuilderDroneEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new BuilderDroneModel(BuilderDroneModel.createBodyLayer().bakeRoot()), 0.35f);
    }

    @Override
    public Identifier getTextureLocation(BuilderDroneRenderState state) {
        return TEXTURE;
    }

    @Override
    public BuilderDroneRenderState createRenderState() {
        return new BuilderDroneRenderState();
    }

    @Override
    public void extractRenderState(BuilderDroneEntity entity, BuilderDroneRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.isWelding = entity.isWelding();
        state.hasBlock = entity.hasBlock();
        state.targetPos = entity.getTargetPos();
        if (state.isWelding && !state.targetPos.equals(BlockPos.ZERO)) {
            Vec3 targetWorld = Vec3.atCenterOf(state.targetPos);
            state.targetRelVec = targetWorld.subtract(entity.position());
        } else {
            state.targetRelVec = null;
        }
        Level lvl = entity.level();
        state.animationTicks = lvl != null ? (lvl.getGameTime() + partialTick) : partialTick;
    }

    @Override
    public void submit(BuilderDroneRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        super.submit(state, poseStack, collector, camera);

        if (state.isWelding && state.targetRelVec != null) {
            renderLaserBeam(state, poseStack, collector);
        }
    }

    private void renderLaserBeam(BuilderDroneRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
        float dx = (float) state.targetRelVec.x;
        float dy = (float) state.targetRelVec.y;
        float dz = (float) state.targetRelVec.z;

        collector.submitCustomGeometry(poseStack, RenderTypes.LINES, (pose, consumer) -> {
            consumer.addVertex(pose, 0.0f, 0.2f, 0.0f).setColor(255, 230, 100, 255).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(3.0f);
            consumer.addVertex(pose, dx, dy, dz).setColor(255, 60, 0, 255).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(3.0f);

            consumer.addVertex(pose, 0.0f, 0.2f, 0.0f).setColor(255, 255, 255, 200).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
            consumer.addVertex(pose, dx, dy, dz).setColor(255, 200, 0, 200).setNormal(pose, 0.0f, 1.0f, 0.0f).setLineWidth(1.0f);

            float r = 0.35f;
            consumer.addVertex(pose, dx - r, dy, dz).setColor(255, 100, 0, 200).setNormal(pose, 1.0f, 0.0f, 0.0f).setLineWidth(2.0f);
            consumer.addVertex(pose, dx + r, dy, dz).setColor(255, 100, 0, 200).setNormal(pose, 1.0f, 0.0f, 0.0f).setLineWidth(2.0f);
            consumer.addVertex(pose, dx, dy, dz - r).setColor(255, 100, 0, 200).setNormal(pose, 0.0f, 0.0f, 1.0f).setLineWidth(2.0f);
            consumer.addVertex(pose, dx, dy, dz + r).setColor(255, 100, 0, 200).setNormal(pose, 0.0f, 0.0f, 1.0f).setLineWidth(2.0f);
        });
    }
}
