package io.github.invvk.mony.database;

import io.github.invvk.mony.MonyLoader;
import io.github.invvk.mony.config.properties.ConfigProperty;
import io.github.invvk.mony.database.manager.IDataManager;
import io.github.invvk.mony.database.misc.StorageMode;
import io.github.invvk.mony.database.storage.DummyStorage;
import io.github.invvk.mony.database.storage.FileStorage;
import io.github.invvk.mony.database.storage.IStorage;
import io.github.invvk.mony.database.storage.MySQLStorage;
import lombok.Getter;

public class StorageManager {

    private final MonyLoader plugin;

    private final StorageMode mode;

    @Getter private IStorage storage;

    public StorageManager(MonyLoader bootstrap) {
        this.plugin = bootstrap;

        mode = this.plugin.getBootstrap().getConfigManager().getConfig().getProperty(ConfigProperty.STORAGE_MODE)
                .equalsIgnoreCase("MYSQL") ? StorageMode.MYSQL : StorageMode.FILE;
        init();
    }

    private void init() {
        if (this.plugin.isTestEnvironment()) {
            this.storage = new DummyStorage(this.plugin.getDataFolder());
            return;
        }

        this.storage = (mode == StorageMode.MYSQL) ? new MySQLStorage(this.plugin) : new FileStorage(this.plugin.getDataFolder());
    }

    public void close() {
        if (this.storage != null)
            this.storage.close();
    }

    public IDataManager getDataManager() {
        return this.storage.getDataManager();
    }
}
