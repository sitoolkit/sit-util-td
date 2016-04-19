package org.sitoolkit.util.tabledata.csv;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.sitoolkit.util.tabledata.FileInputSourceWatcher;
import org.sitoolkit.util.tabledata.FileOverwriteChecker;
import org.sitoolkit.util.tabledata.RowData;
import org.sitoolkit.util.tabledata.TableData;

public class TableDataDaoCsvImplTest {

    @Test
    public void test() {
        TableDataDaoCsvImpl dao = new TableDataDaoCsvImpl();
        dao.setFileOverwriteChecker(new FileOverwriteChecker());
        dao.setInputSourceWatcher(new FileInputSourceWatcher());
        TableData td = dao.read(new File("csvTest.csv"));
        List<RowData> rows = (List<RowData>) td.getRows();

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

    public void assertRowData(RowData row, String col1, String col2,
            String col3, String col4, String col5){
        String reason = row.getCellValue("列１");
        assertThat(reason, is(col1));
        assertThat(reason, row.getCellValue("列２"), is(col2));
        assertThat(reason, row.getCellValue("列３"), is(col3));
        assertThat(reason, row.getCellValue("列４"), is(col4));
        assertThat(reason, row.getCellValue("列５"), is(col5));
    }

}
