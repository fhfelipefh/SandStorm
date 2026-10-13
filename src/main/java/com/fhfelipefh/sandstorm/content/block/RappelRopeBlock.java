package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.block.entity.RappelAnchorBlockEntity;
import com.fhfelipefh.sandstorm.content.entity.RappelHarnessEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RappelRopeBlock extends Block {
    private static final VoxelShape SHAPE = Block.box(7.0, 0.0, 7.0, 9.0, 16.0, 9.0);
    private final boolean glowing;

    public RappelRopeBlock(Properties properties) {
        this(properties, false);
    }

    public RappelRopeBlock(Properties properties, boolean glowing) {
        super(properties);
        this.glowing = glowing;
    }

    public boolean isGlowing() {
        return this.glowing;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean inside) {
        if (entity instanceof LivingEntity living) {
            living.resetFallDistance();
        }
        if (!level.isClientSide() && entity instanceof Player player) {
            tryAutoCatch(level, pos, player);
        }
    }

    public static void tryAutoCatch(Level level, BlockPos pos, Player player) {
        if (player.isPassenger() || player.isSpectator()) {
            return;
        }
        if (RappelHarnessEntity.isOnDismountCooldown(player.getUUID(), level.getGameTime())) {
            return;
        }
        if (player.isShiftKeyDown() && player.onGround()) {
            return;
        }
        BlockPos anchorPos = findAnchorPos(level, pos);
        if (anchorPos != null) {
            BlockEntity be = level.getBlockEntity(anchorPos);
            if (be instanceof RappelAnchorBlockEntity anchor) {
                if (anchor.canAutoCatch(player, anchorPos)) {
                    anchor.attachPlayer(player, pos);
                }
            }
        }
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState above = level.getBlockState(pos.above());
        return above.is(SandStormBlocks.RAPPEL_ANCHOR)
                || above.is(SandStormBlocks.GLOWING_RAPPEL_ANCHOR)
                || above.is(SandStormBlocks.RAPPEL_ROPE)
                || above.is(SandStormBlocks.GLOWING_RAPPEL_ROPE);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (!canSurvive(state, level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        BlockPos.MutableBlockPos cursor = pos.below().mutable();
        while (cursor.getY() >= level.getMinY()) {
            BlockState ropeState = level.getBlockState(cursor);
            if (ropeState.is(SandStormBlocks.RAPPEL_ROPE) || ropeState.is(SandStormBlocks.GLOWING_RAPPEL_ROPE)) {
                level.setBlock(cursor, Blocks.AIR.defaultBlockState(), 3);
                cursor.move(Direction.DOWN);
            } else {
                break;
            }
        }
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockPos anchorPos = findAnchorPos(level, pos);
            if (anchorPos != null) {
                BlockEntity be = level.getBlockEntity(anchorPos);
                if (be instanceof RappelAnchorBlockEntity anchor) {
                    anchor.attachPlayer(player, pos);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (this.glowing && random.nextInt(4) == 0) {
            level.addParticle(
                    ParticleTypes.GLOW,
                    pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.2,
                    pos.getY() + random.nextDouble(),
                    pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.2,
                    0.0,
                    0.01,
                    0.0
            );
        }
    }

    public static BlockPos findAnchorPos(Level level, BlockPos startPos) {
        BlockPos current = startPos.above();
        int checks = 0;
        while (checks < 130 && current.getY() < level.getMaxY()) {
            BlockState st = level.getBlockState(current);
            if (st.is(SandStormBlocks.RAPPEL_ANCHOR) || st.is(SandStormBlocks.GLOWING_RAPPEL_ANCHOR)) {
                return current;
            }
            if (!st.is(SandStormBlocks.RAPPEL_ROPE) && !st.is(SandStormBlocks.GLOWING_RAPPEL_ROPE)) {
                return null;
            }
            current = current.above();
            checks++;
        }
        return null;
    }
}
