package io.github.invvk.internal.listener;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.github.invvk.internal.MonyBootstrap;
import io.github.invvk.internal.utils.TimeUtils;
import io.github.invvk.mony.config.properties.ConfigProperty;
import io.github.invvk.mony.database.User;
import io.github.invvk.mony.events.PlayerKillMobEvent;
import io.github.invvk.mony.events.MonyCacheLoadEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class DailyLimitListener implements Listener {

    private final MonyBootstrap bootstrap;

    @Getter private final Cache<String, Double> cache =
            CacheBuilder.newBuilder().expireAfterAccess(8, TimeUnit.SECONDS)
                    .build();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMobKill(PlayerKillMobEvent event) throws ExecutionException {
        if (!bootstrap.getConfigManager().getConfig().getProperty(ConfigProperty.DAILY_LIMIT_ENABLE))
            return;

        final Player player = event.getPlayer();
        final String uuid = player.getUniqueId().toString();

        final Optional<User> optional = bootstrap.getUserManager().getUser(player.getUniqueId());

        if (!optional.isPresent())
            return;

        final User user = optional.get();

        if (user.hasCooldown())
            return;

        double current = cache.get(uuid, () ->
                bootstrap.getConfigManager()
                         .getConfig().getProperty(ConfigProperty.DAILY_LIMIT_MAX_AMOUNT).doubleValue());

        double expectedAmount = event.getAmount();
        final double difference = current - expectedAmount;

        if (current <= expectedAmount) {
            expectedAmount = current;
            user.setCooldown(System.currentTimeMillis() + TimeUtils.getTimeInMilli(bootstrap.getConfigManager().getConfig().
                    getProperty(ConfigProperty.DAILY_LIMIT_COOLDOWN)));
            cache.invalidate(uuid);
            user.setLastMaxAmount(0);
        } else {
            // replace with the new amount
            cache.put(uuid, difference);
            user.setLastMaxAmount(difference);
        }
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
