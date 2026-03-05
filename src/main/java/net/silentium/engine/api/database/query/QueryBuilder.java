package net.silentium.engine.api.database.query;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class QueryBuilder {

    private final List<String> whereClauses = new ArrayList<>();
    private final List<Object> params = new ArrayList<>();
    private String orderByClause = "";
    private String limitClause = "";

    private QueryBuilder() {}

    public static QueryBuilder create() {
        return new QueryBuilder();
    }

    public QueryBuilder where(String column, String operator, Object value) {
        whereClauses.add("`" + column + "` " + operator + " ?");
        params.add(value);
        return this;
    }

    public QueryBuilder where(String column, Object value) {
        return where(column, "=", value);
    }

    public QueryBuilder orderBy(String column, String direction) {
        this.orderByClause = "ORDER BY `" + column + "` " + direction.toUpperCase();
        return this;
    }

    public QueryBuilder limit(int count) {
        this.limitClause = "LIMIT " + count;
        return this;
    }

    public String build() {
        return buildWhere() + " " + orderByClause + " " + limitClause;
    }

    public String buildWhere() {
        if (whereClauses.isEmpty()) {
            return "";
        }
        return "WHERE " + String.join(" AND ", whereClauses);
    }

    public List<Object> getParams() {
        return params;
    }
}
