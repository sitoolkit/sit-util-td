package org.sitoolkit.util.tabledata.csv;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.sitoolkit.util.tabledata.RowData;
import org.sitoolkit.util.tabledata.TableData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsvWriter {
    private static final Logger LOG = LoggerFactory.getLogger(CsvWriter.class);

    public void write(File targetFile, TableData tableData){

        try {
            FileUtils.writeLines(targetFile, "MS932", remakeTD(tableData));
        } catch (IOException e) {
            LOG.warn("CSVファイルの書き込みに失敗しました。");
            throw new IllegalStateException(e);
        }
    }

    public Collection<RowData> remakeTD(TableData td){
        List<RowData> rows = (List<RowData>) td.getRows();

    	List<RowData> remakedTD = new ArrayList<RowData>();

        RowData schemaRow = new RowData();

        // schemaをデータレコードに追加する。
        for(RowData row : rows){
        	for(String newKey : row.getData().keySet()){
                schemaRow.setCellValue(newKey, newKey);
        	}
        }
		remakedTD.add(schemaRow);

        // メソッドの引数のTableDataからデータを抽出し、新しいスキーマと一致する場合データをセットする。
		// 新しいスキーマと一致しない場合は空文字をセットする。
        for(RowData row : rows){
        	RowData remakedTDRow = new RowData();
        	for(String schemaCol : schemaRow.getData().values()){
    			remakedTDRow.setCellValue(schemaCol, StringUtils.defaultString(row.getCellValue(schemaCol)));
        	}
        	remakedTD.add(remakedTDRow);
        }

    	return remakedTD;
    }
}
