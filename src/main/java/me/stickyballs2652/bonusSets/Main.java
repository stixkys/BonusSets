package me.stickyballs2652.bonusSets;

import me.stickyballs2652.bonusSets.command.BonusSetsCommand;
import me.stickyballs2652.bonusSets.gui.AttributeEditorListener;
import me.stickyballs2652.bonusSets.gui.SetEditorListener;
import me.stickyballs2652.bonusSets.gui.SetMenuListener;
import me.stickyballs2652.bonusSets.listener.EquipmentChangeListener;
import me.stickyballs2652.bonusSets.manager.SetManager;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main instance;
    private SetManager setManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        this.setManager = new SetManager(this);

        getServer().getPluginManager().registerEvents(new SetMenuListener(), this);
        getServer().getPluginManager().registerEvents(new SetEditorListener(), this);
        getServer().getPluginManager().registerEvents(new EquipmentChangeListener(), this);
        getServer().getPluginManager().registerEvents(new AttributeEditorListener(), this);

        if (getCommand("bonussets") != null) {
            getCommand("bonussets").setExecutor(new BonusSetsCommand());
        }

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                boolean hasSetBonus = false;
                for (Attribute attr : Registry.ATTRIBUTE) {
                    AttributeInstance inst = player.getAttribute(attr);
                    if (inst != null) {
                        hasSetBonus = inst.getModifiers().stream()
                                .anyMatch(mod -> mod.getKey().getKey().startsWith("bonusset_"));
                        if (hasSetBonus) break;
                    }
                }

                if (hasSetBonus) {
                    player.getWorld().spawnParticle(
                            Particle.END_ROD,
                            player.getLocation().add(0, 1.0, 0),
                            2,
                            0.3, 0.5, 0.3,
                            0.02
                    );
                }
            }
        }, 10L, 10L);

        getLogger().info("BonusSets enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("BonusSets disabled.");
    }

    public static Main getInstance() {
        return instance;
    }

    public SetManager getSetManager() {
        return setManager;
    }
}