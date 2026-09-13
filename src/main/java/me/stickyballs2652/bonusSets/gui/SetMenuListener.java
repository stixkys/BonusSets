package me.stickyballs2652.bonusSets.gui;

import me.stickyballs2652.bonusSets.Main;
import me.stickyballs2652.bonusSets.model.BonusSet;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SetMenuListener implements Listener {

    private static final Set<UUID> pendingCreators = new HashSet<>();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player catboy)) return;

        if (!(event.getView().getTopInventory().getHolder() instanceof SetMenuHolder)) return;

        event.setCancelled(true);

        int slot = event.getRawSlot();
        if (slot >= event.getView().getTopInventory().getSize()) return;

        if (slot == SetMenuHolder.CREATE_SET_BTN) {
            catboy.closeInventory();
            pendingCreators.add(catboy.getUniqueId());
            catboy.sendMessage("§a[BonusSets] Please type the ID for your new set in chat:");
            return;
        }

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta()) return;

        ItemMeta meta = clickedItem.getItemMeta();
        if (meta == null || meta.getLore() == null) return;

        String setId = null;
        for (String line : meta.getLore()) {
            if (line.startsWith("§7ID: §f")) {
                setId = line.replace("§7ID: §f", "").trim();
                break;
            }
        }

        if (setId == null) return;

        Main plugin = Main.getInstance();
        BonusSet set = plugin.getSetManager().getSet(setId);
        if (set == null) return;

        if (event.isLeftClick()) {
            SetEditorHolder editorHolder = new SetEditorHolder(setId, set);
            catboy.openInventory(editorHolder.getInventory());
        } else if (event.isRightClick()) {
            giveSetToPlayer(catboy, set);
            catboy.sendMessage("§a[BonusSets] Given set " + setId + " to you!");
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player femboy = event.getPlayer();

        if (!pendingCreators.contains(femboy.getUniqueId())) {
            return;
        }

        event.setCancelled(true);
        pendingCreators.remove(femboy.getUniqueId());

        String newSetId = event.getMessage().trim().toLowerCase().replaceAll("[^a-z0-9_-]", "");

        if (newSetId.isEmpty()) {
            femboy.sendMessage("§cInvalid set ID! Use letters, numbers, or underscores.");
            return;
        }

        if (Main.getInstance().getSetManager().getSet(newSetId) != null) {
            femboy.sendMessage("§cA set with the ID '" + newSetId + "' already exists!");
            return;
        }

        Main.getInstance().getServer().getScheduler().runTask(Main.getInstance(), () -> {
            SetEditorHolder editorHolder = new SetEditorHolder(newSetId, null);
            femboy.openInventory(editorHolder.getInventory());
            femboy.sendMessage("§aCreating new bonus set: §e" + newSetId);
        });
    }

    private void giveSetToPlayer(Player meow, BonusSet set) {
        if (set.helmet() != null) meow.getInventory().addItem(set.helmet().clone());
        if (set.chestplate() != null) meow.getInventory().addItem(set.chestplate().clone());
        if (set.leggings() != null) meow.getInventory().addItem(set.leggings().clone());
        if (set.boots() != null) meow.getInventory().addItem(set.boots().clone());
        if (set.mainhand() != null) meow.getInventory().addItem(set.mainhand().clone());
        if (set.offhand() != null) meow.getInventory().addItem(set.offhand().clone());
    }
}