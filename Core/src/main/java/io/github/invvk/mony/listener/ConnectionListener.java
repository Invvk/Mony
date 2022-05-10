package io.github.invvk.mony.listener;

import io.github.invvk.mony.MonyBootstrap;
import io.github.invvk.mony.config.properties.ConfigProperty;
import lombok.RequiredArgsConstructor;
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
        if (!bootstrap.getConfigManager().getConfig().getProperty(ConfigProperty.DAILY_LIMIT_ENABLE))
            return;

        final UUID uuid = event.getUniqueId();
        final String name = event.getName();

        this.bootstrap.getUserManager().createUser(uuid, name);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (!bootstrap.getConfigManager().getConfig().getProperty(ConfigProperty.DAILY_LIMIT_ENABLE))
            return;

        executor.execute(() -> bootstrap.getUserManager().
                invalidate(event.getPlayer().getUniqueId()));
    }

}
