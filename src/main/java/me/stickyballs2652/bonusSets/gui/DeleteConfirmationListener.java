package me.stickyballs2652.bonusSets.gui;

import me.stickyballs2652.bonusSets.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class DeleteConfirmationListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player superfemboy)) return;
        if (!(event.getInventory().getHolder() instanceof DeleteConfirmationHolder holder)) return;

        event.setCancelled(true);

        int slot = event.getRawSlot();
        if (slot >= event.getInventory().getSize()) return;

        Main plugin = Main.getInstance();

        if (slot == DeleteConfirmationHolder.CONFIRM_BTN) {
            String setId = holder.getTargetSet().id();
            plugin.getSetManager().deleteSet(setId);
            superfemboy.sendMessage("§a[BonusSets] Bonus set §e" + setId + " §ahas been deleted.");
            superfemboy.openInventory(new SetMenuHolder(plugin.getSetManager().getSets()).getInventory());
        } else if (slot == DeleteConfirmationHolder.CANCEL_BTN) {
            superfemboy.openInventory(new SetMenuHolder(plugin.getSetManager().getSets()).getInventory());
        }
    }
}