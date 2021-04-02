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

import java.util.{ArrayList, Collections}

import scala.collection.JavaConverters._

/**
  * SQLParts is a means to avoid excessive garbage production during String concatenation when SQL
  * is constructed.
  * <p>
  * Often, same SQL statements get constructed over and over again and passed to <code>SQLiteConnecton.prepare</code>.
  * To avoid producing garbage and to facilitate quick look-up in the cache of prepared statements, better use SQLParts
  * than String concatenation.
  * <p>
  * SQLParts object may be <strong>fixed</strong>, which means it cannot be changed anymore.
  * <p>
  * This class is <strong>not thread-safe</strong> and not intended to be used from different threads.
  *
  * @author Igor Sereda
  */
object SQLParts {
  private val PARAMS_STRINGS = new Array[String](101)
}

/**
  * Create empty SQLParts object.
  */
final class SQLParts(
  val myParts: ArrayList[String] = new ArrayList[String](5)
) {

  private var myHash: Int = 0
  private var mySql: String = _
  private var myFixed: Boolean = false

  /**
    * Create a copy of another SQLParts object. SQL pieces are copied, but the new object is not fixed even if the
    * original object is fixed.
    *
    * @param copyFrom the original object
    */
  def this(copyFrom: SQLParts) {
    this(new ArrayList[String](if (copyFrom == null) 5 else copyFrom.myParts.size))
    if (copyFrom != null) myParts.addAll(copyFrom.myParts)
  }

  /**
    * Create an instance of SQLParts containing only single piece of SQL.
    *
    * @param sql SQL piece
    */
  def this(sql: String) {
    this(new ArrayList[String](1))
    append(sql)
  }

  /**
    * Gets the list of SQL parts.
    *
    * @return unmodifiable list of SQL pieces.
    */
  def getParts(): java.util.List[String] = Collections.unmodifiableList(myParts)

  /**
    * Checks if this instance is fixed.
    *
    * @return true if the instance is fixed, that is, read-only
    * @see #fix
    */
  def isFixed(): Boolean = myFixed

  /**
    * Makes instance immutable. Further calls to {@link #append} will throw an exception.
    *
    * @return this object, fixed
    */
  def fix(): SQLParts = {
    myFixed = true
    this
  }

  /**
    * If this object is fixed, returns itself, otherwise
    * returns a fixed copy of this object.
    *
    * @return fixed SQLParts, representing the same SQL
    */
  def getFixedParts(): SQLParts = if (myFixed) this else new SQLParts(this).fix()

  private def calcHash(): Int = {
    var r = 0

    for (myPart <- myParts.iterator.asScala) {
      r = 31 * r + myPart.hashCode
    }

    r
  }

  override def hashCode(): Int = {
    if (myHash == 0) myHash = calcHash()
    myHash
  }

  /*override def equals(o: Any): Boolean = {
    if (this.equals(o)) return true
    if (o == null || (getClass != o.getClass)) return false

    val other = o.asInstanceOf[SQLParts].myParts
    if (myParts.size != other.size) return false

    var i = 0
    while (i < myParts.size) {
      if (!myParts.get(i).equals(other.get(i))) return false

      i += 1
    }

    true
  }*/

  override def equals(o: Any): Boolean = {
    if (o == null || myParts == null) return false

    o match {
      case that: SQLParts => {
        if (this eq that) return true

        val other = that.myParts
        if (myParts.size != other.size) return false

        var i = 0
        while (i < myParts.size) {
          if (!myParts.get(i).equals(other.get(i))) return false
          i += 1
        }
      }
      case _ => return false
    }

    true
  }

  /**
    * Empties this SQLParts instance.
    *
    * @throws IllegalStateException if instance is fixed
    */
  def clear(): Unit = {
    if (myFixed) throw new IllegalStateException(String.valueOf(this))
    myParts.clear()
    dropCachedValues()
  }

  /**
    * Adds a part to the SQL.
    *
    * @param part a piece of SQL added
    * @return this instance
    * @throws IllegalStateException if instance is fixed
    */
  def append(part: String): SQLParts = {
    if (myFixed) throw new IllegalStateException(String.valueOf(this))
    if (part != null && part.length > 0) {
      myParts.add(part)
      dropCachedValues()
    }
    this
  }

  /**
    * Adds all parts from a different SQLParts to the SQL.
    *
    * @param parts source object to copy parts from, may be null
    * @return this instance
    * @throws IllegalStateException if instance is fixed
    */
  def append(parts: SQLParts): SQLParts = {
    if (myFixed) throw new IllegalStateException(String.valueOf(this))
    if (parts != null && !parts.myParts.isEmpty) {
      myParts.addAll(parts.myParts)
      dropCachedValues()
    }
    this
  }

  /**
    * Appends an SQL part consisting of a list of bind parameters.
    * <p>
    * That is, <code>appendParams(1)</code> appends <code><strong>?</strong></code>, <code>appendParams(2)</code>
    * appends <code><strong>?,?</strong></code> and so on.
    *
    * @param count the number of parameters ("?" symbols) to be added
    * @return this instance
    * @throws IllegalStateException if instance is fixed
    */
  def appendParams(count: Int): SQLParts = append(getParamsString(count))

  private def getParamsString(count: Int): String = {
    if (count < 1) return null
    if (count >= SQLParts.PARAMS_STRINGS.length) return createParamsString(count)

    var s = SQLParts.PARAMS_STRINGS(count)
    if (s == null) {
      s = createParamsString(count)
      SQLParts.PARAMS_STRINGS(count) = s
    }

    s
  }

  private def createParamsString(count: Int): String = {
    val b = new java.lang.StringBuilder()

    var i = 0
    while (i < count) {
      if (i > 0) b.append(',')
      b.append('?')

      i += 1
    }

    b.toString
  }

  private def dropCachedValues(): Unit = {
    myHash = 0
    mySql = null
  }

  /**
    * Returns the SQL representation of this params
    *
    * @return SQL
    */
  override def toString(): String = {
    if (mySql == null) {
      mySql = myParts.iterator.asScala.mkString
      /*val builder = new java.lang.StringBuilder()
      myParts.foreach { myPart =>
        builder.append(myPart)
      }
      mySql = builder.toString*/
    }

    mySql
  }

}

