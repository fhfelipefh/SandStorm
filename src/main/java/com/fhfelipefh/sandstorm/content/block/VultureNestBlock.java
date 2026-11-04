package com.fhfelipefh.sandstorm.content.block;

import com.fhfelipefh.sandstorm.content.item.SandStormItems;
import com.fhfelipefh.sandstorm.content.quest.PlayerQuestSavedData;
import com.fhfelipefh.sandstorm.content.quest.QuestData;
import com.fhfelipefh.sandstorm.content.quest.QuestRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class VultureNestBlock extends Block {
    protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);

    public VultureNestBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (!state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return !level.isEmptyBlock(pos.below());
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && !player.isCreative()) {
            dropNestLoot(level, pos, player);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    private void dropNestLoot(Level level, BlockPos pos, Player player) {
        popResource(level, pos, new ItemStack(SandStormItems.SCRAP_METAL, 2 + level.getRandom().nextInt(3)));
        popResource(level, pos, new ItemStack(Items.IRON_NUGGET, 3 + level.getRandom().nextInt(5)));

        if (level.getRandom().nextFloat() < 0.60f) {
            popResource(level, pos, new ItemStack(SandStormItems.CARBON_FIBER_SERVO_FEATHER, 1 + level.getRandom().nextInt(2)));
        }
        if (level.getRandom().nextFloat() < 0.40f) {
            popResource(level, pos, new ItemStack(SandStormItems.RAW_SILICON, 1 + level.getRandom().nextInt(3)));
        }

        boolean assisted = false;
        if (player instanceof ServerPlayer serverPlayer && level instanceof ServerLevel serverLevel) {
            if (serverLevel.getRandom().nextFloat() < 0.50f) {
                PlayerQuestSavedData questData = PlayerQuestSavedData.get(serverLevel);
                UUID playerId = serverPlayer.getUUID();
                List<QuestData> activeQuests = new ArrayList<>();
                for (QuestData q : QuestRegistry.getAllQuests().values()) {
                    if (!questData.isClaimed(playerId, q.id()) && !questData.isReadyToClaim(playerId, q.id())) {
                        boolean prereqsMet = q.prerequisiteIds().stream().allMatch(p -> questData.isClaimed(playerId, p));
                        if (prereqsMet) {
                            activeQuests.add(q);
                        }
                    }
                }
                if (!activeQuests.isEmpty()) {
                    activeQuests.sort(Comparator.comparingInt(QuestData::chapter));
                    QuestData nextQuest = activeQuests.get(0);
                    List<IngredientReward> craftingComponents = getCraftingIngredientsForQuest(nextQuest);
                    if (!craftingComponents.isEmpty()) {
                        for (IngredientReward reward : craftingComponents) {
                            popResource(level, pos, new ItemStack(reward.item(), reward.count()));
                        }
                        assisted = true;
                        serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 12, 0.25, 0.25, 0.25, 0.05);
                        serverLevel.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 0.8f, 1.4f);
                    }
                }
            }
        }

        if (!assisted) {
            float roll = level.getRandom().nextFloat();
            if (roll < 0.05f) {
                popResource(level, pos, new ItemStack(SandStormItems.MANTLE_ALLOY_INGOT, 1));
            } else if (roll < 0.11f) {
                popResource(level, pos, new ItemStack(SandStormItems.TITANIUM_CHITIN_COMPOSITE, 1 + level.getRandom().nextInt(2)));
            } else if (roll < 0.18f) {
                popResource(level, pos, new ItemStack(SandStormItems.NANO_ACTUATOR, 1));
            } else if (roll < 0.25f) {
                popResource(level, pos, new ItemStack(SandStormItems.SUIT_GLIDER_MODULE, 1));
            } else if (roll < 0.32f) {
                popResource(level, pos, new ItemStack(SandStormItems.TECH_DISC, 1));
            } else if (roll < 0.45f) {
                popResource(level, pos, new ItemStack(SandStormItems.CIRCUIT_BOARD, 1));
            } else if (roll < 0.60f) {
                popResource(level, pos, new ItemStack(SandStormItems.AVIONICS_OPTICAL_SENSOR, 1));
            } else if (roll < 0.75f) {
                popResource(level, pos, new ItemStack(SandStormItems.SILICON_WAFER, 1 + level.getRandom().nextInt(2)));
            } else if (roll < 0.88f) {
                popResource(level, pos, new ItemStack(SandStormItems.ELECTRIC_COMPONENT, 1 + level.getRandom().nextInt(2)));
            } else {
                popResource(level, pos, new ItemStack(Items.COPPER_INGOT, 1 + level.getRandom().nextInt(3)));
            }
        }
    }

    public record IngredientReward(Item item, int count) {}

    public static List<IngredientReward> getCraftingIngredientsForQuest(QuestData quest) {
        List<IngredientReward> ingredients = new ArrayList<>();
        String qId = quest.id();
        switch (qId) {
            case "silicon_shovel" -> {
                ingredients.add(new IngredientReward(SandStormItems.RAW_SILICON, 1));
                ingredients.add(new IngredientReward(SandStormItems.TOOL_BASE, 2));
            }
            case "silicon_pickaxe" -> {
                ingredients.add(new IngredientReward(SandStormItems.RAW_SILICON, 3));
                ingredients.add(new IngredientReward(SandStormItems.TOOL_BASE, 2));
            }
            case "tool_base" -> {
                ingredients.add(new IngredientReward(Blocks.SANDSTONE.asItem(), 2));
                ingredients.add(new IngredientReward(Items.STICK, 2));
            }
            case "compact_sandstone" -> ingredients.add(new IngredientReward(Blocks.SAND.asItem(), 4));
            case "emergency_workbench" -> ingredients.add(new IngredientReward(Blocks.SANDSTONE.asItem(), 4));
            case "sandstone_furnace" -> ingredients.add(new IngredientReward(Blocks.SANDSTONE.asItem(), 8));
            case "electric_component" -> {
                ingredients.add(new IngredientReward(SandStormItems.RAW_SILICON, 2));
                ingredients.add(new IngredientReward(Items.COPPER_INGOT, 1));
            }
            case "collect_coal" -> {
                ingredients.add(new IngredientReward(Items.COAL, 4));
                ingredients.add(new IngredientReward(Items.TORCH, 4));
            }
            case "glass_canister" -> ingredients.add(new IngredientReward(Blocks.GLASS.asItem(), 3));
            case "brackish_brine" -> ingredients.add(new IngredientReward(Items.GLASS_BOTTLE, 2));
            case "silicon_smelting" -> {
                ingredients.add(new IngredientReward(SandStormItems.RAW_SILICON, 4));
                ingredients.add(new IngredientReward(Items.COAL, 2));
            }
            case "silicon_wafer" -> {
                ingredients.add(new IngredientReward(SandStormItems.RAW_SILICON, 2));
                ingredients.add(new IngredientReward(Items.COAL, 1));
            }
            case "desalination_system" -> {
                ingredients.add(new IngredientReward(SandStormItems.FILTER_CARTRIDGE, 1));
                ingredients.add(new IngredientReward(Items.COPPER_INGOT, 3));
                ingredients.add(new IngredientReward(SandStormItems.SCRAP_METAL, 2));
            }
            case "thermal_purification" -> {
                ingredients.add(new IngredientReward(SandStormItems.BRACKISH_WATER_BOTTLE, 2));
                ingredients.add(new IngredientReward(SandStormItems.FILTER_CARTRIDGE, 1));
            }
            case "circuit_manufacturing" -> {
                ingredients.add(new IngredientReward(SandStormItems.SILICON_WAFER, 2));
                ingredients.add(new IngredientReward(SandStormItems.ELECTRIC_COMPONENT, 2));
                ingredients.add(new IngredientReward(Items.COPPER_INGOT, 2));
            }
            case "additive_printer" -> {
                ingredients.add(new IngredientReward(SandStormItems.CIRCUIT_BOARD, 2));
                ingredients.add(new IngredientReward(SandStormItems.SCRAP_METAL, 4));
                ingredients.add(new IngredientReward(SandStormItems.RAW_SILICON, 4));
            }
            case "wireless_solar_receiver" -> {
                ingredients.add(new IngredientReward(SandStormItems.CIRCUIT_BOARD, 2));
                ingredients.add(new IngredientReward(SandStormItems.SCRAP_METAL, 3));
                ingredients.add(new IngredientReward(Items.COPPER_INGOT, 3));
            }
            case "anomaly_sensor" -> {
                ingredients.add(new IngredientReward(SandStormItems.CIRCUIT_BOARD, 1));
                ingredients.add(new IngredientReward(SandStormItems.ELECTRIC_COMPONENT, 2));
                ingredients.add(new IngredientReward(SandStormItems.SCRAP_METAL, 2));
            }
            case "seismic_thumper", "seismic_bait" -> {
                ingredients.add(new IngredientReward(SandStormItems.CIRCUIT_BOARD, 2));
                ingredients.add(new IngredientReward(SandStormItems.SCRAP_METAL, 4));
                ingredients.add(new IngredientReward(SandStormItems.RAW_SILICON, 4));
            }
            case "sonic_defense" -> {
                ingredients.add(new IngredientReward(SandStormItems.CIRCUIT_BOARD, 2));
                ingredients.add(new IngredientReward(SandStormItems.TITANIUM_CHITIN_COMPOSITE, 2));
            }
            case "nanite_assembler" -> {
                ingredients.add(new IngredientReward(SandStormItems.CIRCUIT_BOARD, 2));
                ingredients.add(new IngredientReward(SandStormItems.NANO_ACTUATOR, 2));
                ingredients.add(new IngredientReward(SandStormItems.SCRAP_METAL, 4));
            }
            case "mecha_assembly" -> {
                ingredients.add(new IngredientReward(SandStormItems.TITANIUM_CHITIN_COMPOSITE, 4));
                ingredients.add(new IngredientReward(SandStormItems.NANO_ACTUATOR, 2));
                ingredients.add(new IngredientReward(SandStormItems.TECH_DISC, 1));
            }
            case "megazord_upgrades" -> {
                ingredients.add(new IngredientReward(SandStormItems.TITANIUM_CHITIN_COMPOSITE, 4));
                ingredients.add(new IngredientReward(SandStormItems.NANO_ACTUATOR, 2));
            }
            case "reprogrammer_tool" -> {
                ingredients.add(new IngredientReward(SandStormItems.CIRCUIT_BOARD, 2));
                ingredients.add(new IngredientReward(SandStormItems.AVIONICS_OPTICAL_SENSOR, 1));
                ingredients.add(new IngredientReward(SandStormItems.SCRAP_METAL, 3));
            }
            case "cyber_hound_companion" -> {
                ingredients.add(new IngredientReward(SandStormItems.REPROGRAMMER_TOOL, 1));
                ingredients.add(new IngredientReward(SandStormItems.SCRAP_METAL, 4));
            }
            case "molecular_modifier" -> {
                ingredients.add(new IngredientReward(SandStormItems.TITANIUM_CHITIN_COMPOSITE, 2));
                ingredients.add(new IngredientReward(SandStormItems.CIRCUIT_BOARD, 2));
                ingredients.add(new IngredientReward(SandStormItems.NANO_ACTUATOR, 1));
            }
            case "atmospheric_probe" -> {
                ingredients.add(new IngredientReward(SandStormItems.CIRCUIT_BOARD, 2));
                ingredients.add(new IngredientReward(SandStormItems.AVIONICS_OPTICAL_SENSOR, 1));
            }
            case "nutrient_bomb" -> {
                ingredients.add(new IngredientReward(SandStormItems.SPACE_RATION, 2));
                ingredients.add(new IngredientReward(SandStormItems.RAW_SILICON, 2));
            }
            case "planet_terraformer" -> {
                ingredients.add(new IngredientReward(SandStormItems.TITANIUM_CHITIN_COMPOSITE, 4));
                ingredients.add(new IngredientReward(SandStormItems.NANO_ACTUATOR, 2));
                ingredients.add(new IngredientReward(SandStormItems.CIRCUIT_BOARD, 2));
            }
            case "biosphere_cartridge" -> {
                ingredients.add(new IngredientReward(SandStormItems.TITANIUM_CHITIN_COMPOSITE, 2));
                ingredients.add(new IngredientReward(SandStormItems.CIRCUIT_BOARD, 2));
            }
            case "suit_diagnostics" -> {
                ingredients.add(new IngredientReward(SandStormItems.TITANIUM_CHITIN_COMPOSITE, 2));
                ingredients.add(new IngredientReward(SandStormItems.AVIONICS_OPTICAL_SENSOR, 1));
            }
            default -> {
                Item req = quest.getRequiredItem();
                if (req != null && req != Items.AIR) {
                    ingredients.add(new IngredientReward(req, 1));
                }
            }
        }
        return ingredients;
    }
}
