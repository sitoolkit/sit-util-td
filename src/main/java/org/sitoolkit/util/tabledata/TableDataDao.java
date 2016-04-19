package org.sitoolkit.util.tabledata;

import java.io.File;

public interface TableDataDao {

    TableData read(String path, String name);

    TableData read(File file);

    void write(TableData data, String templatePath, String targetPath, String name);

}
