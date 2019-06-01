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

import utest._

object RegressionIssue13Tests extends SQLiteConnectionFixture {

  val tests = Tests {
    'testColumnCountBug2 - testColumnCountBug2
    'testUnstableColumnResult - testUnstableColumnResult
  }

  /**
    * @author Olivier Monaco
    */
  @throws[Exception]
  def testColumnCountBug2(): Unit = {
    val cnx = new SQLiteConnection()
    cnx.open()

    try {
      var st = cnx.prepare("create table t (c text);")
      try {
        assert(!st.step())
        assert(0 == st.columnCount())
      } finally st.dispose()
      st = cnx.prepare("select name, type from sqlite_master;")
      try {
        assert(st.step())
        assert(2 == st.columnCount())
      } finally st.dispose()
      st = cnx.prepare("select name, type from sqlite_master " + "where name='not_exists';")

      try {
        assert(!st.step())
        assert(2 == st.columnCount())
      } finally st.dispose()

    } finally cnx.dispose()
  }

  @throws[SQLiteException]
  def testUnstableColumnResult(): Unit = {
    val c = new SQLiteConnection().open()
    c.exec("create table A (x, y)")
    c.exec("insert into A values (1, 2)")
    val st = c.prepare("select * from A")
    assert(2 == st.columnCount())
    c.exec("alter table A add column z")
    // unstable - nothing changed! even if we we to call sqlite3_column_count, it would still return 2!
    assert(2 == st.columnCount())
    // now the statement has been recompiled - the result updated
    st.step()
    assert(3 == st.columnCount())
  }
}
