package org.sitoolkit.util.tabledata.excel;

import java.io.File;

import org.junit.Test;
import org.sitoolkit.util.tabledata.FileInputSourceWatcher;
import org.sitoolkit.util.tabledata.FileOverwriteChecker;
import org.sitoolkit.util.tabledata.RowData;
import org.sitoolkit.util.tabledata.TableData;

public class TableDataDaoExcelImplTest {

    @Test
    public void test() {

        TableDataDaoExcelImpl dao = new TableDataDaoExcelImpl();
        dao.setFileOverwriteChecker(new FileOverwriteChecker());
        dao.setInputSourceWatcher(new FileInputSourceWatcher());
        String filePath = "ExcelTestScript.xlsx";
        TableData data = dao.read(filePath, "TestScript");
        File file = new File(filePath);

        // 書き込むデータをTableDataにaddする
        RowData rowData = new RowData();
        rowData.setCellValue("No.", "19");
        rowData.setCellValue("項目名", "追加項目");
        rowData.setCellValue("操作", "click");
        rowData.setCellValue("スクリーンショット", "後");
        rowData.setCellValue("ケース_001", "y");

        data.add(rowData);

        dao.write(data, "TestScriptTemplate.xlsx", file.getAbsolutePath(), null);

    }

}
