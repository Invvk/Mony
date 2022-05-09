package io.github.invvk.mony.tests.listeners;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import io.github.invvk.mony.MonyLoader;
import io.github.invvk.mony.events.PlayerKillMobEvent;
import io.github.invvk.mony.hook.VaultHook;
import io.github.invvk.mony.listener.DailyLimitListener;
import io.github.invvk.mony.vault.TestEconomy;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.ExecutionException;

public class TestDailyLimitListener {

    private static ServerMock server;
    private static MonyLoader plugin;
    private static DailyLimitListener listener;

    @BeforeAll
    static void initAll() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(MonyLoader.class);
        listener = new DailyLimitListener(plugin.getBootstrap());

        // Dummy Vault Economy
        plugin.getBootstrap().setVault(new VaultHook(new TestEconomy()));
    }

    @AfterAll
    static void tearDown() {
        MockBukkit.unload();
    }

    @Test
    public void checkDailyLimit() throws ExecutionException {
        final PlayerMock player = server.addPlayer();

        // Mock an entity
        final Entity entity = Mockito.mock(Entity.class);

        // Dummy PlayerKillMobEvent trigger.
        final PlayerKillMobEvent event = new PlayerKillMobEvent(player, entity, 31);

        // IMPORTANT: create the user so the user don't get ignored by the listener
        plugin.getBootstrap().getUserManager().createUser(player.getUniqueId(), player.getName());

        // check if the daily limit only gives the correct amount.
        // Daily limit is set to 1000, so the player should only get 1000$
        double expected = 1000;

        for (int i = 0; i < 40; i++)
            listener.onMobKill(event);

        // The daily limit should only allow to give the correct amount
        // no matter what the original amount was.
        Assertions.assertEquals(expected, plugin.getBootstrap().getVault().getEconomy().getBalance(player));

        // Check if the user was removed from the cache. prevent saving up unnecessary data in-memory.
        Assertions.assertNull(listener.getCache().getIfPresent(player.getName()));

        // Check if the user is in cooldown
        // The optional is always present because we created the user above.
        // so the optional#isPresent() check is unnecessary here, and we can just use optional#get Directly
        Assertions.assertTrue(plugin.getBootstrap().getUserManager().getUser(player.getUniqueId()).get()
                .hasCooldown());
    }

}
