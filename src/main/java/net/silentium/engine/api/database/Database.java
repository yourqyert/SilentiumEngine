package net.silentium.engine.api.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.silentium.engine.api.database.table.Table;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {

    private static final Logger LOGGER = Logger.getLogger("DatabaseAPI");
    private final HikariDataSource dataSource;
    private final ExecutorService executor;

    public Database(String host, int port, String user, String password, String database) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        config.setUsername(user);
        config.setPassword(password);
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setConnectionTestQuery("SELECT 1");
        config.setPoolName("Silentium-DB-Pool");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");
        config.addDataSourceProperty("autoReconnect", "true");
        config.addDataSourceProperty("useSSL", "false");
        config.addDataSourceProperty("useUnicode", "true");
        config.addDataSourceProperty("characterEncoding", "UTF-8");

        this.dataSource = new HikariDataSource(config);
        this.executor = Executors.newFixedThreadPool(4, r -> {
            Thread t = new Thread(r);
            t.setName("Silentium-DB-Worker");
            t.setDaemon(true);
            return t;
        });
        LOGGER.info("Database pool and async executor initialized for " + database);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public void close() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            LOGGER.info("Database executor service shut down.");
        }
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            LOGGER.info("Database connection pool has been closed.");
        }
    }

    public Table getTable(String tableName) {
        return new Table(this, tableName);
    }

    public static void setLogLevel(Level level) {
        LOGGER.setLevel(level);
    }

    @FunctionalInterface
    public interface TransactionalOperation<T> {
        T execute(Connection connection) throws Exception;
    }

    public <T> CompletableFuture<T> transaction(TransactionalOperation<T> operation) {
        return CompletableFuture.supplyAsync(() -> {
            Connection connection = null;
            try {
                connection = getConnection();
                connection.setAutoCommit(false);

                T result = operation.execute(connection);

                connection.commit();
                return result;
            } catch (Exception e) {
                if (connection != null) {
                    try {
                        connection.rollback();
                    } catch (SQLException rollbackEx) {
                        LOGGER.severe("Failed to rollback transaction: " + rollbackEx.getMessage());
                    }
                }
                throw new RuntimeException("Transaction failed", e);
            } finally {
                if (connection != null) {
                    try {
                        connection.setAutoCommit(true);
                        connection.close();
                    } catch (SQLException closeEx) {
                        LOGGER.severe("Failed to close connection after transaction: " + closeEx.getMessage());
                    }
                }
            }
        }, getExecutor());
    }
}
