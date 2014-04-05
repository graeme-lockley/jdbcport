package za.co.no9.db;

import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class Row implements Map<String, Object> {
    private final Table table;
    private final Object[] primaryKey;
    private final Map<String, Object> rowContent;

    private Row(Table table, Object[] primaryKey, Map<String, Object> rowContent) {
        this.table = table;
        this.primaryKey = primaryKey;
        this.rowContent = rowContent;
    }

    public static Row from(Table table, Object[] primaryKey, Map<String, Object> rowContent) {
        return new Row(table, primaryKey, rowContent);
    }

    @Override
    public int size() {
        return rowContent.size();
    }

    @Override
    public boolean isEmpty() {
        return rowContent.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return rowContent.containsKey(StringUtils.upperCase(String.valueOf(key)));
    }

    @Override
    public boolean containsValue(Object value) {
        return rowContent.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return rowContent.get(StringUtils.upperCase(String.valueOf(key)));
    }

    @Override
    public Object put(String key, Object value) {
        return rowContent.put(StringUtils.upperCase(key), value);
    }

    @Override
    public Object remove(Object key) {
        return rowContent.remove(StringUtils.upperCase(String.valueOf(key)));
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        rowContent.putAll(m);
    }

    @Override
    public void clear() {
        rowContent.clear();
    }

    @Override
    public Set<String> keySet() {
        return rowContent.keySet();
    }

    @Override
    public Collection<Object> values() {
        return rowContent.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return rowContent.entrySet();
    }

    public void store() throws SQLException {
        table.store(primaryKey, rowContent);
    }

    public void delete() throws SQLException {
        table.delete(primaryKey);
    }
}
