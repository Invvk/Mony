package io.github.invvk.mony.test.listeners;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import io.github.invvk.mony.internal.MonyLoader;
import io.github.invvk.mony.internal.listener.ConnectionListener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestConnectionListener {

    private static ServerMock server;
    private static MonyLoader plugin;

    private static ConnectionListener listener;

    @BeforeAll
    static void initAll() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(MonyLoader.class);
        listener = new ConnectionListener(plugin.getBootstrap());
    }

    @Test
    @DisplayName("check if the player is cached correctly")
    public void checkCache() throws UnknownHostException {
        final PlayerMock mock = server.addPlayer();

        // This isn't really running Async here, just for test purposes.
        listener.onJoin(new AsyncPlayerPreLoginEvent(mock.getName(),
                InetAddress.getByName("127.0.0.1") , mock.getUniqueId()));

        // This checks if the user was added to the cache successfully.
        assertTrue(plugin.getBootstrap().getUserManager().getUser(mock.getUniqueId()).isPresent(),
                "Player was not cached correctly");

        // This simulates on quit. the only difference is that the event inside the class ConnectionListener
        // runs this method async due to the fact that it is a
        // thread blocking action. (Database queries - IO operation [Saving User File])
        plugin.getBootstrap().getUserManager().invalidate(mock.getUniqueId());

        // This checks if the user was removed from the cache successfully
        assertFalse(plugin.getBootstrap().getUserManager().getUser(mock.getUniqueId()).isPresent(),
                "Player was not removed correctly");
    }

    @AfterAll
    static void tearDown() {
        MockBukkit.unload();
    }

}
