package me.stickyballs2652.bonusSets.manager;

import me.stickyballs2652.bonusSets.Main;
import me.stickyballs2652.bonusSets.model.BonusSet;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SetManager {

    private final Main plugin;
    private final Map<String, BonusSet> activeSets = new HashMap<>();
    private File file;
    private FileConfiguration config;

    public SetManager(Main plugin) {
        this.plugin = plugin;
        initFile();
        loadSets();
    }

    private void initFile() {
        file = new File(plugin.getDataFolder(), "sets.yml");
        if (!file.exists()) {
            plugin.getDataFolder().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to create sets.yml!");
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void saveSet(BonusSet set) {
        activeSets.put(set.id().toLowerCase(), set);

        String path = "sets." + set.id() + ".";
        config.set(path + "displayName", set.displayName());
        config.set(path + "requiredPieces", set.requiredPieces());
        config.set(path + "permission", set.permission());

        config.set(path + "items.helmet", set.helmet());
        config.set(path + "items.chestplate", set.chestplate());
        config.set(path + "items.leggings", set.leggings());
        config.set(path + "items.boots", set.boots());
        config.set(path + "items.mainhand", set.mainhand());
        config.set(path + "items.offhand", set.offhand());

        config.set(path + "attributes", null);
        set.attributes().forEach((attr, val) -> config.set(path + "attributes." + attr.getKey().getKey(), val));

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save set " + set.id() + " to disk");
        }
    }

    public void loadSets() {
        activeSets.clear();
        ConfigurationSection section = config.getConfigurationSection("sets");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            String path = "sets." + key + ".";
            String displayName = config.getString(path + "displayName", key);
            int requiredPieces = config.getInt(path + "requiredPieces", 1);
            String permission = config.getString(path + "permission", "bonussets.use." + key.toLowerCase());

            ItemStack helmet = config.getItemStack(path + "items.helmet");
            ItemStack chestplate = config.getItemStack(path + "items.chestplate");
            ItemStack leggings = config.getItemStack(path + "items.leggings");
            ItemStack boots = config.getItemStack(path + "items.boots");
            ItemStack mainhand = config.getItemStack(path + "items.mainhand");
            ItemStack offhand = config.getItemStack(path + "items.offhand");

            Map<Attribute, Double> attributes = new HashMap<>();
            ConfigurationSection attrSection = config.getConfigurationSection(path + "attributes");
            if (attrSection != null) {
                for (String attrKey : attrSection.getKeys(false)) {
                    Attribute attr = Registry.ATTRIBUTE.get(NamespacedKey.minecraft(attrKey.toLowerCase()));
                    if (attr != null) {
                        double val = attrSection.getDouble(attrKey);
                        attributes.put(attr, val);
                    } else {
                        plugin.getLogger().severe("Invalid attribute key: " + attrKey);
                    }
                }
            }

            BonusSet set = new BonusSet(
                    key, displayName, helmet, chestplate, leggings, boots,
                    mainhand, offhand, requiredPieces, attributes, permission
            );
            activeSets.put(key.toLowerCase(), set);
        }
    }

    public Collection<BonusSet> getSets() {
        return activeSets.values();
    }

    public BonusSet getSet(String id) {
        return activeSets.get(id.toLowerCase());
    }
}