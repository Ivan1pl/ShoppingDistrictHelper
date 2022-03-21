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

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import org.bukkit.Material;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class MaterialArgumentType implements ArgumentType<Material> {
    @Override
    public Material parse(StringReader reader) throws CommandSyntaxException {
        StringBuilder builder = new StringBuilder();
        while (reader.canRead() && !Character.isWhitespace(reader.peek())) {
            builder.append(reader.read());
        }
        return Material.matchMaterial(builder.toString());
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CompletableFuture.supplyAsync(() -> {
            Stream.of(Material.values())
                    .filter(m -> m.getKey().getKey().toLowerCase().startsWith(builder.getRemainingLowerCase()) ||
                            m.getKey().toString().toLowerCase().startsWith(builder.getRemainingLowerCase()))
                    .forEach(m -> builder.suggest(m.getKey().toString()));
            return builder.build();
        });
    }

    public static MaterialArgumentType material() {
        return new MaterialArgumentType();
    }
}
