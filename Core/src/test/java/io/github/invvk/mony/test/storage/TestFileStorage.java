package io.github.invvk.mony.test.storage;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import io.github.invvk.mony.internal.MonyLoader;
import io.github.invvk.mony.api.database.User;
import io.github.invvk.mony.internal.database.manager.FileDataManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestFileStorage {

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
    @DisplayName("Check if the user gets loaded and saved correctly")
    public void checkFileStorage() {
        final File file = plugin.getDataFolder();
        final FileDataManager fileDataManager = new FileDataManager(file);
        final User user = fileDataManager.getUserFromDatabase(UUID.randomUUID(), "test");
        fileDataManager.saveUser(user);

        // deliberately setting a different username because if the user doesn't get loaded from the file
        // it is going to default to the original name in the constructor
        // so, in case the file doesn't load, the test will fail here.
        final User secondUser = fileDataManager.getUserFromDatabase(user.getUniqueId(), "test1");

        // check if the loaded user from file (secondUser)
        // has the same name as the original hard coded user (user).
        assertEquals(user.getName(), secondUser.getName(), "User has to have the correct name.");

        // delete all generated files on exit just in case
        file.deleteOnExit();
    }

}
