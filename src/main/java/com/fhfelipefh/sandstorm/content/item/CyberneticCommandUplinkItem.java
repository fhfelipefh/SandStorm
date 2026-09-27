package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgEntity;
import com.fhfelipefh.sandstorm.content.entity.cyborg.CyborgRoutine;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CyberneticCommandUplinkItem extends Item {
    public enum UplinkMode {
        MINING("mining", 0x00E5FF),
        BUILDING("building", 0xFF9100),
        HARVESTING("harvesting", 0x00E676),
        PATROL("patrol", 0xD500F9),
        MOVE_ORDER("move_order", 0xFFFFFF);

        private final String id;
        private final int color;

        UplinkMode(String id, int color) {
            this.id = id;
            this.color = color;
        }

        public String getId() {
            return id;
        }

        public int getColor() {
            return color;
        }

        public UplinkMode next() {
            return values()[(ordinal() + 1) % values().length];
        }

        public UplinkMode cycle() {
            return switch (this) {
                case MINING -> BUILDING;
                case BUILDING -> HARVESTING;
                case HARVESTING -> MINING;
                case PATROL -> MOVE_ORDER;
                case MOVE_ORDER -> PATROL;
            };
        }

        public Component getDisplayName() {
            return Component.translatable("uplink.mode.sandstorm." + id);
        }

        public static UplinkMode fromOrdinal(int ordinal) {
            UplinkMode[] values = values();
            if (ordinal < 0 || ordinal >= values.length) {
                return MINING;
            }
            return values[ordinal];
        }
    }

    private static final Map<UUID, BlockPos> PLAYER_CORNER_A = new HashMap<>();
    private static final Map<UUID, BlockPos> PLAYER_CORNER_B = new HashMap<>();
    private static final Map<UUID, UplinkMode> PLAYER_MODE = new HashMap<>();

    public CyberneticCommandUplinkItem(Properties properties) {
        super(properties);
    }

    public static UplinkMode getPlayerMode(Player player) {
        return PLAYER_MODE.getOrDefault(player.getUUID(), UplinkMode.MINING);
    }

    public static void setPlayerMode(Player player, UplinkMode mode) {
        PLAYER_MODE.put(player.getUUID(), mode != null ? mode : UplinkMode.MINING);
    }

    public static BlockPos getCornerA(Player player) {
        return PLAYER_CORNER_A.get(player.getUUID());
    }

    public static BlockPos getCornerB(Player player) {
        return PLAYER_CORNER_B.get(player.getUUID());
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                UplinkMode newMode = getPlayerMode(player).next();
                setPlayerMode(player, newMode);
                level.playSound(null, player.blockPosition(), SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 0.8f, 1.2f);
                player.sendSystemMessage(Component.translatable("telemetry.sandstorm.uplink_mode_switched",
                        Component.translatable("telemetry.sandstorm.uplink_mode." + newMode.getId())));
            }
            return InteractionResult.SUCCESS;
        }
        return super.use(level, player, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }

        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();

        if (player.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                UplinkMode newMode = getPlayerMode(player).next();
                setPlayerMode(player, newMode);
                level.playSound(null, clickedPos, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 0.8f, 1.2f);
                player.sendSystemMessage(Component.translatable("telemetry.sandstorm.uplink_mode_switched",
                        Component.translatable("telemetry.sandstorm.uplink_mode." + newMode.getId())));
            }
            return InteractionResult.SUCCESS;
        }

        if (!level.isClientSide()) {
            UplinkMode mode = getPlayerMode(player);
            if (mode == UplinkMode.MOVE_ORDER) {
                ServerLevel serverLevel = (ServerLevel) level;
                AABB range = new AABB(player.blockPosition()).inflate(64.0);
                List<CyborgEntity> ownedCyborgs = serverLevel.getEntitiesOfClass(CyborgEntity.class, range, c -> player.getUUID().equals(c.getOwnerUUID()));
                for (CyborgEntity c : ownedCyborgs) {
                    c.getNavigation().moveTo(clickedPos.getX() + 0.5, clickedPos.getY() + 1.0, clickedPos.getZ() + 0.5, 1.2);
                    c.setVisorState(1);
                }
                level.playSound(null, clickedPos, SoundEvents.NOTE_BLOCK_BELL.value(), SoundSource.PLAYERS, 1.0f, 1.6f);
                player.sendSystemMessage(Component.translatable("telemetry.sandstorm.uplink_move_command", ownedCyborgs.size(), clickedPos.getX(), clickedPos.getY(), clickedPos.getZ()));
                return InteractionResult.SUCCESS;
            }

            UUID uuid = player.getUUID();
            BlockPos cornerA = PLAYER_CORNER_A.get(uuid);

            if (cornerA == null) {
                PLAYER_CORNER_A.put(uuid, clickedPos);
                level.playSound(null, clickedPos, SoundEvents.NOTE_BLOCK_CHIME.value(), SoundSource.PLAYERS, 0.9f, 1.4f);
                player.sendSystemMessage(Component.translatable("telemetry.sandstorm.uplink_corner_a_set",
                        clickedPos.getX(), clickedPos.getY(), clickedPos.getZ()));
            } else {
                PLAYER_CORNER_B.put(uuid, clickedPos);
                int sizeX = Math.abs(clickedPos.getX() - cornerA.getX()) + 1;
                int sizeY = Math.abs(clickedPos.getY() - cornerA.getY()) + 1;
                int sizeZ = Math.abs(clickedPos.getZ() - cornerA.getZ()) + 1;

                level.playSound(null, clickedPos, SoundEvents.NOTE_BLOCK_BELL.value(), SoundSource.PLAYERS, 1.0f, 1.8f);
                player.sendSystemMessage(Component.translatable("telemetry.sandstorm.uplink_zone_configured",
                        sizeX, sizeY, sizeZ));
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand hand) {
        if (interactionTarget instanceof CyborgEntity cyborg) {
            Level level = player.level();
            if (!level.isClientSide()) {
                UUID uuid = player.getUUID();
                BlockPos cornerA = PLAYER_CORNER_A.get(uuid);
                BlockPos cornerB = PLAYER_CORNER_B.get(uuid);

                if (cornerA != null && cornerB != null) {
                    cyborg.setDemarcatedZone(cornerA, cornerB);
                    if (getPlayerMode(player) == UplinkMode.PATROL) {
                        cyborg.setRoutine(CyborgRoutine.PATROL_PERIMETER);
                    }
                    player.sendSystemMessage(Component.translatable("telemetry.sandstorm.uplink_zone_assigned"));
                    level.playSound(null, cyborg.blockPosition(), SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.PLAYERS, 1.0f, 1.5f);
                }

                if (cyborg.getOwnerUUID() == null) {
                    cyborg.setOwnerUUID(player.getUUID());
                }
                player.openMenu(cyborg);
            }
            return InteractionResult.SUCCESS;
        }

        return super.interactLivingEntity(stack, player, interactionTarget, hand);
    }
}
