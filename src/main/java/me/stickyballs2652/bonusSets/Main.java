package me.stickyballs2652.bonusSets;

import me.stickyballs2652.bonusSets.command.BonusSetsCommand;
import me.stickyballs2652.bonusSets.gui.AttributeEditorListener;
import me.stickyballs2652.bonusSets.gui.SetEditorListener;
import me.stickyballs2652.bonusSets.gui.SetMenuListener;
import me.stickyballs2652.bonusSets.listener.EquipmentChangeListener;
import me.stickyballs2652.bonusSets.manager.SetManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main instance;
    private SetManager setManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        this.setManager = new SetManager(this);

        getServer().getPluginManager().registerEvents(new SetMenuListener(), this);
        getServer().getPluginManager().registerEvents(new SetEditorListener(), this);
        getServer().getPluginManager().registerEvents(new EquipmentChangeListener(), this);
        getServer().getPluginManager().registerEvents(new AttributeEditorListener(), this);

        if (getCommand("bonussets") != null) {
            getCommand("bonussets").setExecutor(new BonusSetsCommand());
        }

        getLogger().info("BonusSets enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("BonusSets disabled.");
    }

    public static Main getInstance() {
        return instance;
    }

    public SetManager getSetManager() {
        return setManager;
    }
}