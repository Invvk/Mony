package io.github.invvk.mony.database.storages;

import ch.jalu.configme.SettingsManager;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.invvk.mony.MonyLoader;
import io.github.invvk.mony.config.properties.ConfigProperty;
import io.github.invvk.mony.database.storage.IStorage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

@RequiredArgsConstructor
public class MySQLStorage implements IStorage {

    private final MonyLoader loader;

    @Getter private String tablePrefix;

    /**
     * player data table
     */
    @Getter private String pdTable;

    private HikariDataSource dataSource;

    @Override
    public void init() {
        final SettingsManager cnfg = loader.getBootstrap().getConfigManager()
                .getConfig();

        String mode = cnfg.getProperty(ConfigProperty.STORAGE_MODE);

        if (!mode.equalsIgnoreCase("MYSQL"))
            return;

        this.tablePrefix = cnfg.getProperty(ConfigProperty.STORAGE_TABLE_PREFIX);
        this.pdTable = tablePrefix + "_playerData";

        HikariConfig config = new HikariConfig();

        config.setPoolName("Mony-Pool");
        config.setDataSourceClassName("com.mysql.cj.jdbc.MysqlDataSource");
        config.setIdleTimeout(10000);
        config.setMaxLifetime(60000);
        config.setMinimumIdle(1);
        config.addDataSourceProperty("cachePrepStmts", true);
        config.addDataSourceProperty("prepStmtCacheSize", 250);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);

        final Properties properties = new Properties();

        properties.put("serverName", cnfg.getProperty(ConfigProperty.STORAGE_HOST));
        properties.put("port", cnfg.getProperty(ConfigProperty.STORAGE_PORT));
        properties.put("user", cnfg.getProperty(ConfigProperty.STORAGE_USER));
        properties.put("password", cnfg.getProperty(ConfigProperty.STORAGE_PASSWORD));
        properties.put("databaseName", cnfg.getProperty(ConfigProperty.STORAGE_DATABASE));
        properties.putIfAbsent("serverTimezone", "UTC");

        config.setDataSourceProperties(properties);

        dataSource = new HikariDataSource(config);
        this.initTable();
    }

    private void initTable() {
        if (dataSource == null)
            return;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement st = connection.prepareStatement(
                     String.format("CREATE TABLE IF NOT EXISTS %s(uuid VARCHAR(36) NOT NULL, name VARCHAR(16) NOT NULL, cooldown BIGINT)", this.pdTable))) {
            st.executeUpdate();
        } catch (SQLException e) {
            loader.getLogger().log(Level.SEVERE, "Failed to create table", e);
        }
    }

    @Override
    public void close() {
        if (this.dataSource != null)
            this.dataSource.close();
    }

    public final Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }

}
