package me.stickyballs2652.bonusSets.gui;

import me.stickyballs2652.bonusSets.model.BonusSet;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class PotionEffectEditorHolder implements InventoryHolder {

    public static final int PREV_PAGE_BTN = 45;
    public static final int SAVE_BACK_BTN = 49;
    public static final int NEXT_PAGE_BTN = 53;

    private final Inventory inventory;
    private final String setId;
    private final BonusSet set;
    private final List<PotionEffect> effects = new ArrayList<>();
    private int page;
    private final List<PotionEffectType> availableTypes = new ArrayList<>();

    public PotionEffectEditorHolder(String setId, BonusSet existingSet, List<PotionEffect> currentEffects) {
        this.setId = setId;
        this.set = existingSet;
        this.page = 0;
        if (currentEffects != null) {
            this.effects.addAll(currentEffects);
        }
        this.inventory = Bukkit.createInventory(this, 54, "Edit Potion Effects");

        for (PotionEffectType type : Registry.POTION_EFFECT_TYPE) {
            if (type != null) {
                availableTypes.add(type);
            }
        }

        render();
    }

    public void render() {
        inventory.clear();

        int startIndex = page * 36;
        int endIndex = Math.min(startIndex + 36, availableTypes.size());

        for (int i = startIndex; i < endIndex; i++) {
            PotionEffectType type = availableTypes.get(i);
            int slot = i - startIndex;

            PotionEffect activeEffect = getActiveEffect(type);
            boolean hasEffect = activeEffect != null;

            ItemStack item = new ItemStack(hasEffect ? Material.POTION : Material.GRAY_DYE);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§e" + type.getKey().getKey().toUpperCase());

                List<String> lore = new ArrayList<>();
                if (hasEffect) {
                    lore.add("§7Active Amplifier: §a" + (activeEffect.getAmplifier() + 1));
                    lore.add("§7Ambient/Particles: §f" + activeEffect.hasParticles());
                } else {
                    lore.add("§7Status: §cInactive");
                }
                lore.add("");
                lore.add("§eLeft Click: §7Add / Increase Tier");
                lore.add("§cRight Click: §7Decrease Tier / Remove");
                meta.setLore(lore);
                item.setItemMeta(meta);
            }

            inventory.setItem(slot, item);
        }

        if (page > 0) {
            inventory.setItem(PREV_PAGE_BTN, createItem(Material.ARROW, "§aPrevious page"));
        }
        if (endIndex < availableTypes.size()) {
            inventory.setItem(NEXT_PAGE_BTN, createItem(Material.ARROW, "§aNext page"));
        }

        inventory.setItem(SAVE_BACK_BTN, createItem(Material.BARRIER, "§cBack to set editor"));
    }

    private PotionEffect getActiveEffect(PotionEffectType type) {
        for (PotionEffect effect : effects) {
            if (effect.getType().equals(type)) {
                return effect;
            }
        }
        return null;
    }

    public void updateEffect(PotionEffectType type, boolean increase) {
        PotionEffect current = getActiveEffect(type);
        effects.removeIf(e -> e.getType().equals(type));

        int newAmplifier = 0;
        if (current != null) {
            newAmplifier = current.getAmplifier() + (increase ? 1 : -1);
        } else if (increase) {
            newAmplifier = 0;
        } else {
            return;
        }

        if (newAmplifier >= 0) {
            effects.add(new PotionEffect(type, Integer.MAX_VALUE, newAmplifier, true, false));
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
    public List<PotionEffect> getEffects() {
        return effects;
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
    public Inventory getInventory() { return inventory; }
}