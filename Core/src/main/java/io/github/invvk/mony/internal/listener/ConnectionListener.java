package io.github.invvk.mony.internal.listener;

import io.github.invvk.mony.internal.MonyBootstrap;
import io.github.invvk.mony.internal.config.properties.ConfigProperty;
import io.github.invvk.mony.api.database.User;
import io.github.invvk.mony.api.events.MonyCacheLoadEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
public class ConnectionListener implements Listener {

    private final MonyBootstrap bootstrap;

    private final Executor executor = Executors.newFixedThreadPool(5);

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent event) {
        if (!bootstrap.isDailyLimitEnabled())
            return;

        final UUID uuid = event.getUniqueId();
        final String name = event.getName();

        final User user = this.bootstrap.getUserManager().map(userManager -> userManager.createUser(uuid, name)).orElse(null);
        if (user == null)
            return;
        Bukkit.getPluginManager().callEvent(new MonyCacheLoadEvent(event.getUniqueId(), event.getName(), user));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (!bootstrap.getConfigManager().getConfig().getProperty(ConfigProperty.DAILY_LIMIT_ENABLE))
            return;

        executor.execute(() -> bootstrap.getUserManager().
                ifPresent(userManager -> userManager.invalidate(event.getPlayer().getUniqueId())));
    }

}
