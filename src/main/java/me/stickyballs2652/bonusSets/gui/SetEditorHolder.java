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

    private final Inventory inventory;
    private final String setId;
    private final Map<Attribute, Double> attributes = new HashMap<>();
    private int requiredPieces = 1;

    private boolean helmetUnbreakable = false;
    private boolean chestplateUnbreakable = false;
    private boolean leggingsUnbreakable = false;
    private boolean bootsUnbreakable = false;

    public static final int HELMET_DISP = 10;
    public static final int CHESTPLATE_DISP = 11;
    public static final int LEGGINGS_DISP = 12;
    public static final int BOOTS_DISP = 13;
    public static final int MAINHAND_DISP = 14;
    public static final int OFFHAND_DISP = 15;

    public static final int HELMET_SLOT = 19;
    public static final int CHESTPLATE_SLOT = 20;
    public static final int LEGGINGS_SLOT = 21;
    public static final int BOOTS_SLOT = 22;
    public static final int MAINHAND_SLOT = 23;
    public static final int OFFHAND_SLOT = 24;

    public static final int HELMET_TOGGLE = 28;
    public static final int CHESTPLATE_TOGGLE = 29;
    public static final int LEGGINGS_TOGGLE = 30;
    public static final int BOOTS_TOGGLE = 31;

    public static final int ATTRIBUTE_BTN = 39;
    public static final int THRESHOLD_BTN = 41;
    public static final int SAVE_BTN = 43;

    public SetEditorHolder(String setId, BonusSet existingSet) {
        this.setId = setId;
        this.inventory = Bukkit.createInventory(this, 54, "Editing set: " + setId);

        renderControls();

        if (existingSet != null) {
            this.attributes.putAll(existingSet.attributes());
            this.requiredPieces = existingSet.requiredPieces();
            loadExistingItems(existingSet);
            renderControls();
        }
    }

    private void loadExistingItems(BonusSet set) {
        if (set.helmet() != null) {
            inventory.setItem(HELMET_SLOT, set.helmet().clone());
            if (set.helmet().hasItemMeta()) helmetUnbreakable = set.helmet().getItemMeta().isUnbreakable();
        }
        if (set.chestplate() != null) {
            inventory.setItem(CHESTPLATE_SLOT, set.chestplate().clone());
            if (set.chestplate().hasItemMeta()) chestplateUnbreakable = set.chestplate().getItemMeta().isUnbreakable();
        }
        if (set.leggings() != null) {
            inventory.setItem(LEGGINGS_SLOT, set.leggings().clone());
            if (set.leggings().hasItemMeta()) leggingsUnbreakable = set.leggings().getItemMeta().isUnbreakable();
        }
        if (set.boots() != null) {
            inventory.setItem(BOOTS_SLOT, set.boots().clone());
            if (set.boots().hasItemMeta()) bootsUnbreakable = set.boots().getItemMeta().isUnbreakable();
        }
        if (set.mainhand() != null) inventory.setItem(MAINHAND_SLOT, set.mainhand().clone());
        if (set.offhand() != null) inventory.setItem(OFFHAND_SLOT, set.offhand().clone());
    }

    public void renderControls() {
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta gMeta = glass.getItemMeta();
        if (gMeta != null) {
            gMeta.setDisplayName(" ");
            glass.setItemMeta(gMeta);
        }

        for (int i = 0; i < inventory.getSize(); i++) {
            if (isInputSlot(i)) continue;
            if (isDisplaySlot(i)) continue;
            if (i == HELMET_TOGGLE || i == CHESTPLATE_TOGGLE || i == LEGGINGS_TOGGLE || i == BOOTS_TOGGLE) continue;
            if (i == ATTRIBUTE_BTN || i == THRESHOLD_BTN || i == SAVE_BTN) continue;
            inventory.setItem(i, glass);
        }

        setDisplayPlaceholder(HELMET_DISP, Material.CHAINMAIL_HELMET, "§eHelmet Slot");
        setDisplayPlaceholder(CHESTPLATE_DISP, Material.CHAINMAIL_CHESTPLATE, "§eChestplate Slot");
        setDisplayPlaceholder(LEGGINGS_DISP, Material.CHAINMAIL_LEGGINGS, "§eLeggings Slot");
        setDisplayPlaceholder(BOOTS_DISP, Material.CHAINMAIL_BOOTS, "§eBoots Slot");
        setDisplayPlaceholder(MAINHAND_DISP, Material.WOODEN_SWORD, "§eMainhand Slot");
        setDisplayPlaceholder(OFFHAND_DISP, Material.SHIELD, "§eOffhand Slot");

        setToggleItem(HELMET_TOGGLE, "Helmet", helmetUnbreakable);
        setToggleItem(CHESTPLATE_TOGGLE, "Chestplate", chestplateUnbreakable);
        setToggleItem(LEGGINGS_TOGGLE, "Leggings", leggingsUnbreakable);
        setToggleItem(BOOTS_TOGGLE, "Boots", bootsUnbreakable);

        ItemStack attrItem = new ItemStack(Material.NETHER_STAR);
        ItemMeta attrMeta = attrItem.getItemMeta();
        if (attrMeta != null) {
            attrMeta.setDisplayName("§bSet attributes");
            List<String> lore = new ArrayList<>();
            lore.add("§7Click to edit attributes:");
            attributes.forEach((attr, val) -> lore.add("§e- " + attr.getKey().getKey().toUpperCase() + ": §a" + (val >= 0 ? "+" : "") + val));
            attrMeta.setLore(lore);
            attrItem.setItemMeta(attrMeta);
        }
        inventory.setItem(ATTRIBUTE_BTN, attrItem);

        ItemStack thresholdItem = new ItemStack(Material.COMPARATOR);
        ItemMeta tMeta = thresholdItem.getItemMeta();
        if (tMeta != null) {
            tMeta.setDisplayName("§eRequired Pieces: §f" + requiredPieces);
            tMeta.setLore(List.of("§7Click to cycle required piece count."));
            thresholdItem.setItemMeta(tMeta);
        }
        inventory.setItem(THRESHOLD_BTN, thresholdItem);

        ItemStack saveItem = new ItemStack(Material.LIME_WOOL);
        ItemMeta sMeta = saveItem.getItemMeta();
        if (sMeta != null) {
            sMeta.setDisplayName("§a§lSave set");
            sMeta.setLore(List.of("§7Click to save set configuration."));
            saveItem.setItemMeta(sMeta);
        }
        inventory.setItem(SAVE_BTN, saveItem);
    }

    private void setToggleItem(int slot, String piece, boolean state) {
        ItemStack item = new ItemStack(state ? Material.ANVIL : Material.DAMAGED_ANVIL);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(state ? "§a" + piece + ": Unbreakable" : "§c" + piece + ": Normal");
            meta.setLore(List.of("§7Click to toggle unbreakable state."));
            item.setItemMeta(meta);
        }
        inventory.setItem(slot, item);
    }

    private void setDisplayPlaceholder(int slot, Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(List.of("§7Place the actual piece in the slot below."));
            item.setItemMeta(meta);
        }
        inventory.setItem(slot, item);
    }

    public boolean isInputSlot(int slot) {
        return slot == HELMET_SLOT || slot == CHESTPLATE_SLOT || slot == LEGGINGS_SLOT
                || slot == BOOTS_SLOT || slot == MAINHAND_SLOT || slot == OFFHAND_SLOT;
    }

    public boolean isDisplaySlot(int slot) {
        return slot == HELMET_DISP || slot == CHESTPLATE_DISP || slot == LEGGINGS_DISP
                || slot == BOOTS_DISP || slot == MAINHAND_DISP || slot == OFFHAND_DISP;
    }

    public boolean isHelmetUnbreakable() {
        return helmetUnbreakable;
    }
    public boolean isChestplateUnbreakable() {
        return chestplateUnbreakable;
    }
    public boolean isLeggingsUnbreakable() {
        return leggingsUnbreakable;
    }
    public boolean isBootsUnbreakable() {
        return bootsUnbreakable;
    }

    public void toggleHelmetUnbreakable() {
        this.helmetUnbreakable = !this.helmetUnbreakable;
    }
    public void toggleChestplateUnbreakable() {
        this.chestplateUnbreakable = !this.chestplateUnbreakable;
    }
    public void toggleLeggingsUnbreakable() {
        this.leggingsUnbreakable = !this.leggingsUnbreakable;
    }
    public void toggleBootsUnbreakable() {
        this.bootsUnbreakable = !this.bootsUnbreakable;
    }

    public String getSetId() {
        return setId;
    }
    public Map<Attribute, Double> getAttributes() {return attributes;}
    public int getRequiredPieces() {
        return requiredPieces;
    }
    public void setRequiredPieces(int count) {
        this.requiredPieces = count;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}