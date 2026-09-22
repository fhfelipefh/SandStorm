package com.fhfelipefh.sandstorm.content.item;

import com.fhfelipefh.sandstorm.content.entity.SandStormEntities;
import com.fhfelipefh.sandstorm.content.entity.SandboardEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SandboardItem extends Item {

    public SandboardItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos clickedPos = context.getClickedPos();
        Direction clickedFace = context.getClickedFace();
        BlockPos spawnPos = clickedPos.relative(clickedFace);

        if (!level.isClientSide() && player != null) {
            ServerLevel serverLevel = (ServerLevel) level;
            SandboardEntity sandboard = SandStormEntities.SANDBOARD.create(serverLevel, EntitySpawnReason.SPAWN_ITEM_USE);
            if (sandboard != null) {
                sandboard.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
                sandboard.setYRot(player.getYRot());
                serverLevel.addFreshEntity(sandboard);
                player.startRiding(sandboard);
                serverLevel.playSound(null, spawnPos, SoundEvents.ARMOR_EQUIP_GENERIC.value(), SoundSource.PLAYERS, 1.0f, 1.0f);
                if (!player.getAbilities().instabuild) {
                    context.getItemInHand().shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            ServerLevel serverLevel = (ServerLevel) level;
            SandboardEntity sandboard = SandStormEntities.SANDBOARD.create(serverLevel, EntitySpawnReason.SPAWN_ITEM_USE);
            if (sandboard != null) {
                Vec3 look = player.getLookAngle();
                sandboard.setPos(player.getX() + look.x * 0.5, player.getY(), player.getZ() + look.z * 0.5);
                sandboard.setYRot(player.getYRot());
                serverLevel.addFreshEntity(sandboard);
                player.startRiding(sandboard);
                serverLevel.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_GENERIC.value(), SoundSource.PLAYERS, 1.0f, 1.0f);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.SUCCESS;
    }
}
