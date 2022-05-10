package io.github.invvk.mony.internal.database.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.invvk.mony.database.User;
import io.github.invvk.mony.database.manager.IDataManager;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
public class FileDataManager implements IDataManager {

    private final File dataFolder;

    private static final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
            .setPrettyPrinting()
            .create();

    @Override
    public User getUserFromDatabase(UUID uuid, String name) {
        final User user = new User(uuid, name);
        final File userFile = new File(dataFolder, uuid.toString() + ".json");
        if (userFile.exists()) {
            try (FileReader reader = new FileReader(userFile)) {
                return gson.fromJson(reader, User.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return user;
    }

    @Override
    public void saveUser(User user) {
        final File userFile = new File(dataFolder, user.getUniqueId().toString() + ".json");
        if (!userFile.exists()) {
            try {
                userFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try (FileWriter writer = new FileWriter(userFile)) {
             gson.toJson(user, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
