package com.artillexstudios.axenvoy.config.impl;

import com.artillexstudios.axenvoy.config.AbstractConfig;
import com.artillexstudios.axenvoy.utils.FileUtils;

public class Config extends AbstractConfig {

    @Key("listen-to-block-physics")
    @Comment({"Enable this, if you want to prevent the crateTypes from changing their surrounding blocks.",
            "Examples:",
            "If a crateType spawns on a 'dirt path' block, that block will become dirt, if this feature is disabled.",
            "Warning: This will probably tank your performance at crateType spawning, if lots of crateTypes spawn (lots = hundreds)",
            "Changing this requires a restart!"
    })
    public static boolean LISTEN_TO_BLOCK_PHYSICS = false;

    @Key("dont-replace-blocks")
    @Comment({
            "Enable this, if you want to prevent some blocks that",
            "replaced by crates in certain cases."
    })
    public static boolean DONT_REPLACE_BLOCKS = true;

    @Key("tracker.enabled")
    @Comment({"Allows players to track the nearest spawned crate using a compass"})
    public static boolean TRACKER_ENABLED = true;

    @Key("tracker.track-on-join")
    @Comment({"Start tracking when players join, otherwise players can use /envoy link"})
    public static boolean TRACK_ON_JOIN = true;

    @Key("tracker.notification-cooldown")
    @Comment({"How long is the delay between distance settings (in seconds)"})
    public static int NOTIFICATION_COOLDOWN = 3;

    private static final Config CONFIG = new Config();

    public static void reload() {
        FileUtils.extractFile(Config.class, "config.yml", FileUtils.PLUGIN_DIRECTORY, false);

        CONFIG.reload(FileUtils.PLUGIN_DIRECTORY.resolve("config.yml"), Config.class, null, null);
    }
}
