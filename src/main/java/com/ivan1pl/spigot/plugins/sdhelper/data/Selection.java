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

public class Selection {
    private Location corner1;
    private Location corner2;

    public Location getCorner1() {
        return corner1;
    }

    public void setCorner1(Location corner1) {
        this.corner1 = corner1;
    }

    public Location getCorner2() {
        return corner2;
    }

    public void setCorner2(Location corner2) {
        this.corner2 = corner2;
    }

    public Either<Box, Error> validate() {
        if (corner1 == null || corner2 == null) {
            return Either.right(new Error("One of the corners is not set"));
        }
        if (!corner1.getWorld().equals(corner2.getWorld())) {
            return Either.right(new Error("Corners are in different worlds"));
        }
        Location c1 = new Location(corner1.getWorld(),
                Math.min(corner1.getBlockX(), corner2.getBlockX()),
                Math.min(corner1.getBlockY(), corner2.getBlockY()),
                Math.min(corner1.getBlockZ(), corner2.getBlockZ()));
        Location c2 = new Location(corner1.getWorld(),
                Math.max(corner1.getBlockX(), corner2.getBlockX()),
                Math.max(corner1.getBlockY(), corner2.getBlockY()),
                Math.max(corner1.getBlockZ(), corner2.getBlockZ()));
        return Either.left(new Box(c1, c2));
    }
}
