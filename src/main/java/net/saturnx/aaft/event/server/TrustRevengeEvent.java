package net.saturnx.aaft.event.server;/*
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
 * File created on: 10/02/2026
 */

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.saturnx.aaft.data.AAFTPlayerData;
import net.saturnx.aaft.data.AAFTTrustRevengeData;
import net.saturnx.aaft.network.clientbound.TrustStatusPacket;
import net.saturnx.aaft.server.SharedTrustState;

import java.util.UUID;

public class TrustRevengeEvent {
    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer victim)) {
            return;
        }

        Entity killer = event.getSource().getEntity();
        if (killer == null || killer == victim) {
            return;
        }

        var server = victim.server;
        AAFTPlayerData players = AAFTPlayerData.get(server);
        if (!players.isAllowed(victim.getUUID())) {
            return;
        }

        AAFTTrustRevengeData revengeData = AAFTTrustRevengeData.get(server);
        revengeData.setLastKiller(victim.getUUID(), killer.getUUID());
    }

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            return;
        }

        Entity killerEntity = event.getSource().getEntity();
        if (!(killerEntity instanceof ServerPlayer killer)) {
            return;
        }

        var server = killer.server;
        AAFTPlayerData players = AAFTPlayerData.get(server);
        if (!players.isAllowed(killer.getUUID())) {
            return;
        }

        ServerPlayer other = server.getPlayerList().getPlayers().stream()
                .filter(p -> p != killer)
                .filter(p -> !p.isSpectator())
                .filter(p -> players.isAllowed(p.getUUID()))
                .findFirst()
                .orElse(null);

        if (other == null) {
            return;
        }

        AAFTTrustRevengeData revengeData = AAFTTrustRevengeData.get(server);
        UUID expectedKiller = revengeData.getLastKiller(other.getUUID());
        if (expectedKiller == null || !expectedKiller.equals(event.getEntity().getUUID())) {
            return;
        }

        long gameTime = server.overworld().getGameTime();
        if (!revengeData.canReward(gameTime)) {
            return;
        }
        revengeData.consumeReward(gameTime);

        int before = SharedTrustState.getTrust();
        SharedTrustState.increaseTrust(8);
        int after = SharedTrustState.getTrust();
        PacketDistributor.sendToAllPlayers(
                new TrustStatusPacket(SharedTrustState.getTrust())
        );
        int delta = after - before;
        if (delta != 0) {
            server.getPlayerList().broadcastSystemMessage(
                    Component.literal("Trust +" + delta),
                    false
            );
        }
    }
}
