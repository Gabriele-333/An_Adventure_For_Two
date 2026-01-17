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
 * File created on: 08/01/2026
 */


import net.gabriele333.gabrielecore.network.ClientboundPacket;
import net.gabriele333.gabrielecore.network.CustomPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.saturnx.aaft.client.state.ClientGameState;
import net.saturnx.aaft.client.InfoToast;

public record
ShowToastPacket(String titleKey, String descKey)
        implements ClientboundPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, ShowToastPacket> STREAM_CODEC = StreamCodec.ofMember(
            ShowToastPacket::write,
            ShowToastPacket::decode);

    public static final Type<ShowToastPacket> TYPE = CustomPayload.createType("aaft_show_toast");



    @Override
    public Type<ShowToastPacket> type() {
        return TYPE;
    }

    public static ShowToastPacket decode(RegistryFriendlyByteBuf buf) {
        return new ShowToastPacket(
                buf.readUtf(128),
                buf.readUtf(256)
        );
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeUtf(this.titleKey);
        buf.writeUtf(this.descKey);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleOnClient(Player player) {
        ClientGameState.startWaiting();
        var mc = Minecraft.getInstance();

        mc.execute(() -> {
            mc.getToasts().addToast(
                    new InfoToast(
                            Component.translatable(titleKey),
                            Component.translatable(descKey)
                    )
            );
        });
    }
}