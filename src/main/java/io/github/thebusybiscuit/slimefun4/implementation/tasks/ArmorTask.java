package io.github.thebusybiscuit.slimefun4.implementation.tasks;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import io.github.thebusybiscuit.slimefun4.api.items.HashedArmorpiece;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.attributes.Radioactive;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.armor.SlimefunArmorPiece;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.gadgets.SolarHelmet;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import me.mrCookieSlime.Slimefun.SlimefunPlugin;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.Slimefun;

/**
 * The {@link ArmorTask} is responsible for handling {@link PotionEffect PotionEffects} for
 * {@link Radioactive} items or any {@link SlimefunArmorPiece}.
 * It also handles the prevention of radioation through a Hazmat Suit
 * 
 * @author TheBusyBiscuit
 *
 */
public class ArmorTask implements Runnable {

    private final Set<PotionEffect> radiationEffects;

    public ArmorTask() {
        Set<PotionEffect> effects = new HashSet<>();
        effects.add(new PotionEffect(PotionEffectType.WITHER, 400, 2));
        effects.add(new PotionEffect(PotionEffectType.BLINDNESS, 400, 3));
        effects.add(new PotionEffect(PotionEffectType.CONFUSION, 400, 3));
        effects.add(new PotionEffect(PotionEffectType.WEAKNESS, 400, 2));
        effects.add(new PotionEffect(PotionEffectType.SLOW, 400, 1));
        effects.add(new PotionEffect(PotionEffectType.SLOW_DIGGING, 400, 1));
        radiationEffects = Collections.unmodifiableSet(effects);
    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.isValid() || p.isDead()) {
                continue;
            }

            PlayerProfile.get(p, profile -> {
                ItemStack[] armor = p.getInventory().getArmorContents();
                HashedArmorpiece[] cachedArmor = profile.getArmor();

                handleSlimefunArmor(p, armor, cachedArmor);

                if (hasSunlight(p)) {
                    checkForSolarHelmet(p);
                }

                checkForRadiation(profile);
            });
        }
    }

    private void handleSlimefunArmor(Player p, ItemStack[] armor, HashedArmorpiece[] cachedArmor) {
        for (int slot = 0; slot < 4; slot++) {
            ItemStack item = armor[slot];
            HashedArmorpiece armorpiece = cachedArmor[slot];

            if (armorpiece.hasDiverged(item)) {
                SlimefunItem sfItem = SlimefunItem.getByItem(item);
                if (!(sfItem instanceof SlimefunArmorPiece) || !Slimefun.hasUnlocked(p, sfItem, true)) {
                    sfItem = null;
                }

                armorpiece.update(item, sfItem);
            }

            if (item != null && armorpiece.getItem().isPresent()) {
                Slimefun.runSync(() -> {
                    for (PotionEffect effect : armorpiece.getItem().get().getPotionEffects()) {
                        p.removePotionEffect(effect.getType());
                        p.addPotionEffect(effect);
                    }
                });
            }
        }
    }

    private void checkForSolarHelmet(Player p) {
        ItemStack helmet = p.getInventory().getHelmet();

        if (SlimefunPlugin.getRegistry().isBackwardsCompatible() && !SlimefunUtils.isItemSimilar(helmet, SlimefunItems.SOLAR_HELMET, true, false)) {
            // Performance saver for slow backwards-compatible versions of Slimefun
            return;
        }

        SlimefunItem item = SlimefunItem.getByItem(helmet);

        if (item instanceof SolarHelmet && Slimefun.hasUnlocked(p, item, true)) {
            ((SolarHelmet) item).rechargeItems(p);
        }
    }

    private boolean hasSunlight(Player p) {
        World world = p.getWorld();

        if (world.getEnvironment() != Environment.NORMAL) {
            // The End and Nether have no sunlight
            return false;
        }

        return (world.getTime() < 12300 || world.getTime() > 23850) && p.getEyeLocation().getBlock().getLightFromSky() == 15;
    }

    private void checkForRadiation(PlayerProfile profile) {
        HashedArmorpiece[] armor = profile.getArmor();
        Player p = profile.getPlayer();

        // Check for a Hazmat Suit
        boolean hasHazmat = false;
        for (HashedArmorpiece armorPiece : armor) {

            Optional<SlimefunArmorPiece> sfArmor = armorPiece.getItem();
            if (!sfArmor.isPresent()) continue;

            if (sfArmor.get().getID().equals("SCUBA_HELMET") ||
                    sfArmor.get().getID().equals("HAZMAT_CHESTPLATE") ||
                    sfArmor.get().getID().equals("HAZMAT_LEGGINGS") ||
                    sfArmor.get().getID().equals("RUBBER_BOOTS")) {
                hasHazmat = true;
            }
        }

        if (!hasHazmat) {
            for (ItemStack item : p.getInventory()) {
                if (isRadioactive(p, item)) {
                    break;
                }
            }
        }
    }

    private boolean isRadioactive(Player p, ItemStack item) {
        for (SlimefunItem radioactiveItem : SlimefunPlugin.getRegistry().getRadioactiveItems()) {
            if (radioactiveItem.isItem(item) && Slimefun.isEnabled(p, radioactiveItem, true)) {
                // If the item is enabled in the world, then make radioactivity do its job
                SlimefunPlugin.getLocal().sendMessage(p, "messages.radiation");

                Slimefun.runSync(() -> {
                    p.addPotionEffects(radiationEffects);
                    p.setFireTicks(400);
                });

                return true;
            }
        }

        return false;
    }

}
