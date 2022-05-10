package io.github.invvk.mony.database;

import io.github.invvk.mony.MonyBootstrap;
import io.github.invvk.mony.config.properties.ConfigProperty;
import io.github.invvk.mony.database.manager.IDataManager;
import io.github.invvk.mony.database.misc.StorageMode;
import io.github.invvk.mony.database.storage.DummyStorage;
import io.github.invvk.mony.database.storage.FileStorage;
import io.github.invvk.mony.database.storage.IStorage;
import io.github.invvk.mony.database.storage.MySQLStorage;
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
