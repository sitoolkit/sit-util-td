package org.sitoolkit.util.tabledata;

public interface TableDataDao {

    TableData read(String path);

    TableData read(String path, String name);

    void write(TableData data, String templatePath, String targetPath, String name);

}
