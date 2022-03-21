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

package com.ivan1pl.spigot.plugins.sdhelper.data;

import com.ivan1pl.spigot.plugins.sdhelper.ShoppingDistrictHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Context {
    private final ShoppingDistrictHelper shoppingDistrictHelper;
    private final Map<UUID, Selection> selections = Collections.synchronizedMap(new HashMap<>());
    private final List<Box> shops = Collections.synchronizedList(new LinkedList<>());
    private Map<Material, List<Location>> items = Collections.synchronizedMap(new HashMap<>());

    public Context(ShoppingDistrictHelper shoppingDistrictHelper) {
        this.shoppingDistrictHelper = shoppingDistrictHelper;
    }

    public Selection getSelection(Player player) {
        return selections.computeIfAbsent(player.getUniqueId(), ignored -> new Selection());
    }

    public void addShop(Box box) {
        shops.add(box);
        Bukkit.getScheduler().runTaskAsynchronously(shoppingDistrictHelper, this::store);
    }

    public boolean removeShop(Location location) {
        return shops.stream().filter(b -> b.isIn(location)).findFirst().map(b -> {
            shops.remove(b);
            Bukkit.getScheduler().runTaskAsynchronously(shoppingDistrictHelper, this::store);
            return true;
        }).orElse(false);
    }

    public List<Box> getShops() {
        return shops;
    }

    public void store() {
        File dataFolder = shoppingDistrictHelper.getDataFolder();
        File newShopsFile = new File(dataFolder, "shops.sdh.new");
        File oldShopsFile = new File(dataFolder, "shops.sdh.old");
        File shopsFile = new File(dataFolder, "shops.sdh");
        try {
            Files.writeString(newShopsFile.toPath(), shops.stream()
                    .map(b -> String.format("%s|%d|%d|%d|%s|%d|%d|%d",
                            b.getCorner1().getWorld().getUID(), b.getCorner1().getBlockX(), b.getCorner1().getBlockY(),
                            b.getCorner1().getBlockZ(),
                            b.getCorner2().getWorld().getUID(), b.getCorner2().getBlockX(), b.getCorner2().getBlockY(),
                            b.getCorner2().getBlockZ()))
                    .collect(Collectors.joining(";")));
        } catch (IOException e) {
            shoppingDistrictHelper.getLogger().log(Level.SEVERE, "Failed to save data", e);
        }
        if (!shopsFile.renameTo(oldShopsFile) || !newShopsFile.renameTo(shopsFile) || !oldShopsFile.delete()) {
            shoppingDistrictHelper.getLogger().log(Level.SEVERE, "Failed to save data");
        }
    }

    public void read() {
        shops.clear();
        File dataFolder = shoppingDistrictHelper.getDataFolder();
        File shopsFile = new File(dataFolder, "shops.sdh");
        if (!shopsFile.exists()) {
            try {
                shopsFile.createNewFile();
            } catch (IOException e) {
                shoppingDistrictHelper.getLogger().log(Level.SEVERE, "Failed to read data", e);
            }
        }
        try {
            String[] shopsFileContent = Files.readString(shopsFile.toPath()).split(";");
            for (String shop : shopsFileContent) {
                String[] parts = shop.split("\\|");
                if (parts.length != 8) {
                    shoppingDistrictHelper.getLogger().log(Level.SEVERE, "Failed to read data");
                    return;
                }
                UUID uuid1 = UUID.fromString(parts[0]);
                int x1 = Integer.parseInt(parts[1]);
                int y1 = Integer.parseInt(parts[2]);
                int z1 = Integer.parseInt(parts[3]);
                UUID uuid2 = UUID.fromString(parts[4]);
                int x2 = Integer.parseInt(parts[5]);
                int y2 = Integer.parseInt(parts[6]);
                int z2 = Integer.parseInt(parts[7]);
                Location corner1 = new Location(shoppingDistrictHelper.getServer().getWorld(uuid1), x1, y1, z1);
                Location corner2 = new Location(shoppingDistrictHelper.getServer().getWorld(uuid2), x2, y2, z2);
                shops.add(new Box(corner1, corner2));
            }
        } catch (IOException | NumberFormatException e) {
            shops.clear();
            shoppingDistrictHelper.getLogger().log(Level.SEVERE, "Failed to read data", e);
        }
    }

    public Map<Material, List<Location>> getItems() {
        return items;
    }

    public void setItems(Map<Material, List<Location>> items) {
        this.items = Collections.synchronizedMap(items);
    }
}
