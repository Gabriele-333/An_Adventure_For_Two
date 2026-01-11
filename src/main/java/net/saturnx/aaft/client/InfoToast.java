package net.saturnx.aaft.client;/*
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
 * File created on: 08/01/2026
 */

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class InfoToast implements Toast {

    private static final ResourceLocation BACKGROUND =
            ResourceLocation.withDefaultNamespace("toast/system");

    private final Component title;
    private final Component description;
    private long startTime = -1L;

    public InfoToast(Component title, Component description) {
        this.title = title;
        this.description = description;
    }

    private static int lerpColor(int from, int to, float t) {
        int fr = (from >> 16) & 0xFF;
        int fg = (from >> 8) & 0xFF;
        int fb = from & 0xFF;

        int tr = (to >> 16) & 0xFF;
        int tg = (to >> 8) & 0xFF;
        int tb = to & 0xFF;

        int r = (int) (fr + (tr - fr) * t);
        int g = (int) (fg + (tg - fg) * t);
        int b = (int) (fb + (tb - fb) * t);

        return (r << 16) | (g << 8) | b;
    }

    @Override
    public @NotNull Visibility render(
            @NotNull GuiGraphics gfx,
            @NotNull ToastComponent toastComponent,
            long time
    ) {
        gfx.blitSprite(BACKGROUND, 0, 0, 160, 32);

        var font = toastComponent.getMinecraft().font;

        float speed = 400f;
        float t = (float) ((Math.sin(time / speed) + 1.0) / 2.0);
        int textColor = lerpColor(0xFFFFFF, 0xFF5555, t);

        gfx.drawString(font, title, 22, 7, textColor);
        gfx.drawString(font, description, 22, 18, textColor);

        return ClientGameState.isWaiting()
                ? Visibility.SHOW
                : Visibility.HIDE;
    }
}