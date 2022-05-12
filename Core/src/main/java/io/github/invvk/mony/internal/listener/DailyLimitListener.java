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

    @Getter private final Cache<String, Double> cache;

    public DailyLimitListener(MonyBootstrap bootstrap) {
        this.bootstrap = bootstrap;
        this.cache =
        CacheBuilder.newBuilder().expireAfterAccess(TimeUtils.getTimeInSeconds
                        (bootstrap.getConfigManager().getConfig().
                                getProperty(ConfigProperty.DAILY_LIMIT_COOLDOWN)), TimeUnit.HOURS)
                .build();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMobKill(PlayerKillMobEvent event) throws ExecutionException {
        if (!bootstrap.getConfigManager().getConfig().getProperty(ConfigProperty.DAILY_LIMIT_ENABLE))
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

        double current = cache.get(uuid, () ->
                bootstrap.getConfigManager()
                         .getConfig().getProperty(ConfigProperty.DAILY_LIMIT_MAX_AMOUNT).doubleValue());

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
            cache.invalidate(uuid);
            user.setLastMaxAmount(0);
            player.sendMessage(Utils.color(bootstrap.getConfigManager().getMessage().
                    getProperty(MessagesProperty.PLAYER_COOLDOWN_TRIGGER).replace("{TIME}",
                            bootstrap.getConfigManager().getConfig().
                            getProperty(ConfigProperty.DAILY_LIMIT_COOLDOWN))));
        } else {
            // replace with the new amount
            cache.put(uuid, difference);
            user.setLastMaxAmount(difference);
        }
        ActionbarUtils.sendActionBar(player, Utils.color(bootstrap.getConfigManager()
                .getMessage().getProperty(MessagesProperty.MOB_KILL_ACTIONBAR).replace("{AMOUNT}",
                        String.valueOf(expectedAmount)).replace("{MOB}", StringUtils.
                        capitalize(event.getType().name().toLowerCase().replace("_", " ")))));
        event.setAmount(expectedAmount);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(MonyCacheLoadEvent event) {
        final User user = event.getLoadedUser();
        final String uuid = user.getUniqueId().toString();

        // This guard clause avoid setting LastMaxAmount to DAILY_LIMIT_MAX_AMOUNT
        // Because the player is in cooldown meaning 'cachedAmount' will definitely return null
        if (user.hasCooldown()) {
            user.setLastMaxAmount(0);

            // Just in case, invalidate the user's cache if for
            // some reason it wasn't invalidated already
            cache.invalidate(uuid);
            return;
        }

        final Double cachedAmount = cache.getIfPresent(user.getUniqueId().toString());
        if (cachedAmount == null) {
            user.setLastMaxAmount(bootstrap.getConfigManager().getConfig().getProperty(ConfigProperty
                    .DAILY_LIMIT_MAX_AMOUNT));
            return;
        }

        user.setLastMaxAmount(cachedAmount);
    }

}
