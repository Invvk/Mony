package io.github.invvk.mony.api.database.storage;

import io.github.invvk.mony.api.database.manager.IDataManager;

public interface IStorage {

    void init();

    void close();

    IDataManager getDataManager();

}
