package io.github.invvk.mony.internal;

import lombok.Getter;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;

public final class MonyLoader extends JavaPlugin {

    @Getter private MonyBootstrap bootstrap;

    @Getter private final boolean testEnvironment;

    public MonyLoader() {
        super();
        testEnvironment = false;
    }

    public MonyLoader(JavaPluginLoader loader, PluginDescriptionFile description, File
            dataFolder, File file) {
        super(loader, description, dataFolder, file);
        // This is only invoked if we are running in a test environment
        testEnvironment = true;
    }

    @Override
    public void onLoad() {
        bootstrap = new MonyBootstrap(this);
    }

    @Override
    public void onEnable() {
        if (testEnvironment)
            bootstrap.enableUnitTest();
        else
            bootstrap.enable();
    }

    @Override
    public void onDisable() {
        bootstrap.disable();
    }

}
