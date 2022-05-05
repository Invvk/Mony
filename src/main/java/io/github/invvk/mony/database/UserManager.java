package io.github.invvk.mony.database;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.github.invvk.mony.MonyLoader;
import io.github.invvk.mony.config.properties.ConfigProperty;
import io.github.invvk.mony.database.data.IDataManager;
import io.github.invvk.mony.database.misc.User;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;

import java.util.Optional;
import java.util.UUID;

public class UserManager {

    private final Cache<UUID, User> usersCache = CacheBuilder.newBuilder()
            .build();

    private final IDataManager dataManager;

    public UserManager(MonyLoader loader) {
        this.dataManager = loader.getBootstrap().getStorageManager().getDataManager();
        long updateInterval = loader.getBootstrap().getConfigManager().getConfig().
                getProperty(ConfigProperty.UPDATE_INTERVAL);
        Bukkit.getScheduler().runTaskTimerAsynchronously(loader, () -> usersCache.asMap()
                        .forEach((uuid, user) -> dataManager.saveUser(user))
                ,updateInterval, updateInterval);
    }

    /**
     * Get User from cache
     *
     * @param uniqueId unique id of the desired user
     * @return Nullable Optional of the desired user
     */
    public Optional<User> getUser(UUID uniqueId) {
        return Optional.ofNullable(usersCache.getIfPresent(uniqueId));
    }

    /**
     * Thread blocking action, should be called async
     *
     * @param uniqueId unique id of the player
     * @param name     name of the player
     */
    @SneakyThrows
    public void createUser(UUID uniqueId, String name) {
        this.usersCache.get(uniqueId, () -> dataManager.getUserFromDatabase(uniqueId, name));
    }

    /**
     * Thread blocking action, should be called async
     *
     * @param uniqueId unqiue id of the player to be removed
     */
    public void removeUser(UUID uniqueId) {
        final User user = this.usersCache.getIfPresent(uniqueId);
        if (user == null)
            return;

        dataManager.saveUser(user);
        this.usersCache.invalidate(uniqueId);
    }

    public void invalidateAll() {
        // Save before Invalidating all user cache
        usersCache.asMap()
                .forEach((uuid, user) -> dataManager.saveUser(user));

        // Invalidate user cache
        this.usersCache.invalidateAll();
    }

}
