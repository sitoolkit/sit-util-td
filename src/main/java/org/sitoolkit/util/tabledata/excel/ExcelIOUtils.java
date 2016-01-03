package org.sitoolkit.util.tabledata.excel;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ExcelIOUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ExcelIOUtils.class);

    /**
     * コーナーセルのパターン(正規表現)
     */
    private static String cornerCellPattern = "項番|No\\.|#";

    static Map<String, Integer> retriveSchema(Row headerRow) {
        Map<String, Integer> schema = new LinkedHashMap<String, Integer>();

        final int lastCellNum = headerRow.getLastCellNum();
        for (int i = 0; i < lastCellNum; i++) {
            schema.put(retriveCellValue(headerRow.getCell(i)), i);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("ヘッダー行{}", schema.toString().replace("\n", ""));
        }
        return schema;
    }

    /**
     * セルの値をStringとして取得します。
     * 
     * @param cell
     *            値を取得するセル
     * @return セルの値 セルがnullまたは値の取得に失敗した場合は空文字を返します。
     */
    static String retriveCellValue(Cell cell) {
        String cellValue = "";
        if (cell == null) {
            return cellValue;
        }
        try {
            switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC:
                cellValue = roundValue(cell.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA:
                cellValue = roundValue(cell.getSheet().getWorkbook().getCreationHelper()
                        .createFormulaEvaluator().evaluate(cell).formatAsString());
                break;
            default:
                cellValue = cell.getStringCellValue();
            }
        } catch (Exception e) {
            LOG.warn("セル({}, {})の値の取得が失敗しました。{}", cell.getRowIndex(), cell.getColumnIndex(),
                    e.getMessage());
        }

        return cellValue;
    }

    static String roundValue(String str) {
        if (StringUtils.isEmpty(str)) {
            return StringUtils.EMPTY;
        } else if (NumberUtils.isNumber(str)) {
            return roundValue(NumberUtils.toDouble(str));
        } else {
            if (str.length() > 1 && str.startsWith("\"") && str.endsWith("\"")) {
                return str.substring(1, str.length() - 1);
            } else {
                return str;
            }
        }
    }

    static String roundValue(double value) {
        if (value == Math.round(value)) {
            return Integer.toString((int) value);
        } else {
            return Double.toString(value);
        }
    }

    /**
     *
     * @param sheet
     * @return
     */
    static Row findHeaderRow(Sheet sheet) {
        LOG.debug("シート[{}]のヘッダー行を特定します。", sheet.getSheetName(), cornerCellPattern);
        Row headerRow = null;
        final int rowCnt = sheet.getPhysicalNumberOfRows();
        for (int i = 0; i < rowCnt; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }

            Cell cell = row.getCell(0);
            if (cell == null) {
                continue;
            }
            String cellValue = ExcelIOUtils.retriveCellValue(cell);
            if (cellValue.matches(cornerCellPattern)) {
                headerRow = row;
                LOG.debug("セル({}, {})に値[{}]が見つかりました。ヘッダー行は{}行目です。", new Object[] {
                        cell.getRowIndex(), cell.getColumnIndex(), cellValue, row.getRowNum() });
                break;
            }
        }
        if (headerRow == null) {
            LOG.warn("シート[{}]には、パターン[{}]に一致するセルが見つかりません。", sheet.getSheetName(), cornerCellPattern);
        }
        return headerRow;
    }

    static String escapeReturn(Object obj) {
        if (obj == null) {
            return "";
        }
        return obj.toString().replaceAll("\\r\\n|\\n|\\r", "\\\\n");
    }

}
