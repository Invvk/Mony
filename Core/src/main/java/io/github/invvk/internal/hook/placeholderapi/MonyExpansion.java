package io.github.invvk.internal.hook.placeholderapi;

import io.github.invvk.internal.MonyBootstrap;
import io.github.invvk.internal.utils.TimeUtils;
import io.github.invvk.mony.database.User;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public class MonyExpansion extends PlaceholderExpansion {

    private final MonyBootstrap bootstrap;

    @Override
    public boolean canRegister() {
        return bootstrap != null
                && bootstrap.getStorageManager() != null
                && bootstrap.getStorageManager().getStorage() != null
                && bootstrap.getStorageManager().getDataManager() != null
                && bootstrap.getUserManager() != null;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "mony";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Invvk";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {

        if (params.equalsIgnoreCase("dl_cooldown"))
            return bootstrap.getUserManager().getUser(player.getUniqueId())
                    .map(user -> user.hasCooldown()
                            ? TimeUtils.translateTime(user.getCooldownDifference())
                            : "none").orElse(null);


        if (params.equalsIgnoreCase("dl_amount_left"))
            return String.valueOf(bootstrap.getUserManager().getUser(player.getUniqueId())
                    .map(User::getLastMaxAmount).orElse(null));

        return null;
    }
}
