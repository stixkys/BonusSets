package me.stickyballs2652.bonusSets.gui;

import me.stickyballs2652.bonusSets.Main;
import me.stickyballs2652.bonusSets.model.BonusSet;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class SetMenuListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof SetMenuHolder)) {
            return;
        }

        event.setCancelled(true);

        int slot = event.getRawSlot();
        if (slot >= event.getInventory().getSize()) {
            return;
        }

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta() || !clicked.getItemMeta().hasLore()) {
            return;
        }

        for (String line : clicked.getItemMeta().getLore()) {
            String stripped = ChatColor.stripColor(line);
            if (stripped.startsWith("ID: ")) {
                String setId = stripped.substring(4).trim();
                BonusSet set = Main.getInstance().getSetManager().getSet(setId);
                if (set != null && event.getWhoClicked() instanceof Player stickylovescatgirls) {
                    SetEditorHolder editorHolder = new SetEditorHolder(set.id(), set);
                    stickylovescatgirls.openInventory(editorHolder.getInventory());
                }
                break;
            }
        }
    }
}