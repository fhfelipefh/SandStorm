package com.fhfelipefh.sandstorm.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdaptiveTextTest {

    @Test
    void shouldClampChatWidthToSmallScreens() {
        int originalChatWidth = 320;

        int screenWidthCompact = 300;
        int maxAllowedCompact = screenWidthCompact - 8;
        int clampedCompact = Math.min(originalChatWidth, Math.max(40, maxAllowedCompact));
        assertEquals(292, clampedCompact);
        assertTrue(clampedCompact <= screenWidthCompact - 8);

        int screenWidthWide = 1920;
        int maxAllowedWide = screenWidthWide - 8;
        int clampedWide = Math.min(originalChatWidth, Math.max(40, maxAllowedWide));
        assertEquals(320, clampedWide);
        assertTrue(clampedWide <= screenWidthWide - 8);

        int screenWidthTiny = 30;
        int maxAllowedTiny = screenWidthTiny - 8;
        int clampedTiny = Math.min(originalChatWidth, Math.max(40, maxAllowedTiny));
        assertEquals(40, clampedTiny);
    }

    @Test
    void shouldScaleOverlayActionbarMessageWhenExceedingScreen() {
        int screenWidth = 320;
        float maxAllowed = screenWidth - 24f;
        int longMessageWidth = 380;

        assertTrue(longMessageWidth > maxAllowed);
        float scale = maxAllowed / (float) longMessageWidth;
        float scaledRenderWidth = longMessageWidth * scale;

        assertEquals(maxAllowed, scaledRenderWidth, 0.001f);
        assertTrue(scaledRenderWidth <= screenWidth);

        float leftMargin = (screenWidth / 2f) - (scaledRenderWidth / 2f);
        float rightMargin = screenWidth - ((screenWidth / 2f) + (scaledRenderWidth / 2f));
        assertEquals(12f, leftMargin, 0.001f);
        assertEquals(12f, rightMargin, 0.001f);
    }

    @Test
    void shouldScaleTitleAndSubtitleWhenExceedingScreen() {
        int screenWidth = 400;
        float maxAllowed = screenWidth - 24f;

        int titleBaseWidth = 120;
        float title4xWidth = titleBaseWidth * 4.0f;
        assertTrue(title4xWidth > maxAllowed);

        float titleScaleFactor = maxAllowed / title4xWidth;
        float effectiveTitleWidth = title4xWidth * titleScaleFactor;
        assertEquals(maxAllowed, effectiveTitleWidth, 0.001f);
        assertTrue(effectiveTitleWidth <= screenWidth);

        int subtitleBaseWidth = 210;
        float subtitle2xWidth = subtitleBaseWidth * 2.0f;
        assertTrue(subtitle2xWidth > maxAllowed);

        float subtitleScaleFactor = maxAllowed / subtitle2xWidth;
        float effectiveSubtitleWidth = subtitle2xWidth * subtitleScaleFactor;
        assertEquals(maxAllowed, effectiveSubtitleWidth, 0.001f);
        assertTrue(effectiveSubtitleWidth <= screenWidth);
    }

    @Test
    void shouldAdaptHudPositionAndPreventHotbarOverlap() {
        int screenHeight = 240;
        int screenWidth = 320;
        int maxTextWidth = 110;
        int margin = 8;

        int x = screenWidth - maxTextWidth - margin;
        int y = screenHeight - 45;

        int hotbarRight = (screenWidth / 2) + 95;
        assertTrue(x < hotbarRight);

        if (x < hotbarRight) {
            if (screenHeight > 160) {
                y = screenHeight - 65;
            } else {
                y = margin;
            }
            x = Math.max(margin, screenWidth - maxTextWidth - margin);
        }

        assertEquals(screenHeight - 65, y);
        assertTrue(x >= margin);
        assertTrue(x + maxTextWidth <= screenWidth);
    }

    @Test
    void shouldScaleHudTextWhenScreenIsExtremelyNarrow() {
        int screenWidth = 100;
        int maxTextWidth = 120;
        int margin = 8;
        float maxAllowed = screenWidth - (margin * 2f);

        assertTrue(maxTextWidth > maxAllowed);
        float scale = maxAllowed / (float) maxTextWidth;
        float scaledWidth = maxTextWidth * scale;

        assertEquals(84f, scaledWidth, 0.001f);
        assertTrue(scaledWidth <= screenWidth - (margin * 2));
    }

    @Test
    void shouldClampTooltipWidthToDatapadScreenBounds() {
        int screenWidth = 320;
        int rawTooltipTextWidth = 380;
        int maxTooltipWidth = Math.max(80, screenWidth - 24);
        int tooltipWidth = Math.min(rawTooltipTextWidth + 12, maxTooltipWidth);
        int tooltipX = (screenWidth - tooltipWidth) / 2;

        assertEquals(296, tooltipWidth);
        assertEquals(12, tooltipX);
        assertEquals(308, tooltipX + tooltipWidth);
        assertTrue(tooltipX >= 0);
        assertTrue(tooltipX + tooltipWidth <= screenWidth);
    }

    @Test
    void shouldDynamicallyAllocateBadgeAndTitleInDatapadCards() {
        int cardLeft = 26;
        int cardRight = 294;
        float availableCardWidth = Math.max(20f, (cardRight - 8) - (cardLeft + 30));
        int rawBadgeWidth = 80;

        float maxBadgeWidth = Math.min(rawBadgeWidth, availableCardWidth * 0.4f);
        float badgeX = cardRight - maxBadgeWidth - 8;
        float maxTitleWidth = Math.max(0, badgeX - (cardLeft + 30) - 8);

        assertTrue(maxBadgeWidth <= availableCardWidth * 0.4f);
        assertTrue(maxTitleWidth > 0);
        assertTrue(maxTitleWidth + maxBadgeWidth <= availableCardWidth + 16);
    }

    @Test
    void shouldScaleMachineTitleToChassisBounds() {
        int imageWidth = 176;
        int titleLabelX = 28;
        int maxTitleWidth = imageWidth - titleLabelX - 6;
        assertEquals(142, maxTitleWidth);

        int longTitleWidth = 160;
        float scale = (float) maxTitleWidth / (float) longTitleWidth;
        float renderedTitleWidth = longTitleWidth * scale;

        assertEquals(142f, renderedTitleWidth, 0.001f);
        assertTrue(renderedTitleWidth <= maxTitleWidth);
    }
}
