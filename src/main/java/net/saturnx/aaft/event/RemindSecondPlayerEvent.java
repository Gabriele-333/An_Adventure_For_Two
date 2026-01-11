package net.saturnx.aaft.event;/*
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
 * File created on: 06/01/2026
 */

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class RemindSecondPlayerEvent {

    @SubscribeEvent
    public static void onPlayerJoinReminder(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        var server = player.getServer();

        if (server == null)
            return;

        int count = server.getPlayerList().getPlayerCount();

        if (count == 1) {
            player.sendSystemMessage(Component.literal(
                    "Serve ancora un altro giocatore per continuare!"
            ));
        }
    }
}