/*
 * Copyright 2013 Monocrea Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.sitoolkit.util.tabledata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.sitoolkit.util.tabledata.schema.ReplacePattern;

/**
 *
 * @author yuichi.kuwahara
 */
public class RowData {

    private static final Logger LOG = LoggerFactory.getLogger(RowData.class);

    private Map<String, String> data = new LinkedHashMap<String, String>();

    public RowData() {
        super();
    }

    public RowData(String columnName, Object value) {
        setCellValue(columnName, value);
    }

    /**
     * 当該行の列名に該当するセルの値を返します。
     *
     * @param columnName
     *            列名
     * @return 列名に該当するセルの値
     */
    public String getCellValue(Object columnName) {
        if (isEmpty(columnName)) {
            LOG.warn("列名が空であるセル値は格納できません。");
            return "";
        }

        return getData().get(cleansing(columnName.toString()));
    }

    public String getCellValue(Object columnName, List<ReplacePattern> replaces) {
        if (isEmpty(columnName)) {
            LOG.warn("列名が空であるセル値は格納できません。");
            return StringUtils.EMPTY;
        }
        String columnNameStr = cleansing(columnName.toString());
        return value(getData().get(columnNameStr), replaces);
    }

    private String value(String value, List<ReplacePattern> replaces) {
        if (value == null) {
            return StringUtils.EMPTY;
        } else if (replaces == null) {
            return value;
        } else {
            for (ReplacePattern replace : replaces) {
                value = value.replaceAll(replace.getPattern(), replace.getReplacement());
            }
            return value;
        }
    }

    /**
     * 正規表現に一致する列に該当するセルの値を返します。
     *
     * @param regex
     *            正規表現
     * @return 正規表現に一致する列に該当するセルの値
     */
    public List<String> getCellValues(String regex, List<ReplacePattern> replaces,
            boolean excludeEmptyValue) {
        List<String> valueList = new ArrayList<String>();

        if (StringUtils.isEmpty(regex)) {
            LOG.warn("列名が空であるセル値は格納できません。");
            return valueList;
        }
        Pattern pattern = Pattern.compile(regex);
        for (Entry<String, String> entry : getData().entrySet()) {
            if (pattern.matcher(entry.getKey()).matches()) {
                if (excludeEmptyValue && StringUtils.isEmpty(entry.getValue())) {
                    continue;
                }
                String value = value(entry.getValue(), replaces);
                LOG.trace("key:{}, value:{}", entry.getKey(), value);
                valueList.add(value);
            }
        }

        return valueList;
    }

    /**
     * 正規表現に一致する列名のセルの値をMapとして取得します。
     *
     * @param regex
     *            列名に一致させる正規表現
     * @param groupIdx
     *            列名の中から抜き出しMapのキーとするグループの番号
     * @return 正規表現に一致する列名のセルの値
     * @see Matcher#group(int)
     */
    public Map<String, String> getCellValuesAsMap(String regex, int groupIdx,
            List<ReplacePattern> replaces) {
        Map<String, String> map = new TreeMap<String, String>();

        Pattern pattern = Pattern.compile(regex);
        for (Entry<String, String> entry : getData().entrySet()) {
            Matcher matcher = pattern.matcher(entry.getKey());

            if (matcher.matches()) {
                String value = value(entry.getValue(), replaces);
                map.put(matcher.group(groupIdx), value);
            }
        }
        return map;
    }

    public int getInt(Object columnName, List<ReplacePattern> replaces) {
        return NumberUtils.toInt(getCellValue(columnName, replaces), 0);
    }

    public boolean getBoolean(Object columnName, String trueStr, List<ReplacePattern> replaces) {
        return StringUtils.equalsIgnoreCase(trueStr, getCellValue(columnName, replaces));
    }

    public void setCellValue(Object columnName, Object value) {
        if (isEmpty(columnName)) {
            LOG.warn("列名が空であるセル値は格納できません。");
            return;
        }
        getData().put(cleansing(columnName.toString()), value == null ? "" : value.toString());
    }

    public void setInt(Object columnName, int value, int limit) {
        if (value <= limit) {
            setCellValue(columnName, "");
        } else {
            setCellValue(columnName, value);
        }
    }

    public Map<String, String> getData() {
        return data;
    }

    @Override
    public String toString() {

        Collection<String> cols = getData().values();
        Collection<String> returnCols = new LinkedList<String>();

        for (String col : cols) {

            if (col.contains("\"")) {
                col = col.replace("\"", "\"\"");
                col = "\"" + col + "\"";
            } else if (StringUtils.containsAny(col, ",", "\n")) {
                col = "\"" + col + "\"";
            }

            returnCols.add(col);

        }

        return StringUtils.join(returnCols, ",");

    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RowData other = (RowData) obj;
        if (this.data != other.data && (this.data == null || !this.data.equals(other.data))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 73 * hash + (this.data != null ? this.data.hashCode() : 0);
        return hash;
    }

    public String cleansing(String str) {
        return StringUtils.isEmpty(str) ? "" : str.replaceAll("[\\r\\n 　]", "");
    }

    public boolean isEmpty(Object obj) {
        return obj == null ? true : obj.toString().isEmpty();
    }

}
