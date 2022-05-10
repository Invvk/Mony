package io.github.invvk.internal.hook.placeholderapi;

import io.github.invvk.internal.MonyBootstrap;
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
