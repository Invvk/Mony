package io.github.invvk.mony.listener;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.github.invvk.mony.MonyBootstrap;
import io.github.invvk.mony.config.properties.ConfigProperty;
import io.github.invvk.mony.database.User;
import io.github.invvk.mony.events.PlayerKillMobEvent;
import io.github.invvk.mony.utils.TimeUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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

    @EventHandler
    public void onMobKill(PlayerKillMobEvent event) throws ExecutionException {
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
        } else
            // replace with the new amount
            cache.put(uuid, difference);

        bootstrap.getVault().getEconomy().depositPlayer(player, expectedAmount);
    }

}
