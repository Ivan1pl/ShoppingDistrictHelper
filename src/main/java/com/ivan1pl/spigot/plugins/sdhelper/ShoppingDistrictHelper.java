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

package com.ivan1pl.spigot.plugins.sdhelper;

import com.ivan1pl.spigot.plugins.sdhelper.commands.BrigadierCommandExecutor;
import com.ivan1pl.spigot.plugins.sdhelper.commands.ShopCommand;
import com.ivan1pl.spigot.plugins.sdhelper.commands.ShopLookupCommand;
import com.ivan1pl.spigot.plugins.sdhelper.data.Context;
import com.ivan1pl.spigot.plugins.sdhelper.tasks.ItemLookupTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class ShoppingDistrictHelper extends JavaPlugin {
    private final Context context = new Context(this);
    private final BrigadierCommandExecutor brigadierCommandExecutor = new BrigadierCommandExecutor();
    private final ItemLookupTask itemLookupTask = new ItemLookupTask(this, context);

    @Override
    public void onEnable() {
        saveDefaultConfig();
        context.read();
        brigadierCommandExecutor.register(ShopCommand.createCommandNode(context));
        brigadierCommandExecutor.register(ShopLookupCommand.createCommandNode(context));
        getCommand("shop").setExecutor(brigadierCommandExecutor);
        getCommand("shop").setTabCompleter(brigadierCommandExecutor);
        getCommand("shoplookup").setExecutor(brigadierCommandExecutor);
        getCommand("shoplookup").setTabCompleter(brigadierCommandExecutor);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, itemLookupTask::execute, 0L, 6000L);
    }
}
