package io.github.invvk.mony.api.database;

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

    // cached from the pre login event
    private boolean existsInDB;

    @Expose
    private double lastMaxAmount;

    /**
     * check if the player has any active cooldown
     * @return whether the player has a cooldown or not.
     */
    public boolean hasCooldown() {
        // This will check if the cooldown is indeed not 0 or less
        if (cooldown <= 0)
            return false;

        // This will get the difference between cooldown and current time millis then check if
        // it is greater than 0, if true then the user is on cooldown.
        return this.getCooldownDifference() > 0;
    }

    public long getCooldownDifference() {
        return cooldown - System.currentTimeMillis();
    }

}
