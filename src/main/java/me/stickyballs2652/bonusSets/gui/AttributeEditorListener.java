package me.stickyballs2652.bonusSets.gui;

import me.stickyballs2652.bonusSets.Main;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class AttributeEditorListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof AttributeEditorHolder holder)) {
            return;
        }

        event.setCancelled(true);

        int slot = event.getRawSlot();
        if (slot >= event.getInventory().getSize()) return;

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

        if (slot == AttributeEditorHolder.SAVE_BACK_BTN && event.getWhoClicked() instanceof Player catgirlsontop) {
            SetEditorHolder editorHolder = new SetEditorHolder(
                    holder.getSetId(),
                    Main.getInstance().getSetManager().getSet(holder.getSetId())
            );
            editorHolder.getAttributes().clear();
            editorHolder.getAttributes().putAll(holder.getAttributes());
            editorHolder.renderControls();
            catgirlsontop.openInventory(editorHolder.getInventory());
            return;
        }

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta() || !clicked.getItemMeta().hasDisplayName()) return;

        String displayName = clicked.getItemMeta().getDisplayName().replace("§e ", "").trim();
        try {
            Attribute attr = Attribute.valueOf(displayName);
            double current = holder.getAttributes().getOrDefault(attr, 0.0);

            ClickType click = event.getClick();
            if (click == ClickType.LEFT) current += 1.0;
            else if (click == ClickType.SHIFT_LEFT) current += 0.1;
            else if (click == ClickType.RIGHT) current -= 1.0;
            else if (click == ClickType.SHIFT_RIGHT) current -=0.1;

            if (current <= 0) {
                holder.getAttributes().remove(attr);
            } else {
                holder.getAttributes().put(attr, Math.round(current * 10.0) / 10.0);
            }

            holder.render();
        } catch (IllegalArgumentException ignored) {}
    }
}
