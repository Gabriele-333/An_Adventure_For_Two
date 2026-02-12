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
 * File created on: 03/02/2026
 */

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.saturnx.aaft.damage.AAFTDamageTypes;
import net.saturnx.aaft.effect.AAFTEffects;
import net.saturnx.aaft.server.SharedTrustState;

import java.util.Collection;
import java.util.List;

public class LontananzaTickEvent {

    private static final double BASE_EFFECT_DISTANCE = 50.0;
    private static final double BASE_DAMAGE_DISTANCE = 75.0;
    private static final double STEP_DISTANCE = 20.0;
    private static final double MAX_EFFECT_DISTANCE = 900.0;
    private static final double MAX_DAMAGE_DISTANCE = 1000.0;
    private static final int DAMAGE_INTERVAL_TICKS = 20;
    private static final float DAMAGE_AMOUNT = 1.0F;

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        List<ServerPlayer> players = server.getPlayerList().getPlayers();

        if (players.size() != 2) {
            removeEffect(players);
            return;
        }

        ServerPlayer p1 = players.get(0);
        ServerPlayer p2 = players.get(1);

        double distanceSqr = getDistanceSqr(p1, p2);

        double effectDistance = getEffectDistance(SharedTrustState.getHighlightedTickIndex());
        double damageDistance = getDamageDistance(SharedTrustState.getHighlightedTickIndex());
        double effectDistanceSqr = effectDistance * effectDistance;
        double damageDistanceSqr = damageDistance * damageDistance;

        if (distanceSqr > effectDistanceSqr) {
            applyEffect(p1);
            applyEffect(p2);
        } else {
            removeEffect(players);
        }

        if (distanceSqr > damageDistanceSqr && (server.getTickCount() % DAMAGE_INTERVAL_TICKS == 0)) {
            damagePlayer(p1);
            damagePlayer(p2);
        }
    }

    private static double getDistanceSqr(ServerPlayer p1, ServerPlayer p2) {
        if (p1.level() != p2.level()) {
            return Double.MAX_VALUE;
        }
        return p1.distanceToSqr(p2);
    }

    private static void applyEffect(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(AAFTEffects.LONTANANZA, 60, 0, true, true, true));
    }

    private static void removeEffect(Collection<ServerPlayer> players) {
        for (ServerPlayer player : players) {
            player.removeEffect(AAFTEffects.LONTANANZA);
        }
    }

    private static void damagePlayer(ServerPlayer player) {
        if (player.isCreative() || player.isSpectator()) {
            return;
        }
        DamageSource source = player.level().damageSources().source(AAFTDamageTypes.LONTANANZA);
        player.hurt(source, DAMAGE_AMOUNT);
    }

    private static double getEffectDistance(int trustTick) {
        if (trustTick >= 9) {
            return MAX_EFFECT_DISTANCE;
        }
        if (trustTick <= 2) {
            return BASE_EFFECT_DISTANCE;
        }
        return BASE_EFFECT_DISTANCE + (trustTick - 2) * STEP_DISTANCE;
    }

    private static double getDamageDistance(int trustTick) {
        if (trustTick >= 9) {
            return MAX_DAMAGE_DISTANCE;
        }
        if (trustTick <= 2) {
            return BASE_DAMAGE_DISTANCE;
        }
        return BASE_DAMAGE_DISTANCE + (trustTick - 2) * STEP_DISTANCE;
    }
}
