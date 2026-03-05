package net.silentium.engine.internal.cooldown;

import net.silentium.engine.api.cooldown.Cooldown;
import net.silentium.engine.api.database.Database;
import net.silentium.engine.api.database.query.QueryBuilder;
import net.silentium.engine.api.database.table.Table;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CooldownDAOImpl implements CooldownDAO {

    private final Table cooldownsTable;

    public CooldownDAOImpl(Database database) {
        this.cooldownsTable = database.getTable("engine_cooldowns");
    }

    @Override
    public void createTable() {
        cooldownsTable.executeRawUpdate(
                "CREATE TABLE IF NOT EXISTS `engine_cooldowns` (" +
                        "`owner` VARCHAR(36) NOT NULL," +
                        "`key` VARCHAR(128) NOT NULL," +
                        "`expiry_timestamp` BIGINT NOT NULL," +
                        "PRIMARY KEY (`owner`, `key`)" +
                        ") ENGINE=InnoDB;"
        );
    }

    @Override
    public CompletableFuture<Void> saveOrUpdate(Cooldown cooldown) {
        String sql = "REPLACE INTO `engine_cooldowns` (`owner`, `key`, `expiry_timestamp`) VALUES (?, ?, ?);";
        return cooldownsTable.executeRawUpdate(sql,
                cooldown.getOwner().toString(),
                cooldown.getKey(),
                cooldown.getExpiryTimestamp()
        ).thenAccept(v -> {});
    }

    @Override
    public CompletableFuture<Void> delete(UUID owner, String key) {
        return cooldownsTable.delete(
                QueryBuilder.create().where("owner", owner.toString()).where("key", key)
        ).thenAccept(v -> {});
    }

    @Override
    public CompletableFuture<List<Cooldown>> loadAllFor(UUID owner) {
        return cooldownsTable.find(QueryBuilder.create().where("owner", owner.toString()))
                .thenApply(result -> result.mapTo(Cooldown.class));
    }
}
