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

import org.bukkit.Location;

public class Box {
    private final Location corner1;
    private final Location corner2;

    public Box(Location corner1, Location corner2) {
        this.corner1 = corner1;
        this.corner2 = corner2;
    }

    public Location getCorner1() {
        return corner1;
    }

    public Location getCorner2() {
        return corner2;
    }

    public boolean isIn(Location location) {
        if (!corner1.getWorld().equals(location.getWorld())) {
            return false;
        }
        return corner1.getBlockX() <= location.getX() && corner2.getBlockX() >= location.getX() &&
                corner1.getBlockY() <= location.getY() && corner2.getBlockY() >= location.getY() &&
                corner1.getBlockZ() <= location.getZ() && corner2.getBlockZ() >= location.getZ();
    }
}
