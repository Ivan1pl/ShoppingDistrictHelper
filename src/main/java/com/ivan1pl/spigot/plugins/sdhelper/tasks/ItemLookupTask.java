/*
 * Copyright 2022 Ivan1
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivan1pl.spigot.plugins.sdhelper.tasks;

import com.ivan1pl.spigot.plugins.sdhelper.ShoppingDistrictHelper;
import com.ivan1pl.spigot.plugins.sdhelper.data.Box;
import com.ivan1pl.spigot.plugins.sdhelper.data.Context;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Stream;

public class ItemLookupTask {
    private static final Material CURRENCY = Material.DIAMOND;

    private final ShoppingDistrictHelper shoppingDistrictHelper;
    private final Context context;

    public ItemLookupTask(ShoppingDistrictHelper shoppingDistrictHelper, Context context) {
        this.shoppingDistrictHelper = shoppingDistrictHelper;
        this.context = context;
    }

    public void execute() {
        Map<Material, List<Location>> items = new HashMap<>();
        for (Box shop : context.getShops()) {
            for (int x = shop.getCorner1().getBlockX(); x <= shop.getCorner2().getBlockX(); ++x) {
                for (int y = shop.getCorner1().getBlockY(); y <= shop.getCorner2().getBlockY(); ++y) {
                    for (int z = shop.getCorner1().getBlockZ(); z <= shop.getCorner2().getBlockZ(); ++z) {
                        int xx = x, yy = y, zz = z;
                        Bukkit.getScheduler().runTask(shoppingDistrictHelper, () -> {
                            Location l = new Location(shop.getCorner1().getWorld(), xx, yy, zz);
                            if (l.getBlock().getState() instanceof BlockInventoryHolder blockInventoryHolder) {
                                Stream.of(blockInventoryHolder.getInventory().getContents()).map(ItemStack::getType)
                                        .filter(m -> m != CURRENCY).distinct()
                                        .forEach(m -> items.computeIfAbsent(
                                                m, ignored -> Collections.synchronizedList(new LinkedList<>())).add(l));
                            }
                        });
                    }
                }
            }
        }
        context.setItems(items);
    }
}
