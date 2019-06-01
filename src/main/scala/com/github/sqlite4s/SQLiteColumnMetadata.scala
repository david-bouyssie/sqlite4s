/*
 * Copyright 2010 ALM Works Ltd
 * Copyright 2019 David Bouyssié
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

package com.github.sqlite4s

import scala.beans.BeanProperty

/**
  * <p>{@code SQLiteColumnMetadata} contains information about a table column:</p>
  * <ul>
  * <li>Declared datatype;</li>
  * <li>Collation sequence name;</li>
  * <li>Flag that is true if NOT NULL constraint exists;</li>
  * <li>Flag that is true if column is a part of primary key;</li>
  * <li>Flag that is true if column is auto-increment.</li>
  * </ul>
  *
  * You get instances of SQLiteColumnMetadata via {@link SQLiteConnection#getTableColumnMetadata} method.
  *
  * @param dataType declared data type
  * @param collSeq collation sequence name
  * @param isNotNull declared data type
  * @param isPrimaryKey declared data type
  * @param isAutoInc declared data type
  * @see <a href="http://www.sqlite.org/c3ref/table_column_metadata.html">sqlite3_table_column_metadata</a>
  */
case class SQLiteColumnMetadata(
  @BeanProperty
  dataType: String,
  @BeanProperty
  collSeq: String,
  isNotNull: Boolean,
  isPrimaryKey: Boolean,
  isAutoInc: Boolean
)