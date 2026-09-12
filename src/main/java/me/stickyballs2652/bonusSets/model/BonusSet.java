package me.stickyballs2652.bonusSets.model;

import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.Map;

public record BonusSet(
        String id,
        String displayName,
        ItemStack helmet,
        ItemStack chestplate,
        ItemStack leggings,
        ItemStack boots,
        ItemStack mainhand,
        ItemStack offhand,
        int requiredPieces,
        Map<Attribute, Double> attributes,
        List<PotionEffect> potionEffects,
        List<String> activateCommands,
        List<String> deactivateCommands,
        String permission
) {

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