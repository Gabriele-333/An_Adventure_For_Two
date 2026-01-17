package net.saturnx.aaft.client.screen;/*
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
 * File created on: 17/01/2026
 */


import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


public class AAFTMenus {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, "aaft");

    public static DeferredHolder<MenuType<?>, MenuType<AAFTMenu>> AAFT_MENU =
            MENUS.register(
                    "aaft_menu",
                    () -> new MenuType<>(AAFTMenu::new, FeatureFlags.DEFAULT_FLAGS)
            );

    public static void registerAll(net.neoforged.bus.api.IEventBus modBus) {
        MENUS.register(modBus);
    }
}