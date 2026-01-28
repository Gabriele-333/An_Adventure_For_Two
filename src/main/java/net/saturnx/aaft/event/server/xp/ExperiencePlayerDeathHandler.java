package net.saturnx.aaft.event.server.xp;/*
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

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.saturnx.aaft.network.clientbound.XpRestoreStatusPacket;
import net.saturnx.aaft.server.SharedXpState;

public class
ExperiencePlayerDeathHandler {
    @SubscribeEvent
    public static void onPlayerDeath(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer dead)) return;

        var server = dead.getServer();
        if (server == null) return;

        var players = server.getPlayerList().getPlayers()
                .stream()
                .filter(p -> !p.isSpectator())
                .toList();

        if (players.size() != 2) return;

        ServerPlayer other = players.stream()
                .filter(p -> p != dead)
                .findFirst()
                .orElse(null);

        if (other == null) return;

        SharedXpState.start(dead, other, server.getTickCount());
        PacketDistributor.sendToPlayer(
                dead,
                new XpRestoreStatusPacket(true, 30 * 20)
        );
    }

    @SubscribeEvent
    public static void onSecondDeath(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer p)) return;

        if (!SharedXpState.isPending()) return;
        if (!SharedXpState.isAlivePlayer(p)) return;

        ServerPlayer dead = SharedXpState.isDeadPlayer(p) ? null : p;
        ServerPlayer other = SharedXpState.isDeadPlayer(p) ? p : null;

        if (dead == null || other == null) return;

        dead.setExperiencePoints(0);
        other.setExperiencePoints(0);

        SharedXpState.clear();
        PacketDistributor.sendToPlayer(
                dead,
                new XpRestoreStatusPacket(false, 0)
        );
    }

}
