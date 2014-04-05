package za.co.no9.db;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database implements Closeable {
    private final Connection connection;

    public Database(Connection connection) {
        this.connection = connection;
    }

    public static Database useConnection(Connection connection) {
        return new Database(connection);
    }

    public Table useTable(String name) throws SQLException {
        TableName tableName = TableName.from(name);

        if (tableExists(tableName)) {
            return Table.use(this, TableMetaData.from(connection, tableName));
        } else {
            throw new SQLException("Table " + tableName + " does not exist");
        }
    }

    private boolean tableExists(TableName tableName) throws SQLException {
        DatabaseMetaData dbm = connection.getMetaData();
        try (ResultSet tables = dbm.getTables(null, null, tableName.dbName(), null)) {
            return tables.next();
        }
    }

    protected Connection getConnection() {
        return connection;
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            // nothing to do here...
        }
    }
}
