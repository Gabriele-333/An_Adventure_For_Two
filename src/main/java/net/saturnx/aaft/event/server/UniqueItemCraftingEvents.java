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
 * File created on: 08/02/2026
 */

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.saturnx.aaft.data.AAFTUniqueItemData;
import net.saturnx.aaft.item.AAFTItem;

import java.util.List;
import java.util.Set;

public class UniqueItemCraftingEvents {
    private static final Set<Item> UNIQUE_ITEMS = Set.of(
            AAFTItem.RING.get(),
            AAFTItem.BRACELET.get(),
            AAFTItem.COMPASS.get()
    );

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        ItemStack crafting = event.getCrafting();
        Item item = crafting.getItem();
        if (!UNIQUE_ITEMS.contains(item)) {
            return;
        }

        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
        var server = player.level().getServer();
        if (server == null) {
            return;
        }

        AAFTUniqueItemData data = AAFTUniqueItemData.get(server);

        if (!data.hasChoice()) {
            data.choose(itemId);
            return;
        }

        if (data.isChosen(itemId)) {
            return;
        }

        int craftedCount = crafting.getCount();
        removeCraftedStack(player, item, craftedCount, event.getInventory());
        refundIngredients(player, item, craftedCount);
        player.displayClientMessage(Component.translatable("message.aaft.unique_item_locked"), true);
    }

    private static void removeCraftedStack(ServerPlayer player, Item item, int count, Container craftMatrix) {
        if (count <= 0) {
            return;
        }

        ItemStack carried = player.containerMenu.getCarried();
        if (carried.is(item)) {
            int remove = Math.min(count, carried.getCount());
            carried.shrink(remove);
            count -= remove;
            if (carried.isEmpty()) {
                player.containerMenu.setCarried(ItemStack.EMPTY);
            }
        }

        if (count > 0) {
            player.getInventory().clearOrCountMatchingItems(stack -> stack.is(item), count, craftMatrix);
        }
    }

    private static void refundIngredients(ServerPlayer player, Item item, int craftedCount) {
        if (craftedCount <= 0) {
            return;
        }

        List<ItemStack> refundStacks = getRefundStacks(item);
        for (ItemStack stack : refundStacks) {
            int total = stack.getCount() * craftedCount;
            while (total > 0) {
                int give = Math.min(stack.getMaxStackSize(), total);
                ItemStack refund = new ItemStack(stack.getItem(), give);
                if (!player.getInventory().add(refund)) {
                    player.drop(refund, false);
                }
                total -= give;
            }
        }
    }

    private static List<ItemStack> getRefundStacks(Item item) {
        if (item == AAFTItem.RING.get()) {
            return List.of(
                    new ItemStack(Items.GOLD_NUGGET, 4),
                    new ItemStack(Items.DIAMOND, 1)
            );
        }
        if (item == AAFTItem.BRACELET.get()) {
            return List.of(
                    new ItemStack(Items.GOLD_NUGGET, 2),
                    new ItemStack(Items.STRING, 1),
                    new ItemStack(Items.AMETHYST_SHARD, 1)
            );
        }
        if (item == AAFTItem.COMPASS.get()) {
            return List.of(
                    new ItemStack(Items.COMPASS, 1),
                    new ItemStack(Items.ENDER_PEARL, 1),
                    new ItemStack(Items.AMETHYST_SHARD, 1)
            );
        }
        return List.of();
    }
}


