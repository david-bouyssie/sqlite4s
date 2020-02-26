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

private[this] object Internal {

  private final val LOG_PREFIX = "[sqlite4s] "

  def mkLogMessage(message: String): String = {
    mkLogMessage(null, message)
  }

  def mkLogMessage(source: String, message: String): String = {
    val builder = new StringBuilder(LOG_PREFIX)
    if (source != null) builder.append(source)

    /*if (source != null) {
      if (source.isInstanceOf[Class[_]]) {
        val className = source.asInstanceOf[Class[_]].getName
        builder.append(className.substring(className.lastIndexOf('.') + 1))
      }
      else builder.append(source)
      builder.append(": ")
    }*/
    if (message != null) builder.append(message)

    builder.toString
  }
}

