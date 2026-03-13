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
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.saturnx.aaft.data.AAFTPlayerData;
import net.saturnx.aaft.data.AAFTTrustHitData;
import net.saturnx.aaft.network.clientbound.TrustStatusPacket;
import net.saturnx.aaft.server.SharedTrustState;

public class TrustHitEvent {
    @SubscribeEvent
    public static void onPlayerHit(LivingDamageEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer target)) {
            return;
        }

        if (event.getNewDamage() <= 0f) {
            return;
        }

        if (!(event.getSource().getEntity() instanceof ServerPlayer attacker)) {
            return;
        }

        if (attacker == target) {
            return;
        }

        var server = target.level().getServer();
        if (server == null) {
            return;
        }

        AAFTPlayerData players = AAFTPlayerData.get(server);
        if (!players.isAllowed(attacker.getUUID()) || !players.isAllowed(target.getUUID())) {
            return;
        }

        long gameTime = server.overworld().getGameTime();
        AAFTTrustHitData hitData = AAFTTrustHitData.get(server);
        int earned = hitData.registerHit(gameTime);
        if (earned > 0) {
            int before = SharedTrustState.getTrust();
            SharedTrustState.decreaseTrust(earned * 10);
            int after = SharedTrustState.getTrust();
            PacketDistributor.sendToAllPlayers(
                    new TrustStatusPacket(SharedTrustState.getTrust())
            );
            int delta = after - before;
            if (delta != 0) {
                server.getPlayerList().broadcastSystemMessage(
                        Component.literal("Trust " + delta),
                        false
                );
            }
        }
    }
}
