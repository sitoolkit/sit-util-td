package org.sitoolkit.util.tabledata.csv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CsvIOUtils {

    private static final Logger LOG = LoggerFactory.getLogger(CsvIOUtils.class);

    private static String fileEncoding = System.getProperty("file.encoding");

    private static String lineSeparator = System.getProperty("line.separator");

    static String escapeReturn(Object obj) {
        if (obj == null) {
            return "";
        }
        return obj.toString().replaceAll("\\r\\n|\\n|\\r", "\\\\n");
    }

    /**
     * 文字列を「,」(半角カンマ)または「\t」(タブ)で分割した文字配列を返します。 分割後の1要素の文字列が「
     * "」(半角ダブルクオーテーション)で開始かつ終了していた場合は、 前後の「"」を除きます。
     *
     * @param line
     *            分割対象の文字列
     * @param columnsNum
     *            分割数
     * @return 「,」(半角カンマ)または「\t」(タブ)で分割した文字配列
     */
    static List<String> splitLine(String line, int columnsNum) {
        String[] values = line.split(",|\t");

        List<String> strList = Arrays.asList(values);
        List<String> newStrList = new ArrayList<String>();
        StringBuilder builder = null;

        for (Iterator<String> itr = strList.iterator(); itr.hasNext();) {

            String startStr = itr.next();

            if (startStr.startsWith("\"") && !startStr.endsWith("\"")) {

                builder = new StringBuilder();
                builder.append(startStr);

                String nextStr = null;

                do {
                    nextStr = itr.next();
                    // splitで除外された「,」をappend
                    builder.append(",");
                    builder.append(nextStr);

                } while (!nextStr.endsWith("\"") && itr.hasNext());

                newStrList.add(modifyStr(builder.toString()));

            } else {
                newStrList.add(modifyStr(startStr));
            }
        }
        // 最終列から見て最初にデータが入力されているセルまでの空白のセルが「newStrList」にaddされていないため補完する。
        if (newStrList.size() < columnsNum) {
            do {
                newStrList.add("");
            } while (newStrList.size() != columnsNum);
        }

        return newStrList;
    }

    private static String modifyStr(String inStr) {
        String retStr = inStr;

        // 文字列が「"」(半角ダブルクオーテーション)で開始かつ終了していた場合は、 前後の「"」を除く。
        if (retStr != null && retStr.startsWith("\"") && retStr.endsWith("\"")) {
            retStr = retStr.substring(1, retStr.length() - 1);
        }
        // 文字列内の「""」を「"」に置換する。
        if (retStr != null) {
            retStr = retStr.replace("\"\"", "\"");
        }

        return retStr;
    }

    public static String getFileEncoding() {
        return fileEncoding;
    }

    public static void setFileEncoding(String fileEncoding) {
        CsvIOUtils.fileEncoding = fileEncoding;
    }

    public static String getLineSeparator() {
        return lineSeparator;
    }

    public static void setLineSeparator(String lineSeparator) {
        CsvIOUtils.lineSeparator = lineSeparator;
    }

}
