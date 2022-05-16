package io.github.invvk.mony.api.config.properties;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.StringProperty;

public class MessagesProperty implements SettingsHolder {

    public static Property<String> MOB_KILL_ACTIONBAR = new StringProperty("actionbar.mob-kill",
            "&2+{AMOUNT} &afor killing &e{MOB}");

    public static Property<String> PLAYER_COOLDOWN_TRIGGER = new
            StringProperty("player.cooldown-started", "&aYou are now in cooldown for &e{TIME}");

}
