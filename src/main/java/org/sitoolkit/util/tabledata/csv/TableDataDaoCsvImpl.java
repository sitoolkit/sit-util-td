package org.sitoolkit.util.tabledata.csv;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.sitoolkit.util.tabledata.FileOverwriteChecker;
import org.sitoolkit.util.tabledata.InputSourceWatcher;
import org.sitoolkit.util.tabledata.MessageManager;
import org.sitoolkit.util.tabledata.TableData;
import org.sitoolkit.util.tabledata.TableDataDao;
import org.sitoolkit.util.tabledata.excel.TableDataDaoExcelImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TableDataDaoCsvImpl implements TableDataDao {

    private static final Logger LOG = LoggerFactory.getLogger(TableDataDaoExcelImpl.class);

    private CsvReader reader = new CsvReader();

    private CsvWriter writer = new CsvWriter();

    private FileOverwriteChecker fileOverwriteChecker;

    private InputSourceWatcher inputSourceWatcher;

    @Override
    public TableData read(String path, String name) {
        return read(path);
    }

    @Override
    public TableData read(String path) {
        TableData tableData = reader.readCsv(path);
        tableData.setInputSource(path);
        inputSourceWatcher.watch(path);

        return tableData;
    }

    @Override
    public void write(TableData data, String templatePath, String targetPath, String name) {
        write(data, new File(targetPath));
    }

    public void write(TableData data, File targetFile) {
        if (!fileOverwriteChecker.isWritable(targetFile)) {
            return;
        }
        LOG.info(MessageManager.getMessage("csv.writing"), targetFile.getAbsolutePath());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(targetFile);
            writer.write(targetFile, data);
            LOG.debug(MessageManager.getMessage("csv.wrote"), targetFile.getAbsolutePath());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    LOG.warn(MessageManager.getMessage("exception.closingStream"), e);
                }
            }
        }

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
