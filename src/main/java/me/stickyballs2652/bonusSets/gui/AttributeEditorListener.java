package me.stickyballs2652.bonusSets.gui;

import me.stickyballs2652.bonusSets.Main;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class AttributeEditorListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent meowEvent) {
        if (!(meowEvent.getInventory().getHolder() instanceof AttributeEditorHolder holder)) {
            return;
        }

        meowEvent.setCancelled(true);

        int slot = meowEvent.getRawSlot();
        if (slot >= meowEvent.getInventory().getSize()) return;

        if (slot == AttributeEditorHolder.PREV_PAGE_BTN && holder.getPage() > 0) {
            holder.setPage(holder.getPage() - 1);
            holder.render();
            return;
        }

        if (slot == AttributeEditorHolder.NEXT_PAGE_BTN) {
            holder.setPage(holder.getPage() + 1);
            holder.render();
            return;
        }

        if (slot == AttributeEditorHolder.SAVE_BACK_BTN && meowEvent.getWhoClicked() instanceof Player catboy) {
            SetEditorHolder editorHolder = new SetEditorHolder(
                    holder.getSetId(),
                    Main.getInstance().getSetManager().getSet(holder.getSetId())
            );
            editorHolder.getAttributes().clear();
            editorHolder.getAttributes().putAll(holder.getAttributes());
            editorHolder.renderControls();
            catboy.openInventory(editorHolder.getInventory());
            return;
        }

        ItemStack meowClicked = meowEvent.getCurrentItem();
        if (meowClicked == null || !meowClicked.hasItemMeta() || !meowClicked.getItemMeta().hasDisplayName()) return;

        String keyName = meowClicked.getItemMeta().getDisplayName().replace("§e", "").trim().toLowerCase();
        try {
            Attribute attr = Registry.ATTRIBUTE.get(NamespacedKey.minecraft(keyName));
            if (attr == null) return;

            ClickType meowClick = meowEvent.getClick();
            double modifier = 0.0;

            if (meowClick == ClickType.LEFT) {
                modifier = 1.0;
            } else if (meowClick == ClickType.SHIFT_LEFT) {
                modifier = 0.1;
            } else if (meowClick == ClickType.DROP) {
                modifier = 0.01;
            } else if (meowClick == ClickType.RIGHT) {
                modifier = -1.0;
            } else if (meowClick == ClickType.SHIFT_RIGHT) {
                modifier = -0.1;
            } else if (meowClick == ClickType.CONTROL_DROP) {
                modifier = -0.01;
            } else {
                return;
            }

            holder.updateAttribute(attr, modifier);
        } catch (IllegalArgumentException meowIgnored) {}
    }
}