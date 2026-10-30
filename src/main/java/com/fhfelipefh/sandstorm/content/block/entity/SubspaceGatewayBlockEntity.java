package com.fhfelipefh.sandstorm.content.block.entity;

import com.fhfelipefh.sandstorm.content.block.SandStormBlocks;
import com.fhfelipefh.sandstorm.content.block.SolidStateAccumulatorManager;
import com.fhfelipefh.sandstorm.content.block.WirelessSolarReceiverManager;
import com.fhfelipefh.sandstorm.content.world.DimensionPortalRestrictionHandler;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
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
    public static final long TRANSIT_COOLDOWN_TICKS = 100L;

    private static final Map<UUID, Long> PLAYER_COOLDOWNS = new ConcurrentHashMap<>();

    private int energy = 0;
    private int cooldown = 0;
    private long lastJumpGameTime = 0L;
    private int cachedThermalRate = -1;
    private BlockPos linkedGatewayPos = null;

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

    public int getCachedThermalRate() {
        return this.cachedThermalRate;
    }

    public BlockPos getLinkedGatewayPos() {
        return this.linkedGatewayPos;
    }

    public void setLinkedGatewayPos(BlockPos pos) {
        this.linkedGatewayPos = pos;
        setChanged();
    }

    public static int getThermalRechargeRate(Level level, BlockPos pos) {
        int rate = 0;
        if (level.dimension() == Level.NETHER) {
            rate += 20;
        }
        for (Direction dir : Direction.values()) {
            BlockState neighbor = level.getBlockState(pos.relative(dir));
            if (neighbor.is(Blocks.LAVA)) {
                rate += 5;
            } else if (neighbor.is(Blocks.MAGMA_BLOCK) || neighbor.is(Blocks.FIRE) || neighbor.is(Blocks.SOUL_FIRE)) {
                rate += 2;
            }
        }
        BlockState below2 = level.getBlockState(pos.below(2));
        if (below2.is(Blocks.LAVA)) {
            rate += 5;
        }
        return rate;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SubspaceGatewayBlockEntity be) {
        if (be.cooldown > 0) {
            be.cooldown--;
        }

        long gameTime = level.getGameTime();
        if ((gameTime + pos.hashCode()) % 20 == 0 || be.cachedThermalRate < 0) {
            be.cachedThermalRate = getThermalRechargeRate(level, pos);
            float wptSolar = WirelessSolarReceiverManager.getWptChargeAt(level, pos);
            long wptAccumulator = SolidStateAccumulatorManager.getWptChargeAt(level, pos);
            long totalIncome = Math.round(wptSolar * 15.0f) + wptAccumulator;
            if (totalIncome > 0 && be.energy < MAX_ENERGY) {
                be.energy = Math.min(MAX_ENERGY, be.energy + (int) Math.min(MAX_ENERGY - be.energy, totalIncome));
                be.setChanged();
            }
        }

        if (be.cachedThermalRate > 0 && be.energy < MAX_ENERGY) {
            be.energy = Math.min(MAX_ENERGY, be.energy + be.cachedThermalRate);
            be.setChanged();
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
        Long playerLastJump = PLAYER_COOLDOWNS.get(player.getUUID());
        if (playerLastJump != null && (currentTime - playerLastJump < TRANSIT_COOLDOWN_TICKS)) {
            return false;
        }

        if (currentTime - this.lastJumpGameTime < TRANSIT_COOLDOWN_TICKS || this.cooldown > 0) {
            return false;
        }

        if (this.energy < JUMP_COST) {
            player.sendSystemMessage(
                    Component.translatable(this.cachedThermalRate > 0
                            ? "telemetry.sandstorm.subspace_gateway_geothermal_charging"
                            : "telemetry.sandstorm.subspace_gateway_insufficient_energy", this.energy, JUMP_COST)
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
            destX = this.worldPosition.getX() / 8.0;
            destZ = this.worldPosition.getZ() / 8.0;
            destY = findSafeNetherAltitude(destLevel, (int) Math.floor(destX), (int) Math.floor(destZ));
        } else {
            destX = this.worldPosition.getX() * 8.0;
            destZ = this.worldPosition.getZ() * 8.0;
            destY = findSafeOverworldAltitude(destLevel, (int) Math.floor(destX), (int) Math.floor(destZ));
        }

        BlockPos landingPos = null;
        if (this.linkedGatewayPos != null && destLevel.getBlockState(this.linkedGatewayPos).is(SandStormBlocks.SUBSPACE_GATEWAY)) {
            landingPos = this.linkedGatewayPos;
        } else {
            landingPos = findSubspaceGatewayNear(destLevel, (int) Math.floor(destX), (int) Math.floor(destZ), destY, 12);
        }

        if (landingPos == null) {
            landingPos = new BlockPos((int) Math.floor(destX), destY, (int) Math.floor(destZ));
            ensureDestinationLandingGate(destLevel, landingPos);
        }

        this.linkedGatewayPos = landingPos;
        this.energy -= JUMP_COST;
        this.cooldown = (int) TRANSIT_COOLDOWN_TICKS;
        this.lastJumpGameTime = currentTime;
        setChanged();

        BlockEntity destBe = destLevel.getBlockEntity(landingPos);
        if (destBe instanceof SubspaceGatewayBlockEntity gatewayBe) {
            gatewayBe.linkedGatewayPos = this.worldPosition;
            if (gatewayBe.getEnergy() < JUMP_COST) {
                gatewayBe.setEnergy(JUMP_COST);
            }
            gatewayBe.cooldown = (int) TRANSIT_COOLDOWN_TICKS;
            gatewayBe.lastJumpGameTime = destLevel.getGameTime();
            gatewayBe.setChanged();
        }

        PLAYER_COOLDOWNS.put(player.getUUID(), currentTime);
        DimensionPortalRestrictionHandler.authorizeSubspaceTransit(player.getUUID());

        player.teleportTo(
                destLevel,
                landingPos.getX() + 0.5,
                landingPos.getY() + 1.0,
                landingPos.getZ() + 1.5,
                Set.of(),
                180.0f,
                0.0f,
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

    public static BlockPos findSubspaceGatewayNear(ServerLevel level, int centerX, int centerZ, int centerY, int radius) {
        int minChunkX = (centerX - radius) >> 4;
        int maxChunkX = (centerX + radius) >> 4;
        int minChunkZ = (centerZ - radius) >> 4;
        int maxChunkZ = (centerZ + radius) >> 4;
        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                level.getChunk(cx, cz);
            }
        }
        BlockPos bestPos = null;
        double bestDistSq = Double.MAX_VALUE;
        int minY = Math.max(level.getMinY(), centerY - 24);
        int maxY = Math.min(level.getMaxY() - 1, centerY + 24);
        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                for (int y = minY; y <= maxY; y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (level.getBlockState(pos).is(SandStormBlocks.SUBSPACE_GATEWAY)) {
                        double dsq = pos.distToCenterSqr(centerX, centerY, centerZ);
                        if (dsq < bestDistSq) {
                            bestDistSq = dsq;
                            bestPos = pos;
                        }
                    }
                }
            }
        }
        return bestPos;
    }

    private static int findSafeNetherAltitude(ServerLevel level, int x, int z) {
        level.getChunk(x >> 4, z >> 4);
        for (int y = 40; y <= 90; y++) {
            BlockPos pos = new BlockPos(x, y, z);
            if (level.getBlockState(pos).isAir() && level.getBlockState(pos.above()).isAir()) {
                return y - 1;
            }
        }
        return 64;
    }

    private static int findSafeOverworldAltitude(ServerLevel level, int x, int z) {
        level.getChunk(x >> 4, z >> 4);
        int height = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
        return Math.max(64, height);
    }

    private static void ensureDestinationLandingGate(ServerLevel destLevel, BlockPos landingPos) {
        destLevel.getChunk(landingPos.getX() >> 4, landingPos.getZ() >> 4);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos floorPos = landingPos.offset(dx, 0, dz);
                if (dx == 0 && dz == 0) {
                    BlockState existingState = destLevel.getBlockState(floorPos);
                    if (!existingState.is(SandStormBlocks.SUBSPACE_GATEWAY)) {
                        destLevel.setBlock(floorPos, SandStormBlocks.SUBSPACE_GATEWAY.defaultBlockState().setValue(BlockStateProperties.LIT, true), 3);
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
        if (this.linkedGatewayPos != null) {
            output.putLong("linkedPos", this.linkedGatewayPos.asLong());
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.energy = input.getIntOr("energy", 0);
        this.cooldown = input.getIntOr("cooldown", 0);
        long linkedLong = input.getLongOr("linkedPos", 0L);
        if (linkedLong != 0L) {
            this.linkedGatewayPos = BlockPos.of(linkedLong);
        } else {
            this.linkedGatewayPos = null;
        }
    }
}
