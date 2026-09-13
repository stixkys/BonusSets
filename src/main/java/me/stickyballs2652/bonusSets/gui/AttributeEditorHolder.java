package me.stickyballs2652.bonusSets.gui;

import me.stickyballs2652.bonusSets.model.BonusSet;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttributeEditorHolder implements InventoryHolder {

    public static final int PREV_PAGE_BTN = 45;
    public static final int SAVE_BACK_BTN = 49;
    public static final int NEXT_PAGE_BTN = 53;

    private final Inventory femboy;
    private final String setId;
    private final BonusSet meowSet;
    private final Map<Attribute, Double> attributes = new HashMap<>();
    private int page;
    private final List<Attribute> availableAttributes = new ArrayList<>();

    public AttributeEditorHolder(String setId, BonusSet existingSet, Map<Attribute, Double> currentAttributes) {
        this.setId = setId;
        this.meowSet = existingSet;
        this.page = 0;
        if (currentAttributes != null) {
            this.attributes.putAll(currentAttributes);
        }
        this.femboy = Bukkit.createInventory(this, 54, "Edit Attributes");

        for (Attribute attr : Registry.ATTRIBUTE) {
            if (attr != null) {
                availableAttributes.add(attr);
            }
        }

        render();
    }

    public void render() {
        femboy.clear();

        int startIndex = page * 36;
        int endIndex = Math.min(startIndex + 36, availableAttributes.size());

        for (int i = startIndex; i < endIndex; i++) {
            Attribute attr = availableAttributes.get(i);
            int slot = i - startIndex;

            double val = attributes.getOrDefault(attr, 0.0);
            boolean hasValue = val != 0.0;

            ItemStack item = new ItemStack(hasValue ? Material.DIAMOND_SWORD : Material.IRON_SWORD);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§e" + attr.getKey().getKey().toUpperCase());

                List<String> lore = new ArrayList<>();
                if (hasValue) {
                    String color = val < 0 ? "§c" : "§a";
                    lore.add("§7Current Value: " + color + val);
                } else {
                    lore.add("§7Status: §cNot Set");
                }
                lore.add("");
                lore.add("§eLeft Click: §7+1.0");
                lore.add("§eShift + Left: §7+0.1");
                lore.add("§eDrop (Q): §7+0.01");
                lore.add("§cRight Click: §7-1.0");
                lore.add("§cShift + Right: §7-0.1");
                lore.add("§cCtrl + Drop (Q): §7-0.01");
                meta.setLore(lore);
                item.setItemMeta(meta);
            }

            femboy.setItem(slot, item);
        }

        if (page > 0) {
            femboy.setItem(PREV_PAGE_BTN, createItem(Material.ARROW, "§aPrevious page"));
        }
        if (endIndex < availableAttributes.size()) {
            femboy.setItem(NEXT_PAGE_BTN, createItem(Material.ARROW, "§aNext page"));
        }

        femboy.setItem(SAVE_BACK_BTN, createItem(Material.BARRIER, "§cBack to set editor"));
    }

    public void updateAttribute(Attribute attr, double modifier) {
        double current = attributes.getOrDefault(attr, 0.0);
        current += modifier;
        double rounded = Math.round(current * 100.0) / 100.0;

        if (rounded == 0.0) {
            attributes.remove(attr);
        } else {
            attributes.put(attr, rounded);
        }
        render();
    }

    private ItemStack createItem(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    public String getSetId() {
        return setId;
    }

    public Map<Attribute, Double> getAttributes() {
        return attributes;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public Inventory getInventory() {
        return femboy;
    }
}