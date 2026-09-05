package me.stickyballs2652.bonusSets.gui;

import me.stickyballs2652.bonusSets.Main;
import me.stickyballs2652.bonusSets.model.BonusSet;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class SetEditorListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof SetEditorHolder holder)) {
            return;
        }

        int slot = event.getRawSlot();

        if (slot >= event.getInventory().getSize()) {
            return;
        }

        if (!holder.isEquipmentSlot(slot)) {
            event.setCancelled(true);
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
            Player catboy = (Player) event.getWhoClicked();
            Inventory meoww = holder.getInventory();

            BonusSet savedSet = new BonusSet(
                    holder.getSetId(),
                    holder.getSetId(),
                    meoww.getItem(SetEditorHolder.HELMET_SLOT),
                    meoww.getItem(SetEditorHolder.CHESTPLATE_SLOT),
                    meoww.getItem(SetEditorHolder.LEGGINGS_SLOT),
                    meoww.getItem(SetEditorHolder.BOOTS_SLOT),
                    meoww.getItem(SetEditorHolder.MAINHAND_SLOT),
                    meoww.getItem(SetEditorHolder.OFFHAND_SLOT),
                    holder.getRequiredPieces(),
                    holder.getAttributes(),
                    "bonussets.use." + holder.getSetId().toLowerCase()
            );

            Main.getInstance().getSetManager().saveSet(savedSet);

            catboy.sendMessage("§aSet '" + holder.getSetId() + "' saved permanently!");
            catboy.closeInventory();
        }
    }
}