package io.github.invvk.mony.database.manager;

import io.github.invvk.mony.database.User;

import java.util.UUID;

public interface IDataManager {

    User getUserFromDatabase(UUID uuid, String name);

    void saveUser(User user);

}
