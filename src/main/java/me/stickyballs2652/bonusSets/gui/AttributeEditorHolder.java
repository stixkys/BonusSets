package me.stickyballs2652.bonusSets.gui;

import me.stickyballs2652.bonusSets.model.BonusSet;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class AttributeEditorHolder implements InventoryHolder {

    private final Inventory insideheh;
    private final String setId;
    private final BonusSet existingSet;
    private final Map<Attribute, Double> attributes;
    private final List<Attribute> allAttributes = new ArrayList<>();
    private int page = 0;

    public static final int PREV_PAGE_BTN = 45;
    public static final int SAVE_BACK_BTN = 49;
    public static final int NEXT_PAGE_BTN = 53;

    public AttributeEditorHolder(String setId, BonusSet existingSet, Map<Attribute, Double> attributes) {
        this.setId = setId;
        this.existingSet = existingSet;
        this.attributes = attributes;
        this.insideheh = Bukkit.createInventory(this, 54, "Edit attributes: " + setId);

        this.allAttributes.addAll(Arrays.asList(Attribute.values()));
        this.allAttributes.sort(Comparator.comparing(Enum::name));


        render();
    }

    public void render() {
        insideheh.clear();

        int maxItemsPerPage = 36;
        int startIndex = page * maxItemsPerPage;
        int endIndex = Math.min(startIndex + maxItemsPerPage, allAttributes.size());

        for (int i = startIndex; i < endIndex; i++) {
            Attribute attr = allAttributes.get(i);
            double currentVal = attributes.getOrDefault(attr, 0.0);

            ItemStack item = new ItemStack(currentVal > 0 ? Material.LIME_DYE : Material.GRAY_DYE);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§e " + attr.name());
                meta.setLore(List.of(
                        "§7Current Bonus: §a+" + currentVal,
                        "§eLeft-Click: §f+1.0",
                        "§eShift + Left-Click: §f+0.1",
                        "§cRight-Click: §f-1.0",
                        "§cShift + Right-Click: §f-0.1"
                ));
                item.setItemMeta(meta);
            }
            insideheh.setItem(i - startIndex, item);
        }
        ItemStack glass =  new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta gMeta = glass.getItemMeta();
        if (gMeta != null) {
            gMeta.setDisplayName(" ");
            glass.setItemMeta(gMeta);
        }
        for (int i = 36; i < 45; i++) {
            insideheh.setItem(i, glass);
        }

        if (page > 0) {
            ItemStack prev = new ItemStack(Material.PAPER);
            ItemMeta pMeta = prev.getItemMeta();
            if (pMeta != null) {
                pMeta.setDisplayName("§ePrevious page (" + page + ")");
                prev.setItemMeta(pMeta);
            }
            insideheh.setItem(PREV_PAGE_BTN, prev);
        }

        if (endIndex < allAttributes.size()) {
            ItemStack next = new ItemStack(Material.PAPER);
            ItemMeta nMeta = next.getItemMeta();
            if (nMeta != null) {
                nMeta.setDisplayName("§eNext page (" + (page + 2) + ")");
                next.setItemMeta(nMeta);
            }
            insideheh.setItem(NEXT_PAGE_BTN, next);
        }

        ItemStack saveBack = new ItemStack(Material.ARROW);
        ItemMeta saveMeta = saveBack.getItemMeta();
        if (saveMeta != null) {
            saveMeta.setDisplayName("§aSave and go back");
            saveBack.setItemMeta(saveMeta);
        }
        insideheh.setItem(SAVE_BACK_BTN, saveBack);
    }

    public String getSetId() {
        return setId;
    }
    public BonusSet getExistingSet() {
        return existingSet;
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
        return insideheh;
    }
}
