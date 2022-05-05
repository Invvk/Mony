package io.github.invvk.mony;

import io.github.invvk.mony.config.ConfigManager;
import io.github.invvk.mony.database.storage.IStorage;
import io.github.invvk.mony.hook.VaultHook;
import io.github.invvk.mony.listener.MobKillListener;
import io.github.invvk.mony.database.StorageManager;
import io.github.invvk.mony.database.UserManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public class MonyBootstrap implements Mony{

    private final MonyLoader loader;

    @Getter private VaultHook vault;

    @Getter private ConfigManager configManager;

    @Getter private UserManager userManager;

    @Getter private StorageManager storageManager;

    public void enable() {
        this.initial();
        this.vault = new VaultHook(this.loader);

        this.registerListeners(new MobKillListener(this));
    }

    public void enableUnitTest() {
        this.initial();
    }

    public void disable() {
        this.getUserManager().invalidateAll();
        storageManager.close();
    }

    private void initial() {
        this.configManager = new ConfigManager(this.loader.getDataFolder());
        this.storageManager = new StorageManager(this.loader);
        this.userManager = new UserManager(this.loader);
    }

    private void registerListeners(Listener... listeners) {
        for (Listener listener: listeners)
            Bukkit.getPluginManager().registerEvents(listener, this.loader);
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

}
