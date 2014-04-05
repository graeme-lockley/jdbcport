package za.co.no9.db;

import org.apache.commons.lang3.StringUtils;

import java.awt.dnd.DragGestureEvent;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TableMetaData {
    private TableName tableName;
    private FieldMetaData[] fieldsMetaData;

    public TableMetaData(TableName tableName, za.co.no9.db.FieldMetaData[] fieldsMetaData) {
        this.tableName = tableName;
        this.fieldsMetaData = fieldsMetaData;
    }

    static TableMetaData from(Connection connection, TableName tableName) throws SQLException {
        Set<String> primaryKey = primaryKey(connection, tableName);

        return new TableMetaData(tableName, fields(connection, tableName, primaryKey));
    }

    static Set<String> primaryKey(Connection connection, TableName tableName) throws SQLException {
        DatabaseMetaData dbm = connection.getMetaData();

        Set<String> primaryKey = new HashSet<>();
        try (ResultSet resultSet = dbm.getPrimaryKeys("", "", tableName.dbName())) {
            while (resultSet.next()) {
                primaryKey.add(resultSet.getString(4));
            }
        }

        return primaryKey;
    }

    static FieldMetaData[] fields(Connection connection, TableName tableName, Set<String> primaryKeys) throws SQLException {
        DatabaseMetaData dbm = connection.getMetaData();

        List<FieldMetaData> fields = new ArrayList<>();
        try (ResultSet resultSet = dbm.getColumns("", "", tableName.dbName(), null)) {
            while (resultSet.next()) {
                String fieldName = resultSet.getString(4);
                boolean isAutoIncrement = StringUtils.equalsIgnoreCase("YES", resultSet.getString(23));

                fields.add(new FieldMetaData(fieldName, primaryKeys.contains(fieldName), isAutoIncrement));
            }
        }

        return fields.toArray(new FieldMetaData[1]);
    }

    public String tableName() {
        return tableName.name();
    }

    public Set<String> primaryKeyFieldNames() {
        Set<String> result = new HashSet<>();

        for(FieldMetaData fieldMetaData: fieldsMetaData) {
            if (fieldMetaData.isPrimaryKey()) {
                result.add(fieldMetaData.name());
            }
        }

        return result;
    }

    public Set<String> autoIncrementFieldNames() {
        Set<String> result = new HashSet<>();

        for(FieldMetaData fieldMetaData: fieldsMetaData) {
            if (fieldMetaData.isAutoIncrement()) {
                result.add(fieldMetaData.name());
            }
        }

        return result;
    }
}
