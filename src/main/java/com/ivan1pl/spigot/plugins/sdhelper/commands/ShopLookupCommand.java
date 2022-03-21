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

package com.ivan1pl.spigot.plugins.sdhelper.commands;

import com.ivan1pl.spigot.plugins.sdhelper.data.Context;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Collections;

import static com.ivan1pl.spigot.plugins.sdhelper.commands.arguments.MaterialArgumentType.material;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

public class ShopLookupCommand {
    public static LiteralArgumentBuilder<Object> createCommandNode(Context context) {
        return literal("shoplookup").then(argument("material", material()).executes(c -> {
            Player p = (Player) c.getSource();
            Material material = c.getArgument("material", Material.class);
            context.getItems().getOrDefault(material, Collections.emptyList()).stream()
                    .filter(l -> l.getWorld().equals(p.getWorld()))
                    .forEach(l ->
                            p.sendMessage(String.format("Item available at location: (%d, %d, %d) - %f blocks away",
                                    l.getBlockX(), l.getBlockY(), l.getBlockZ(), l.distance(p.getLocation()))));
            return 1;
        }));
    }
}
