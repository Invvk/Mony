package io.github.invvk.mony.config;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import io.github.invvk.mony.MonyLoader;
import io.github.invvk.mony.config.properties.ConfigProperty;
import io.github.invvk.mony.config.properties.MessagesProperty;
import lombok.Getter;

import java.io.File;

public class ConfigManager {

    private final MonyLoader loader;

    @Getter private SettingsManager config, message;

    public ConfigManager(MonyLoader loader) {
        this.loader = loader;
        this.init();
    }

    private void init() {
        config = construct("config", ConfigProperty.class);
        message = construct("messages", MessagesProperty.class);
    }

    private SettingsManager construct(String fileName, Class<? extends SettingsHolder> holder) {
        return SettingsManagerBuilder.withYamlFile(new File(loader.getDataFolder(), fileName + ".yml"))
                .useDefaultMigrationService()
                .configurationData(holder)
                .create();
    }

}
