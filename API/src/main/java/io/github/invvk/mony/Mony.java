package io.github.invvk.mony;

import io.github.invvk.mony.config.ConfigManager;
import io.github.invvk.mony.database.IUserManager;
import io.github.invvk.mony.database.storage.IStorage;

public interface Mony {

    IStorage getStorage();

    IUserManager getUserManager();

    ConfigManager getConfigManager();

    boolean isTestEnvironment();

    String getVersion();

}
