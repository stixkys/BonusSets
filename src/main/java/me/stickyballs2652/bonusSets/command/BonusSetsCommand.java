package me.stickyballs2652.bonusSets.command;

import me.stickyballs2652.bonusSets.Main;
import me.stickyballs2652.bonusSets.gui.SetEditorHolder;
import me.stickyballs2652.bonusSets.gui.SetMenuHolder;
import me.stickyballs2652.bonusSets.model.BonusSet;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BonusSetsCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length >= 2 && (args[0].equalsIgnoreCase("disable") || args[0].equalsIgnoreCase("enable"))) {
            if (!sender.hasPermission("bonussets.admin")) {
                sender.sendMessage("§cYou don't have permission to do this.");
                return true;
            }
            String setId = args[1];
            BonusSet set = Main.getInstance().getSetManager().getSet(setId);
            if (set == null) {
                sender.sendMessage("§cSet '" + setId + "' not found!");
                return true;
            }

            boolean newState = args[0].equalsIgnoreCase("enable");
            Main.getInstance().getSetManager().setSetState(setId, newState);
            sender.sendMessage("§aSet §e" + setId + " §ahas been " + (newState ? "§eenabled" : "§cdisabled") + ".");
            return true;
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("bonussets.admin")) {
                sender.sendMessage("§cYou don't have permission to do this.");
                return true;
            }
            Main.getInstance().reloadConfig();
            Main.getInstance().getSetManager().loadSets();
            sender.sendMessage("§a[BonusSets] Configuration reloaded successfully!");
            return true;
        }

        if (args.length >= 3 && args[0].equalsIgnoreCase("give")) {
            if (!sender.hasPermission("bonussets.admin")) {
                sender.sendMessage("§cYou do not have permission to execute this command.");
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("§cPlayer not found or offline.");
                return true;
            }

            String setId = args[2];
            BonusSet bonusSet = Main.getInstance().getSetManager().getSet(setId);
            if (bonusSet == null) {
                sender.sendMessage("§cBonus set '" + setId + "' does not exist.");
                return true;
            }

            giveSetToPlayer(target, bonusSet);
            sender.sendMessage("§aSuccessfully gave bonus set §e" + setId + " §ato §e" + target.getName() + "§a.");
            target.sendMessage("§aYou have received the bonus set §e" + setId + "§a!");
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can execute this command!");
            return true;
        }
        if (!player.hasPermission("bonussets.use")) {
            player.sendMessage("§cYou don't have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            SetMenuHolder menuHolder = new SetMenuHolder(Main.getInstance().getSetManager().getSets());
            player.openInventory(menuHolder.getInventory());
            return true;
        }

        if (args[0].equalsIgnoreCase("edit") && args.length >= 2) {
            String setId = args[1];
            BonusSet existingSet = Main.getInstance().getSetManager().getSet(setId);
            SetEditorHolder editorHolder = new SetEditorHolder(setId, existingSet);
            player.openInventory(editorHolder.getInventory());
            return true;
        }

        player.sendMessage("§eUsage: /bonussets §7(opens menu), §e/bonussets edit <setId>, §e/bonussets give <player> <setId>, or §e/bonussets <enable|disable> <setId>");
        return true;
    }

    private void giveSetToPlayer(Player player, BonusSet set) {
        if (set.helmet() != null) player.getInventory().addItem(set.helmet().clone());
        if (set.chestplate() != null) player.getInventory().addItem(set.chestplate().clone());
        if (set.leggings() != null) player.getInventory().addItem(set.leggings().clone());
        if (set.boots() != null) player.getInventory().addItem(set.boots().clone());
        if (set.mainhand() != null) player.getInventory().addItem(set.mainhand().clone());
        if (set.offhand() != null) player.getInventory().addItem(set.offhand().clone());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("edit");
            completions.add("give");
            completions.add("enable");
            completions.add("disable");
            completions.add("reload");
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("edit") || args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("disable"))) {
            for (BonusSet set : Main.getInstance().getSetManager().getSets()) {
                completions.add(set.id());
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                completions.add(p.getName());
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            for (BonusSet set : Main.getInstance().getSetManager().getSets()) {
                completions.add(set.id());
            }
        }
        return completions;
    }
}