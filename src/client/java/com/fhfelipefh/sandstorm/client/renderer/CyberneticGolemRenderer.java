package com.fhfelipefh.sandstorm.client.renderer;

import com.fhfelipefh.sandstorm.content.entity.CyberneticGolemEntity;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.minecraft.core.BlockPos;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public class CyberneticGolemRenderer extends MobRenderer<CyberneticGolemEntity, CyberneticGolemRenderState, CyberneticGolemModel> {
    private static final Identifier TEXTURE_IRON = SandStormMod.id("textures/entity/cybernetic_golem/cybernetic_golem_iron.png");
    private static final Identifier TEXTURE_COPPER = SandStormMod.id("textures/entity/cybernetic_golem/cybernetic_golem_copper.png");
    private static final Identifier TEXTURE_GOLD = SandStormMod.id("textures/entity/cybernetic_golem/cybernetic_golem_gold.png");
    private static final Identifier TEXTURE_NETHERITE = SandStormMod.id("textures/entity/cybernetic_golem/cybernetic_golem_netherite.png");
    private static final Identifier TEXTURE_COMPOSITE = SandStormMod.id("textures/entity/cybernetic_golem/cybernetic_golem_composite.png");

    public CyberneticGolemRenderer(EntityRendererProvider.Context context) {
        super(context, new CyberneticGolemModel(CyberneticGolemModel.createBodyLayer().bakeRoot()), 0.7f);
        this.addLayer(new CyberneticGolemHeatLayer(this));
    }

    @Override
    public Identifier getTextureLocation(CyberneticGolemRenderState state) {
        if ("copper".equalsIgnoreCase(state.metalTier)) {
            return TEXTURE_COPPER;
        }
        if ("gold".equalsIgnoreCase(state.metalTier)) {
            return TEXTURE_GOLD;
        }
        if ("netherite".equalsIgnoreCase(state.metalTier)) {
            return TEXTURE_NETHERITE;
        }
        if ("composite".equalsIgnoreCase(state.metalTier)) {
            return TEXTURE_COMPOSITE;
        }
        return TEXTURE_IRON;
    }

    @Override
    public CyberneticGolemRenderState createRenderState() {
        return new CyberneticGolemRenderState();
    }

    @Override
    public void extractRenderState(CyberneticGolemEntity entity, CyberneticGolemRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.isOverdrive = entity.isOverdrive();
        state.heat = entity.getHeat();
        state.metalTier = entity.getMetalTier().getId();
        state.attackAnimTicks = entity.getAttackAnimTicks();
        Level lvl = entity.level();
        state.animationTicks = lvl != null ? (lvl.getGameTime() + partialTick) : partialTick;
    }

    @Override
    protected int getBlockLightLevel(CyberneticGolemEntity entity, BlockPos pos) {
        if (entity.isOverdrive() || entity.getHeat() > 0.05f) {
            return Math.max(super.getBlockLightLevel(entity, pos), 9);
        }
        return super.getBlockLightLevel(entity, pos);
    }
}
