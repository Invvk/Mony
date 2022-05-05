package io.github.invvk.mony.database.storage;

import io.github.invvk.mony.database.manager.IDataManager;

public interface IStorage {

    void init();

    void close();

    IDataManager getDataManager();

}
