package net.saturnx.aaft.command;/*
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

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.saturnx.aaft.data.AAFTPlayerData;
import net.saturnx.aaft.data.AAFTTrustItemData;
import net.saturnx.aaft.network.clientbound.TrustStatusPacket;
import net.saturnx.aaft.server.SharedTrustState;

public class AAFTCommandModify {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("aaft")
                        .requires(cs -> cs.hasPermission(2)) // OP
                        .then(Commands.literal("modify")
                                .then(Commands.literal("AllowedPlayers")
                                        .then(Commands.literal("delete")
                                                .then(Commands.literal("all")
                                                        .executes(ctx ->
                                                                deleteAll(ctx.getSource())
                                                        )
                                                )
                                                .then(Commands.literal("other")
                                                        .executes(ctx ->
                                                                deleteOther(ctx.getSource())
                                                        )
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("trust")
                                .then(Commands.literal("set")
                                        .then(Commands.argument("value", IntegerArgumentType.integer(
                                                SharedTrustState.MIN_TRUST,
                                                SharedTrustState.MAX_TRUST))
                                                .executes(ctx ->
                                                        setTrust(ctx.getSource(),
                                                                IntegerArgumentType.getInteger(ctx, "value"))
                                                )
                                        )
                                )
                                .then(Commands.literal("add")
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                                .executes(ctx ->
                                                        addTrust(ctx.getSource(),
                                                                IntegerArgumentType.getInteger(ctx, "amount"))
                                                )
                                        )
                                )
                                .then(Commands.literal("remove")
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                                .executes(ctx ->
                                                        removeTrust(ctx.getSource(),
                                                                IntegerArgumentType.getInteger(ctx, "amount"))
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("tech")
                                .then(Commands.literal("trust")
                                        .then(Commands.literal("get")
                                                .executes(ctx ->
                                                        getTrust(ctx.getSource())
                                                )
                                        )
                                )
                                .then(Commands.literal("vars")
                                        .then(Commands.literal("trust_item")
                                                .executes(ctx ->
                                                        getTrustItem(ctx.getSource())
                                                )
                                        )
                                )
                        )
        );
    }


    private static int deleteAll(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        AAFTPlayerData data = AAFTPlayerData.get(server);

        data.clearAll();

        source.sendSuccess(
                () -> Component.translatable("command.aaft.modify.allowedplayers.delete.all"),
                true
        );

        return 1;
    }

    private static int deleteOther(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.translatable(

                    "command.aaft.error.player_only"));
            return 0;
        }

        MinecraftServer server = source.getServer();
        AAFTPlayerData data = AAFTPlayerData.get(server);

        data.clearOther(player.getUUID());

        source.sendSuccess(
                () -> Component.translatable("command.aaft.modify.allowedplayers.delete.other"),
                true
        );

        return 1;
    }

    private static int setTrust(CommandSourceStack source, int value) {
        int clamped = Math.max(SharedTrustState.MIN_TRUST, Math.min(SharedTrustState.MAX_TRUST, value));
        int current = SharedTrustState.getTrust();
        if (clamped != current) {
            if (clamped > current) {
                SharedTrustState.increaseTrust(clamped - current);
            } else {
                SharedTrustState.decreaseTrust(current - clamped);
            }
        }

        broadcastTrust(source.getServer());
        if (clamped != current) {
            source.getServer().getPlayerList().broadcastSystemMessage(
                    Component.literal("Trust set: " + SharedTrustState.getTrust()),
                    false
            );
        }
        source.sendSuccess(() -> Component.literal("Trust set to " + SharedTrustState.getTrust()), true);
        return 1;
    }

    private static int addTrust(CommandSourceStack source, int amount) {
        int before = SharedTrustState.getTrust();
        SharedTrustState.increaseTrust(amount);
        int after = SharedTrustState.getTrust();
        broadcastTrust(source.getServer());
        int delta = after - before;
        if (delta != 0) {
            source.getServer().getPlayerList().broadcastSystemMessage(
                    Component.literal("Trust +" + delta),
                    false
            );
        }
        source.sendSuccess(() -> Component.literal("Trust increased to " + SharedTrustState.getTrust()), true);
        return 1;
    }

    private static int removeTrust(CommandSourceStack source, int amount) {
        int before = SharedTrustState.getTrust();
        SharedTrustState.decreaseTrust(amount);
        int after = SharedTrustState.getTrust();
        broadcastTrust(source.getServer());
        int delta = after - before;
        if (delta != 0) {
            source.getServer().getPlayerList().broadcastSystemMessage(
                    Component.literal("Trust " + delta),
                    false
            );
        }
        source.sendSuccess(() -> Component.literal("Trust decreased to " + SharedTrustState.getTrust()), true);
        return 1;
    }

    private static int getTrust(CommandSourceStack source) {
        source.sendSuccess(() -> Component.literal("Trust: " + SharedTrustState.getTrust()), false);
        return 1;
    }

    private static int getTrustItem(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        AAFTTrustItemData data = AAFTTrustItemData.get(server);
        long currentTick = server.overworld().getGameTime();
        long remainingTicks = Math.max(0L, data.getCooldownEndTick() - currentTick);
        String cooldown = remainingTicks > 0L
                ? ((remainingTicks + 19L) / 20L) + "s"
                : "ready";
        source.sendSuccess(
                () -> Component.literal("trust_item: " + data.getTrustItem() + " (cooldown: " + cooldown + ")"),
                false
        );
        return 1;
    }

    private static void broadcastTrust(MinecraftServer server) {
        PacketDistributor.sendToAllPlayers(
                new TrustStatusPacket(SharedTrustState.getTrust())
        );
    }
}
