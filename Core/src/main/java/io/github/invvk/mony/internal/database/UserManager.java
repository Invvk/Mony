package io.github.invvk.mony.internal.database;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.github.invvk.mony.internal.MonyLoader;
import io.github.invvk.mony.api.config.properties.ConfigProperty;
import io.github.invvk.mony.api.database.IUserManager;
import io.github.invvk.mony.api.database.User;
import io.github.invvk.mony.api.database.manager.IDataManager;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;

import java.util.Optional;
import java.util.UUID;

public class UserManager implements IUserManager {

    private final Cache<UUID, User> usersCache = CacheBuilder.newBuilder()
            .build();

    private final IDataManager dataManager;

    public UserManager(MonyLoader loader) {
        this.dataManager = loader.getBootstrap().getStorageManager().getDataManager();
        long updateInterval = loader.getBootstrap().getConfigManager().getConfig().
                getProperty(ConfigProperty.UPDATE_INTERVAL) * 20 * 60;
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
    @Override
    public Optional<User> getUser(UUID uniqueId) {
        return Optional.ofNullable(usersCache.getIfPresent(uniqueId));
    }

    /**
     * Thread blocking action, should be called async
     *
     * @param uniqueId unique id of the player
     * @param name     name of the player
     * @return the created user
     */
    @SneakyThrows
    @Override
    public User createUser(UUID uniqueId, String name) {
        return this.usersCache.get(uniqueId, () -> dataManager.getUserFromDatabase(uniqueId, name));
    }

    /**
     * Thread blocking action, should be called async
     *
     * @param uniqueId unqiue id of the player to be removed
     */
    @Override
    public void invalidate(UUID uniqueId) {
        final User user = this.usersCache.getIfPresent(uniqueId);
        if (user == null)
            return;

        dataManager.saveUser(user);
        this.usersCache.invalidate(uniqueId);
    }

    @Override
    public void invalidateAll() {
        // Save before Invalidating all user cache
        usersCache.asMap()
                .forEach((uuid, user) -> dataManager.saveUser(user));

        // Invalidate user cache
        this.usersCache.invalidateAll();
    }

}
