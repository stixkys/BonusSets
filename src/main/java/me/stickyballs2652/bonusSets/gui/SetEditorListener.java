package me.stickyballs2652.bonusSets.gui;

import me.stickyballs2652.bonusSets.Main;
import me.stickyballs2652.bonusSets.model.BonusSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class SetEditorListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof SetEditorHolder holder)) {
            return;
        }

        int slot = event.getRawSlot();

        if (slot < event.getInventory().getSize() && !holder.isInputSlot(slot)) {
            event.setCancelled(true);
        }

        if (slot == SetEditorHolder.HELMET_TOGGLE) {
            holder.toggleHelmetUnbreakable();
            holder.renderControls();
            return;
        }

        if (slot == SetEditorHolder.CHESTPLATE_TOGGLE) {
            holder.toggleChestplateUnbreakable();
            holder.renderControls();
            return;
        }

        if (slot == SetEditorHolder.LEGGINGS_TOGGLE) {
            holder.toggleLeggingsUnbreakable();
            holder.renderControls();
            return;
        }

        if (slot == SetEditorHolder.BOOTS_TOGGLE) {
            holder.toggleBootsUnbreakable();
            holder.renderControls();
            return;
        }

        if (slot == SetEditorHolder.ATTRIBUTE_BTN && event.getWhoClicked() instanceof Player player) {
            AttributeEditorHolder attrHolder = new AttributeEditorHolder(
                    holder.getSetId(),
                    Main.getInstance().getSetManager().getSet(holder.getSetId()),
                    holder.getAttributes()
            );
            player.openInventory(attrHolder.getInventory());
            return;
        }

        if (slot == SetEditorHolder.THRESHOLD_BTN) {
            int current = holder.getRequiredPieces();
            int updated = (current >= 6) ? 1 : current + 1;
            holder.setRequiredPieces(updated);
            holder.renderControls();
        }

        if (slot == SetEditorHolder.SAVE_BTN) {
            Player player = (Player) event.getWhoClicked();
            Inventory inventory = holder.getInventory();

            BonusSet savedSet = new BonusSet(
                    holder.getSetId(),
                    holder.getSetId(),
                    prepareItem(inventory.getItem(SetEditorHolder.HELMET_SLOT), holder.getSetId(), holder.isHelmetUnbreakable()),
                    prepareItem(inventory.getItem(SetEditorHolder.CHESTPLATE_SLOT), holder.getSetId(), holder.isChestplateUnbreakable()),
                    prepareItem(inventory.getItem(SetEditorHolder.LEGGINGS_SLOT), holder.getSetId(), holder.isLeggingsUnbreakable()),
                    prepareItem(inventory.getItem(SetEditorHolder.BOOTS_SLOT), holder.getSetId(), holder.isBootsUnbreakable()),
                    prepareItem(inventory.getItem(SetEditorHolder.MAINHAND_SLOT), holder.getSetId(), false),
                    prepareItem(inventory.getItem(SetEditorHolder.OFFHAND_SLOT), holder.getSetId(), false),
                    holder.getRequiredPieces(),
                    holder.getAttributes(),
                    "bonussets.use." + holder.getSetId().toLowerCase()
            );

            Main.getInstance().getSetManager().saveSet(savedSet);

            player.sendMessage("§aSet '" + holder.getSetId() + "' saved!");
            player.closeInventory();
        }
    }

    private ItemStack prepareItem(ItemStack item, String setId, boolean makeUnbreakable) {
        if (item == null) return null;

        ItemStack result = item.clone();
        ItemMeta meta = result.getItemMeta();
        if (meta != null) {
            meta.setUnbreakable(makeUnbreakable);

            NamespacedKey pdcKey = new NamespacedKey(Main.getInstance(), "bs_set_id");
            meta.getPersistentDataContainer().set(pdcKey, PersistentDataType.STRING, setId);

            result.setItemMeta(meta);
        }
        return result;
    }
}