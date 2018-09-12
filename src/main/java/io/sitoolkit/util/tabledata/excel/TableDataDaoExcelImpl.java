package io.sitoolkit.util.tabledata.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.sitoolkit.util.tabledata.FileOverwriteChecker;
import io.sitoolkit.util.tabledata.InputSourceWatcher;
import io.sitoolkit.util.tabledata.MessageManager;
import io.sitoolkit.util.tabledata.TableData;
import io.sitoolkit.util.tabledata.TableDataCatalog;
import io.sitoolkit.util.tabledata.TableDataDao;

public class TableDataDaoExcelImpl implements TableDataDao {

    private static final Logger LOG = LoggerFactory.getLogger(TableDataDaoExcelImpl.class);

    private ExcelReader reader = new ExcelReader();

    private ExcelWriter writer = new ExcelWriter();

    /**
     * 読み取り対象から除外するシート名
     */
    private Set<String> excludingSheetNames = new HashSet<>();

    private FileOverwriteChecker fileOverwriteChecker;

    private InputSourceWatcher inputSourceWatcher;

    public TableDataCatalog readAll(String path) {
        return read(path, new String[0]);
    }

    @Override
    public TableData read(String path) {
        return read(path, "Sheet1");
    }

    @Override
    public TableData read(String path, String name) {
        TableDataCatalog tableDataCatalog = readAll(path);
        return tableDataCatalog.get(name);
    }

    public TableDataCatalog read(String filePath, String[] sheetNames) {
        Workbook workbook = reader.load(filePath);
        TableDataCatalog catalog = ArrayUtils.isEmpty(sheetNames)
                ? readWorkbookByExceptedSheetNames(workbook)
                : readWorkbookBySheetNames(workbook, sheetNames);

        for (TableData table : catalog.tables()) {
            table.setInputSource(filePath);
        }
        inputSourceWatcher.watch(filePath);

        return catalog;
    }

    private TableDataCatalog readWorkbookByExceptedSheetNames(Workbook workbook) {
        TableDataCatalog catalog = new TableDataCatalog();

        final int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if (excludingSheetNames.contains(sheet.getSheetName())) {
                continue;
            }
            TableData tableData = reader.readSheet(sheet);
            if (tableData == null) {
                LOG.warn(MessageManager.getMessage("sheet.noLoadableTableData"),
                        sheet.getSheetName());
                continue;
            }
            catalog.add(tableData);
        }

        return catalog;
    }

    private TableDataCatalog readWorkbookBySheetNames(Workbook workbook, String[] sheetNames) {
        TableDataCatalog catalog = new TableDataCatalog();

        for (String sheetName : sheetNames) {
            Sheet sheet = workbook.getSheet(sheetName);
            TableData tableData = reader.readSheet(sheet);
            if (tableData == null) {
                LOG.warn(MessageManager.getMessage("sheet.noLoadableTableData"),
                        sheet.getSheetName());
                continue;
            }
            catalog.add(tableData);
        }
        return catalog;
    }

    @Override
    public void write(TableData data, String templatePath, String targetPath, String name) {
        TableDataCatalog catalog = new TableDataCatalog();
        catalog.add(data);
        write(templatePath, new File(targetPath), catalog);
    }

    public void write(String templateFile, File targetFile, TableDataCatalog catalog) {
        if (!fileOverwriteChecker.isWritable(targetFile)) {
            return;
        }
        LOG.info(MessageManager.getMessage("excel.writing"), targetFile.getAbsolutePath());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(targetFile);
            writer.writeWorkbook(reader.load(templateFile), catalog).write(fos);
            LOG.debug(MessageManager.getMessage("excel.wrote"), targetFile.getAbsolutePath());
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
