package com.fhfelipefh.sandstorm.content.world;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.GameType;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ShowcaseCinematicManager {
    private static final Map<UUID, ActiveFlight> ACTIVE_FLIGHTS = new HashMap<>();
    private static final Map<UUID, Integer> PENDING_AUTO_TOURS = new HashMap<>();

    public record CinematicWaypoint(double x, double y, double z, float yaw, float pitch, int durationTicks) {}

    public record CinematicRoute(String name, String title, List<CinematicWaypoint> waypoints) {}

    private static class ActiveFlight {
        private final ServerPlayer player;
        private final ServerLevel level;
        private final List<CinematicRoute> routes;
        private int currentRouteIndex;
        private int currentSegmentIndex;
        private int currentSegmentTick;
        private int transitionDelayTicks;
        private final GameType originalGameType;

        public ActiveFlight(ServerPlayer player, ServerLevel level, List<CinematicRoute> routes) {
            this.player = player;
            this.level = level;
            this.routes = routes;
            this.currentRouteIndex = 0;
            this.currentSegmentIndex = 0;
            this.currentSegmentTick = 0;
            this.transitionDelayTicks = 0;
            this.originalGameType = player.gameMode.getGameModeForPlayer();
        }
    }

    public static void initialize() {
        ServerTickEvents.END_SERVER_TICK.register(ShowcaseCinematicManager::tick);
    }

    public static void scheduleAutoTourIfRequested(ServerPlayer player) {
        if (Files.exists(Path.of("auto_record.flag")) || Files.exists(Path.of("run", "auto_record.flag"))) {
            PENDING_AUTO_TOURS.put(player.getUUID(), 60);
            SandStormMod.LOGGER.info("[SandStorm-Cinematic] AUTO_TOUR_SCHEDULED for player {}", player.getName().getString());
        }
    }

    public static int startCinematic(CommandSourceStack source, String routeName) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("§cComando exclusivo para jogadores."));
            return 0;
        }

        String norm = (routeName != null) ? routeName.toLowerCase(Locale.ROOT) : "dungeon";
        List<CinematicRoute> routes = new ArrayList<>();
        if (norm.equals("all") || norm.equals("tour") || norm.equals("showcase")) {
            routes.add(buildDungeonRoute());
            routes.add(buildMilitaryLabRoute());
            routes.add(buildCryoVaultRoute());
            routes.add(buildFarmingRoute());
            routes.add(buildTerraformingRoute());
        } else {
            CinematicRoute r = buildRoute(norm);
            if (r == null || r.waypoints().size() < 2) {
                source.sendFailure(Component.literal("§cRota cinemática desconhecida."));
                return 0;
            }
            routes.add(r);
        }

        startTour(player, routes);
        source.sendSuccess(() -> Component.literal("§6[Cinemática] §aIniciando voo: §f" + routes.getFirst().title()), true);
        source.sendSuccess(() -> Component.literal("§7Shift ou /sandstorm_showcase_cinematic stop para sair."), false);
        return 1;
    }

    private static void startTour(ServerPlayer player, List<CinematicRoute> routes) {
        ServerLevel level = (ServerLevel) player.level();
        ActiveFlight flight = new ActiveFlight(player, level, routes);
        ACTIVE_FLIGHTS.put(player.getUUID(), flight);

        player.setGameMode(GameType.SPECTATOR);
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 6000, 0, false, false, false));

        CinematicWaypoint first = routes.getFirst().waypoints().getFirst();
        player.teleportTo(level, first.x(), first.y(), first.z(), Set.of(), first.yaw(), first.pitch(), true);
        SandStormMod.LOGGER.info("[SandStorm-Cinematic] ROUTE_STARTED: {}", routes.getFirst().name());
    }

    public static int stopCinematic(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("§cComando exclusivo para jogadores."));
            return 0;
        }

        ActiveFlight flight = ACTIVE_FLIGHTS.remove(player.getUUID());
        if (flight != null) {
            player.removeEffect(MobEffects.NIGHT_VISION);
            player.setGameMode(flight.originalGameType);
            source.sendSuccess(() -> Component.literal("§6[Cinemática] §eGravação finalizada."), true);
            return 1;
        }

        source.sendFailure(Component.literal("§cNenhum voo cinemático ativo no momento."));
        return 0;
    }

    private static void tick(MinecraftServer server) {
        if (!PENDING_AUTO_TOURS.isEmpty()) {
            List<UUID> toStart = new ArrayList<>();
            for (Map.Entry<UUID, Integer> entry : PENDING_AUTO_TOURS.entrySet()) {
                int delay = entry.getValue() - 1;
                if (delay <= 0) {
                    toStart.add(entry.getKey());
                } else {
                    entry.setValue(delay);
                }
            }
            for (UUID uuid : toStart) {
                PENDING_AUTO_TOURS.remove(uuid);
                ServerPlayer player = server.getPlayerList().getPlayer(uuid);
                if (player != null && player.isAlive()) {
                    List<CinematicRoute> allRoutes = List.of(buildDungeonRoute(), buildMilitaryLabRoute(), buildCryoVaultRoute(), buildFarmingRoute(), buildTerraformingRoute());
                    startTour(player, allRoutes);
                }
            }
        }

        if (ACTIVE_FLIGHTS.isEmpty()) {
            return;
        }

        List<UUID> toRemove = new ArrayList<>();

        for (Map.Entry<UUID, ActiveFlight> entry : ACTIVE_FLIGHTS.entrySet()) {
            ActiveFlight flight = entry.getValue();
            ServerPlayer player = flight.player;

            if (!player.isAlive() || player.hasDisconnected() || player.isShiftKeyDown()) {
                toRemove.add(entry.getKey());
                continue;
            }

            if (flight.transitionDelayTicks > 0) {
                flight.transitionDelayTicks--;
                if (flight.transitionDelayTicks == 0) {
                    flight.currentRouteIndex++;
                    flight.currentSegmentIndex = 0;
                    flight.currentSegmentTick = 0;
                    CinematicRoute nextRoute = flight.routes.get(flight.currentRouteIndex);
                    CinematicWaypoint wp0 = nextRoute.waypoints().getFirst();
                    player.teleportTo(flight.level, wp0.x(), wp0.y(), wp0.z(), Set.of(), wp0.yaw(), wp0.pitch(), true);
                    SandStormMod.LOGGER.info("[SandStorm-Cinematic] ROUTE_STARTED: {}", nextRoute.name());
                }
                continue;
            }

            CinematicRoute curRoute = flight.routes.get(flight.currentRouteIndex);
            List<CinematicWaypoint> wps = curRoute.waypoints();

            if (flight.currentSegmentIndex >= wps.size() - 1) {
                SandStormMod.LOGGER.info("[SandStorm-Cinematic] ROUTE_COMPLETED: {}", curRoute.name());
                if (flight.currentRouteIndex + 1 < flight.routes.size()) {
                    flight.transitionDelayTicks = 20;
                } else {
                    toRemove.add(entry.getKey());
                }
                continue;
            }

            CinematicWaypoint from = wps.get(flight.currentSegmentIndex);
            CinematicWaypoint to = wps.get(flight.currentSegmentIndex + 1);

            int segDuration = Math.max(1, from.durationTicks());
            flight.currentSegmentTick++;

            double progress = Math.min(1.0, (double) flight.currentSegmentTick / (double) segDuration);
            double smoothed = smoothLerpFactor(progress);

            double curX = from.x() + (to.x() - from.x()) * smoothed;
            double curY = from.y() + (to.y() - from.y()) * smoothed;
            double curZ = from.z() + (to.z() - from.z()) * smoothed;

            float curYaw = lerpAngle(from.yaw(), to.yaw(), smoothed);
            float curPitch = (float) (from.pitch() + (to.pitch() - from.pitch()) * smoothed);

            player.teleportTo(flight.level, curX, curY, curZ, Set.of(), curYaw, curPitch, true);
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 200, 0, false, false, false));

            if (flight.currentSegmentTick >= segDuration) {
                flight.currentSegmentIndex++;
                flight.currentSegmentTick = 0;
            }
        }

        for (UUID uuid : toRemove) {
            ActiveFlight removed = ACTIVE_FLIGHTS.remove(uuid);
            if (removed != null && removed.player.isAlive()) {
                removed.player.removeEffect(MobEffects.NIGHT_VISION);
                removed.player.setGameMode(removed.originalGameType);
                SandStormMod.LOGGER.info("[SandStorm-Cinematic] ALL_TOURS_COMPLETED");
                try {
                    Files.deleteIfExists(Path.of("auto_record.flag"));
                    Files.deleteIfExists(Path.of("run", "auto_record.flag"));
                } catch (Exception ignored) {
                }
            }
        }
    }

    private static double smoothLerpFactor(double t) {
        return (1.0 - Math.cos(t * Math.PI)) * 0.5;
    }

    private static float lerpAngle(float start, float end, double t) {
        float diff = ((end - start) % 360.0f + 540.0f) % 360.0f - 180.0f;
        return (float) (start + diff * t);
    }

    private static CinematicRoute buildRoute(String rawName) {
        String name = (rawName != null) ? rawName.toLowerCase(Locale.ROOT) : "dungeon";
        return switch (name) {
            case "dungeon", "dungeons", "masmorra", "masmorras", "millennial", "west" -> buildDungeonRoute();
            case "military", "lab", "laboratorio", "north" -> buildMilitaryLabRoute();
            case "cryo", "cryo_vault", "gelo", "subglacial" -> buildCryoVaultRoute();
            case "farming", "farm", "cyborgs", "automacao" -> buildFarmingRoute();
            case "terraforming", "terraformer", "oasis", "biosfera" -> buildTerraformingRoute();
            default -> buildDungeonRoute();
        };
    }

    private static CinematicRoute buildDungeonRoute() {
        List<CinematicWaypoint> wps = new ArrayList<>();
        wps.add(new CinematicWaypoint(-36.5, 121.7, 17.0, 0.0f, -2.0f, 40));
        wps.add(new CinematicWaypoint(-36.5, 121.7, 24.0, 0.0f, -1.0f, 40));
        wps.add(new CinematicWaypoint(-36.5, 121.7, 31.0, -15.0f, -2.0f, 40));
        wps.add(new CinematicWaypoint(-35.0, 121.7, 38.0, 15.0f, -1.0f, 40));
        wps.add(new CinematicWaypoint(-36.5, 121.7, 45.0, 0.0f, -2.0f, 40));
        return new CinematicRoute("dungeon", "Corredores da Megadungeon Oeste", wps);
    }

    private static CinematicRoute buildMilitaryLabRoute() {
        List<CinematicWaypoint> wps = new ArrayList<>();
        wps.add(new CinematicWaypoint(0.5, 116.7, 89.0, 0.0f, -2.0f, 40));
        wps.add(new CinematicWaypoint(0.5, 116.7, 96.0, 0.0f, -1.0f, 40));
        wps.add(new CinematicWaypoint(0.5, 116.7, 103.0, 15.0f, -2.0f, 40));
        wps.add(new CinematicWaypoint(1.5, 116.7, 110.0, -15.0f, -1.0f, 40));
        wps.add(new CinematicWaypoint(0.5, 116.7, 117.0, 0.0f, -2.0f, 40));
        return new CinematicRoute("military_lab", "Instalações do Laboratório Militar", wps);
    }

    private static CinematicRoute buildCryoVaultRoute() {
        List<CinematicWaypoint> wps = new ArrayList<>();
        wps.add(new CinematicWaypoint(65.5, 116.7, -65.0, 0.0f, -2.0f, 40));
        wps.add(new CinematicWaypoint(65.5, 116.7, -58.0, 0.0f, -1.0f, 40));
        wps.add(new CinematicWaypoint(65.5, 116.7, -51.0, -15.0f, -2.0f, 40));
        wps.add(new CinematicWaypoint(64.0, 116.7, -44.0, 15.0f, -1.0f, 40));
        return new CinematicRoute("cryo_vault", "Câmaras do Cofre Criogênico Subglacial", wps);
    }

    private static CinematicRoute buildFarmingRoute() {
        List<CinematicWaypoint> wps = new ArrayList<>();
        wps.add(new CinematicWaypoint(0.5, 162.2, -38.5, 180.0f, 0.0f, 45));
        wps.add(new CinematicWaypoint(-6.0, 162.5, -48.0, 140.0f, -8.0f, 50));
        wps.add(new CinematicWaypoint(-10.5, 162.5, -53.5, 120.0f, -12.0f, 55));
        wps.add(new CinematicWaypoint(0.0, 163.0, -56.0, -90.0f, -6.0f, 50));
        wps.add(new CinematicWaypoint(7.0, 162.5, -52.0, -90.0f, -8.0f, 65));
        wps.add(new CinematicWaypoint(12.0, 162.5, -52.0, -45.0f, -10.0f, 65));
        wps.add(new CinematicWaypoint(4.0, 162.5, -62.0, 0.0f, -4.0f, 45));
        return new CinematicRoute("farming", "Indústria, Construção & Agricultura Autônoma", wps);
    }

    private static CinematicRoute buildTerraformingRoute() {
        List<CinematicWaypoint> wps = new ArrayList<>();
        wps.add(new CinematicWaypoint(0.5, 161.5, 80.0, 0.0f, -2.0f, 60));
        wps.add(new CinematicWaypoint(0.5, 165.5, 96.0, 0.0f, -10.0f, 60));
        wps.add(new CinematicWaypoint(16.0, 164.5, 110.0, -45.0f, -8.0f, 60));
        wps.add(new CinematicWaypoint(26.0, 163.0, 118.0, -15.0f, -4.0f, 60));
        wps.add(new CinematicWaypoint(32.5, 162.5, 130.0, 0.0f, -6.0f, 60));
        wps.add(new CinematicWaypoint(32.5, 163.0, 142.0, 20.0f, 2.0f, 60));
        return new CinematicRoute("terraforming", "Terraformador Atmosférico & Oásis", wps);
    }
}
