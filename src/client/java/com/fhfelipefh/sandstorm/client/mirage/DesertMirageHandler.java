package com.fhfelipefh.sandstorm.client.mirage;

import com.fhfelipefh.sandstorm.client.hud.SurvivalHudOverlay;
import com.fhfelipefh.sandstorm.content.world.SandstormWeatherHandler;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DesertMirageHandler {

    private static final List<BlockPos> PUDDLE_MIRAGES = new ArrayList<>();
    private static BlockPos distantRuinMirage = null;
    private static int ruinCooldownTicks = 0;

    public static void initialize() {
        ClientTickEvents.END_CLIENT_TICK.register(DesertMirageHandler::tick);
    }

    public static boolean isMirageActive(Minecraft client) {
        if (client == null || client.player == null || client.level == null) {
            return false;
        }
        if (client.level.dimension() != Level.OVERWORLD) {
            return false;
        }
        if (!client.level.getBiome(client.player.blockPosition()).is(Biomes.DESERT)) {
            return false;
        }
        if (!client.level.canSeeSky(client.player.blockPosition())) {
            return false;
        }

        if (SandstormWeatherHandler.getWeather().isActive() && SandstormWeatherHandler.getWeather().getIntensity() > 0.20) {
            return false;
        }

        double temp = SurvivalHudOverlay.getClientSuit().getThermal().getCurrentTemperature();
        boolean suitOverheated = temp >= 45.0;
        boolean ambientOverheated = client.level.getSkyDarken() < 4 && client.player.getY() >= 50;

        return suitOverheated || ambientOverheated;
    }

    public static void tick(Minecraft client) {
        if (!isMirageActive(client)) {
            PUDDLE_MIRAGES.clear();
            distantRuinMirage = null;
            return;
        }

        tickPuddleMirages(client);
        tickDistantRuinMirage(client);
    }

    private static void tickPuddleMirages(Minecraft client) {
        Vec3 playerPos = client.player.position();
        RandomSource random = client.level.getRandom();

        Iterator<BlockPos> it = PUDDLE_MIRAGES.iterator();
        while (it.hasNext()) {
            BlockPos pos = it.next();
            double distSq = playerPos.distanceToSqr(Vec3.atCenterOf(pos));

            if (distSq < 121.0) {
                it.remove();
                for (int i = 0; i < 8; i++) {
                    client.level.addParticle(
                            ParticleTypes.POOF,
                            pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 1.5,
                            pos.getY() + 0.1,
                            pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 1.5,
                            0.0, 0.04 + random.nextDouble() * 0.04, 0.0
                    );
                }
            } else if (distSq > 3025.0) {
                it.remove();
            } else {
                int count = 2;
                for (int i = 0; i < count; i++) {
                    double ox = (random.nextDouble() - 0.5) * 2.2;
                    double oz = (random.nextDouble() - 0.5) * 2.2;
                    double px = pos.getX() + 0.5 + ox;
                    double py = pos.getY() + 0.04;
                    double pz = pos.getZ() + 0.5 + oz;

                    DustParticleOptions waterSheen = (random.nextFloat() < 0.65f)
                            ? new DustParticleOptions(0x38B0DE, 0.85f)
                            : new DustParticleOptions(0x8EE5FF, 0.60f);

                    client.level.addParticle(waterSheen, px, py, pz, 0.0, 0.005, 0.0);

                    if (random.nextFloat() < 0.15f) {
                        client.level.addParticle(ParticleTypes.FALLING_WATER, px, py + 0.02, pz, 0.0, 0.0, 0.0);
                    }
                }
            }
        }

        if (PUDDLE_MIRAGES.size() < 2 && random.nextFloat() < 0.12f) {
            Vec3 look = client.player.getViewVector(1.0f);
            double forwardDist = 20.0 + random.nextDouble() * 20.0;
            double sideOffset = (random.nextDouble() - 0.5) * 12.0;

            double targetX = client.player.getX() + look.x * forwardDist - look.z * sideOffset;
            double targetZ = client.player.getZ() + look.z * forwardDist + look.x * sideOffset;

            int targetBlockX = (int) Math.floor(targetX);
            int targetBlockZ = (int) Math.floor(targetZ);

            BlockPos.MutableBlockPos check = new BlockPos.MutableBlockPos(targetBlockX, (int) client.player.getY() + 10, targetBlockZ);
            while (check.getY() > client.level.getMinY()) {
                BlockState state = client.level.getBlockState(check);
                if (state.is(Blocks.SAND) || state.is(Blocks.RED_SAND) || state.is(Blocks.SANDSTONE)) {
                    if (client.level.getBlockState(check.above()).isAir()) {
                        PUDDLE_MIRAGES.add(check.above().immutable());
                        break;
                    }
                }
                check.move(0, -1, 0);
            }
        }
    }

    private static void tickDistantRuinMirage(Minecraft client) {
        Vec3 playerPos = client.player.position();
        RandomSource random = client.level.getRandom();

        if (distantRuinMirage != null) {
            double distSq = playerPos.distanceToSqr(Vec3.atCenterOf(distantRuinMirage));

            if (distSq < 225.0) {
                for (int i = 0; i < 20; i++) {
                    client.level.addParticle(
                            new DustParticleOptions(0xC49B64, 0.65f),
                            distantRuinMirage.getX() + (random.nextDouble() - 0.5) * 3.0,
                            distantRuinMirage.getY() + random.nextDouble() * 3.5,
                            distantRuinMirage.getZ() + (random.nextDouble() - 0.5) * 3.0,
                            (random.nextDouble() - 0.5) * 0.2, 0.05, (random.nextDouble() - 0.5) * 0.2
                    );
                }
                client.level.playLocalSound(
                        distantRuinMirage.getX(), distantRuinMirage.getY(), distantRuinMirage.getZ(),
                        SoundEvents.SAND_STEP, SoundSource.AMBIENT, 0.6f, 0.7f, false
                );
                distantRuinMirage = null;
                ruinCooldownTicks = 400;
            } else if (distSq > 4225.0) {
                distantRuinMirage = null;
            } else {
                double rx = distantRuinMirage.getX() + 0.5;
                double ry = distantRuinMirage.getY();
                double rz = distantRuinMirage.getZ() + 0.5;

                for (int p = 0; p < 4; p++) {
                    double pillarY = ry + p * 0.9;
                    client.level.addParticle(
                            new DustParticleOptions(0xDFB67A, 0.55f),
                            rx - 1.2 + (random.nextDouble() - 0.5) * 0.3,
                            pillarY,
                            rz + (random.nextDouble() - 0.5) * 0.3,
                            0.0, 0.02, 0.0
                    );
                    client.level.addParticle(
                            new DustParticleOptions(0xDFB67A, 0.55f),
                            rx + 1.2 + (random.nextDouble() - 0.5) * 0.3,
                            pillarY,
                            rz + (random.nextDouble() - 0.5) * 0.3,
                            0.0, 0.02, 0.0
                    );
                }
                for (double ox = -1.2; ox <= 1.2; ox += 0.6) {
                    client.level.addParticle(
                            new DustParticleOptions(0xFFD700, 0.45f),
                            rx + ox + (random.nextDouble() - 0.5) * 0.2,
                            ry + 3.6,
                            rz + (random.nextDouble() - 0.5) * 0.2,
                            0.0, 0.015, 0.0
                    );
                }
            }
        } else {
            if (ruinCooldownTicks > 0) {
                ruinCooldownTicks--;
            } else if (random.nextFloat() < 0.02f) {
                Vec3 look = client.player.getViewVector(1.0f);
                double distance = 35.0 + random.nextDouble() * 15.0;
                double targetX = client.player.getX() + look.x * distance;
                double targetZ = client.player.getZ() + look.z * distance;

                int targetBlockX = (int) Math.floor(targetX);
                int targetBlockZ = (int) Math.floor(targetZ);

                BlockPos.MutableBlockPos check = new BlockPos.MutableBlockPos(targetBlockX, (int) client.player.getY() + 15, targetBlockZ);
                while (check.getY() > client.level.getMinY()) {
                    BlockState state = client.level.getBlockState(check);
                    if (state.is(Blocks.SAND) || state.is(Blocks.RED_SAND) || state.is(Blocks.SANDSTONE)) {
                        if (client.level.getBlockState(check.above()).isAir()) {
                            distantRuinMirage = check.above().immutable();
                            break;
                        }
                    }
                    check.move(0, -1, 0);
                }
            }
        }
    }
}
