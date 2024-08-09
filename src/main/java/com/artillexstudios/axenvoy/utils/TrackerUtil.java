package com.artillexstudios.axenvoy.utils;

import com.artillexstudios.axenvoy.AxEnvoyPlugin;
import com.artillexstudios.axenvoy.config.impl.Messages;
import com.artillexstudios.axenvoy.envoy.Envoy;
import com.artillexstudios.axenvoy.envoy.SpawnedCrate;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TrackerUtil {
    private static final Map<UUID, Location> playerTracker = new HashMap<>();
    private static final Map<Location, SpawnedCrate> crateCache = new HashMap<>();

    public static boolean hasActiveCrates() {
        return !crateCache.isEmpty();
    }

    public static boolean isCrateActive(Location location) {
        return crateCache.containsKey(location);
    }

    public static boolean isPlayerTrackingCrate(Player player) {
        return playerTracker.containsKey(player.getUniqueId());
    }

    public static void cache(Envoy envoy) {
        envoy.getSpawnedCrates().forEach(crate -> crateCache.put(crate.getFinishLocation(), crate));
    }

    public static void expire(SpawnedCrate crate) {
        crateCache.remove(crate.getFinishLocation());
    }

    public static void expire(Envoy envoy) {
        envoy.getSpawnedCrates().forEach(TrackerUtil::expire);
    }

    public static void reload() {
        playerTracker.clear();
    }

    /**
     * Determines the nearest crate to the player and sets their compass direction to point in the direction of
     * that crate until it is removed or
     *
     * @param player Player to link the crate to
     */
    public static void trackNearestCrate(Player player) {
        if (player != null) {
            Location location = player.getLocation();
            Location nearestCrate = crateCache.keySet().stream()
                    .min(Comparator.comparingDouble(location::distanceSquared))
                    .orElse(null);

            if (nearestCrate != null) {
                player.setCompassTarget(nearestCrate);
                playerTracker.put(player.getUniqueId(), nearestCrate);
            } else {
                playerTracker.remove(player.getUniqueId());
            }
        }
    }

    /**
     * Unlink all supply crate tracking for the {@link Player}
     *
     * @param player The player potentially tracking any crates
     */
    public static void untrackCrates(Player player) {
        player.setCompassTarget(player.getWorld().getSpawnLocation());
        playerTracker.remove(player.getUniqueId());
    }

    public static void notifyDistance(Player player) {
        Messages messages = AxEnvoyPlugin.getMessages();
        String distance = getDistance(player);
        Utils.sendMessage(player, messages.PREFIX, messages.TRACKER_NEAREST_CRATE.replace("%distance%", distance));
    }

    public static void recheckTrackedCrate(Player player) {
        Location location = playerTracker.get(player.getUniqueId());
        if (location == null || !crateCache.containsKey(location)) {
            trackNearestCrate(player);
        }
    }

    private static String getDistance(Player player) {
        UUID id = player.getUniqueId();
        Location crate = playerTracker.get(id);
        int distance = (int) Math.round(crate.distance(player.getLocation()));
        return String.valueOf(distance);
    }
}

