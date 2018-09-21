package io.sitoolkit.util.tabledata.csv;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Test;

import io.sitoolkit.util.tabledata.FileIOUtils;
import io.sitoolkit.util.tabledata.FileInputSourceWatcher;
import io.sitoolkit.util.tabledata.RowData;
import io.sitoolkit.util.tabledata.TableData;
import io.sitoolkit.util.tabledata.VoidFileOverwriteChecker;
import io.sitoolkit.util.tabledata.csv.TableDataDaoCsvImpl;

public class TableDataDaoCsvImplTest {

    static String fileEncoding = FileIOUtils.getFileEncoding();
    static String lineSeparator = FileIOUtils.getLineSeparator();

    @AfterClass
    public static void afterClass() {
        FileIOUtils.setFileEncoding(fileEncoding);
        FileIOUtils.setLineSeparator(lineSeparator);
    }

    @Test
    public void testReadWinExcel() {
        TableDataDaoCsvImpl dao = new TableDataDaoCsvImpl();
        dao.setFileOverwriteChecker(new VoidFileOverwriteChecker());
        dao.setInputSourceWatcher(new FileInputSourceWatcher());

        FileIOUtils.setFileEncoding("MS932");
        FileIOUtils.setLineSeparator("\r\n");

        testRead(dao, "testdata/csv/CsvReadTestInputWinExcel.csv");
    }

    @Test
    public void testReadMacLibre() {
        TableDataDaoCsvImpl dao = new TableDataDaoCsvImpl();
        dao.setFileOverwriteChecker(new VoidFileOverwriteChecker());
        dao.setInputSourceWatcher(new FileInputSourceWatcher());

        FileIOUtils.setFileEncoding("UTF-8");
        FileIOUtils.setLineSeparator("\n");

        testRead(dao, "testdata/csv/CsvReadTestInputMacLibre.csv");
    }

    private void testRead(TableDataDaoCsvImpl dao, String path) {
        TableData td = dao.read(path);

        assertThat(td.getRowCount(), is(33));

        Iterator<RowData> row = td.getRows().iterator();

        // このassertRowDataの文字列は、Excel、LibreOfficeで以下の数式で作ると簡単
        // =SUBSTITUTE(SUBSTITUTE(C2,"""","\"""),CHAR(10),"\n")
        // ="assertRowData(row.next(), """&E2&""", """&F2&""","""&G2&""");"

        assertRowData(row.next(), "通常", "1012", "1013");
        assertRowData(row.next(), "先頭列\n改行", "2012", "2013");
        assertRowData(row.next(), "途中列改行", "20\n22", "2023");
        assertRowData(row.next(), "最終列改行", "2032", "20\n34");
        assertRowData(row.next(), "\n先頭列\n複数\n改行\n", "2042", "2043");
        assertRowData(row.next(), "途中列複数改行", "\n20\n52\n", "2053");
        assertRowData(row.next(), "最終列複数改行", "2062", "\n20\n63\n");
        assertRowData(row.next(), "", "先頭列空白", "3013");
        assertRowData(row.next(), "途中列空白", "", "3023");
        assertRowData(row.next(), "最終列空白", "3032", "");
        assertRowData(row.next(), "改行空白混在", "40\n12", "");
        assertRowData(row.next(), "空白改行混在", "", "40\n23");
        assertRowData(row.next(), "先頭列,カンマ", "5012", "5013");
        assertRowData(row.next(), "途中列カンマ", "50,23", "5033");
        assertRowData(row.next(), "最終列カンマ", "5032", "50,33");
        assertRowData(row.next(), ",先頭列,複数カンマ,", "5012", "5013");
        assertRowData(row.next(), "途中列複数カンマ", ",50,23,", "5033");
        assertRowData(row.next(), "最終列複数カンマ", "5032", ",50,33,");
        assertRowData(row.next(), "先頭列\"ダブルクオート", "6012", "6013");
        assertRowData(row.next(), "途中列ダブルクオート", "60\"22", "6023");
        assertRowData(row.next(), "最終列ダブルクオート", "6032", "60”33");
        assertRowData(row.next(), "\"先頭列複数\"ダブルクオート\"", "6042", "6043");
        assertRowData(row.next(), "途中列複数ダブルクオート", "\"60\"52\"", "6053");
        assertRowData(row.next(), "最終列複数ダブルクオート", "6062", "\"60\"63\"");
        assertRowData(row.next(), "\"\n先頭列ダブルクオート改行混在", "7012", "7013");
        assertRowData(row.next(), "先頭列ダブルクオート\"\n改行混在", "7022", "7023");
        assertRowData(row.next(), "先頭列ダブルクオート改行混在\"\n", "7032", "7033");
        assertRowData(row.next(), "途中列ダブルクオート改行混在１", "\"\n7042", "7043");
        assertRowData(row.next(), "途中列ダブルクオート改行混在２", "70\"\n52", "7053");
        assertRowData(row.next(), "途中列ダブルクオート改行混在３", "7062\"\n", "7063");
        assertRowData(row.next(), "最終列ダブルクオート改行混在１", "7072", "\"\n7073");
        assertRowData(row.next(), "最終列ダブルクオート改行混在２", "7082", "70\"\n83");
        assertRowData(row.next(), "最終列ダブルクオート改行混在３", "7092", "7093\"\n");

    }

    private void assertRowData(RowData row, String col1, String col2, String col3) {
        String reason = row.getCellValue("列１");
        assertThat(reason, is(col1));
        assertThat(reason, row.getCellValue("列２"), is(col2));
        assertThat(reason, row.getCellValue("列３"), is(col3));
    }

    @Test
    public void testWrite() throws IOException {
        TableDataDaoCsvImpl dao = new TableDataDaoCsvImpl();
        dao.setFileOverwriteChecker(new VoidFileOverwriteChecker());
        dao.setInputSourceWatcher(new FileInputSourceWatcher());
        TableData td = new TableData();

        td.add(buildRow("正常データ", "12", "13", "14", "15"));
        td.add(buildRow("途中列改行データ", "22", "2\n3", "24", "25"));
        td.add(buildRow("最終列改行データ", "32", "33", "34", "3\n5"));
        td.add(buildRow("最終列空白データ", "42", "43", "", ""));
        td.add(buildRow("", "先頭列最終列空白データ", "53", "54", ""));
        td.add(buildRow("先頭列\n改行データ", "62", "63", "64", "65"));
        td.add(buildRow("真ん中列改行データ、最終列空白データ", "72", "7\n3", "74", ""));
        td.add(buildRow("先頭列\n複数改行\nデータ", "82", "83", "84", "85"));
        td.add(buildRow("先頭列,カンマ,あり,データ", "92", "93", "94", "95"));
        td.add(buildRow("先頭列,カンマ,あり,データ", "92", "", "", ""));
        td.add(buildRow("ダブルクォートあり", "最後だけ\"", "\"真\"ん中\"", "\"最初だけ", "真中\"だけ"));
        td.add(buildRow("ダブルクォートだけ", "\"", "\"\"", "\"\"\"", "\"\"\"\""));

        File actualFile = new File("target/CsvWriteTestActual.csv");

        FileIOUtils.setFileEncoding("MS932");
        FileIOUtils.setLineSeparator("\r\n");

        dao.write(td, actualFile);

        List<String> actualAllLines = FileUtils.readLines(actualFile,
                FileIOUtils.getFileEncoding());
        Iterator<String> actualItr = actualAllLines.iterator();

        File expectedFile = new File("testdata/csv/CsvWriteTestExpected.csv");
        List<String> expectedAllLines = FileUtils.readLines(expectedFile,
                FileIOUtils.getFileEncoding());

        assertThat(actualAllLines.size(), is(expectedAllLines.size()));

        for (Iterator<String> expectedItr = expectedAllLines.iterator(); expectedItr.hasNext();) {
            assertThat(actualItr.next(), is(expectedItr.next()));
        }

    }

    private RowData buildRow(String col1, String col2, String col3, String col4, String col5) {
        RowData row = new RowData();

        row.setCellValue("列１", col1);
        row.setCellValue("列２", col2);
        row.setCellValue("列３", col3);
        row.setCellValue("列４", col4);
        row.setCellValue("列５", col5);

        return row;
    }
}
