package net.saturnx.aaft.network;/*
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
import net.gabriele333.gabrielecore.network.ServerboundPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.saturnx.aaft.network.clientbound.ShowToastPacket;
import net.saturnx.aaft.network.clientbound.StopWaitingToastPacket;
import net.saturnx.aaft.network.clientbound.DistanceStatusPacket;
import net.saturnx.aaft.network.clientbound.XpRestoreStatusPacket;


import static net.saturnx.aaft.AAFT.LOGGER;
import static net.saturnx.aaft.AAFT.MOD_ID;

public class aaftNetwork {
    public static void init(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(MOD_ID);

        // Clientbound
        clientbound(registrar, ShowToastPacket.TYPE, ShowToastPacket.STREAM_CODEC);
        clientbound(registrar, StopWaitingToastPacket.TYPE, StopWaitingToastPacket.STREAM_CODEC);
        clientbound(registrar, XpRestoreStatusPacket.TYPE, XpRestoreStatusPacket.STREAM_CODEC);
        clientbound(registrar, DistanceStatusPacket.TYPE, DistanceStatusPacket.STREAM_CODEC);



        // Serverbound

    }

    private static <T extends ClientboundPacket> void clientbound(PayloadRegistrar registrar,
                                                                  CustomPacketPayload.Type<T> type,
                                                                  StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        registrar.playToClient(type, codec, ClientboundPacket::handleOnClient);
        LOGGER.debug("Registered clientbound packet: {}", type.id());
    }

    private static <T extends ServerboundPacket> void serverbound(PayloadRegistrar registrar,
                                                                  CustomPacketPayload.Type<T> type,
                                                                  StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        registrar.playToServer(type, codec, ServerboundPacket::handleOnServer);
        LOGGER.debug("Registered serverbound packet: {}", type.id());
    }

}
