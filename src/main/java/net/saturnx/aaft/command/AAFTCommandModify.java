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
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.saturnx.aaft.data.AAFTPlayerData;

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
}