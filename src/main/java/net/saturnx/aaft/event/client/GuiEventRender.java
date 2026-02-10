package net.saturnx.aaft.event.client;/*
 * This file is part of An Adventure For Two.
 * Copyright (c) 2026, SaturnX Studios, All rights reserved.
 *
 * An Adventure For Two is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * An Adventure For Two is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with An Adventure For Two.  If not, see <http://www.gnu.org/licenses/lgpl>.
 *
 * File created on: 12/01/2026
 */

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.saturnx.aaft.client.state.ClientTrustOverlayState;
import net.saturnx.aaft.client.state.ClientXpOverlayState;


public class GuiEventRender {
    @SubscribeEvent
    public static void onXpRender(RenderGuiEvent.Pre event) {
        GuiGraphics gfx = event.getGuiGraphics();
        Minecraft mc = Minecraft.getInstance();

        int width = mc.getWindow().getGuiScaledWidth();
        int x = width / 2 - 91;
        int y = mc.getWindow().getGuiScaledHeight() - 29;

        int distance = ClientXpOverlayState.distanceBlocks;
        int baseTextY = y - 10;

        if (ClientXpOverlayState.pending) {
            gfx.fill(x, y, x + 182, y + 5, 0xAAFF0000);

            int seconds = ClientXpOverlayState.remainingTicks / 20;
            gfx.drawCenteredString(mc.font,
                    Component.literal(seconds + "s"),
                    width / 2,
                    baseTextY,
                    0xFF5555
            );
            baseTextY -= 10;
        }



        if (distance >= 0) {
            int distanceTextX = width / 2 - 110;
            if (mc.player != null && !mc.player.getOffhandItem().isEmpty()) {
                distanceTextX -= 24;
            }
            gfx.drawCenteredString(mc.font,
                    Component.literal(distance + " m"),
                    distanceTextX,
                    baseTextY + 24,
                    0xFFFFFF
            );
        }

        renderTrustBar(gfx, mc);
    }

    private static void renderTrustBar(GuiGraphics gfx, Minecraft mc) {
        int guiHeight = mc.getWindow().getGuiScaledHeight();
        int x = 12;
        int y = guiHeight - 14;
        int barWidth = 96;
        int barHeight = 4;

        int min = ClientTrustOverlayState.MIN_TRUST;
        int max = ClientTrustOverlayState.MAX_TRUST;
        int trust = ClientTrustOverlayState.getTrust();
        int clamped = Math.max(min, Math.min(max, trust));

        gfx.fill(x, y, x + barWidth, y + barHeight, 0xAA1D1B1A);
        gfx.fill(x - 1, y - 1, x + barWidth + 1, y, 0xFF4A4543);
        gfx.fill(x - 1, y + barHeight, x + barWidth + 1, y + barHeight + 1, 0xFF4A4543);
        gfx.fill(x - 1, y, x, y + barHeight, 0xFF4A4543);
        gfx.fill(x + barWidth, y, x + barWidth + 1, y + barHeight, 0xFF4A4543);

        int range = Math.max(1, max - min);
        int pointerX = x + (barWidth * (clamped - min)) / range;
        gfx.fill(pointerX - 1, y - 7, pointerX + 2, y - 5, 0xFFE6E1D8);
        gfx.fill(pointerX, y - 5, pointerX + 1, y - 2, 0xFFE6E1D8);

        int ticks = 8;
        int marginValue = 8;
        int effectiveMin = Math.min(max, min + marginValue);
        int effectiveMax = Math.max(min, max - marginValue);
        int effectiveRange = Math.max(1, effectiveMax - effectiveMin);

        int[] tickPositions = new int[ticks + 1];
        for (int i = 0; i <= ticks; i++) {
            int value = effectiveMin + (effectiveRange * i) / ticks;
            tickPositions[i] = x + (barWidth * (value - min)) / range;
        }

        int mid = min + (max - min) / 2;
        int highlightIndex = 0;
        if (clamped > mid) {
            for (int i = 0; i < tickPositions.length; i++) {
                if (tickPositions[i] <= pointerX) {
                    highlightIndex = i;
                }
            }
        } else if (clamped < mid) {
            highlightIndex = tickPositions.length - 1;
            for (int i = 0; i < tickPositions.length; i++) {
                if (tickPositions[i] >= pointerX) {
                    highlightIndex = i;
                    break;
                }
            }
        } else {
            int closest = Integer.MAX_VALUE;
            for (int i = 0; i < tickPositions.length; i++) {
                int distance = Math.abs(tickPositions[i] - pointerX);
                if (distance < closest) {
                    closest = distance;
                    highlightIndex = i;
                }
            }
        }

        for (int i = 0; i < tickPositions.length; i++) {
            int tickX = tickPositions[i];
            int tickHeight = (i == ticks / 2) ? 5 : 3;
            int color = (i == highlightIndex) ? 0xFFFFFFFF : 0xFF7A716D;
            gfx.fill(tickX, y - tickHeight, tickX + 1, y, color);
        }
    }
}
