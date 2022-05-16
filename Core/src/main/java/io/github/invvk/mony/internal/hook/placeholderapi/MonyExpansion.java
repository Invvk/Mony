package io.github.invvk.mony.internal.hook.placeholderapi;

import io.github.invvk.mony.api.database.IUserManager;
import io.github.invvk.mony.api.database.User;
import io.github.invvk.mony.internal.MonyBootstrap;
import io.github.invvk.mony.internal.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class MonyExpansion extends PlaceholderExpansion {

    private final MonyBootstrap bootstrap;

    @Override
    public boolean canRegister() {
        return bootstrap.isDailyLimitEnabled();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @NotNull List<String> getPlaceholders() {
        return Arrays.asList("mony_dl_cooldown", "mony_dl_amount_left");
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

        final IUserManager userManager = bootstrap.getUserManager().orElse(null);

        if (userManager == null)
            return null;

        if (params.equalsIgnoreCase("dl_cooldown"))
            return userManager.getUser(player.getUniqueId())
                    .map(user -> user.hasCooldown()
                            ? TimeUtils.translateTime(user.getCooldownDifference())
                            : "none").orElse(null);


        if (params.equalsIgnoreCase("dl_amount_left"))
            return String.valueOf(userManager.getUser(player.getUniqueId())
                    .map(User::getLastMaxAmount).orElse(null));

        return null;
    }
}
