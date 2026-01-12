package net.saturnx.aaft.data;/*
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

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.UUID;

public class AAFTPlayerData extends SavedData {

    private UUID player1;
    private UUID player2;



    public static AAFTPlayerData get(MinecraftServer server) {
        return server.overworld()
                .getDataStorage()
                .computeIfAbsent(FACTORY, "aaft_players");
    }


    public boolean isAllowed(UUID uuid) {
        return uuid != null && (uuid.equals(player1) || uuid.equals(player2));
    }

    public boolean hasFreeSlot() {
        return player1 == null || player2 == null;
    }

    public void addPlayer(UUID uuid) {
        if (player1 == null) player1 = uuid;
        else if (player2 == null) player2 = uuid;
        setDirty();
    }

    public void clearAll() {
        player1 = null;
        player2 = null;
        setDirty();
    }

    public void clearOther(UUID executor) {
        if (player1 != null && !player1.equals(executor)) {
            player1 = null;
        } else if (player2 != null && !player2.equals(executor)) {
            player2 = null;
        }
        setDirty();
    }


    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        if (player1 != null) tag.putUUID("p1", player1);
        if (player2 != null) tag.putUUID("p2", player2);
        return tag;
    }

    public static AAFTPlayerData load(CompoundTag tag, HolderLookup.Provider registries) {
        AAFTPlayerData data = new AAFTPlayerData();
        if (tag.hasUUID("p1")) data.player1 = tag.getUUID("p1");
        if (tag.hasUUID("p2")) data.player2 = tag.getUUID("p2");
        return data;
    }


    public static final SavedData.Factory<AAFTPlayerData> FACTORY =
            new SavedData.Factory<>(
                    AAFTPlayerData::new,
                    AAFTPlayerData::load
            );
}