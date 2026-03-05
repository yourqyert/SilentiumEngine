package net.silentium.engine.api.user;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface UserDAO {

    CompletableFuture<Optional<User>> findUserByUUID(UUID uuid);
    CompletableFuture<Void> createUser(User user);
    CompletableFuture<Void> updateUser(User user);
    void createTable();

    Optional<User> findUserByUUIDSync(Connection connection, UUID uuid) throws SQLException;

    void createUserSync(Connection connection, User user) throws SQLException;

    void updateUserSync(Connection connection, User user) throws SQLException;
}