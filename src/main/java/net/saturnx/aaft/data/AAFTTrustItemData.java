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
 * File created on: 10/02/2026
 */

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

public class AAFTTrustItemData extends SavedData {
    public static final int COOLDOWN_TICKS = 45 * 20;

    private static final String DATA_ID = "aaft_trust_item";
    private static final String KEY_TRUST_ITEM = "trust_item";
    private static final String KEY_COOLDOWN_END = "trust_item_cooldown_end";

    private int trustItem;
    private long cooldownEndTick;

    public static AAFTTrustItemData get(MinecraftServer server) {
        return server.overworld()
                .getDataStorage()
                .computeIfAbsent(FACTORY, DATA_ID);
    }

    public int getTrustItem() {
        return trustItem;
    }

    public long getCooldownEndTick() {
        return cooldownEndTick;
    }

    public int registerExchange(long currentTick) {
        if (currentTick < cooldownEndTick) {
            return 0;
        }
        trustItem++;
        cooldownEndTick = currentTick + COOLDOWN_TICKS;
        setDirty();

        int earned = trustItem / 10;
        if (earned > 0) {
            trustItem = trustItem % 10;
            setDirty();
        }
        return earned;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt(KEY_TRUST_ITEM, trustItem);
        tag.putLong(KEY_COOLDOWN_END, cooldownEndTick);
        return tag;
    }

    public static AAFTTrustItemData load(CompoundTag tag, HolderLookup.Provider registries) {
        AAFTTrustItemData data = new AAFTTrustItemData();
        if (tag.contains(KEY_TRUST_ITEM)) {
            data.trustItem = tag.getInt(KEY_TRUST_ITEM);
        }
        if (tag.contains(KEY_COOLDOWN_END)) {
            data.cooldownEndTick = tag.getLong(KEY_COOLDOWN_END);
        }
        return data;
    }

    public static final SavedData.Factory<AAFTTrustItemData> FACTORY =
            new SavedData.Factory<>(
                    AAFTTrustItemData::new,
                    AAFTTrustItemData::load
            );
}
