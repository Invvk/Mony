package io.github.invvk.mony.internal.listener;

import io.github.invvk.mony.internal.config.properties.ConfigProperty;
import io.github.invvk.mony.internal.config.properties.MessagesProperty;
import io.github.invvk.mony.api.database.IUserManager;
import io.github.invvk.mony.api.database.User;
import io.github.invvk.mony.api.events.PlayerKillMobEvent;
import io.github.invvk.mony.internal.MonyBootstrap;
import io.github.invvk.mony.api.utils.TimeUtils;
import io.github.invvk.mony.internal.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Optional;

public class DailyLimitListener implements Listener {

    private final MonyBootstrap bootstrap;

    public DailyLimitListener(MonyBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMobKill(PlayerKillMobEvent event) {
        if (!bootstrap.isDailyLimitEnabled())
            return;

        final IUserManager userManager = bootstrap.getUserManager().orElse(null);
        if (userManager == null)
            return;
        final Player player = event.getPlayer();

        final Optional<User> optional = userManager.getUser(player.getUniqueId());

        if (!optional.isPresent())
            return;

        final User user = optional.get();

        if (user.hasCooldown()) {
            event.setCancelled(true);
            return;
        }

        if (user.getLastMaxAmount() == 0) {
            user.setLastMaxAmount(bootstrap.getConfigManager().getConfig().getProperty(ConfigProperty
                    .DAILY_LIMIT_MAX_AMOUNT));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDailyLimit(PlayerKillMobEvent event) {
        if (!bootstrap.isDailyLimitEnabled() || event.isPreventDL())
            return;

        final IUserManager userManager = bootstrap.getUserManager().orElse(null);
        if (userManager == null)
            return;
        final Player player = event.getPlayer();

        final Optional<User> optional = userManager.getUser(player.getUniqueId());

        if (!optional.isPresent())
            return;

        final User user = optional.get();

        if (user.hasCooldown()) {
            event.setCancelled(true);
            return;
        }

        double current = user.getLastMaxAmount();

        double expectedAmount = event.getAmount();
        final double difference = current - expectedAmount;
        if (expectedAmount >= current) {
            expectedAmount = current;
            user.setCooldown(System.currentTimeMillis() + TimeUtils.getTimeInMilli(bootstrap.getConfigManager().getConfig().
                    getProperty(ConfigProperty.DAILY_LIMIT_COOLDOWN)));
            user.setLastMaxAmount(0);
            player.sendMessage(Utils.color(bootstrap.getConfigManager().getMessage().
                    getProperty(MessagesProperty.PLAYER_COOLDOWN_TRIGGER).replace("{TIME}",
                            bootstrap.getConfigManager().getConfig().
                                    getProperty(ConfigProperty.DAILY_LIMIT_COOLDOWN))));
        } else {
            // replace with the new amount
            user.setLastMaxAmount(difference);
        }
        event.setAmount(expectedAmount);
    }

}
