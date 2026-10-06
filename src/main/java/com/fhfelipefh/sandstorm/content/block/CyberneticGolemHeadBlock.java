package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.entity.CyberneticGolemEntity;
import com.fhfelipefh.sandstorm.content.entity.GolemMetalTier;
import com.fhfelipefh.sandstorm.content.entity.SandStormEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import java.util.List;

public class CyberneticGolemHeadBlock extends Block {
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

    public CyberneticGolemHeadBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (oldState.is(state.getBlock())) {
            return;
        }
        if (!level.isClientSide()) {
            trySpawnGolem(level, pos);
        }
    }

    private void trySpawnGolem(Level level, BlockPos headPos) {
        BlockPos torsoPos = headPos.below(1);
        BlockPos basePos = headPos.below(2);

        BlockState torsoState = level.getBlockState(torsoPos);
        BlockState baseState = level.getBlockState(basePos);

        if (!GolemMetalTier.isMetallicBlock(torsoState) || !GolemMetalTier.isMetallicBlock(baseState)) {
            return;
        }

        BlockPos eastArm = torsoPos.east();
        BlockPos westArm = torsoPos.west();
        BlockState eastState = level.getBlockState(eastArm);
        BlockState westState = level.getBlockState(westArm);

        BlockPos northArm = torsoPos.north();
        BlockPos southArm = torsoPos.south();
        BlockState northState = level.getBlockState(northArm);
        BlockState southState = level.getBlockState(southArm);

        BlockPos arm1Pos = null;
        BlockPos arm2Pos = null;
        BlockState arm1State = null;
        BlockState arm2State = null;

        if (GolemMetalTier.isMetallicBlock(eastState) && GolemMetalTier.isMetallicBlock(westState)) {
            arm1Pos = eastArm;
            arm2Pos = westArm;
            arm1State = eastState;
            arm2State = westState;
        } else if (GolemMetalTier.isMetallicBlock(northState) && GolemMetalTier.isMetallicBlock(southState)) {
            arm1Pos = northArm;
            arm2Pos = southArm;
            arm1State = northState;
            arm2State = southState;
        }

        if (arm1Pos == null || arm2Pos == null) {
            return;
        }

        List<BlockState> bodyBlocks = List.of(torsoState, baseState, arm1State, arm2State);
        GolemMetalTier tier = GolemMetalTier.determineTier(bodyBlocks);

        level.setBlock(headPos, Blocks.AIR.defaultBlockState(), 2);
        level.setBlock(torsoPos, Blocks.AIR.defaultBlockState(), 2);
        level.setBlock(basePos, Blocks.AIR.defaultBlockState(), 2);
        level.setBlock(arm1Pos, Blocks.AIR.defaultBlockState(), 2);
        level.setBlock(arm2Pos, Blocks.AIR.defaultBlockState(), 2);

        ServerLevel serverLevel = (ServerLevel) level;
        serverLevel.sendParticles(ParticleTypes.EXPLOSION, headPos.getX() + 0.5, headPos.getY() - 1.0, headPos.getZ() + 0.5, 3, 0.4, 0.6, 0.4, 0.05);
        serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, headPos.getX() + 0.5, headPos.getY() - 0.5, headPos.getZ() + 0.5, 15, 0.3, 0.3, 0.3, 0.1);
        serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, headPos.getX() + 0.5, headPos.getY() - 1.0, headPos.getZ() + 0.5, 30, 0.6, 0.8, 0.6, 0.15);

        serverLevel.playSound(null, headPos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.0f, 1.0f);
        serverLevel.playSound(null, headPos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.2f, 1.4f);

        CyberneticGolemEntity golem = SandStormEntities.CYBERNETIC_GOLEM.create(serverLevel, EntitySpawnReason.TRIGGERED);
        if (golem != null) {
            golem.setPos(headPos.getX() + 0.5, headPos.getY() - 1.95, headPos.getZ() + 0.5);
            golem.setMetalTier(tier);
            serverLevel.addFreshEntity(golem);
        }
    }
}
