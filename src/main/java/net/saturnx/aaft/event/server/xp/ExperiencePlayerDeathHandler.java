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
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.saturnx.aaft.network.clientbound.XpRestoreStatusPacket;
import net.saturnx.aaft.server.SharedXpState;

public class
ExperiencePlayerDeathHandler {
    @SubscribeEvent
    public static void onPlayerXpDrop(LivingExperienceDropEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer)) {
            return;
        }
        event.setDroppedExperience(0);
    }

    @SubscribeEvent
    public static void onPlayerDeath(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer dead)) return;

        var server = dead.level().getServer();
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
    public static void onPendingAliveDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer alive)) return;
        if (!SharedXpState.isPending() || !SharedXpState.isAlivePlayer(alive)) return;

        var server = alive.level().getServer();
        if (server == null) return;

        SharedXpState.resetAllXp(server);
        SharedXpState.clear();

        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            PacketDistributor.sendToPlayer(p, new XpRestoreStatusPacket(false, 0));
        }
    }

}
