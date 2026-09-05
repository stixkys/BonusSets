package me.stickyballs2652.bonusSets.gui;

import me.stickyballs2652.bonusSets.model.BonusSet;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetEditorHolder implements InventoryHolder {

    // mroww >.< im having alot fun..
    private final Inventory meoww;
    private final String setId;
    private final Map<Attribute, Double> attributes = new HashMap<>();
    private int requiredPieces = 1;

    public static final int HELMET_SLOT = 10;
    public static final int CHESTPLATE_SLOT = 11;
    public static final int LEGGINGS_SLOT = 12;
    public static final int BOOTS_SLOT = 13;
    public static final int MAINHAND_SLOT = 14;
    public static final int OFFHAND_SLOT = 15;

    public static final int ATTRIBUTE_BTN = 30;
    public static final int THRESHOLD_BTN = 32;
    public static final int SAVE_BTN = 34;

    public SetEditorHolder(String setId, BonusSet existingSet) {
        this.setId = setId;
        this.meoww = Bukkit.createInventory(this, 45, "Editing Set: " + setId);

        if (existingSet != null) {
            this.attributes.putAll(existingSet.attributes());
            this.requiredPieces = existingSet.requiredPieces();
            loadExistingItems(existingSet);
        }

        renderControls();
    }

    private void loadExistingItems(BonusSet set) {
        if (set.helmet() != null) meoww.setItem(HELMET_SLOT, set.helmet());
        if (set.chestplate() != null) meoww.setItem(CHESTPLATE_SLOT, set.chestplate());
        if (set.leggings() != null) meoww.setItem(LEGGINGS_SLOT, set.leggings());
        if (set.boots() != null) meoww.setItem(BOOTS_SLOT, set.boots());
        if (set.mainhand() != null) meoww.setItem(MAINHAND_SLOT, set.mainhand());
        if (set.offhand() != null) meoww.setItem(OFFHAND_SLOT, set.offhand());
    }

    public void renderControls() {
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta gMeta = glass.getItemMeta();
        if (gMeta != null) {
            gMeta.setDisplayName(" ");
            glass.setItemMeta(gMeta);
        }

        for (int i = 0; i < meoww.getSize(); i++) {
            if (isEquipmentSlot(i)) continue;
            if (i == ATTRIBUTE_BTN || i == THRESHOLD_BTN || i == SAVE_BTN) continue;
            meoww.setItem(i, glass);
        }

        ItemStack attrItem = new ItemStack(Material.NETHER_STAR);
        ItemMeta attrMeta = attrItem.getItemMeta();
        if (attrMeta != null) {
            attrMeta.setDisplayName("§bSet Attributes");
            List<String> lore = new ArrayList<>();
            lore.add("§7Click to edit attributes (e.g., Health, Speed, Scale):");
            attributes.forEach((attr, val) -> lore.add("§e- " + attr.name() + ": §a+" + val));
            attrMeta.setLore(lore);
            attrItem.setItemMeta(attrMeta);
        }
        meoww.setItem(ATTRIBUTE_BTN, attrItem);

        ItemStack thresholdItem = new ItemStack(Material.COMPARATOR);
        ItemMeta tMeta = thresholdItem.getItemMeta();
        if (tMeta != null) {
            tMeta.setDisplayName("§eRequired Pieces: §f" + requiredPieces);
            tMeta.setLore(List.of("§7Click to cycle required piece count to trigger set bonus."));
            thresholdItem.setItemMeta(tMeta);
        }
        meoww.setItem(THRESHOLD_BTN, thresholdItem);

        ItemStack saveItem = new ItemStack(Material.LIME_WOOL);
        ItemMeta sMeta = saveItem.getItemMeta();
        if (sMeta != null) {
            sMeta.setDisplayName("§a§lSAVE SET");
            sMeta.setLore(List.of("§7Click to save set configuration."));
            saveItem.setItemMeta(sMeta);
        }
        meoww.setItem(SAVE_BTN, saveItem);
    }

    public boolean isEquipmentSlot(int slot) {
        return slot == HELMET_SLOT || slot == CHESTPLATE_SLOT || slot == LEGGINGS_SLOT
                || slot == BOOTS_SLOT || slot == MAINHAND_SLOT || slot == OFFHAND_SLOT;
    }

    public String getSetId() { return setId; }
    public Map<Attribute, Double> getAttributes() { return attributes; }
    public int getRequiredPieces() { return requiredPieces; }
    public void setRequiredPieces(int count) { this.requiredPieces = count; }

    @Override
    public Inventory getInventory() {
        return meoww;
    }
}