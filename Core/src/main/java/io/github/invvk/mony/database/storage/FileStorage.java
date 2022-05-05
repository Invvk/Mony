package io.github.invvk.mony.database.storages;

import io.github.invvk.mony.database.storage.IStorage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.File;

@RequiredArgsConstructor
public class FileStorage implements IStorage {

    private final File plDataFolder;

    @Getter private File dataFolder;

    @Override
    public void init() {
        dataFolder = new File(plDataFolder, "/users");
        if (!dataFolder.exists())
            dataFolder.mkdir();
    }

    @Override
    public void close() {}

}
