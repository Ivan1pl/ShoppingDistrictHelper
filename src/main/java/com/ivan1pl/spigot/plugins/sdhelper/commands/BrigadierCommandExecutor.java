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

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class BrigadierCommandExecutor implements TabExecutor {
    private final CommandDispatcher<Object> commandDispatcher = new CommandDispatcher<>();

    public void register(LiteralArgumentBuilder<Object> tree) {
        commandDispatcher.register(tree);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String input = command.getName() + CommandDispatcher.ARGUMENT_SEPARATOR +
                String.join(CommandDispatcher.ARGUMENT_SEPARATOR, args);
        ParseResults<Object> parseResults = commandDispatcher.parse(input, sender);
        if(parseResults.getExceptions().size()>0) {
            sender.sendMessage("Invalid command syntax");
            return true;
        }
        try {
            commandDispatcher.execute(parseResults);
        } catch (CommandSyntaxException e) {
            sender.sendMessage("Invalid command syntax");
            return true;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        try {
            String input = command.getName() + CommandDispatcher.ARGUMENT_SEPARATOR +
                    String.join(CommandDispatcher.ARGUMENT_SEPARATOR, args);
            CompletableFuture<Suggestions> completionSuggestions = commandDispatcher
                    .getCompletionSuggestions(commandDispatcher.parse(input, sender));
            return completionSuggestions.get().getList().stream().map(Suggestion::getText).collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            //nop
        }
        return new ArrayList<>();
    }
}
