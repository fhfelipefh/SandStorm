package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.block.entity.AutonomousSonicTurretBlockEntity;
import com.fhfelipefh.sandstorm.content.gui.AutonomousSonicTurretMenu;
import com.fhfelipefh.sandstorm.content.network.ConfigureTurretPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class AutonomousSonicTurretScreen extends AbstractContainerScreen<AutonomousSonicTurretMenu> {
    public record EntityEntry(String id, Component displayName, MobCategory category) {
    }

    private record ControlsLayout(int modeX, int modeW, int stratX, int stratW, int hostilesX, int hostilesW, int clearX, int clearW) {
    }

    private final List<EntityEntry> allEntities = new ArrayList<>();
    private final List<EntityEntry> filteredEntities = new ArrayList<>();
    private final Set<String> selectedTargets = new HashSet<>();
    private int filterMode = 0;
    private int targetingStrategy = 0;
    private int scrollOffset = 0;
    private int maxScroll = 0;
    private EditBox searchBox;

    public AutonomousSonicTurretScreen(AutonomousSonicTurretMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = 0;
        this.topPos = 0;

        this.filterMode = menu.getFilterMode();
        this.targetingStrategy = menu.getTargetingStrategy();

        loadTurretEntityData();
        buildEntityCatalog();

        int searchW = Math.max(90, Math.min(130, width / 4));
        int searchX = width - searchW - 20;
        int searchY = 38;
        this.searchBox = new EditBox(this.font, searchX, searchY, searchW, 16, Component.literal("Search"));
        this.searchBox.setHint(Component.translatable("gui.sandstorm.turret.search_hint"));
        this.searchBox.setResponder(this::onSearchChanged);
        this.addRenderableWidget(this.searchBox);

        updateFilteredList("");
    }

    private ControlsLayout getControlsLayout() {
        int searchW = Math.max(90, Math.min(130, width / 4));
        int searchX = width - searchW - 20;
        int availW = searchX - 6 - 20;
        int btnSpacing = 4;
        int usableW = Math.max(100, availW - btnSpacing * 3);

        int modeW = (int) (usableW * 0.36);
        int stratW = (int) (usableW * 0.36);
        int hostilesW = (int) (usableW * 0.15);
        int clearW = usableW - modeW - stratW - hostilesW;

        int modeX = 20;
        int stratX = modeX + modeW + btnSpacing;
        int hostilesX = stratX + stratW + btnSpacing;
        int clearX = hostilesX + hostilesW + btnSpacing;

        return new ControlsLayout(modeX, modeW, stratX, stratW, hostilesX, hostilesW, clearX, clearW);
    }

    private void loadTurretEntityData() {
        if (minecraft != null && minecraft.level != null) {
            if (minecraft.level.getBlockEntity(menu.getTurretPos()) instanceof AutonomousSonicTurretBlockEntity be) {
                selectedTargets.clear();
                selectedTargets.addAll(be.getTargetEntityIds());
                this.filterMode = be.getFilterMode();
                this.targetingStrategy = be.getTargetingStrategy();
            }
        }
    }

    private void buildEntityCatalog() {
        allEntities.clear();
        for (Identifier id : BuiltInRegistries.ENTITY_TYPE.keySet()) {
            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getValue(id);
            if (type.getCategory() != MobCategory.MISC) {
                allEntities.add(new EntityEntry(id.toString(), type.getDescription(), type.getCategory()));
            }
        }
        allEntities.sort(Comparator.comparing(e -> e.displayName().getString().toLowerCase(Locale.ROOT)));
    }

    private void onSearchChanged(String query) {
        updateFilteredList(query);
    }

    private void updateFilteredList(String query) {
        filteredEntities.clear();
        String q = query.trim().toLowerCase(Locale.ROOT);
        for (EntityEntry entry : allEntities) {
            if (q.isEmpty()
                    || entry.displayName().getString().toLowerCase(Locale.ROOT).contains(q)
                    || entry.id().toLowerCase(Locale.ROOT).contains(q)) {
                filteredEntities.add(entry);
            }
        }
        recalculateScroll();
    }

    private void recalculateScroll() {
        int cols = Math.max(1, (width - 40) / 190);
        int rows = (int) Math.ceil((double) filteredEntities.size() / cols);
        int contentHeight = rows * 44;
        int viewHeight = height - 120;
        this.maxScroll = Math.max(0, contentHeight - viewHeight);
        this.scrollOffset = Math.clamp(this.scrollOffset, 0, maxScroll);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (maxScroll > 0) {
            scrollOffset = Math.clamp(scrollOffset - (int) (verticalAmount * 28), 0, maxScroll);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        double mx = event.x();
        double my = event.y();

        if (handleControlsClick(mx, my)) {
            return true;
        }

        if (handleCardClick(mx, my)) {
            return true;
        }

        return super.mouseClicked(event, isDouble);
    }

    private boolean handleControlsClick(double mx, double my) {
        int top = 38;
        if (my < top || my > top + 16) {
            return false;
        }

        ControlsLayout cl = getControlsLayout();

        if (mx >= cl.modeX && mx <= cl.modeX + cl.modeW) {
            filterMode = (filterMode == 0) ? 1 : 0;
            sendConfig();
            playClickSound();
            return true;
        }

        if (mx >= cl.stratX && mx <= cl.stratX + cl.stratW) {
            targetingStrategy = (targetingStrategy + 1) % 3;
            sendConfig();
            playClickSound();
            return true;
        }

        if (mx >= cl.hostilesX && mx <= cl.hostilesX + cl.hostilesW) {
            selectHostilesOnly();
            sendConfig();
            playClickSound();
            return true;
        }

        if (mx >= cl.clearX && mx <= cl.clearX + cl.clearW) {
            selectedTargets.clear();
            sendConfig();
            playClickSound();
            return true;
        }

        return false;
    }

    private void selectHostilesOnly() {
        selectedTargets.clear();
        for (EntityEntry entry : allEntities) {
            if (entry.category() == MobCategory.MONSTER) {
                selectedTargets.add(entry.id());
            }
        }
    }

    private boolean handleCardClick(double mx, double my) {
        int listTop = 62;
        int listBottom = height - 44;
        if (my < listTop || my > listBottom) {
            return false;
        }

        int cols = Math.max(1, (width - 40) / 190);
        int colWidth = (width - 40 - (cols - 1) * 8) / cols;
        int cardH = 38;

        for (int i = 0; i < filteredEntities.size(); i++) {
            int col = i % cols;
            int row = i / cols;
            int cx = 20 + col * (colWidth + 8);
            int cy = listTop + row * (cardH + 6) - scrollOffset;

            if (cy + cardH < listTop || cy > listBottom) {
                continue;
            }

            if (mx >= cx && mx <= cx + colWidth && my >= cy && my <= cy + cardH) {
                EntityEntry entry = filteredEntities.get(i);
                if (selectedTargets.contains(entry.id())) {
                    selectedTargets.remove(entry.id());
                } else {
                    selectedTargets.add(entry.id());
                }
                sendConfig();
                playClickSound();
                return true;
            }
        }
        return false;
    }

    private void playClickSound() {
        if (minecraft != null) {
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
    }

    private void sendConfig() {
        ClientPlayNetworking.send(new ConfigureTurretPayload(
                menu.getTurretPos(),
                filterMode,
                targetingStrategy,
                new ArrayList<>(selectedTargets)
        ));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        extractor.fill(0, 0, width, height, 0xF00A0E17);

        int left = 12;
        int top = 8;
        int right = width - 12;
        int bottom = height - 8;

        extractor.fill(left, top, right, bottom, 0xEE101824);
        extractor.fill(left, top, right, top + 1, 0xFF00E5FF);
        extractor.fill(left, bottom - 1, right, bottom, 0xFF00E5FF);
        extractor.fill(left, top, left + 1, bottom, 0xFF00E5FF);
        extractor.fill(right - 1, top, right, bottom, 0xFF00E5FF);

        Component title = Component.literal("TORRETA SÔNICA AUTOMATIZADA // MATRIZ DE I.A.");
        extractor.text(font, title, 20, 14, 0xFF00E5FF);

        int energy = menu.getEnergy();
        int maxEnergy = menu.getMaxEnergy();
        String energyStr = energy + " / " + maxEnergy + " J";
        Component energyComp = Component.literal(energyStr);
        int energyW = font.width(energyComp);
        extractor.text(font, energyComp, width - energyW - 20, 14, 0xFF00E5FF);

        boolean wpt = menu.isWptConnected();
        Component wptComp = wpt ? Component.literal("WPT CONECTADO") : Component.literal("SEM WPT");
        int wptColor = wpt ? 0xFF76FF03 : 0xFFFF9100;
        int wptW = font.width(wptComp);
        extractor.text(font, wptComp, width - energyW - wptW - 32, 14, wptColor, false);

        int btnTop = 38;
        ControlsLayout cl = getControlsLayout();

        int modeColor = (filterMode == 0) ? 0xFF00E5FF : 0xFFFFAB40;
        String modeText = (filterMode == 0) ? "MODO: ALVOS (WHITELIST)" : "MODO: IGNORADOS (BLACKLIST)";
        extractor.fill(cl.modeX, btnTop, cl.modeX + cl.modeW, btnTop + 16, 0x44000000);
        extractor.fill(cl.modeX, btnTop, cl.modeX + cl.modeW, btnTop + 1, modeColor);
        extractor.fill(cl.modeX, btnTop + 15, cl.modeX + cl.modeW, btnTop + 16, modeColor);
        extractor.fill(cl.modeX, btnTop, cl.modeX + 1, btnTop + 16, modeColor);
        extractor.fill(cl.modeX + cl.modeW - 1, btnTop, cl.modeX + cl.modeW, btnTop + 16, modeColor);
        drawScaledCenteredText(extractor, Component.literal(modeText), cl.modeX + cl.modeW / 2f, btnTop + 4, cl.modeW - 6, modeColor);

        String stratText = switch (targetingStrategy) {
            case 1 -> "FOCO: MENOR VIDA";
            case 2 -> "FOCO: MAIOR AMEAÇA";
            default -> "FOCO: MAIS PRÓXIMO";
        };
        extractor.fill(cl.stratX, btnTop, cl.stratX + cl.stratW, btnTop + 16, 0x44000000);
        extractor.fill(cl.stratX, btnTop, cl.stratX + cl.stratW, btnTop + 1, 0xFF81D4FA);
        extractor.fill(cl.stratX, btnTop + 15, cl.stratX + cl.stratW, btnTop + 16, 0xFF81D4FA);
        extractor.fill(cl.stratX, btnTop, cl.stratX + 1, btnTop + 16, 0xFF81D4FA);
        extractor.fill(cl.stratX + cl.stratW - 1, btnTop, cl.stratX + cl.stratW, btnTop + 16, 0xFF81D4FA);
        drawScaledCenteredText(extractor, Component.literal(stratText), cl.stratX + cl.stratW / 2f, btnTop + 4, cl.stratW - 6, 0xFF81D4FA);

        extractor.fill(cl.hostilesX, btnTop, cl.hostilesX + cl.hostilesW, btnTop + 16, 0x44000000);
        extractor.fill(cl.hostilesX, btnTop, cl.hostilesX + cl.hostilesW, btnTop + 1, 0xFFFF7043);
        extractor.fill(cl.hostilesX, btnTop + 15, cl.hostilesX + cl.hostilesW, btnTop + 16, 0xFFFF7043);
        extractor.fill(cl.hostilesX, btnTop, cl.hostilesX + 1, btnTop + 16, 0xFFFF7043);
        extractor.fill(cl.hostilesX + cl.hostilesW - 1, btnTop, cl.hostilesX + cl.hostilesW, btnTop + 16, 0xFFFF7043);
        drawScaledCenteredText(extractor, Component.literal("MONSTROS"), cl.hostilesX + cl.hostilesW / 2f, btnTop + 4, cl.hostilesW - 4, 0xFFFF7043);

        extractor.fill(cl.clearX, btnTop, cl.clearX + cl.clearW, btnTop + 16, 0x44000000);
        extractor.fill(cl.clearX, btnTop, cl.clearX + cl.clearW, btnTop + 1, 0xFFB0BEC5);
        extractor.fill(cl.clearX, btnTop + 15, cl.clearX + cl.clearW, btnTop + 16, 0xFFB0BEC5);
        extractor.fill(cl.clearX, btnTop, cl.clearX + 1, btnTop + 16, 0xFFB0BEC5);
        extractor.fill(cl.clearX + cl.clearW - 1, btnTop, cl.clearX + cl.clearW, btnTop + 16, 0xFFB0BEC5);
        drawScaledCenteredText(extractor, Component.literal("LIMPAR"), cl.clearX + cl.clearW / 2f, btnTop + 4, cl.clearW - 4, 0xFFB0BEC5);

        renderCardsGrid(extractor, mouseX, mouseY);
        renderFooter(extractor);

        super.extractRenderState(extractor, mouseX, mouseY, delta);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
    }

    private void renderCardsGrid(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int listTop = 62;
        int listBottom = height - 44;
        int cols = Math.max(1, (width - 40) / 190);
        int colWidth = (width - 40 - (cols - 1) * 8) / cols;
        int cardH = 38;

        extractor.enableScissor(12, listTop, width - 12, listBottom);

        for (int i = 0; i < filteredEntities.size(); i++) {
            int col = i % cols;
            int row = i / cols;
            int cx = 20 + col * (colWidth + 8);
            int cy = listTop + row * (cardH + 6) - scrollOffset;

            if (cy + cardH < listTop || cy > listBottom) {
                continue;
            }

            EntityEntry entry = filteredEntities.get(i);
            boolean isSelected = selectedTargets.contains(entry.id());
            boolean hovered = mouseX >= cx && mouseX <= cx + colWidth && mouseY >= cy && mouseY <= cy + cardH;

            int bg = hovered ? 0xEE1E293B : 0xEE141C28;
            extractor.fill(cx, cy, cx + colWidth, cy + cardH, bg);

            int borderColor = isSelected ? 0xFF00E5FF : 0xFF2A394D;
            if (hovered && !isSelected) {
                borderColor = 0xFF546E7A;
            }
            extractor.fill(cx, cy, cx + colWidth, cy + 1, borderColor);
            extractor.fill(cx, cy + cardH - 1, cx + colWidth, cy + cardH, borderColor);
            extractor.fill(cx, cy, cx + 1, cy + cardH, borderColor);
            extractor.fill(cx + colWidth - 1, cy, cx + colWidth, cy + cardH, borderColor);

            int catColor = switch (entry.category()) {
                case MONSTER -> 0xFFFF5252;
                case CREATURE -> 0xFF69F0AE;
                case AMBIENT -> 0xFFFFD740;
                default -> 0xFF40C4FF;
            };
            extractor.fill(cx + 4, cy + 4, cx + 6, cy + cardH - 4, catColor);

            int maxTextW = colWidth - 76;
            drawScaledText(extractor, entry.displayName(), cx + 10, cy + 6, maxTextW, 0xFFFFFFFF);
            drawScaledText(extractor, Component.literal(entry.id()), cx + 10, cy + 20, maxTextW, 0xFF78909C);

            int badgeW = 58;
            int badgeH = 18;
            int badgeX = cx + colWidth - badgeW - 6;
            int badgeY = cy + 10;
            if (isSelected) {
                extractor.fill(badgeX, badgeY, badgeX + badgeW, badgeY + badgeH, 0x4400E5FF);
                extractor.fill(badgeX, badgeY, badgeX + badgeW, badgeY + 1, 0xFF00E5FF);
                extractor.fill(badgeX, badgeY + badgeH - 1, badgeX + badgeW, badgeY + badgeH, 0xFF00E5FF);
                extractor.fill(badgeX, badgeY, badgeX + 1, badgeY + badgeH, 0xFF00E5FF);
                extractor.fill(badgeX + badgeW - 1, badgeY, badgeX + badgeW, badgeY + badgeH, 0xFF00E5FF);
                drawScaledCenteredText(extractor, Component.literal("ALVO"), badgeX + badgeW / 2f, badgeY + 5, badgeW - 4, 0xFF00E5FF);
            } else {
                extractor.fill(badgeX, badgeY, badgeX + badgeW, badgeY + badgeH, 0x22000000);
                extractor.fill(badgeX, badgeY, badgeX + badgeW, badgeY + 1, 0xFF37474F);
                extractor.fill(badgeX, badgeY + badgeH - 1, badgeX + badgeW, badgeY + badgeH, 0xFF37474F);
                extractor.fill(badgeX, badgeY, badgeX + 1, badgeY + badgeH, 0xFF37474F);
                extractor.fill(badgeX + badgeW - 1, badgeY, badgeX + badgeW, badgeY + badgeH, 0xFF37474F);
                drawScaledCenteredText(extractor, Component.literal("IGNORAR"), badgeX + badgeW / 2f, badgeY + 5, badgeW - 4, 0xFF78909C);
            }
        }

        extractor.disableScissor();
    }

    private void renderFooter(GuiGraphicsExtractor extractor) {
        int barY = height - 36;
        extractor.fill(12, barY - 4, width - 12, height - 8, 0xFA101824);
        extractor.fill(20, barY, width - 20, barY + 1, 0x5500E5FF);

        String stats = "Alvos: " + selectedTargets.size() + " / " + allEntities.size();
        drawScaledText(extractor, Component.literal(stats), 24, barY + 8, (width - 60) / 2f, 0xFF90A4AE);

        Component rangeComp = Component.literal("Alcance: 20m | 400 J/disp");
        int rw = font.width(rangeComp);
        extractor.text(font, rangeComp, width - rw - 24, barY + 8, 0xFF00E5FF, false);
    }

    private void drawScaledText(GuiGraphicsExtractor extractor, Component text, float x, float y, float maxPixelWidth, int color) {
        int textWidth = this.font.width(text);
        if (textWidth <= maxPixelWidth || maxPixelWidth <= 0) {
            extractor.text(this.font, text, (int) x, (int) y, color, false);
        } else {
            float scale = maxPixelWidth / (float) textWidth;
            float offsetY = (9f - 9f * scale) / 2f;
            extractor.pose().pushMatrix();
            extractor.pose().translate(x, y + offsetY);
            extractor.pose().scale(scale, scale);
            extractor.text(this.font, text, 0, 0, color, false);
            extractor.pose().popMatrix();
        }
    }

    private void drawScaledCenteredText(GuiGraphicsExtractor extractor, Component text, float centerX, float y, float maxPixelWidth, int color) {
        int textWidth = this.font.width(text);
        if (textWidth <= maxPixelWidth || maxPixelWidth <= 0) {
            extractor.text(this.font, text, (int) (centerX - textWidth / 2f), (int) y, color, false);
        } else {
            float scale = maxPixelWidth / (float) textWidth;
            float offsetY = (9f - 9f * scale) / 2f;
            extractor.pose().pushMatrix();
            extractor.pose().translate(centerX - (textWidth * scale) / 2f, y + offsetY);
            extractor.pose().scale(scale, scale);
            extractor.text(this.font, text, 0, 0, color, false);
            extractor.pose().popMatrix();
        }
    }
}
