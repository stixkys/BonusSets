package me.stickyballs2652.bonusSets.listener;

import me.stickyballs2652.bonusSets.Main;
import me.stickyballs2652.bonusSets.model.BonusSet;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class EquipmentChangeListener implements Listener {

    private final Main plugin = Main.getInstance();
    private final NamespacedKey pdcKey;
    private final Map<UUID, BukkitTask> activeParticleTasks = new HashMap<>();

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
        if (event.getWhoClicked() instanceof Player player) {
            plugin.getServer().getScheduler().runTask(plugin, () -> updatePlayerAttributes(player));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemHeld(PlayerItemHeldEvent event) {
        plugin.getServer().getScheduler().runTask(plugin, () -> updatePlayerAttributes(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemDrop(PlayerDropItemEvent event) {
        plugin.getServer().getScheduler().runTask(plugin, () -> updatePlayerAttributes(event.getPlayer()));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        updatePlayerAttributes(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        removeAllModifiers(event.getPlayer());
        stopParticleTask(event.getPlayer());
    }

    public void updatePlayerAttributes(Player player) {
        removeAllModifiers(player);

        Map<BonusSet, Integer> activeCounts = new HashMap<>();

        for (BonusSet set : plugin.getSetManager().getSets()) {
            if (set.permission() != null && !set.permission().isEmpty() && !player.hasPermission(set.permission())) {
                continue;
            }

            int matches = 0;
            if (isSetPiece(player.getEquipment().getHelmet(), set.id())) matches++;
            if (isSetPiece(player.getEquipment().getChestplate(), set.id())) matches++;
            if (isSetPiece(player.getEquipment().getLeggings(), set.id())) matches++;
            if (isSetPiece(player.getEquipment().getBoots(), set.id())) matches++;
            if (isSetPiece(player.getEquipment().getItemInMainHand(), set.id())) matches++;
            if (isSetPiece(player.getEquipment().getItemInOffHand(), set.id())) matches++;

            if (matches >= set.requiredPieces()) {
                activeCounts.put(set, matches);
            }
        }

        applySetModifiers(player, activeCounts);
        updateParticleEffects(player, !activeCounts.isEmpty());
    }

    private void applySetModifiers(Player player, Map<BonusSet, Integer> activeSets) {
        activeSets.forEach((set, count) -> {
            set.attributes().forEach((attr, value) -> {
                AttributeInstance instance = player.getAttribute(attr);
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

    private void removeAllModifiers(Player player) {
        String pluginNamespace = plugin.getName().toLowerCase(Locale.ROOT);

        for (Attribute attr : Registry.ATTRIBUTE) {
            AttributeInstance instance = player.getAttribute(attr);
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

    private void updateParticleEffects(Player player, boolean hasActiveSet) {
        stopParticleTask(player);

        if (hasActiveSet) {
            BukkitTask task = new BukkitRunnable() {
                private double angle = 0;

                @Override
                public void run() {
                    if (!player.isOnline()) {
                        cancel();
                        return;
                    }

                    Location loc = player.getLocation();
                    double radius = 0.8;

                    for (int i = 0; i < 2; i++) {
                        double currentAngle = angle + (i * Math.PI);
                        double x = radius * Math.cos(currentAngle);
                        double z = radius * Math.sin(currentAngle);

                        double yOffset = (angle / (2 * Math.PI)) % 1.9;

                        Location particleLoc = loc.clone().add(x, yOffset, z);

                        Color randomColor = PARTICLE_COLORS[ThreadLocalRandom.current().nextInt(PARTICLE_COLORS.length)];

                        player.getWorld().spawnParticle(
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

            activeParticleTasks.put(player.getUniqueId(), task);
        }
    }

    private void stopParticleTask(Player player) {
        BukkitTask task = activeParticleTasks.remove(player.getUniqueId());
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