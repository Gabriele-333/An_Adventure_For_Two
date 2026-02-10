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
 * File created on: 10/02/2026
 */

import net.gabriele333.gabrielecore.network.ClientboundPacket;
import net.gabriele333.gabrielecore.network.CustomPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.saturnx.aaft.client.state.ClientTrustOverlayState;

public record TrustStatusPacket(
        int trust) implements ClientboundPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, TrustStatusPacket> STREAM_CODEC = StreamCodec.ofMember(
            TrustStatusPacket::write,
            TrustStatusPacket::decode);

    public static final Type<TrustStatusPacket> TYPE = CustomPayload.createType("aaft_trust_status");

    @Override
    public Type<TrustStatusPacket> type() {
        return TYPE;
    }

    public static TrustStatusPacket decode(RegistryFriendlyByteBuf buf) {
        return new TrustStatusPacket(
                buf.readInt()
        );
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeInt(this.trust);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleOnClient(Player player) {
        ClientTrustOverlayState.setTrust(trust);
    }
}
