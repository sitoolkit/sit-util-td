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
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author yuichi.kuwahara
 */
public class TableData {

    private String name;

    private String inputSource;

    private List<RowData> rows = new ArrayList<>();

    private List<String> columnNameList;

    public List<RowData> getRows() {
        return rows;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void add(RowData rowData) {
        rows.add(rowData);
    }

    public List<String> getColumnNameList() {
        return columnNameList;
    }

    public void setColumnNameList(List<String> columnNameList) {
        this.columnNameList = columnNameList;
    }

    public boolean hasColumnNameList() {
        return columnNameList != null && !columnNameList.isEmpty();
    }

    @Override
    public String toString() {
        return StringUtils.join(rows, FileIOUtils.getLineSeparator());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TableData other = (TableData) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if (this.rows != other.rows && (this.rows == null || !this.rows.equals(other.rows))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 97 * hash + (this.rows != null ? this.rows.hashCode() : 0);
        return hash;
    }

    public int getRowCount() {
        return rows.size();
    }

    public String getInputSource() {
        return inputSource;
    }

    public void setInputSource(String inputSource) {
        this.inputSource = inputSource;
    }
}
