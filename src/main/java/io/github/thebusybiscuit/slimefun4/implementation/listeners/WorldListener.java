package io.github.thebusybiscuit.slimefun4.implementation.listeners;

import java.util.logging.Level;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.Slimefun;

public class WorldListener implements Listener {

    public WorldListener(SlimefunPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        SlimefunPlugin.getWorldSettingsService().load(e.getWorld());
        BlockStorage.getForcedStorage(e.getWorld());
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent e) {
        BlockStorage storage = BlockStorage.getStorage(e.getWorld());

        if (storage != null) {
            storage.save(true);
        }
        else {
            Slimefun.getLogger().log(Level.SEVERE, "Could not save Slimefun Blocks for World \"{0}\"", e.getWorld().getName());
        }
    }

}
