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
 * File created on: 12/01/2026
 */

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.saturnx.aaft.network.clientbound.XpRestoreStatusPacket;
import net.saturnx.aaft.server.SharedXpState;

public class XpTickEvent {



    private static long lastPacketSecond = -1;

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (!SharedXpState.isPending()) return;

        MinecraftServer server = event.getServer();
        long now = server.getTickCount();

        ServerPlayer dead = null;
        ServerPlayer alive = null;

        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            if (SharedXpState.isDeadPlayer(p)) dead = p;
            else if (SharedXpState.isAlivePlayer(p)) alive = p;
        }

        if (dead == null || alive == null) {
            reset(server);
            return;
        }

        if (!alive.isAlive()) {
            SharedXpState.resetAllXp(server);
            reset(server);
            return;
        }


        long remaining = SharedXpState.remainingTicks(now);


        long currentSecond = remaining / 20;
        if (currentSecond != lastPacketSecond) {
            lastPacketSecond = currentSecond;

            PacketDistributor.sendToPlayer(
                    dead,
                    new XpRestoreStatusPacket(true, (int) remaining)
            );
        }

        if (remaining > 0) return;


        copyXp(alive, dead);
        reset(server);
    }


    /* ---------------- RESET ---------------- */

    private static void reset(MinecraftServer server) {
        // spegne UI per sicurezza a tutti
        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            PacketDistributor.sendToPlayer(
                    p,
                    new XpRestoreStatusPacket(false, 0)
            );
        }

        SharedXpState.clear();
        lastPacketSecond = -1;
    }

    /* ---------------- XP ---------------- */

    private static void copyXp(ServerPlayer from, ServerPlayer to) {
        to.totalExperience = from.totalExperience;
        to.experienceLevel = from.experienceLevel;
        to.experienceProgress = from.experienceProgress;
    }


}