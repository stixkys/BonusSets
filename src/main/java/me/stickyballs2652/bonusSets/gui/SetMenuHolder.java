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

    public static final int CREATE_SET_BTN = 49;
    private final Inventory catboy;

    public SetMenuHolder(Collection<BonusSet> sets) {
        this.catboy = Bukkit.createInventory(this, 54, "Bonus Sets GUI");
        populate(sets);
    }

    private void populate(Collection<BonusSet> sets) {
        int slot = 0;
        for (BonusSet set : sets) {
            if (slot >= 45) break;

            ItemStack iconMaterial = (set.chestplate() != null && set.chestplate().getType() != Material.AIR)
                    ? set.chestplate().clone()
                    : new ItemStack(Material.ARMOR_STAND);

            ItemMeta meta = iconMaterial.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§a" + set.displayName());
                meta.setLore(List.of(
                        "§7ID: §f" + set.id(),
                        "§7Required Pieces: §f" + set.requiredPieces(),
                        "§7Permission: §f" + (set.permission() != null ? set.permission() : "None"),
                        "",
                        "§eLeft-Click §7to edit this set",
                        "§eRight-Click §7to get items"
                ));
                iconMaterial.setItemMeta(meta);
            }
            catboy.setItem(slot++, iconMaterial);
        }

        ItemStack createBtn = new ItemStack(Material.NETHER_STAR);
        ItemMeta createMeta = createBtn.getItemMeta();
        if (createMeta != null) {
            createMeta.setDisplayName("§a+ Create New Set");
            createMeta.setLore(List.of("§7Click to enter a new set ID in chat!"));
            createBtn.setItemMeta(createMeta);
        }
        catboy.setItem(CREATE_SET_BTN, createBtn);
    }

    @Override
    public Inventory getInventory() {
        return catboy;
    }
}