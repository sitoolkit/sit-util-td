package org.sitoolkit.util.tabledata.csv;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.sitoolkit.util.tabledata.RowData;
import org.sitoolkit.util.tabledata.TableData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsvReader {

    private static final Logger LOG = LoggerFactory.getLogger(CsvReader.class);

    public TableData readCsv(File file) {

        LOG.info("CSVファイルを読み込みます。");

        List<String> allLines = new ArrayList<String>();
        try {
            // csvファイルの読み込み
            String allLine = FileUtils.readFileToString(file, CsvIOUtils.getFileEncoding());

            // 改行コードでsplitしリストに格納
            for (String line : allLine.split(CsvIOUtils.getLineSeparator())) {
                allLines.add(line);
            }

        } catch (IOException e) {
            LOG.warn("CSVファイルの読み込みに失敗しました。");
            throw new IllegalStateException(e);
        }

        TableData tableData = new TableData();

        // データヘッダをリストから除外し、schemaに設定
        Map<String, Integer> schema = retriveSchema(allLines.remove(0));

        for (String line : allLines) {
            tableData.add(readRow(schema, line));
        }

        LOG.info("CSVファイルのデータを{}行読み込みました。", tableData.getRowCount());

        return tableData;
    }

    Map<String, Integer> retriveSchema(String headRowData) {
        Map<String, Integer> schema = new LinkedHashMap<String, Integer>();
        List<String> cells = CsvIOUtils.splitLine(headRowData, 0);
        int i = 0;
        for (String cell : cells) {
            i++;
            schema.put(cell, i);
        }
        return schema;
    }

    /**
     *
     * @param schema
     *            スキーマ
     * @param rows
     *            行データ文字列
     * @param rownum
     *            行番号
     * @return 行オブジェクトの情報を読み込んだRowDataオブジェクト
     */
    private RowData readRow(Map<String, Integer> schema, String rows) {
        RowData rowData = new RowData();
        List<String> row = CsvIOUtils.splitLine(rows, schema.size());

        int i = 0;
        for (Entry<String, Integer> entry : schema.entrySet()) {
            rowData.setCellValue(entry.getKey(), row.get(i));
            i++;
        }

        return rowData;
    }

}
