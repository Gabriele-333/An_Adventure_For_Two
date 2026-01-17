package net.saturnx.aaft.network.clientbound;/*
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
 * File created on: 09/01/2026
 */

import net.gabriele333.gabrielecore.network.ClientboundPacket;
import net.gabriele333.gabrielecore.network.CustomPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.saturnx.aaft.client.state.ClientGameState;

public record StopWaitingToastPacket() implements ClientboundPacket {

    public static final Type<StopWaitingToastPacket> TYPE =
            CustomPayload.createType("aaft_stop_waiting_toast");

    public static final StreamCodec<RegistryFriendlyByteBuf, StopWaitingToastPacket> STREAM_CODEC =
            StreamCodec.unit(new StopWaitingToastPacket());

    @Override
    public Type<StopWaitingToastPacket> type() {
        return TYPE;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleOnClient(Player player) {
        ClientGameState.stopWaiting();
    }
}