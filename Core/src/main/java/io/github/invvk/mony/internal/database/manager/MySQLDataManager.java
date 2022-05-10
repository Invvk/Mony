package io.github.invvk.mony.internal.database.manager;

import io.github.invvk.mony.api.database.User;
import io.github.invvk.mony.api.database.manager.IDataManager;
import io.github.invvk.mony.internal.database.storage.MySQLStorage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

@RequiredArgsConstructor
public class MySQLDataManager implements IDataManager {

    private final MySQLStorage storage;

    private static final String PLAYER_SELECT = "SELECT name,cooldown FROM '%s' WHERE uuid=?";
    private static final String PLAYER_INSERT_SAVE = "INSERT INTO '%s'(uuid,name,cooldown) VALUES(?,?,?)";
    private static final String PLAYER_UPDATE_SAVE = "UPDATE '%s' SET cooldown=? WHERE uuid=?";
    private static final String PLAYER_UPDATE_SAVE_USERNAME = "UPDATE '%s' SET cooldown=?,name=? WHERE uuid=?";

    @SneakyThrows
    public User getUserFromDatabase(UUID uuid, String name) {
        final User user = new User(uuid, name);
        user.setDbName(name);
        user.setExistsInDB(false);

        try (final Connection connection = storage.getConnection();
             final PreparedStatement st = connection.prepareStatement(String.format(
                PLAYER_SELECT, storage.getPdTable()))) {
            st.setString(1, uuid.toString());
            try (final ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    user.setExistsInDB(true);
                    user.setCooldown(rs.getLong(2));

                    // DbName will be used in queries, this adds support for premium account username change.
                    final String selectedName = rs.getString(1);
                    if (!selectedName.equalsIgnoreCase(name))
                        user.setDbName(selectedName);
                }
            }
        }
        return user;
    }

    @SneakyThrows
    public void saveUser(User user) {
        try (final Connection connection = storage.getConnection()) {
             if (user.isExistsInDB()) {
                 if (user.nameMatchesDB()) updateCooldown(connection, user);
                  else updateCooldownAndName(connection, user);
             } else {
                 insert(connection, user);
             }
        }
    }

    @SneakyThrows
    public void insert(Connection connection, User user) {
        try (final PreparedStatement st = connection.prepareStatement(String.format(PLAYER_INSERT_SAVE, storage.getPdTable()))) {
            st.setString(1, user.getUniqueId().toString());
            st.setString(2, user.getName());
            st.setLong(3, user.getCooldown());
            st.executeUpdate();
        }
    }

    @SneakyThrows
    private void updateCooldown(Connection connection, User user) {
        try (final PreparedStatement st = connection.prepareStatement(String.format(PLAYER_UPDATE_SAVE,
                storage.getPdTable()))) {
            st.setLong(1, user.getCooldown());
            st.setString(2, user.getUniqueId().toString());
            st.executeUpdate();
        }
    }

    @SneakyThrows
    private void updateCooldownAndName(Connection connection, User user) {
        try (final PreparedStatement st = connection.prepareStatement(String.format(PLAYER_UPDATE_SAVE_USERNAME,
                storage.getPdTable()))) {
            st.setLong(1, user.getCooldown());
            st.setString(2, user.getName());
            st.setString(3, user.getUniqueId().toString());
            st.executeUpdate();
        }
    }

}
