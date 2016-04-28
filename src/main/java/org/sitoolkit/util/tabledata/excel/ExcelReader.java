package org.sitoolkit.util.tabledata.excel;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.sitoolkit.util.tabledata.FileIOUtils;
import org.sitoolkit.util.tabledata.RowData;
import org.sitoolkit.util.tabledata.TableData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelReader {

    private static final Logger LOG = LoggerFactory.getLogger(ExcelReader.class);

    public Workbook load(String path) {
        // LOG.info("Excelファイルを読み込みます。{}", file.getAbsolutePath());
        InputStream fis = null;
        try {
            fis = FileIOUtils.getInputStream(path);
            if (path.endsWith(".xlsx")) {
                return new XSSFWorkbook(fis);
            } else {
                return new HSSFWorkbook(fis);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    LOG.warn("ストリームのクローズで例外が発生しました。", e);
                }
            }
        }
    }

    public TableData readSheet(Sheet sheet) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("シート[{}]を読み込みます。", sheet.getSheetName());
        }
        Row headerRow = ExcelIOUtils.findHeaderRow(sheet);
        if (headerRow == null) {
            return null;
        }
        TableData tableData = new TableData();
        tableData.setName(sheet.getSheetName());

        Map<String, Integer> schema = ExcelIOUtils.retriveSchema(headerRow);
        final int rowCnt = sheet.getLastRowNum();
        for (int i = headerRow.getRowNum() + 1; i <= rowCnt; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                break;
            }
            tableData.add(readRow(schema, row));
        }
        LOG.info("シート[{}]内の表データを{}行読み込みました。", sheet.getSheetName(), tableData.getRowCount());

        return tableData;
    }

    /**
     *
     * @param schema
     *            スキーマ
     * @param row
     *            行オブジェクト
     * @return 行オブジェクトの情報を読み込んだRowDataオブジェクト
     */
    private RowData readRow(Map<String, Integer> schema, Row row) {
        RowData rowData = new RowData();

        for (Entry<String, Integer> entry : schema.entrySet()) {
            Cell cell = row.getCell(entry.getValue());
            if (cell == null) {
                LOG.warn("存在しない列名が指定されました。{}", entry.getValue());
            }
            rowData.setCellValue(entry.getKey(), ExcelIOUtils.retriveCellValue(cell));
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("{}行目を読み込みました。{}", row.getRowNum(), FileIOUtils.escapeReturn(rowData));
        }

        return rowData;
    }

}
