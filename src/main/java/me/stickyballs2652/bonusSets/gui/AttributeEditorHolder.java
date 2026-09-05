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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttributeEditorHolder implements InventoryHolder {

    public static final int PREV_PAGE_BTN = 45;
    public static final int SAVE_BACK_BTN = 49;
    public static final int NEXT_PAGE_BTN = 53;

    private final Inventory inventory;
    private final String setId;
    private final BonusSet set;
    private final Map<Attribute, Double> attributes = new HashMap<>();
    private int page;
    private final List<Attribute> availableAttributes = new ArrayList<>();

    public AttributeEditorHolder(String setId, BonusSet existingSet, Map<Attribute, Double> attributes) {
        this.setId = setId;
        this.set = existingSet;
        this.page = 0;
        if (attributes != null) {
            this.attributes.putAll(attributes);
        }
        this.inventory = Bukkit.createInventory(this, 54, "Edit attributes");

        for (Attribute attr : Registry.ATTRIBUTE) {
            if (attr != null) {
                availableAttributes.add(attr);
            }
        }

        render();
    }

    public void render() {
        inventory.clear();

        int startIndex = page * 36;
        int endIndex = Math.min(startIndex + 36, availableAttributes.size());

        for (int i = startIndex; i < endIndex; i++) {
            Attribute attr = availableAttributes.get(i);
            int slot = i - startIndex;

            double currentValue = attributes.getOrDefault(attr, 0.0);

            ItemStack item = new ItemStack(currentValue > 0 ? Material.LIME_DYE : Material.GRAY_DYE);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§e" + attr.getKey().getKey().toUpperCase());
                meta.setLore(Arrays.asList(
                        "§7Current bonus: §a+" + currentValue,
                        "",
                        "§eLeft click: §7+1.0",
                        "§eShift Left-click: §7+0.1",
                        "§cRight click: §7-1.0",
                        "§cShift Right-click: §7-0.1"
                ));
                item.setItemMeta(meta);
            }

            inventory.setItem(slot, item);
        }

        if (page > 0) {
            inventory.setItem(PREV_PAGE_BTN, createItem(Material.ARROW, "§aPrevious page"));
        }
        if (endIndex < availableAttributes.size()) {
            inventory.setItem(NEXT_PAGE_BTN, createItem(Material.ARROW, "§aNext page"));
        }

        inventory.setItem(SAVE_BACK_BTN, createItem(Material.BARRIER, "§cBack to set 3ditor"));
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

    public BonusSet getSet() {
        return set;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}