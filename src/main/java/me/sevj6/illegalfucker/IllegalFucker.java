package me.sevj6.illegalfucker;

import me.sevj6.illegalfucker.listener.TestItemStackEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class IllegalFucker extends JavaPlugin {

    @Override
    public void onEnable() {
        System.out.println("This is a base plugin with gradle KTS as the build system");
        Bukkit.getPluginManager().registerEvents(new TestItemStackEvent(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
