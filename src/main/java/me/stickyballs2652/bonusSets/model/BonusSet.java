package me.stickyballs2652.bonusSets.model;

import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;

import org.bukkit.attribute.Attribute;
import java.util.Map;

public record BonusSet(
        String id,
        String displayName,
        Material helmet,
        Material chestplate,
        Material leggings,
        Material boots,
        Map<Attribute, AttributeModifier> attributes,
        String permission
) {

    public boolean matches(Material helm, Material chest, Material legs, Material sniffsniff) {
        return (helmet == null || helmet == helm) &&
               (chestplate == null || chestplate == chest) &&
               (leggings == null || leggings == legs) &&
               (boots == null || boots == sniffsniff);
    }
}
