package org.sitoolkit.util.tabledata.csv;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sitoolkit.util.tabledata.FileIOUtils;
import org.sitoolkit.util.tabledata.FileInputSourceWatcher;
import org.sitoolkit.util.tabledata.RowData;
import org.sitoolkit.util.tabledata.TableData;
import org.sitoolkit.util.tabledata.VoidFileOverwriteChecker;

public class TableDataDaoCsvImplTest {

    static String fileEncoding = FileIOUtils.getFileEncoding();
    static String lineSeparator = FileIOUtils.getLineSeparator();

    @BeforeClass
    public static void beforeClass() {
        FileIOUtils.setFileEncoding("MS932");
        FileIOUtils.setLineSeparator("\r\n");
    }

    @AfterClass
    public static void afterClass() {
        FileIOUtils.setFileEncoding(fileEncoding);
        FileIOUtils.setLineSeparator(lineSeparator);
    }

    @Test
    public void testRead() {
        TableDataDaoCsvImpl dao = new TableDataDaoCsvImpl();
        dao.setFileOverwriteChecker(new VoidFileOverwriteChecker());
        dao.setInputSourceWatcher(new FileInputSourceWatcher());
        TableData td = dao.read("testdata/csv/CsvReadTestInput.csv");
        List<RowData> rows = td.getRows();

        Iterator<RowData> row = rows.iterator();

        assertRowData(row.next(), "正常データ", "12", "13", "14", "15");
        assertRowData(row.next(), "途中列改行データ", "22", "2\n3", "24", "25");
        assertRowData(row.next(), "最終列改行データ", "32", "33", "34", "3\n5");
        assertRowData(row.next(), "最終列空白データ", "42", "43", "", "");
        assertRowData(row.next(), "", "先頭列最終列空白データ", "53", "54", "");
        assertRowData(row.next(), "先頭列\n改行データ", "62", "63", "64", "65");
        assertRowData(row.next(), "真ん中列改行データ、最終列空白データ", "72", "7\n3", "74", "");
        assertRowData(row.next(), "先頭列\n複数改行\nデータ", "82", "83", "84", "85");
        assertRowData(row.next(), "先頭列,カンマ,あり,データ", "92", "93", "94", "95");
        assertRowData(row.next(), "先頭列,カンマ,あり,データ", "92", "", "", "");
        assertRowData(row.next(), "ダブルクォートあり", "最後だけ\"", "\"真\"ん中\"", "\"最初だけ", "真中\"だけ");
        assertRowData(row.next(), "ダブルクォートだけ", "\"", "\"\"", "\"\"\"", "\"\"\"\"");

    }

    private void assertRowData(RowData row, String col1, String col2, String col3, String col4,
            String col5) {
        String reason = row.getCellValue("列１");
        assertThat(reason, is(col1));
        assertThat(reason, row.getCellValue("列２"), is(col2));
        assertThat(reason, row.getCellValue("列３"), is(col3));
        assertThat(reason, row.getCellValue("列４"), is(col4));
        assertThat(reason, row.getCellValue("列５"), is(col5));
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
