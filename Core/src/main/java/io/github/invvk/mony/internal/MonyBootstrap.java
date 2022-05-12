package io.github.invvk.mony.internal;

import io.github.invvk.mony.api.config.properties.ConfigProperty;
import io.github.invvk.mony.api.database.IUserManager;
import io.github.invvk.mony.internal.hook.placeholderapi.PlaceholderHook;
import io.github.invvk.mony.internal.listener.DailyLimitListener;
import io.github.invvk.mony.api.Mony;
import io.github.invvk.mony.api.config.ConfigManager;
import io.github.invvk.mony.internal.database.StorageManager;
import io.github.invvk.mony.internal.database.UserManager;
import io.github.invvk.mony.api.database.storage.IStorage;
import io.github.invvk.mony.internal.hook.VaultHook;
import io.github.invvk.mony.internal.listener.ConnectionListener;
import io.github.invvk.mony.internal.listener.MobKillListener;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.ServicePriority;

import java.io.File;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * INTERAL CLASS, NOT MEANT FOR PUBLIC USE
 * @see io.github.invvk.mony.api.Mony
 */
@RequiredArgsConstructor
public class MonyBootstrap implements Mony {

    private final MonyLoader loader;

    @Getter
    private VaultHook vault;

    @Getter private ConfigManager configManager;

    private UserManager userManager;

    @Getter private StorageManager storageManager;

    private void initial() {
        this.configManager = new ConfigManager(getDataFolder());
        this.storageManager = new StorageManager(this);

        if (this.isDailyLimitEnabled())
            this.userManager = new UserManager(this.loader);
    }

    public void enable() {
        this.initial();
        Bukkit.getServicesManager().register(Mony.class, this, this.loader, ServicePriority.Highest);

        this.vault = new VaultHook(this.loader);

        // only register these listeners if the daily limit is enabled
        // otherwise these events will be rendered useless.
        if (this.isDailyLimitEnabled())
            this.registerListeners(new DailyLimitListener(this),
                    new ConnectionListener(this));

        this.registerListeners(new MobKillListener(this));

        this.enableMetrics();
        this.handleSoftDepends();
    }

    public void disable() {
        this.getUserManager().ifPresent(IUserManager::invalidateAll);
        storageManager.close();
    }

    @Override
    public Optional<IStorage> getStorage() {
        return Optional.ofNullable(this.storageManager.getStorage());
    }

    @Override
    public Optional<IUserManager> getUserManager() {
        return Optional.ofNullable(this.userManager);
    }

    @Override
    public boolean isTestEnvironment() {
        return this.loader.isTestEnvironment();
    }

    @Override
    public boolean isDailyLimitEnabled() {
        return configManager.getConfig().getProperty(ConfigProperty.DAILY_LIMIT_ENABLE);
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

    private void handleSoftDepends() {
        new PlaceholderHook(this).init();
    }

    public void setVault(VaultHook vault) {
        if (this.isTestEnvironment())
            this.vault = vault;
    }

    public File getDataFolder() {
        return this.loader.getDataFolder();
    }

    public Logger getLogger() {
        return this.loader.getLogger();
    }
}
