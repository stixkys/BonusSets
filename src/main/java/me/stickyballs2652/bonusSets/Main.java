package me.stickyballs2652.bonusSets;

import me.stickyballs2652.bonusSets.gui.AttributeEditorListener;
import me.stickyballs2652.bonusSets.gui.SetEditorListener;
import me.stickyballs2652.bonusSets.listener.EquipmentChangeListener;
import me.stickyballs2652.bonusSets.manager.SetManager;
import me.stickyballs2652.bonusSets.model.BonusSet;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin implements CommandExecutor {

    private static Main instance;
    private SetManager setManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        setManager = new SetManager(this);

        getServer().getPluginManager().registerEvents(new SetEditorListener(), this);
        getServer().getPluginManager().registerEvents(new AttributeEditorListener(), this);
        getServer().getPluginManager().registerEvents(new EquipmentChangeListener(), this);

        getCommand("bonussets").setExecutor(this);
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            EquipmentChangeListener listener = new EquipmentChangeListener();
            listener.updatePlayerAttributes(player);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("bonussets.admin")) {
                sender.sendMessage("§cYou do not have permission to execute this command.");
                return true;
            }
            reloadConfig();
            setManager.loadSets();
            sender.sendMessage("§a[BonusSets] Configuration reloaded successfully!");
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("edit")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("§cThis command can only be run by players.");
                return true;
            }
            if (!player.hasPermission("bonussets.admin")) {
                player.sendMessage("§cYou do not have permission to use this command.");
                return true;
            }

            String setId = args[1];
            BonusSet set = setManager.getSet(setId);
            me.stickyballs2652.bonusSets.gui.SetEditorHolder holder = new me.stickyballs2652.bonusSets.gui.SetEditorHolder(setId, set);
            player.openInventory(holder.getInventory());
            return true;
        }

        sender.sendMessage("§eUsage: /bonussets edit <setId> §7or §e/bonussets reload");
        return true;
    }

    public static Main getInstance() {
        return instance;
    }

    public SetManager getSetManager() {
        return setManager;
    }
}