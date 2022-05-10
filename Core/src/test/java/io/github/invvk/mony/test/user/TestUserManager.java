package io.github.invvk.mony.test.user;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import io.github.invvk.mony.internal.MonyLoader;
import io.github.invvk.mony.database.User;
import org.junit.jupiter.api.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestUserManager {

    private static ServerMock server;
    private static MonyLoader plugin;

    @BeforeAll
    static void initAll() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(MonyLoader.class);
    }

    @AfterAll
    static void tearDown() {
        MockBukkit.unload();
    }

    @Test
    @DisplayName("Validating the logic of hasCooldown() check")
    public void runCooldownCheck() {
        final User user = new User(UUID.randomUUID(), "1313r23");

        long cooldown = System.currentTimeMillis() - 1000;
        user.setCooldown(cooldown);

        Assertions.assertFalse(user.hasCooldown(), "cooldown has ended, yet user#hasCooldown() returns true");

        cooldown = System.currentTimeMillis() + 20000;
        user.setCooldown(cooldown);

        Assertions.assertTrue(user.hasCooldown(), "cooldown didn't end, yet user#hasCooldown() returns false");
    }

    @Test
    @DisplayName("check if user#matchesName() works correctly")
    public void checkMatchesName() {
        final User user = new User(UUID.randomUUID(), "Invvk");

        user.setDbName("Invvk");
        assertTrue(user.nameMatchesDB(), "User name didn't match (Val1: Invvk, Val2: Invvk)");

        user.setDbName("Invvk2");
        assertFalse(user.nameMatchesDB(), "User name matched (Val1: Invvk, Val2: Invvk2)");
    }

}
