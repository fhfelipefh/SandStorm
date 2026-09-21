package com.fhfelipefh.sandstorm.client.particle;

import com.fhfelipefh.sandstorm.content.world.SandstormWeatherHandler;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class SandstormParticleHandler {
    private static final int SAND_COLOR = 0xD8B880;

    public static void initialize() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> tick(client));
    }

    public static void tick(Minecraft client) {
        if (client.player == null || client.level == null) {
            return;
        }

        if (!SandstormWeatherHandler.getWeather().isActive()) {
            return;
        }

        double intensity = SandstormWeatherHandler.getWeather().getIntensity();
        if (intensity <= 0.05) {
            return;
        }

        BlockPos playerPos = client.player.blockPosition();
        boolean outdoors = client.level.canSeeSky(playerPos) || client.level.canSeeSky(playerPos.above(2));
        if (!outdoors) {
            return;
        }

        RandomSource random = client.level.getRandom();
        double windVx = -1.6 - (intensity * 0.8);
        double windVy = -0.04;
        double windVz = -0.8 - (intensity * 0.4);

        int count = (int) (12 + (intensity * 26));
        for (int i = 0; i < count; i++) {
            double spreadX = (random.nextDouble() - 0.5) * 22.0;
            double spreadY = random.nextDouble() * 7.0 - 0.5;
            double spreadZ = (random.nextDouble() - 0.5) * 22.0;

            double x = client.player.getX() - (windVx * 3.5) + spreadX;
            double y = client.player.getY() + spreadY;
            double z = client.player.getZ() - (windVz * 3.5) + spreadZ;

            float roll = random.nextFloat();
            if (roll < 0.40f) {
                client.level.addParticle(
                        new BlockParticleOption(ParticleTypes.FALLING_DUST, Blocks.SAND.defaultBlockState()),
                        x, y, z, windVx, windVy, windVz
                );
            } else if (roll < 0.75f) {
                client.level.addParticle(
                        new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SAND.defaultBlockState()),
                        x, y, z, windVx, windVy, windVz
                );
            } else if (roll < 0.95f) {
                client.level.addParticle(
                        new DustParticleOptions(SAND_COLOR, 1.2f),
                        x, y, z, windVx * 0.7, windVy, windVz * 0.7
                );
            } else {
                client.level.addParticle(
                        ParticleTypes.POOF,
                        x, y, z, windVx * 0.5, windVy, windVz * 0.5
                );
            }
        }

        Vec3 look = client.player.getLookAngle();
        for (int i = 0; i < 4; i++) {
            double eyeX = client.player.getX() + look.x * 1.2 + (random.nextDouble() - 0.5) * 1.6;
            double eyeY = client.player.getEyeY() + look.y * 1.2 + (random.nextDouble() - 0.5) * 0.8;
            double eyeZ = client.player.getZ() + look.z * 1.2 + (random.nextDouble() - 0.5) * 1.6;

            client.level.addParticle(
                    new BlockParticleOption(ParticleTypes.FALLING_DUST, Blocks.SAND.defaultBlockState()),
                    eyeX, eyeY, eyeZ, windVx * 1.2, windVy, windVz * 1.2
            );
        }
    }
}
