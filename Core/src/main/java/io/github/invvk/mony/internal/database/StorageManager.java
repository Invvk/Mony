package io.github.invvk.mony.internal.database;

import io.github.invvk.mony.internal.MonyBootstrap;
import io.github.invvk.mony.internal.database.storage.DummyStorage;
import io.github.invvk.mony.internal.database.storage.FileStorage;
import io.github.invvk.mony.internal.database.storage.MySQLStorage;
import io.github.invvk.mony.internal.config.properties.ConfigProperty;
import io.github.invvk.mony.api.database.manager.IDataManager;
import io.github.invvk.mony.api.database.misc.StorageMode;
import io.github.invvk.mony.api.database.storage.IStorage;
import lombok.Getter;

public class StorageManager {

    private final MonyBootstrap bootstrap;

    private final StorageMode mode;

    @Getter private IStorage storage;

    public StorageManager(MonyBootstrap bootstrap) {
        this.bootstrap = bootstrap;

        mode = this.bootstrap.getConfigManager().getConfig().getProperty(ConfigProperty.STORAGE_MODE)
                .equalsIgnoreCase("MYSQL") ? StorageMode.MYSQL : StorageMode.FILE;
        init();
    }

    private void init() {
        if (this.bootstrap.isTestEnvironment()) {
            this.storage = new DummyStorage(this.bootstrap.getDataFolder());
            return;
        }

        if (!bootstrap.getConfigManager().getConfig().getProperty(ConfigProperty.DAILY_LIMIT_ENABLE))
            return;

        this.storage = (mode == StorageMode.MYSQL) ? new MySQLStorage(this.bootstrap) : new FileStorage(this.bootstrap.getDataFolder());
        this.storage.init();
    }

    public void close() {
        if (this.storage != null)
            this.storage.close();
    }

    public IDataManager getDataManager() {
        return this.storage.getDataManager();
    }
}
