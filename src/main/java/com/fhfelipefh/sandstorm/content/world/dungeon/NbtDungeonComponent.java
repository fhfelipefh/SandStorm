package com.fhfelipefh.sandstorm.content.world.dungeon;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.Map;
import java.util.Optional;

public class NbtDungeonComponent implements DungeonComponent {

    private final String id;
    private final Identifier templateId;
    private final int weight;
    private final boolean unique;
    private final Map<DungeonDirection, DungeonSocket> sockets;
    private final DungeonComponent fallback;

    public NbtDungeonComponent(String id, Identifier templateId, int weight, boolean unique, Map<DungeonDirection, DungeonSocket> sockets, DungeonComponent fallback) {
        this.id = id;
        this.templateId = templateId;
        this.weight = weight;
        this.unique = unique;
        this.sockets = Map.copyOf(sockets);
        this.fallback = fallback;
    }

    public NbtDungeonComponent(String id, Identifier templateId, int weight, boolean unique, Map<DungeonDirection, DungeonSocket> sockets) {
        this(id, templateId, weight, unique, sockets, null);
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public int weight() {
        return weight;
    }

    @Override
    public boolean isUnique() {
        return unique;
    }

    @Override
    public DungeonSocket getSocket(DungeonDirection direction) {
        return sockets.getOrDefault(direction, DungeonSocket.SOLID);
    }

    public Identifier templateId() {
        return templateId;
    }

    public DungeonComponent fallback() {
        return fallback;
    }

    @Override
    public void build(ServerLevel level, BlockPos origin, DungeonRotation rotation, RandomSource random, int size) {
        StructureTemplateManager manager = level.getServer().getStructureTemplateManager();
        Optional<StructureTemplate> templateOpt = manager.get(templateId);

        if (templateOpt.isPresent()) {
            StructureTemplate template = templateOpt.get();
            Rotation mcRot = toMinecraftRotation(rotation);
            StructurePlaceSettings settings = new StructurePlaceSettings()
                    .setRotation(mcRot)
                    .setIgnoreEntities(false)
                    .setRotationPivot(new BlockPos(size / 2, 0, size / 2));

            template.placeInWorld(level, origin, origin, settings, random, 2);
            return;
        }

        if (fallback != null) {
            fallback.build(level, origin, rotation, random, size);
        }
    }

    public static Rotation toMinecraftRotation(DungeonRotation rotation) {
        return switch (rotation) {
            case NONE -> Rotation.NONE;
            case CLOCKWISE_90 -> Rotation.CLOCKWISE_90;
            case CLOCKWISE_180 -> Rotation.CLOCKWISE_180;
            case CLOCKWISE_270 -> Rotation.COUNTERCLOCKWISE_90;
        };
    }
}
