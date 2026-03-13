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
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.saturnx.aaft.network.clientbound.XpRestoreStatusPacket;
import net.saturnx.aaft.server.SharedXpState;

public class ExperienceSyncHandler {

    // Flag per prevenire ricorsione
    private static final ThreadLocal<Boolean> isSyncing = ThreadLocal.withInitial(() -> false);

    @SubscribeEvent
    public static void onXpChange(PlayerXpEvent.XpChange event) {
        if (SharedXpState.isPending()) {
            if (event.getEntity() instanceof ServerPlayer player && SharedXpState.isDeadPlayer(player)) {
                event.setAmount(0);
            }
            return;
        }

        // Se stiamo gia sincronizzando, esci per evitare ricorsione.
        if (isSyncing.get()) {
            return;
        }

        var sourcePlayer = event.getEntity();
        var level = sourcePlayer.level();

        if (level.isClientSide()) return;

        var server = level.getServer();
        if (server == null) return;

        var playerList = server.getPlayerList();
        var onlinePlayers = playerList.getPlayers();

        long activePlayers = onlinePlayers.stream()
                .filter(p -> !p.isSpectator())
                .count();

        if (activePlayers == 1) {
            event.setAmount(0);
            return;
        }

        if (activePlayers != 2) {
            return;
        }

        var xpAmount = event.getAmount();
        if (xpAmount == 0) return;

        var otherPlayer = onlinePlayers.stream()
                .filter(p -> p != sourcePlayer)
                .filter(p -> !p.isSpectator())
                .findFirst()
                .orElse(null);

        if (otherPlayer == null) return;

        isSyncing.set(true);
        try {
            addExperienceSilently((ServerPlayer) otherPlayer, xpAmount);
        } finally {
            isSyncing.set(false);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        syncPendingOverlay((ServerPlayer) event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        syncPendingOverlay((ServerPlayer) event.getEntity());
    }

    private static void syncPendingOverlay(ServerPlayer player) {
        if (!SharedXpState.isPending() || !SharedXpState.isDeadPlayer(player)) {
            return;
        }

        var server = player.level().getServer();
        if (server == null) {
            return;
        }

        int remaining = (int) SharedXpState.remainingTicks(server.getTickCount());
        PacketDistributor.sendToPlayer(player, new XpRestoreStatusPacket(true, remaining));
    }

    private static void addExperienceSilently(ServerPlayer player, int xpAmount) {
        if (xpAmount > 0) {
            player.setScore(player.getScore() + xpAmount);

            int newExperience = (int) (player.experienceProgress * player.getXpNeededForNextLevel() + xpAmount);

            while (newExperience >= player.getXpNeededForNextLevel()) {
                newExperience -= player.getXpNeededForNextLevel();
                player.experienceLevel++;
            }
            player.experienceProgress = (float) newExperience / player.getXpNeededForNextLevel();
            player.totalExperience += xpAmount;
        }
    }
}
