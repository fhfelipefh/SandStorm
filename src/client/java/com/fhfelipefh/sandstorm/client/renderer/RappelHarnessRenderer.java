package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.entity.RappelHarnessEntity;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public class RappelHarnessRenderer extends MobRenderer<RappelHarnessEntity, RappelHarnessRenderState, RappelHarnessModel> {
    private static final Identifier TEXTURE = SandStormMod.id("textures/entity/rappel_harness/rappel_harness.png");

    public RappelHarnessRenderer(EntityRendererProvider.Context context) {
        super(context, new RappelHarnessModel(RappelHarnessModel.createBodyLayer().bakeRoot()), 0.2f);
    }

    @Override
    public Identifier getTextureLocation(RappelHarnessRenderState state) {
        return TEXTURE;
    }

    @Override
    public RappelHarnessRenderState createRenderState() {
        return new RappelHarnessRenderState();
    }

    @Override
    public void extractRenderState(RappelHarnessEntity entity, RappelHarnessRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.isAscending = entity.isAscending();
        state.isDescending = entity.isDescending();
        state.isRidden = entity.isVehicle();
        Level lvl = entity.level();
        state.animationTicks = lvl != null ? (lvl.getGameTime() + partialTick) : partialTick;
    }
}
