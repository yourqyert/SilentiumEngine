package net.silentium.engine.api.database.table;

import net.silentium.engine.api.database.Database;
import net.silentium.engine.api.database.model.DataObject;
import net.silentium.engine.api.database.query.QueryBuilder;
import net.silentium.engine.api.database.query.QueryResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class Table {

    private static final Logger LOGGER = Logger.getLogger("DatabaseAPI");
    private final Database database;
    private final String name;

    public Table(Database database, String name) {
        this.database = database;
        this.name = name;
    }

    public CompletableFuture<Boolean> insert(DataObject dataObject) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = database.getConnection()) {
                return insertSync(conn, dataObject);
            } catch (SQLException e) {
                throw new RuntimeException("Async insert failed", e);
            }
        }, database.getExecutor());
    }

    public CompletableFuture<Integer> update(QueryBuilder queryBuilder, DataObject dataObject) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = database.getConnection()) {
                return updateSync(conn, queryBuilder, dataObject);
            } catch (SQLException e) {
                throw new RuntimeException("Async update failed", e);
            }
        }, database.getExecutor());
    }

    public CompletableFuture<Integer> delete(QueryBuilder queryBuilder) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = database.getConnection()) {
                return deleteSync(conn, queryBuilder);
            } catch (SQLException e) {
                throw new RuntimeException("Async delete failed", e);
            }
        }, database.getExecutor());
    }

    public CompletableFuture<QueryResult> findOne(QueryBuilder queryBuilder) {
        return find(queryBuilder.limit(1));
    }

    public CompletableFuture<QueryResult> find(QueryBuilder queryBuilder) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = database.getConnection()) {
                return findSync(conn, queryBuilder);
            } catch (SQLException e) {
                throw new RuntimeException("Async find failed", e);
            }
        }, database.getExecutor());
    }

    public CompletableFuture<Boolean> exists(QueryBuilder queryBuilder) {
        return findOne(queryBuilder).thenApply(queryResult -> !queryResult.isEmpty());
    }

    public CompletableFuture<Integer> executeRawUpdate(String sql, Object... params) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = database.getConnection()) {
                return executeRawUpdateSync(conn, sql, params);
            } catch (SQLException e) {
                throw new RuntimeException("Async raw update failed", e);
            }
        }, database.getExecutor());
    }

    public boolean insertSync(Connection connection, DataObject dataObject) throws SQLException {
        if (dataObject.isEmpty()) return false;
        StringJoiner columns = new StringJoiner(", ");
        StringJoiner placeholders = new StringJoiner(", ");
        List<Object> values = new ArrayList<>(dataObject.values());
        dataObject.keySet().forEach(key -> {
            columns.add("`" + key + "`");
            placeholders.add("?");
        });
        String sql = "INSERT INTO `" + name + "` (" + columns + ") VALUES (" + placeholders + ")";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < values.size(); i++) {
                pstmt.setObject(i + 1, values.get(i));
            }
            return pstmt.executeUpdate() > 0;
        }
    }

    public int updateSync(Connection connection, QueryBuilder queryBuilder, DataObject dataObject) throws SQLException {
        if (dataObject.isEmpty()) return 0;
        StringJoiner setClause = new StringJoiner(", ");
        List<Object> values = new ArrayList<>();
        dataObject.forEach((key, value) -> {
            setClause.add("`" + key + "` = ?");
            values.add(value);
        });
        values.addAll(queryBuilder.getParams());
        String sql = "UPDATE `" + name + "` SET " + setClause + " " + queryBuilder.buildWhere();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < values.size(); i++) {
                pstmt.setObject(i + 1, values.get(i));
            }
            return pstmt.executeUpdate();
        }
    }

    public int deleteSync(Connection connection, QueryBuilder queryBuilder) throws SQLException {
        String sql = "DELETE FROM `" + name + "` " + queryBuilder.buildWhere();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            List<Object> params = queryBuilder.getParams();
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            return pstmt.executeUpdate();
        }
    }

    public QueryResult findSync(Connection connection, QueryBuilder queryBuilder) throws SQLException {
        List<DataObject> results = new ArrayList<>();
        String sql = "SELECT * FROM `" + name + "` " + queryBuilder.build();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            List<Object> params = queryBuilder.getParams();
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                while (rs.next()) {
                    DataObject row = new DataObject();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(metaData.getColumnName(i), rs.getObject(i));
                    }
                    results.add(row);
                }
            }
        }
        return new QueryResult(results);
    }

    public int executeRawUpdateSync(Connection connection, String sql, Object... params) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            return pstmt.executeUpdate();
        }
    }
}
