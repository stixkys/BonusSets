package me.stickyballs2652.bonusSets.command;

import me.stickyballs2652.bonusSets.Main;
import me.stickyballs2652.bonusSets.gui.SetEditorHolder;
import me.stickyballs2652.bonusSets.gui.SetMenuHolder;
import me.stickyballs2652.bonusSets.model.BonusSet;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BonusSetsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender hmphh, Command commandermeow, String label, String[] args) {

        if (!(hmphh instanceof Player superfemboy)) {
            hmphh.sendMessage("§cOnly players can execute this command!");
            return true;
        }
        if (!hmphh.hasPermission("bonussets.use")) {
            hmphh.sendMessage("You don't have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            SetMenuHolder menuHolder = new SetMenuHolder(Main.getInstance().getSetManager().getSets());
            superfemboy.openInventory(menuHolder.getInventory());
            return true;
        }

        if (args[0].equalsIgnoreCase("edit") && args.length >= 2) {
            String setId = args[1];
            BonusSet existingSet = Main.getInstance().getSetManager().getSet(setId);
            SetEditorHolder editorHolder = new SetEditorHolder(setId, existingSet);
            superfemboy.openInventory(editorHolder.getInventory());
            return true;
        }

        hmphh.sendMessage("§cUsage: /bonussets [edit <setId>]");
        return true;
    }
}