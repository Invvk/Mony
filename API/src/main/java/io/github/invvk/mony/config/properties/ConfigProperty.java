package io.github.invvk.mony.config.properties;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.IntegerProperty;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.StringProperty;

public class ConfigProperty implements SettingsHolder {

    @Comment("FILE or MYSQL mode.")
    public static Property<String> STORAGE_MODE = new StringProperty
            ("storage.mode", "FILE");

    public static Property<String> STORAGE_HOST = new StringProperty
            ("storage.mysql.host", "localhost");

    public static Property<Integer> STORAGE_PORT = new IntegerProperty
            ("storage.mysql.port", 3306);

    public static Property<String> STORAGE_USER = new StringProperty
            ("storage.mysql.username", "username");

    public static Property<String> STORAGE_PASSWORD = new StringProperty
            ("storage.mysql.password", "password");

    public static Property<String> STORAGE_DATABASE = new StringProperty
            ("storage.mysql.database", "database");

    public static Property<String> STORAGE_TABLE_PREFIX = new StringProperty
            ("storage.mysql.table-prefix", "mony_");

    @Comment({"the updater is in minutes, example: 30 = 30 minutes",
            "Note: settings apply every reboot, PLEASE DO NOT USE /reload"})
    public static Property<Integer> UPDATE_INTERVAL = new IntegerProperty(
            "Settings.data-update-interval", 30);

    public static Property<String> TIME_STAMP = new StringProperty("settings.timestamp",
            "0:00");

}
