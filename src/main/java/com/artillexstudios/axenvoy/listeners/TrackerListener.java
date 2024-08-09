package com.artillexstudios.axenvoy.listeners;

import com.artillexstudios.axenvoy.AxEnvoyPlugin;
import com.artillexstudios.axenvoy.config.impl.Config;
import com.artillexstudios.axenvoy.event.EnvoyCrateCollectEvent;
import com.artillexstudios.axenvoy.event.EnvoyEndEvent;
import com.artillexstudios.axenvoy.event.EnvoyStartEvent;
import com.artillexstudios.axenvoy.utils.TrackerUtil;
import com.artillexstudios.axenvoy.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class TrackerListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (Config.TRACK_ON_JOIN && player.getInventory().contains(Material.COMPASS)) {
            TrackerUtil.trackNearestCrate(event.getPlayer());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        TrackerUtil.untrackCrates(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();

        if (!Config.TRACKER_ENABLED) return;
        if (item.getType() != Material.COMPASS) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;

        if (!TrackerUtil.hasActiveCrates()) {
            Utils.sendMessage(player, AxEnvoyPlugin.getMessages().PREFIX, AxEnvoyPlugin.getMessages().TRACKER_NO_ENVOY);
            return;
        }

        TrackerUtil.recheckTrackedCrate(player);
        TrackerUtil.notifyDistance(player);
    }

    @EventHandler
    public void onEnvoyStart(EnvoyStartEvent event) {
        TrackerUtil.refreshActiveCrates();
    }

    @EventHandler
    public void onEnvoyEnd(EnvoyEndEvent event) {
        TrackerUtil.refreshActiveCrates();
    }

    @EventHandler
    public void onEnvoyCollect(EnvoyCrateCollectEvent event) {
        TrackerUtil.refreshActiveCrates();
    }
}
