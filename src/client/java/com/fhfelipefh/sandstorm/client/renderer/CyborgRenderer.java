package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgEntity;
import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgSpecialty;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public class CyborgRenderer extends MobRenderer<CyborgEntity, CyborgRenderState, CyborgModel> {
    private static final Identifier TEXTURE_EXCAVATOR = SandStormMod.id("textures/entity/cyborg/cyborg_excavator.png");
    private static final Identifier TEXTURE_BUILDER = SandStormMod.id("textures/entity/cyborg/cyborg_builder.png");
    private static final Identifier TEXTURE_HARVESTER = SandStormMod.id("textures/entity/cyborg/cyborg_harvester.png");

    public CyborgRenderer(EntityRendererProvider.Context context) {
        super(context, new CyborgModel(CyborgModel.createBodyLayer().bakeRoot()), 0.5f);
    }

    @Override
    public Identifier getTextureLocation(CyborgRenderState state) {
        if (state.specialty == CyborgSpecialty.BUILDER) {
            return TEXTURE_BUILDER;
        }
        if (state.specialty == CyborgSpecialty.HARVESTER) {
            return TEXTURE_HARVESTER;
        }
        return TEXTURE_EXCAVATOR;
    }

    @Override
    public CyborgRenderState createRenderState() {
        return new CyborgRenderState();
    }

    @Override
    public void extractRenderState(CyborgEntity entity, CyborgRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.routine = entity.getRoutine();
        state.visorColor = entity.getVisorColor();
        state.specialty = entity.getSpecialty();
        state.isWorking = entity.isWorking();
        Level lvl = entity.level();
        state.animationTicks = lvl != null ? (lvl.getGameTime() + partialTick) : partialTick;
    }
}
