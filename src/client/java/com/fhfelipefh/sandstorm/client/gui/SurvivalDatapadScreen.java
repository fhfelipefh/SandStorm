package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.network.ClaimQuestRewardPayload;
import com.fhfelipefh.sandstorm.content.quest.QuestData;
import com.fhfelipefh.sandstorm.content.quest.QuestRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class SurvivalDatapadScreen extends Screen {
    private int currentChapter = 1;
    private int scrollOffset = 0;
    private int maxScroll = 0;

    public SurvivalDatapadScreen() {
        super(Component.translatable("gui.sandstorm.datapad.title"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        extractor.fill(0, 0, width, height, 0xD00A0E17);

        int left = 16;
        int top = 12;
        int right = width - 16;
        int bottom = height - 12;

        extractor.fill(left, top, right, bottom, 0xEE101824);
        extractor.fill(left, top, right, top + 1, 0xFF00E5FF);
        extractor.fill(left, bottom - 1, right, bottom, 0xFF00E5FF);
        extractor.fill(left, top, left + 1, bottom, 0xFF00E5FF);
        extractor.fill(right - 1, top, right, bottom, 0xFF00E5FF);

        Component titleComp = Component.literal("I.A.T.I. OS // TERMINAL DE TERRAFORMAÇÃO // ARRAKIS-IX");
        Component statusComp = Component.literal("ONLINE // CONEXAO ORBITAL ESTAVEL");
        int statusWidth = font.width(statusComp);
        int statusX = right - statusWidth - 10;
        float maxHeaderTitleWidth = statusX - (left + 10) - 12;

        if (maxHeaderTitleWidth < 140) {
            float halfWidth = (right - left - 24) / 2f;
            drawScaledText(extractor, titleComp, left + 10, top + 8, halfWidth, 0xFF00E5FF);
            drawScaledText(extractor, statusComp, left + 10 + halfWidth + 4, top + 8, halfWidth, 0xFF76FF03);
        } else {
            drawScaledText(extractor, titleComp, left + 10, top + 8, maxHeaderTitleWidth, 0xFF00E5FF);
            extractor.text(font, statusComp, statusX, top + 8, 0xFF76FF03);
        }

        Component chapterComp = Component.translatable("gui.sandstorm.datapad.chapter." + currentChapter);
        drawScaledText(extractor, chapterComp, left + 10, top + 20, right - left - 20, 0xFFFFD54F);

        extractor.fill(left + 6, top + 32, right - 6, top + 33, 0x5500E5FF);

        int tabCount = 5;
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

        List<QuestData> quests = QuestRegistry.getQuestsForChapter(currentChapter);
        int totalContentHeight = quests.size() * (cardHeight + cardSpacing);
        int visibleHeight = questAreaBottom - questAreaTop;
        maxScroll = Math.max(0, totalContentHeight - visibleHeight);
        scrollOffset = Math.clamp(scrollOffset, 0, maxScroll);

        Player player = minecraft != null ? minecraft.player : null;

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

            int cardLeft = left + 10;
            int cardRight = right - 10;

            int cardBg = isClaimed ? 0xDD0D2619 : (isClaimable ? 0xDD1B2A38 : (isInProgress ? 0xDD121F2D : 0xBB181820));
            extractor.fill(cardLeft, cy, cardRight, cy + cardHeight, cardBg);

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
            int badgeWidth = font.width(badge);
            int badgeX = cardRight - badgeWidth - 8;
            extractor.text(font, badge, badgeX, cy + 5, badgeColor);

            int titleColor = isClaimed ? 0xFF69F0AE : (isClaimable ? 0xFFFFF176 : (isInProgress ? 0xFFE0F7FA : 0xFFB0BEC5));
            float maxTitleWidth = badgeX - (cardLeft + 30) - 8;
            drawScaledText(extractor, Component.translatable(quest.titleKey()), cardLeft + 30, cy + 5, maxTitleWidth, titleColor);

            float maxLineWidth = (cardRight - 90) - (cardLeft + 30) - 4;
            drawScaledText(extractor, Component.translatable(quest.taskKey()), cardLeft + 30, cy + 17, maxLineWidth, 0xFFCFD8DC);
            drawScaledText(extractor, Component.translatable(quest.noteKey()), cardLeft + 30, cy + 29, maxLineWidth, 0xFF80DEEA);

            if (quest.getRewardItem() != null) {
                extractor.item(quest.getRewardItem().getDefaultInstance(), cardRight - 82, cy + 19);
                String countStr = "x" + quest.rewardCount();
                extractor.text(font, countStr, cardRight - 64, cy + 23, 0xFFFFD54F);
            }

            int btnX = cardRight - 46;
            int btnY = cy + 19;
            int btnW = 40;
            int btnH = 16;

            if (isClaimable) {
                boolean hovered = mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;
                int btnBg = hovered ? 0xFF00E5FF : 0xFF0091EA;
                extractor.fill(btnX, btnY, btnX + btnW, btnY + btnH, btnBg);
                extractor.fill(btnX, btnY, btnX + btnW, btnY + 1, hovered ? 0xFFFFFFFF : 0xFF80D8FF);
                extractor.fill(btnX, btnY + btnH - 1, btnX + btnW, btnY + btnH, hovered ? 0xFFFFFFFF : 0xFF80D8FF);
                extractor.fill(btnX, btnY, btnX + 1, btnY + btnH, hovered ? 0xFFFFFFFF : 0xFF80D8FF);
                extractor.fill(btnX + btnW - 1, btnY, btnX + btnW, btnY + btnH, hovered ? 0xFFFFFFFF : 0xFF80D8FF);

                Component claimText = Component.translatable("gui.sandstorm.datapad.claim");
                drawScaledCenteredText(extractor, claimText, btnX + btnW / 2f, btnY + 4, btnW - 4, hovered ? 0xFF0A0E17 : 0xFFFFFFFF);
            } else if (isClaimed) {
                Component claimedText = Component.translatable("gui.sandstorm.datapad.claimed");
                drawScaledCenteredText(extractor, claimedText, btnX + btnW / 2f, btnY + 4, btnW - 2, 0xFF69F0AE);
            }
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        if (event.button() == 0) {
            double mx = event.x();
            double my = event.y();

            int left = 16;
            int top = 12;
            int right = width - 16;
            int bottom = height - 12;
            int tabCount = 5;
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

            List<QuestData> quests = QuestRegistry.getQuestsForChapter(currentChapter);
            Player player = minecraft != null ? minecraft.player : null;

            for (int i = 0; i < quests.size(); i++) {
                QuestData quest = quests.get(i);
                int cy = questAreaTop + i * (cardHeight + cardSpacing) - scrollOffset;
                if (cy + cardHeight < questAreaTop || cy > questAreaBottom) {
                    continue;
                }

                int cardRight = right - 10;
                int btnX = cardRight - 46;
                int btnY = cy + 19;
                int btnW = 40;
                int btnH = 16;

                if (mx >= btnX && mx <= btnX + btnW && my >= btnY && my <= btnY + btnH) {
                    if (isQuestClaimable(player, quest)) {
                        ClientPlayNetworking.send(new ClaimQuestRewardPayload(quest.id()));
                        if (minecraft != null) {
                            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                        }
                        return true;
                    }
                }
            }
        }
        return super.mouseClicked(event, isDouble);
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
        if (player == null || quest == null || quest.getRequiredItem() == null) {
            return false;
        }
        Item req = quest.getRequiredItem();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = player.getItemBySlot(slot);
            if (!stack.isEmpty() && stack.is(req)) {
                return true;
            }
        }
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.is(req)) {
                return true;
            }
        }
        return false;
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

    private void drawScaledText(GuiGraphicsExtractor extractor, Component text, float x, float y, float maxPixelWidth, int color) {
        int textWidth = font.width(text);
        if (textWidth <= maxPixelWidth || maxPixelWidth <= 0) {
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

    private void drawScaledCenteredText(GuiGraphicsExtractor extractor, Component text, float centerX, float y, float maxPixelWidth, int color) {
        int textWidth = font.width(text);
        if (textWidth <= maxPixelWidth || maxPixelWidth <= 0) {
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
