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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.saturnx.aaft.data.AAFTPlayerData;
import net.saturnx.aaft.data.AAFTTrustItemData;
import net.saturnx.aaft.network.clientbound.TrustStatusPacket;
import net.saturnx.aaft.server.SharedTrustState;

public class TrustItemExchangeEvent {
    @SubscribeEvent
    public static void onItemPickup(ItemEntityPickupEvent.Post event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) {
            return;
        }

        ItemEntity itemEntity = event.getItemEntity();
        Entity owner = itemEntity.getOwner();
        if (!(owner instanceof ServerPlayer ownerPlayer)) {
            return;
        }

        if (ownerPlayer == player) {
            return;
        }

        var server = player.level().getServer();
        if (server == null) {
            return;
        }

        AAFTPlayerData players = AAFTPlayerData.get(server);
        if (!players.isAllowed(player.getUUID()) || !players.isAllowed(ownerPlayer.getUUID())) {
            return;
        }

        long gameTime = server.overworld().getGameTime();
        AAFTTrustItemData trustData = AAFTTrustItemData.get(server);
        int earned = trustData.registerExchange(gameTime);
        if (earned > 0) {
            int before = SharedTrustState.getTrust();
            SharedTrustState.increaseTrust(earned * 5);
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
}
