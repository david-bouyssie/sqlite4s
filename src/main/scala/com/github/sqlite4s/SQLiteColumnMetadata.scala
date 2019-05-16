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