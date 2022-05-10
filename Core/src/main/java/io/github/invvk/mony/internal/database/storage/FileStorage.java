package io.github.invvk.mony.internal.database.storage;

import io.github.invvk.mony.internal.database.manager.FileDataManager;
import io.github.invvk.mony.database.manager.IDataManager;
import io.github.invvk.mony.database.storage.IStorage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.File;

@RequiredArgsConstructor
public class FileStorage implements IStorage {

    private final File plDataFolder;

    @Getter private File dataFolder;

    private FileDataManager fileDataManager;

    @Override
    public void init() {
        dataFolder = new File(plDataFolder, "/users");
        if (!dataFolder.exists())
            dataFolder.mkdir();

        this.fileDataManager = new FileDataManager(dataFolder);
    }

    @Override
    public void close() {}

    @Override
    public IDataManager getDataManager() {
        return fileDataManager;
    }

}
