package io.github.invvk.mony.database;

import com.google.gson.annotations.Expose;
import lombok.Data;

import java.util.UUID;

@Data
public class User {

    @Expose
    private final UUID uniqueId;
    @Expose
    private final String name;

    // cooldown till the next daily limit
    @Expose
    private long cooldown;

    // should be used in queries, not 'name'
    private String dbName;

    // cached from the pre login event
    private boolean existsInDB;

    /**
     * This method check if the player's username matches the saved username in the database.
     * @return whether the username matches db username
     */
    public boolean nameMatchesDB() {
        return name.equalsIgnoreCase(dbName);
    }

    /**
     * check if the player has any active cooldown
     * @return whether the player has a cooldown or not.
     */
    public boolean hasCooldown() {
        if (cooldown <= 0)
            return false;

        final long current = System.currentTimeMillis();
        final long value = cooldown - current;
        return value > 0;
    }

}
