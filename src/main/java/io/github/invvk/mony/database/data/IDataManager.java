package io.github.invvk.mony.database.data;

import io.github.invvk.mony.database.misc.User;

import java.util.UUID;

public interface IDataManager {

    User getUserFromDatabase(UUID uuid, String name);

    void saveUser(User user);

}
