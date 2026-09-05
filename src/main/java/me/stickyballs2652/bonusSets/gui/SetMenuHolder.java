package me.stickyballs2652.bonusSets.gui;

import me.stickyballs2652.bonusSets.model.BonusSet;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.List;

public class SetMenuHolder implements InventoryHolder {

    private final Inventory meoww;

    public SetMenuHolder(Collection<BonusSet> sets) {
        this.meoww = Bukkit.createInventory(this, 54, "Bonus Sets GUI");
        populate(sets);
    }

    private void populate(Collection<BonusSet> sets) {
        int slot = 0;
        for (BonusSet set : sets) {
            if (slot >= 54) break;

            ItemStack iconMaterial = (set.chestplate() != null) ? set.chestplate().clone() : new ItemStack(Material.ARMOR_STAND);
            ItemMeta meta = iconMaterial.getItemMeta();

            if (meta != null) {
                meta.setDisplayName("§a" + set.displayName());
                meta.setLore(List.of(
                        "§7ID: §f" + set.id(),
                        "§7Required Pieces: §f" + set.requiredPieces(),
                        "§7Permission: §f" + (set.permission() != null ? set.permission() : "None"),
                        "§eClick to edit this set"
                ));
                iconMaterial.setItemMeta(meta);
            }
            meoww.setItem(slot++, iconMaterial);
        }
    }

    @Override
    public Inventory getInventory() {
        return meoww;
    }
}