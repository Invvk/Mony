package io.github.invvk.mony;

import io.github.invvk.mony.config.ConfigManager;
import io.github.invvk.mony.database.storage.IStorage;
import io.github.invvk.mony.hook.VaultHook;
import io.github.invvk.mony.listener.MobKillListener;
import io.github.invvk.mony.database.StorageManager;
import io.github.invvk.mony.database.UserManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.ServicePriority;

@RequiredArgsConstructor
public class MonyBootstrap implements Mony{

    private final MonyLoader loader;

    @Getter private VaultHook vault;

    @Getter private ConfigManager configManager;

    @Getter private UserManager userManager;

    @Getter private StorageManager storageManager;

    private void initial() {
        this.configManager = new ConfigManager(this.loader.getDataFolder());
        this.storageManager = new StorageManager(this.loader);
        this.userManager = new UserManager(this.loader);
    }

    public void enable() {
        this.initial();
        Bukkit.getServicesManager().register(Mony.class, this, this.loader, ServicePriority.Highest);

        this.vault = new VaultHook(this.loader);

        this.registerListeners(new MobKillListener(this));
        this.enableMetrics();
    }

    public void disable() {
        this.getUserManager().invalidateAll();
        storageManager.close();
    }

    @Override
    public IStorage getStorage() {
        return this.storageManager.getStorage();
    }

    @Override
    public boolean isTestEnvironment() {
        return this.loader.isTestEnvironment();
    }

    @Override
    public String getVersion() {
        return this.loader.getDescription().getVersion();
    }

    public void enableUnitTest() {
        this.initial();
    }

    private void registerListeners(Listener... listeners) {
        for (Listener listener: listeners)
            Bukkit.getPluginManager().registerEvents(listener, this.loader);
    }

    private void enableMetrics() {
        final Metrics metrics = new Metrics(this.loader, 15147);
        // TODO: not sure what custom charts to add
    }

}
