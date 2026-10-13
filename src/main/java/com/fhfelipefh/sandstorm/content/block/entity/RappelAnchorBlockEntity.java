package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.entity.RappelHarnessEntity;
import com.fhfelipefh.sandstorm.content.entity.SandStormEntities;
import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class RappelAnchorBlockEntity extends BlockEntity {
    public static final int STATE_IDLE = 0;
    public static final int STATE_DEPLOYING = 1;
    public static final int STATE_RETRACTING = 2;
    public static final int MAX_DEPTH = 128;

    private int deployedDepth = 0;
    private int targetDepth = 0;
    private int state = STATE_IDLE;
    private int tickTimer = 0;
    private float spoolAngle = 0.0f;

    public RappelAnchorBlockEntity(BlockPos pos, BlockState state) {
        super(SandStormBlocks.RAPPEL_ANCHOR_BE, pos, state);
    }

    public int getDeployedDepth() {
        return this.deployedDepth;
    }

    public int getState() {
        return this.state;
    }

    public float getSpoolAngle() {
        return this.spoolAngle;
    }

    public void startDeployment() {
        if (this.level == null) {
            return;
        }
        this.targetDepth = calculateMaxTargetDepth();
        this.deployedDepth = 0;
        this.state = STATE_DEPLOYING;
        this.tickTimer = 0;
        setChanged();
        if (!this.level.isClientSide()) {
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void startRetraction(Player player) {
        if (this.level == null) {
            return;
        }
        this.state = STATE_RETRACTING;
        this.tickTimer = 0;
        setChanged();
        if (!this.level.isClientSide()) {
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
            this.level.playSound(null, this.worldPosition, SoundEvents.ARMOR_EQUIP_CHAIN.value(), SoundSource.BLOCKS, 1.0f, 1.2f);
        }
    }

    private int calculateMaxTargetDepth() {
        if (this.level == null) {
            return 0;
        }
        int depth = 0;
        for (int i = 1; i <= MAX_DEPTH; i++) {
            BlockPos checkPos = this.worldPosition.below(i);
            if (checkPos.getY() < this.level.getMinY()) {
                break;
            }
            BlockState st = this.level.getBlockState(checkPos);
            if (st.isAir() || st.is(SandStormBlocks.RAPPEL_ROPE) || st.is(SandStormBlocks.GLOWING_RAPPEL_ROPE)) {
                depth = i;
            } else {
                break;
            }
        }
        return depth;
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) {
            if (this.state == STATE_DEPLOYING) {
                this.spoolAngle += 24.0f;
            } else if (this.state == STATE_RETRACTING) {
                this.spoolAngle -= 36.0f;
            }
            return;
        }

        this.tickTimer++;
        boolean isGlowing = state.is(SandStormBlocks.GLOWING_RAPPEL_ANCHOR);

        if (this.state == STATE_DEPLOYING) {
            if (this.tickTimer % 2 == 0) {
                if (this.deployedDepth < this.targetDepth) {
                    this.deployedDepth++;
                    BlockPos ropePos = pos.below(this.deployedDepth);
                    BlockState currentSt = level.getBlockState(ropePos);
                    if (currentSt.isAir() || currentSt.canBeReplaced()) {
                        BlockState ropeState = isGlowing ? SandStormBlocks.GLOWING_RAPPEL_ROPE.defaultBlockState() : SandStormBlocks.RAPPEL_ROPE.defaultBlockState();
                        level.setBlock(ropePos, ropeState, 3);
                        if (level instanceof ServerLevel serverLevel) {
                            if (isGlowing) {
                                serverLevel.sendParticles(ParticleTypes.GLOW, ropePos.getX() + 0.5, ropePos.getY() + 0.5, ropePos.getZ() + 0.5, 3, 0.1, 0.1, 0.1, 0.02);
                            } else {
                                serverLevel.sendParticles(ParticleTypes.CRIT, ropePos.getX() + 0.5, ropePos.getY() + 0.5, ropePos.getZ() + 0.5, 2, 0.1, 0.1, 0.1, 0.05);
                            }
                        }
                        if (this.deployedDepth % 4 == 0) {
                            level.playSound(null, pos, SoundEvents.CHAIN_STEP, SoundSource.BLOCKS, 0.6f, 1.4f);
                        }
                    }
                    setChanged();
                    level.sendBlockUpdated(pos, state, state, 3);
                } else {
                    this.state = STATE_IDLE;
                    level.playSound(null, pos.below(this.deployedDepth), SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.8f, 1.5f);
                    level.playSound(null, pos, SoundEvents.COPPER_BULB_TURN_ON, SoundSource.BLOCKS, 1.0f, 1.0f);
                    setChanged();
                    level.sendBlockUpdated(pos, state, state, 3);
                }
            }
        } else if (this.state == STATE_RETRACTING) {
            if (this.deployedDepth > 0) {
                BlockPos ropePos = pos.below(this.deployedDepth);
                BlockState currentRope = level.getBlockState(ropePos);
                if (currentRope.is(SandStormBlocks.RAPPEL_ROPE) || currentRope.is(SandStormBlocks.GLOWING_RAPPEL_ROPE)) {
                    level.setBlock(ropePos, Blocks.AIR.defaultBlockState(), 3);
                }
                this.deployedDepth--;
                if (this.deployedDepth % 3 == 0) {
                    level.playSound(null, pos, SoundEvents.CHAIN_HIT, SoundSource.BLOCKS, 0.5f, 1.6f);
                }
                setChanged();
                level.sendBlockUpdated(pos, state, state, 3);
            } else {
                this.state = STATE_IDLE;
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                Item dropItem = isGlowing ? SandStormItems.GLOWING_RAPPEL_WINCH : SandStormItems.RAPPEL_WINCH;
                ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(dropItem));
                level.addFreshEntity(itemEntity);
                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 1.0f, 1.0f);
            }
        }
    }

    public void cleanupRope() {
        if (this.level == null || this.level.isClientSide()) {
            return;
        }
        for (int i = 1; i <= Math.max(this.deployedDepth, MAX_DEPTH); i++) {
            BlockPos ropePos = this.worldPosition.below(i);
            BlockState st = this.level.getBlockState(ropePos);
            if (st.is(SandStormBlocks.RAPPEL_ROPE) || st.is(SandStormBlocks.GLOWING_RAPPEL_ROPE)) {
                this.level.setBlock(ropePos, Blocks.AIR.defaultBlockState(), 3);
            }
        }
    }

    public void attachPlayer(Player player, BlockPos interactionPos) {
        if (this.level == null || this.level.isClientSide()) {
            return;
        }
        if (player.isPassenger()) {
            return;
        }

        double spawnY = Math.min(this.worldPosition.getY() - 0.2, interactionPos.getY() + 0.5);
        double minY = this.worldPosition.getY() - Math.max(1, this.deployedDepth) + 0.1;
        double maxY = this.worldPosition.getY() - 0.2;

        RappelHarnessEntity harness = new RappelHarnessEntity(SandStormEntities.RAPPEL_HARNESS, this.level);
        harness.setPos(this.worldPosition.getX() + 0.5, spawnY, this.worldPosition.getZ() + 0.5);
        harness.setAnchorPos(this.worldPosition);
        harness.setMinY(minY);
        harness.setMaxY(maxY);

        this.level.addFreshEntity(harness);
        player.startRiding(harness);

        this.level.playSound(null, this.worldPosition, SoundEvents.ARMOR_EQUIP_CHAIN.value(), SoundSource.PLAYERS, 1.0f, 1.2f);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("deployedDepth", this.deployedDepth);
        output.putInt("targetDepth", this.targetDepth);
        output.putInt("state", this.state);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.deployedDepth = input.getIntOr("deployedDepth", 0);
        this.targetDepth = input.getIntOr("targetDepth", 0);
        this.state = input.getIntOr("state", 0);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putInt("deployedDepth", this.deployedDepth);
        tag.putInt("state", this.state);
        return tag;
    }
}
