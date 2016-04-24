package org.sitoolkit.util.tabledata;

import java.io.File;

import org.sitoolkit.util.tabledata.FileOverwriteChecker;

public class VoidFileOverwriteChecker extends FileOverwriteChecker {

    @Override
    public boolean isWritable(File arg0) {
        return true;
    }
}
