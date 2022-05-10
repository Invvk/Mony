package io.github.invvk.mony.config.properties;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.*;
import io.github.invvk.mony.config.properties.bean.MobBean;

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

    @Comment({"Data save task is in minutes, example: 30 = 30 minutes",
            "meaning, a task will run every x minutes to save the current player cache",
            "Note: settings apply every reboot, PLEASE DO NOT USE /reload"})
    public static Property<Integer> UPDATE_INTERVAL = new IntegerProperty(
            "Settings.data-update-interval", 30);

    @Comment({"Should we enable the daily limit?", "if no, then the plugin will stop saving data."})
    public static Property<Boolean> DAILY_LIMIT_ENABLE =
            new BooleanProperty("settings.daily-limit.enable", true);

    @Comment("Cooldown for the daily limit.")
    public static Property<String> DAILY_LIMIT_COOLDOWN
            = new StringProperty("settings.daily-limit.cooldown",
            "8h");

    @Comment("max amount of money to be given in the daily limit.")
    public static Property<Integer> DAILY_LIMIT_MAX_AMOUNT
            = new IntegerProperty("settings.daily-limit.max-amount",
            1000);

    @Comment({
            "this section controls how the mob prices work",
            "You can set a global price and set a price per Mob",
            "If the mob doesn't exists in the custom_price section, then it will default to defaultPrice and default multiplier",
            "",
            "To configure an entity, please make sure that the names matches in this link: ",
            "WARNING: PLEASE MAKE SURE THAT YOU CHOOSE ENTITIES THAT MATCHES YOUR SERVER VERSION",
            "",
            "1.18: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/entity/EntityType.html",
            "1.12.2: https://helpch.at/docs/1.12.2/org/bukkit/entity/EntityType.html",
            "1.8: https://helpch.at/docs/1.8.8/org/bukkit/entity/EntityType.html",
            "",
            "$$$ Example Configuration $$$",
            "",
            "enconomy:",
            "  Default price for any mob if they don't have any configuration",
            "  defaultPrice: 100.0",
            "  ",
            "  # Combo multiplier that will add up to the kill price",
            "  comboMultiplier: 1.0",
            "  ",
            "  # disable combo multiplier?",
            "  disableComboGlobally: false",
            "  ",
            "  custom_price:",
            "    ZOMBIE:",
            "      # Money to be given after killing this mob",
            "      price: 120.0",
            "      ",
            "      # Combo multiplier that will add up to the kill price",
            "      multiplier: 1.2",
            "      ",
            "      # This will disable combo checks for Zombies, this have priority over disableComboGlobally",
            "      disableCombo: true",
            ""})
    public static Property<MobBean> MOB_PRICE = new BeanProperty<>(MobBean.class, "economy",
            new MobBean());

}
