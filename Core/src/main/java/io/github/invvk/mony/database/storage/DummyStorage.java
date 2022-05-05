package io.github.invvk.mony.database.storage;

import io.github.invvk.mony.database.manager.FileDataManager;
import io.github.invvk.mony.database.manager.IDataManager;
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
