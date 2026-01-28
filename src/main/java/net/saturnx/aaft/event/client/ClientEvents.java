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
 * File created on: 17/01/2026
 */

import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.saturnx.aaft.client.AAFTKeyBindings;
import net.saturnx.aaft.client.screen.AAFTMenu;
import net.saturnx.aaft.client.screen.AAFTScreen;

@EventBusSubscriber(modid = "aaft", bus = EventBusSubscriber.Bus.GAME)
public class ClientEvents {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (AAFTKeyBindings.OPEN_MENU.consumeClick()) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player != null) {
                mc.setScreen(new AAFTScreen(
                        new AAFTMenu(0, mc.player.getInventory()),
                        mc.player.getInventory(),
                        mc.player.getDisplayName()
                ));
            }
        }
    }
}