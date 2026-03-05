package net.silentium.engine.api.database.model;

import java.util.LinkedHashMap;

public class DataObject extends LinkedHashMap<String, Object> {

    public DataObject with(String key, Object value) {
        this.put(key, value);
        return this;
    }
}
