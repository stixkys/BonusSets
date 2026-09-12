package me.stickyballs2652.bonusSets.gui;

import me.stickyballs2652.bonusSets.Main;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectEditorListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof PotionEffectEditorHolder holder)) {
            return;
        }

        event.setCancelled(true);

        int slot = event.getRawSlot();
        if (slot >= event.getInventory().getSize()) return;

        if (slot == PotionEffectEditorHolder.PREV_PAGE_BTN && holder.getPage() > 0) {
            holder.setPage(holder.getPage() - 1);
            holder.render();
            return;
        }

        if (slot == PotionEffectEditorHolder.NEXT_PAGE_BTN) {
            holder.setPage(holder.getPage() + 1);
            holder.render();
            return;
        }

        if (slot == PotionEffectEditorHolder.SAVE_BACK_BTN && event.getWhoClicked() instanceof Player player) {
            SetEditorHolder editorHolder = new SetEditorHolder(
                    holder.getSetId(),
                    Main.getInstance().getSetManager().getSet(holder.getSetId())
            );
            editorHolder.getPotionEffects().clear();
            editorHolder.getPotionEffects().addAll(holder.getEffects());
            editorHolder.renderControls();
            player.openInventory(editorHolder.getInventory());
            return;
        }

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta() || !clicked.getItemMeta().hasDisplayName()) return;

        String keyName = clicked.getItemMeta().getDisplayName().replace("§e", "").trim().toLowerCase();
        try {
            PotionEffectType type = Registry.POTION_EFFECT_TYPE.get(NamespacedKey.minecraft(keyName));
            if (type == null) return;

            ClickType click = event.getClick();
            if (click.isLeftClick()) {
                holder.updateEffect(type, true);
            } else if (click.isRightClick()) {
                holder.updateEffect(type, false);
            }
        } catch (IllegalArgumentException ignored) {}
    }
}