package net.saturnx.aaft.effect;
/*
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
 * File created on: 03/02/2026
 */

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.saturnx.aaft.AAFT;

public final class AAFTEffects {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, AAFT.MOD_ID);

    public static final DeferredHolder<MobEffect, MobEffect> LONTANANZA =
            MOB_EFFECTS.register("lontananza", LontananzaEffect::new);

    private AAFTEffects() {
    }

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
