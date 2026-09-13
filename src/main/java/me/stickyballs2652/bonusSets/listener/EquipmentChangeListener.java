package me.stickyballs2652.bonusSets.listener;

import me.stickyballs2652.bonusSets.Main;
import me.stickyballs2652.bonusSets.model.BonusSet;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class EquipmentChangeListener implements Listener {

    private final Main plugin = Main.getInstance();
    private final NamespacedKey pdcKey;
    private final Map<UUID, BukkitTask> activeParticleTasks = new HashMap<>();
    private final Map<UUID, Set<String>> activePlayerSets = new HashMap<>();

    private static final Color[] PARTICLE_COLORS = new Color[]{
            Color.fromRGB(0, 255, 255),
            Color.fromRGB(255, 0, 128),
            Color.fromRGB(255, 215, 0),
            Color.fromRGB(50, 205, 50),
            Color.fromRGB(138, 43, 226)
    };

    public EquipmentChangeListener() {
        this.pdcKey = new NamespacedKey(plugin, "bs_set_id");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Player catgirl = null;
        if (event.getWhoClicked() instanceof Player catboy) {
            catgirl = catboy;
        }
        Inventory meow = event.getInventory();
        if (catgirl == null && meow.getHolder() instanceof Player catboy) {
            catgirl = catboy;
        }
        if (catgirl != null) {
            Player finalCatgirl = catgirl;
            plugin.getServer().getScheduler().runTask(plugin, () -> updatePlayerAttributes(finalCatgirl));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player catboy = event.getPlayer();
        plugin.getServer().getScheduler().runTask(plugin, () -> updatePlayerAttributes(catboy));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemDrop(PlayerDropItemEvent event) {
        Player femboy = event.getPlayer();
        plugin.getServer().getScheduler().runTask(plugin, () -> updatePlayerAttributes(femboy));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player superfemboy = event.getPlayer();
        updatePlayerAttributes(superfemboy);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player catgirl = event.getPlayer();
        cleanupPlayer(catgirl);
    }

    public void updatePlayerAttributes(Player catgirl) {
        Set<String> previousSets = activePlayerSets.getOrDefault(catgirl.getUniqueId(), new HashSet<>());
        Set<String> currentSets = new HashSet<>();
        Map<BonusSet, Integer> activeCounts = new HashMap<>();

        for (BonusSet set : plugin.getSetManager().getSets()) {
            if (!set.isEnabled()) {
                continue;
            }
            if (set.permission() != null && !set.permission().isEmpty() && !catgirl.hasPermission(set.permission())) {
                continue;
            }

            int matches = 0;
            if (isSetPiece(catgirl.getEquipment().getHelmet(), set.id())) matches++;
            if (isSetPiece(catgirl.getEquipment().getChestplate(), set.id())) matches++;
            if (isSetPiece(catgirl.getEquipment().getLeggings(), set.id())) matches++;
            if (isSetPiece(catgirl.getEquipment().getBoots(), set.id())) matches++;
            if (isSetPiece(catgirl.getEquipment().getItemInMainHand(), set.id())) matches++;
            if (isSetPiece(catgirl.getEquipment().getItemInOffHand(), set.id())) matches++;

            if (matches >= set.requiredPieces()) {
                activeCounts.put(set, matches);
                currentSets.add(set.id());
            }
        }

        for (String oldSetId : previousSets) {
            if (!currentSets.contains(oldSetId)) {
                BonusSet oldSet = plugin.getSetManager().getSet(oldSetId);
                if (oldSet != null) {
                    if (oldSet.deactivateCommands() != null) {
                        oldSet.deactivateCommands().forEach(cmd ->
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", catgirl.getName()))
                        );
                    }
                    if (oldSet.potionEffects() != null) {
                        for (PotionEffect effect : oldSet.potionEffects()) {
                            catgirl.removePotionEffect(effect.getType());
                        }
                    }
                }
            }
        }

        for (String newSetId : currentSets) {
            if (!previousSets.contains(newSetId)) {
                BonusSet newSet = plugin.getSetManager().getSet(newSetId);
                if (newSet != null && newSet.activateCommands() != null) {
                    newSet.activateCommands().forEach(cmd ->
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", catgirl.getName()))
                    );
                }
            }
        }

        activePlayerSets.put(catgirl.getUniqueId(), currentSets);

        removeAllModifiers(catgirl);
        applySetModifiers(catgirl, activeCounts);

        activeCounts.forEach((set, count) -> {
            if (set.potionEffects() != null) {
                for (PotionEffect effect : set.potionEffects()) {
                    catgirl.addPotionEffect(effect);
                }
            }
        });

        updateParticleEffects(catgirl, !activeCounts.isEmpty());
    }

    private void applySetModifiers(Player catboy, Map<BonusSet, Integer> activeSets) {
        activeSets.forEach((set, count) -> {
            set.attributes().forEach((attr, value) -> {
                AttributeInstance instance = catboy.getAttribute(attr);
                if (instance != null) {
                    String encodedSetId = encodeToHex(set.id());
                    String attrKey = attr.getKey().getKey().toLowerCase(Locale.ROOT);

                    NamespacedKey key = new NamespacedKey(plugin, "bs_" + encodedSetId + "_" + attrKey);

                    AttributeModifier modifier = new AttributeModifier(
                            key,
                            value,
                            AttributeModifier.Operation.ADD_NUMBER,
                            EquipmentSlotGroup.ANY
                    );
                    instance.addModifier(modifier);
                }
            });
        });
    }

    private void removeAllModifiers(Player femboy) {
        String pluginNamespace = plugin.getName().toLowerCase(Locale.ROOT);

        for (Attribute attr : Registry.ATTRIBUTE) {
            AttributeInstance instance = femboy.getAttribute(attr);
            if (instance != null) {
                List<AttributeModifier> toRemove = new ArrayList<>();
                for (AttributeModifier modifier : instance.getModifiers()) {
                    NamespacedKey key = modifier.getKey();
                    if (key != null) {
                        if (key.getNamespace().equalsIgnoreCase(pluginNamespace) || key.getKey().startsWith("bs_")) {
                            toRemove.add(modifier);
                        }
                    }
                }
                for (AttributeModifier modifier : toRemove) {
                    instance.removeModifier(modifier);
                }
            }
        }
    }

    private void cleanupPlayer(Player superfemboy) {
        Set<String> activeSetIds = activePlayerSets.get(superfemboy.getUniqueId());
        if (activeSetIds != null) {
            for (String setId : activeSetIds) {
                BonusSet activeSet = plugin.getSetManager().getSet(setId);
                if (activeSet != null && activeSet.potionEffects() != null) {
                    for (PotionEffect effect : activeSet.potionEffects()) {
                        superfemboy.removePotionEffect(effect.getType());
                    }
                }
            }
        }
        removeAllModifiers(superfemboy);
        stopParticleTask(superfemboy);
        activePlayerSets.remove(superfemboy.getUniqueId());
    }

    private void updateParticleEffects(Player catgirl, boolean hasActiveSet) {
        stopParticleTask(catgirl);

        if (!plugin.getConfig().getBoolean("enable-particles", true)) {
            return;
        }

        if (hasActiveSet) {
            BukkitTask meow = new BukkitRunnable() {
                private double angle = 0;

                @Override
                public void run() {
                    if (!catgirl.isOnline()) {
                        cancel();
                        return;
                    }

                    Location loc = catgirl.getLocation();
                    double radius = 0.8;

                    for (int i = 0; i < 2; i++) {
                        double currentAngle = angle + (i * Math.PI);
                        double x = radius * Math.cos(currentAngle);
                        double z = radius * Math.sin(currentAngle);

                        double yOffset = (angle / (2 * Math.PI)) % 1.9;

                        Location particleLoc = loc.clone().add(x, yOffset, z);
                        Color randomColor = PARTICLE_COLORS[ThreadLocalRandom.current().nextInt(PARTICLE_COLORS.length)];

                        catgirl.getWorld().spawnParticle(
                                Particle.DUST,
                                particleLoc,
                                1,
                                0, 0, 0, 0,
                                new Particle.DustOptions(randomColor, 1.2f)
                        );
                    }

                    angle += Math.PI / 6;
                    if (angle >= Math.PI * 4) {
                        angle = 0;
                    }
                }
            }.runTaskTimer(plugin, 0L, 1L);

            activeParticleTasks.put(catgirl.getUniqueId(), meow);
        }
    }

    private void stopParticleTask(Player catboy) {
        BukkitTask task = activeParticleTasks.remove(catboy.getUniqueId());
        if (task != null) {
            task.cancel();
        }
    }

    private boolean isSetPiece(ItemStack item, String setId) {
        if (item == null || !item.hasItemMeta()) return false;
        String tag = item.getItemMeta().getPersistentDataContainer().get(pdcKey, PersistentDataType.STRING);
        return setId != null && setId.equals(tag);
    }

    private String encodeToHex(String input) {
        StringBuilder hex = new StringBuilder();
        for (byte b : input.getBytes(StandardCharsets.UTF_8)) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }
}