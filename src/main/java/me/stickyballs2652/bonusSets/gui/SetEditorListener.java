package me.stickyballs2652.bonusSets.gui;

import me.stickyballs2652.bonusSets.Main;
import me.stickyballs2652.bonusSets.model.BonusSet;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class SetEditorListener implements Listener {

    private final Main plugin = Main.getInstance();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player catgirl)) return;
        if (!(event.getView().getTopInventory().getHolder() instanceof SetEditorHolder holder)) return;

        Inventory meow = event.getClickedInventory();
        if (meow == null) return;

        int rawSlot = event.getRawSlot();

        if (event.isShiftClick() && meow.equals(event.getView().getBottomInventory())) {
            event.setCancelled(true);
            return;
        }

        if (meow.equals(event.getView().getBottomInventory())) {
            if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                event.setCancelled(true);
            }
            return;
        }

        if (rawSlot < 0 || rawSlot >= 54) {
            event.setCancelled(true);
            return;
        }

        if (holder.isDisplaySlot(rawSlot)) {
            event.setCancelled(true);
            return;
        }

        if (!holder.isInputSlot(rawSlot)) {
            event.setCancelled(true);

            if (rawSlot == SetEditorHolder.HELMET_TOGGLE) {
                holder.toggleHelmetUnbreakable();
                holder.renderControls();
            } else if (rawSlot == SetEditorHolder.CHESTPLATE_TOGGLE) {
                holder.toggleChestplateUnbreakable();
                holder.renderControls();
            } else if (rawSlot == SetEditorHolder.LEGGINGS_TOGGLE) {
                holder.toggleLeggingsUnbreakable();
                holder.renderControls();
            } else if (rawSlot == SetEditorHolder.BOOTS_TOGGLE) {
                holder.toggleBootsUnbreakable();
                holder.renderControls();
            } else if (rawSlot == SetEditorHolder.THRESHOLD_BTN) {
                int current = holder.getRequiredPieces();
                current = current >= 6 ? 1 : current + 1;
                holder.setRequiredPieces(current);
                holder.renderControls();
            } else if (rawSlot == SetEditorHolder.TOGGLE_ENABLE_BTN) {
                holder.toggleEnabled();
                holder.renderControls();
                catgirl.updateInventory();
            } else if (rawSlot == SetEditorHolder.ATTRIBUTE_BTN) {
                catgirl.openInventory(new AttributeEditorHolder(holder.getSetId(), null, holder.getAttributes()).getInventory());
            } else if (rawSlot == SetEditorHolder.POTION_BTN) {
                catgirl.openInventory(new PotionEffectEditorHolder(holder.getSetId(), null, holder.getPotionEffects()).getInventory());
            } else if (rawSlot == SetEditorHolder.DELETE_BTN) {
                BonusSet set = plugin.getSetManager().getSet(holder.getSetId());
                if (set != null) {
                    catgirl.openInventory(new DeleteConfirmationHolder(set).getInventory());
                } else {
                    catgirl.closeInventory();
                }
            } else if (rawSlot == SetEditorHolder.SAVE_BTN) {
                ItemStack helmet = cloneOrNull(holder.getInventory().getItem(SetEditorHolder.HELMET_SLOT));
                ItemStack chestplate = cloneOrNull(holder.getInventory().getItem(SetEditorHolder.CHESTPLATE_SLOT));
                ItemStack leggings = cloneOrNull(holder.getInventory().getItem(SetEditorHolder.LEGGINGS_SLOT));
                ItemStack boots = cloneOrNull(holder.getInventory().getItem(SetEditorHolder.BOOTS_SLOT));
                ItemStack mainhand = cloneOrNull(holder.getInventory().getItem(SetEditorHolder.MAINHAND_SLOT));
                ItemStack offhand = cloneOrNull(holder.getInventory().getItem(SetEditorHolder.OFFHAND_SLOT));

                helmet = applyUnbreakable(helmet, holder.isHelmetUnbreakable(), holder.getSetId());
                chestplate = applyUnbreakable(chestplate, holder.isChestplateUnbreakable(), holder.getSetId());
                leggings = applyUnbreakable(leggings, holder.isLeggingsUnbreakable(), holder.getSetId());
                boots = applyUnbreakable(boots, holder.isBootsUnbreakable(), holder.getSetId());
                mainhand = applyPdc(mainhand, holder.getSetId());
                offhand = applyPdc(offhand, holder.getSetId());

                BonusSet existing = plugin.getSetManager().getSet(holder.getSetId());
                List<String> actCmds = existing != null ? existing.activateCommands() : null;
                List<String> deactCmds = existing != null ? existing.deactivateCommands() : null;

                BonusSet set = new BonusSet(
                        holder.getSetId(),
                        holder.getSetId(),
                        helmet,
                        chestplate,
                        leggings,
                        boots,
                        mainhand,
                        offhand,
                        holder.getRequiredPieces(),
                        holder.getAttributes(),
                        holder.getPotionEffects(),
                        actCmds,
                        deactCmds,
                        "bonussets.use." + holder.getSetId().toLowerCase(),
                        holder.isEnabled()
                );

                plugin.getSetManager().saveSet(set);
                if (plugin.getEquipmentListener() != null) {
                    plugin.getEquipmentListener().updatePlayerAttributes(catgirl);
                }
                catgirl.sendMessage("§aSet " + holder.getSetId() + " successfully saved! (Status: " + (holder.isEnabled() ? "Enabled" : "Disabled") + ")");
                catgirl.closeInventory();
            }
        }
    }

    private ItemStack cloneOrNull(ItemStack item) {
        return (item != null && item.getType() != Material.AIR) ? item.clone() : null;
    }

    private ItemStack applyUnbreakable(ItemStack item, boolean unbreakable, String setId) {
        if (item == null || item.getType() == Material.AIR) return null;
        item = applyPdc(item, setId);
        var meta = item.getItemMeta();
        if (meta != null) {
            meta.setUnbreakable(unbreakable);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack applyPdc(ItemStack item, String setId) {
        if (item == null || item.getType() == Material.AIR) return null;
        var meta = item.getItemMeta();
        if (meta != null) {
            NamespacedKey key = new NamespacedKey(plugin, "bs_set_id");
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, setId);
            item.setItemMeta(meta);
        }
        return item;
    }
}