package io.sitoolkit.util.tabledata;

import java.io.File;

import io.sitoolkit.util.tabledata.FileOverwriteChecker;

public class VoidFileOverwriteChecker extends FileOverwriteChecker {

    @Override
    public boolean isWritable(File arg0) {
        return true;
    }
}
