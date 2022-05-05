package io.github.invvk.mony.listener;

import io.github.invvk.mony.MonyLoader;
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

    private final MonyLoader loader;

    private final Executor executor = Executors.newFixedThreadPool(5);

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent event) {
        final UUID uuid = event.getUniqueId();
        final String name = event.getName();
        this.loader.getBootstrap().getUserManager().createUser(uuid, name);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        executor.execute(() -> loader.getBootstrap().getUserManager().
                invalidate(event.getPlayer().getUniqueId()));
    }

}
