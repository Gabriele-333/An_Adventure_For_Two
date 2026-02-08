package net.saturnx.aaft.item;/*
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

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CompassItem extends Item {
    public static final int COOLDOWN_TICKS = 20 * 30;
    public static final String COOLDOWN_TAG = "aaft_compass_cooldown";

    public CompassItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) return InteractionResultHolder.pass(stack);

        if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResultHolder.pass(stack);

        if (getStoredCooldown(serverPlayer) > 0) return InteractionResultHolder.fail(stack);

        ServerPlayer other = findOtherPlayer(serverPlayer);
        if (other == null) return InteractionResultHolder.fail(stack);

        ServerLevel targetLevel = other.serverLevel();
        serverPlayer.teleportTo(
                targetLevel,
                other.getX(),
                other.getY(),
                other.getZ(),
                other.getYRot(),
                other.getXRot()
        );

        setStoredCooldown(serverPlayer, COOLDOWN_TICKS);
        serverPlayer.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

        return InteractionResultHolder.success(stack);
    }

    public static int getStoredCooldown(ServerPlayer player) {
        return player.getPersistentData().getInt(COOLDOWN_TAG);
    }

    public static void setStoredCooldown(ServerPlayer player, int ticks) {
        if (ticks <= 0) {
            player.getPersistentData().remove(COOLDOWN_TAG);
        } else {
            player.getPersistentData().putInt(COOLDOWN_TAG, ticks);
        }
    }

    private static ServerPlayer findOtherPlayer(ServerPlayer player) {
        var server = player.getServer();
        if (server == null) return null;

        for (ServerPlayer other : server.getPlayerList().getPlayers()) {
            if (!other.getUUID().equals(player.getUUID())) return other;
        }

        return null;
    }
}
