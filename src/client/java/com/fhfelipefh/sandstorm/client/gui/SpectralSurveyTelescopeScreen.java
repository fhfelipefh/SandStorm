package com.fhfelipefh.sandstorm.client.gui;

import com.fhfelipefh.sandstorm.content.gui.SpectralSurveyTelescopeMenu;
import com.fhfelipefh.sandstorm.util.NumberFormat;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class SpectralSurveyTelescopeScreen extends AbstractContainerScreen<SpectralSurveyTelescopeMenu> {
    private static final int CHASSIS_WIDTH = 230;
    private static final int CHASSIS_HEIGHT = 208;

    public SpectralSurveyTelescopeScreen(SpectralSurveyTelescopeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, CHASSIS_WIDTH, CHASSIS_HEIGHT);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 8;
        this.titleLabelY = 5;
        this.inventoryLabelX = 35;
        this.inventoryLabelY = 111;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        double mx = event.x();
        double my = event.y();
        int x = this.leftPos;
        int y = this.topPos;

        if (mx >= x + 136 && mx <= x + 220 && my >= y + 20 && my <= y + 34) {
            triggerButton(0);
            return true;
        }

        if (mx >= x + 136 && mx <= x + 162 && my >= y + 37 && my <= y + 49) {
            triggerButton(1);
            return true;
        }

        if (mx >= x + 165 && mx <= x + 191 && my >= y + 37 && my <= y + 49) {
            triggerButton(2);
            return true;
        }

        if (mx >= x + 194 && mx <= x + 220 && my >= y + 37 && my <= y + 49) {
            triggerButton(3);
            return true;
        }

        if (mx >= x + 136 && mx <= x + 220 && my >= y + 52 && my <= y + 65) {
            triggerButton(4);
            return true;
        }

        return super.mouseClicked(event, isDouble);
    }

    private void triggerButton(int id) {
        if (this.minecraft != null && this.minecraft.gameMode != null) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, id);
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        renderChassis(extractor);
        super.extractRenderState(extractor, mouseX, mouseY, delta);
        renderDeepSpaceViewport(extractor, delta);
        renderObservatoryControls(extractor, mouseX, mouseY);
        renderWeatherTelemetry(extractor);
        renderEnergyGauge(extractor);
        renderSlotFrames(extractor);
        renderObservatoryTooltips(extractor, mouseX, mouseY);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        drawAdaptiveText(extractor, this.title, this.titleLabelX, this.titleLabelY, CHASSIS_WIDTH - 16, 0xFF00E5FF);
        drawAdaptiveText(extractor, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, CHASSIS_WIDTH - 44, 0xFF78909C);
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

    private void renderSlotFrames(GuiGraphicsExtractor extractor) {
        for (Slot slot : this.menu.slots) {
            int sx = this.leftPos + slot.x;
            int sy = this.topPos + slot.y;
            extractor.fill(sx - 1, sy - 1, sx + 17, sy + 17, 0xFF1E293B);
            extractor.fill(sx, sy, sx + 16, sy + 16, 0xFF0B132B);
        }
    }

    private void renderChassis(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        extractor.fill(x, y, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFA070B14);
        extractor.fill(x, y, x + CHASSIS_WIDTH, y + 1, 0xFF00E5FF);
        extractor.fill(x, y + CHASSIS_HEIGHT - 1, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFF00E5FF);
        extractor.fill(x, y + 1, x + 1, y + CHASSIS_HEIGHT, 0xFF00E5FF);
        extractor.fill(x + CHASSIS_WIDTH - 1, y, x + CHASSIS_WIDTH, y + CHASSIS_HEIGHT, 0xFF00E5FF);

        extractor.fill(x + 1, y + 1, x + 4, y + 4, 0xFF00E5FF);
        extractor.fill(x + CHASSIS_WIDTH - 4, y + 1, x + CHASSIS_WIDTH - 1, y + 4, 0xFF00E5FF);
        extractor.fill(x + 1, y + CHASSIS_HEIGHT - 4, x + 4, y + CHASSIS_HEIGHT - 1, 0xFF00E5FF);
        extractor.fill(x + CHASSIS_WIDTH - 4, y + CHASSIS_HEIGHT - 4, x + CHASSIS_WIDTH - 1, y + CHASSIS_HEIGHT - 1, 0xFF00E5FF);
    }

    private void renderDeepSpaceViewport(GuiGraphicsExtractor extractor, float delta) {
        int vx = this.leftPos + 10;
        int vy = this.topPos + 18;
        int vw = 120;
        int vh = 90;

        extractor.fill(vx - 1, vy - 1, vx + vw + 1, vy + vh + 1, 0xFF1E293B);
        extractor.fill(vx, vy, vx + vw, vy + vh, 0xFF02040A);

        for (int gx = vx + 24; gx < vx + vw; gx += 24) {
            extractor.fill(gx, vy, gx + 1, vy + vh, 0x1800E5FF);
        }
        for (int gy = vy + 18; gy < vy + vh; gy += 18) {
            extractor.fill(vx, gy, vx + vw, gy + 1, 0x1800E5FF);
        }

        float time = (this.minecraft != null && this.minecraft.level != null)
                ? (float) this.minecraft.level.getGameTime() + delta
                : delta;

        int cx = vx + 60;
        int cy = vy + 45;

        for (int i = 0; i < 36; i++) {
            float baseAngle = (float) (i * 0.35);
            float baseDist = (float) (10.0 + ((i * 7) % 46));
            float rot = baseAngle + time * 0.005f;
            int sx = (int) (cx + Math.cos(rot) * baseDist);
            int sy = (int) (cy + Math.sin(rot) * baseDist * 0.65);
            if (sx >= vx + 2 && sx < vx + vw - 2 && sy >= vy + 2 && sy < vy + vh - 2) {
                int alpha = 0x66 + (int) (0x77 * Math.sin(time * 0.15 + i));
                int starColor = (alpha << 24) | 0x00E0F7FA;
                extractor.fill(sx, sy, sx + 1, sy + 1, starColor);
            }
        }

        renderCelestialObject(extractor, cx, cy, vx, vy, vw, vh, time);

        renderViewportHud(extractor, cx, cy, vx, vy, vw, vh);
    }

    private void renderCelestialObject(GuiGraphicsExtractor extractor, int cx, int cy, int vx, int vy, int vw, int vh, float time) {
        int filter = this.menu.getActiveFilter();
        int target = this.menu.getCelestialTarget();

        int primaryColor = switch (filter) {
            case SpectralSurveyTelescopeMenu.FILTER_INFRARED -> 0xFFFF6D00;
            case SpectralSurveyTelescopeMenu.FILTER_RADIO_XRAY -> 0xFFE040FB;
            default -> 0xFF00E5FF;
        };

        int coreColor = switch (filter) {
            case SpectralSurveyTelescopeMenu.FILTER_INFRARED -> 0xFFFFAB40;
            case SpectralSurveyTelescopeMenu.FILTER_RADIO_XRAY -> 0xFF76FF03;
            default -> 0xFFFFD54F;
        };

        if (target == SpectralSurveyTelescopeMenu.TARGET_ANDROMEDA) {
            extractor.fill(cx - 3, cy - 2, cx + 4, cy + 3, coreColor);
            extractor.fill(cx - 5, cy - 1, cx + 6, cy + 2, (0xAA << 24) | (coreColor & 0x00FFFFFF));
            for (int t = 0; t < 30; t++) {
                float rad = 4f + t * 0.9f;
                float angle = t * 0.22f + time * 0.02f;
                int ax1 = (int) (cx + Math.cos(angle) * rad);
                int ay1 = (int) (cy + Math.sin(angle) * rad * 0.5f);
                int ax2 = (int) (cx - Math.cos(angle) * rad);
                int ay2 = (int) (cy - Math.sin(angle) * rad * 0.5f);
                int alpha = Math.max(0x33, 0xEE - t * 6);
                int armColor = (alpha << 24) | (primaryColor & 0x00FFFFFF);
                if (ax1 >= vx + 2 && ax1 < vx + vw - 2 && ay1 >= vy + 2 && ay1 < vy + vh - 2) {
                    extractor.fill(ax1, ay1, ax1 + 1, ay1 + 1, armColor);
                }
                if (ax2 >= vx + 2 && ax2 < vx + vw - 2 && ay2 >= vy + 2 && ay2 < vy + vh - 2) {
                    extractor.fill(ax2, ay2, ax2 + 1, ay2 + 1, armColor);
                }
            }
        } else if (target == SpectralSurveyTelescopeMenu.TARGET_CRAB_NEBULA) {
            for (int i = 0; i < 24; i++) {
                float angle = (float) (i * Math.PI / 12.0);
                float pulse = (float) (14.0 + Math.sin(time * 0.12 + i) * 4.0);
                int nx = (int) (cx + Math.cos(angle) * pulse);
                int ny = (int) (cy + Math.sin(angle) * pulse * 0.8);
                int col = (i % 2 == 0) ? primaryColor : coreColor;
                if (nx >= vx + 2 && nx < vx + vw - 2 && ny >= vy + 2 && ny < vy + vh - 2) {
                    extractor.fill(nx - 1, ny - 1, nx + 2, ny + 2, (0xCC << 24) | (col & 0x00FFFFFF));
                }
            }
            extractor.fill(cx - 2, cy - 2, cx + 3, cy + 3, 0xFFFFFFFF);
        } else if (target == SpectralSurveyTelescopeMenu.TARGET_KEPLER_EXOPLANET) {
            extractor.fill(cx - 6, cy - 6, cx + 7, cy + 7, primaryColor);
            extractor.fill(cx - 4, cy - 4, cx + 5, cy + 5, coreColor);
            for (int rx = -16; rx <= 16; rx++) {
                int ry = (int) (rx * -0.25f);
                int px = cx + rx;
                int py = cy + ry;
                if (Math.abs(rx) > 6 && px >= vx + 2 && px < vx + vw - 2) {
                    extractor.fill(px, py, px + 1, py + 1, 0xFFE0F7FA);
                }
            }
        } else if (target == SpectralSurveyTelescopeMenu.TARGET_CYGNUS_PULSAR) {
            int pulseSize = (int) (2 + Math.abs(Math.sin(time * 0.4)) * 3);
            extractor.fill(cx - pulseSize, cy - pulseSize, cx + pulseSize + 1, cy + pulseSize + 1, 0xFFFFFFFF);
            for (int j = 4; j <= 28; j += 2) {
                int jetAlpha = Math.max(0x22, 0xFF - j * 8);
                int jCol = (jetAlpha << 24) | (primaryColor & 0x00FFFFFF);
                extractor.fill(cx, cy - j, cx + 1, cy - j + 1, jCol);
                extractor.fill(cx, cy + j, cx + 1, cy + j + 1, jCol);
            }
        } else {
            int r = (int) (8 + Math.sin(time * 0.15) * 2);
            extractor.fill(cx - r, cy - r, cx + r + 1, cy + r + 1, coreColor);
            for (int a = 0; a < 8; a++) {
                float ang = (float) (a * Math.PI / 4.0 + time * 0.05f);
                int fx = (int) (cx + Math.cos(ang) * (r + 4));
                int fy = (int) (cy + Math.sin(ang) * (r + 4));
                if (fx >= vx + 2 && fx < vx + vw - 2 && fy >= vy + 2 && fy < vy + vh - 2) {
                    extractor.fill(fx - 1, fy - 1, fx + 1, fy + 1, primaryColor);
                }
            }
        }
    }

    private void renderViewportHud(GuiGraphicsExtractor extractor, int cx, int cy, int vx, int vy, int vw, int vh) {
        extractor.fill(cx - 8, cy, cx - 2, cy + 1, 0xAA00E5FF);
        extractor.fill(cx + 3, cy, cx + 9, cy + 1, 0xAA00E5FF);
        extractor.fill(cx, cy - 8, cx + 1, cy - 2, 0xAA00E5FF);
        extractor.fill(cx, cy + 3, cx + 1, cy + 9, 0xAA00E5FF);

        extractor.fill(vx + 3, vy + 3, vx + 8, vy + 4, 0xFF00E5FF);
        extractor.fill(vx + 3, vy + 3, vx + 4, vy + 8, 0xFF00E5FF);
        extractor.fill(vx + vw - 8, vy + 3, vx + vw - 3, vy + 4, 0xFF00E5FF);
        extractor.fill(vx + vw - 4, vy + 3, vx + vw - 3, vy + 8, 0xFF00E5FF);
        extractor.fill(vx + 3, vy + vh - 4, vx + 8, vy + vh - 3, 0xFF00E5FF);
        extractor.fill(vx + 3, vy + vh - 8, vx + 4, vy + vh - 3, 0xFF00E5FF);
        extractor.fill(vx + vw - 8, vy + vh - 4, vx + vw - 3, vy + vh - 3, 0xFF00E5FF);
        extractor.fill(vx + vw - 4, vy + vh - 8, vx + vw - 3, vy + vh - 3, 0xFF00E5FF);

        drawAdaptiveText(extractor, Component.literal("RA:18h42m"), vx + 6, vy + 6, 48, 0xDD00E5FF);
        drawAdaptiveText(extractor, Component.literal("DEC:+34 18"), vx + 6, vy + 14, 48, 0xDD00E5FF);

        String targetAbbr = getTargetAbbreviation(this.menu.getCelestialTarget());
        drawAdaptiveText(extractor, Component.literal(targetAbbr), vx + vw - 44, vy + 6, 38, 0xFFE0F7FA);

        int prog = this.menu.getScanProgress();
        drawAdaptiveText(extractor, Component.literal(prog + "%"), vx + vw - 30, vy + vh - 12, 26, 0xFF76FF03);

        extractor.fill(vx + 2, vy + vh - 4, vx + vw - 2, vy + vh - 2, 0xFF05080E);
        int barW = (int) ((vw - 4) * (prog / 100.0f));
        if (barW > 0) {
            extractor.fill(vx + 2, vy + vh - 4, vx + 2 + barW, vy + vh - 2, 0xFF00E5FF);
        }
    }

    private void renderObservatoryControls(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        boolean targetHover = (mouseX >= x + 136 && mouseX <= x + 220 && mouseY >= y + 20 && mouseY <= y + 34);
        int targetBorder = targetHover ? 0xFF00E5FF : 0xFF1E293B;
        extractor.fill(x + 135, y + 19, x + 221, y + 35, targetBorder);
        extractor.fill(x + 136, y + 20, x + 220, y + 34, 0xFF05080E);
        String targetName = getTargetName(this.menu.getCelestialTarget());
        drawAdaptiveText(extractor, Component.literal("ALVO: " + targetName), x + 139, y + 23, 76, 0xFFE0F7FA);

        int activeF = this.menu.getActiveFilter();
        renderFilterButton(extractor, x + 136, y + 37, 26, 12, "OPT", activeF == 0, 0xFF00E5FF, mouseX, mouseY);
        renderFilterButton(extractor, x + 165, y + 37, 26, 12, "INF", activeF == 1, 0xFFFF6D00, mouseX, mouseY);
        renderFilterButton(extractor, x + 194, y + 37, 26, 12, "RAD", activeF == 2, 0xFFE040FB, mouseX, mouseY);

        boolean scanHover = (mouseX >= x + 136 && mouseX <= x + 220 && mouseY >= y + 52 && mouseY <= y + 65);
        int scanBorder = scanHover ? 0xFF00E5FF : 0xFF1E293B;
        extractor.fill(x + 135, y + 51, x + 221, y + 66, scanBorder);
        extractor.fill(x + 136, y + 52, x + 220, y + 65, 0xFF05080E);
        drawAdaptiveText(extractor, Component.literal("VARREDURA (500 EU)"), x + 139, y + 55, 76, 0xFF00E5FF);

        drawAdaptiveText(extractor, Component.literal("DADOS / DISCO:"), x + 136, y + 92, 58, 0xFF78909C);
    }

    private void renderFilterButton(GuiGraphicsExtractor extractor, int bx, int by, int bw, int bh, String label, boolean active, int activeColor, int mx, int my) {
        boolean hover = (mx >= bx && mx <= bx + bw && my >= by && my <= by + bh);
        int border = active ? activeColor : (hover ? 0xFF00E5FF : 0xFF1E293B);
        int bg = active ? activeColor : 0xFF05080E;
        int txtCol = active ? 0xFF070B14 : 0xFFB0BEC5;

        extractor.fill(bx - 1, by - 1, bx + bw + 1, by + bh + 1, border);
        extractor.fill(bx, by, bx + bw, by + bh, bg);
        drawAdaptiveText(extractor, Component.literal(label), bx + 4, by + 2, bw - 8, txtCol);
    }

    private void renderEnergyGauge(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        int gx = x + 136;
        int gy = y + 70;
        int gw = 84;
        int gh = 5;

        extractor.fill(gx - 1, gy - 1, gx + gw + 1, gy + gh + 1, 0xFF1E293B);
        extractor.fill(gx, gy, gx + gw, gy + gh, 0xFF05080E);

        int maxE = Math.max(1, this.menu.getMaxEnergy());
        int curE = Math.min(maxE, this.menu.getStoredEnergy());
        int fillW = (int) ((long) curE * gw / maxE);
        if (fillW > 0) {
            extractor.fill(gx, gy, gx + fillW, gy + gh, 0xFF00E5FF);
        }

        String energyLabel = NumberFormat.compact(curE) + " / " + NumberFormat.compact(maxE) + " EU";
        drawAdaptiveText(extractor, Component.literal(energyLabel), gx, gy + 8, gw, 0xFF78909C);
    }

    private void renderWeatherTelemetry(GuiGraphicsExtractor extractor) {
        int x = this.leftPos;
        int y = this.topPos;

        boolean sky = this.menu.hasSkyAccess();
        Component skyText = sky ? Component.literal("CÉU ABERTO (100%)") : Component.literal("CÉU BLOQUEADO");
        int skyColor = sky ? 0xFF4CAF50 : 0xFFF44336;
        drawAdaptiveText(extractor, skyText, x + 10, y + 110, 80, 0xFF000000 | skyColor);

        int threat = this.menu.getStormThreat();
        int sec = this.menu.getSecondsToStorm();
        Component weatherText;
        int weatherColor;
        if (threat == 2) {
            weatherText = Component.literal("TEMPESTADE: " + sec + "s");
            weatherColor = 0xFFFF5252;
        } else if (threat == 1) {
            weatherText = Component.literal("ALERTA: " + sec + "s");
            weatherColor = 0xFFFFAB40;
        } else {
            weatherText = Component.literal("RADAR: CÉU LIMPO");
            weatherColor = 0xFF80DEEA;
        }
        drawAdaptiveText(extractor, weatherText, x + 136, y + 110, 84, weatherColor);
    }

    private void renderObservatoryTooltips(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        if (mouseX >= x + 10 && mouseX <= x + 130 && mouseY >= y + 18 && mouseY <= y + 108) {
            String targetName = getTargetName(this.menu.getCelestialTarget());
            String filterName = getFilterName(this.menu.getActiveFilter());
            List<Component> tip = List.of(
                    Component.literal("§b[Observatório de Varredura Espectral]"),
                    Component.literal("§7Mapeamento astronômico de longo alcance."),
                    Component.literal("§fAlvo Atual: §e" + targetName),
                    Component.literal("§fBanda: §b" + filterName),
                    Component.literal("§fProgresso da Varredura: §a" + this.menu.getScanProgress() + "%")
            );
            extractor.setComponentTooltipForNextFrame(this.font, tip, mouseX, mouseY);
        }

        if (mouseX >= x + 136 && mouseX <= x + 220 && mouseY >= y + 20 && mouseY <= y + 34) {
            List<Component> tip = List.of(
                    Component.literal("§6[Sintonia de Alvo Celestial]"),
                    Component.literal("§7Clique para alternar o corpo celeste observado.")
            );
            extractor.setComponentTooltipForNextFrame(this.font, tip, mouseX, mouseY);
        }

        if (mouseX >= x + 136 && mouseX <= x + 162 && mouseY >= y + 37 && mouseY <= y + 49) {
            List<Component> tip = List.of(
                    Component.literal("§b[Banda Óptica / Visível]"),
                    Component.literal("§7Mapeia estrelas e braços galácticos luminosos.")
            );
            extractor.setComponentTooltipForNextFrame(this.font, tip, mouseX, mouseY);
        }

        if (mouseX >= x + 165 && mouseX <= x + 191 && mouseY >= y + 37 && mouseY <= y + 49) {
            List<Component> tip = List.of(
                    Component.literal("§6[Banda Infravermelha Térmica]"),
                    Component.literal("§7Mapeia poeira estelar e calor de exoplanetas.")
            );
            extractor.setComponentTooltipForNextFrame(this.font, tip, mouseX, mouseY);
        }

        if (mouseX >= x + 194 && mouseX <= x + 220 && mouseY >= y + 37 && mouseY <= y + 49) {
            List<Component> tip = List.of(
                    Component.literal("§d[Banda de Rádio & Raios-X]"),
                    Component.literal("§7Mapeia pulsares e emissões relativísticas.")
            );
            extractor.setComponentTooltipForNextFrame(this.font, tip, mouseX, mouseY);
        }

        if (mouseX >= x + 136 && mouseX <= x + 220 && mouseY >= y + 52 && mouseY <= y + 65) {
            List<Component> tip = List.of(
                    Component.literal("§a[Disparar Varredura Profunda]"),
                    Component.literal("§7Consumo: 500 EU por pulso de varredura."),
                    Component.literal("§7Avança a análise do corpo celeste em foco.")
            );
            extractor.setComponentTooltipForNextFrame(this.font, tip, mouseX, mouseY);
        }

        if (mouseX >= x + 136 && mouseX <= x + 220 && mouseY >= y + 68 && mouseY <= y + 84) {
            int maxE = Math.max(1, this.menu.getMaxEnergy());
            int curE = Math.min(maxE, this.menu.getStoredEnergy());
            List<Component> tip = List.of(
                    Component.literal("§e[Acumulador de Energia do Telescópio]"),
                    Component.literal("§fEnergia: §e" + NumberFormat.compact(curE) + " / " + NumberFormat.compact(maxE) + " EU"),
                    Component.literal("§7Recarga solar contínua sob o céu aberto.")
            );
            extractor.setComponentTooltipForNextFrame(this.font, tip, mouseX, mouseY);
        }

        if (mouseX >= x + 10 && mouseX <= x + 220 && mouseY >= y + 108 && mouseY <= y + 120) {
            int threat = this.menu.getStormThreat();
            Component threatLine = (threat == 2)
                    ? Component.literal("§cTempestade de areia em curso!")
                    : (threat == 1 ? Component.literal("§6Frente de tempestade detectada!") : Component.literal("§aAtmosfera e vento estáveis no setor."));
            List<Component> tip = List.of(
                    Component.literal("§3[Telemetria Planetária Orbital]"),
                    Component.literal("§7Radar meteorológico e enlace com satélites."),
                    threatLine
            );
            extractor.setComponentTooltipForNextFrame(this.font, tip, mouseX, mouseY);
        }
    }

    private String getTargetName(int target) {
        return switch (target) {
            case SpectralSurveyTelescopeMenu.TARGET_CRAB_NEBULA -> "Nebulosa Caranguejo";
            case SpectralSurveyTelescopeMenu.TARGET_KEPLER_EXOPLANET -> "Exoplaneta Kepler";
            case SpectralSurveyTelescopeMenu.TARGET_CYGNUS_PULSAR -> "Pulsar de Cygnus";
            case SpectralSurveyTelescopeMenu.TARGET_SOLAR_CORONA -> "Corona Solar";
            default -> "Andrômeda";
        };
    }

    private String getTargetAbbreviation(int target) {
        return switch (target) {
            case SpectralSurveyTelescopeMenu.TARGET_CRAB_NEBULA -> "NEBULOSA";
            case SpectralSurveyTelescopeMenu.TARGET_KEPLER_EXOPLANET -> "EXOPLANETA";
            case SpectralSurveyTelescopeMenu.TARGET_CYGNUS_PULSAR -> "PULSAR";
            case SpectralSurveyTelescopeMenu.TARGET_SOLAR_CORONA -> "SOLAR";
            default -> "ANDRÔMEDA";
        };
    }

    private String getFilterName(int filter) {
        return switch (filter) {
            case SpectralSurveyTelescopeMenu.FILTER_INFRARED -> "Infravermelho Térmico";
            case SpectralSurveyTelescopeMenu.FILTER_RADIO_XRAY -> "Rádio & Raios-X";
            default -> "Óptico / Visível";
        };
    }
}
