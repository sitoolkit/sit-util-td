package org.sitoolkit.util.tabledata;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileIOUtils {

    private static final Logger LOG = LoggerFactory.getLogger(FileIOUtils.class);

    private static String fileEncoding = System.getProperty("file.encoding");

    private static String lineSeparator = System.getProperty("line.separator");

    private static String cellSeparator = ",";

    /**
     * CSVデータにおけるセル内改行による行分離の開始行であることを判定するパターン 「,"」を含む　または　「"」で始まる
     */
    private static final Pattern LINE_SEPARATE_START = Pattern.compile(".*,\"{1}.*|^\"{1}.*");
    /**
     * CSVデータにおけるセル内改行による行分離の終了行であることを判定するパターン 「",」を含む　または　「"」で終わる または「,」で終わる
     */
    private static final Pattern LINE_SEPARATE_END = Pattern.compile(".*\"{1},.+|.*\"{1}$|.*,$");

    public static String escapeReturn(Object obj) {
        if (obj == null) {
            return "";
        }
        return obj.toString().replaceAll("\\r\\n|\\n|\\r", "\\\\n");
    }

    public static InputStream getInputStream(String path) throws IOException {
        if (path.startsWith("http://") || path.startsWith("https://")) {
            URL url = new URL(path);
            LOG.info("ファイルを読み込みます {}", url);
            return url.openStream();

        } else if (path.startsWith("classpath:")) {
            String classpath = StringUtils.substringAfter(path, "classpath:");
            URL url = Thread.currentThread().getContextClassLoader().getResource(classpath);
            LOG.info("ファイルを読み込みます {}", url);
            return url.openStream();

        } else {
            File file = new File(path);
            LOG.info("ファイルを読み込みます {}", file.getAbsolutePath());

            return new FileInputStream(path);

        }
    }

    /**
     * CSV文字列をセル内改行を考慮して1行ずつに分離します。
     * 
     * @param csvText
     *            CSV文字列
     * @return セル内改行を考慮した1行ずつを保持するList
     */
    public static List<String> splitToLines(String csvText) {
        List<String> newLines = new ArrayList<>();

        for (Iterator<String> lineItr = Arrays.asList(csvText.split("\r\n|\n")).iterator(); lineItr
                .hasNext();) {

            String line = lineItr.next();
            LOG.trace("line : {}", line);
            StringBuilder newLine = new StringBuilder(line);

            // 行内にダブルクオーテーションが奇数個の場合はセル内改行ありとみなし、
            // 次にダブルクオーテーションが奇数個の行までを連結して1行とする。
            if (StringUtils.countMatches(line, '"') % 2 != 0) {

                do {
                    line = lineItr.next();
                    newLine.append("\n");
                    newLine.append(line);
                } while (StringUtils.countMatches(line, '"') % 2 == 0 && lineItr.hasNext());

            }

            String newLineStr = newLine.toString();
            LOG.trace("new line : {}", newLineStr);
            newLines.add(newLine.toString());
        }

        LOG.trace("actual line number : {}", newLines.size());
        return newLines;
    }

    /**
     * 文字列を「,」(半角カンマ)または「\t」(タブ)で分割しListに格納して返します。分割後の1要素の文字列が「
     * "」(半角ダブルクオーテーション)で開始かつ終了していた場合は、 前後の「"」を除きます。
     * 分割後の要素数が下限値に満たない場合でも、空文字をList末尾に追加して下限値となるようにします。
     *
     * @param line
     *            分割対象の文字列
     * @param minCnt
     *            分割後の要素数の下限値
     * @return 「,」(半角カンマ)または「\t」(タブ)で分割した文字を格納するList
     */
    public static List<String> splitToCells(String line, int minCnt) {

        LOG.trace("split line : {}", line);

        List<String> newCells = new ArrayList<String>();

        for (Iterator<String> cellItr = Arrays.asList(line.split(",")).iterator(); cellItr
                .hasNext();) {

            String cell = cellItr.next();
            StringBuilder builder = new StringBuilder(cell);

            // セル内のダブルクオーテーションが奇数個の場合はセル内分割があるとみなし、
            // 次のダブルクオーテーションが奇数個のセルまで連結して1セルとする。
            if (StringUtils.countMatches(cell, '"') % 2 != 0) {

                do {
                    cell = cellItr.next();
                    // splitで除外された「,」をappend
                    builder.append(",");
                    builder.append(cell);

                } while (StringUtils.countMatches(cell, '"') % 2 == 0 && cellItr.hasNext());

            }

            newCells.add(modifyStr(builder.toString()));

        }

        while (newCells.size() < minCnt) {
            newCells.add("");
        }

        LOG.trace("new cells : {}", newCells);

        return newCells;
    }

    private static String modifyStr(String inStr) {
        String retStr = inStr;

        // 文字列が「"」(半角ダブルクオーテーション)で開始かつ終了していた場合は、 前後の「"」を除く。
        if (retStr != null && retStr.length() > 1 && retStr.startsWith("\"")
                && retStr.endsWith("\"")) {
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
        FileIOUtils.fileEncoding = fileEncoding;
    }

    public static String getLineSeparator() {
        return lineSeparator;
    }

    public static void setLineSeparator(String lineSeparator) {
        FileIOUtils.lineSeparator = lineSeparator;
    }

    public static String getCellSeparator() {
        return cellSeparator;
    }

    public static void setCellSeparator(String cellSeparator) {
        FileIOUtils.cellSeparator = cellSeparator;
    }

}
