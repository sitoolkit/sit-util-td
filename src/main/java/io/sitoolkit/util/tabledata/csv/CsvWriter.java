package io.sitoolkit.util.tabledata.csv;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.sitoolkit.util.tabledata.FileIOUtils;
import io.sitoolkit.util.tabledata.RowData;
import io.sitoolkit.util.tabledata.TableData;

public class CsvWriter {
    private static final Logger LOG = LoggerFactory.getLogger(CsvWriter.class);

    public void write(File targetFile, TableData tableData) {

        try {
            FileUtils.writeLines(targetFile, FileIOUtils.getFileEncoding(), mergeSchema(tableData));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    Collection<RowData> mergeSchema(TableData td) {
        List<RowData> rows = td.getRows();

        List<RowData> resultRows = new ArrayList<RowData>();

        RowData schemaRow = new RowData();

        // schemaをデータレコードに追加する。
        for (RowData row : rows) {
            for (String newKey : row.getData().keySet()) {
                schemaRow.setCellValue(newKey, newKey);
            }
        }
        resultRows.add(schemaRow);

        // メソッドの引数のTableDataからデータを抽出し、新しいスキーマと一致する場合データをセットする。
        // 新しいスキーマと一致しない場合は空文字をセットする。
        for (RowData row : rows) {
            RowData resultRow = new RowData();
            for (String schemaCol : schemaRow.getData().values()) {
                resultRow.setCellValue(schemaCol,
                        StringUtils.defaultString(row.getCellValue(schemaCol)));
            }
            resultRows.add(resultRow);
        }

        return resultRows;
    }
}
