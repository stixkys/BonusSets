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
    public void onInventoryClick(InventoryClickEvent meowEvent) {
        if (!(meowEvent.getInventory().getHolder() instanceof PotionEffectEditorHolder holder)) {
            return;
        }

        meowEvent.setCancelled(true);

        int slot = meowEvent.getRawSlot();
        if (slot >= meowEvent.getInventory().getSize()) return;

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

        if (slot == PotionEffectEditorHolder.SAVE_BACK_BTN && meowEvent.getWhoClicked() instanceof Player catgirl) {
            SetEditorHolder editorHolder = new SetEditorHolder(
                    holder.getSetId(),
                    Main.getInstance().getSetManager().getSet(holder.getSetId())
            );
            editorHolder.getPotionEffects().clear();
            editorHolder.getPotionEffects().addAll(holder.getEffects());
            editorHolder.renderControls();
            catgirl.openInventory(editorHolder.getInventory());
            return;
        }

        ItemStack clickedMeow = meowEvent.getCurrentItem();
        if (clickedMeow == null || !clickedMeow.hasItemMeta() || !clickedMeow.getItemMeta().hasDisplayName()) return;

        String keyName = clickedMeow.getItemMeta().getDisplayName().replace("§e", "").trim().toLowerCase();
        try {
            PotionEffectType meowType = Registry.POTION_EFFECT_TYPE.get(NamespacedKey.minecraft(keyName));
            if (meowType == null) return;

            ClickType clickCatboy = meowEvent.getClick();
            int tierChange = 0;
            boolean toggleParticles = false;

            if (clickCatboy == ClickType.LEFT) {
                tierChange = 1;
            } else if (clickCatboy == ClickType.RIGHT) {
                tierChange = -1;
            } else if (clickCatboy == ClickType.CONTROL_DROP || clickCatboy == ClickType.DROP || clickCatboy == ClickType.MIDDLE) {
                toggleParticles = true;
            } else {
                return;
            }

            holder.updateEffect(meowType, tierChange, toggleParticles);
        } catch (IllegalArgumentException meowIgnored) {}
    }
}