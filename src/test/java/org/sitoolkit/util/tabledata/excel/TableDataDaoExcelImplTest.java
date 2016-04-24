package org.sitoolkit.util.tabledata.excel;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

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
	public void readTest() {
        TableDataDaoExcelImpl dao = new TableDataDaoExcelImpl();
        dao.setFileOverwriteChecker(new FileOverwriteChecker());
        dao.setInputSourceWatcher(new FileInputSourceWatcher());
        TableData td = dao.read("testdata/ExcelTestScript.xlsx", "TestScript");
        List<RowData> rows = (List<RowData>) td.getRows();
        assertThat(rows.get(0).getCellValue("項目名"), is("開始URL"));
	}

	@Test
	public void writeTest() throws IOException{
        TableDataDaoExcelImpl dao = new TableDataDaoExcelImpl();
        dao.setVoidFileOverwriteChecker(new VoidFileOverwriteChecker());
        dao.setInputSourceWatcher(new FileInputSourceWatcher());

        TableData td = new TableData();
        TableDataCatalog tdc = new TableDataCatalog();

        td.add(makeRow("1","通常データ","改行なし列２","改行なし列３"));
        td.add(makeRow("2","列２,列３空白（null）",null,""));
        td.add(makeRow("3","途中改行あり","セル内\n改行","セル内\n改行\n複数"));
        td.add(makeRow("4","ダブルクォートあり","\"3-2\"","\"3\"3\""));
        td.add(makeRow("5","ダブルクォートのみ","\"","\"\""));
        td.add(makeRow("6","カンマあり","列２,２","列２,３"));
        td.add(makeRow("7","カンマのみ",",",",,"));

        String sheetName = "TestScript";
        td.setName(sheetName);
        tdc.add(td);

        String actualFilePath = "testdata/actualTestScript.xlsx";
        File actualFile = new File(actualFilePath);

        dao.write("testdata/TestScriptTemplate.xlsx", actualFile, tdc);

        // Excelファイルの比較
        assertCells(sheetName, actualFilePath);

	}

    public RowData makeRow(String col1,String col2,String col3,String col4){
    	RowData row = new RowData();

    	row.setCellValue("No.", col1);
    	row.setCellValue("列１", col2);
    	row.setCellValue("列２", col3);
    	row.setCellValue("列３", col4);

    	return row;

	}

    public void assertCells(String sheetName, String actualFilepath) throws IOException{
        File expectedFile = new File("testdata/expectedTestScript.xlsx");
        FileInputStream expectedExcellFile = new FileInputStream(expectedFile);
        FileInputStream actualExcellFile = new FileInputStream(actualFilepath);

        Workbook expectedWorkBook = new XSSFWorkbook(expectedExcellFile);
        Workbook actualWorkBook = new XSSFWorkbook(actualExcellFile);

        Iterator<Row> expectedRowsItr = expectedWorkBook.getSheet(sheetName).iterator();
        Iterator<Row> actuallRowsItr = actualWorkBook.getSheet(sheetName).iterator();

        for(;expectedRowsItr.hasNext();){
            Row expectedRow = expectedRowsItr.next();
            Row actualRow = actuallRowsItr.next();

            Iterator<Cell> expectedCellsItr = expectedRow.iterator();
            Iterator<Cell> actualCellsItr = actualRow.iterator();

            for(;expectedCellsItr.hasNext();){
                assertThat(actualCellsItr.next().toString(), is((expectedCellsItr.next().toString())));
            }

        }

        //close files
        expectedWorkBook.close();
        actualWorkBook.close();
        expectedExcellFile.close();
        actualExcellFile.close();

    }

}
