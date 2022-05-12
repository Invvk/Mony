package io.github.invvk.mony.api;

import io.github.invvk.mony.api.config.ConfigManager;
import io.github.invvk.mony.api.database.IUserManager;
import io.github.invvk.mony.api.database.storage.IStorage;

import java.util.Optional;

public interface Mony {

    Optional<IStorage> getStorage();

    Optional<IUserManager> getUserManager();

    ConfigManager getConfigManager();

    boolean isTestEnvironment();

    boolean isDailyLimitEnabled();

    String getVersion();

}
