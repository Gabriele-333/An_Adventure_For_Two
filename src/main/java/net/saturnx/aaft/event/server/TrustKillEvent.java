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

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.saturnx.aaft.data.AAFTPlayerData;
import net.saturnx.aaft.network.clientbound.TrustStatusPacket;
import net.saturnx.aaft.server.SharedTrustState;

public class TrustKillEvent {
    @SubscribeEvent
    public static void onPlayerKilled(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer dead)) {
            return;
        }

        if (!(event.getSource().getEntity() instanceof ServerPlayer killer)) {
            return;
        }

        if (killer == dead) {
            return;
        }

        AAFTPlayerData players = AAFTPlayerData.get(dead.server);
        if (!players.isAllowed(killer.getUUID()) || !players.isAllowed(dead.getUUID())) {
            return;
        }

        int before = SharedTrustState.getTrust();
        SharedTrustState.decreaseTrust(20);
        int after = SharedTrustState.getTrust();
        PacketDistributor.sendToAllPlayers(
                new TrustStatusPacket(SharedTrustState.getTrust())
        );
        int delta = after - before;
        if (delta != 0) {
            dead.server.getPlayerList().broadcastSystemMessage(
                    Component.literal("Trust " + delta),
                    false
            );
        }
    }
}
