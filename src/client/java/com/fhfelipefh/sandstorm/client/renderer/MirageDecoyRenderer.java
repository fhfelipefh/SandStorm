package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.entity.MirageDecoyEntity;
import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgSpecialty;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public class MirageDecoyRenderer extends MobRenderer<MirageDecoyEntity, CyborgRenderState, CyborgModel> {
    private static final Identifier TEXTURE = SandStormMod.id("textures/entity/mirage_decoy/mirage_decoy.png");

    public MirageDecoyRenderer(EntityRendererProvider.Context context) {
        super(context, new CyborgModel(CyborgModel.createBodyLayer().bakeRoot()), 0.5f);
    }

    @Override
    public Identifier getTextureLocation(CyborgRenderState state) {
        return TEXTURE;
    }

    @Override
    public CyborgRenderState createRenderState() {
        return new CyborgRenderState();
    }

    @Override
    public void extractRenderState(MirageDecoyEntity entity, CyborgRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.specialty = CyborgSpecialty.BUILDER;
        state.visorColor = 0x00FFFF;
        state.isWorking = false;
        Level lvl = entity.level();
        state.animationTicks = lvl != null ? (lvl.getGameTime() + partialTick) : partialTick;
    }
}
