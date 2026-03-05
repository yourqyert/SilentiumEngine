package net.silentium.engine.internal.user;

import net.silentium.engine.api.database.Database;
import net.silentium.engine.api.database.model.DataObject;
import net.silentium.engine.api.database.query.QueryBuilder;
import net.silentium.engine.api.database.table.Table;
import net.silentium.engine.api.user.User;
import net.silentium.engine.api.user.UserDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UserDAOImpl implements UserDAO {

    private final Table usersTable;

    public UserDAOImpl(Database database) {
        this.usersTable = database.getTable("engine_users");
    }

    @Override
    public CompletableFuture<Optional<User>> findUserByUUID(UUID uuid) {
        return usersTable.findOne(QueryBuilder.create().where("uuid", uuid.toString()))
                .thenApply(result -> result.mapFirstTo(User.class));
    }

    @Override
    public CompletableFuture<Void> createUser(User user) {
        return usersTable.insert(userToDataObject(user)).thenAccept(v -> {});
    }

    @Override
    public CompletableFuture<Void> updateUser(User user) {
        return usersTable.update(
                QueryBuilder.create().where("uuid", user.getUuid().toString()),
                userToDataObject(user)
        ).thenAccept(v -> {});
    }

    @Override
    public void createTable() {
        usersTable.executeRawUpdate(
                "CREATE TABLE IF NOT EXISTS `engine_users` (" +
                        "`uuid` VARCHAR(36) NOT NULL PRIMARY KEY," +
                        "`nickname` VARCHAR(16) NOT NULL," +
                        "`language` VARCHAR(10) DEFAULT NULL," +
                        "`roubles` INT DEFAULT 0," +
                        "`playtime` BIGINT DEFAULT 0," +
                        "`lastSeen` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                        "`playerKills` INT DEFAULT 0," +
                        "`mutantKills` INT DEFAULT 0," +
                        "`bossKills` INT DEFAULT 0," +
                        "`deaths` INT DEFAULT 0," +
                        "`title` VARCHAR(255) DEFAULT NULL," +
                        "`faction` VARCHAR(255) DEFAULT NULL" +
                        ") ENGINE=InnoDB;"
        );
    }

    @Override
    public Optional<User> findUserByUUIDSync(Connection connection, UUID uuid) throws SQLException {
        return usersTable.findSync(connection, QueryBuilder.create().where("uuid", uuid.toString()))
                .mapFirstTo(User.class);
    }

    @Override
    public void createUserSync(Connection connection, User user) throws SQLException {
        usersTable.insertSync(connection, userToDataObject(user));
    }

    @Override
    public void updateUserSync(Connection connection, User user) throws SQLException {
        usersTable.updateSync(
                connection,
                QueryBuilder.create().where("uuid", user.getUuid().toString()),
                userToDataObject(user)
        );
    }

    private DataObject userToDataObject(User user) {
        return new DataObject()
                .with("uuid", user.getUuid().toString())
                .with("nickname", user.getNickname())
                .with("language", user.getLanguage())
                .with("roubles", user.getRoubles())
                .with("playtime", user.getPlaytime())
                .with("lastSeen", user.getLastSeen())
                .with("playerKills", user.getPlayerKills())
                .with("mutantKills", user.getMutantKills())
                .with("bossKills", user.getBossKills())
                .with("deaths", user.getDeaths())
                .with("title", user.getTitle())
                .with("faction", user.getFaction());
    }
}