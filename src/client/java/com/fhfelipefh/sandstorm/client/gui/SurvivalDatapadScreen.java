package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.client.hud.SurvivalHudOverlay;
import com.fhfelipefh.sandstorm.client.renderer.MultiblockHologramRenderer;
import com.fhfelipefh.sandstorm.component.SuitPowerComponent;
import com.fhfelipefh.sandstorm.content.multiblock.MultiblockBlockRequirement;
import com.fhfelipefh.sandstorm.content.multiblock.MultiblockRegistry;
import com.fhfelipefh.sandstorm.content.multiblock.MultiblockStructure;
import com.fhfelipefh.sandstorm.content.network.ClaimQuestRewardPayload;
import com.fhfelipefh.sandstorm.content.quest.QuestData;
import com.fhfelipefh.sandstorm.content.quest.QuestRegistry;
import com.fhfelipefh.sandstorm.content.quest.QuestRewardHandler;
import com.fhfelipefh.sandstorm.core.SandStormMod;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SurvivalDatapadScreen extends Screen {
    private int currentChapter = 1;
    private int scrollOffset = 0;
    private int maxScroll = 0;
    private final Map<String, Long> claimFlashTimestamps = new HashMap<>();
    private Component statusTooltip;
    private long statusTooltipExpiry;
    private String lastHoveredClaimableQuestId;
    private boolean clickHandledOnPress = false;
    private MultiblockStructure ponderStructure;
    private int ponderStep = 1;

    public SurvivalDatapadScreen() {
        super(Component.translatable("gui.sandstorm.datapad.title"));
    }

    @Override
    protected void init() {
        super.init();
        SandStormMod.LOGGER.info("Datapad opened by client player");
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        lastHoveredClaimableQuestId = null;

        extractor.fill(0, 0, width, height, 0xD00A0E17);

        int left = 16;
        int top = 12;
        int right = width - 16;
        int bottom = height - 12;

        if (ponderStructure != null) {
            renderPonderView(extractor, left, top, right, bottom, mouseX, mouseY);
            return;
        }

        extractor.fill(left, top, right, bottom, 0xEE101824);
        extractor.fill(left, top, right, top + 1, 0xFF00E5FF);
        extractor.fill(left, bottom - 1, right, bottom, 0xFF00E5FF);
        extractor.fill(left, top, left + 1, bottom, 0xFF00E5FF);
        extractor.fill(right - 1, top, right, bottom, 0xFF00E5FF);

        Component titleComp = Component.literal("I.A.T.I. // ARRAKIS-IX");
        boolean satLinked = DatapadClientHelper.isSatelliteActive();
        Component statusComp = satLinked ? Component.translatable("gui.sandstorm.datapad.status_online") : Component.translatable("gui.sandstorm.datapad.status_offline");
        int statusColor = satLinked ? 0xFF76FF03 : 0xFFFFA000;
        int statusWidth = font.width(statusComp);
        int statusX = right - statusWidth - 10;
        float maxHeaderTitleWidth = statusX - (left + 10) - 12;

        if (maxHeaderTitleWidth < 140) {
            float halfWidth = (right - left - 24) / 2f;
            drawScaledText(extractor, titleComp, left + 10, top + 8, halfWidth, 0xFF00E5FF);
            drawScaledText(extractor, statusComp, left + 10 + halfWidth + 4, top + 8, halfWidth, statusColor);
        } else {
            drawScaledText(extractor, titleComp, left + 10, top + 8, maxHeaderTitleWidth, 0xFF00E5FF);
            extractor.text(font, statusComp, statusX, top + 8, statusColor);
        }

        Component chapterComp = Component.translatable("gui.sandstorm.datapad.chapter." + currentChapter);
        Component shortcutHint = Component.translatable("gui.sandstorm.datapad.shortcut_hint");
        int hintWidth = font.width(shortcutHint);
        extractor.text(font, shortcutHint, right - hintWidth - 10, top + 20, 0xFF00E5FF);
        drawScaledText(extractor, chapterComp, left + 10, top + 20, right - left - hintWidth - 24, 0xFFFFD54F);

        extractor.fill(left + 6, top + 32, right - 6, top + 33, 0x5500E5FF);

        int tabCount = 6;
        int tabWidth = (right - left - 20) / tabCount;
        for (int ch = 1; ch <= tabCount; ch++) {
            int tx = left + 10 + (ch - 1) * tabWidth;
            int ty = top + 36;
            boolean isSelected = (ch == currentChapter);

            extractor.fill(tx + 1, ty, tx + tabWidth - 2, ty + 16, isSelected ? 0xFF005B66 : 0x88152233);
            extractor.fill(tx + 1, ty + 15, tx + tabWidth - 2, ty + 16, isSelected ? 0xFF00E5FF : 0x4400E5FF);
            drawScaledCenteredText(extractor, Component.translatable("gui.sandstorm.datapad.tab." + ch), tx + tabWidth / 2f, ty + 4, tabWidth - 6, isSelected ? 0xFFFFFFFF : 0xFF90A4AE);
        }

        int questAreaTop = top + 58;
        int questAreaBottom = bottom - 10;
        int cardHeight = 46;
        int cardSpacing = 6;
        int visibleHeight = questAreaBottom - questAreaTop;

        Player player = minecraft != null ? minecraft.player : null;
        long currentTimeMs = System.currentTimeMillis();

        if (currentChapter == 6) {
            renderMultiblockTab(extractor, left, right, questAreaTop, questAreaBottom, cardHeight, cardSpacing, visibleHeight, mouseX, mouseY);
        } else {
            renderQuestsTab(extractor, left, right, questAreaTop, questAreaBottom, cardHeight, cardSpacing, visibleHeight, player, currentTimeMs, mouseX, mouseY);
        }

        if (statusTooltip != null && currentTimeMs < statusTooltipExpiry) {
            int maxTooltipWidth = Math.max(80, width - 24);
            int rawTooltipWidth = font.width(statusTooltip) + 12;
            int tooltipWidth = Math.min(rawTooltipWidth, maxTooltipWidth);
            int tooltipX = (width - tooltipWidth) / 2;
            int tooltipY = bottom - 28;
            extractor.fill(tooltipX, tooltipY, tooltipX + tooltipWidth, tooltipY + 14, 0xEE1A237E);
            extractor.fill(tooltipX, tooltipY, tooltipX + tooltipWidth, tooltipY + 1, 0xFFFF6D00);
            drawScaledCenteredText(extractor, statusTooltip, width / 2f, tooltipY + 3, tooltipWidth - 8, 0xFFFFAB40);
        } else {
            statusTooltip = null;
        }
    }

    private void renderQuestsTab(GuiGraphicsExtractor extractor, int left, int right,
                                 int questAreaTop, int questAreaBottom, int cardHeight, int cardSpacing,
                                 int visibleHeight, Player player, long currentTimeMs, int mouseX, int mouseY) {
        List<QuestData> quests = QuestRegistry.getQuestsForChapter(currentChapter);
        int totalContentHeight = quests.size() * (cardHeight + cardSpacing);
        maxScroll = Math.max(0, totalContentHeight - visibleHeight);
        scrollOffset = Math.clamp(scrollOffset, 0, maxScroll);

        for (int i = 0; i < quests.size(); i++) {
            QuestData quest = quests.get(i);
            int cy = questAreaTop + i * (cardHeight + cardSpacing) - scrollOffset;
            if (cy + cardHeight < questAreaTop || cy > questAreaBottom) {
                continue;
            }

            boolean isClaimed = DatapadClientHelper.isQuestClaimed(quest.id());
            boolean prereqsClaimed = arePrerequisitesClaimed(quest);
            boolean hasItem = hasRequiredItem(player, quest);
            boolean isClaimable = !isClaimed && prereqsClaimed && hasItem;
            boolean isInProgress = !isClaimed && prereqsClaimed && !hasItem;

            Long flashTime = claimFlashTimestamps.get(quest.id());
            boolean isFlashing = flashTime != null && (currentTimeMs - flashTime) < 600;
            float flashAlpha = isFlashing ? 1.0f - ((currentTimeMs - flashTime) / 600f) : 0f;

            int cardLeft = left + 10;
            int cardRight = right - 10;

            int cardBg = isClaimed ? 0xDD0D2619 : (isClaimable ? 0xDD1B2A38 : (isInProgress ? 0xDD121F2D : 0xBB181820));
            extractor.fill(cardLeft, cy, cardRight, cy + cardHeight, cardBg);

            if (isFlashing) {
                int flashOverlay = ((int) (flashAlpha * 80) << 24) | 0x00E5FF;
                extractor.fill(cardLeft, cy, cardRight, cy + cardHeight, flashOverlay);
            }

            int borderColor = isClaimed ? 0xFF00E676 : (isClaimable ? 0xFFFFD54F : (isInProgress ? 0xFF00B0FF : 0xFF546E7A));
            extractor.fill(cardLeft, cy, cardLeft + 2, cy + cardHeight, borderColor);

            if (quest.getIconItem() != null) {
                extractor.item(quest.getIconItem().getDefaultInstance(), cardLeft + 8, cy + 15);
            }

            Component badge = isClaimed ? Component.translatable("gui.sandstorm.datapad.status.completed")
                    : (isClaimable ? Component.translatable("gui.sandstorm.datapad.status.ready")
                    : (isInProgress ? Component.translatable("gui.sandstorm.datapad.status.in_progress")
                    : Component.translatable("gui.sandstorm.datapad.status.locked")));
            int badgeColor = isClaimed ? 0xFF69F0AE : (isClaimable ? 0xFFFFD54F : (isInProgress ? 0xFF80D8FF : 0xFF78909C));
            float availableCardWidth = Math.max(20f, (cardRight - 8) - (cardLeft + 30));
            int rawBadgeWidth = font.width(badge);
            float maxBadgeWidth = Math.min(rawBadgeWidth, availableCardWidth * 0.4f);
            float badgeX = cardRight - maxBadgeWidth - 8;
            drawScaledText(extractor, badge, badgeX, cy + 5, maxBadgeWidth, badgeColor);

            int titleColor = isClaimed ? 0xFF69F0AE : (isClaimable ? 0xFFFFF176 : (isInProgress ? 0xFFE0F7FA : 0xFFB0BEC5));
            float maxTitleWidth = Math.max(0, badgeX - (cardLeft + 30) - 8);
            drawScaledText(extractor, Component.translatable(quest.titleKey()), cardLeft + 30, cy + 5, maxTitleWidth, titleColor);

            MultiblockStructure associatedStructure = MultiblockRegistry.getStructureForQuest(quest.id());
            int holoOffset = (associatedStructure != null) ? 80 : 0;

            float maxLineWidth = Math.max(0, (cardRight - 94 - holoOffset) - (cardLeft + 30) - 4);
            drawScaledText(extractor, Component.translatable(quest.taskKey()), cardLeft + 30, cy + 17, maxLineWidth, 0xFFCFD8DC);
            drawScaledText(extractor, Component.translatable(quest.noteKey()), cardLeft + 30, cy + 29, maxLineWidth, 0xFF80DEEA);

            if (associatedStructure != null) {
                int holoW = 34;
                int holoH = 18;
                int holoX = cardRight - 60 - holoW - 4;
                int pdrW = 34;
                int pdrX = holoX - pdrW - 4;
                int holoY = cy + 17;

                boolean pdrHovered = mouseX >= pdrX && mouseX <= pdrX + pdrW && mouseY >= holoY && mouseY <= holoY + holoH;
                int pdrBg = pdrHovered ? 0xFF00E5FF : 0xAA005B66;
                extractor.fill(pdrX, holoY, pdrX + pdrW, holoY + holoH, pdrBg);
                extractor.fill(pdrX, holoY, pdrX + pdrW, holoY + 1, 0xFF00E5FF);
                extractor.fill(pdrX, holoY + holoH - 1, pdrX + pdrW, holoY + holoH, 0xFF00E5FF);
                extractor.fill(pdrX, holoY, pdrX + 1, holoY + holoH, 0xFF00E5FF);
                extractor.fill(pdrX + pdrW - 1, holoY, pdrX + pdrW, holoY + holoH, 0xFF00E5FF);

                Component pdrText = Component.translatable("gui.sandstorm.datapad.multiblock.ponder_btn");
                drawScaledCenteredText(extractor, pdrText, pdrX + pdrW / 2f, holoY + 5, pdrW - 2, pdrHovered ? 0xFF0A0E17 : 0xFFFFFFFF);

                boolean holoHovered = mouseX >= holoX && mouseX <= holoX + holoW && mouseY >= holoY && mouseY <= holoY + holoH;
                int holoBg = holoHovered ? 0xFF00E5FF : 0xAA005B66;
                extractor.fill(holoX, holoY, holoX + holoW, holoY + holoH, holoBg);
                extractor.fill(holoX, holoY, holoX + holoW, holoY + 1, 0xFF00E5FF);
                extractor.fill(holoX, holoY + holoH - 1, holoX + holoW, holoY + holoH, 0xFF00E5FF);
                extractor.fill(holoX, holoY, holoX + 1, holoY + holoH, 0xFF00E5FF);
                extractor.fill(holoX + holoW - 1, holoY, holoX + holoW, holoY + holoH, 0xFF00E5FF);

                Component holoText = Component.translatable("gui.sandstorm.datapad.multiblock.holo_btn");
                drawScaledCenteredText(extractor, holoText, holoX + holoW / 2f, holoY + 5, holoW - 2, holoHovered ? 0xFF0A0E17 : 0xFFFFFFFF);
            }

            if (quest.getRewardItem() != null) {
                extractor.item(quest.getRewardItem().getDefaultInstance(), cardRight - 94, cy + 18);
                String countStr = "x" + quest.rewardCount();
                extractor.text(font, countStr, cardRight - 76, cy + 22, 0xFFFFD54F);
            }

            int btnW = 56;
            int btnH = 20;
            int btnX = cardRight - 60;
            int btnY = cy + 16;

            if (isClaimable) {
                boolean hovered = mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;
                boolean cardHovered = mouseX >= cardLeft && mouseX <= cardRight && mouseY >= cy && mouseY <= cy + cardHeight;
                if (hovered || cardHovered) {
                    lastHoveredClaimableQuestId = quest.id();
                }

                int btnBg = hovered ? 0xFF00E5FF : 0xFF0091EA;
                extractor.fill(btnX, btnY, btnX + btnW, btnY + btnH, btnBg);
                extractor.fill(btnX, btnY, btnX + btnW, btnY + 1, hovered ? 0xFFFFFFFF : 0xFF80D8FF);
                extractor.fill(btnX, btnY + btnH - 1, btnX + btnW, btnY + btnH, hovered ? 0xFFFFFFFF : 0xFF80D8FF);
                extractor.fill(btnX, btnY, btnX + 1, btnY + btnH, hovered ? 0xFFFFFFFF : 0xFF80D8FF);
                extractor.fill(btnX + btnW - 1, btnY, btnX + btnW, btnY + btnH, hovered ? 0xFFFFFFFF : 0xFF80D8FF);

                Component claimText = Component.translatable("gui.sandstorm.datapad.claim_btn");
                drawScaledCenteredText(extractor, claimText, btnX + btnW / 2f, btnY + 6, btnW - 4, hovered ? 0xFF0A0E17 : 0xFFFFFFFF);
            } else if (isClaimed) {
                Component claimedText = Component.translatable("gui.sandstorm.datapad.claimed");
                drawScaledCenteredText(extractor, claimedText, btnX + btnW / 2f, btnY + 6, btnW - 2, 0xFF69F0AE);
            }
        }
    }

    private void renderMultiblockTab(GuiGraphicsExtractor extractor, int left, int right,
                                     int questAreaTop, int questAreaBottom, int cardHeight, int cardSpacing,
                                     int visibleHeight, int mouseX, int mouseY) {
        List<MultiblockStructure> structures = new ArrayList<>(MultiblockRegistry.getAllStructures().values());
        int totalContentHeight = structures.size() * (cardHeight + cardSpacing);
        maxScroll = Math.max(0, totalContentHeight - visibleHeight);
        scrollOffset = Math.clamp(scrollOffset, 0, maxScroll);

        for (int i = 0; i < structures.size(); i++) {
            MultiblockStructure structure = structures.get(i);
            int cy = questAreaTop + i * (cardHeight + cardSpacing) - scrollOffset;
            if (cy + cardHeight < questAreaTop || cy > questAreaBottom) {
                continue;
            }

            int cardLeft = left + 10;
            int cardRight = right - 10;

            boolean isActive = MultiblockHologramRenderer.isStructureActive()
                    && structure.getId().equals(MultiblockHologramRenderer.getActiveStructure().getId());

            int cardBg = isActive ? 0xDD0D2619 : 0xDD121F2D;
            extractor.fill(cardLeft, cy, cardRight, cy + cardHeight, cardBg);

            int borderColor = isActive ? 0xFF00E676 : 0xFF00E5FF;
            extractor.fill(cardLeft, cy, cardLeft + 2, cy + cardHeight, borderColor);

            if (!structure.getRequirements().isEmpty()) {
                Block firstBlock = structure.getRequirements().getFirst().expectedBlock();
                extractor.item(firstBlock.asItem().getDefaultInstance(), cardLeft + 8, cy + 15);
            }

            int btnW = 60;
            int btnH = 20;
            int btnX = cardRight - 66;
            int btnY = cy + 13;

            int pdrW = 48;
            int pdrX = btnX - pdrW - 4;

            float maxTitleWidth = Math.max(0, pdrX - (cardLeft + 30) - 8);
            drawScaledText(extractor, Component.translatable(structure.getTitleKey()), cardLeft + 30, cy + 5, maxTitleWidth, isActive ? 0xFF69F0AE : 0xFFFFF176);

            float maxDescWidth = Math.max(0, pdrX - (cardLeft + 30) - 8);
            drawScaledText(extractor, Component.translatable(structure.getDescKey()), cardLeft + 30, cy + 17, maxDescWidth, 0xFFCFD8DC);

            String summary = formatMaterialSummary(structure);
            drawScaledText(extractor, Component.literal(summary), cardLeft + 30, cy + 29, maxDescWidth, 0xFF80DEEA);

            boolean pdrHovered = mouseX >= pdrX && mouseX <= pdrX + pdrW && mouseY >= btnY && mouseY <= btnY + btnH;
            int pdrBg = pdrHovered ? 0xFF00E5FF : 0xFF005B66;
            extractor.fill(pdrX, btnY, pdrX + pdrW, btnY + btnH, pdrBg);
            extractor.fill(pdrX, btnY, pdrX + pdrW, btnY + 1, 0xFF00E5FF);
            extractor.fill(pdrX, btnY + btnH - 1, pdrX + pdrW, btnY + btnH, 0xFF00E5FF);
            extractor.fill(pdrX, btnY, pdrX + 1, btnY + btnH, 0xFF00E5FF);
            extractor.fill(pdrX + pdrW - 1, btnY, pdrX + pdrW, btnY + btnH, 0xFF00E5FF);

            Component pdrText = Component.translatable("gui.sandstorm.datapad.multiblock.ponder_btn");
            drawScaledCenteredText(extractor, pdrText, pdrX + pdrW / 2f, btnY + 6, pdrW - 4, pdrHovered ? 0xFF0A0E17 : 0xFFFFFFFF);

            boolean btnHovered = mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;
            int btnBg = isActive ? (btnHovered ? 0xFFD32F2F : 0xFFC2185B) : (btnHovered ? 0xFF00E5FF : 0xFF0091EA);
            extractor.fill(btnX, btnY, btnX + btnW, btnY + btnH, btnBg);
            extractor.fill(btnX, btnY, btnX + btnW, btnY + 1, 0xFFFFFFFF);
            extractor.fill(btnX, btnY + btnH - 1, btnX + btnW, btnY + btnH, 0xFFFFFFFF);
            extractor.fill(btnX, btnY, btnX + 1, btnY + btnH, 0xFFFFFFFF);
            extractor.fill(btnX + btnW - 1, btnY, btnX + btnW, btnY + btnH, 0xFFFFFFFF);

            Component btnText = isActive
                    ? Component.translatable("gui.sandstorm.datapad.multiblock.clear_btn")
                    : Component.translatable("gui.sandstorm.datapad.multiblock.project_btn");
            drawScaledCenteredText(extractor, btnText, btnX + btnW / 2f, btnY + 6, btnW - 4, btnHovered ? 0xFF0A0E17 : 0xFFFFFFFF);
        }
    }

    private String formatMaterialSummary(MultiblockStructure structure) {
        StringBuilder sb = new StringBuilder();
        sb.append(structure.getTotalBlocks()).append(" blocos: ");
        int count = 0;
        for (Map.Entry<Block, Integer> entry : structure.getMaterialCounts().entrySet()) {
            if (count > 0) {
                sb.append(", ");
            }
            if (count >= 3) {
                sb.append("...");
                break;
            }
            sb.append(entry.getKey().getName().getString()).append(" x").append(entry.getValue());
            count++;
        }
        return sb.toString();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        SandStormMod.LOGGER.info("Datapad mouseClicked: button={}, isLeft={}, x={}, y={}, isDouble={}",
                event.button(), event.isLeft(), event.x(), event.y(), isDouble);
        if (handleInputClick(event.x(), event.y())) {
            clickHandledOnPress = true;
            return true;
        }
        clickHandledOnPress = false;
        return super.mouseClicked(event, isDouble);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        SandStormMod.LOGGER.info("Datapad mouseReleased: button={}, isLeft={}, x={}, y={}",
                event.button(), event.isLeft(), event.x(), event.y());
        if (clickHandledOnPress) {
            clickHandledOnPress = false;
            return true;
        }
        if (handleInputClick(event.x(), event.y())) {
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        SandStormMod.LOGGER.info("Datapad keyPressed: key={}, keycode={}, isConfirmation={}",
                event.key(), event.keycode(), event.isConfirmation());
        if (event.key() == 256) {
            if (ponderStructure != null) {
                ponderStructure = null;
                return true;
            }
        }
        if (ponderStructure != null) {
            if (event.key() == 263) {
                ponderStep = Math.max(1, ponderStep - 1);
                return true;
            }
            if (event.key() == 262) {
                int minY = ponderStructure.getRequirements().stream().mapToInt(r -> r.offset().getY()).min().orElse(0);
                int maxY = ponderStructure.getRequirements().stream().mapToInt(r -> r.offset().getY()).max().orElse(0);
                int totalLayers = Math.max(1, maxY - minY + 1);
                ponderStep = Math.min(totalLayers, ponderStep + 1);
                return true;
            }
        }
        if (event.key() == 82 || event.keycode() == 82) {
            if (claimAvailableQuest()) {
                return true;
            }
        }
        if (event.key() == 72 || event.keycode() == 72) {
            if (MultiblockHologramRenderer.isStructureActive()) {
                MultiblockHologramRenderer.clearActiveStructure();
                if (minecraft != null) {
                    minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BEACON_DEACTIVATE, 1.0f));
                }
                statusTooltip = Component.translatable("gui.sandstorm.datapad.multiblock_cleared");
                statusTooltipExpiry = System.currentTimeMillis() + 3000;
                return true;
            }
        }
        if (event.isConfirmation() || event.key() == 257 || event.key() == 32) {
            if (claimAvailableQuest()) {
                return true;
            }
        }
        if (event.key() >= 49 && event.key() <= 54) {
            currentChapter = event.key() - 48;
            scrollOffset = 0;
            return true;
        }
        return super.keyPressed(event);
    }

    private boolean handleInputClick(double mx, double my) {
        int left = 16;
        int top = 12;
        int right = width - 16;
        int bottom = height - 12;

        if (ponderStructure != null) {
            return handlePonderClick(mx, my, left, top, right, bottom);
        }
        int tabCount = 6;
        int tabWidth = (right - left - 20) / tabCount;
        int ty = top + 36;

        if (my >= ty && my <= ty + 16) {
            for (int ch = 1; ch <= tabCount; ch++) {
                int tx = left + 10 + (ch - 1) * tabWidth;
                if (mx >= tx && mx <= tx + tabWidth) {
                    currentChapter = ch;
                    scrollOffset = 0;
                    return true;
                }
            }
        }

        int questAreaTop = top + 58;
        int questAreaBottom = bottom - 10;
        int cardHeight = 46;
        int cardSpacing = 6;

        if (currentChapter == 6) {
            return handleMultiblockClick(mx, my, left, right, questAreaTop, questAreaBottom, cardHeight, cardSpacing);
        }

        List<QuestData> quests = QuestRegistry.getQuestsForChapter(currentChapter);
        Player player = minecraft != null ? minecraft.player : null;

        for (int i = 0; i < quests.size(); i++) {
            QuestData quest = quests.get(i);
            int cy = questAreaTop + i * (cardHeight + cardSpacing) - scrollOffset;
            if (cy + cardHeight < questAreaTop || cy > questAreaBottom) {
                continue;
            }

            int cardLeft = left + 10;
            int cardRight = right - 10;

            MultiblockStructure associatedStructure = MultiblockRegistry.getStructureForQuest(quest.id());
            if (associatedStructure != null) {
                int holoW = 34;
                int holoH = 18;
                int holoX = cardRight - 60 - holoW - 4;
                int pdrW = 34;
                int pdrX = holoX - pdrW - 4;
                int holoY = cy + 17;
                if (mx >= pdrX && mx <= pdrX + pdrW && my >= holoY && my <= holoY + holoH) {
                    ponderStructure = associatedStructure;
                    ponderStep = 1;
                    if (minecraft != null) {
                        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                    }
                    return true;
                }
                if (mx >= holoX && mx <= holoX + holoW && my >= holoY && my <= holoY + holoH) {
                    projectMultiblock(associatedStructure);
                    return true;
                }
            }

            int btnW = 56;
            int btnH = 20;
            int btnX = cardRight - 60;
            int btnY = cy + 16;

            boolean insideCard = mx >= cardLeft && mx <= cardRight && my >= cy && my <= cy + cardHeight;
            boolean onButton = mx >= btnX && mx <= btnX + btnW && my >= btnY && my <= btnY + btnH;
            boolean wasHovered = quest.id().equals(lastHoveredClaimableQuestId);

            if (onButton || insideCard || wasHovered) {
                if (DatapadClientHelper.isQuestClaimed(quest.id())) {
                    return false;
                }
                return executeClaim(player, quest);
            }
        }
        return false;
    }

    private boolean handleMultiblockClick(double mx, double my, int left, int right,
                                          int questAreaTop, int questAreaBottom, int cardHeight, int cardSpacing) {
        List<MultiblockStructure> structures = new ArrayList<>(MultiblockRegistry.getAllStructures().values());
        for (int i = 0; i < structures.size(); i++) {
            MultiblockStructure structure = structures.get(i);
            int cy = questAreaTop + i * (cardHeight + cardSpacing) - scrollOffset;
            if (cy + cardHeight < questAreaTop || cy > questAreaBottom) {
                continue;
            }

            int cardLeft = left + 10;
            int cardRight = right - 10;
            int btnW = 60;
            int btnH = 20;
            int btnX = cardRight - 66;
            int btnY = cy + 13;
            int pdrW = 48;
            int pdrX = btnX - pdrW - 4;

            boolean onPonder = mx >= pdrX && mx <= pdrX + pdrW && my >= btnY && my <= btnY + btnH;
            if (onPonder) {
                ponderStructure = structure;
                ponderStep = 1;
                if (minecraft != null) {
                    minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                }
                return true;
            }

            boolean onButton = mx >= btnX && mx <= btnX + btnW && my >= btnY && my <= btnY + btnH;
            boolean insideCard = mx >= cardLeft && mx <= cardRight && my >= cy && my <= cy + cardHeight;

            if (onButton || insideCard) {
                boolean isActive = MultiblockHologramRenderer.isStructureActive()
                        && structure.getId().equals(MultiblockHologramRenderer.getActiveStructure().getId());
                if (isActive) {
                    MultiblockHologramRenderer.clearActiveStructure();
                    if (minecraft != null) {
                        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BEACON_DEACTIVATE, 1.0f));
                    }
                    statusTooltip = Component.translatable("gui.sandstorm.datapad.multiblock_cleared");
                    statusTooltipExpiry = System.currentTimeMillis() + 3000;
                } else {
                    projectMultiblock(structure);
                }
                return true;
            }
        }
        return false;
    }

    private void projectMultiblock(MultiblockStructure structure) {
        if (minecraft == null || minecraft.player == null) {
            return;
        }
        LocalPlayer player = minecraft.player;
        BlockPos anchorPos;
        Direction facing = player.getDirection();

        if (minecraft.hitResult instanceof BlockHitResult bhr && bhr.getType() == HitResult.Type.BLOCK) {
            anchorPos = bhr.getBlockPos().above();
        } else {
            anchorPos = player.blockPosition().relative(facing, 3);
        }

        MultiblockHologramRenderer.setActiveStructure(structure, anchorPos, facing);
        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BEACON_ACTIVATE, 1.0f));

        Component nameComp = Component.translatable(structure.getTitleKey());
        player.sendSystemMessage(Component.translatable("gui.sandstorm.datapad.multiblock_projected", nameComp));

        this.onClose();
    }

    private boolean executeClaim(Player player, QuestData quest) {
        if (quest == null || DatapadClientHelper.isQuestClaimed(quest.id())) {
            return false;
        }
        if (isQuestClaimable(player, quest)) {
            SandStormMod.LOGGER.info("Datapad client claiming quest: {}", quest.id());
            DatapadClientHelper.addClaimedQuest(quest.id());
            ClientPlayNetworking.send(new ClaimQuestRewardPayload(quest.id(), new ArrayList<>(DatapadClientHelper.getCompletedConditions())));
            claimFlashTimestamps.put(quest.id(), System.currentTimeMillis());
            lastHoveredClaimableQuestId = null;
            statusTooltip = null;
            if (minecraft != null) {
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            }
            return true;
        } else {
            SandStormMod.LOGGER.info("Datapad quest {} is not claimable. PrereqsMet={}, HasItem={}",
                    quest.id(), arePrerequisitesClaimed(quest), hasRequiredItem(player, quest));
            showStatusTooltip(player, quest);
            if (minecraft != null) {
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 0.5f));
            }
            return true;
        }
    }

    private boolean claimAvailableQuest() {
        Player player = minecraft != null ? minecraft.player : null;
        if (lastHoveredClaimableQuestId != null) {
            QuestData hoveredQuest = QuestRegistry.getQuest(lastHoveredClaimableQuestId);
            if (hoveredQuest != null && isQuestClaimable(player, hoveredQuest)) {
                return executeClaim(player, hoveredQuest);
            }
        }

        List<QuestData> quests = QuestRegistry.getQuestsForChapter(currentChapter);
        for (QuestData quest : quests) {
            if (isQuestClaimable(player, quest)) {
                return executeClaim(player, quest);
            }
        }

        for (QuestData quest : QuestRegistry.getAllQuests().values()) {
            if (isQuestClaimable(player, quest)) {
                currentChapter = quest.chapter();
                scrollOffset = 0;
                return executeClaim(player, quest);
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scrollOffset = Math.clamp(scrollOffset - (int) (verticalAmount * 24), 0, maxScroll);
        return true;
    }

    private boolean isQuestClaimable(Player player, QuestData quest) {
        if (DatapadClientHelper.isQuestClaimed(quest.id())) {
            return false;
        }
        return arePrerequisitesClaimed(quest) && hasRequiredItem(player, quest);
    }

    private boolean hasRequiredItem(Player player, QuestData quest) {
        if (player == null || quest == null) {
            return false;
        }
        if (quest.isConditionBased()) {
            if ("sandstorm.battery_60".equals(quest.conditionTag())) {
                SuitPowerComponent suit = SurvivalHudOverlay.getClientSuit();
                if (suit != null) {
                    long cap = suit.getEnergyStorage().getCapacity();
                    long stored = suit.getEnergyStorage().getStoredEnergy();
                    if (cap > 0 && ((double) stored * 100.0 / (double) cap) >= 60.0) {
                        DatapadClientHelper.addCondition("sandstorm.battery_60");
                        return true;
                    }
                }
            }
            return DatapadClientHelper.isConditionMet(quest.conditionTag())
                    || player.entityTags().contains(quest.conditionTag());
        }
        if (quest.getRequiredItem() == null) {
            return false;
        }
        Item req = quest.getRequiredItem();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = player.getItemBySlot(slot);
            if (!stack.isEmpty() && matchesQuestItem(quest, stack, req)) {
                return true;
            }
        }
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && matchesQuestItem(quest, stack, req)) {
                return true;
            }
        }
        return false;
    }

    public static boolean matchesQuestItem(QuestData quest, ItemStack stack, Item req) {
        return QuestRewardHandler.matchesQuestItem(quest, stack, req);
    }

    private boolean arePrerequisitesClaimed(QuestData quest) {
        if (quest.prerequisiteIds().isEmpty()) {
            return true;
        }
        for (String preId : quest.prerequisiteIds()) {
            if (!DatapadClientHelper.isQuestClaimed(preId)) {
                return false;
            }
        }
        return true;
    }

    private void showStatusTooltip(Player player, QuestData quest) {
        if (quest == null || DatapadClientHelper.isQuestClaimed(quest.id())) {
            return;
        }
        if (!arePrerequisitesClaimed(quest)) {
            statusTooltip = Component.translatable("gui.sandstorm.datapad.tooltip.prereqs_missing");
        } else if (quest.isConditionBased()) {
            statusTooltip = Component.translatable("gui.sandstorm.datapad.tooltip.condition_missing");
        } else if (!hasRequiredItem(player, quest)) {
            statusTooltip = Component.translatable("gui.sandstorm.datapad.tooltip.item_missing");
        } else {
            return;
        }
        statusTooltipExpiry = System.currentTimeMillis() + 3000;
    }

    private void drawScaledText(GuiGraphicsExtractor extractor, Component text, float x, float y, float maxPixelWidth, int color) {
        if (maxPixelWidth <= 0) {
            return;
        }
        int textWidth = font.width(text);
        if (textWidth <= maxPixelWidth) {
            extractor.text(font, text, (int) x, (int) y, color);
        } else {
            float scale = maxPixelWidth / (float) textWidth;
            float offsetY = (9f - 9f * scale) / 2f;
            extractor.pose().pushMatrix();
            extractor.pose().translate(x, y + offsetY);
            extractor.pose().scale(scale, scale);
            extractor.text(font, text, 0, 0, color);
            extractor.pose().popMatrix();
        }
    }

    private void renderPonderView(GuiGraphicsExtractor extractor, int left, int top, int right, int bottom, int mouseX, int mouseY) {
        if (ponderStructure == null) {
            return;
        }
        int minY = ponderStructure.getRequirements().stream().mapToInt(r -> r.offset().getY()).min().orElse(0);
        int maxY = ponderStructure.getRequirements().stream().mapToInt(r -> r.offset().getY()).max().orElse(0);
        int totalLayers = Math.max(1, maxY - minY + 1);
        ponderStep = Math.clamp(ponderStep, 1, totalLayers);
        int currentY = minY + ponderStep - 1;

        extractor.fill(left, top, right, bottom, 0xF5081018);
        extractor.fill(left, top, right, top + 1, 0xFF00E5FF);
        extractor.fill(left, bottom - 1, right, bottom, 0xFF00E5FF);
        extractor.fill(left, top, left + 1, bottom, 0xFF00E5FF);
        extractor.fill(right - 1, top, right, bottom, 0xFF00E5FF);

        drawScaledText(extractor, Component.translatable(ponderStructure.getTitleKey()), left + 16, top + 12, right - left - 120, 0xFF00E5FF);
        String stepInfo = "Camada Y = " + currentY + " (" + ponderStep + " / " + totalLayers + ")";
        drawScaledText(extractor, Component.literal(stepInfo), left + 16, top + 26, right - left - 120, 0xFFFFD54F);

        int closeBtnW = 50;
        int closeBtnH = 18;
        int closeBtnX = right - closeBtnW - 14;
        int closeBtnY = top + 12;
        boolean closeHover = mouseX >= closeBtnX && mouseX <= closeBtnX + closeBtnW && mouseY >= closeBtnY && mouseY <= closeBtnY + closeBtnH;
        extractor.fill(closeBtnX, closeBtnY, closeBtnX + closeBtnW, closeBtnY + closeBtnH, closeHover ? 0xFFD32F2F : 0xAA212121);
        extractor.fill(closeBtnX, closeBtnY, closeBtnX + closeBtnW, closeBtnY + 1, 0xFFFFFFFF);
        extractor.fill(closeBtnX, closeBtnY + closeBtnH - 1, closeBtnX + closeBtnW, closeBtnY + closeBtnH, 0xFFFFFFFF);
        extractor.fill(closeBtnX, closeBtnY, closeBtnX + 1, closeBtnY + closeBtnH, 0xFFFFFFFF);
        extractor.fill(closeBtnX + closeBtnW - 1, closeBtnY, closeBtnX + closeBtnW, closeBtnY + closeBtnH, 0xFFFFFFFF);
        drawScaledCenteredText(extractor, Component.translatable("gui.sandstorm.datapad.multiblock.ponder_close"), closeBtnX + closeBtnW / 2f, closeBtnY + 5, closeBtnW - 4, 0xFFFFFFFF);

        int navY = bottom - 32;
        int prevBtnW = 70;
        int prevBtnH = 20;
        int prevBtnX = left + 16;
        boolean prevHover = mouseX >= prevBtnX && mouseX <= prevBtnX + prevBtnW && mouseY >= navY && mouseY <= navY + prevBtnH;
        extractor.fill(prevBtnX, navY, prevBtnX + prevBtnW, navY + prevBtnH, prevHover ? 0xFF00B0FF : 0xAA102030);
        extractor.fill(prevBtnX, navY, prevBtnX + prevBtnW, navY + 1, 0xFF00E5FF);
        extractor.fill(prevBtnX, navY + prevBtnH - 1, prevBtnX + prevBtnW, navY + prevBtnH, 0xFF00E5FF);
        extractor.fill(prevBtnX, navY, prevBtnX + 1, navY + prevBtnH, 0xFF00E5FF);
        extractor.fill(prevBtnX + prevBtnW - 1, navY, prevBtnX + prevBtnW, navY + prevBtnH, 0xFF00E5FF);
        drawScaledCenteredText(extractor, Component.translatable("gui.sandstorm.datapad.multiblock.ponder_prev"), prevBtnX + prevBtnW / 2f, navY + 6, prevBtnW - 4, 0xFFFFFFFF);

        int nextBtnW = 70;
        int nextBtnH = 20;
        int nextBtnX = prevBtnX + prevBtnW + 8;
        boolean nextHover = mouseX >= nextBtnX && mouseX <= nextBtnX + nextBtnW && mouseY >= navY && mouseY <= navY + nextBtnH;
        extractor.fill(nextBtnX, navY, nextBtnX + nextBtnW, navY + nextBtnH, nextHover ? 0xFF00B0FF : 0xAA102030);
        extractor.fill(nextBtnX, navY, nextBtnX + nextBtnW, navY + 1, 0xFF00E5FF);
        extractor.fill(nextBtnX, navY + nextBtnH - 1, nextBtnX + nextBtnW, navY + nextBtnH, 0xFF00E5FF);
        extractor.fill(nextBtnX, navY, nextBtnX + 1, navY + nextBtnH, 0xFF00E5FF);
        extractor.fill(nextBtnX + nextBtnW - 1, navY, nextBtnX + nextBtnW, navY + nextBtnH, 0xFF00E5FF);
        drawScaledCenteredText(extractor, Component.translatable("gui.sandstorm.datapad.multiblock.ponder_next"), nextBtnX + nextBtnW / 2f, navY + 6, nextBtnW - 4, 0xFFFFFFFF);

        int projBtnW = 86;
        int projBtnH = 20;
        int projBtnX = right - projBtnW - 16;
        boolean projHover = mouseX >= projBtnX && mouseX <= projBtnX + projBtnW && mouseY >= navY && mouseY <= navY + projBtnH;
        extractor.fill(projBtnX, navY, projBtnX + projBtnW, navY + projBtnH, projHover ? 0xFF00E676 : 0xAA005B66);
        extractor.fill(projBtnX, navY, projBtnX + projBtnW, navY + 1, 0xFF00E676);
        extractor.fill(projBtnX, navY + projBtnH - 1, projBtnX + projBtnW, navY + projBtnH, 0xFF00E676);
        extractor.fill(projBtnX, navY, projBtnX + 1, navY + projBtnH, 0xFF00E676);
        extractor.fill(projBtnX + projBtnW - 1, navY, projBtnX + projBtnW, navY + projBtnH, 0xFF00E676);
        drawScaledCenteredText(extractor, Component.translatable("gui.sandstorm.datapad.multiblock.ponder_project"), projBtnX + projBtnW / 2f, navY + 6, projBtnW - 4, 0xFFFFFFFF);

        int gridCenterX = (left + right) / 2;
        int gridCenterY = (top + bottom) / 2 - 10;
        MultiblockBlockRequirement hoveredReq = null;

        for (MultiblockBlockRequirement req : ponderStructure.getRequirements()) {
            if (req.offset().getY() == currentY) {
                int cellX = gridCenterX + req.offset().getX() * 30;
                int cellY = gridCenterY + req.offset().getZ() * 30;
                boolean cellHover = mouseX >= cellX - 13 && mouseX <= cellX + 13 && mouseY >= cellY - 13 && mouseY <= cellY + 13;
                extractor.fill(cellX - 13, cellY - 13, cellX + 13, cellY + 13, cellHover ? 0xEE1E3A5F : 0x880D1B2A);
                int borderColor = cellHover ? 0xFF00E5FF : (0xFF000000 | req.colorRgb());
                extractor.fill(cellX - 13, cellY - 13, cellX + 13, cellY - 12, borderColor);
                extractor.fill(cellX - 13, cellY + 12, cellX + 13, cellY + 13, borderColor);
                extractor.fill(cellX - 13, cellY - 13, cellX - 12, cellY + 13, borderColor);
                extractor.fill(cellX + 12, cellY - 13, cellX + 13, cellY + 13, borderColor);
                extractor.item(req.expectedBlock().asItem().getDefaultInstance(), cellX - 8, cellY - 8);
                if (cellHover) {
                    hoveredReq = req;
                }
            }
        }

        if (hoveredReq != null) {
            String blockName = hoveredReq.expectedBlock().getName().getString();
            String role = hoveredReq.roleLabel();
            String coord = "(" + hoveredReq.offset().getX() + ", " + hoveredReq.offset().getY() + ", " + hoveredReq.offset().getZ() + ")";
            String tip = blockName + " [" + role + "] " + coord;
            drawScaledCenteredText(extractor, Component.literal(tip), gridCenterX, bottom - 46, right - left - 40, 0xFF80DEEA);
        } else {
            drawScaledCenteredText(extractor, Component.translatable("gui.sandstorm.datapad.multiblock.ponder_hint"), gridCenterX, bottom - 46, right - left - 40, 0xFF90A4AE);
        }
    }

    private boolean handlePonderClick(double mx, double my, int left, int top, int right, int bottom) {
        if (ponderStructure == null) {
            return false;
        }
        int minY = ponderStructure.getRequirements().stream().mapToInt(r -> r.offset().getY()).min().orElse(0);
        int maxY = ponderStructure.getRequirements().stream().mapToInt(r -> r.offset().getY()).max().orElse(0);
        int totalLayers = Math.max(1, maxY - minY + 1);

        int closeBtnW = 50;
        int closeBtnH = 18;
        int closeBtnX = right - closeBtnW - 14;
        int closeBtnY = top + 12;
        if (mx >= closeBtnX && mx <= closeBtnX + closeBtnW && my >= closeBtnY && my <= closeBtnY + closeBtnH) {
            ponderStructure = null;
            if (minecraft != null) {
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 0.8f));
            }
            return true;
        }

        int navY = bottom - 32;
        int prevBtnW = 70;
        int prevBtnH = 20;
        int prevBtnX = left + 16;
        if (mx >= prevBtnX && mx <= prevBtnX + prevBtnW && my >= navY && my <= navY + prevBtnH) {
            ponderStep = Math.max(1, ponderStep - 1);
            if (minecraft != null) {
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            }
            return true;
        }

        int nextBtnW = 70;
        int nextBtnH = 20;
        int nextBtnX = prevBtnX + prevBtnW + 8;
        if (mx >= nextBtnX && mx <= nextBtnX + nextBtnW && my >= navY && my <= navY + nextBtnH) {
            ponderStep = Math.min(totalLayers, ponderStep + 1);
            if (minecraft != null) {
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            }
            return true;
        }

        int projBtnW = 86;
        int projBtnH = 20;
        int projBtnX = right - projBtnW - 16;
        if (mx >= projBtnX && mx <= projBtnX + projBtnW && my >= navY && my <= navY + projBtnH) {
            MultiblockStructure toProject = ponderStructure;
            ponderStructure = null;
            projectMultiblock(toProject);
            return true;
        }

        return true;
    }

    private void drawScaledCenteredText(GuiGraphicsExtractor extractor, Component text, float centerX, float y, float maxPixelWidth, int color) {
        if (maxPixelWidth <= 0) {
            return;
        }
        int textWidth = font.width(text);
        if (textWidth <= maxPixelWidth) {
            extractor.centeredText(font, text, (int) centerX, (int) y, color);
        } else {
            float scale = maxPixelWidth / (float) textWidth;
            float offsetY = (9f - 9f * scale) / 2f;
            extractor.pose().pushMatrix();
            extractor.pose().translate(centerX - (maxPixelWidth / 2f), y + offsetY);
            extractor.pose().scale(scale, scale);
            extractor.text(font, text, 0, 0, color);
            extractor.pose().popMatrix();
        }
    }

}
