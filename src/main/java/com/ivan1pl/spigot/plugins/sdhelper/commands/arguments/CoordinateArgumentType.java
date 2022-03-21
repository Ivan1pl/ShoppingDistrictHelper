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

package com.ivan1pl.spigot.plugins.sdhelper.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

public class CoordinateArgumentType implements ArgumentType<Function<Player, Integer>> {
    private static final Collection<String> EXAMPLES = Arrays.asList("~", "~1", "~-1", "0", "1", "-1");
    private final Function<Player, Integer> currentCoordinateExtractor;

    private CoordinateArgumentType(Function<Player, Integer> currentCoordinateExtractor) {
        this.currentCoordinateExtractor = currentCoordinateExtractor;
    }

    @Override
    public Function<Player, Integer> parse(StringReader reader) throws CommandSyntaxException {
        boolean relative = reader.peek() == '~';
        if (relative) {
            reader.read();
        }
        int value = !reader.canRead() || Character.isWhitespace(reader.peek()) ? 0 : reader.readInt();
        Function<Player, Integer> coordinateExtractor = relative ? currentCoordinateExtractor : p -> 0;
        return player -> coordinateExtractor.apply(player) + value;
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public static CoordinateArgumentType coordinateX() {
        return new CoordinateArgumentType(player -> player.getLocation().getBlockX());
    }

    public static CoordinateArgumentType coordinateY() {
        return new CoordinateArgumentType(player -> player.getLocation().getBlockY());
    }

    public static CoordinateArgumentType coordinateZ() {
        return new CoordinateArgumentType(player -> player.getLocation().getBlockZ());
    }
}
