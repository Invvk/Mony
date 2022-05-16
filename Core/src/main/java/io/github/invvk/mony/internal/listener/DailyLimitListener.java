package io.github.invvk.mony.internal.listener;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.github.invvk.mony.api.config.properties.ConfigProperty;
import io.github.invvk.mony.api.config.properties.MessagesProperty;
import io.github.invvk.mony.api.database.IUserManager;
import io.github.invvk.mony.api.database.User;
import io.github.invvk.mony.api.events.MonyCacheLoadEvent;
import io.github.invvk.mony.api.events.PlayerKillMobEvent;
import io.github.invvk.mony.internal.MonyBootstrap;
import io.github.invvk.mony.internal.utils.ActionbarUtils;
import io.github.invvk.mony.internal.utils.TimeUtils;
import io.github.invvk.mony.internal.utils.Utils;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

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
        final String uuid = player.getUniqueId().toString();

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

        double current = user.getLastMaxAmount();

        if (current == 0) {
            event.setCancelled(true);
            return;
        }

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
