package com.artillexstudios.axenvoy.utils;

import com.artillexstudios.axenvoy.AxEnvoyPlugin;
import com.artillexstudios.axenvoy.config.impl.Messages;
import com.artillexstudios.axenvoy.envoy.Envoys;
import com.artillexstudios.axenvoy.envoy.SpawnedCrate;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class TrackerUtil {
    private static final Map<UUID, Location> playerCache = new HashMap<>();
    private static final List<Location> cratesCache = new ArrayList<>();

    public static boolean hasActiveCrates() {
        return !cratesCache.isEmpty();
    }

    public static boolean isPlayerTrackingCrate(@NotNull Player player) {
        return playerCache.containsKey(player.getUniqueId());
    }

    public static void refreshActiveCrates() {
        cratesCache.clear();
        cratesCache.addAll(Envoys.getTypes()
                .values()
                .stream()
                .flatMap(crate -> crate.getSpawnedCrates()
                        .stream()
                        .map(SpawnedCrate::getFinishLocation))
                .toList());
    }

    public static void recheckTrackedCrate(@NotNull Player player) {
        Location tracked = playerCache.get(player.getUniqueId());
        if (!cratesCache.contains(tracked)) {
            trackNearestCrate(player);
        }
    }


    /**
     * Determines the nearest crate to the player and sets their compass direction to point in the direction of
     * that crate until it is removed or
     *
     * @param player Player to link the crate to
     */
    public static void trackNearestCrate(@NotNull Player player) {
        Location nearest = cratesCache.stream()
                .min(Comparator.comparingDouble(player.getLocation()::distanceSquared))
                .orElse(null);

        if (nearest != null) {
            player.setCompassTarget(nearest);
            playerCache.put(player.getUniqueId(), nearest);
        } else
            playerCache.remove(player.getUniqueId());
    }

    /**
     * Unlink all supply crate tracking for the {@link Player}
     *
     * @param player The player potentially tracking any crates
     */
    public static void untrackCrates(Player player) {
        player.setCompassTarget(player.getWorld().getSpawnLocation());
        playerCache.remove(player.getUniqueId());
    }

    public static void notifyDistance(Player player) {
        Messages messages = AxEnvoyPlugin.getMessages();
        String distance = getDistance(player);
        Utils.sendMessage(player, messages.PREFIX, messages.TRACKER_NEAREST_CRATE.replace("%distance%", distance));
    }

    private static String getDistance(Player player) {
        UUID id = player.getUniqueId();
        Location crate = playerCache.get(id);
        int distance = (int) Math.round(crate.distance(player.getLocation()));
        return String.valueOf(distance);
    }

    public static void reload() {
        playerCache.clear();
        cratesCache.clear();
        refreshActiveCrates();
    }
}

