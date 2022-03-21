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
import com.ivan1pl.spigot.plugins.sdhelper.data.Selection;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.ivan1pl.spigot.plugins.sdhelper.commands.arguments.CoordinateArgumentType.*;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

public class ShopCommand {
    public static LiteralArgumentBuilder<Object> createCommandNode(Context context) {
        return literal("shop")
                .then(literal("save").executes(c -> {
                    Player p = (Player) c.getSource();
                    Selection selection = context.getSelection(p);
                    selection.validate().handle(
                            box -> {
                                context.addShop(box);
                                p.sendMessage("Shop added");
                            },
                            error -> p.sendMessage(error.getDescription()));
                    return 1;
                }))
                .then(literal("remove").executes(c -> {
                    Player p = (Player) c.getSource();
                    p.sendMessage(context.removeShop(p.getLocation()) ?
                            "Shop removed" : "Shop not found at current location");
                    return 1;
                }))
                .then(literal("corner1")
                        .then(createLocationArgumentBuilder((p, l) ->
                                context.getSelection(p).setCorner1(l)))
                        .executes(c -> {
                            Player p = (Player) c.getSource();
                            Location l = p.getLocation();
                            context.getSelection(p).setCorner1(l);
                            p.sendMessage(String.format("Corner set to: (%d, %d, %d)",
                                    l.getBlockX(), l.getBlockY(), l.getBlockZ()));
                            return 1;
                        }))
                .then(literal("corner2")
                        .then(createLocationArgumentBuilder((p, l) ->
                                context.getSelection(p).setCorner2(l)))
                        .executes(c -> {
                            Player p = (Player) c.getSource();
                            Location l = p.getLocation();
                            context.getSelection(p).setCorner2(l);
                            p.sendMessage(String.format("Corner set to: (%d, %d, %d)",
                                    l.getBlockX(), l.getBlockY(), l.getBlockZ()));
                            return 1;
                        }));
    }

    @SuppressWarnings("unchecked")
    private static ArgumentBuilder<Object, ?> createLocationArgumentBuilder(
            BiConsumer<Player, Location> locationConsumer) {
        return argument("x", coordinateX())
                .then(argument("y", coordinateY())
                        .then(argument("z", coordinateZ())
                                .executes(c -> {
                                    Player p = (Player) c.getSource();
                                    Function<Player, Integer> xFunction = c.getArgument("x", Function.class);
                                    Function<Player, Integer> yFunction = c.getArgument("y", Function.class);
                                    Function<Player, Integer> zFunction = c.getArgument("z", Function.class);
                                    Location l = new Location(
                                            p.getWorld(), xFunction.apply(p), yFunction.apply(p), zFunction.apply(p));
                                    locationConsumer.accept(p, l);
                                    p.sendMessage(String.format("Corner set to: (%d, %d, %d)",
                                            l.getBlockX(), l.getBlockY(), l.getBlockZ()));
                                    return 1;
                                })));
    }
}
