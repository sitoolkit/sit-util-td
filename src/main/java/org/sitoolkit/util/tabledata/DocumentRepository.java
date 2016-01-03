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

/**
 *
 * @author yuichi.kuwahara
 */
public interface DocumentRepository {

	TableData read(String catalogName, String sheetName);

	TableDataCatalog readAll(String catalogName);

	void write(String templateFilePath, String targetFilePath, TableDataCatalog catalog);

}
