package org.sitoolkit.util.tabledata.csv;

import java.io.File;

import org.junit.Test;
import org.sitoolkit.util.tabledata.FileInputSourceWatcher;
import org.sitoolkit.util.tabledata.FileOverwriteChecker;
import org.sitoolkit.util.tabledata.RowData;
import org.sitoolkit.util.tabledata.TableData;

public class CsvWriterTest {

    @Test
    public void test() {
        CsvWriter writer = new CsvWriter();

        TableDataDaoCsvImpl dao = new TableDataDaoCsvImpl();
        dao.setFileOverwriteChecker(new FileOverwriteChecker());
        dao.setInputSourceWatcher(new FileInputSourceWatcher());
        TableData td = dao.read(new File("csvTest.csv"));

        // 書き込むデータをTableDataにaddする
        RowData rowData = new RowData();
        rowData.setCellValue("列１", "追加,データ");
        rowData.setCellValue("列２", "\"\"");
        rowData.setCellValue("列３", "真\"中");
        rowData.setCellValue("列４", "144");
        rowData.setCellValue("列５", "145");

        td.add(rowData);

        writer.writeRow(new File("csvTest2.csv"), td);
    }

}
