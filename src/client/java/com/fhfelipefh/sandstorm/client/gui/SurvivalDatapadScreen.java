package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.quest.QuestData;
import com.fhfelipefh.sandstorm.content.quest.QuestRegistry;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

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

        extractor.text(font, Component.literal("I.A.T.I. OS // TERMINAL DE TERRAFORMAÇÃO // ARRAKIS-IX"), left + 10, top + 8, 0x00E5FF);
        extractor.text(font, Component.translatable("gui.sandstorm.datapad.chapter." + currentChapter), left + 10, top + 20, 0xFFD54F);
        extractor.text(font, Component.literal("ONLINE // CONEXAO ORBITAL ESTAVEL"), right - 220, top + 8, 0x76FF03);

        extractor.fill(left + 6, top + 32, right - 6, top + 33, 0x5500E5FF);

        int tabCount = 5;
        int tabWidth = (right - left - 20) / tabCount;
        for (int ch = 1; ch <= tabCount; ch++) {
            int tx = left + 10 + (ch - 1) * tabWidth;
            int ty = top + 36;
            boolean isSelected = (ch == currentChapter);

            extractor.fill(tx + 1, ty, tx + tabWidth - 2, ty + 16, isSelected ? 0xFF005B66 : 0x88152233);
            extractor.fill(tx + 1, ty + 15, tx + tabWidth - 2, ty + 16, isSelected ? 0xFF00E5FF : 0x4400E5FF);
            extractor.centeredText(font, Component.translatable("gui.sandstorm.datapad.tab." + ch), tx + tabWidth / 2, ty + 4, isSelected ? 0xFFFFFF : 0x90A4AE);
        }

        int questAreaTop = top + 58;
        int questAreaBottom = bottom - 10;
        int cardHeight = 44;
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

            boolean completed = isQuestCompleted(player, quest.id());
            boolean available = !completed && isQuestAvailable(player, quest);

            int cardLeft = left + 10;
            int cardRight = right - 10;

            int cardBg = completed ? 0xDD0D2619 : (available ? 0xDD121F2D : 0xBB181820);
            extractor.fill(cardLeft, cy, cardRight, cy + cardHeight, cardBg);

            int borderColor = completed ? 0xFF00E676 : (available ? 0xFF00B0FF : 0xFF546E7A);
            extractor.fill(cardLeft, cy, cardLeft + 2, cy + cardHeight, borderColor);

            extractor.item(quest.getIconItem().getDefaultInstance(), cardLeft + 8, cy + 14);

            int titleColor = completed ? 0x69F0AE : (available ? 0xE0F7FA : 0xB0BEC5);
            extractor.text(font, Component.translatable(quest.titleKey()), cardLeft + 32, cy + 5, titleColor);

            Component badge = completed ? Component.translatable("gui.sandstorm.datapad.status.completed")
                    : (available ? Component.translatable("gui.sandstorm.datapad.status.available")
                    : Component.translatable("gui.sandstorm.datapad.status.locked"));
            int badgeColor = completed ? 0x69F0AE : (available ? 0xFFD54F : 0x78909C);
            extractor.text(font, badge, cardRight - font.width(badge) - 8, cy + 5, badgeColor);

            extractor.text(font, Component.translatable(quest.taskKey()), cardLeft + 32, cy + 18, 0xCFD8DC);
            extractor.text(font, Component.translatable(quest.noteKey()), cardLeft + 32, cy + 29, 0x80DEEA);
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
        }
        return super.mouseClicked(event, isDouble);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scrollOffset = Math.clamp(scrollOffset - (int) (verticalAmount * 24), 0, maxScroll);
        return true;
    }

    private boolean isQuestCompleted(Player player, String questId) {
        if (player == null) {
            return false;
        }
        QuestData data = QuestRegistry.getQuest(questId);
        if (data == null || data.getRequiredItem() == null) {
            return false;
        }
        return player.getInventory().contains(data.getRequiredItem().getDefaultInstance());
    }

    private boolean isQuestAvailable(Player player, QuestData quest) {
        if (quest.prerequisiteIds().isEmpty()) {
            return true;
        }
        for (String preId : quest.prerequisiteIds()) {
            if (!isQuestCompleted(player, preId)) {
                return false;
            }
        }
        return true;
    }
}
