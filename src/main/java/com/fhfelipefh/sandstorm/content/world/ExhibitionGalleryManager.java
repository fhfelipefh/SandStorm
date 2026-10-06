package com.fhfelipefh.sandstorm.content.world;

import com.fhfelipefh.sandstorm.content.command.SandstormBuildCommand;
import com.fhfelipefh.sandstorm.content.entity.AquiferBeetleEntity;
import com.fhfelipefh.sandstorm.content.entity.BuilderDroneEntity;
import com.fhfelipefh.sandstorm.content.entity.CargoDroneEntity;
import com.fhfelipefh.sandstorm.content.entity.CrawlerDroneEntity;
import com.fhfelipefh.sandstorm.content.entity.CyberHoundEntity;
import com.fhfelipefh.sandstorm.content.entity.DerelictAutomatonEntity;
import com.fhfelipefh.sandstorm.content.entity.ExcavatorVehicleEntity;
import com.fhfelipefh.sandstorm.content.entity.LaborerUnitEntity;
import com.fhfelipefh.sandstorm.content.entity.MegazordEntity;
import com.fhfelipefh.sandstorm.content.entity.NomadScavengerEntity;
import com.fhfelipefh.sandstorm.content.entity.SandStormEntities;
import com.fhfelipefh.sandstorm.content.entity.SandboardEntity;
import com.fhfelipefh.sandstorm.content.entity.SandwormEntity;
import com.fhfelipefh.sandstorm.content.entity.ScoutDroneEntity;
import com.fhfelipefh.sandstorm.content.entity.ScrapSentinelEntity;
import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgBuilderEntity;
import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgExcavatorEntity;
import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgHarvesterEntity;
import com.fhfelipefh.sandstorm.content.sound.SandStormSoundEvents;
import com.fhfelipefh.sandstorm.content.world.structure.ColossalCastleGenerator;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.entity.SignTextSlot;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ExhibitionGalleryManager {

    @FunctionalInterface
    public interface GalleryAction {
        void execute(ServerLevel level, int x, int groundY, int z);
    }

    public record GalleryTask(String name, int x, int groundY, int z, int radius, GalleryAction action) {}

    private static final Queue<GalleryTask> TASK_QUEUE = new ConcurrentLinkedQueue<>();
    private static ServerPlayer targetPlayer = null;
    private static int totalTasks = 0;
    private static int currentTaskIndex = 0;

    public static void initialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> registerCommands(dispatcher));

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (TASK_QUEUE.isEmpty()) {
                return;
            }

            GalleryTask task = TASK_QUEUE.poll();
            if (task == null) {
                return;
            }

            ServerLevel level = server.overworld();
            if (targetPlayer != null && targetPlayer.isAlive()) {
                level = (ServerLevel) targetPlayer.level();
            }
            if (level == null) {
                return;
            }

            int minChunkX = (task.x() - task.radius() - 2) >> 4;
            int maxChunkX = (task.x() + task.radius() + 2) >> 4;
            int minChunkZ = (task.z() - task.radius() - 2) >> 4;
            int maxChunkZ = (task.z() + task.radius() + 2) >> 4;

            for (int cx = minChunkX; cx <= maxChunkX; cx++) {
                for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                    level.getChunk(cx, cz);
                }
            }

            task.action().execute(level, task.x(), task.groundY(), task.z());
            currentTaskIndex++;

            if (targetPlayer != null && targetPlayer.isAlive()) {
                final int cur = currentTaskIndex;
                final int tot = totalTasks;
                final String itemName = task.name();
                targetPlayer.sendSystemMessage(Component.literal(String.format(Locale.ROOT, "§6[Galeria] §e[%d/%d] §a%s", cur, tot, itemName)), true);
                if (cur % 5 == 0) {
                    level.playSound(null, targetPlayer.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.6f, 1.2f);
                }
            }

            if (TASK_QUEUE.isEmpty() && targetPlayer != null && targetPlayer.isAlive()) {
                targetPlayer.sendSystemMessage(Component.literal(String.format(Locale.ROOT, "§a[SandStorm] Galeria Monumental gerada com sucesso! (%d elementos alinhados lado a lado na mesma altura)", totalTasks)));
                level.playSound(null, targetPlayer.blockPosition(), SandStormSoundEvents.MEGASTRUCTURE_COMPLETE, SoundSource.PLAYERS, 1.5f, 1.0f);
            }
        });
    }

    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sandstorm")
                .then(Commands.literal("gallery")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ExhibitionGalleryManager::executeCommand)
                )
                .then(Commands.literal("showcase_all")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ExhibitionGalleryManager::executeCommand)
                )
        );

        dispatcher.register(Commands.literal("sandstorm_gallery")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ExhibitionGalleryManager::executeCommand)
        );

        dispatcher.register(Commands.literal("gallery_all")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ExhibitionGalleryManager::executeCommand)
        );

        dispatcher.register(Commands.literal("showcase_all")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ExhibitionGalleryManager::executeCommand)
        );

        dispatcher.register(Commands.literal("build_gallery")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ExhibitionGalleryManager::executeCommand)
        );
    }

    public static int executeCommand(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("§c[SandStorm] Este comando deve ser executado por um jogador."));
            return 0;
        }

        if (!TASK_QUEUE.isEmpty()) {
            source.sendFailure(Component.literal("§c[SandStorm] Já existe uma galeria sendo gerada! Aguarde a conclusão da fila atual."));
            return 0;
        }

        startGalleryGeneration(player);
        source.sendSuccess(() -> Component.literal("§a[SandStorm] Fila de geração da Galeria Monumental iniciada! Gerando um por vez..."), true);
        return 1;
    }

    public static void startGalleryGeneration(ServerPlayer player) {
        TASK_QUEUE.clear();
        targetPlayer = player;
        currentTaskIndex = 0;

        int groundY = player.getBlockY();
        int startX = player.getBlockX() + 8;
        int centerZ = player.getBlockZ();

        List<RawDisplayEntry> entries = gatherAllEntries();
        totalTasks = entries.size();

        int currentX = startX;
        int lastRadius = 0;

        for (RawDisplayEntry entry : entries) {
            if (lastRadius > 0) {
                currentX += lastRadius + 6 + entry.radius();
            }
            lastRadius = entry.radius();

            final int posX = currentX;
            final int posZ = centerZ;
            final RawDisplayEntry currentEntry = entry;

            TASK_QUEUE.add(new GalleryTask(entry.name(), posX, groundY, posZ, entry.radius(), (level, x, y, z) -> {
                paveMuseumPlaza(level, x, y, z, currentEntry.radius());
                currentEntry.action().execute(level, x, y, z);
            }));
        }
    }

    record RawDisplayEntry(String name, int radius, GalleryAction action) {}

    static List<RawDisplayEntry> gatherAllEntries() {
        List<RawDisplayEntry> list = new ArrayList<>();

        gatherItems(list);
        gatherBlocks(list);
        gatherEntities(list);
        gatherStructures(list);

        return list;
    }

    private static void gatherItems(List<RawDisplayEntry> list) {
        List<Item> items = BuiltInRegistries.ITEM.stream()
                .filter(item -> BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(SandStormMod.MOD_ID))
                .filter(item -> !(item instanceof BlockItem))
                .sorted(Comparator.comparing(item -> BuiltInRegistries.ITEM.getKey(item).toString()))
                .toList();

        for (Item item : items) {
            String key = item.getDescriptionId();
            String translated = Component.translatable(key).getString();
            String name = translated.equals(key) ? formatPath(BuiltInRegistries.ITEM.getKey(item).getPath()) : translated;
            list.add(new RawDisplayEntry("Item: " + name, 2, (level, x, groundY, z) -> {
                BlockPos base = new BlockPos(x, groundY, z);
                placePedestal(level, base);
                spawnItemStand(level, base.above(), item, name);
                placeSign(level, base.offset(0, 0, -2), name, "Item");
            }));
        }
    }

    private static void gatherBlocks(List<RawDisplayEntry> list) {
        List<Block> blocks = BuiltInRegistries.BLOCK.stream()
                .filter(b -> BuiltInRegistries.BLOCK.getKey(b).getNamespace().equals(SandStormMod.MOD_ID))
                .sorted(Comparator.comparing(b -> BuiltInRegistries.BLOCK.getKey(b).toString()))
                .toList();

        for (Block block : blocks) {
            String key = block.getDescriptionId();
            String translated = Component.translatable(key).getString();
            String name = translated.equals(key) ? formatPath(BuiltInRegistries.BLOCK.getKey(block).getPath()) : translated;
            list.add(new RawDisplayEntry("Bloco: " + name, 2, (level, x, groundY, z) -> {
                BlockPos base = new BlockPos(x, groundY, z);
                placePedestal(level, base);
                level.setBlock(base.above(), block.defaultBlockState(), 3);
                placeSign(level, base.offset(0, 0, -2), name, "Bloco");
            }));
        }
    }

    private static String formatPath(String path) {
        String[] parts = path.split("_");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (p.isEmpty()) {
                continue;
            }
            if (!sb.isEmpty()) {
                sb.append(" ");
            }
            sb.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1));
        }
        return sb.toString();
    }

    private static void gatherEntities(List<RawDisplayEntry> list) {
        list.add(new RawDisplayEntry("NPC: Sucateiro Nômade", 3, (level, x, groundY, z) -> {
            spawnFrozenEntity(level, new NomadScavengerEntity(SandStormEntities.NOMAD_SCAVENGER, level), x, groundY, z, "Sucateiro Nômade");
        }));

        list.add(new RawDisplayEntry("Mob: Sentinela de Sucata", 3, (level, x, groundY, z) -> {
            spawnFrozenEntity(level, new ScrapSentinelEntity(SandStormEntities.SCRAP_SENTINEL, level), x, groundY, z, "Sentinela de Sucata");
        }));

        list.add(new RawDisplayEntry("Mob: Autômato Abandonado", 3, (level, x, groundY, z) -> {
            spawnFrozenEntity(level, new DerelictAutomatonEntity(SandStormEntities.DERELICT_AUTOMATON, level), x, groundY, z, "Autômato Abandonado");
        }));

        list.add(new RawDisplayEntry("Mob: Cão Cibernético", 3, (level, x, groundY, z) -> {
            spawnFrozenEntity(level, new CyberHoundEntity(SandStormEntities.CYBER_HOUND, level), x, groundY, z, "Cão Cibernético");
        }));

        list.add(new RawDisplayEntry("Mob: Unidade Operária", 3, (level, x, groundY, z) -> {
            spawnFrozenEntity(level, new LaborerUnitEntity(SandStormEntities.LABORER_UNIT, level), x, groundY, z, "Unidade Operária");
        }));

        list.add(new RawDisplayEntry("Mob: Drone Explorador", 3, (level, x, groundY, z) -> {
            spawnFrozenEntity(level, new ScoutDroneEntity(SandStormEntities.SCOUT_DRONE, level), x, groundY, z, "Drone Explorador");
        }));

        list.add(new RawDisplayEntry("Mob: Drone Rastreador", 3, (level, x, groundY, z) -> {
            spawnFrozenEntity(level, new CrawlerDroneEntity(SandStormEntities.CRAWLER_DRONE, level), x, groundY, z, "Drone Rastreador");
        }));

        list.add(new RawDisplayEntry("Criatura: Besouro Aquífero", 3, (level, x, groundY, z) -> {
            spawnFrozenEntity(level, new AquiferBeetleEntity(SandStormEntities.AQUIFER_BEETLE, level), x, groundY, z, "Besouro Aquífero");
        }));

        list.add(new RawDisplayEntry("Androide: Minerador", 3, (level, x, groundY, z) -> {
            spawnFrozenEntity(level, new CyborgExcavatorEntity(SandStormEntities.CYBORG_EXCAVATOR, level), x, groundY, z, "Androide Minerador");
        }));

        list.add(new RawDisplayEntry("Androide: Construtor", 3, (level, x, groundY, z) -> {
            spawnFrozenEntity(level, new CyborgBuilderEntity(SandStormEntities.CYBORG_BUILDER, level), x, groundY, z, "Androide Construtor");
        }));

        list.add(new RawDisplayEntry("Androide: Agrícola", 3, (level, x, groundY, z) -> {
            spawnFrozenEntity(level, new CyborgHarvesterEntity(SandStormEntities.CYBORG_HARVESTER, level), x, groundY, z, "Androide Agrícola");
        }));

        list.add(new RawDisplayEntry("Drone: Carga", 3, (level, x, groundY, z) -> {
            spawnFrozenEntity(level, new CargoDroneEntity(SandStormEntities.CARGO_DRONE, level), x, groundY, z, "Drone de Carga");
        }));

        list.add(new RawDisplayEntry("Drone: Construtor", 3, (level, x, groundY, z) -> {
            spawnFrozenEntity(level, new BuilderDroneEntity(SandStormEntities.BUILDER_DRONE, level), x, groundY, z, "Drone Construtor");
        }));

        list.add(new RawDisplayEntry("Veículo: Sandboard", 3, (level, x, groundY, z) -> {
            spawnFrozenEntity(level, new SandboardEntity(SandStormEntities.SANDBOARD, level), x, groundY, z, "Prancha Sandboard");
        }));

        list.add(new RawDisplayEntry("Veículo: Escavador Pesado", 5, (level, x, groundY, z) -> {
            spawnFrozenEntity(level, new ExcavatorVehicleEntity(SandStormEntities.EXCAVATOR_VEHICLE, level), x, groundY, z, "Escavador Pesado");
        }));

        list.add(new RawDisplayEntry("Mecha: Megazord Titan", 6, (level, x, groundY, z) -> {
            spawnFrozenEntity(level, new MegazordEntity(SandStormEntities.MEGAZORD, level), x, groundY, z, "Megazord Titan");
        }));

        list.add(new RawDisplayEntry("Monstro: Verme de Areia", 12, (level, x, groundY, z) -> {
            spawnFrozenEntity(level, new SandwormEntity(SandStormEntities.SANDWORM, level), x, groundY, z, "Verme de Areia");
        }));
    }

    private static void gatherStructures(List<RawDisplayEntry> list) {
        list.add(new RawDisplayEntry("Estrutura: Posto Avançado", 8, (level, x, groundY, z) -> {
            SandstormBuildCommand.build(level, new BlockPos(x, groundY, z), "colony_outpost");
        }));

        list.add(new RawDisplayEntry("Estrutura: Estufa Hidropônica", 8, (level, x, groundY, z) -> {
            SandstormBuildCommand.build(level, new BlockPos(x, groundY, z), "hydroponics_greenhouse");
        }));

        list.add(new RawDisplayEntry("Estrutura: Complexo de Mineração", 9, (level, x, groundY, z) -> {
            SandstormBuildCommand.build(level, new BlockPos(x, groundY, z), "mining_complex");
        }));

        list.add(new RawDisplayEntry("Estrutura: Rede Energética", 8, (level, x, groundY, z) -> {
            SandstormBuildCommand.build(level, new BlockPos(x, groundY, z), "energy_grid");
        }));

        list.add(new RawDisplayEntry("Estrutura: Perímetro Defensivo", 10, (level, x, groundY, z) -> {
            SandstormBuildCommand.build(level, new BlockPos(x, groundY, z), "defense_perimeter");
        }));

        list.add(new RawDisplayEntry("Estrutura: Laboratório de Ruínas", 8, (level, x, groundY, z) -> {
            SandstormBuildCommand.build(level, new BlockPos(x, groundY, z), "ruins_laboratory");
        }));

        list.add(new RawDisplayEntry("Estrutura: Silo de Lançamento Orbital", 12, (level, x, groundY, z) -> {
            SandstormBuildCommand.build(level, new BlockPos(x, groundY, z), "orbital_launch_silo");
        }));

        list.add(new RawDisplayEntry("Estrutura: Pirâmide Tecnológica", 16, (level, x, groundY, z) -> {
            SandstormBuildCommand.build(level, new BlockPos(x, groundY, z), "desert_tech_pyramid");
        }));

        list.add(new RawDisplayEntry("Estrutura: Cúpula de Biosfera", 14, (level, x, groundY, z) -> {
            SandstormBuildCommand.build(level, new BlockPos(x, groundY, z), "biosphere_dome");
        }));

        list.add(new RawDisplayEntry("Estrutura: Cidadela Planetária", 17, (level, x, groundY, z) -> {
            SandstormBuildCommand.build(level, new BlockPos(x, groundY, z), "planetary_citadel");
        }));

        list.add(new RawDisplayEntry("Megaestrutura: Castelo Colossal de Pedra", 45, (level, x, groundY, z) -> {
            ColossalCastleGenerator.generate(level, new BlockPos(x, groundY, z));
        }));
    }

    private static void paveMuseumPlaza(ServerLevel level, int cx, int groundY, int cz, int radius) {
        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                int wx = cx + dx;
                int wz = cz + dz;
                mpos.set(wx, groundY, wz);
                boolean isEdge = Math.abs(dx) == radius || Math.abs(dz) == radius;
                BlockState floor = isEdge ? Blocks.CUT_SANDSTONE.defaultBlockState() : Blocks.SMOOTH_STONE.defaultBlockState();
                level.setBlock(mpos, floor, 2);

                for (int dy = 1; dy <= 6; dy++) {
                    mpos.set(wx, groundY + dy, wz);
                    if (!level.getBlockState(mpos).isAir()) {
                        level.setBlock(mpos, Blocks.AIR.defaultBlockState(), 2);
                    }
                }
            }
        }
    }

    private static void placePedestal(ServerLevel level, BlockPos base) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos p = base.offset(dx, 0, dz);
                BlockState b = (dx == 0 && dz == 0)
                        ? Blocks.OCHRE_FROGLIGHT.defaultBlockState()
                        : Blocks.POLISHED_ANDESITE.defaultBlockState();
                level.setBlock(p, b, 2);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void spawnItemStand(ServerLevel level, BlockPos pos, Item item, String name) {
        EntityType<ArmorStand> armorStandType = (EntityType<ArmorStand>) (EntityType<?>) BuiltInRegistries.ENTITY_TYPE.getValue(SandStormMod.mcId("armor_stand"));
        ArmorStand stand = new ArmorStand(armorStandType, level);
        stand.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        stand.setShowArms(true);
        stand.setNoGravity(true);
        stand.setSilent(true);
        stand.setCustomName(Component.literal("§e" + name));
        stand.setCustomNameVisible(true);
        stand.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(item));
        level.addFreshEntity(stand);
    }

    private static void spawnFrozenEntity(ServerLevel level, Entity entity, int x, int groundY, int z, String name) {
        BlockPos base = new BlockPos(x, groundY, z);
        placePedestal(level, base);

        entity.setPos(x + 0.5, groundY + 1.0, z + 0.5);
        if (entity instanceof Mob mob) {
            mob.setNoAi(true);
            mob.setPersistenceRequired();
        }
        if (entity instanceof SandwormEntity worm) {
            worm.setShowcaseMode(true);
            worm.setWormSize(2, true);
        }
        entity.setNoGravity(true);
        entity.setSilent(true);
        entity.setCustomName(Component.literal("§6" + name));
        entity.setCustomNameVisible(true);
        level.addFreshEntity(entity);

        placeSign(level, base.offset(0, 0, -2), name, "Entidade");
    }

    private static void placeSign(ServerLevel level, BlockPos signPos, String title, String subtitle) {
        level.setBlock(signPos, Blocks.OAK_WALL_SIGN.defaultBlockState().setValue(WallSignBlock.FACING, Direction.NORTH), 3);
        if (level.getBlockEntity(signPos) instanceof SignBlockEntity signBe) {
            String l0 = title.length() > 15 ? title.substring(0, 15) : title;
            String l1 = subtitle.length() > 15 ? subtitle.substring(0, 15) : subtitle;
            SignText text = signBe.getText(SignTextSlot.FRONT).asMutable()
                    .setLine(0, Component.literal("§6§l" + l0))
                    .setLine(1, Component.literal("§f" + l1))
                    .setLine(3, Component.literal("§b[SandStorm]"))
                    .asImmutable();
            signBe.setText(text, SignTextSlot.FRONT);
            signBe.setChanged();
        }
    }
}
