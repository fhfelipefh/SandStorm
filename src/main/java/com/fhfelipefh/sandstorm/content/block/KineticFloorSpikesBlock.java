package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.item.SpaceSuitItem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class KineticFloorSpikesBlock extends Block {
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;

    private static final VoxelShape SHAPE_RETRACTED = Block.box(0, 0, 0, 16, 2, 16);
    private static final VoxelShape SHAPE_EXTENDED = Block.box(0, 0, 0, 16, 12, 16);

    public KineticFloorSpikesBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(TRIGGERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TRIGGERED);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(TRIGGERED) ? SHAPE_EXTENDED : SHAPE_RETRACTED;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean inside) {
        triggerSpikes(level, pos, state, entity);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);
        triggerSpikes(level, pos, state, entity);
    }

    private void triggerSpikes(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!state.getValue(TRIGGERED)) {
            level.setBlock(pos, state.setValue(TRIGGERED, true), 3);
            level.scheduleTick(pos, this, 40);
            level.playSound(null, pos, SoundEvents.IRON_TRAPDOOR_OPEN, SoundSource.BLOCKS, 0.7f, 1.4f);

            if (!level.isClientSide() && level instanceof ServerLevel serverLevel && entity instanceof LivingEntity living) {
                ItemStack boots = living.getItemBySlot(EquipmentSlot.FEET);
                if (!(boots.getItem() instanceof SpaceSuitItem)) {
                    living.hurtServer(serverLevel, serverLevel.damageSources().generic(), 8.0f);
                    living.makeStuckInBlock(state, new Vec3(0.4, 0.4, 0.4));
                    living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 2));
                }
            }
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        if (state.getValue(TRIGGERED)) {
            level.setBlock(pos, state.setValue(TRIGGERED, false), 3);
            level.playSound(null, pos, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 0.5f, 1.2f);
        }
    }
}
