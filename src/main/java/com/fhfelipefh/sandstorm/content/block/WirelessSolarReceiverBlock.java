package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.component.WirelessChargerComponent;
import com.fhfelipefh.sandstorm.content.world.SandstormWeatherHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Locale;

public class WirelessSolarReceiverBlock extends Block {
    private final int tier;
    private final WirelessChargerComponent charger;

    public WirelessSolarReceiverBlock(Properties properties, int tier) {
        super(properties);
        this.tier = tier;
        this.charger = new WirelessChargerComponent(tier);
    }

    public int getTier() {
        return tier;
    }

    public WirelessChargerComponent getCharger() {
        return charger;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide()) {
            WirelessSolarReceiverManager.registerReceiver(level.dimension(), pos, tier);
        }
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean isMoving) {
        WirelessSolarReceiverManager.unregisterReceiver(level.dimension(), pos);
        super.affectNeighborsAfterRemoval(state, level, pos, isMoving);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        boolean isDay = level.getSkyDarken() < 4;
        if (level.canSeeSky(pos.above()) && isDay && random.nextFloat() < 0.35f) {
            double x = pos.getX() + 0.2 + random.nextDouble() * 0.6;
            double y = pos.getY() + 0.9 + random.nextDouble() * 0.2;
            double z = pos.getZ() + 0.2 + random.nextDouble() * 0.6;
            level.addParticle(ParticleTypes.ELECTRIC_SPARK, x, y, z, 0.0, 0.02, 0.0);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            boolean canSeeSky = level.canSeeSky(pos.above());
            boolean isDay = level.getSkyDarken() < 4;
            int skyDarken = level.getSkyDarken();
            double weather = SandstormWeatherHandler.getWeather().getSolarEfficiencyMultiplier();
            double radius = charger.calculateEffectiveRadius(canSeeSky, isDay, skyDarken, weather);
            long transferRate = charger.calculateTransferRate(canSeeSky, isDay, skyDarken, weather);

            String status = (canSeeSky && isDay) ? "TRANSMITINDO WPT" : "SEM LUZ SOLAR";
            serverPlayer.sendSystemMessage(Component.literal(
                    "§b[WPT Solar Tier " + tier + "]§r " + status + " | Raio: " + String.format(Locale.ROOT, "%.1f", radius) + "m | Taxa: " + transferRate + " J/tick"
            ), true);
        }
        return InteractionResult.SUCCESS;
    }
}
