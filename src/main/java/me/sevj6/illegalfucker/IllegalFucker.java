package me.sevj6.illegalfucker;

import me.sevj6.illegalfucker.listener.ItemStackCreateListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class IllegalFucker extends JavaPlugin {
    private static IllegalFucker instance;

    public static IllegalFucker getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        getLogger().addHandler(new LoggerHandler());
        Bukkit.getPluginManager().registerEvents(new ItemStackCreateListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
