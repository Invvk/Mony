package io.github.invvk.mony.internal.hook.placeholderapi;

import io.github.invvk.mony.internal.MonyBootstrap;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;

@RequiredArgsConstructor
public class PlaceholderHook {

    private final MonyBootstrap bootstrap;

    public void init() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null)
            return;

        new MonyExpansion(bootstrap).register();
    }

}
