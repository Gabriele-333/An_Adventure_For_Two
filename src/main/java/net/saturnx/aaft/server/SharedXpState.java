package net.saturnx.aaft.server;/*
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
import net.saturnx.aaft.config.AAFTServerConfig;

public final class SharedXpState {

    private static ServerPlayer deadPlayer;
    private static ServerPlayer alivePlayer;
    private static long restoreDeadline; // gameTime
    private static boolean pending;

    public static void start(ServerPlayer dead, ServerPlayer alive, long gameTime) {
        deadPlayer = dead;
        alivePlayer = alive;
        restoreDeadline = gameTime + (30 * 20);
        pending = true;
    }

    public static boolean isPending() {
        return pending;
    }

    public static boolean isDeadPlayer(ServerPlayer p) {
        return pending && p == deadPlayer;
    }

    public static boolean isAlivePlayer(ServerPlayer p) {
        return pending && p == alivePlayer;
    }

    public static long remainingTicks(long gameTime) {
        return Math.max(0, restoreDeadline - gameTime);
    }

    public static void clear() {
        deadPlayer = null;
        alivePlayer = null;
        pending = false;
    }



    public static boolean areBothDead(MinecraftServer server) {
        return server.getPlayerList().getPlayers().stream()
                .filter(p -> !p.isSpectator())
                .noneMatch(ServerPlayer::isAlive);
    }

    public static void resetAllXp(MinecraftServer server) {
        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            p.totalExperience = 0;
            p.experienceLevel = 0;
            p.experienceProgress = 0f;
        }


    }
}