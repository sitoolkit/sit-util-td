package org.sitoolkit.util.tabledata.csv;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.sitoolkit.util.tabledata.TableData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsvWriter {
    private static final Logger LOG = LoggerFactory.getLogger(CsvWriter.class);

    public void writeRow(File targetFile, TableData tableData){

        try {
            FileUtils.writeLines(targetFile, "MS932", tableData.getRows());
        } catch (IOException e) {
            LOG.warn("CSVファイルの書き込みに失敗しました。", e);
        }
    }

}
