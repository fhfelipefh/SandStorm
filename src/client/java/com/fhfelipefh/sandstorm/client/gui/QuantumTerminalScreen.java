package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.QuantumTerminalMenu;
import com.fhfelipefh.sandstorm.content.network.TerminalActionPayload;
import com.fhfelipefh.sandstorm.content.storage.StoredItemEntry;
import com.fhfelipefh.sandstorm.util.NumberFormat;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class QuantumTerminalScreen extends AbstractContainerScreen<QuantumTerminalMenu> {

    public enum SortMode {
        COUNT,
        NAME
    }

    private EditBox searchBox;
    private List<StoredItemEntry> filteredItems = new ArrayList<>();
    private SortMode sortMode = SortMode.COUNT;
    private int scrollRow = 0;

    public QuantumTerminalScreen(QuantumTerminalMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 196, 222);
        this.inventoryLabelY = 126;
    }

    @Override
    protected void init() {
        super.init();
        int searchX = this.leftPos + 8;
        int searchY = this.topPos + 18;
        this.searchBox = new EditBox(this.font, searchX, searchY, 86, 14, Component.translatable("gui.sandstorm.terminal.search_hint"));
        this.searchBox.setHint(Component.translatable("gui.sandstorm.terminal.search_hint"));
        this.searchBox.setTextColor(0xFF00E5FF);
        this.searchBox.setResponder(s -> updateFilteredItems());
        this.addRenderableWidget(this.searchBox);
        updateFilteredItems();
    }

    private void updateFilteredItems() {
        List<StoredItemEntry> source = this.menu.getClientItems();
        String query = searchBox != null ? searchBox.getValue().trim().toLowerCase(Locale.ROOT) : "";

        filteredItems = new ArrayList<>();
        for (StoredItemEntry entry : source) {
            String name = entry.template().getHoverName().getString().toLowerCase(Locale.ROOT);
            if (query.isEmpty() || name.contains(query)) {
                filteredItems.add(entry);
            }
        }

        if (sortMode == SortMode.COUNT) {
            filteredItems.sort(Comparator.comparingLong(StoredItemEntry::count).reversed());
        } else {
            filteredItems.sort(Comparator.comparing(e -> e.template().getHoverName().getString()));
        }

        int maxScroll = getMaxScroll();
        if (scrollRow > maxScroll) {
            scrollRow = maxScroll;
        }
    }

    private int getMaxScroll() {
        int totalRows = Math.max(1, (int) Math.ceil(filteredItems.size() / 9.0));
        return Math.max(0, totalRows - 4);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        if (this.menu.isClientStateDirty()) {
            updateFilteredItems();
            this.menu.clearClientStateDirty();
        }

        int x = this.leftPos;
        int y = this.topPos;

        extractor.fill(x, y, x + this.imageWidth, y + this.imageHeight, 0xFF0E141B);
        extractor.fill(x + 1, y + 1, x + this.imageWidth - 1, y + 15, 0xFF15222E);
        extractor.fill(x + 1, y + 15, x + this.imageWidth - 1, y + 16, 0xFF00E5FF);

        int sortBtnX = x + 100;
        int sortBtnY = y + 18;
        int sortBtnW = 68;
        int sortBtnH = 14;
        extractor.fill(sortBtnX, sortBtnY, sortBtnX + sortBtnW, sortBtnY + sortBtnH, 0xFF18232C);
        extractor.fill(sortBtnX + 1, sortBtnY + 1, sortBtnX + sortBtnW - 1, sortBtnY + sortBtnH - 1, 0xFF0B1015);
        Component sortText = sortMode == SortMode.COUNT
                ? Component.translatable("gui.sandstorm.terminal.sort_count")
                : Component.translatable("gui.sandstorm.terminal.sort_name");
        extractor.text(this.font, sortText, sortBtnX + 6, sortBtnY + 3, 0xFF00E5FF, false);

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 9; col++) {
                int sx = x + 7 + col * 18;
                int sy = y + 36 + row * 18;
                extractor.fill(sx, sy, sx + 18, sy + 18, 0xFF18232C);
                extractor.fill(sx + 1, sy + 1, sx + 17, sy + 17, 0xFF0B1015);
            }
        }

        int trackX = x + 173;
        int trackY = y + 36;
        int trackH = 72;
        extractor.fill(trackX, trackY, trackX + 12, trackY + trackH, 0xFF15222E);

        int totalRows = Math.max(1, (int) Math.ceil(filteredItems.size() / 9.0));
        int maxScroll = getMaxScroll();
        if (maxScroll > 0) {
            int thumbH = Math.max(12, (int) ((4.0 / Math.max(4, totalRows)) * trackH));
            int thumbY = trackY + (int) (((float) scrollRow / maxScroll) * (trackH - thumbH));
            extractor.fill(trackX + 1, thumbY, trackX + 11, thumbY + thumbH, 0xFF00E5FF);
        } else {
            extractor.fill(trackX + 2, trackY + 2, trackX + 10, trackY + 14, 0xFF2A3B4D);
        }

        StoredItemEntry hoveredEntry = null;

        for (int i = 0; i < 36; i++) {
            int idx = scrollRow * 9 + i;
            if (idx < filteredItems.size()) {
                StoredItemEntry entry = filteredItems.get(idx);
                int col = i % 9;
                int row = i / 9;
                int sx = x + 7 + col * 18;
                int sy = y + 36 + row * 18;

                extractor.item(entry.template(), sx + 1, sy + 1);

                String countStr = NumberFormat.compact(entry.count());
                Component countComp = Component.literal(countStr);
                int cw = this.font.width(countComp);
                extractor.text(this.font, countComp, sx + 17 - cw, sy + 9, 0xFFFFFFFF, true);

                if (mouseX >= sx && mouseX < sx + 18 && mouseY >= sy && mouseY < sy + 18) {
                    hoveredEntry = entry;
                    extractor.fill(sx + 1, sy + 1, sx + 17, sy + 17, 0x4000E5FF);
                }
            }
        }

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 9; c++) {
                int px = x + 7 + c * 18;
                int py = y + 137 + r * 18;
                extractor.fill(px, py, px + 18, py + 18, 0xFF18232C);
                extractor.fill(px + 1, py + 1, px + 17, py + 17, 0xFF0B1015);
            }
        }

        for (int c = 0; c < 9; c++) {
            int hx = x + 7 + c * 18;
            int hy = y + 195;
            extractor.fill(hx, hy, hx + 18, hy + 18, 0xFF18232C);
            extractor.fill(hx + 1, hy + 1, hx + 17, hy + 17, 0xFF0B1015);
        }

        super.extractRenderState(extractor, mouseX, mouseY, delta);

        if (hoveredEntry != null) {
            List<Component> tooltip = new ArrayList<>(this.getTooltipFromContainerItem(hoveredEntry.template()));
            tooltip.add(Component.translatable("gui.sandstorm.terminal.stored_tooltip", NumberFormat.formatExact(hoveredEntry.count())));
            extractor.setComponentTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        long stored = this.menu.getClientTotalStored();
        long cap = this.menu.getClientTotalCapacity();
        String stats = NumberFormat.compact(stored) + "/" + (cap > 0 ? NumberFormat.compact(cap) : "∞");
        Component statsComp = Component.literal(stats);
        int statsWidth = this.font.width(statsComp);
        int statsX = this.imageWidth - statsWidth - 8;
        extractor.text(this.font, statsComp, statsX, 5, 0xFF00E5FF, false);

        int maxTitleWidth = Math.max(10, statsX - 12);
        drawAdaptiveText(extractor, this.title, 8, 5, maxTitleWidth, 0xFF00E5FF);
        drawAdaptiveText(extractor, this.playerInventoryTitle, 8, this.inventoryLabelY, this.imageWidth - 16, 0xFF90A4AE);
    }

    private void drawAdaptiveText(GuiGraphicsExtractor extractor, Component text, float x, float y, float maxPixelWidth, int color) {
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

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        double mx = event.x();
        double my = event.y();
        int x = this.leftPos;
        int y = this.topPos;

        int sortBtnX = x + 100;
        int sortBtnY = y + 18;
        int sortBtnW = 68;
        int sortBtnH = 14;

        if (mx >= sortBtnX && mx <= sortBtnX + sortBtnW && my >= sortBtnY && my <= sortBtnY + sortBtnH) {
            sortMode = (sortMode == SortMode.COUNT ? SortMode.NAME : SortMode.COUNT);
            updateFilteredItems();
            if (this.minecraft != null) {
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            }
            return true;
        }

        int gridX = x + 7;
        int gridY = y + 36;
        int gridW = 9 * 18;
        int gridH = 4 * 18;

        if (mx >= gridX && mx < gridX + gridW && my >= gridY && my < gridY + gridH) {
            int col = (int) (mx - gridX) / 18;
            int row = (int) (my - gridY) / 18;
            int index = (scrollRow + row) * 9 + col;

            if (!this.menu.getCarried().isEmpty()) {
                ClientPlayNetworking.send(new TerminalActionPayload(this.menu.getTerminalPos(), ItemStack.EMPTY, TerminalActionPayload.ACTION_INSERT_HELD));
                return true;
            } else if (index >= 0 && index < filteredItems.size()) {
                StoredItemEntry entry = filteredItems.get(index);
                int action = event.hasShiftDown() ? TerminalActionPayload.ACTION_SHIFT_EXTRACT : (event.isRight() ? TerminalActionPayload.ACTION_EXTRACT_HALF : TerminalActionPayload.ACTION_EXTRACT_STACK);
                ClientPlayNetworking.send(new TerminalActionPayload(this.menu.getTerminalPos(), entry.template(), action));
                return true;
            }
        }

        return super.mouseClicked(event, isDouble);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int x = this.leftPos;
        int y = this.topPos;
        if (mouseX >= x && mouseX <= x + this.imageWidth && mouseY >= y + 18 && mouseY <= y + 110) {
            int maxScroll = getMaxScroll();
            scrollRow = Math.max(0, Math.min(maxScroll, scrollRow - (int) Math.signum(verticalAmount)));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
}
