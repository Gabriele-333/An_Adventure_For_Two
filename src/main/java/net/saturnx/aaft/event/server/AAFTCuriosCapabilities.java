package net.saturnx.aaft.event.server;/*
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
 * File created on: 24/01/2026
 */


import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.saturnx.aaft.item.AAFTItem;
import net.saturnx.aaft.item.BasicCurio;
import top.theillusivec4.curios.api.CuriosCapability;

@EventBusSubscriber(modid = "aaft", bus = EventBusSubscriber.Bus.MOD)
public class AAFTCuriosCapabilities {
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {

        event.registerItem(
                CuriosCapability.ITEM,
                (stack, ctx) -> new BasicCurio(stack, "ring"),
                AAFTItem.RING.get()
        );

        event.registerItem(
                CuriosCapability.ITEM,
                (stack, ctx) -> new BasicCurio(stack, "bracelet"),
                AAFTItem.BRACELET.get()
        );
    }
}
