package me.stickyballs2652.bonusSets.model;

import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.Map;

public class BonusSet {
    private final String id;
    private final String displayName;
    private final ItemStack helmet;
    private final ItemStack chestplate;
    private final ItemStack leggings;
    private final ItemStack boots;
    private final ItemStack mainhand;
    private final ItemStack offhand;
    private final int requiredPieces;
    private final Map<Attribute, Double> attributes;
    private final List<PotionEffect> potionEffects;
    private final List<String> activateCommands;
    private final List<String> deactivateCommands;
    private final String permission;
    private boolean enabled;

    public BonusSet(String id, String displayName, ItemStack helmet, ItemStack chestplate, ItemStack leggings,
                    ItemStack boots, ItemStack mainhand, ItemStack offhand, int requiredPieces,
                    Map<Attribute, Double> attributes, List<PotionEffect> potionEffects,
                    List<String> activateCommands, List<String> deactivateCommands, String permission, boolean enabled) {
        this.id = id;
        this.displayName = displayName;
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
        this.mainhand = mainhand;
        this.offhand = offhand;
        this.requiredPieces = requiredPieces;
        this.attributes = attributes;
        this.potionEffects = potionEffects;
        this.activateCommands = activateCommands;
        this.deactivateCommands = deactivateCommands;
        this.permission = permission;
        this.enabled = enabled;
    }

    public String id() {
        return id;
    }

    public String displayName() {
        return displayName;
    }

    public ItemStack helmet() {
        return helmet;
    }

    public ItemStack chestplate() {
        return chestplate;
    }

    public ItemStack leggings() {
        return leggings;
    }

    public ItemStack boots() {
        return boots;
    }

    public ItemStack mainhand() {
        return mainhand;
    }

    public ItemStack offhand() {
        return offhand;
    }

    public int requiredPieces() {
        return requiredPieces;
    }

    public Map<Attribute, Double> attributes() {
        return attributes;
    }

    public List<PotionEffect> potionEffects() {
        return potionEffects;
    }

    public List<String> activateCommands() {
        return activateCommands;
    }

    public List<String> deactivateCommands() {
        return deactivateCommands;
    }

    public String permission() {
        return permission;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getTotalPiecesConfigured() {
        int count = 0;
        if (helmet != null) count++;
        if (chestplate != null) count++;
        if (leggings != null) count++;
        if (boots != null) count++;
        if (mainhand != null) count++;
        if (offhand != null) count++;
        return count;
    }
}