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
 * File created on: 08/02/2026
 */

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

public class AAFTUniqueItemData extends SavedData {
    private static final String DATA_ID = "aaft_unique_item";
    private static final String KEY_ITEM = "chosen_item";

    private String chosenItemId;

    public static AAFTUniqueItemData get(MinecraftServer server) {
        return server.overworld()
                .getDataStorage()
                .computeIfAbsent(FACTORY, DATA_ID);
    }

    public boolean hasChoice() {
        return chosenItemId != null;
    }

    public boolean isChosen(ResourceLocation itemId) {
        return chosenItemId != null && chosenItemId.equals(itemId.toString());
    }

    public void choose(ResourceLocation itemId) {
        if (chosenItemId == null) {
            chosenItemId = itemId.toString();
            setDirty();
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        if (chosenItemId != null) {
            tag.putString(KEY_ITEM, chosenItemId);
        }
        return tag;
    }

    public static AAFTUniqueItemData load(CompoundTag tag, HolderLookup.Provider registries) {
        AAFTUniqueItemData data = new AAFTUniqueItemData();
        if (tag.contains(KEY_ITEM)) {
            data.chosenItemId = tag.getString(KEY_ITEM);
        }
        return data;
    }

    public static final SavedData.Factory<AAFTUniqueItemData> FACTORY =
            new SavedData.Factory<>(
                    AAFTUniqueItemData::new,
                    AAFTUniqueItemData::load
            );
}
