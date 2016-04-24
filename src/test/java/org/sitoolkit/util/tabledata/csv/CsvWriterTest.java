package org.sitoolkit.util.tabledata.csv;

import java.io.File;

import org.junit.Test;
import org.sitoolkit.util.tabledata.FileInputSourceWatcher;
import org.sitoolkit.util.tabledata.RowData;
import org.sitoolkit.util.tabledata.TableData;
import org.sitoolkit.util.tabledata.VoidFileOverwriteChecker;

public class CsvWriterTest {

    @Test
    public void test() {
        CsvWriter writer = new CsvWriter();

        TableDataDaoCsvImpl dao = new TableDataDaoCsvImpl();
//        dao.setFileOverwriteChecker(new FileOverwriteChecker());
        dao.setVoidFileOverwriteChecker(new VoidFileOverwriteChecker());
        dao.setInputSourceWatcher(new FileInputSourceWatcher());
        TableData td = new TableData();

        RowData rowData = new RowData();
        rowData.setCellValue("列１", "１行目：基本データ１");
        rowData.setCellValue("列２", "\"\"");
        rowData.setCellValue("列３", "真\"中");
        td.add(rowData);

        rowData = new RowData();
        rowData.setCellValue("列４", "列４以外空白");
        td.add(rowData);

        rowData = new RowData();
        rowData.setCellValue("列１", "３行目");
        rowData.setCellValue("列３", "33");
        rowData.setCellValue("列５", "33,35以外空白");
        td.add(rowData);

        writer.write(new File("csvTest2.csv"), td);
    }

}
