package io.github.invvk.mony.hook;

import io.github.invvk.mony.MonyLoader;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

@AllArgsConstructor
public class VaultHook {

    @Getter private Economy economy;

    public VaultHook(MonyLoader loader) {
        RegisteredServiceProvider<Economy> provider = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (provider == null)
            return;
        economy = provider.getProvider();
        loader.getLogger().info("Vault has been loaded successfully!");
    }

}
