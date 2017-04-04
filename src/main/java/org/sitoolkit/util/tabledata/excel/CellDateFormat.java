package org.sitoolkit.util.tabledata.excel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;

public enum CellDateFormat {
    NONE(-1, new SimpleDateFormat("yyyy/MM/dd")),
    FORMAT_14(14, new SimpleDateFormat("yyyy/M/d")),
    FORMAT_18(18, new SimpleDateFormat("K:mm aa", Locale.ENGLISH)),
    FORMAT_19(19, new SimpleDateFormat("K:mm:ss aa", Locale.ENGLISH)),
    FORMAT_20(20, new SimpleDateFormat("H:mm")),
    FORMAT_21(21, new SimpleDateFormat("H:mm:ss")),
    FORMAT_22(22, new SimpleDateFormat("yyyy/M/d H:mm")),
    FORMAT_179(179, new SimpleDateFormat("d-MMM", Locale.ENGLISH)),
    FORMAT_180(180, new SimpleDateFormat("d-MMM-yy", Locale.ENGLISH)),
    FORMAT_181(181, new SimpleDateFormat("MMM-yy", Locale.ENGLISH)),
    FORMAT_183(183, new SimpleDateFormat("yyyy年M月d日"));

    private int formatId;

    private DateFormat dateFormat;

    private CellDateFormat(int format, DateFormat dateFormat) {
        this.formatId = format;
        this.dateFormat = dateFormat;
    }

    public int getFormatNo() {
        return this.formatId;
    }

    public DateFormat getDateFormat() {
        return this.dateFormat;
    }

    public static CellDateFormat getFormat(int formatId) {
        for (CellDateFormat elm : CellDateFormat.values()) {
            if (elm.getFormatNo() == formatId) {
                return elm;
            }
        }
        return NONE;
    }

    public static boolean isDateCell(Cell cell) {
        if (DateUtil.isCellDateFormatted(cell)) {
            return true;

        } else {
            int formatId = cell.getCellStyle().getDataFormat();

            for (CellDateFormat elm : CellDateFormat.values()) {
                if (elm.getFormatNo() == formatId) {
                    return true;
                }
            }
            return false;
        }
    }
}
