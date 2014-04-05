package za.co.no9.db;

import org.apache.commons.lang3.StringUtils;

public class TableName {
    private String tableName;

    private TableName(String tableName) {
        this.tableName = tableName;
    }

    public String dbName() {
        return StringUtils.upperCase(tableName);
    }

    public static TableName from(String name) {
        return new TableName(name);
    }

    public String name() {
        return tableName;
    }
}
