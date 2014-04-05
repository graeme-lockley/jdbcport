package za.co.no9.db;

public class FieldMetaData {
    final private String name;
    final private boolean isPrimaryKey;
    final private boolean isAutoIncrement;

    public FieldMetaData(String name, boolean isPrimaryKey, boolean isAutoIncrement) {
        this.name = name;
        this.isPrimaryKey = isPrimaryKey;
        this.isAutoIncrement = isAutoIncrement;
    }

    public String name() {
        return name;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public boolean isAutoIncrement() {
        return isAutoIncrement;
    }
}
