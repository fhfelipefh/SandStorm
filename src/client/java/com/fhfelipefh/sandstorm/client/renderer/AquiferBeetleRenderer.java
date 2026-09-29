package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.entity.AquiferBeetleEntity;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public class AquiferBeetleRenderer extends MobRenderer<AquiferBeetleEntity, AquiferBeetleRenderState, AquiferBeetleModel> {
    private static final Identifier TEXTURE_ACTIVE = SandStormMod.id("textures/entity/aquifer_beetle/aquifer_beetle.png");
    private static final Identifier TEXTURE_HIBERNATING = SandStormMod.id("textures/entity/aquifer_beetle/aquifer_beetle_hibernating.png");

    public AquiferBeetleRenderer(EntityRendererProvider.Context context) {
        super(context, new AquiferBeetleModel(AquiferBeetleModel.createBodyLayer().bakeRoot()), 0.7f);
    }

    @Override
    public Identifier getTextureLocation(AquiferBeetleRenderState state) {
        return state.isHibernating ? TEXTURE_HIBERNATING : TEXTURE_ACTIVE;
    }

    @Override
    public AquiferBeetleRenderState createRenderState() {
        return new AquiferBeetleRenderState();
    }

    @Override
    public void extractRenderState(AquiferBeetleEntity entity, AquiferBeetleRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.isHibernating = entity.isHibernating();
        state.isSteamAttacking = entity.isSteamAttacking();
        Level lvl = entity.level();
        state.animationTicks = lvl != null ? (lvl.getGameTime() + partialTick) : partialTick;
    }
}
