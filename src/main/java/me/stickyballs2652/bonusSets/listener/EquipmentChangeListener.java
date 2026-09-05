package me.stickyballs2652.bonusSets.listener;

import me.stickyballs2652.bonusSets.Main;
import me.stickyballs2652.bonusSets.model.BonusSet;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EquipmentChangeListener implements Listener {

    private final Main plugin = Main.getInstance();
    private static final String MODIFIER_PREFIX = "bonusset_";

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player catgirl) {
            plugin.getServer().getScheduler().runTask(plugin, () -> updatePlayerAttributes(catgirl));
        }
    }

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        plugin.getServer().getScheduler().runTask(plugin, () -> updatePlayerAttributes(event.getPlayer()));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        updatePlayerAttributes(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        removeAllModifiers(event.getPlayer());
    }

    public void updatePlayerAttributes(Player catgirl) {
        Map<BonusSet, Integer> activeCounts = new HashMap<>();

        for (BonusSet set : plugin.getSetManager().getSets()) {
            if (set.permission() != null && !set.permission().isEmpty() && !catgirl.hasPermission(set.permission())) {
                continue;
            }

            int matches = 0;
            if (isSimilar(catgirl.getEquipment().getHelmet(), set.helmet())) matches++;
            if (isSimilar(catgirl.getEquipment().getChestplate(), set.chestplate())) matches++;
            if (isSimilar(catgirl.getEquipment().getLeggings(), set.leggings())) matches++;
            if (isSimilar(catgirl.getEquipment().getBoots(), set.boots())) matches++;
            if (isSimilar(catgirl.getEquipment().getItemInMainHand(), set.mainhand())) matches++;
            if (isSimilar(catgirl.getEquipment().getItemInOffHand(), set.offhand())) matches++;

            if (matches >= set.requiredPieces()) {
                activeCounts.put(set, matches);
            }
        }

        applySetModifiers(catgirl, activeCounts);
    }

    private void applySetModifiers(Player catgirl, Map<BonusSet, Integer> activeSets) {
        removeAllModifiers(catgirl);

        activeSets.forEach((set, count) -> {
            set.attributes().forEach((attr, value) -> {
                AttributeInstance meowstance = catgirl.getAttribute(attr);
                if (meowstance != null) {
                    String name = MODIFIER_PREFIX + set.id() + "_" + attr.name().toLowerCase();
                    UUID uuid = UUID.nameUUIDFromBytes(name.getBytes());
                    AttributeModifier meowdifier = new AttributeModifier(
                            uuid,
                            name,
                            value,
                            AttributeModifier.Operation.ADD_NUMBER
                    );
                    meowstance.addModifier(meowdifier);
                }
            });
        });
    }

    private void removeAllModifiers(Player catgirl) {
        for (Attribute attr : Attribute.values()) {
            AttributeInstance meowstance = catgirl.getAttribute(attr);
            if (meowstance != null) {
                for (AttributeModifier meowdifier : meowstance.getModifiers()) {
                    if (meowdifier.getName().startsWith(MODIFIER_PREFIX)) {
                        meowstance.removeModifier(meowdifier);
                    }
                }
            }
        }
    }

    private boolean isSimilar(ItemStack item, ItemStack target) {
        if (item == null || target == null) return false;
        return item.isSimilar(target);
    }
}