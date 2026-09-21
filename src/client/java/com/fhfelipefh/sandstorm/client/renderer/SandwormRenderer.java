package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.entity.SandwormEntity;
import com.fhfelipefh.sandstorm.content.entity.ai.SandwormState;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class SandwormRenderer extends MobRenderer<SandwormEntity, SandwormRenderState, SandwormModel> {
    private static final Identifier TEXTURE = SandStormMod.id("textures/entity/sandworm/sandworm.png");

    public SandwormRenderer(EntityRendererProvider.Context context) {
        super(context, new SandwormModel(SandwormModel.createBodyLayer().bakeRoot()), 4.5f);
    }

    @Override
    public Identifier getTextureLocation(SandwormRenderState state) {
        return TEXTURE;
    }

    @Override
    public SandwormRenderState createRenderState() {
        return new SandwormRenderState();
    }

    @Override
    public void extractRenderState(SandwormEntity entity, SandwormRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        SandwormState wormState = entity.getSandwormState();
        state.burrowed = wormState == SandwormState.BURROWED;
        state.breaching = wormState == SandwormState.BREACHING;
        state.surfaced = wormState == SandwormState.SURFACED_ASSAULT;
        state.submerging = wormState == SandwormState.SUBMERGING;
        state.breachProgress = entity.getBreachAnimationProgress(partialTick);
        state.biteProgress = entity.getBiteAnimationProgress(partialTick);
        state.bodyPitch = entity.getXRot();
        state.bodyYaw = entity.getYRot();

        LivingEntity target = entity.getTarget();
        if (target != null && target.isAlive()) {
            state.hasTarget = true;
            Vec3 wormPos = entity.position();
            Vec3 targetPos = target.position();
            double dx = targetPos.x - wormPos.x;
            double dy = (targetPos.y + target.getEyeHeight() * 0.5) - (wormPos.y + 12.0);
            double dz = targetPos.z - wormPos.z;
            double horizontalDist = Math.sqrt(dx * dx + dz * dz);
            state.targetDistance = (float) horizontalDist;
            float targetYaw = (float) (Mth.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0f;
            state.targetRelativeYaw = Mth.wrapDegrees(targetYaw - entity.getYRot());
            float pitchToTarget = (float) (-Mth.atan2(dy, horizontalDist));
            state.targetPitch = Mth.clamp(pitchToTarget, 0.2f, 1.35f);
        } else {
            state.hasTarget = false;
            state.targetDistance = 0.0f;
            state.targetPitch = 0.45f;
            state.targetRelativeYaw = 0.0f;
        }

        state.rearingProgress = entity.getRearingProgress();
        Vec3 vel = entity.getDeltaMovement();
        double speedSq = vel.x * vel.x + vel.z * vel.z;
        state.isSlithering = speedSq > 0.001 || state.rearingProgress < 0.85f;
        state.slitherProgress = (entity.tickCount + partialTick) * 0.22f;
    }

    @Override
    protected boolean isBodyVisible(SandwormRenderState state) {
        return super.isBodyVisible(state);
    }

    @Override
    protected void scale(SandwormRenderState state, PoseStack poseStack) {
        super.scale(state, poseStack);
        float scaleFactor = 7.5f;
        poseStack.scale(scaleFactor, scaleFactor, scaleFactor);
    }
}
