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
 * File created on: 08/02/2026
 */

import net.gabriele333.gabrielecore.network.ClientboundPacket;
import net.gabriele333.gabrielecore.network.CustomPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.saturnx.aaft.client.state.ClientXpOverlayState;

public record DistanceStatusPacket(
        int distanceBlocks) implements ClientboundPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, DistanceStatusPacket> STREAM_CODEC = StreamCodec.ofMember(
            DistanceStatusPacket::write,
            DistanceStatusPacket::decode);

    public static final Type<DistanceStatusPacket> TYPE = CustomPayload.createType("aaft_distance_status");

    @Override
    public Type<DistanceStatusPacket> type() {
        return TYPE;
    }

    public static DistanceStatusPacket decode(RegistryFriendlyByteBuf buf) {
        return new DistanceStatusPacket(
                buf.readInt()
        );
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeInt(this.distanceBlocks);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleOnClient(Player player) {
        ClientXpOverlayState.distanceBlocks = distanceBlocks;
    }
}
