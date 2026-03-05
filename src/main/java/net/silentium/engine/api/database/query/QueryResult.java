package net.silentium.engine.api.database.query;

import net.silentium.engine.api.database.model.DataObject;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

public class QueryResult {

    private static final Logger LOGGER = Logger.getLogger("DatabaseAPI");
    private final List<DataObject> results;

    public QueryResult(List<DataObject> results) {
        this.results = results;
    }

    public Optional<DataObject> first() {
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<DataObject> all() {
        return results;
    }

    public boolean isEmpty() {
        return results.isEmpty();
    }

    public int count() {
        return results.size();
    }

    public <T> List<T> mapTo(Class<T> targetClass) {
        List<T> mappedList = new ArrayList<>();
        for (DataObject data : results) {
            try {
                T instance = mapRow(data, targetClass);
                mappedList.add(instance);
            } catch (Exception e) {
                LOGGER.severe("Failed to map data to class " + targetClass.getSimpleName() + ": " + e.getMessage());
                return new ArrayList<>();
            }
        }
        return mappedList;
    }

    public <T> Optional<T> mapFirstTo(Class<T> targetClass) {
        return first().map(dataObject -> {
            try {
                return mapRow(dataObject, targetClass);
            } catch (Exception e) {
                LOGGER.severe("Failed to map first data object to class " + targetClass.getSimpleName() + ": " + e.getMessage());
                return null;
            }
        });
    }

    private <T> T mapRow(DataObject data, Class<T> targetClass) throws ReflectiveOperationException {
        T instance = targetClass.getDeclaredConstructor().newInstance();
        for (Field field : targetClass.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            if (data.containsKey(fieldName)) {
                Object value = data.get(fieldName);
                Object castedValue = castValue(value, field.getType());
                field.set(instance, castedValue);
            }
        }
        return instance;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Object castValue(Object value, Class<?> targetType) {
        if (value == null) return null;
        if (targetType.isInstance(value)) return value;

        if (targetType == UUID.class && value instanceof String) {
            try {
                return UUID.fromString((String) value);
            } catch (IllegalArgumentException e) {
                LOGGER.warning("Could not map string '" + value + "' to UUID: " + e.getMessage());
                return null;
            }
        }

        if (targetType.isEnum() && value instanceof String) {
            try {
                return Enum.valueOf((Class<Enum>) targetType, (String) value);
            } catch (IllegalArgumentException e) {
                LOGGER.warning("Could not map string '" + value + "' to enum " + targetType.getSimpleName());
                return null;
            }
        }

        if (value instanceof Number) {
            Number number = (Number) value;
            if (targetType == Integer.class || targetType == int.class) return number.intValue();
            if (targetType == Long.class || targetType == long.class) return number.longValue();
            if (targetType == Double.class || targetType == double.class) return number.doubleValue();
            if (targetType == Float.class || targetType == float.class) return number.floatValue();
            if (targetType == Short.class || targetType == short.class) return number.shortValue();
            if (targetType == Boolean.class || targetType == boolean.class) return number.intValue() != 0;
        }

        if (value instanceof Timestamp) {
            if (targetType == Long.class || targetType == long.class) {
                return ((Timestamp) value).getTime();
            }
        }

        return value;
    }
}