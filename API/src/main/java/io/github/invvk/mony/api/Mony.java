package io.github.invvk.mony.api;

import io.github.invvk.mony.api.config.ConfigManager;
import io.github.invvk.mony.api.database.IUserManager;
import io.github.invvk.mony.api.database.storage.IStorage;

public interface Mony {

    IStorage getStorage();

    IUserManager getUserManager();

    ConfigManager getConfigManager();

    boolean isTestEnvironment();

    String getVersion();

}
