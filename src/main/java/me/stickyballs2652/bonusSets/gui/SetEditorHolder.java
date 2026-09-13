package me.stickyballs2652.bonusSets.gui;

import me.stickyballs2652.bonusSets.model.BonusSet;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetEditorHolder implements InventoryHolder {

    private final Inventory femboy;
    private final String setId;
    private final Map<Attribute, Double> attributes = new HashMap<>();
    private final List<PotionEffect> potionEffects = new ArrayList<>();
    private int requiredPieces = 1;
    private boolean enabled;

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

    public static final int ATTRIBUTE_BTN = 38;
    public static final int POTION_BTN = 39;
    public static final int THRESHOLD_BTN = 40;
    public static final int TOGGLE_ENABLE_BTN = 41;
    public static final int SAVE_BTN = 43;
    public static final int DELETE_BTN = 44;

    public SetEditorHolder(String setId, BonusSet existingSet) {
        this.setId = setId;
        this.femboy = Bukkit.createInventory(this, 54, "Editing set: " + setId);

        if (existingSet != null) {
            this.attributes.putAll(existingSet.attributes());
            this.requiredPieces = existingSet.requiredPieces();
            this.enabled = existingSet.isEnabled();
            loadExistingItems(existingSet);
        } else {
            this.enabled = true;
        }

        renderControls();
    }

    private void loadExistingItems(BonusSet set) {
        if (set.helmet() != null && set.helmet().getType() != Material.AIR) {
            femboy.setItem(HELMET_SLOT, set.helmet().clone());
            if (set.helmet().hasItemMeta()) helmetUnbreakable = set.helmet().getItemMeta().isUnbreakable();
        }
        if (set.chestplate() != null && set.chestplate().getType() != Material.AIR) {
            femboy.setItem(CHESTPLATE_SLOT, set.chestplate().clone());
            if (set.chestplate().hasItemMeta()) chestplateUnbreakable = set.chestplate().getItemMeta().isUnbreakable();
        }
        if (set.leggings() != null && set.leggings().getType() != Material.AIR) {
            femboy.setItem(LEGGINGS_SLOT, set.leggings().clone());
            if (set.leggings().hasItemMeta()) leggingsUnbreakable = set.leggings().getItemMeta().isUnbreakable();
        }
        if (set.boots() != null && set.boots().getType() != Material.AIR) {
            femboy.setItem(BOOTS_SLOT, set.boots().clone());
            if (set.boots().hasItemMeta()) bootsUnbreakable = set.boots().getItemMeta().isUnbreakable();
        }

        if (set.potionEffects() != null) {
            this.potionEffects.addAll(set.potionEffects());
        }

        if (set.mainhand() != null && set.mainhand().getType() != Material.AIR) femboy.setItem(MAINHAND_SLOT, set.mainhand().clone());
        if (set.offhand() != null && set.offhand().getType() != Material.AIR) femboy.setItem(OFFHAND_SLOT, set.offhand().clone());
    }

    public void renderControls() {
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta gMeta = glass.getItemMeta();
        if (gMeta != null) {
            gMeta.setDisplayName(" ");
            glass.setItemMeta(gMeta);
        }

        for (int i = 0; i < femboy.getSize(); i++) {
            if (isInputSlot(i)) continue;
            if (isDisplaySlot(i)) continue;
            if (i == HELMET_TOGGLE || i == CHESTPLATE_TOGGLE || i == LEGGINGS_TOGGLE || i == BOOTS_TOGGLE) continue;
            if (i == ATTRIBUTE_BTN || i == POTION_BTN || i == THRESHOLD_BTN || i == TOGGLE_ENABLE_BTN || i == SAVE_BTN || i == DELETE_BTN) continue;
            femboy.setItem(i, glass);
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
        femboy.setItem(ATTRIBUTE_BTN, attrItem);

        ItemStack potionItem = new ItemStack(Material.BREWING_STAND);
        ItemMeta pMeta = potionItem.getItemMeta();
        if (pMeta != null) {
            pMeta.setDisplayName("§dSet Potion Effects");
            List<String> lore = new ArrayList<>();
            lore.add("§7Click to edit effects:");
            if (potionEffects.isEmpty()) {
                lore.add("§cNone");
            } else {
                potionEffects.forEach(eff -> lore.add("§e- " + eff.getType().getKey().getKey().toUpperCase() + " " + (eff.getAmplifier() + 1)));
            }
            pMeta.setLore(lore);
            potionItem.setItemMeta(pMeta);
        }
        femboy.setItem(POTION_BTN, potionItem);

        ItemStack thresholdItem = new ItemStack(Material.COMPARATOR);
        ItemMeta tMeta = thresholdItem.getItemMeta();
        if (tMeta != null) {
            tMeta.setDisplayName("§eRequired Pieces: §f" + requiredPieces);
            tMeta.setLore(List.of("§7Click to cycle required piece count."));
            thresholdItem.setItemMeta(tMeta);
        }
        femboy.setItem(THRESHOLD_BTN, thresholdItem);

        ItemStack toggleEnableItem = new ItemStack(enabled ? Material.LIME_DYE : Material.GRAY_DYE);
        ItemMeta toggleMeta = toggleEnableItem.getItemMeta();
        if (toggleMeta != null) {
            toggleMeta.setDisplayName(enabled ? "§aStatus: Enabled" : "§cStatus: Disabled");
            toggleMeta.setLore(List.of("§7Click to toggle this set active or inactive."));
            toggleEnableItem.setItemMeta(toggleMeta);
        }
        femboy.setItem(TOGGLE_ENABLE_BTN, toggleEnableItem);

        ItemStack saveItem = new ItemStack(Material.LIME_WOOL);
        ItemMeta sMeta = saveItem.getItemMeta();
        if (sMeta != null) {
            sMeta.setDisplayName("§a§lSave Set");
            sMeta.setLore(List.of("§7Click to save set configuration."));
            saveItem.setItemMeta(sMeta);
        }
        femboy.setItem(SAVE_BTN, saveItem);

        ItemStack deleteItem = new ItemStack(Material.RED_CONCRETE);
        ItemMeta dMeta = deleteItem.getItemMeta();
        if (dMeta != null) {
            dMeta.setDisplayName("§c§lDelete Set");
            dMeta.setLore(List.of("§7Click to delete this set permanently."));
            deleteItem.setItemMeta(dMeta);
        }
        femboy.setItem(DELETE_BTN, deleteItem);
    }

    private void setToggleItem(int slot, String piece, boolean state) {
        ItemStack item = new ItemStack(state ? Material.ANVIL : Material.DAMAGED_ANVIL);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(state ? "§a" + piece + ": Unbreakable" : "§c" + piece + ": Normal");
            meta.setLore(List.of("§7Click to toggle unbreakable state."));
            item.setItemMeta(meta);
        }
        femboy.setItem(slot, item);
    }

    private void setDisplayPlaceholder(int slot, Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(List.of("§7Place the actual piece in the slot below."));
            item.setItemMeta(meta);
        }
        femboy.setItem(slot, item);
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

    public boolean isEnabled() {
        return enabled;
    }
    public void toggleEnabled() {
        this.enabled = !this.enabled;
    }

    public String getSetId() {
        return setId;
    }
    public Map<Attribute, Double> getAttributes() {
        return attributes;
    }
    public List<PotionEffect> getPotionEffects() {
        return potionEffects;
    }
    public int getRequiredPieces() {
        return requiredPieces;
    }
    public void setRequiredPieces(int count) {
        this.requiredPieces = count;
    }

    @Override
    public Inventory getInventory() {
        return femboy;
    }
}