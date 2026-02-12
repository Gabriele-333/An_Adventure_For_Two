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
 * File created on: 06/01/2026
 */


import net.gabriele333.gabrielecore.network.ClientboundPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.saturnx.aaft.data.AAFTPlayerData;
import net.saturnx.aaft.network.clientbound.ShowToastPacket;
import net.saturnx.aaft.network.clientbound.StopWaitingToastPacket;
import net.saturnx.aaft.network.clientbound.TrustStatusPacket;
import net.saturnx.aaft.server.SharedTrustState;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.UUID;


public class PlayerJoinHandler {
    private static final String FIRST_BOOK_KEY = "aaft_received_guide_book";
    private static final ResourceLocation GUIDE_BOOK_ID =
            ResourceLocation.fromNamespaceAndPath("aaft", "adventure_for_two");


    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        var server = event.getEntity().getServer();
        if (server == null) return;

        updatePlayersState(server);
    }

    @SubscribeEvent
    public static void onPlayerDisconnect(PlayerEvent.PlayerLoggedOutEvent event) {
        var server = event.getEntity().getServer();
        if (server == null) return;

        ServerPlayer leaving = (ServerPlayer) event.getEntity();

        server.execute(() -> {
            var players = server.getPlayerList().getPlayers();
            long remainingCount = players.stream()
                    .filter(p -> p != leaving)
                    .count();

            if (remainingCount == 1) {
                ServerPlayer remaining = players.stream()
                        .filter(p -> p != leaving)
                        .findFirst()
                        .orElse(null);

                if (remaining == null) return;

                giveBlindness(remaining);

                    remaining.setGameMode(GameType.ADVENTURE);





                ClientboundPacket toast = new ShowToastPacket(
                        "toast.aaft.title",
                        "toast.aaft.waiting_second"
                );
                PacketDistributor.sendToPlayer(remaining, toast);
            }
        });
    }

    private static void updatePlayersState(MinecraftServer server) {
        var players = server.getPlayerList().getPlayers();
        int count = players.size();

        if (count == 1) {
            ServerPlayer p = players.getFirst();

            giveBlindness(p);
            p.setGameMode(GameType.ADVENTURE);


            if(SingleplayerWorldTracker.worldWasSingleplayer){
                ClientboundPacket toast = new ShowToastPacket(
                        "toast.aaft.title",
                        "toast.aaft.need_second"
                );

                PacketDistributor.sendToPlayer(p, toast);
            } else {
                ClientboundPacket toast = new ShowToastPacket(
                        "toast.aaft.title",
                        "toast.aaft.waiting_second"
                );

                PacketDistributor.sendToPlayer(p, toast);
            }

            return;
        }

        if (count >= 2) {
            for (ServerPlayer p : players) {
                removeBlindness(p);
                p.setGameMode(GameType.SURVIVAL);
                ClientboundPacket stopToast = new StopWaitingToastPacket();
                PacketDistributor.sendToPlayer(p, stopToast);
            }
        }

        PacketDistributor.sendToAllPlayers(
                new TrustStatusPacket(SharedTrustState.getTrust())
        );
    }

    private static void giveBlindness(Player player) {
        player.addEffect(new MobEffectInstance(
                MobEffects.BLINDNESS,
                1000000,
                0,
                true,
                false
        ));
    }


    private static void removeBlindness(Player player) {
        player.removeEffect(MobEffects.BLINDNESS);
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        var server = player.getServer();
        if (server == null) return;

        if (server.getPlayerList().getPlayerCount() > 2) {
            player.connection.disconnect(
                    Component.translatable("message.aaft.reject_join")
            );
            return;
        } else if(server.getPlayerList().getPlayerCount() < 3){
            AAFTPlayerData data = AAFTPlayerData.get(server);
            UUID uuid = player.getUUID();

            if (data.isAllowed(uuid)) {
                giveGuideBookOnce(player);
                return;
            }

            if (data.hasFreeSlot()) {
                data.addPlayer(uuid);
                giveGuideBookOnce(player);
                return;
            }

            player.connection.disconnect(
                    Component.translatable("message.aaft.not_allowed")
            );

        }
    }

    private static void giveGuideBookOnce(ServerPlayer player) {
        var persistent = player.getPersistentData();
        if (persistent.getBoolean(FIRST_BOOK_KEY)) {
            return;
        }

        ItemStack guideBook = PatchouliAPI.get().getBookStack(GUIDE_BOOK_ID);
        if (guideBook.isEmpty()) {
            return;
        }

        if (!player.getInventory().add(guideBook)) {
            player.drop(guideBook, false);
        }

        persistent.putBoolean(FIRST_BOOK_KEY, true);
    }
}
