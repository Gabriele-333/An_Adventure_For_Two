package net.saturnx.aaft.event.server.tick;/*
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
 * File created on: 08/02/2026
 */

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.saturnx.aaft.network.clientbound.DistanceStatusPacket;

import java.util.List;

public class DistanceTickEvent {
    private static long lastDistanceSecond = -1;

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        List<ServerPlayer> players = server.getPlayerList().getPlayers();

        if (players.size() != 2) {
            sendDistanceIfDue(server, players, -1);
            return;
        }

        ServerPlayer p1 = players.get(0);
        ServerPlayer p2 = players.get(1);

        if (p1.level() != p2.level()) {
            sendDistanceIfDue(server, players, -1);
            return;
        }

        int distance = (int) Math.round(Math.sqrt(p1.distanceToSqr(p2)));
        sendDistanceIfDue(server, players, distance);
    }

    private static void sendDistanceIfDue(MinecraftServer server, List<ServerPlayer> players, int distance) {
        long currentSecond = server.getTickCount() / 20;
        if (currentSecond == lastDistanceSecond) return;

        lastDistanceSecond = currentSecond;
        for (ServerPlayer player : players) {
            PacketDistributor.sendToPlayer(player, new DistanceStatusPacket(distance));
        }
    }
}
