# SQLite4S

SQLite4S is port of the Java library [Sqlite4java](https://bitbucket.org/almworks/sqlite4java) for the Scala Native platform.

The goal of this project is to provide a thin wrapper around the SQLite C library with an API similar to the Sqlite4java one. Since JNI is not needed anymore thanks to Scala Native, SQLite4S should have a lower overhead compared to the Sqlite4ava wrapper. However performance comparison has not been done yet.

# Supported Sqlite4java features

  * **Thin wrapper** for [SQLite C Interface](http://sqlite.org/c3ref/funclist.html). Most of SQLite's user functions (not extender functions) are either already provided by the library or can be easily added.
  * **Single-threaded model** - each SQLite connection is confined to a single thread, all calls must come from that thread. Application may open several connections to the same database from different threads. Along with the Serializable isolation level from SQLite, this feature facilitates writing very clean and predictable code.
  * **Bulk retrieval** from SELECT statements, greatly improving speed and garbage rate via minimizing the number of JNI calls to `step()` and `column...()` methods. See  [SQLiteStatement.loadInts()](http://almworks.com/sqlite4java/javadoc/index.html) for example.
  * **Interruptible statements** support allows to cancel a long-running query or update. See [SQLiteConnection.interrupt()](http://almworks.com/sqlite4java/javadoc/index.html).
  * **Long array binding (NOT YET IMPLEMENTED)** allows to represent a `long[]` Java array as an SQL table. Table lookup is optimized if you specify that the array is sorted and/or has unique values. See [SQLiteLongArray](http://almworks.com/sqlite4java/javadoc/index.html).
  * **Incremental BLOB I/O** maps to `sqlite3_blob...` methods, which provide means to read/write portions of a large BLOB. See [SQLiteBlob](http://almworks.com/sqlite4java/javadoc/index.html).
  * **BLOBs as streams** - you can bind parameter as an `OutputStream` and read column value as `InputStream`. See [SQLiteStatement.bindStream()](http://almworks.com/sqlite4java/javadoc/index.html) for example.
  * **Job queue implementation (NOT YET IMPLEMENTED)** lets you queue database jobs in a multi-threaded application, to be executed one-by-one in a dedicated database thread. See [JobQueue](https://bitbucket.org/almworks/sqlite4java/wiki/JobQueue).
  * **SQL Profiler** collects statistics on the executed SQL.
  * **Backup API** support lets you use SQLite's hot backup feature. See [SQLiteConnection.initializeBackup()](http://almworks.com/sqlite4java/javadoc/index.html).
  
Please note that the Job queue feature is not yet implemented in SQLite4S due to some limitations of Threads support in the current implementation of the Scala Native platform.
Regarding the Long array feature I decided it was low priority for a first implementation, but if it is an issue for your own project do not hesitate to open an issue.

## Getting started
[![Maven Central](https://img.shields.io/maven-central/v/com.github.david-bouyssie/sqlite4s_native0.4.0_2.11/0.3.0)](https://mvnrepository.com/artifact/com.github.david-bouyssie/sqlite4s_native0.4.0_2.11/0.3.0)

If you are already familiar with Scala Native you can jump right in by adding the following dependency in your `sbt` build file.

```scala
libraryDependencies += "com.github.david-bouyssie" %%% "sqlite4s" % "x.y.z"
```

To use in `sbt`, replace `x.y.z` with the version from Maven Central badge above.
All available versions can be seen at the [Maven Repository](https://mvnrepository.com/artifact/com.github.david-bouyssie/sqlite4s).

Otherwise follow the [Getting Started](https://scala-native.readthedocs.io/en/latest/user/setup.html) instructions for Scala Native if you are not already setup.

Additionally, you need to install [SQLite](https://www.sqlite.org) on you system  as follows:

* Linux/Ubuntu

```
$ sudo apt-get install libsqlite3-dev
```

* macOS

```
$ brew install sqlite3
```

* Other OSes need to have `libsqlite3` available on the system.
An alternative could consist in creating a project sub-directory called for instance "native-lib" and to put the SQLite shared library in this directory.
Then you would also have to change the build.sbt file and add the following settings:
```
nativeLinkingOptions ++= Seq("-L" ++ baseDirectory.value.getAbsolutePath() ++ "/native-lib")
```

## Useful links:

* [Sqlite4java API](http://almworks.com/sqlite4java/javadoc/index.html)
* [SQLite C API](https://www.sqlite.org/capi3ref.html)
