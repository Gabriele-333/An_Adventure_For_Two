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
import net.saturnx.aaft.client.state.ClientXpOverlayState;


public class GuiEventRender {
    @SubscribeEvent
    public static void onXpRender(RenderGuiEvent.Pre event) {
        if (!ClientXpOverlayState.pending) return;

        GuiGraphics gfx = event.getGuiGraphics();
        Minecraft mc = Minecraft.getInstance();

        int width = mc.getWindow().getGuiScaledWidth();
        int x = width / 2 - 91;
        int y = mc.getWindow().getGuiScaledHeight() - 29;

        gfx.fill(x, y, x + 182, y + 5, 0xAAFF0000);

        int seconds = ClientXpOverlayState.remainingTicks / 20;
        gfx.drawCenteredString(mc.font,
                Component.literal(seconds + "s"),
                width / 2,
                y - 10,
                0xFF5555
        );
    }
}
