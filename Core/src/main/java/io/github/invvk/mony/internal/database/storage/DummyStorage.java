package io.github.invvk.mony.internal.database.storage;

import io.github.invvk.mony.internal.database.manager.FileDataManager;
import io.github.invvk.mony.api.database.manager.IDataManager;
import io.github.invvk.mony.api.database.storage.IStorage;
import lombok.RequiredArgsConstructor;

import java.io.File;

@RequiredArgsConstructor
public class DummyStorage implements IStorage {

    private final File file;

    @Override
    public void init() {}

    @Override
    public void close() {}

    @Override
    public IDataManager getDataManager() {
        return new FileDataManager(file);
    }

}
