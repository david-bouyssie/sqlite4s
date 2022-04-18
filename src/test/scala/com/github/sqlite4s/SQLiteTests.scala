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

object SQLiteTests extends SQLiteTestFixture {

  val tests = Tests {
    /*"test1" - {
      throw new Exception("test1")
    }*/
    "test2" - {
      2 //MemoryMappedFileInScala.run()
    }
    /*"test3" - {
      val a = List[Byte](1, 2)
      a(10)
    }*/
  }




}

/*
import java.io.RandomAccessFile
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

import scala.language.postfixOps

object MemoryMappedFileInScala {

  @throws[Exception]
  def run(): Unit = {
    val startTime = System.currentTimeMillis
    val memoryMappedFile = new RandomAccessFile("largeFile.txt", "rw")

    //Mapping a file into memory
    //10 MB
    val count = 10485760
    var out: MappedByteBuffer = memoryMappedFile.getChannel.map(FileChannel.MapMode.READ_WRITE, 0, count)
    val src = new Array[Byte](count)

    //Writing into Memory Mapped File
    while (out.capacity() > count) {
      out.put('A'.toByte)
    }
    println("Writing to Memory Mapped File is completed")

    //reading from memory file in Java
    //var i = 0
    for (i <- 0 until count) {
      out.get(i).toChar
    }
    println("Reading from Memory Mapped File is completed")
    println("Time elapsed: %.5f seconds." format((System.currentTimeMillis - startTime)/1000F))
  }
}
*/