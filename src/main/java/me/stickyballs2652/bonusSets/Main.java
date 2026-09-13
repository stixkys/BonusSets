package me.stickyballs2652.bonusSets;

import me.stickyballs2652.bonusSets.command.BonusSetsCommand;
import me.stickyballs2652.bonusSets.gui.*;
import me.stickyballs2652.bonusSets.listener.EquipmentChangeListener;
import me.stickyballs2652.bonusSets.manager.SetManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main instance;
    private SetManager setManager;
    private EquipmentChangeListener equipmentChangeListener;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        setManager = new SetManager(this);
        equipmentChangeListener = new EquipmentChangeListener();

        getServer().getPluginManager().registerEvents(new SetEditorListener(), this);
        getServer().getPluginManager().registerEvents(new AttributeEditorListener(), this);
        getServer().getPluginManager().registerEvents(new PotionEffectEditorListener(), this);
        getServer().getPluginManager().registerEvents(new DeleteConfirmationListener(), this);
        getServer().getPluginManager().registerEvents(equipmentChangeListener, this);
        getServer().getPluginManager().registerEvents(new SetMenuListener(), this);

        BonusSetsCommand bonusSetsCommand = new BonusSetsCommand();
        if (getCommand("bonussets") != null) {
            getCommand("bonussets").setExecutor(bonusSetsCommand);
            getCommand("bonussets").setTabCompleter(bonusSetsCommand);
        }
    }

    @Override
    public void onDisable() {
        if (equipmentChangeListener != null) {
            for (Player catgirlsOnTop : Bukkit.getOnlinePlayers()) {
                equipmentChangeListener.updatePlayerAttributes(catgirlsOnTop);
            }
        }
    }

    public static Main getInstance() {
        return instance;
    }

    public SetManager getSetManager() {
        return setManager;
    }

    public EquipmentChangeListener getEquipmentListener() {
        return equipmentChangeListener;
    }
}