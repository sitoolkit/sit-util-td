package io.sitoolkit.util.tabledata.excel;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.sitoolkit.util.tabledata.FileIOUtils;
import io.sitoolkit.util.tabledata.MessageManager;
import io.sitoolkit.util.tabledata.RowData;
import io.sitoolkit.util.tabledata.TableData;
import io.sitoolkit.util.tabledata.TableDataCatalog;

public class ExcelWriter {

    private static final Logger LOG = LoggerFactory.getLogger(ExcelWriter.class);

    public Workbook writeWorkbook(Workbook workbook, TableDataCatalog catalog) {
        for (TableData tableData : catalog.tables()) {
            Sheet sheet = workbook.getSheet(tableData.getName());
            if (sheet == null) {
                sheet = workbook.createSheet(tableData.getName());
            }
            writeSheet(sheet, tableData);
        }
        return workbook;
    }

    private void writeSheet(Sheet sheet, TableData tableData) {
        LOG.debug(MessageManager.getMessage("sheet.writing"), sheet.getSheetName());
        Row headerRow = ExcelIOUtils.findHeaderRow(sheet);

        if (tableData.hasColumnNameList()) {
            List<String> columnNameList = tableData.getColumnNameList();
            writeHeaderRow(columnNameList, headerRow);
        }

        Map<String, Integer> schema = ExcelIOUtils.retriveSchema(headerRow);
        Row row = headerRow;
        for (RowData rowData : tableData.getRows()) {
            row = getNextRow(sheet, row);
            writeRow(rowData, schema, row);
        }

    }


    private void writeRow(RowData rowData, Map<String, Integer> schema, Row row) {
        LOG.debug(MessageManager.getMessage("row.writing"), row.getRowNum(),
                FileIOUtils.escapeReturn(rowData));
        for (String columnName : schema.keySet()) {
            writeCellValue(rowData.getCellValue(columnName), schema, columnName, row);
        }
    }

    /**
     * コピー元行の全セルのスタイルを、コピー先行の同じ列番号のセルにコピーする。
     *
     * @param src
     *            コピー元行
     * @param dst
     *            コピー先行
     */
    private void copyStyle(Row src, Row dst) {
        for (int i = src.getFirstCellNum(); i < src.getLastCellNum(); i++) {
            Cell srcCell = src.getCell(i);
            Cell dstCell = dst.getCell(i, Row.CREATE_NULL_AS_BLANK);
            dstCell.setCellStyle(srcCell.getCellStyle());
        }
    }

    private Row getNextRow(Sheet sheet, Row lastRow) {
        if (lastRow == null) {
            return sheet.getRow(sheet.getLastRowNum() + 1);
        } else {
            int newRowNum = lastRow.getRowNum() + 1;
            Row newRow = sheet.getRow(newRowNum);
            if (newRow == null) {
                newRow = sheet.createRow(newRowNum);
                copyStyle(lastRow, newRow);
            }
            return newRow;
        }
    }

    /**
     * 処理行の列名に対応するセルの値を設定する。
     *
     * @param column
     * @param value
     * @return
     */
    private void writeCellValue(Object value, Map<String, Integer> schema, String column, Row row) {
        if (column == null || value == null) {
            return;
        }
        Cell cell = row.getCell(schema.get(column), Row.CREATE_NULL_AS_BLANK);
        if (NumberUtils.isNumber(value.toString())) {
            cell.setCellValue(Double.parseDouble(value.toString()));
        } else {
            if (value.toString().contains("\n")) {
                cell.getCellStyle().setWrapText(true);
            }
            cell.setCellValue(value.toString());
        }
    }

    private void writeHeaderRow(List<String> columnNameList, Row headerRow) {
        if (columnNameList == null) {
            return;
        }

        IntStream.range(0, columnNameList.size()).forEach(i -> {
            Cell cell = headerRow.getCell(i, Row.CREATE_NULL_AS_BLANK);
            String value = columnNameList.get(i);
            if (value.contains("\n")) {
                cell.getCellStyle().setWrapText(true);
            }
            cell.setCellValue(value);
        });
    }
}
