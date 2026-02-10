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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.saturnx.aaft.data.AAFTPlayerData;
import net.saturnx.aaft.network.clientbound.TrustStatusPacket;
import net.saturnx.aaft.server.SharedTrustState;

import java.util.List;

public class TrustDragonKillEvent {
    @SubscribeEvent
    public static void onDragonKilled(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof EnderDragon)) {
            return;
        }

        if (!(event.getEntity().level() instanceof ServerLevel level)) {
            return;
        }

        if (!level.dimension().equals(Level.END)) {
            return;
        }

        var server = level.getServer();
        AAFTPlayerData playersData = AAFTPlayerData.get(server);
        List<ServerPlayer> players = server.getPlayerList().getPlayers()
                .stream()
                .filter(p -> !p.isSpectator())
                .filter(p -> p.level().dimension().equals(Level.END))
                .filter(p -> playersData.isAllowed(p.getUUID()))
                .toList();

        if (players.size() != 2) {
            return;
        }

        int before = SharedTrustState.getTrust();
        SharedTrustState.increaseTrust(24);
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
