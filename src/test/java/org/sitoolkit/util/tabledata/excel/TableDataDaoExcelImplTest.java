package org.sitoolkit.util.tabledata.excel;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.sitoolkit.util.tabledata.FileInputSourceWatcher;
import org.sitoolkit.util.tabledata.FileOverwriteChecker;
import org.sitoolkit.util.tabledata.RowData;
import org.sitoolkit.util.tabledata.TableData;
import org.sitoolkit.util.tabledata.TableDataCatalog;
import org.sitoolkit.util.tabledata.VoidFileOverwriteChecker;

public class TableDataDaoExcelImplTest {

    @Test
    public void testRead() {
        TableDataDaoExcelImpl dao = new TableDataDaoExcelImpl();
        dao.setFileOverwriteChecker(new FileOverwriteChecker());
        dao.setInputSourceWatcher(new FileInputSourceWatcher());

        TableData td = dao.read("testdata/excel/ExcelReadTestInput.xlsx");
        Iterator<RowData> rows = td.getRows().iterator();

        assertCell(rows.next(), "abcあいう");
        assertCell(rows.next(), "1");
        assertCell(rows.next(), "1.2");
        assertCell(rows.next(), "2");
    }

    private void assertCell(RowData row, String expected) {
        assertThat(row.getCellValue("列１"), row.getCellValue("列２"), is(expected));
    }

    @Test
    public void testWrite() throws IOException {
        TableDataDaoExcelImpl dao = new TableDataDaoExcelImpl();
        dao.setFileOverwriteChecker(new VoidFileOverwriteChecker());
        dao.setInputSourceWatcher(new FileInputSourceWatcher());

        TableData td = new TableData();
        TableDataCatalog tdc = new TableDataCatalog();

        td.add(makeRow("1", "通常データ", "改行なし列２", "改行なし列３"));
        td.add(makeRow("2", "列２,列３空白（null）", null, ""));
        td.add(makeRow("3", "途中改行あり", "セル内\n改行", "セル内\n改行\n複数"));
        td.add(makeRow("4", "ダブルクォートあり", "\"3-2\"", "\"3\"3\""));
        td.add(makeRow("5", "ダブルクォートのみ", "\"", "\"\""));
        td.add(makeRow("6", "カンマあり", "列２,２", "列２,３"));
        td.add(makeRow("7", "カンマのみ", ",", ",,"));

        String sheetName = "TestScript";
        td.setName(sheetName);
        tdc.add(td);

        String actualFilePath = "target/ExcelWriteTestActual.xlsx";
        File actualFile = new File(actualFilePath);

        dao.write("testdata/excel/ExcelWriteTestTemplate.xlsx", actualFile, tdc);

        // Excelファイルの比較
        assertCells(sheetName, actualFilePath);

    }

    private RowData makeRow(String col1, String col2, String col3, String col4) {
        RowData row = new RowData();

        row.setCellValue("No.", col1);
        row.setCellValue("列１", col2);
        row.setCellValue("列２", col3);
        row.setCellValue("列３", col4);

        return row;

    }

    private void assertCells(String sheetName, String actualFilepath) throws IOException {
        File expectedFile = new File("testdata/excel/ExcelWriteTestExpected.xlsx");
        FileInputStream expectedExcellFile = new FileInputStream(expectedFile);
        FileInputStream actualExcellFile = new FileInputStream(actualFilepath);

        Workbook expectedWorkBook = new XSSFWorkbook(expectedExcellFile);
        Workbook actualWorkBook = new XSSFWorkbook(actualExcellFile);

        Iterator<Row> expectedRowsItr = expectedWorkBook.getSheet(sheetName).iterator();
        Iterator<Row> actuallRowsItr = actualWorkBook.getSheet(sheetName).iterator();

        for (; expectedRowsItr.hasNext();) {
            Row expectedRow = expectedRowsItr.next();
            Row actualRow = actuallRowsItr.next();

            Iterator<Cell> expectedCellsItr = expectedRow.iterator();
            Iterator<Cell> actualCellsItr = actualRow.iterator();

            for (; expectedCellsItr.hasNext();) {
                assertThat(actualCellsItr.next().toString(),
                        is((expectedCellsItr.next().toString())));
            }

        }

        // close files
        expectedWorkBook.close();
        actualWorkBook.close();
        expectedExcellFile.close();
        actualExcellFile.close();

    }

}
