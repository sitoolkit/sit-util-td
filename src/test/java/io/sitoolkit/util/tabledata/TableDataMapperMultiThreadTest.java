/*
 * Copyright 2016 Monocrea Inc.
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

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.beanutils.Converter;
import org.junit.Test;

import io.sitoolkit.util.tabledata.schema.Column;

/**
 * {@link TableDataMapper}がマルチスレッドで正しく動作することを確認するテストです。
 *
 * @author yuichi.kuwahara
 *
 */
public class TableDataMapperMultiThreadTest {

    @Test
    public void test() throws ExecutionException, InterruptedException {

        ExecutorService executor = Executors.newFixedThreadPool(4);
        List<Future<String>> futures = executor.invokeAll(
                Arrays.asList(new TestCode(), new TestCode(), new TestCode(), new TestCode()));

        for (Future<String> future : futures) {
            future.get();
        }
    }

    private static class TestCode implements Callable<String> {

        /**
         * スレッドに依存するConverterを{@link TableDataMapper}に登録し、
         * {@link TableDataMapper#map(Object, Column, RowData)}を実行します。
         */
        @Override
        public String call() throws Exception {
            TableDataMapper tdm = new TableDataMapper();

            final String threadName = Thread.currentThread().getName();
            tdm.getConverterMap().put(String.class, new Converter() {

                /**
                 * 値の前にスレッド名を付け加える{@code Converter}実装です。
                 */
                @SuppressWarnings("unchecked")
                @Override
                public <T> T convert(Class<T> type, Object value) {
                    return (T) (threadName + value);
                }
            });
            tdm.initConverters();

            RowData rowData = new RowData("プロパティ", "value");
            Column column = new Column();
            column.setProperty("prop");
            column.setName("プロパティ");

            Bean bean = new Bean();

            tdm.map(bean, column, rowData);

            assertThat("変換が不正", bean.getProp(), is(threadName + "value"));

            return threadName;
        }

    }

    public static class Bean {
        private String prop;

        public String getProp() {
            return prop;
        }

        public void setProp(String prop) {
            this.prop = prop;
        }
    }
}
