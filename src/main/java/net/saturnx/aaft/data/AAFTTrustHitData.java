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

public class AAFTTrustHitData extends SavedData {
    public static final int COOLDOWN_TICKS = 20 * 20;

    private static final String DATA_ID = "aaft_trust_hit";
    private static final String KEY_TRUST_HIT = "trust_hit";
    private static final String KEY_COOLDOWN_END = "trust_hit_cooldown_end";

    private int trustHit;
    private long cooldownEndTick;

    public static AAFTTrustHitData get(MinecraftServer server) {
        return server.overworld()
                .getDataStorage()
                .computeIfAbsent(FACTORY, DATA_ID);
    }

    public int getTrustHit() {
        return trustHit;
    }

    public long getCooldownEndTick() {
        return cooldownEndTick;
    }

    public int registerHit(long currentTick) {
        if (currentTick < cooldownEndTick) {
            return 0;
        }
        trustHit++;
        cooldownEndTick = currentTick + COOLDOWN_TICKS;
        setDirty();

        int earned = trustHit / 5;
        if (earned > 0) {
            trustHit = trustHit % 5;
            setDirty();
        }
        return earned;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt(KEY_TRUST_HIT, trustHit);
        tag.putLong(KEY_COOLDOWN_END, cooldownEndTick);
        return tag;
    }

    public static AAFTTrustHitData load(CompoundTag tag, HolderLookup.Provider registries) {
        AAFTTrustHitData data = new AAFTTrustHitData();
        if (tag.contains(KEY_TRUST_HIT)) {
            data.trustHit = tag.getInt(KEY_TRUST_HIT);
        }
        if (tag.contains(KEY_COOLDOWN_END)) {
            data.cooldownEndTick = tag.getLong(KEY_COOLDOWN_END);
        }
        return data;
    }

    public static final SavedData.Factory<AAFTTrustHitData> FACTORY =
            new SavedData.Factory<>(
                    AAFTTrustHitData::new,
                    AAFTTrustHitData::load
            );
}
