package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.SolidStateAccumulatorManager;
import com.fhfelipefh.sandstorm.content.block.WirelessSolarReceiverManager;
import com.fhfelipefh.sandstorm.content.world.DimensionPortalRestrictionHandler;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class SubspaceGatewayBlockEntity extends BlockEntity {
    public static final int MAX_ENERGY = 100000;
    public static final int JUMP_COST = 20000;
    public static final int IDLE_CONSUMPTION = 5;

    private int energy = 0;
    private int cooldown = 0;
    private long lastJumpGameTime = 0L;

    public SubspaceGatewayBlockEntity(BlockPos pos, BlockState state) {
        this(SandStormBlocks.SUBSPACE_GATEWAY_BE, pos, state);
    }

    public SubspaceGatewayBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = Math.min(MAX_ENERGY, Math.max(0, energy));
        setChanged();
    }

    public boolean isCharged() {
        return this.energy >= JUMP_COST;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SubspaceGatewayBlockEntity be) {
        if (be.cooldown > 0) {
            be.cooldown--;
        }

        long gameTime = level.getGameTime();
        if ((gameTime + pos.hashCode()) % 20 == 0) {
            float wptSolar = WirelessSolarReceiverManager.getWptChargeAt(level, pos);
            long wptAccumulator = SolidStateAccumulatorManager.getWptChargeAt(level, pos);
            long totalIncome = Math.round(wptSolar * 15.0f) + wptAccumulator;
            if (totalIncome > 0 && be.energy < MAX_ENERGY) {
                be.energy = Math.min(MAX_ENERGY, be.energy + (int) Math.min(MAX_ENERGY - be.energy, totalIncome));
                be.setChanged();
            }
        }

        boolean shouldBeLit = be.energy >= JUMP_COST;
        if (state.getValue(BlockStateProperties.LIT) != shouldBeLit) {
            level.setBlock(pos, state.setValue(BlockStateProperties.LIT, shouldBeLit), 3);
        }

        if (shouldBeLit && (gameTime % 20 == 0) && be.energy > 0) {
            be.energy = Math.max(0, be.energy - IDLE_CONSUMPTION);
            be.setChanged();
        }
    }

    public boolean attemptSubspaceJump(ServerPlayer player) {
        Level currentLevel = player.level();
        if (currentLevel.isClientSide()) {
            return false;
        }

        long currentTime = currentLevel.getGameTime();
        if (currentTime - this.lastJumpGameTime < 60L || this.cooldown > 0) {
            return false;
        }

        if (this.energy < JUMP_COST) {
            player.sendSystemMessage(
                    Component.translatable("telemetry.sandstorm.subspace_gateway_insufficient_energy", this.energy, JUMP_COST)
            );
            this.cooldown = 20;
            return false;
        }

        if (!(currentLevel instanceof ServerLevel currentServerLevel)) {
            return false;
        }

        MinecraftServer server = currentServerLevel.getServer();
        if (server == null) {
            return false;
        }

        ServerLevel destLevel;
        boolean goingToNether;

        if (currentServerLevel.dimension() == Level.OVERWORLD) {
            destLevel = server.getLevel(Level.NETHER);
            goingToNether = true;
        } else if (currentServerLevel.dimension() == Level.NETHER) {
            destLevel = server.getLevel(Level.OVERWORLD);
            goingToNether = false;
        } else {
            return false;
        }

        if (destLevel == null) {
            return false;
        }

        double destX;
        double destZ;
        int destY;

        if (goingToNether) {
            destX = player.getX() / 8.0;
            destZ = player.getZ() / 8.0;
            destY = findSafeNetherAltitude(destLevel, (int) Math.floor(destX), (int) Math.floor(destZ));
        } else {
            destX = player.getX() * 8.0;
            destZ = player.getZ() * 8.0;
            destY = findSafeOverworldAltitude(destLevel, (int) Math.floor(destX), (int) Math.floor(destZ));
        }

        BlockPos landingPos = new BlockPos((int) Math.floor(destX), destY, (int) Math.floor(destZ));
        ensureDestinationLandingGate(destLevel, landingPos);

        this.energy -= JUMP_COST;
        this.cooldown = 80;
        this.lastJumpGameTime = currentTime;
        setChanged();

        DimensionPortalRestrictionHandler.authorizeSubspaceTransit(player.getUUID());

        player.teleportTo(
                destLevel,
                landingPos.getX() + 0.5,
                landingPos.getY() + 1.0,
                landingPos.getZ() + 0.5,
                Set.of(),
                player.getYRot(),
                player.getXRot(),
                false
        );

        destLevel.playSound(null, landingPos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0f, 1.2f);
        currentServerLevel.playSound(null, this.worldPosition, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 1.0f, 0.8f);

        if (goingToNether) {
            player.sendSystemMessage(Component.translatable("telemetry.sandstorm.subspace_transit_nether"));
        } else {
            player.sendSystemMessage(Component.translatable("telemetry.sandstorm.subspace_transit_overworld"));
        }

        return true;
    }

    private static int findSafeNetherAltitude(ServerLevel level, int x, int z) {
        for (int y = 40; y <= 90; y++) {
            BlockPos pos = new BlockPos(x, y, z);
            if (level.getBlockState(pos).isAir() && level.getBlockState(pos.above()).isAir()) {
                return y - 1;
            }
        }
        return 64;
    }

    private static int findSafeOverworldAltitude(ServerLevel level, int x, int z) {
        int height = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
        return Math.max(64, height);
    }

    private static void ensureDestinationLandingGate(ServerLevel destLevel, BlockPos landingPos) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos floorPos = landingPos.offset(dx, 0, dz);
                if (dx == 0 && dz == 0) {
                    BlockState existingState = destLevel.getBlockState(floorPos);
                    if (!existingState.is(SandStormBlocks.SUBSPACE_GATEWAY)) {
                        destLevel.setBlock(floorPos, SandStormBlocks.SUBSPACE_GATEWAY.defaultBlockState().setValue(BlockStateProperties.LIT, true), 3);
                        BlockEntity destBe = destLevel.getBlockEntity(floorPos);
                        if (destBe instanceof SubspaceGatewayBlockEntity gatewayBe) {
                            gatewayBe.setEnergy(JUMP_COST);
                        }
                    }
                } else {
                    BlockState floorState = destLevel.getBlockState(floorPos);
                    if (floorState.isAir() || !floorState.isFaceSturdy(destLevel, floorPos, Direction.UP)) {
                        destLevel.setBlock(floorPos, Blocks.OBSIDIAN.defaultBlockState(), 3);
                    }
                }
                for (int dy = 1; dy <= 3; dy++) {
                    BlockPos airPos = landingPos.offset(dx, dy, dz);
                    destLevel.setBlock(airPos, Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("energy", this.energy);
        output.putInt("cooldown", this.cooldown);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.energy = input.getIntOr("energy", 0);
        this.cooldown = input.getIntOr("cooldown", 0);
    }
}
