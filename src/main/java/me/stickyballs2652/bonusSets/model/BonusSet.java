package me.stickyballs2652.bonusSets.model;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;

import java.util.List;

public record BonusSet(
        String id,
        String displayname,
        Material helmet,
        Material chestplate,
        Material leggings,
        Material boots,
        List<PotionEffect> effects,
        String permission
) {

    public boolean matches(Material helm, Material chest, Material legs, Material sniffsniff) {
        return (helmet == null || helmet == helm) &&
               (chestplate == null || chestplate == chest) &&
               (leggings == null || leggings == legs) &&
               (boots == null || boots == sniffsniff);
    }
}
