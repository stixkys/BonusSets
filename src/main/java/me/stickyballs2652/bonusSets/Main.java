package me.stickyballs2652.bonusSets;

import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        getLogger().info("BonusSets enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("BonusSets disabled.");
    }

    public static Main getInstance() {
        return instance;
    }
}