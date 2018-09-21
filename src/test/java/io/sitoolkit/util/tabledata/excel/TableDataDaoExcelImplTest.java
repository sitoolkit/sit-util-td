package io.sitoolkit.util.tabledata.excel;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import io.sitoolkit.util.tabledata.FileInputSourceWatcher;
import io.sitoolkit.util.tabledata.FileOverwriteChecker;
import io.sitoolkit.util.tabledata.RowData;
import io.sitoolkit.util.tabledata.TableData;
import io.sitoolkit.util.tabledata.TableDataCatalog;
import io.sitoolkit.util.tabledata.VoidFileOverwriteChecker;
import io.sitoolkit.util.tabledata.excel.TableDataDaoExcelImpl;


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
        assertCell(rows.next(), "2017/4/4");
        assertCell(rows.next(), "4-Apr-17");
        assertCell(rows.next(), "4-Apr");
        assertCell(rows.next(), "Apr-17");
        assertCell(rows.next(), "4:17 PM");
        assertCell(rows.next(), "4:17:00 PM");
        assertCell(rows.next(), "16:17");
        assertCell(rows.next(), "16:01:14");
        assertCell(rows.next(), "2017/4/4 16:18");
        assertCell(rows.next(), "2017年8月12日");
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


    @Test
    public void testWrite2() throws IOException {
        TableDataDaoExcelImpl dao = new TableDataDaoExcelImpl();
        dao.setFileOverwriteChecker(new VoidFileOverwriteChecker());
        dao.setInputSourceWatcher(new FileInputSourceWatcher());

        TableData td = new TableData();
        TableDataCatalog tdc = new TableDataCatalog();

        List<String> header = Arrays.asList("No.", "列１", "列２", "列３", "列４");
        td.setColumnNameList(header);

        td.add(makeRow(header, "1", "通常データ", "改行なし列２", "改行なし列３"));
        td.add(makeRow(header, "2", "列２,列３空白（null）", null, ""));
        td.add(makeRow(header, "3", "途中改行あり", "セル内\n改行", "セル内\n改行\n複数"));
        td.add(makeRow(header, "4", "ダブルクォートあり", "\"3-2\"", "\"3\"3\""));
        td.add(makeRow(header, "5", "ダブルクォートのみ", "\"", "\"\""));
        td.add(makeRow(header, "6", "カンマあり", "列２,２", "列２,３"));
        td.add(makeRow(header, "7", "カンマのみ", ",", ",,"));
        td.add(makeRow(header, "8", "列追加", null, null, "追加された列のデータ"));

        String sheetName = "TestScript";
        td.setName(sheetName);
        tdc.add(td);

        String actualFilePath = "target/ExcelWriteTestActual2.xlsx";
        File actualFile = new File(actualFilePath);

        dao.write("testdata/excel/ExcelWriteTestTemplate2.xlsx", actualFile, tdc);

        // Excelファイルの比較
        assertCells(sheetName, actualFilePath,"testdata/excel/ExcelWriteTestExpected2.xlsx");

    }

    private RowData makeRow(String col1, String col2, String col3, String col4) {
        RowData row = new RowData();

        row.setCellValue("No.", col1);
        row.setCellValue("列１", col2);
        row.setCellValue("列２", col3);
        row.setCellValue("列３", col4);

        return row;

    }

    private RowData makeRow(List<String> colNames, String... cols) {
        RowData row = new RowData();
        int columnCount = Math.min(colNames.size(), cols.length);

        IntStream.range(0, columnCount).forEach(i -> {
            row.setCellValue(colNames.get(i), cols[i]);
        });
        return row;
    }

    private void assertCells(String sheetName, String actualFilepath) throws IOException {
        assertCells(sheetName, actualFilepath, "testdata/excel/ExcelWriteTestExpected.xlsx");
    }

    private void assertCells(String sheetName, String actualFilepath, String expectedFilepath)
            throws IOException {
        File expectedFile = new File(expectedFilepath);
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
