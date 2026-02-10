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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AAFTTrustRevengeData extends SavedData {
    public static final int COOLDOWN_TICKS = 2 * 60 * 20;

    private static final String DATA_ID = "aaft_trust_revenge";
    private static final String KEY_KILLERS = "killers";
    private static final String KEY_COOLDOWN_END = "trust_revenge_cooldown_end";

    private final Map<UUID, UUID> lastKillers = new HashMap<>();
    private long cooldownEndTick;

    public static AAFTTrustRevengeData get(MinecraftServer server) {
        return server.overworld()
                .getDataStorage()
                .computeIfAbsent(FACTORY, DATA_ID);
    }

    public UUID getLastKiller(UUID victim) {
        return lastKillers.get(victim);
    }

    public void setLastKiller(UUID victim, UUID killer) {
        if (victim == null) {
            return;
        }
        if (killer == null) {
            lastKillers.remove(victim);
        } else {
            lastKillers.put(victim, killer);
        }
        setDirty();
    }

    public boolean canReward(long currentTick) {
        return currentTick >= cooldownEndTick;
    }

    public void consumeReward(long currentTick) {
        cooldownEndTick = currentTick + COOLDOWN_TICKS;
        setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        CompoundTag killersTag = new CompoundTag();
        for (Map.Entry<UUID, UUID> entry : lastKillers.entrySet()) {
            killersTag.putUUID(entry.getKey().toString(), entry.getValue());
        }
        tag.put(KEY_KILLERS, killersTag);
        tag.putLong(KEY_COOLDOWN_END, cooldownEndTick);
        return tag;
    }

    public static AAFTTrustRevengeData load(CompoundTag tag, HolderLookup.Provider registries) {
        AAFTTrustRevengeData data = new AAFTTrustRevengeData();
        if (tag.contains(KEY_KILLERS)) {
            CompoundTag killersTag = tag.getCompound(KEY_KILLERS);
            for (String key : killersTag.getAllKeys()) {
                UUID victim = UUID.fromString(key);
                UUID killer = killersTag.getUUID(key);
                data.lastKillers.put(victim, killer);
            }
        }
        if (tag.contains(KEY_COOLDOWN_END)) {
            data.cooldownEndTick = tag.getLong(KEY_COOLDOWN_END);
        }
        return data;
    }

    public static final SavedData.Factory<AAFTTrustRevengeData> FACTORY =
            new SavedData.Factory<>(
                    AAFTTrustRevengeData::new,
                    AAFTTrustRevengeData::load
            );
}
