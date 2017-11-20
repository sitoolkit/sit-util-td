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

package org.sitoolkit.util.tabledata;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.sitoolkit.util.tabledata.schema.Column;
import org.sitoolkit.util.tabledata.schema.LocalizedProperty;
import org.sitoolkit.util.tabledata.schema.Mapping;
import org.sitoolkit.util.tabledata.schema.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author yuichi.kuwahara
 */
public class TableDataMapper {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    /**
     *
     */
    private Map<String, Table> tableMap = new HashMap<String, Table>();
    /**
     * BeanUtilsで使用するConverter
     */
    private Map<Class<?>, Converter> converterMap = new HashMap<>();

    /**
     * 設定ファイルのパス
     */
    private String configFilePath = "/tabledata-mapping.xml";

    private BeanFactory beanFactory;

    private BeanUtilsBean beanUtils = new BeanUtilsBean();

    private PropertyUtilsBean propertyUtils = new PropertyUtilsBean();

    public RowData map(String beanId, Object bean) {
        RowData rowData = new RowData();
        Table table = tableMap.get(beanId);

        for (Column column : table.getColumn()) {
            if (Boolean.TRUE.equals(column.isReadOnly())) {
                continue;
            }
            try {

                Object value = beanUtils.getProperty(bean, column.getProperty());
                Class<?> propertyType = propertyUtils.getPropertyType(bean, column.getProperty());
                if (ClassUtils.isAssignable(propertyType, Integer.class)) {
                    rowData.setInt(column.getName(), Integer.parseInt(value.toString()),
                            column.getMin());
                } else if (ClassUtils.isAssignable(propertyType, Boolean.class)) {
                    boolean bool = value == null ? false : Boolean.parseBoolean(value.toString());
                    String cellValue = bool ? column.getTrueStr() : column.getFalseStr();
                    rowData.setCellValue(column.getName(), cellValue);
                } else {
                    rowData.setCellValue(column.getName(), value);
                }
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        return rowData;
    }

    /**
     * 1行分のデータをSpring Beanの各プロパティに設定します。 行内の列とプロパティとの対応は、designDoc.xmlの定義に従います。
     * designDoc.xmlは、{@link #init() }で予め読み込まれている事が前提です。
     *
     * @param <T>
     *            Spring Beanの型
     * @param beanId
     *            Spring Beanを指定するID
     * @param rowData
     *            1行分のデータ
     * @param type
     *            Spring Beanの型
     * @return 1行分のデータを設定されたSpring Bean
     */
    public <T> T map(String beanId, RowData rowData, Class<T> type) {
        T bean = beanFactory.getBean(beanId, type);
        Table table = tableMap.get(beanId);

        for (Column column : table.getColumn()) {
            map(bean, column, rowData);
        }
        return bean;
    }

    protected void map(Object bean, Column column, RowData rowData) {
        try {
            String property = column.getProperty();
            Object value = retriveValue(propertyUtils.getPropertyType(bean, column.getProperty()),
                    rowData, column);

            if (log.isTraceEnabled()) {
                log.trace("{}.{}に[{}]を設定します",
                        new Object[] { bean.getClass().getSimpleName(), property, value });
            }

            beanUtils.setProperty(bean, property, value);
        } catch (Exception e) {
            log.error("ID:{}", column.getProperty(), e);
            throw new IllegalStateException(e);
        }
    }

    /**
     * 行データの指定された列の値を取得します。 値は、beanの対応するプロパティの型よって適宜変換されます。
     *
     * @param type
     *            プロパティをの型
     * @param row
     *            プロパティに設定する値を取得する行
     * @param col
     *            設定するプロパティを特定する列
     * @return beanに設定する値
     */
    protected Object retriveValue(Class<?> type, RowData row, Column col) {
        if (StringUtils.isNotEmpty(col.getPattern())) {
            if (ClassUtils.isAssignable(type, Map.class)) {
                return row.getCellValuesAsMap(col.getPattern(), 1, col.getReplace());
            } else {
                return row.getCellValues(col.getPattern(), col.getReplace(),
                        col.isExcludeEmptyValue());
            }
        } else {
            if (ClassUtils.isAssignable(type, Integer.class)) {
                return row.getInt(col.getName(), col.getReplace());
            } else if (ClassUtils.isAssignable(type, Boolean.class)) {
                return row.getBoolean(col.getName(), col.getTrueStr(), col.getReplace());
            } else {
                return row.getCellValue(col.getName(), col.getReplace());
            }
        }
    }

    @PostConstruct
    public void init() {
        log.info(MessageManager.getMessage("tabledata.loadingDef"));
        Mapping mapping = JaxbUtils.res2obj(Mapping.class, getConfigFilePath());

        for (Table table : mapping.getTable()) {

            for (Column column : table.getColumn()) {
                setLocalizedProperty(column);
            }

            for (String id : table.getBeanId().split(",")) {
                tableMap.put(id, table);
            }
        }
        log.info(MessageManager.getMessage("tabledata.loadedDef"), tableMap);
    }

    private void setLocalizedProperty(Column column) {
        String localeStr = Locale.getDefault().getLanguage().toString();

        for (LocalizedProperty localizedName : column.getLocalizedName()) {
            if (localeStr.equalsIgnoreCase(localizedName.getLocale())) {
                column.setName(localizedName.getValue());
            }
        }

        for (LocalizedProperty localizedPattern : column.getLocalizedPattern()) {
            if (localeStr.equalsIgnoreCase(localizedPattern.getLocale())) {
                column.setPattern(localizedPattern.getValue());
            }
        }

    }

    @PostConstruct
    public void initConverters() {
        if (log.isDebugEnabled()) {
            log.debug(MessageManager.getMessage("beanutils.registerConverter"), getConverterMap());
        }
        ConvertUtilsBean convertUtils = new ConvertUtilsBean();
        for (Entry<Class<?>, ? extends Converter> entry : getConverterMap().entrySet()) {
            convertUtils.register(entry.getValue(), entry.getKey());
        }
        beanUtils = new BeanUtilsBean(convertUtils);
    }

    public Map<Class<?>, Converter> getConverterMap() {
        return converterMap;
    }

    public void setConverterMap(Map<Class<?>, Converter> converterMap) {
        this.converterMap = converterMap;
    }

    public String getConfigFilePath() {
        return configFilePath;
    }

    public void setConfigFilePath(String configFilePath) {
        this.configFilePath = configFilePath;
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    Map<String, Table> getTableMap() {
        return tableMap;
    }
}
