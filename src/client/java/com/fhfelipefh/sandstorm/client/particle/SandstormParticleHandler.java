package com.fhfelipefh.sandstorm.client.particle;

import com.fhfelipefh.sandstorm.content.world.SandstormWeatherHandler;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;

public class SandstormParticleHandler {
    private static final int SAND_GOLD = 0xC49B64;
    private static final int SAND_DARK = 0x8C6538;
    private static final int SAND_PALE = 0xE2C48E;
    private static final int SAND_SILT = 0x5A3E1F;

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
        double windVx = -1.2 - (intensity * 0.6);
        double windVy = -0.02 - (random.nextDouble() * 0.02);
        double windVz = -0.6 - (intensity * 0.3);

        int count = (int) (3 + (intensity * 6));
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2.0;
            double distance = 4.0 + random.nextDouble() * 18.0;
            double spreadY = (random.nextDouble() - 0.25) * 5.0;

            double x = client.player.getX() - (windVx * 2.0) + Math.cos(angle) * distance;
            double y = client.player.getY() + spreadY;
            double z = client.player.getZ() - (windVz * 2.0) + Math.sin(angle) * distance;

            float roll = random.nextFloat();
            DustParticleOptions particle;
            if (roll < 0.40f) {
                particle = new DustParticleOptions(SAND_GOLD, 0.45f);
            } else if (roll < 0.70f) {
                particle = new DustParticleOptions(SAND_DARK, 0.40f);
            } else if (roll < 0.90f) {
                particle = new DustParticleOptions(SAND_PALE, 0.50f);
            } else {
                particle = new DustParticleOptions(SAND_SILT, 0.35f);
            }

            client.level.addParticle(
                    particle,
                    x, y, z, windVx, windVy, windVz
            );
        }

        if (intensity >= 0.75) {
            int ionCount = (int) ((intensity - 0.75) * 20.0) + 2;
            for (int j = 0; j < ionCount; j++) {
                double px = client.player.getX() + (random.nextDouble() - 0.5) * 18.0;
                double py = client.player.getY() + random.nextDouble() * 4.0;
                double pz = client.player.getZ() + (random.nextDouble() - 0.5) * 18.0;
                client.level.addParticle(
                        ParticleTypes.ELECTRIC_SPARK,
                        px, py, pz,
                        (random.nextDouble() - 0.5) * 0.8,
                        (random.nextDouble() - 0.5) * 0.4,
                        (random.nextDouble() - 0.5) * 0.8
                );
                if (random.nextFloat() < 0.35f) {
                    client.level.addParticle(
                            new DustParticleOptions(0x00E5FF, 0.65f),
                            px, py, pz,
                            windVx * 1.4, windVy, windVz * 1.4
                    );
                }
            }
        }
    }
}
