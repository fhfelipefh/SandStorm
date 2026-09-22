package com.fhfelipefh.sandstorm.content.recipe;

import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Collection;
import java.util.stream.Collectors;

public class RecipeUnlockHandler {
    public static void initialize() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();
            if (player != null) {
                Collection<RecipeHolder<?>> sandstormRecipes = server.getRecipeManager().getRecipes().stream()
                        .filter(recipe -> recipe.id().toString().contains(SandStormMod.MOD_ID + ":"))
                        .collect(Collectors.toList());
                player.awardRecipes(sandstormRecipes);
            }
        });
    }
}
