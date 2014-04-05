package za.co.no9.db;

import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;
import za.co.no9.util.FreeMarkerUtils;
import za.co.no9.util.MapBuilder;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class Table {
    private final Database database;
    private final TableMetaData tableMetaData;
    private String[] primaryKeyFieldNames;
    private String[] autoIncrementFieldNames;

    private Table(Database database, TableMetaData tableMetaData) {
        this.database = database;
        this.tableMetaData = tableMetaData;
        this.primaryKeyFieldNames = tableMetaData.primaryKeyFieldNames().toArray(new String[1]);
        this.autoIncrementFieldNames = tableMetaData.autoIncrementFieldNames().toArray(new String[1]);
    }

    public static Table use(Database database, TableMetaData tableMetaData) {
        return new Table(database, tableMetaData);
    }

    public Collection<Row> all() throws SQLException {
        return query("select * from " + tableMetaData.tableName());
    }

    public Collection<Row> where(String whereClause) throws SQLException {
        if (StringUtils.isBlank(whereClause)) {
            return all();
        } else {
            return query("select * from " + tableMetaData.tableName() + " where " + whereClause);
        }
    }

    private Collection<Row> query(String query) throws SQLException {
        try (Statement statement = database.getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            String[] columnNames = columnNames(resultSet);

            List<Row> result = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();

                for (int columnIndex = 0; columnIndex < columnNames.length; columnIndex += 1) {
                    row.put(columnNames[columnIndex], resultSet.getObject(columnIndex + 1));
                }
                result.add(Row.from(this, primaryKeyValues(row), row));
            }

            return result;
        }
    }

    private Object[] primaryKeyValues(Map<String, Object> row) {
        Object[] result = new Object[primaryKeyFieldNames.length];

        for (int index = 0; index < primaryKeyFieldNames.length; index += 1) {
            result[index] = row.get(primaryKeyFieldNames[index]);
        }

        return result;
    }

    private String[] columnNames(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        String[] columnNames = new String[metaData.getColumnCount()];

        for (int columnIndex = 0; columnIndex < metaData.getColumnCount(); columnIndex += 1) {
            columnNames[columnIndex] = metaData.getColumnName(columnIndex + 1);
        }

        return columnNames;
    }

    protected void store(Object[] primaryKey, Map<String, Object> rowContent) throws SQLException {
        executeTemplate(
                MapBuilder.<String, Object>create()
                        .add("tableName", tableMetaData.tableName())
                        .add("primaryKey", MapBuilder.<String, Object>create()
                                .add("names", primaryKeyFieldNames)
                                .add("values", primaryKey)
                                .build())
                        .add("fields", rowContent)
                        .build(),
                "za.co.no9.db.Table.UpdateRow.ftl");
    }

    protected void delete(Object[] primaryKey) throws SQLException {
        executeTemplate(
                MapBuilder.<String, Object>create()
                        .add("tableName", tableMetaData.tableName())
                        .add("primaryKey", MapBuilder.<String, Object>create()
                                .add("names", primaryKeyFieldNames)
                                .add("values", primaryKey)
                                .build())
                        .build(),
                "za.co.no9.db.Table.DeleteRow.ftl");
    }

    private void executeTemplate(Map<String, Object> data, String templateName) throws SQLException {
        try {
            String output = FreeMarkerUtils.template(data, templateName);
            try (Statement statement = database.getConnection().createStatement()) {
                statement.execute(output);
            }
        } catch (IOException | TemplateException e) {
            throw new SQLException(e);
        }
    }

    public Row add(Map<String, Object> state) throws SQLException {
        try {
            Map<String, Object> row = toUpperKeys(state);

            String output = FreeMarkerUtils.template(MapBuilder.<String, Object>create()
                    .add("tableName", tableMetaData.tableName())
                    .add("fields", row)
                    .build(), "za.co.no9.db.Table.InsertRow.ftl");
            try (Statement statement = database.getConnection().createStatement()) {
                statement.executeUpdate(output);
                ResultSet keys = statement.getGeneratedKeys();

                int autoIncrementFieldNamesIdx = 0;
                while (keys.next()) {
                    row.put(autoIncrementFieldNames[autoIncrementFieldNamesIdx], keys.getObject(1));
                    autoIncrementFieldNamesIdx += 1;
                }

                return Row.from(this, primaryKeyValues(row), row);
            }
        } catch (IOException | TemplateException e) {
            throw new SQLException(e);
        }
    }

    private Map<String, Object> toUpperKeys(Map<String, Object> mapInput) {
        Map<String, Object> mapResult = new HashMap<>();

        for (String key: mapInput.keySet()) {
            mapResult.put(key.toUpperCase(), mapInput.get(key));
        }

        return mapResult;
    }
}
