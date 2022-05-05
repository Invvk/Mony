package io.github.invvk.mony.database;

import io.github.invvk.mony.MonyLoader;
import io.github.invvk.mony.config.properties.ConfigProperty;
import io.github.invvk.mony.database.data.FileDataManager;
import io.github.invvk.mony.database.data.IDataManager;
import io.github.invvk.mony.database.storages.DummyStorage;
import io.github.invvk.mony.database.storages.FileStorage;
import io.github.invvk.mony.database.misc.StorageMode;
import io.github.invvk.mony.database.data.MySQLDataManager;
import io.github.invvk.mony.database.storages.IStorage;
import io.github.invvk.mony.database.storages.MySQLStorage;
import lombok.Getter;

public class StorageManager {

    private final MonyLoader plugin;

    private final StorageMode mode;

    @Getter private IStorage storage;
    @Getter private IDataManager dataManager;

    public StorageManager(MonyLoader bootstrap) {
        this.plugin = bootstrap;

        mode = this.plugin.getBootstrap().getConfigManager().getConfig().getProperty(ConfigProperty.STORAGE_MODE)
                .equalsIgnoreCase("MYSQL") ? StorageMode.MYSQL : StorageMode.FILE;
        init();
    }

    private void init() {
        if (this.plugin.isTestEnvironment()) {
            this.storage = new DummyStorage();
            this.dataManager = new FileDataManager(this.plugin.getDataFolder());
            return;
        }

        if (mode == StorageMode.MYSQL) {
            final MySQLStorage mysql = new MySQLStorage(this.plugin);
            this.storage = mysql;
            this.storage.init();
            dataManager = new MySQLDataManager(mysql);
        } else {
            final FileStorage fileStorage = new FileStorage(this.plugin.getDataFolder());
            this.storage = fileStorage;
            this.dataManager = new FileDataManager(fileStorage.getDataFolder());
        }
    }

    public void close() {
        if (this.storage != null)
            this.storage.close();
    }

}
