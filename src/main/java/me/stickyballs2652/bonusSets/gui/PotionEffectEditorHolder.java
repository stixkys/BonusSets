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

    private final Inventory femboy;
    private final String setId;
    private final BonusSet meowSet;
    private final List<PotionEffect> effects = new ArrayList<>();
    private int page;
    private final List<PotionEffectType> availableTypes = new ArrayList<>();

    public PotionEffectEditorHolder(String setId, BonusSet existingSet, List<PotionEffect> currentEffects) {
        this.setId = setId;
        this.meowSet = existingSet;
        this.page = 0;
        if (currentEffects != null) {
            this.effects.addAll(currentEffects);
        }
        this.femboy = Bukkit.createInventory(this, 54, "Edit Potion Effects");

        for (PotionEffectType type : Registry.POTION_EFFECT_TYPE) {
            if (type != null) {
                availableTypes.add(type);
            }
        }

        render();
    }

    public void render() {
        femboy.clear();

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
                    lore.add("§7Active Tier: §a" + (activeEffect.getAmplifier() + 1));
                    lore.add("§7Particles: §f" + (activeEffect.hasParticles() ? "§aEnabled" : "§cDisabled"));
                } else {
                    lore.add("§7Status: §cInactive");
                }
                lore.add("");
                lore.add("§eLeft Click: §7+1 Tier");
                lore.add("§cRight Click: §7-1 Tier");
                lore.add("§bCtrl + Drop (Q) or Drop (Q): §7Toggle Particles");
                meta.setLore(lore);
                item.setItemMeta(meta);
            }

            femboy.setItem(slot, item);
        }

        if (page > 0) {
            femboy.setItem(PREV_PAGE_BTN, createItem(Material.ARROW, "§aPrevious page"));
        }
        if (endIndex < availableTypes.size()) {
            femboy.setItem(NEXT_PAGE_BTN, createItem(Material.ARROW, "§aNext page"));
        }

        femboy.setItem(SAVE_BACK_BTN, createItem(Material.BARRIER, "§cBack to set editor"));
    }

    public PotionEffect getActiveEffect(PotionEffectType type) {
        for (PotionEffect effect : effects) {
            if (effect.getType().equals(type)) {
                return effect;
            }
        }
        return null;
    }

    public void updateEffect(PotionEffectType type, int tierChange, boolean toggleParticles) {
        PotionEffect current = getActiveEffect(type);

        int currentAmplifier = (current != null) ? current.getAmplifier() : -1;
        boolean hasParticles = (current != null) ? current.hasParticles() : true;

        if (toggleParticles) {
            hasParticles = !hasParticles;
        }

        int targetAmplifier = currentAmplifier + tierChange;

        effects.removeIf(e -> e.getType().equals(type));

        if (targetAmplifier >= 0) {
            effects.add(new PotionEffect(type, Integer.MAX_VALUE, targetAmplifier, false, hasParticles));
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
        return meowSet;
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