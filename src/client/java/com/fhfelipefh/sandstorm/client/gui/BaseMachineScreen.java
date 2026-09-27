package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.MachineMenu;
import com.fhfelipefh.sandstorm.content.item.MolecularUpgradeItem;
import com.fhfelipefh.sandstorm.content.recipe.MachineRecipe;
import com.fhfelipefh.sandstorm.content.recipe.MachineRecipeRegistry;
import com.fhfelipefh.sandstorm.util.NumberFormat;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public abstract class BaseMachineScreen<T extends AbstractContainerMenu & MachineMenu> extends AbstractContainerScreen<T> {
    protected boolean recipeCatalogOpen = false;
    protected int recipeCatalogPage = 0;

    public BaseMachineScreen(T menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    protected String getMachineId() {
        return "";
    }

    protected List<MachineRecipe> getRecipes() {
        return MachineRecipeRegistry.getRecipes(getMachineId());
    }

    protected static String formatCompact(long value) {
        return NumberFormat.compact(value);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 28;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 72;
    }

    private boolean clickHandledOnPress = false;

    private boolean handleCatalogClick(double mx, double my) {
        List<MachineRecipe> recipes = getRecipes();
        if (recipes.isEmpty()) {
            return false;
        }

        int btnX = this.leftPos + this.imageWidth - 24;
        int btnY = this.topPos + 3;
        int btnW = 20;
        int btnH = 13;

        if (mx >= btnX - 1 && mx <= btnX + btnW + 1 && my >= btnY - 1 && my <= btnY + btnH + 1) {
            this.recipeCatalogOpen = !this.recipeCatalogOpen;
            if (this.minecraft != null) {
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            }
            return true;
        }

        if (this.recipeCatalogOpen) {
            int drawerW = 146;
            int drawerH = this.imageHeight;
            int drawerX = getDrawerX(drawerW);
            int drawerY = this.topPos;

            int closeX = drawerX + drawerW - 14;
            int closeY = drawerY + 4;
            if (mx >= closeX - 2 && mx <= closeX + 12 && my >= closeY && my <= closeY + 12) {
                this.recipeCatalogOpen = false;
                if (this.minecraft != null) {
                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                }
                return true;
            }

            int totalPages = Math.max(1, (recipes.size() + 2) / 3);
            if (totalPages > 1) {
                int prevX = drawerX + drawerW - 62;
                int nextX = drawerX + drawerW - 28;
                int pageBtnY = drawerY + 4;
                if (mx >= prevX && mx <= prevX + 12 && my >= pageBtnY && my <= pageBtnY + 12) {
                    this.recipeCatalogPage = Math.max(0, this.recipeCatalogPage - 1);
                    if (this.minecraft != null) {
                        this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                    }
                    return true;
                }
                if (mx >= nextX && mx <= nextX + 12 && my >= pageBtnY && my <= pageBtnY + 12) {
                    this.recipeCatalogPage = Math.min(totalPages - 1, this.recipeCatalogPage + 1);
                    if (this.minecraft != null) {
                        this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                    }
                    return true;
                }
            }

            int cw = drawerW - 10;
            int ch = 46;
            for (int i = 0; i < 3; i++) {
                int recipeIdx = this.recipeCatalogPage * 3 + i;
                if (recipeIdx >= recipes.size()) {
                    break;
                }
                int cx = drawerX + 5;
                int cy = drawerY + 19 + i * 48;
                if (mx >= cx && mx <= cx + cw && my >= cy && my <= cy + ch) {
                    if (this.minecraft != null && this.minecraft.gameMode != null) {
                        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, recipeIdx);
                        this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                    }
                    return true;
                }
            }

            if (mx >= drawerX && mx <= drawerX + drawerW && my >= drawerY && my <= drawerY + drawerH) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        if (handleCatalogClick(event.x(), event.y())) {
            this.clickHandledOnPress = true;
            return true;
        }
        this.clickHandledOnPress = false;
        return super.mouseClicked(event, isDouble);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (this.clickHandledOnPress) {
            this.clickHandledOnPress = false;
            return true;
        }
        if (handleCatalogClick(event.x(), event.y())) {
            return true;
        }
        return super.mouseReleased(event);
    }

    private int getDrawerX(int drawerW) {
        int rightX = this.leftPos + this.imageWidth + 2;
        if (rightX + drawerW <= this.width) {
            return rightX;
        }
        int leftX = this.leftPos - drawerW - 2;
        if (leftX >= 0) {
            return leftX;
        }
        return Math.max(0, (this.width - drawerW) / 2);
    }

    protected int lastMouseX;
    protected int lastMouseY;

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        this.lastMouseX = mouseX;
        this.lastMouseY = mouseY;
        renderChassis(extractor);
        super.extractRenderState(extractor, mouseX, mouseY, delta);
        if (this.recipeCatalogOpen && !getRecipes().isEmpty()) {
            renderRecipeDrawer(extractor, mouseX, mouseY);
        }
        renderCustomTooltips(extractor, mouseX, mouseY);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int maxTitleWidth = getRecipes().isEmpty()
                ? (this.imageWidth - this.titleLabelX - 6)
                : (this.imageWidth - this.titleLabelX - 26);
        drawAdaptiveText(extractor, this.title, this.titleLabelX, this.titleLabelY, maxTitleWidth, 0xFF00E5FF);
        int maxInvWidth = this.imageWidth - this.inventoryLabelX - 6;
        drawAdaptiveText(extractor, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, maxInvWidth, 0xFF78909C);
    }

    protected void drawAdaptiveText(GuiGraphicsExtractor extractor, Component text, float x, float y, float maxPixelWidth, int color) {
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

    protected void renderChassis(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        extractor.fill(x, y, x + imageWidth, y + imageHeight, 0xFA0A0E17);
        extractor.fill(x, y, x + imageWidth, y + 1, 0xFF00E5FF);
        extractor.fill(x, y + imageHeight - 1, x + imageWidth, y + imageHeight, 0xFF00E5FF);
        extractor.fill(x, y, x + 1, y + imageHeight, 0xFF00E5FF);
        extractor.fill(x + imageWidth - 1, y, x + imageWidth, y + imageHeight, 0xFF00E5FF);

        extractor.fill(x + 1, y + 1, x + 4, y + 4, 0xFF00E5FF);
        extractor.fill(x + imageWidth - 4, y + 1, x + imageWidth - 1, y + 4, 0xFF00E5FF);
        extractor.fill(x + 1, y + imageHeight - 4, x + 4, y + imageHeight - 1, 0xFF00E5FF);
        extractor.fill(x + imageWidth - 4, y + imageHeight - 4, x + imageWidth - 1, y + imageHeight - 1, 0xFF00E5FF);

        extractor.fill(x + 4, y + 4, x + imageWidth - 4, y + 16, 0xDD101824);
        extractor.fill(x + 4, y + 16, x + imageWidth - 4, y + 17, 0x8800E5FF);

        boolean wpt = this.menu.isWptConnected();
        int wptColor = wpt ? 0xFF00E5FF : 0xFF455A64;
        extractor.fill(x + 10, y + 6, x + 20, y + 14, 0xFF05080E);
        extractor.fill(x + 14, y + 7, x + 16, y + 13, wptColor);
        extractor.fill(x + 11, y + 8, x + 13, y + 10, wptColor);
        extractor.fill(x + 17, y + 8, x + 19, y + 10, wptColor);

        renderRecipeButton(extractor, this.lastMouseX, this.lastMouseY);

        for (Slot slot : this.menu.slots) {
            int sx = x + slot.x;
            int sy = y + slot.y;
            boolean isMachineSlot = slot.index < this.menu.slots.size() - 36;
            int borderColor = isMachineSlot ? 0xFF00E5FF : 0xFF1E293B;
            int bgColor = isMachineSlot ? 0xDD0D131F : 0xAA080C14;

            extractor.fill(sx - 1, sy - 1, sx + 17, sy + 17, borderColor);
            extractor.fill(sx, sy, sx + 16, sy + 16, bgColor);
        }

        renderEnergyMeter(extractor, x + 8, y + 19, 16, 26);
        renderProgressBar(extractor, x + 70, y + 39, 36, 12);
    }

    private void renderRecipeButton(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        List<MachineRecipe> recipes = getRecipes();
        if (recipes.isEmpty()) {
            return;
        }

        int btnX = this.leftPos + this.imageWidth - 24;
        int btnY = this.topPos + 3;
        int btnW = 20;
        int btnH = 13;

        boolean hovered = mouseX >= btnX - 1 && mouseX <= btnX + btnW + 1
                && mouseY >= btnY - 1 && mouseY <= btnY + btnH + 1;
        int btnBg = this.recipeCatalogOpen ? 0xFF006677 : (hovered ? 0xFF0A2234 : 0xFF05080E);
        int btnBorder = (this.recipeCatalogOpen || hovered) ? 0xFF00E5FF : 0xFF455A64;

        extractor.fill(btnX, btnY, btnX + btnW, btnY + btnH, btnBg);
        extractor.fill(btnX, btnY, btnX + btnW, btnY + 1, btnBorder);
        extractor.fill(btnX, btnY + btnH - 1, btnX + btnW, btnY + btnH, btnBorder);
        extractor.fill(btnX, btnY, btnX + 1, btnY + btnH, btnBorder);
        extractor.fill(btnX + btnW - 1, btnY, btnX + btnW, btnY + btnH, btnBorder);

        int iconColor = (this.recipeCatalogOpen || hovered) ? 0xFF80D8FF : 0xFF00E5FF;
        extractor.fill(btnX + 4, btnY + 2, btnX + 9, btnY + 11, iconColor);
        extractor.fill(btnX + 9, btnY + 2, btnX + 11, btnY + 11, 0xFF101824);
        extractor.fill(btnX + 11, btnY + 2, btnX + 16, btnY + 11, iconColor);
        extractor.fill(btnX + 5, btnY + 4, btnX + 8, btnY + 5, 0xFF05080E);
        extractor.fill(btnX + 5, btnY + 7, btnX + 8, btnY + 8, 0xFF05080E);
        extractor.fill(btnX + 12, btnY + 4, btnX + 15, btnY + 5, 0xFF05080E);
        extractor.fill(btnX + 12, btnY + 7, btnX + 15, btnY + 8, 0xFF05080E);
    }

    private void renderRecipeDrawer(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        List<MachineRecipe> recipes = getRecipes();
        if (recipes.isEmpty()) {
            return;
        }

        int drawerW = 146;
        int drawerH = this.imageHeight;
        int drawerX = getDrawerX(drawerW);
        int drawerY = this.topPos;

        extractor.fill(drawerX, drawerY, drawerX + drawerW, drawerY + drawerH, 0xFA0A0E17);
        extractor.fill(drawerX, drawerY, drawerX + drawerW, drawerY + 1, 0xFF00E5FF);
        extractor.fill(drawerX, drawerY + drawerH - 1, drawerX + drawerW, drawerY + drawerH, 0xFF00E5FF);
        extractor.fill(drawerX, drawerY, drawerX + 1, drawerY + drawerH, 0xFF00E5FF);
        extractor.fill(drawerX + drawerW - 1, drawerY, drawerX + drawerW, drawerY + drawerH, 0xFF00E5FF);

        extractor.fill(drawerX + 1, drawerY + 1, drawerX + 4, drawerY + 4, 0xFF00E5FF);
        extractor.fill(drawerX + drawerW - 4, drawerY + 1, drawerX + drawerW - 1, drawerY + 4, 0xFF00E5FF);
        extractor.fill(drawerX + 1, drawerY + drawerH - 4, drawerX + 4, drawerY + drawerH - 1, 0xFF00E5FF);
        extractor.fill(drawerX + drawerW - 4, drawerY + drawerH - 4, drawerX + drawerW - 1, drawerY + drawerH - 1, 0xFF00E5FF);

        extractor.fill(drawerX + 4, drawerY + 4, drawerX + drawerW - 4, drawerY + 16, 0xDD101824);
        extractor.fill(drawerX + 4, drawerY + 16, drawerX + drawerW - 4, drawerY + 17, 0x8800E5FF);

        drawAdaptiveText(extractor, Component.translatable("gui.sandstorm.machine.recipes_title"), drawerX + 8, drawerY + 6, drawerW - 74, 0xFF00E5FF);

        int totalPages = Math.max(1, (recipes.size() + 2) / 3);
        if (totalPages > 1) {
            String pageStr = (this.recipeCatalogPage + 1) + "/" + totalPages;
            int pageStrW = this.font.width(pageStr);
            int prevArrowX = drawerX + drawerW - 60;
            int nextArrowX = drawerX + drawerW - 28;
            int pageStrX = (prevArrowX + 8 + nextArrowX - pageStrW) / 2;
            extractor.text(this.font, Component.literal("<"), prevArrowX, drawerY + 6, 0xFF80D8FF, false);
            extractor.text(this.font, Component.literal(pageStr), pageStrX, drawerY + 6, 0xFF78909C, false);
            extractor.text(this.font, Component.literal(">"), nextArrowX, drawerY + 6, 0xFF80D8FF, false);
        }

        boolean closeHover = mouseX >= drawerX + drawerW - 16 && mouseX <= drawerX + drawerW - 2
                && mouseY >= drawerY + 4 && mouseY <= drawerY + 16;
        int closeColor = closeHover ? 0xFFFF1744 : 0xFF78909C;
        extractor.text(this.font, Component.literal("x"), drawerX + drawerW - 12, drawerY + 5, closeColor, false);

        int cw = drawerW - 10;
        int ch = 46;
        for (int i = 0; i < 3; i++) {
            int recipeIdx = this.recipeCatalogPage * 3 + i;
            if (recipeIdx >= recipes.size()) {
                break;
            }
            MachineRecipe recipe = recipes.get(recipeIdx);
            int cx = drawerX + 5;
            int cy = drawerY + 19 + i * 48;

            boolean cardHovered = mouseX >= cx && mouseX <= cx + cw && mouseY >= cy && mouseY <= cy + ch;
            boolean hasInputs = hasAnyItem(recipe.getSlot0Inputs()) && hasAnyItem(recipe.getSlot1Inputs());

            int bg = cardHovered ? 0xEE162234 : 0xDD0D131F;
            int border = cardHovered ? 0xFF00E5FF : (hasInputs ? 0xFF00B0FF : 0xFF1E293B);

            extractor.fill(cx, cy, cx + cw, cy + ch, bg);
            extractor.fill(cx, cy, cx + cw, cy + 1, border);
            extractor.fill(cx, cy + ch - 1, cx + cw, cy + ch, border);
            extractor.fill(cx, cy, cx + 1, cy + ch, border);
            extractor.fill(cx + cw - 1, cy, cx + cw, cy + ch, border);

            drawAdaptiveText(extractor, recipe.getTitle(), cx + 4, cy + 3, cw - 20, cardHovered ? 0xFFFFFFFF : 0xFFFFD54F);

            if (hasInputs) {
                extractor.text(this.font, Component.literal("✓"), cx + cw - 12, cy + 3, 0xFF00E676, false);
            } else {
                extractor.text(this.font, Component.literal("x"), cx + cw - 12, cy + 3, 0xFF546E7A, false);
            }

            renderSlotBox(extractor, cx + 4, cy + 15, 18, 18);
            renderSlotBox(extractor, cx + 32, cy + 15, 18, 18);
            renderSlotBox(extractor, cx + 62, cy + 14, 20, 20);

            extractor.text(this.font, Component.literal("+"), cx + 24, cy + 20, 0xFF78909C, false);
            extractor.text(this.font, Component.literal("»"), cx + 52, cy + 20, 0xFF00E5FF, false);

            ItemStack in0 = getCyclingItem(recipe.getSlot0Inputs());
            ItemStack in1 = getCyclingItem(recipe.getSlot1Inputs());
            ItemStack out = recipe.getOutput();

            extractor.item(in0, cx + 5, cy + 16);
            extractor.item(in1, cx + 33, cy + 16);
            extractor.item(out, cx + 64, cy + 16);

            int infoX = cx + 86;
            extractor.text(this.font, Component.literal("§b" + recipe.getEnergyCost() + "J"), infoX, cy + 16, 0xFF00E5FF, false);
            extractor.text(this.font, Component.literal("§7" + (recipe.getProcessTicks() / 20) + "s"), infoX, cy + 25, 0xFF90A4AE, false);

            if (cardHovered) {
                drawAdaptiveText(extractor, Component.translatable("gui.sandstorm.machine.recipe_click_hint"), cx + 4, cy + 36, cw - 8, 0xFFFFD54F);
            } else if (in1.getItem() instanceof MolecularUpgradeItem upgrade) {
                String effectKey = "tooltip.sandstorm.molecular_upgrade." + upgrade.getUpgradeType().getId() + ".effect";
                drawAdaptiveText(extractor, Component.translatable(effectKey), cx + 4, cy + 36, cw - 8, hasInputs ? 0xFF00E676 : 0xFF81C784);
            } else if (hasInputs) {
                drawAdaptiveText(extractor, Component.translatable("gui.sandstorm.machine.recipe_ready"), cx + 4, cy + 36, cw - 8, 0xFF00E676);
            } else {
                drawAdaptiveText(extractor, Component.translatable("gui.sandstorm.machine.recipe_missing"), cx + 4, cy + 36, cw - 8, 0xFF546E7A);
            }

            if (mouseX >= cx + 4 && mouseX <= cx + 22 && mouseY >= cy + 15 && mouseY <= cy + 33) {
                extractor.setTooltipForNextFrame(this.font, in0.getHoverName(), mouseX, mouseY);
            } else if (mouseX >= cx + 32 && mouseX <= cx + 50 && mouseY >= cy + 15 && mouseY <= cy + 33) {
                if (in1.getItem() instanceof MolecularUpgradeItem upgrade) {
                    String descKey = "tooltip.sandstorm.molecular_upgrade." + upgrade.getUpgradeType().getId() + ".desc";
                    String effectKey = "tooltip.sandstorm.molecular_upgrade." + upgrade.getUpgradeType().getId() + ".effect";
                    List<Component> tip = List.of(
                            in1.getHoverName(),
                            Component.literal("§a").append(Component.translatable(effectKey)),
                            Component.literal("§7").append(Component.translatable(descKey))
                    );
                    extractor.setComponentTooltipForNextFrame(this.font, tip, mouseX, mouseY);
                } else {
                    extractor.setTooltipForNextFrame(this.font, in1.getHoverName(), mouseX, mouseY);
                }
            } else if (mouseX >= cx + 62 && mouseX <= cx + 82 && mouseY >= cy + 14 && mouseY <= cy + 34) {
                if (in1.getItem() instanceof MolecularUpgradeItem upgrade) {
                    String effectKey = "tooltip.sandstorm.molecular_upgrade." + upgrade.getUpgradeType().getId() + ".effect";
                    List<Component> tip = List.of(
                            out.getHoverName(),
                            Component.literal("§a").append(Component.translatable(effectKey))
                    );
                    extractor.setComponentTooltipForNextFrame(this.font, tip, mouseX, mouseY);
                } else {
                    extractor.setTooltipForNextFrame(this.font, out.getHoverName(), mouseX, mouseY);
                }
            } else if (mouseX >= infoX && mouseX <= cx + cw && mouseY >= cy + 15 && mouseY <= cy + 35) {
                Component costTooltip = Component.literal("§bEnergia: " + recipe.getEnergyCost() + " J (" + recipe.getProcessTicks() + " ticks)");
                extractor.setTooltipForNextFrame(this.font, costTooltip, mouseX, mouseY);
            }
        }
    }

    private void renderSlotBox(GuiGraphicsExtractor extractor, int sx, int sy, int w, int h) {
        extractor.fill(sx, sy, sx + w, sy + h, 0xFF05080E);
        extractor.fill(sx, sy, sx + w, sy + 1, 0xFF334155);
        extractor.fill(sx, sy + h - 1, sx + w, sy + h, 0xFF334155);
        extractor.fill(sx, sy, sx + 1, sy + h, 0xFF334155);
        extractor.fill(sx + w - 1, sy, sx + w, sy + h, 0xFF334155);
    }

    private ItemStack getCyclingItem(List<ItemStack> list) {
        if (list.isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (list.size() == 1) {
            return list.get(0);
        }
        int idx = (int) ((System.currentTimeMillis() / 1500) % list.size());
        return list.get(idx);
    }

    private boolean hasAnyItem(List<ItemStack> candidates) {
        if (this.minecraft == null || this.minecraft.player == null) {
            return false;
        }
        Inventory inv = this.minecraft.player.getInventory();
        for (ItemStack candidate : candidates) {
            int needed = Math.max(1, candidate.getCount());
            int count = 0;
            for (int i = 0; i < inv.getContainerSize(); i++) {
                ItemStack s = inv.getItem(i);
                if (!s.isEmpty() && s.is(candidate.getItem())) {
                    count += s.getCount();
                    if (count >= needed) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected void renderEnergyMeter(GuiGraphicsExtractor extractor, int x, int y, int width, int height) {
        extractor.fill(x - 1, y - 1, x + width + 1, y + height + 1, 0xFF1E293B);
        extractor.fill(x, y, x + width, y + height, 0xFF05080E);

        int scaled = this.menu.getEnergyScaled(height);
        if (scaled > 0) {
            int energyColor = 0xFF00E5FF;
            if (this.menu.getEnergy() < this.menu.getMaxEnergy() / 4) {
                energyColor = 0xFFFF1744;
            } else if (this.menu.getEnergy() < this.menu.getMaxEnergy() / 2) {
                energyColor = 0xFFFF9100;
            }
            extractor.fill(x + 1, y + height - scaled, x + width - 1, y + height, energyColor);
        }
    }

    protected boolean hasOutputReady() {
        for (Slot slot : this.menu.slots) {
            if (slot.x >= 110 && slot.hasItem() && !slot.mayPlace(ItemStack.EMPTY)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isMouseOverProgress(int mouseX, int mouseY, int x, int y) {
        return mouseX >= x + 69 && mouseX <= x + 107 && mouseY >= y + 38 && mouseY <= y + 52;
    }

    protected void renderProgressBar(GuiGraphicsExtractor extractor, int x, int y, int width, int height) {
        extractor.fill(x - 1, y - 1, x + width + 1, y + height + 1, 0xFF1E293B);
        extractor.fill(x, y, x + width, y + height, 0xFF080C14);

        int progressWidth = this.menu.getProgressScaled(width - 2);
        boolean ready = !this.menu.isProcessing() && hasOutputReady();
        if (progressWidth > 0) {
            extractor.fill(x + 1, y + 1, x + 1 + progressWidth, y + height - 1, 0xFF00E5FF);
            extractor.fill(x + progressWidth - 1, y + 1, x + 1 + progressWidth, y + height - 1, 0xFFFFFFFF);
        } else if (ready) {
            extractor.fill(x + 1, y + 1, x + width - 1, y + height - 1, 0xFF00E676);
            extractor.fill(x + width - 2, y + 1, x + width - 1, y + height - 1, 0xFFB9F6CA);
        }

        int arrowY = y + height / 2;
        int arrowColor = ready ? 0xFF00E676 : 0xFF00E5FF;
        extractor.fill(x + width - 4, arrowY - 2, x + width - 2, arrowY + 3, arrowColor);
    }

    protected void renderCustomTooltips(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        if (!getRecipes().isEmpty()) {
            int btnX = x + this.imageWidth - 24;
            int btnY = y + 3;
            int btnW = 20;
            int btnH = 13;
            if (mouseX >= btnX - 1 && mouseX <= btnX + btnW + 1 && mouseY >= btnY - 1 && mouseY <= btnY + btnH + 1) {
                extractor.setTooltipForNextFrame(this.font, Component.translatable("gui.sandstorm.machine.recipes_tooltip"), mouseX, mouseY);
                return;
            }
        }

        if (mouseX >= x + 7 && mouseX <= x + 25 && mouseY >= y + 18 && mouseY <= y + 46) {
            String energy = formatCompact(this.menu.getEnergy());
            String maxEnergy = formatCompact(this.menu.getMaxEnergy());
            String wptStatus = this.menu.isWptConnected()
                    ? "§a⚡ WPT Ativa"
                    : "§c⚡ Sem WPT";
            Component tooltip = Component.literal("§b" + energy + " / " + maxEnergy + " J " + wptStatus);
            extractor.setTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
        } else if (mouseX >= x + 8 && mouseX <= x + 22 && mouseY >= y + 4 && mouseY <= y + 16) {
            String wptStatus = this.menu.isWptConnected()
                    ? "§aWPT Online"
                    : "§7WPT Offline";
            Component tooltip = Component.literal(wptStatus);
            extractor.setTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
        } else if (isMouseOverProgress(mouseX, mouseY, x, y)) {
            int maxProg = this.menu.getMaxProgress();
            int pct = maxProg > 0 ? (this.menu.getProgress() * 100 / maxProg) : 0;
            String status = this.menu.isProcessing() ? " §a[PROCESSANDO]" : " §7[EM ESPERA]";
            if (!this.menu.isProcessing() && hasOutputReady()) {
                pct = 100;
                status = " §a[PRONTO]";
            }
            Component tooltip = Component.literal("§bProgresso: §f" + pct + "%" + status);
            extractor.setTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
        } else if (mouseX >= x + 7 && mouseX <= x + 25 && mouseY >= y + 47 && mouseY <= y + 65) {
            boolean hasBatterySlot = this.menu.slots.stream().anyMatch(s -> s.x == 8 && s.y == 53);
            if (hasBatterySlot && this.menu.slots.size() > 3 && !this.menu.slots.get(3).hasItem()) {
                Component tooltip = Component.translatable("gui.sandstorm.machine.battery_slot_hint");
                extractor.setTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
            }
        }
    }
}
