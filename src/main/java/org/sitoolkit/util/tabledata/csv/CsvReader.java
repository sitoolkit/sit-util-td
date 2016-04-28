package org.sitoolkit.util.tabledata.csv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.sitoolkit.util.tabledata.FileIOUtils;
import org.sitoolkit.util.tabledata.RowData;
import org.sitoolkit.util.tabledata.TableData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsvReader {

    private static final Logger LOG = LoggerFactory.getLogger(CsvReader.class);

    public TableData readCsv(String file) {

        List<String> allLines = new ArrayList<String>();
        try {
            // csvファイルの読み込み
            String text = IOUtils.toString(FileIOUtils.getInputStream(file),
                    FileIOUtils.getFileEncoding());

            // 改行コードでsplitしリストに格納
            for (String line : FileIOUtils.splitToLines(text)) {
                allLines.add(line);
            }

        } catch (IOException e) {
            LOG.warn("CSVファイルの読み込みに失敗しました。");
            throw new IllegalStateException(e);
        }

        TableData tableData = new TableData();

        // データヘッダをリストから除外し、schemaに設定
        Map<String, Integer> schema = retriveSchema(allLines.remove(0));
        LOG.debug("ヘッダー行{}", schema);

        for (int i = 0; i < allLines.size(); i++) {
            RowData row = readRow(schema, allLines.get(i));
            tableData.add(row);

            LOG.debug("{}行目を読み込みました {}", i + 1, FileIOUtils.escapeReturn(row));
        }

        LOG.info("CSVファイルのデータを{}行読み込みました。", tableData.getRowCount());

        return tableData;
    }

    Map<String, Integer> retriveSchema(String headRowData) {
        Map<String, Integer> schema = new LinkedHashMap<String, Integer>();
        List<String> cells = FileIOUtils.splitToCells(headRowData, 0);
        int colNum = 0;
        for (String cell : cells) {
            colNum++;
            schema.put(cell, colNum);
        }
        return schema;
    }

    /**
     *
     * @param schema
     *            スキーマ
     * @param line
     *            行データ文字列
     * @param rownum
     *            行番号
     * @return 行オブジェクトの情報を読み込んだRowDataオブジェクト
     */
    private RowData readRow(Map<String, Integer> schema, String line) {
        RowData rowData = new RowData();
        List<String> row = FileIOUtils.splitToCells(line, schema.size());

        int colNum = 0;
        for (Entry<String, Integer> entry : schema.entrySet()) {
            rowData.setCellValue(entry.getKey(), row.get(colNum++));
        }

        return rowData;
    }

}
