package org.sitoolkit.util.tabledata.csv;

import java.io.File;

import org.sitoolkit.util.tabledata.FileOverwriteChecker;
import org.sitoolkit.util.tabledata.InputSourceWatcher;
import org.sitoolkit.util.tabledata.TableData;
import org.sitoolkit.util.tabledata.TableDataDao;

public class TableDataDaoCsvImpl implements TableDataDao {

    private CsvReader reader = new CsvReader();

    private FileOverwriteChecker fileOverwriteChecker;

    private InputSourceWatcher inputSourceWatcher;

    @Override
    public TableData read(String path, String name) {
		return null;
    }

	@Override
    public TableData read(File file) {
    	TableData tableData = reader.readCsv(file);
    	tableData.setInputSource(file.getAbsolutePath());
        inputSourceWatcher.watch(file.getAbsolutePath());

        return tableData;
    }

    @Override
    public void write(TableData data, String templatePath, String targetPath, String name) {
    }

    public FileOverwriteChecker getFileOverwriteChecker() {
        return fileOverwriteChecker;
    }

    public void setFileOverwriteChecker(FileOverwriteChecker fileOverwriteChecker) {
        this.fileOverwriteChecker = fileOverwriteChecker;
    }

    public InputSourceWatcher getInputSourceWatcher() {
        return inputSourceWatcher;
    }

    public void setInputSourceWatcher(InputSourceWatcher inputSourceWatcher) {
        this.inputSourceWatcher = inputSourceWatcher;
    }


}
