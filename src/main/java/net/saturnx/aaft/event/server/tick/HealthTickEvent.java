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
 * File created on: 30/01/2026
 */

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.saturnx.aaft.item.AAFTItem;
import net.saturnx.aaft.server.SharedTrustState;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Collection;
import java.util.List;


public class HealthTickEvent {

    public static final ResourceLocation RING_BONUS_ID =
            ResourceLocation.fromNamespaceAndPath("aaft", "ring_bonus");


    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        ServerLevel level = server.overworld();

        List<ServerPlayer> players = level.players();

        if (players.size() != 2) {
            removeBonus(players);
            return;
        }

        ServerPlayer p1 = players.get(0);
        ServerPlayer p2 = players.get(1);

        boolean p1HasRing = hasRingEquipped(p1);
        boolean p2HasRing = hasRingEquipped(p2);

        if (p1HasRing && p2HasRing) {
            int bonusHearts = getTrustBonusHearts();
            applyBonus(p1, bonusHearts);
            applyBonus(p2, bonusHearts);
        } else {
            removeBonus(players);
        }
    }


    /* Health etc */

    public static boolean hasRingEquipped(Player player) {
        return CuriosApi.getCuriosInventory(player)
                .map(inv -> inv.findFirstCurio(AAFTItem.RING.get()).isPresent())
                .orElse(false);
    }

    private static void applyBonus(ServerPlayer player, int bonusHearts) {
        AttributeInstance attr = player.getAttribute(Attributes.MAX_HEALTH);
        if (attr == null) return;

        double bonusAmount = bonusHearts * 2.0;
        AttributeModifier existing = attr.getModifier(RING_BONUS_ID);
        if (existing != null) {
            if (existing.amount() == bonusAmount) {
                return;
            }
            attr.removeModifier(RING_BONUS_ID);
        }

        AttributeModifier modifier = new AttributeModifier(
                RING_BONUS_ID,
                bonusAmount,
                AttributeModifier.Operation.ADD_VALUE
        );
        attr.addPermanentModifier(modifier);
    }
    private static void removeBonus(
            Collection<ServerPlayer> players) {
        for (ServerPlayer player : players) {
            AttributeInstance attr = player.getAttribute(Attributes.MAX_HEALTH);
            if (attr == null) continue;

            if (attr.hasModifier(RING_BONUS_ID)) {
                attr.removeModifier(RING_BONUS_ID);

                if (player.getHealth() > player.getMaxHealth()) {
                    player.setHealth(player.getMaxHealth());
                }
            }
        }
    }

    private static int getTrustBonusHearts() {
        int tick = SharedTrustState.getHighlightedTickIndex();
        if (tick <= 2) {
            return 1;
        }
        if (tick <= 4) {
            return 2;
        }
        if (tick <= 6) {
            return 3;
        }
        if (tick <= 8) {
            return 4;
        }
        return 5;
    }
}
