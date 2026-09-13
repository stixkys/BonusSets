package me.stickyballs2652.bonusSets.gui;

import me.stickyballs2652.bonusSets.model.BonusSet;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class DeleteConfirmationHolder implements InventoryHolder {

    public static final int CONFIRM_BTN = 11;
    public static final int CANCEL_BTN = 15;

    private final Inventory meoww;
    private final BonusSet targetSet;

    public DeleteConfirmationHolder(BonusSet set) {
        this.targetSet = set;
        this.meoww = Bukkit.createInventory(this, 27, "Delete Set: " + set.id());
        render();
    }

    private void render() {
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        if (fillerMeta != null) {
            fillerMeta.setDisplayName(" ");
            filler.setItemMeta(fillerMeta);
        }
        for (int i = 0; i < meoww.getSize(); i++) {
            meoww.setItem(i, filler);
        }

        ItemStack confirmItem = new ItemStack(Material.RED_WOOL);
        ItemMeta confirmMeta = confirmItem.getItemMeta();
        if (confirmMeta != null) {
            confirmMeta.setDisplayName("§c§lConfirm delete");
            confirmMeta.setLore(List.of(
                    "§7Permanently delete bonus set:",
                    "§e" + targetSet.id(),
                    "",
                    "§cThis action cannot be undone!"
            ));
            confirmItem.setItemMeta(confirmMeta);
        }
        meoww.setItem(CONFIRM_BTN, confirmItem);

        ItemStack cancelItem = new ItemStack(Material.GREEN_WOOL);
        ItemMeta cancelMeta = cancelItem.getItemMeta();
        if (cancelMeta != null) {
            cancelMeta.setDisplayName("§a§lCancel");
            cancelMeta.setLore(List.of("§7Return to the previous menu."));
            cancelItem.setItemMeta(cancelMeta);
        }
        meoww.setItem(CANCEL_BTN, cancelItem);
    }

    public BonusSet getTargetSet() {
        return targetSet;
    }

    @Override
    public Inventory getInventory() {
        return meoww;
    }
}