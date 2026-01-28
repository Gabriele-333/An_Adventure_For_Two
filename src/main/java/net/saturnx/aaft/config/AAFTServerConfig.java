package net.saturnx.aaft.config;/*
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
 * File created on: 13/01/2026
 */

import net.neoforged.neoforge.common.ModConfigSpec;

public final class AAFTServerConfig {

    public static final ModConfigSpec SPEC;


    /* ---------------- GENERAL ---------------- */

    public static final ModConfigSpec.BooleanValue forceAdventureWhenSolo;

    /* ---------------- XP SYSTEM ---------------- */

    public static final ModConfigSpec.BooleanValue sharedXpEnabled;
    public static final ModConfigSpec.IntValue xpRestoreTimeSeconds;


    /* ---------------- TOAST / UI ---------------- */


    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("An Adventure For Two - Server Configuration")
                .push("general");


        forceAdventureWhenSolo = builder
                .comment("Force ADVENTURE mode when only one player is online")
                .define("forceAdventureWhenSolo", true);

        builder.pop();

        /* ---------------- XP ---------------- */

        builder.push("xp");

        sharedXpEnabled = builder
                .comment("Enable shared XP system between players")
                .define("sharedXpEnabled", true);

        xpRestoreTimeSeconds = builder
                .comment("Seconds to wait before restoring XP after death")
                .defineInRange("restoreTimeSeconds", 30, 5, 300);


        builder.pop();

        SPEC = builder.build();
    }

    private AAFTServerConfig() {}
}