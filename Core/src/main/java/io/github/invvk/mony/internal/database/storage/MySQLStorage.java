package io.github.invvk.mony.internal.database.storage;

import ch.jalu.configme.SettingsManager;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.invvk.mony.internal.MonyBootstrap;
import io.github.invvk.mony.internal.database.manager.MySQLDataManager;
import io.github.invvk.mony.api.config.properties.ConfigProperty;
import io.github.invvk.mony.api.database.manager.IDataManager;
import io.github.invvk.mony.api.database.storage.IStorage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

@RequiredArgsConstructor
public class MySQLStorage implements IStorage {

    private final MonyBootstrap bootstrap;

    @Getter private String tablePrefix;

    /**
     * player data table
     */
    @Getter private String pdTable;

    private HikariDataSource dataSource;

    private MySQLDataManager dataManager;

    @Override
    public void init() {
        final SettingsManager cnfg = bootstrap.getConfigManager().getConfig();

        String mode = cnfg.getProperty(ConfigProperty.STORAGE_MODE);

        if (!mode.equalsIgnoreCase("MYSQL"))
            return;

        this.tablePrefix = cnfg.getProperty(ConfigProperty.STORAGE_TABLE_PREFIX);
        this.pdTable = tablePrefix + "_playerData";

        HikariConfig config = new HikariConfig();

        config.setPoolName("Mony-Pool");
        config.setJdbcUrl("jdbc:mysql://" + cnfg.getProperty(ConfigProperty.STORAGE_HOST) + ":"
                + cnfg.getProperty(ConfigProperty.STORAGE_PORT) + "/" + cnfg.getProperty(ConfigProperty.STORAGE_DATABASE));
        config.setUsername(cnfg.getProperty(ConfigProperty.STORAGE_USER));
        config.setPassword(cnfg.getProperty(ConfigProperty.STORAGE_PASSWORD));
        config.setIdleTimeout(10000);
        config.setMaxLifetime(60000);
        config.setMinimumIdle(1);
        config.addDataSourceProperty("cachePrepStmts", true);
        config.addDataSourceProperty("prepStmtCacheSize", 250);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);

        final Properties properties = new Properties();
        properties.putIfAbsent("serverTimezone", "UTC");

        config.setDataSourceProperties(properties);

        dataSource = new HikariDataSource(config);
        this.initTable();

        this.dataManager = new MySQLDataManager(this);
    }

    private void initTable() {
        if (dataSource == null)
            return;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement st = connection.prepareStatement(
                     String.format("CREATE TABLE IF NOT EXISTS %s(uuid VARCHAR(36) NOT NULL, name VARCHAR(16) NOT NULL, cooldown BIGINT, amountLeft DOUBLE, PRIMARY KEY (uuid, name), UNIQUE (uuid), UNIQUE(name))", this.pdTable))) {
            st.executeUpdate();
        } catch (SQLException e) {
            bootstrap.getLogger().log(Level.SEVERE, "Failed to create table", e);
        }
    }

    @Override
    public void close() {
        if (this.dataSource != null)
            this.dataSource.close();
    }

    @Override
    public IDataManager getDataManager() {
        return this.dataManager;
    }

    public final Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }

}
