/*
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

package com.github.sqlite4s.bindings

import scala.scalanative.unsafe._

@link("sqlite3")
@extern
object sqlite {
  type struct___va_list_tag = CStruct0 // incomplete type
  type struct_sqlite3 = CStruct0 // incomplete type
  type sqlite3 = struct_sqlite3
  type sqlite_int64 = CLongLong
  type sqlite_uint64 = CUnsignedLongLong
  type sqlite3_int64 = sqlite_int64
  type sqlite3_uint64 = sqlite_uint64
  type sqlite3_callback = CFuncPtr4[Ptr[Byte], CInt, Ptr[CString], Ptr[CString], CInt]
  type struct_sqlite3_file = CStruct1[Ptr[struct_sqlite3_io_methods]]
  type sqlite3_file = struct_sqlite3_file
  type struct_sqlite3_io_methods = CStruct19[CInt, CFuncPtr1[Ptr[Byte], CInt], CFuncPtr4[Ptr[Byte], Ptr[Byte], CInt, CLongLong, CInt], CFuncPtr4[Ptr[Byte], Ptr[Byte], CInt, CLongLong, CInt], CFuncPtr2[Ptr[Byte], CLongLong, CInt], CFuncPtr2[Ptr[Byte], CInt, CInt], CFuncPtr2[Ptr[Byte], Ptr[CLongLong], CInt], CFuncPtr2[Ptr[Byte], CInt, CInt], CFuncPtr2[Ptr[Byte], CInt, CInt], CFuncPtr2[Ptr[Byte], Ptr[CInt], CInt], CFuncPtr3[Ptr[Byte], CInt, Ptr[Byte], CInt], CFuncPtr1[Ptr[Byte], CInt], CFuncPtr1[Ptr[Byte], CInt], CFuncPtr5[Ptr[Byte], CInt, CInt, CInt, Ptr[Ptr[Byte]], CInt], CFuncPtr4[Ptr[Byte], CInt, CInt, CInt, CInt], CFuncPtr1[Ptr[Byte], Unit], CFuncPtr2[Ptr[Byte], CInt, CInt], CFuncPtr4[Ptr[Byte], CLongLong, CInt, Ptr[Ptr[Byte]], CInt], CFuncPtr3[Ptr[Byte], CLongLong, Ptr[Byte], CInt]]
  type sqlite3_io_methods = struct_sqlite3_io_methods
  type struct_sqlite3_mutex = CStruct0 // incomplete type
  type sqlite3_mutex = struct_sqlite3_mutex
  type struct_sqlite3_vfs = CStruct22[CInt, CInt, CInt, Ptr[Byte], CString, Ptr[Byte], CFuncPtr5[Ptr[Byte], CString, Ptr[struct_sqlite3_file], CInt, Ptr[CInt], CInt], CFuncPtr3[Ptr[Byte], CString, CInt, CInt], CFuncPtr4[Ptr[Byte], CString, CInt, Ptr[CInt], CInt], CFuncPtr4[Ptr[Byte], CString, CInt, CString, CInt], CFuncPtr2[Ptr[Byte], CString, Ptr[Byte]], CFuncPtr3[Ptr[Byte], CInt, CString, Unit], CFuncPtr3[Ptr[Byte], Ptr[Byte], CString, CFuncPtr0[Unit]], CFuncPtr2[Ptr[Byte], Ptr[Byte], Unit], CFuncPtr3[Ptr[Byte], CInt, CString, CInt], CFuncPtr2[Ptr[Byte], CInt, CInt], CFuncPtr2[Ptr[Byte], Ptr[CDouble], CInt], CFuncPtr3[Ptr[Byte], CInt, CString, CInt], CFuncPtr2[Ptr[Byte], Ptr[CLongLong], CInt], CFuncPtr3[Ptr[Byte], CString, CFuncPtr0[Unit], CInt], CFuncPtr2[Ptr[Byte], CString, CFuncPtr0[Unit]], CFuncPtr2[Ptr[Byte], CString, CString]]
  type sqlite3_vfs = struct_sqlite3_vfs
  type sqlite3_syscall_ptr = CFuncPtr0[Unit]
  type struct_sqlite3_mem_methods = CStruct8[CFuncPtr1[CInt, Ptr[Byte]], CFuncPtr1[Ptr[Byte], Unit], CFuncPtr2[Ptr[Byte], CInt, Ptr[Byte]], CFuncPtr1[Ptr[Byte], CInt], CFuncPtr1[CInt, CInt], CFuncPtr1[Ptr[Byte], CInt], CFuncPtr1[Ptr[Byte], Unit], Ptr[Byte]]
  type sqlite3_mem_methods = struct_sqlite3_mem_methods
  type struct_sqlite3_stmt = CStruct0 // incomplete type
  type sqlite3_stmt = struct_sqlite3_stmt
  type struct_sqlite3_value = CStruct0 // incomplete type
  type sqlite3_value = struct_sqlite3_value
  type struct_sqlite3_context = CStruct0 // incomplete type
  type sqlite3_context = struct_sqlite3_context
  // FIXME: this a workaround => find a better solution
  type sqlite3_destructor_type = CFuncPtr1[Ptr[Byte], Unit] //scala.scalanative.runtime.RawPtr //CFuncPtr1[Ptr[Byte], Unit]
  type struct_sqlite3_vtab = CStruct3[Ptr[Byte], CInt, CString]
  type sqlite3_vtab = struct_sqlite3_vtab
  type struct_sqlite3_index_info = CStruct13[CInt, Ptr[struct_sqlite3_index_constraint], CInt, Ptr[struct_sqlite3_index_orderby], Ptr[struct_sqlite3_index_constraint_usage], CInt, CString, CInt, CInt, CDouble, sqlite3_int64, CInt, sqlite3_uint64]
  type sqlite3_index_info = struct_sqlite3_index_info
  type struct_sqlite3_vtab_cursor = CStruct1[Ptr[Byte]]
  type sqlite3_vtab_cursor = struct_sqlite3_vtab_cursor
  // FIXME: see https://github.com/scala-native/scala-native/issues/637 (DBO)
  //type struct_sqlite3_module = CStruct23[CInt, CFuncPtr6[Ptr[sqlite3], Ptr[Byte], CInt, Ptr[CString], Ptr[Ptr[sqlite3_vtab]], Ptr[CString], CInt], CFuncPtr6[Ptr[sqlite3], Ptr[Byte], CInt, Ptr[CString], Ptr[Ptr[sqlite3_vtab]], Ptr[CString], CInt], CFuncPtr2[Ptr[sqlite3_vtab], Ptr[sqlite3_index_info], CInt], CFuncPtr1[Ptr[sqlite3_vtab], CInt], CFuncPtr1[Ptr[sqlite3_vtab], CInt], CFuncPtr2[Ptr[sqlite3_vtab], Ptr[Ptr[sqlite3_vtab_cursor]], CInt], CFuncPtr1[Ptr[sqlite3_vtab_cursor], CInt], CFuncPtr5[Ptr[sqlite3_vtab_cursor], CInt, CString, CInt, Ptr[Ptr[sqlite3_value]], CInt], CFuncPtr1[Ptr[sqlite3_vtab_cursor], CInt], CFuncPtr1[Ptr[sqlite3_vtab_cursor], CInt], CFuncPtr3[Ptr[sqlite3_vtab_cursor], Ptr[sqlite3_context], CInt, CInt], CFuncPtr2[Ptr[sqlite3_vtab_cursor], Ptr[sqlite3_int64], CInt], CFuncPtr4[Ptr[sqlite3_vtab], CInt, Ptr[Ptr[sqlite3_value]], Ptr[sqlite3_int64], CInt], CFuncPtr1[Ptr[sqlite3_vtab], CInt], CFuncPtr1[Ptr[sqlite3_vtab], CInt], CFuncPtr1[Ptr[sqlite3_vtab], CInt], CFuncPtr1[Ptr[sqlite3_vtab], CInt], CFuncPtr5[Ptr[sqlite3_vtab], CInt, CString, Ptr[CFuncPtr3[Ptr[sqlite3_context], CInt, Ptr[Ptr[sqlite3_value]], Unit]], Ptr[Ptr[Byte]], CInt], CFuncPtr2[Ptr[sqlite3_vtab], CString, CInt], CFuncPtr2[Ptr[sqlite3_vtab], CInt, CInt], CFuncPtr2[Ptr[sqlite3_vtab], CInt, CInt], CFuncPtr2[Ptr[sqlite3_vtab], CInt, CInt]]
  //type sqlite3_module = struct_sqlite3_module
  type struct_sqlite3_index_constraint = CStruct4[CInt, CUnsignedChar, CUnsignedChar, CInt]
  type struct_sqlite3_index_orderby = CStruct2[CInt, CUnsignedChar]
  type struct_sqlite3_index_constraint_usage = CStruct2[CInt, CUnsignedChar]
  type struct_sqlite3_blob = CStruct0 // incomplete type
  type sqlite3_blob = struct_sqlite3_blob
  type struct_sqlite3_mutex_methods = CStruct9[CFuncPtr0[CInt], CFuncPtr0[CInt], CFuncPtr1[CInt, Ptr[sqlite3_mutex]], CFuncPtr1[Ptr[sqlite3_mutex], Unit], CFuncPtr1[Ptr[sqlite3_mutex], Unit], CFuncPtr1[Ptr[sqlite3_mutex], CInt], CFuncPtr1[Ptr[sqlite3_mutex], Unit], CFuncPtr1[Ptr[sqlite3_mutex], CInt], CFuncPtr1[Ptr[sqlite3_mutex], CInt]]
  type sqlite3_mutex_methods = struct_sqlite3_mutex_methods
  type struct_sqlite3_pcache = CStruct0 // incomplete type
  type sqlite3_pcache = struct_sqlite3_pcache
  type struct_sqlite3_pcache_page = CStruct2[Ptr[Byte], Ptr[Byte]]
  type sqlite3_pcache_page = struct_sqlite3_pcache_page
  type struct_sqlite3_pcache_methods2 = CStruct13[CInt, Ptr[Byte], CFuncPtr1[Ptr[Byte], CInt], CFuncPtr1[Ptr[Byte], Unit], CFuncPtr3[CInt, CInt, CInt, Ptr[sqlite3_pcache]], CFuncPtr2[Ptr[sqlite3_pcache], CInt, Unit], CFuncPtr1[Ptr[sqlite3_pcache], CInt], CFuncPtr3[Ptr[sqlite3_pcache], CUnsignedInt, CInt, Ptr[sqlite3_pcache_page]], CFuncPtr3[Ptr[sqlite3_pcache], Ptr[sqlite3_pcache_page], CInt, Unit], CFuncPtr4[Ptr[sqlite3_pcache], Ptr[sqlite3_pcache_page], CUnsignedInt, CUnsignedInt, Unit], CFuncPtr2[Ptr[sqlite3_pcache], CUnsignedInt, Unit], CFuncPtr1[Ptr[sqlite3_pcache], Unit], CFuncPtr1[Ptr[sqlite3_pcache], Unit]]
  type sqlite3_pcache_methods2 = struct_sqlite3_pcache_methods2
  type struct_sqlite3_pcache_methods = CStruct11[Ptr[Byte], CFuncPtr1[Ptr[Byte], CInt], CFuncPtr1[Ptr[Byte], Unit], CFuncPtr2[CInt, CInt, Ptr[sqlite3_pcache]], CFuncPtr2[Ptr[sqlite3_pcache], CInt, Unit], CFuncPtr1[Ptr[sqlite3_pcache], CInt], CFuncPtr3[Ptr[sqlite3_pcache], CUnsignedInt, CInt, Ptr[Byte]], CFuncPtr3[Ptr[sqlite3_pcache], Ptr[Byte], CInt, Unit], CFuncPtr4[Ptr[sqlite3_pcache], Ptr[Byte], CUnsignedInt, CUnsignedInt, Unit], CFuncPtr2[Ptr[sqlite3_pcache], CUnsignedInt, Unit], CFuncPtr1[Ptr[sqlite3_pcache], Unit]]
  type sqlite3_pcache_methods = struct_sqlite3_pcache_methods
  type struct_sqlite3_backup = CStruct0 // incomplete type
  type sqlite3_backup = struct_sqlite3_backup
  type struct_sqlite3_snapshot = CStruct1[CArray[CUnsignedChar, Nat.Digit2[Nat._4, Nat._8]]]
  type sqlite3_snapshot = struct_sqlite3_snapshot
  type struct_sqlite3_rtree_geometry = CStruct5[Ptr[Byte], CInt, Ptr[sqlite3_rtree_dbl], Ptr[Byte], CFuncPtr1[Ptr[Byte], Unit]]
  type sqlite3_rtree_geometry = struct_sqlite3_rtree_geometry
  type struct_sqlite3_rtree_query_info = CStruct16[Ptr[Byte], CInt, Ptr[sqlite3_rtree_dbl], Ptr[Byte], CFuncPtr1[Ptr[Byte], Unit], Ptr[sqlite3_rtree_dbl], Ptr[CUnsignedInt], CInt, CInt, CInt, sqlite3_int64, sqlite3_rtree_dbl, CInt, CInt, sqlite3_rtree_dbl, Ptr[Ptr[sqlite3_value]]]
  type sqlite3_rtree_query_info = struct_sqlite3_rtree_query_info
  type sqlite3_rtree_dbl = CDouble
  //type struct_Fts5ExtensionApi = CStruct20[CInt, CFuncPtr1[Ptr[Fts5Context], Ptr[Byte]], CFuncPtr1[Ptr[Fts5Context], CInt], CFuncPtr2[Ptr[Fts5Context], Ptr[sqlite3_int64], CInt], CFuncPtr3[Ptr[Fts5Context], CInt, Ptr[sqlite3_int64], CInt], CFuncPtr5[Ptr[Fts5Context], CString, CInt, Ptr[Byte], CFuncPtr6[Ptr[Byte], CInt, CString, CInt, CInt, CInt, CInt], CInt], CFuncPtr1[Ptr[Fts5Context], CInt], CFuncPtr2[Ptr[Fts5Context], CInt, CInt], CFuncPtr2[Ptr[Fts5Context], Ptr[CInt], CInt], CFuncPtr5[Ptr[Fts5Context], CInt, Ptr[CInt], Ptr[CInt], Ptr[CInt], CInt], CFuncPtr1[Ptr[Fts5Context], sqlite3_int64], CFuncPtr4[Ptr[Fts5Context], CInt, Ptr[CString], Ptr[CInt], CInt], CFuncPtr3[Ptr[Fts5Context], CInt, Ptr[CInt], CInt], CFuncPtr4[Ptr[struct_Fts5Context], CInt, Ptr[Byte], CFuncPtr3[Ptr[struct_Fts5ExtensionApi], Ptr[struct_Fts5Context], Ptr[Byte], CInt], CInt], CFuncPtr3[Ptr[Fts5Context], Ptr[Byte], CFuncPtr1[Ptr[Byte], Unit], CInt], CFuncPtr2[Ptr[Fts5Context], CInt, Ptr[Byte]], CFuncPtr5[Ptr[Fts5Context], CInt, Ptr[Fts5PhraseIter], Ptr[CInt], Ptr[CInt], CInt], CFuncPtr4[Ptr[Fts5Context], Ptr[Fts5PhraseIter], Ptr[CInt], Ptr[CInt], Unit], CFuncPtr4[Ptr[Fts5Context], CInt, Ptr[Fts5PhraseIter], Ptr[CInt], CInt], CFuncPtr3[Ptr[Fts5Context], Ptr[Fts5PhraseIter], Ptr[CInt], Unit]]
  //type Fts5ExtensionApi = struct_Fts5ExtensionApi
  type struct_Fts5Context = CStruct0 // incomplete type
  type Fts5Context = struct_Fts5Context
  type struct_Fts5PhraseIter = CStruct2[Ptr[CUnsignedChar], Ptr[CUnsignedChar]]
  type Fts5PhraseIter = struct_Fts5PhraseIter
  //type fts5_extension_function = CFuncPtr5[Ptr[Fts5ExtensionApi], Ptr[Fts5Context], Ptr[sqlite3_context], CInt, Ptr[Ptr[sqlite3_value]], Unit]
  type struct_Fts5Tokenizer = CStruct0 // incomplete type
  type Fts5Tokenizer = struct_Fts5Tokenizer
  type struct_fts5_tokenizer = CStruct3[CFuncPtr4[Ptr[Byte], Ptr[CString], CInt, Ptr[Ptr[Fts5Tokenizer]], CInt], CFuncPtr1[Ptr[Fts5Tokenizer], Unit], CFuncPtr6[Ptr[Fts5Tokenizer], Ptr[Byte], CInt, CString, CInt, CFuncPtr6[Ptr[Byte], CInt, CString, CInt, CInt, CInt, CInt], CInt]]
  type fts5_tokenizer = struct_fts5_tokenizer
  //type struct_fts5_api = CStruct4[CInt, CFuncPtr5[Ptr[Byte], CString, Ptr[Byte], Ptr[struct_fts5_tokenizer], CFuncPtr1[Ptr[Byte], Unit], CInt], CFuncPtr4[Ptr[Byte], CString, Ptr[Ptr[Byte]], Ptr[struct_fts5_tokenizer], CInt], CFuncPtr5[Ptr[Byte], CString, Ptr[Byte], CFuncPtr5[Ptr[struct_Fts5ExtensionApi], Ptr[struct_Fts5Context], Ptr[struct_sqlite3_context], CInt, Ptr[Ptr[struct_sqlite3_value]], Unit], CFuncPtr1[Ptr[Byte], Unit], CInt]]
  //type fts5_api = struct_fts5_api
  val sqlite3_version: CString = extern
  val sqlite3_temp_directory: CString = extern
  val sqlite3_data_directory: CString = extern
  def sqlite3_libversion(): CString = extern
  def sqlite3_sourceid(): CString = extern
  def sqlite3_libversion_number(): CInt = extern
  def sqlite3_compileoption_used(zOptName: CString): CInt = extern
  def sqlite3_compileoption_get(N: CInt): CString = extern
  def sqlite3_threadsafe(): CInt = extern
  def sqlite3_close(p0: Ptr[sqlite3]): CInt = extern
  def sqlite3_close_v2(p0: Ptr[sqlite3]): CInt = extern
  def sqlite3_exec(p0: Ptr[sqlite3], sql: CString, callback: CFuncPtr4[Ptr[Byte], CInt, Ptr[CString], Ptr[CString], CInt], p1: Ptr[Byte], errmsg: Ptr[CString]): CInt = extern
  def sqlite3_initialize(): CInt = extern
  def sqlite3_shutdown(): CInt = extern
  def sqlite3_os_init(): CInt = extern
  def sqlite3_os_end(): CInt = extern
  def sqlite3_config(p0: CInt, varArgs: CVarArg*): CInt = extern
  def sqlite3_db_config(p0: Ptr[sqlite3], op: CInt, varArgs: CVarArg*): CInt = extern
  def sqlite3_extended_result_codes(p0: Ptr[sqlite3], onoff: CInt): CInt = extern
  def sqlite3_last_insert_rowid(p0: Ptr[sqlite3]): sqlite3_int64 = extern
  def sqlite3_set_last_insert_rowid(p0: Ptr[sqlite3], p1: sqlite3_int64): Unit = extern
  def sqlite3_changes(p0: Ptr[sqlite3]): CInt = extern
  def sqlite3_total_changes(p0: Ptr[sqlite3]): CInt = extern
  def sqlite3_interrupt(p0: Ptr[sqlite3]): Unit = extern
  def sqlite3_complete(sql: CString): CInt = extern
  def sqlite3_complete16(sql: Ptr[Byte]): CInt = extern
  def sqlite3_busy_handler(p0: Ptr[sqlite3], p1: CFuncPtr2[Ptr[Byte], CInt, CInt], p2: Ptr[Byte]): CInt = extern
  def sqlite3_busy_timeout(p0: Ptr[sqlite3], ms: CInt): CInt = extern
  def sqlite3_get_table(db: Ptr[sqlite3], zSql: CString, pazResult: Ptr[Ptr[CString]], pnRow: Ptr[CInt], pnColumn: Ptr[CInt], pzErrmsg: Ptr[CString]): CInt = extern
  def sqlite3_free_table(result: Ptr[CString]): Unit = extern
  def sqlite3_mprintf(p0: CString, varArgs: CVarArg*): CString = extern
  def sqlite3_vmprintf(p0: CString, p1: Ptr[struct___va_list_tag]): CString = extern
  def sqlite3_snprintf(p0: CInt, p1: CString, p2: CString, varArgs: CVarArg*): CString = extern
  def sqlite3_vsnprintf(p0: CInt, p1: CString, p2: CString, p3: Ptr[struct___va_list_tag]): CString = extern
  def sqlite3_malloc(p0: CInt): Ptr[Byte] = extern
  def sqlite3_malloc64(p0: sqlite3_uint64): Ptr[Byte] = extern
  def sqlite3_realloc(p0: Ptr[Byte], p1: CInt): Ptr[Byte] = extern
  def sqlite3_realloc64(p0: Ptr[Byte], p1: sqlite3_uint64): Ptr[Byte] = extern
  def sqlite3_free(p0: Ptr[Byte]): Unit = extern
  def sqlite3_msize(p0: Ptr[Byte]): sqlite3_uint64 = extern
  def sqlite3_memory_used(): sqlite3_int64 = extern
  def sqlite3_memory_highwater(resetFlag: CInt): sqlite3_int64 = extern
  def sqlite3_randomness(N: CInt, P: Ptr[Byte]): Unit = extern
  def sqlite3_set_authorizer(p0: Ptr[sqlite3], xAuth: CFuncPtr6[Ptr[Byte], CInt, CString, CString, CString, CString, CInt], pUserData: Ptr[Byte]): CInt = extern
  def sqlite3_trace(p0: Ptr[sqlite3], xTrace: CFuncPtr2[Ptr[Byte], CString, Unit], p1: Ptr[Byte]): Ptr[Byte] = extern
  def sqlite3_profile(p0: Ptr[sqlite3], xProfile: CFuncPtr3[Ptr[Byte], CString, sqlite3_uint64, Unit], p1: Ptr[Byte]): Ptr[Byte] = extern
  def sqlite3_trace_v2(p0: Ptr[sqlite3], uMask: CUnsignedInt, xCallback: CFuncPtr4[CUnsignedInt, Ptr[Byte], Ptr[Byte], Ptr[Byte], CInt], pCtx: Ptr[Byte]): CInt = extern
  def sqlite3_progress_handler(p0: Ptr[sqlite3], p1: CInt, p2: CFuncPtr1[Ptr[Byte], CInt], p3: Ptr[Byte]): Unit = extern
  def sqlite3_open(filename: CString, ppDb: Ptr[Ptr[sqlite3]]): CInt = extern
  def sqlite3_open16(filename: Ptr[Byte], ppDb: Ptr[Ptr[sqlite3]]): CInt = extern
  def sqlite3_open_v2(filename: CString, ppDb: Ptr[Ptr[sqlite3]], flags: CInt, zVfs: CString): CInt = extern
  def sqlite3_uri_parameter(zFilename: CString, zParam: CString): CString = extern
  def sqlite3_uri_boolean(zFile: CString, zParam: CString, bDefault: CInt): CInt = extern
  def sqlite3_uri_int64(p0: CString, p1: CString, p2: sqlite3_int64): sqlite3_int64 = extern
  def sqlite3_errcode(db: Ptr[sqlite3]): CInt = extern
  def sqlite3_extended_errcode(db: Ptr[sqlite3]): CInt = extern
  def sqlite3_errmsg(p0: Ptr[sqlite3]): CString = extern
  def sqlite3_errmsg16(p0: Ptr[sqlite3]): Ptr[Byte] = extern
  def sqlite3_errstr(p0: CInt): CString = extern
  def sqlite3_limit(p0: Ptr[sqlite3], id: CInt, newVal: CInt): CInt = extern
  def sqlite3_prepare(db: Ptr[sqlite3], zSql: CString, nByte: CInt, ppStmt: Ptr[Ptr[sqlite3_stmt]], pzTail: Ptr[CString]): CInt = extern
  def sqlite3_prepare_v2(db: Ptr[sqlite3], zSql: CString, nByte: CInt, ppStmt: Ptr[Ptr[sqlite3_stmt]], pzTail: Ptr[CString]): CInt = extern
  def sqlite3_prepare_v3(db: Ptr[sqlite3], zSql: CString, nByte: CInt, prepFlags: CUnsignedInt, ppStmt: Ptr[Ptr[sqlite3_stmt]], pzTail: Ptr[CString]): CInt = extern
  def sqlite3_prepare16(db: Ptr[sqlite3], zSql: Ptr[Byte], nByte: CInt, ppStmt: Ptr[Ptr[sqlite3_stmt]], pzTail: Ptr[Ptr[Byte]]): CInt = extern
  def sqlite3_prepare16_v2(db: Ptr[sqlite3], zSql: Ptr[Byte], nByte: CInt, ppStmt: Ptr[Ptr[sqlite3_stmt]], pzTail: Ptr[Ptr[Byte]]): CInt = extern
  def sqlite3_prepare16_v3(db: Ptr[sqlite3], zSql: Ptr[Byte], nByte: CInt, prepFlags: CUnsignedInt, ppStmt: Ptr[Ptr[sqlite3_stmt]], pzTail: Ptr[Ptr[Byte]]): CInt = extern
  def sqlite3_sql(pStmt: Ptr[sqlite3_stmt]): CString = extern
  def sqlite3_expanded_sql(pStmt: Ptr[sqlite3_stmt]): CString = extern
  def sqlite3_stmt_readonly(pStmt: Ptr[sqlite3_stmt]): CInt = extern
  def sqlite3_stmt_busy(p0: Ptr[sqlite3_stmt]): CInt = extern
  def sqlite3_bind_blob(p0: Ptr[sqlite3_stmt], p1: CInt, p2: Ptr[Byte], n: CInt, p3: sqlite3_destructor_type): CInt = extern
  def sqlite3_bind_blob64(p0: Ptr[sqlite3_stmt], p1: CInt, p2: Ptr[Byte], p3: sqlite3_uint64, p4: sqlite3_destructor_type): CInt = extern
  def sqlite3_bind_double(p0: Ptr[sqlite3_stmt], p1: CInt, p2: CDouble): CInt = extern
  def sqlite3_bind_int(p0: Ptr[sqlite3_stmt], p1: CInt, p2: CInt): CInt = extern
  def sqlite3_bind_int64(p0: Ptr[sqlite3_stmt], p1: CInt, p2: sqlite3_int64): CInt = extern
  def sqlite3_bind_null(p0: Ptr[sqlite3_stmt], p1: CInt): CInt = extern
  def sqlite3_bind_text(p0: Ptr[sqlite3_stmt], p1: CInt, p2: CString, p3: CInt, p4: sqlite3_destructor_type): CInt = extern
  def sqlite3_bind_text16(p0: Ptr[sqlite3_stmt], p1: CInt, p2: Ptr[Byte], p3: CInt, p4: sqlite3_destructor_type): CInt = extern
  def sqlite3_bind_text64(p0: Ptr[sqlite3_stmt], p1: CInt, p2: CString, p3: sqlite3_uint64, p4: sqlite3_destructor_type, encoding: CUnsignedChar): CInt = extern
  def sqlite3_bind_value(p0: Ptr[sqlite3_stmt], p1: CInt, p2: Ptr[sqlite3_value]): CInt = extern
  def sqlite3_bind_pointer(p0: Ptr[sqlite3_stmt], p1: CInt, p2: Ptr[Byte], p3: CString, p4: CFuncPtr1[Ptr[Byte], Unit]): CInt = extern
  def sqlite3_bind_zeroblob(p0: Ptr[sqlite3_stmt], p1: CInt, n: CInt): CInt = extern
  def sqlite3_bind_zeroblob64(p0: Ptr[sqlite3_stmt], p1: CInt, p2: sqlite3_uint64): CInt = extern
  def sqlite3_bind_parameter_count(p0: Ptr[sqlite3_stmt]): CInt = extern
  def sqlite3_bind_parameter_name(p0: Ptr[sqlite3_stmt], p1: CInt): CString = extern
  def sqlite3_bind_parameter_index(p0: Ptr[sqlite3_stmt], zName: CString): CInt = extern
  def sqlite3_clear_bindings(p0: Ptr[sqlite3_stmt]): CInt = extern
  def sqlite3_column_count(pStmt: Ptr[sqlite3_stmt]): CInt = extern
  def sqlite3_column_name(p0: Ptr[sqlite3_stmt], N: CInt): CString = extern
  def sqlite3_column_name16(p0: Ptr[sqlite3_stmt], N: CInt): Ptr[Byte] = extern
  def sqlite3_column_database_name(p0: Ptr[sqlite3_stmt], p1: CInt): CString = extern
  def sqlite3_column_database_name16(p0: Ptr[sqlite3_stmt], p1: CInt): Ptr[Byte] = extern
  def sqlite3_column_table_name(p0: Ptr[sqlite3_stmt], p1: CInt): CString = extern
  def sqlite3_column_table_name16(p0: Ptr[sqlite3_stmt], p1: CInt): Ptr[Byte] = extern
  def sqlite3_column_origin_name(p0: Ptr[sqlite3_stmt], p1: CInt): CString = extern
  def sqlite3_column_origin_name16(p0: Ptr[sqlite3_stmt], p1: CInt): Ptr[Byte] = extern
  def sqlite3_column_decltype(p0: Ptr[sqlite3_stmt], p1: CInt): CString = extern
  def sqlite3_column_decltype16(p0: Ptr[sqlite3_stmt], p1: CInt): Ptr[Byte] = extern
  def sqlite3_step(p0: Ptr[sqlite3_stmt]): CInt = extern
  def sqlite3_data_count(pStmt: Ptr[sqlite3_stmt]): CInt = extern
  def sqlite3_column_blob(p0: Ptr[sqlite3_stmt], iCol: CInt): Ptr[Byte] = extern
  def sqlite3_column_double(p0: Ptr[sqlite3_stmt], iCol: CInt): CDouble = extern
  def sqlite3_column_int(p0: Ptr[sqlite3_stmt], iCol: CInt): CInt = extern
  def sqlite3_column_int64(p0: Ptr[sqlite3_stmt], iCol: CInt): sqlite3_int64 = extern
  def sqlite3_column_text(p0: Ptr[sqlite3_stmt], iCol: CInt): Ptr[CUnsignedChar] = extern
  def sqlite3_column_text16(p0: Ptr[sqlite3_stmt], iCol: CInt): Ptr[Byte] = extern
  def sqlite3_column_value(p0: Ptr[sqlite3_stmt], iCol: CInt): Ptr[sqlite3_value] = extern
  def sqlite3_column_bytes(p0: Ptr[sqlite3_stmt], iCol: CInt): CInt = extern
  def sqlite3_column_bytes16(p0: Ptr[sqlite3_stmt], iCol: CInt): CInt = extern
  def sqlite3_column_type(p0: Ptr[sqlite3_stmt], iCol: CInt): CInt = extern
  def sqlite3_finalize(pStmt: Ptr[sqlite3_stmt]): CInt = extern
  def sqlite3_reset(pStmt: Ptr[sqlite3_stmt]): CInt = extern
  def sqlite3_create_function(db: Ptr[sqlite3], zFunctionName: CString, nArg: CInt, eTextRep: CInt, pApp: Ptr[Byte], xFunc: CFuncPtr3[Ptr[sqlite3_context], CInt, Ptr[Ptr[sqlite3_value]], Unit], xStep: CFuncPtr3[Ptr[sqlite3_context], CInt, Ptr[Ptr[sqlite3_value]], Unit], xFinal: CFuncPtr1[Ptr[sqlite3_context], Unit]): CInt = extern
  def sqlite3_create_function16(db: Ptr[sqlite3], zFunctionName: Ptr[Byte], nArg: CInt, eTextRep: CInt, pApp: Ptr[Byte], xFunc: CFuncPtr3[Ptr[sqlite3_context], CInt, Ptr[Ptr[sqlite3_value]], Unit], xStep: CFuncPtr3[Ptr[sqlite3_context], CInt, Ptr[Ptr[sqlite3_value]], Unit], xFinal: CFuncPtr1[Ptr[sqlite3_context], Unit]): CInt = extern
  def sqlite3_create_function_v2(db: Ptr[sqlite3], zFunctionName: CString, nArg: CInt, eTextRep: CInt, pApp: Ptr[Byte], xFunc: CFuncPtr3[Ptr[sqlite3_context], CInt, Ptr[Ptr[sqlite3_value]], Unit], xStep: CFuncPtr3[Ptr[sqlite3_context], CInt, Ptr[Ptr[sqlite3_value]], Unit], xFinal: CFuncPtr1[Ptr[sqlite3_context], Unit], xDestroy: CFuncPtr1[Ptr[Byte], Unit]): CInt = extern
  def sqlite3_aggregate_count(p0: Ptr[sqlite3_context]): CInt = extern
  def sqlite3_expired(p0: Ptr[sqlite3_stmt]): CInt = extern
  def sqlite3_transfer_bindings(p0: Ptr[sqlite3_stmt], p1: Ptr[sqlite3_stmt]): CInt = extern
  def sqlite3_global_recover(): CInt = extern
  def sqlite3_thread_cleanup(): Unit = extern
  def sqlite3_memory_alarm(p0: CFuncPtr3[Ptr[Byte], sqlite3_int64, CInt, Unit], p1: Ptr[Byte], p2: sqlite3_int64): CInt = extern
  def sqlite3_value_blob(p0: Ptr[sqlite3_value]): Ptr[Byte] = extern
  def sqlite3_value_double(p0: Ptr[sqlite3_value]): CDouble = extern
  def sqlite3_value_int(p0: Ptr[sqlite3_value]): CInt = extern
  def sqlite3_value_int64(p0: Ptr[sqlite3_value]): sqlite3_int64 = extern
  def sqlite3_value_pointer(p0: Ptr[sqlite3_value], p1: CString): Ptr[Byte] = extern
  def sqlite3_value_text(p0: Ptr[sqlite3_value]): Ptr[CUnsignedChar] = extern
  def sqlite3_value_text16(p0: Ptr[sqlite3_value]): Ptr[Byte] = extern
  def sqlite3_value_text16le(p0: Ptr[sqlite3_value]): Ptr[Byte] = extern
  def sqlite3_value_text16be(p0: Ptr[sqlite3_value]): Ptr[Byte] = extern
  def sqlite3_value_bytes(p0: Ptr[sqlite3_value]): CInt = extern
  def sqlite3_value_bytes16(p0: Ptr[sqlite3_value]): CInt = extern
  def sqlite3_value_type(p0: Ptr[sqlite3_value]): CInt = extern
  def sqlite3_value_numeric_type(p0: Ptr[sqlite3_value]): CInt = extern
  def sqlite3_value_nochange(p0: Ptr[sqlite3_value]): CInt = extern
  def sqlite3_value_subtype(p0: Ptr[sqlite3_value]): CUnsignedInt = extern
  def sqlite3_value_dup(p0: Ptr[sqlite3_value]): Ptr[sqlite3_value] = extern
  def sqlite3_value_free(p0: Ptr[sqlite3_value]): Unit = extern
  def sqlite3_aggregate_context(p0: Ptr[sqlite3_context], nBytes: CInt): Ptr[Byte] = extern
  def sqlite3_user_data(p0: Ptr[sqlite3_context]): Ptr[Byte] = extern
  def sqlite3_context_db_handle(p0: Ptr[sqlite3_context]): Ptr[sqlite3] = extern
  def sqlite3_get_auxdata(p0: Ptr[sqlite3_context], N: CInt): Ptr[Byte] = extern
  def sqlite3_set_auxdata(p0: Ptr[sqlite3_context], N: CInt, p1: Ptr[Byte], p2: CFuncPtr1[Ptr[Byte], Unit]): Unit = extern
  def sqlite3_result_blob(p0: Ptr[sqlite3_context], p1: Ptr[Byte], p2: CInt, p3: CFuncPtr1[Ptr[Byte], Unit]): Unit = extern
  def sqlite3_result_blob64(p0: Ptr[sqlite3_context], p1: Ptr[Byte], p2: sqlite3_uint64, p3: CFuncPtr1[Ptr[Byte], Unit]): Unit = extern
  def sqlite3_result_double(p0: Ptr[sqlite3_context], p1: CDouble): Unit = extern
  def sqlite3_result_error(p0: Ptr[sqlite3_context], p1: CString, p2: CInt): Unit = extern
  def sqlite3_result_error16(p0: Ptr[sqlite3_context], p1: Ptr[Byte], p2: CInt): Unit = extern
  def sqlite3_result_error_toobig(p0: Ptr[sqlite3_context]): Unit = extern
  def sqlite3_result_error_nomem(p0: Ptr[sqlite3_context]): Unit = extern
  def sqlite3_result_error_code(p0: Ptr[sqlite3_context], p1: CInt): Unit = extern
  def sqlite3_result_int(p0: Ptr[sqlite3_context], p1: CInt): Unit = extern
  def sqlite3_result_int64(p0: Ptr[sqlite3_context], p1: sqlite3_int64): Unit = extern
  def sqlite3_result_null(p0: Ptr[sqlite3_context]): Unit = extern
  def sqlite3_result_text(p0: Ptr[sqlite3_context], p1: CString, p2: CInt, p3: CFuncPtr1[Ptr[Byte], Unit]): Unit = extern
  def sqlite3_result_text64(p0: Ptr[sqlite3_context], p1: CString, p2: sqlite3_uint64, p3: CFuncPtr1[Ptr[Byte], Unit], encoding: CUnsignedChar): Unit = extern
  def sqlite3_result_text16(p0: Ptr[sqlite3_context], p1: Ptr[Byte], p2: CInt, p3: CFuncPtr1[Ptr[Byte], Unit]): Unit = extern
  def sqlite3_result_text16le(p0: Ptr[sqlite3_context], p1: Ptr[Byte], p2: CInt, p3: CFuncPtr1[Ptr[Byte], Unit]): Unit = extern
  def sqlite3_result_text16be(p0: Ptr[sqlite3_context], p1: Ptr[Byte], p2: CInt, p3: CFuncPtr1[Ptr[Byte], Unit]): Unit = extern
  def sqlite3_result_value(p0: Ptr[sqlite3_context], p1: Ptr[sqlite3_value]): Unit = extern
  def sqlite3_result_pointer(p0: Ptr[sqlite3_context], p1: Ptr[Byte], p2: CString, p3: CFuncPtr1[Ptr[Byte], Unit]): Unit = extern
  def sqlite3_result_zeroblob(p0: Ptr[sqlite3_context], n: CInt): Unit = extern
  def sqlite3_result_zeroblob64(p0: Ptr[sqlite3_context], n: sqlite3_uint64): CInt = extern
  def sqlite3_result_subtype(p0: Ptr[sqlite3_context], p1: CUnsignedInt): Unit = extern
  def sqlite3_create_collation(p0: Ptr[sqlite3], zName: CString, eTextRep: CInt, pArg: Ptr[Byte], xCompare: CFuncPtr5[Ptr[Byte], CInt, Ptr[Byte], CInt, Ptr[Byte], CInt]): CInt = extern
  def sqlite3_create_collation_v2(p0: Ptr[sqlite3], zName: CString, eTextRep: CInt, pArg: Ptr[Byte], xCompare: CFuncPtr5[Ptr[Byte], CInt, Ptr[Byte], CInt, Ptr[Byte], CInt], xDestroy: CFuncPtr1[Ptr[Byte], Unit]): CInt = extern
  def sqlite3_create_collation16(p0: Ptr[sqlite3], zName: Ptr[Byte], eTextRep: CInt, pArg: Ptr[Byte], xCompare: CFuncPtr5[Ptr[Byte], CInt, Ptr[Byte], CInt, Ptr[Byte], CInt]): CInt = extern
  def sqlite3_collation_needed(p0: Ptr[sqlite3], p1: Ptr[Byte], p2: CFuncPtr4[Ptr[Byte], Ptr[sqlite3], CInt, CString, Unit]): CInt = extern
  def sqlite3_collation_needed16(p0: Ptr[sqlite3], p1: Ptr[Byte], p2: CFuncPtr4[Ptr[Byte], Ptr[sqlite3], CInt, Ptr[Byte], Unit]): CInt = extern
  def sqlite3_sleep(p0: CInt): CInt = extern
  def sqlite3_get_autocommit(p0: Ptr[sqlite3]): CInt = extern
  def sqlite3_db_handle(p0: Ptr[sqlite3_stmt]): Ptr[sqlite3] = extern
  def sqlite3_db_filename(db: Ptr[sqlite3], zDbName: CString): CString = extern
  def sqlite3_db_readonly(db: Ptr[sqlite3], zDbName: CString): CInt = extern
  def sqlite3_next_stmt(pDb: Ptr[sqlite3], pStmt: Ptr[sqlite3_stmt]): Ptr[sqlite3_stmt] = extern
  def sqlite3_commit_hook(p0: Ptr[sqlite3], p1: CFuncPtr1[Ptr[Byte], CInt], p2: Ptr[Byte]): Ptr[Byte] = extern
  def sqlite3_rollback_hook(p0: Ptr[sqlite3], p1: CFuncPtr1[Ptr[Byte], Unit], p2: Ptr[Byte]): Ptr[Byte] = extern
  def sqlite3_update_hook(p0: Ptr[sqlite3], p1: CFuncPtr5[Ptr[Byte], CInt, CString, CString, sqlite3_int64, Unit], p2: Ptr[Byte]): Ptr[Byte] = extern
  def sqlite3_enable_shared_cache(p0: CInt): CInt = extern
  def sqlite3_release_memory(p0: CInt): CInt = extern
  def sqlite3_db_release_memory(p0: Ptr[sqlite3]): CInt = extern
  def sqlite3_soft_heap_limit64(N: sqlite3_int64): sqlite3_int64 = extern
  def sqlite3_soft_heap_limit(N: CInt): Unit = extern
  def sqlite3_table_column_metadata(db: Ptr[sqlite3], zDbName: CString, zTableName: CString, zColumnName: CString, pzDataType: Ptr[CString], pzCollSeq: Ptr[CString], pNotNull: Ptr[CInt], pPrimaryKey: Ptr[CInt], pAutoinc: Ptr[CInt]): CInt = extern
  def sqlite3_load_extension(db: Ptr[sqlite3], zFile: CString, zProc: CString, pzErrMsg: Ptr[CString]): CInt = extern
  def sqlite3_enable_load_extension(db: Ptr[sqlite3], onoff: CInt): CInt = extern
  def sqlite3_auto_extension(xEntryPoint: CFuncPtr0[Unit]): CInt = extern
  def sqlite3_cancel_auto_extension(xEntryPoint: CFuncPtr0[Unit]): CInt = extern
  def sqlite3_reset_auto_extension(): Unit = extern
  // FIXME: see https://github.com/scala-native/scala-native/issues/637 (DBO)
  //def sqlite3_create_module(db: Ptr[sqlite3], zName: CString, p: Ptr[sqlite3_module], pClientData: Ptr[Byte]): CInt = extern
  //def sqlite3_create_module_v2(db: Ptr[sqlite3], zName: CString, p: Ptr[sqlite3_module], pClientData: Ptr[Byte], xDestroy: CFuncPtr1[Ptr[Byte], Unit]): CInt = extern
  def sqlite3_declare_vtab(p0: Ptr[sqlite3], zSQL: CString): CInt = extern
  def sqlite3_overload_function(p0: Ptr[sqlite3], zFuncName: CString, nArg: CInt): CInt = extern
  def sqlite3_blob_open(p0: Ptr[sqlite3], zDb: CString, zTable: CString, zColumn: CString, iRow: sqlite3_int64, flags: CInt, ppBlob: Ptr[Ptr[sqlite3_blob]]): CInt = extern
  def sqlite3_blob_reopen(p0: Ptr[sqlite3_blob], p1: sqlite3_int64): CInt = extern
  def sqlite3_blob_close(p0: Ptr[sqlite3_blob]): CInt = extern
  def sqlite3_blob_bytes(p0: Ptr[sqlite3_blob]): CInt = extern
  def sqlite3_blob_read(p0: Ptr[sqlite3_blob], Z: Ptr[Byte], N: CInt, iOffset: CInt): CInt = extern
  def sqlite3_blob_write(p0: Ptr[sqlite3_blob], z: Ptr[Byte], n: CInt, iOffset: CInt): CInt = extern
  def sqlite3_vfs_find(zVfsName: CString): Ptr[sqlite3_vfs] = extern
  def sqlite3_vfs_register(p0: Ptr[sqlite3_vfs], makeDflt: CInt): CInt = extern
  def sqlite3_vfs_unregister(p0: Ptr[sqlite3_vfs]): CInt = extern
  def sqlite3_mutex_alloc(p0: CInt): Ptr[sqlite3_mutex] = extern
  def sqlite3_mutex_free(p0: Ptr[sqlite3_mutex]): Unit = extern
  def sqlite3_mutex_enter(p0: Ptr[sqlite3_mutex]): Unit = extern
  def sqlite3_mutex_try(p0: Ptr[sqlite3_mutex]): CInt = extern
  def sqlite3_mutex_leave(p0: Ptr[sqlite3_mutex]): Unit = extern
  def sqlite3_mutex_held(p0: Ptr[sqlite3_mutex]): CInt = extern
  def sqlite3_mutex_notheld(p0: Ptr[sqlite3_mutex]): CInt = extern
  def sqlite3_db_mutex(p0: Ptr[sqlite3]): Ptr[sqlite3_mutex] = extern
  def sqlite3_file_control(p0: Ptr[sqlite3], zDbName: CString, op: CInt, p1: Ptr[Byte]): CInt = extern
  def sqlite3_test_control(op: CInt, varArgs: CVarArg*): CInt = extern
  def sqlite3_status(op: CInt, pCurrent: Ptr[CInt], pHighwater: Ptr[CInt], resetFlag: CInt): CInt = extern
  def sqlite3_status64(op: CInt, pCurrent: Ptr[sqlite3_int64], pHighwater: Ptr[sqlite3_int64], resetFlag: CInt): CInt = extern
  def sqlite3_db_status(p0: Ptr[sqlite3], op: CInt, pCur: Ptr[CInt], pHiwtr: Ptr[CInt], resetFlg: CInt): CInt = extern
  def sqlite3_stmt_status(p0: Ptr[sqlite3_stmt], op: CInt, resetFlg: CInt): CInt = extern
  def sqlite3_backup_init(pDest: Ptr[sqlite3], zDestName: CString, pSource: Ptr[sqlite3], zSourceName: CString): Ptr[sqlite3_backup] = extern
  def sqlite3_backup_step(p: Ptr[sqlite3_backup], nPage: CInt): CInt = extern
  def sqlite3_backup_finish(p: Ptr[sqlite3_backup]): CInt = extern
  def sqlite3_backup_remaining(p: Ptr[sqlite3_backup]): CInt = extern
  def sqlite3_backup_pagecount(p: Ptr[sqlite3_backup]): CInt = extern
  def sqlite3_unlock_notify(pBlocked: Ptr[sqlite3], xNotify: CFuncPtr2[Ptr[Ptr[Byte]], CInt, Unit], pNotifyArg: Ptr[Byte]): CInt = extern
  def sqlite3_stricmp(p0: CString, p1: CString): CInt = extern
  def sqlite3_strnicmp(p0: CString, p1: CString, p2: CInt): CInt = extern
  def sqlite3_strglob(zGlob: CString, zStr: CString): CInt = extern
  def sqlite3_strlike(zGlob: CString, zStr: CString, cEsc: CUnsignedInt): CInt = extern
  def sqlite3_log(iErrCode: CInt, zFormat: CString, varArgs: CVarArg*): Unit = extern
  def sqlite3_wal_hook(p0: Ptr[sqlite3], p1: CFuncPtr4[Ptr[Byte], Ptr[sqlite3], CString, CInt, CInt], p2: Ptr[Byte]): Ptr[Byte] = extern
  def sqlite3_wal_autocheckpoint(db: Ptr[sqlite3], N: CInt): CInt = extern
  def sqlite3_wal_checkpoint(db: Ptr[sqlite3], zDb: CString): CInt = extern
  def sqlite3_wal_checkpoint_v2(db: Ptr[sqlite3], zDb: CString, eMode: CInt, pnLog: Ptr[CInt], pnCkpt: Ptr[CInt]): CInt = extern
  def sqlite3_vtab_config(p0: Ptr[sqlite3], op: CInt, varArgs: CVarArg*): CInt = extern
  def sqlite3_vtab_on_conflict(p0: Ptr[sqlite3]): CInt = extern
  def sqlite3_vtab_nochange(p0: Ptr[sqlite3_context]): CInt = extern
  def sqlite3_vtab_collation(p0: Ptr[sqlite3_index_info], p1: CInt): CString = extern
  def sqlite3_stmt_scanstatus(pStmt: Ptr[sqlite3_stmt], idx: CInt, iScanStatusOp: CInt, pOut: Ptr[Byte]): CInt = extern
  def sqlite3_stmt_scanstatus_reset(p0: Ptr[sqlite3_stmt]): Unit = extern
  def sqlite3_db_cacheflush(p0: Ptr[sqlite3]): CInt = extern
  def sqlite3_system_errno(p0: Ptr[sqlite3]): CInt = extern
  def sqlite3_snapshot_get(db: Ptr[sqlite3], zSchema: CString, ppSnapshot: Ptr[Ptr[sqlite3_snapshot]]): CInt = extern
  def sqlite3_snapshot_open(db: Ptr[sqlite3], zSchema: CString, pSnapshot: Ptr[sqlite3_snapshot]): CInt = extern
  def sqlite3_snapshot_free(p0: Ptr[sqlite3_snapshot]): Unit = extern
  def sqlite3_snapshot_cmp(p1: Ptr[sqlite3_snapshot], p2: Ptr[sqlite3_snapshot]): CInt = extern
  def sqlite3_snapshot_recover(db: Ptr[sqlite3], zDb: CString): CInt = extern
  def sqlite3_rtree_geometry_callback(db: Ptr[sqlite3], zGeom: CString, xGeom: CFuncPtr4[Ptr[sqlite3_rtree_geometry], CInt, Ptr[sqlite3_rtree_dbl], Ptr[CInt], CInt], pContext: Ptr[Byte]): CInt = extern
  def sqlite3_rtree_query_callback(db: Ptr[sqlite3], zQueryFunc: CString, xQueryFunc: CFuncPtr1[Ptr[sqlite3_rtree_query_info], CInt], pContext: Ptr[Byte], xDestructor: CFuncPtr1[Ptr[Byte], Unit]): CInt = extern
}
object SQLITE_CONSTANT {
  val SQLITE_VERSION: CString = c"3.22.0"
  val SQLITE_VERSION_NUMBER: CInt = 3022000
  val SQLITE_SOURCE_ID: CString = c"2018-01-22 18:45:57 0c55d179733b46d8d0ba4d88e01a25e10677046ee3da1d5b1581e86726f2alt1"
  val SQLITE_OK: CInt = 0
  val SQLITE_ERROR: CInt = 1
  val SQLITE_INTERNAL: CInt = 2
  val SQLITE_PERM: CInt = 3
  val SQLITE_ABORT: CInt = 4
  val SQLITE_BUSY: CInt = 5
  val SQLITE_LOCKED: CInt = 6
  val SQLITE_NOMEM: CInt = 7
  val SQLITE_READONLY: CInt = 8
  val SQLITE_INTERRUPT: CInt = 9
  val SQLITE_IOERR: CInt = 10
  val SQLITE_CORRUPT: CInt = 11
  val SQLITE_NOTFOUND: CInt = 12
  val SQLITE_FULL: CInt = 13
  val SQLITE_CANTOPEN: CInt = 14
  val SQLITE_PROTOCOL: CInt = 15
  val SQLITE_EMPTY: CInt = 16
  val SQLITE_SCHEMA: CInt = 17
  val SQLITE_TOOBIG: CInt = 18
  val SQLITE_CONSTRAINT: CInt = 19
  val SQLITE_MISMATCH: CInt = 20
  val SQLITE_MISUSE: CInt = 21
  val SQLITE_NOLFS: CInt = 22
  val SQLITE_AUTH: CInt = 23
  val SQLITE_FORMAT: CInt = 24
  val SQLITE_RANGE: CInt = 25
  val SQLITE_NOTADB: CInt = 26
  val SQLITE_NOTICE: CInt = 27
  val SQLITE_WARNING: CInt = 28
  val SQLITE_ROW: CInt = 100
  val SQLITE_DONE: CInt = 101
  val SQLITE_OPEN_READONLY: CInt = 1
  val SQLITE_OPEN_READWRITE: CInt = 2
  val SQLITE_OPEN_CREATE: CInt = 4
  val SQLITE_OPEN_DELETEONCLOSE: CInt = 8
  val SQLITE_OPEN_EXCLUSIVE: CInt = 16
  val SQLITE_OPEN_AUTOPROXY: CInt = 32
  val SQLITE_OPEN_URI: CInt = 64
  val SQLITE_OPEN_MEMORY: CInt = 128
  val SQLITE_OPEN_MAIN_DB: CInt = 256
  val SQLITE_OPEN_TEMP_DB: CInt = 512
  val SQLITE_OPEN_TRANSIENT_DB: CInt = 1024
  val SQLITE_OPEN_MAIN_JOURNAL: CInt = 2048
  val SQLITE_OPEN_TEMP_JOURNAL: CInt = 4096
  val SQLITE_OPEN_SUBJOURNAL: CInt = 8192
  val SQLITE_OPEN_MASTER_JOURNAL: CInt = 16384
  val SQLITE_OPEN_NOMUTEX: CInt = 32768
  val SQLITE_OPEN_FULLMUTEX: CInt = 65536
  val SQLITE_OPEN_SHAREDCACHE: CInt = 131072
  val SQLITE_OPEN_PRIVATECACHE: CInt = 262144
  val SQLITE_OPEN_WAL: CInt = 524288
  val SQLITE_IOCAP_ATOMIC: CInt = 1
  val SQLITE_IOCAP_ATOMIC512: CInt = 2
  val SQLITE_IOCAP_ATOMIC1K: CInt = 4
  val SQLITE_IOCAP_ATOMIC2K: CInt = 8
  val SQLITE_IOCAP_ATOMIC4K: CInt = 16
  val SQLITE_IOCAP_ATOMIC8K: CInt = 32
  val SQLITE_IOCAP_ATOMIC16K: CInt = 64
  val SQLITE_IOCAP_ATOMIC32K: CInt = 128
  val SQLITE_IOCAP_ATOMIC64K: CInt = 256
  val SQLITE_IOCAP_SAFE_APPEND: CInt = 512
  val SQLITE_IOCAP_SEQUENTIAL: CInt = 1024
  val SQLITE_IOCAP_UNDELETABLE_WHEN_OPEN: CInt = 2048
  val SQLITE_IOCAP_POWERSAFE_OVERWRITE: CInt = 4096
  val SQLITE_IOCAP_IMMUTABLE: CInt = 8192
  val SQLITE_IOCAP_BATCH_ATOMIC: CInt = 16384
  val SQLITE_LOCK_NONE: CInt = 0
  val SQLITE_LOCK_SHARED: CInt = 1
  val SQLITE_LOCK_RESERVED: CInt = 2
  val SQLITE_LOCK_PENDING: CInt = 3
  val SQLITE_LOCK_EXCLUSIVE: CInt = 4
  val SQLITE_SYNC_NORMAL: CInt = 2
  val SQLITE_SYNC_FULL: CInt = 3
  val SQLITE_SYNC_DATAONLY: CInt = 16
  val SQLITE_FCNTL_LOCKSTATE: CInt = 1
  val SQLITE_FCNTL_GET_LOCKPROXYFILE: CInt = 2
  val SQLITE_FCNTL_SET_LOCKPROXYFILE: CInt = 3
  val SQLITE_FCNTL_LAST_ERRNO: CInt = 4
  val SQLITE_FCNTL_SIZE_HINT: CInt = 5
  val SQLITE_FCNTL_CHUNK_SIZE: CInt = 6
  val SQLITE_FCNTL_FILE_POINTER: CInt = 7
  val SQLITE_FCNTL_SYNC_OMITTED: CInt = 8
  val SQLITE_FCNTL_WIN32_AV_RETRY: CInt = 9
  val SQLITE_FCNTL_PERSIST_WAL: CInt = 10
  val SQLITE_FCNTL_OVERWRITE: CInt = 11
  val SQLITE_FCNTL_VFSNAME: CInt = 12
  val SQLITE_FCNTL_POWERSAFE_OVERWRITE: CInt = 13
  val SQLITE_FCNTL_PRAGMA: CInt = 14
  val SQLITE_FCNTL_BUSYHANDLER: CInt = 15
  val SQLITE_FCNTL_TEMPFILENAME: CInt = 16
  val SQLITE_FCNTL_MMAP_SIZE: CInt = 18
  val SQLITE_FCNTL_TRACE: CInt = 19
  val SQLITE_FCNTL_HAS_MOVED: CInt = 20
  val SQLITE_FCNTL_SYNC: CInt = 21
  val SQLITE_FCNTL_COMMIT_PHASETWO: CInt = 22
  val SQLITE_FCNTL_WIN32_SET_HANDLE: CInt = 23
  val SQLITE_FCNTL_WAL_BLOCK: CInt = 24
  val SQLITE_FCNTL_ZIPVFS: CInt = 25
  val SQLITE_FCNTL_RBU: CInt = 26
  val SQLITE_FCNTL_VFS_POINTER: CInt = 27
  val SQLITE_FCNTL_JOURNAL_POINTER: CInt = 28
  val SQLITE_FCNTL_WIN32_GET_HANDLE: CInt = 29
  val SQLITE_FCNTL_PDB: CInt = 30
  val SQLITE_FCNTL_BEGIN_ATOMIC_WRITE: CInt = 31
  val SQLITE_FCNTL_COMMIT_ATOMIC_WRITE: CInt = 32
  val SQLITE_FCNTL_ROLLBACK_ATOMIC_WRITE: CInt = 33
  val SQLITE_GET_LOCKPROXYFILE: CInt = 2
  val SQLITE_SET_LOCKPROXYFILE: CInt = 3
  val SQLITE_LAST_ERRNO: CInt = 4
  val SQLITE_ACCESS_EXISTS: CInt = 0
  val SQLITE_ACCESS_READWRITE: CInt = 1
  val SQLITE_ACCESS_READ: CInt = 2
  val SQLITE_SHM_UNLOCK: CInt = 1
  val SQLITE_SHM_LOCK: CInt = 2
  val SQLITE_SHM_SHARED: CInt = 4
  val SQLITE_SHM_EXCLUSIVE: CInt = 8
  val SQLITE_SHM_NLOCK: CInt = 8
  val SQLITE_CONFIG_SINGLETHREAD: CInt = 1
  val SQLITE_CONFIG_MULTITHREAD: CInt = 2
  val SQLITE_CONFIG_SERIALIZED: CInt = 3
  val SQLITE_CONFIG_MALLOC: CInt = 4
  val SQLITE_CONFIG_GETMALLOC: CInt = 5
  val SQLITE_CONFIG_SCRATCH: CInt = 6
  val SQLITE_CONFIG_PAGECACHE: CInt = 7
  val SQLITE_CONFIG_HEAP: CInt = 8
  val SQLITE_CONFIG_MEMSTATUS: CInt = 9
  val SQLITE_CONFIG_MUTEX: CInt = 10
  val SQLITE_CONFIG_GETMUTEX: CInt = 11
  val SQLITE_CONFIG_LOOKASIDE: CInt = 13
  val SQLITE_CONFIG_PCACHE: CInt = 14
  val SQLITE_CONFIG_GETPCACHE: CInt = 15
  val SQLITE_CONFIG_LOG: CInt = 16
  val SQLITE_CONFIG_URI: CInt = 17
  val SQLITE_CONFIG_PCACHE2: CInt = 18
  val SQLITE_CONFIG_GETPCACHE2: CInt = 19
  val SQLITE_CONFIG_COVERING_INDEX_SCAN: CInt = 20
  val SQLITE_CONFIG_SQLLOG: CInt = 21
  val SQLITE_CONFIG_MMAP_SIZE: CInt = 22
  val SQLITE_CONFIG_WIN32_HEAPSIZE: CInt = 23
  val SQLITE_CONFIG_PCACHE_HDRSZ: CInt = 24
  val SQLITE_CONFIG_PMASZ: CInt = 25
  val SQLITE_CONFIG_STMTJRNL_SPILL: CInt = 26
  val SQLITE_CONFIG_SMALL_MALLOC: CInt = 27
  val SQLITE_DBCONFIG_MAINDBNAME: CInt = 1000
  val SQLITE_DBCONFIG_LOOKASIDE: CInt = 1001
  val SQLITE_DBCONFIG_ENABLE_FKEY: CInt = 1002
  val SQLITE_DBCONFIG_ENABLE_TRIGGER: CInt = 1003
  val SQLITE_DBCONFIG_ENABLE_FTS3_TOKENIZER: CInt = 1004
  val SQLITE_DBCONFIG_ENABLE_LOAD_EXTENSION: CInt = 1005
  val SQLITE_DBCONFIG_NO_CKPT_ON_CLOSE: CInt = 1006
  val SQLITE_DBCONFIG_ENABLE_QPSG: CInt = 1007
  val SQLITE_DBCONFIG_TRIGGER_EQP: CInt = 1008
  val SQLITE_DBCONFIG_MAX: CInt = 1008
  val SQLITE_DENY: CInt = 1
  val SQLITE_IGNORE: CInt = 2
  val SQLITE_CREATE_INDEX: CInt = 1
  val SQLITE_CREATE_TABLE: CInt = 2
  val SQLITE_CREATE_TEMP_INDEX: CInt = 3
  val SQLITE_CREATE_TEMP_TABLE: CInt = 4
  val SQLITE_CREATE_TEMP_TRIGGER: CInt = 5
  val SQLITE_CREATE_TEMP_VIEW: CInt = 6
  val SQLITE_CREATE_TRIGGER: CInt = 7
  val SQLITE_CREATE_VIEW: CInt = 8
  val SQLITE_DELETE: CInt = 9
  val SQLITE_DROP_INDEX: CInt = 10
  val SQLITE_DROP_TABLE: CInt = 11
  val SQLITE_DROP_TEMP_INDEX: CInt = 12
  val SQLITE_DROP_TEMP_TABLE: CInt = 13
  val SQLITE_DROP_TEMP_TRIGGER: CInt = 14
  val SQLITE_DROP_TEMP_VIEW: CInt = 15
  val SQLITE_DROP_TRIGGER: CInt = 16
  val SQLITE_DROP_VIEW: CInt = 17
  val SQLITE_INSERT: CInt = 18
  val SQLITE_PRAGMA: CInt = 19
  val SQLITE_READ: CInt = 20
  val SQLITE_SELECT: CInt = 21
  val SQLITE_TRANSACTION: CInt = 22
  val SQLITE_UPDATE: CInt = 23
  val SQLITE_ATTACH: CInt = 24
  val SQLITE_DETACH: CInt = 25
  val SQLITE_ALTER_TABLE: CInt = 26
  val SQLITE_REINDEX: CInt = 27
  val SQLITE_ANALYZE: CInt = 28
  val SQLITE_CREATE_VTABLE: CInt = 29
  val SQLITE_DROP_VTABLE: CInt = 30
  val SQLITE_FUNCTION: CInt = 31
  val SQLITE_SAVEPOINT: CInt = 32
  val SQLITE_COPY: CInt = 0
  val SQLITE_RECURSIVE: CInt = 33
  val SQLITE_TRACE_STMT: CInt = 1
  val SQLITE_TRACE_PROFILE: CInt = 2
  val SQLITE_TRACE_ROW: CInt = 4
  val SQLITE_TRACE_CLOSE: CInt = 8
  val SQLITE_LIMIT_LENGTH: CInt = 0
  val SQLITE_LIMIT_SQL_LENGTH: CInt = 1
  val SQLITE_LIMIT_COLUMN: CInt = 2
  val SQLITE_LIMIT_EXPR_DEPTH: CInt = 3
  val SQLITE_LIMIT_COMPOUND_SELECT: CInt = 4
  val SQLITE_LIMIT_VDBE_OP: CInt = 5
  val SQLITE_LIMIT_FUNCTION_ARG: CInt = 6
  val SQLITE_LIMIT_ATTACHED: CInt = 7
  val SQLITE_LIMIT_LIKE_PATTERN_LENGTH: CInt = 8
  val SQLITE_LIMIT_VARIABLE_NUMBER: CInt = 9
  val SQLITE_LIMIT_TRIGGER_DEPTH: CInt = 10
  val SQLITE_LIMIT_WORKER_THREADS: CInt = 11
  val SQLITE_PREPARE_PERSISTENT: CInt = 1
  val SQLITE_INTEGER: CInt = 1
  val SQLITE_FLOAT: CInt = 2
  val SQLITE_BLOB: CInt = 4
  val SQLITE_NULL: CInt = 5
  val SQLITE_TEXT: CInt = 3
  val SQLITE3_TEXT: CInt = 3
  val SQLITE_UTF8: CInt = 1
  val SQLITE_UTF16LE: CInt = 2
  val SQLITE_UTF16BE: CInt = 3
  val SQLITE_UTF16: CInt = 4
  val SQLITE_ANY: CInt = 5
  val SQLITE_UTF16_ALIGNED: CInt = 8
  val SQLITE_DETERMINISTIC: CInt = 2048
  val SQLITE_INDEX_SCAN_UNIQUE: CInt = 1
  val SQLITE_INDEX_CONSTRAINT_EQ: CInt = 2
  val SQLITE_INDEX_CONSTRAINT_GT: CInt = 4
  val SQLITE_INDEX_CONSTRAINT_LE: CInt = 8
  val SQLITE_INDEX_CONSTRAINT_LT: CInt = 16
  val SQLITE_INDEX_CONSTRAINT_GE: CInt = 32
  val SQLITE_INDEX_CONSTRAINT_MATCH: CInt = 64
  val SQLITE_INDEX_CONSTRAINT_LIKE: CInt = 65
  val SQLITE_INDEX_CONSTRAINT_GLOB: CInt = 66
  val SQLITE_INDEX_CONSTRAINT_REGEXP: CInt = 67
  val SQLITE_INDEX_CONSTRAINT_NE: CInt = 68
  val SQLITE_INDEX_CONSTRAINT_ISNOT: CInt = 69
  val SQLITE_INDEX_CONSTRAINT_ISNOTNULL: CInt = 70
  val SQLITE_INDEX_CONSTRAINT_ISNULL: CInt = 71
  val SQLITE_INDEX_CONSTRAINT_IS: CInt = 72
  val SQLITE_MUTEX_FAST: CInt = 0
  val SQLITE_MUTEX_RECURSIVE: CInt = 1
  val SQLITE_MUTEX_STATIC_MASTER: CInt = 2
  val SQLITE_MUTEX_STATIC_MEM: CInt = 3
  val SQLITE_MUTEX_STATIC_MEM2: CInt = 4
  val SQLITE_MUTEX_STATIC_OPEN: CInt = 4
  val SQLITE_MUTEX_STATIC_PRNG: CInt = 5
  val SQLITE_MUTEX_STATIC_LRU: CInt = 6
  val SQLITE_MUTEX_STATIC_LRU2: CInt = 7
  val SQLITE_MUTEX_STATIC_PMEM: CInt = 7
  val SQLITE_MUTEX_STATIC_APP1: CInt = 8
  val SQLITE_MUTEX_STATIC_APP2: CInt = 9
  val SQLITE_MUTEX_STATIC_APP3: CInt = 10
  val SQLITE_MUTEX_STATIC_VFS1: CInt = 11
  val SQLITE_MUTEX_STATIC_VFS2: CInt = 12
  val SQLITE_MUTEX_STATIC_VFS3: CInt = 13
  val SQLITE_TESTCTRL_FIRST: CInt = 5
  val SQLITE_TESTCTRL_PRNG_SAVE: CInt = 5
  val SQLITE_TESTCTRL_PRNG_RESTORE: CInt = 6
  val SQLITE_TESTCTRL_PRNG_RESET: CInt = 7
  val SQLITE_TESTCTRL_BITVEC_TEST: CInt = 8
  val SQLITE_TESTCTRL_FAULT_INSTALL: CInt = 9
  val SQLITE_TESTCTRL_BENIGN_MALLOC_HOOKS: CInt = 10
  val SQLITE_TESTCTRL_PENDING_BYTE: CInt = 11
  val SQLITE_TESTCTRL_ASSERT: CInt = 12
  val SQLITE_TESTCTRL_ALWAYS: CInt = 13
  val SQLITE_TESTCTRL_RESERVE: CInt = 14
  val SQLITE_TESTCTRL_OPTIMIZATIONS: CInt = 15
  val SQLITE_TESTCTRL_ISKEYWORD: CInt = 16
  val SQLITE_TESTCTRL_SCRATCHMALLOC: CInt = 17
  val SQLITE_TESTCTRL_LOCALTIME_FAULT: CInt = 18
  val SQLITE_TESTCTRL_EXPLAIN_STMT: CInt = 19
  val SQLITE_TESTCTRL_ONCE_RESET_THRESHOLD: CInt = 19
  val SQLITE_TESTCTRL_NEVER_CORRUPT: CInt = 20
  val SQLITE_TESTCTRL_VDBE_COVERAGE: CInt = 21
  val SQLITE_TESTCTRL_BYTEORDER: CInt = 22
  val SQLITE_TESTCTRL_ISINIT: CInt = 23
  val SQLITE_TESTCTRL_SORTER_MMAP: CInt = 24
  val SQLITE_TESTCTRL_IMPOSTER: CInt = 25
  val SQLITE_TESTCTRL_PARSER_COVERAGE: CInt = 26
  val SQLITE_TESTCTRL_LAST: CInt = 26
  val SQLITE_STATUS_MEMORY_USED: CInt = 0
  val SQLITE_STATUS_PAGECACHE_USED: CInt = 1
  val SQLITE_STATUS_PAGECACHE_OVERFLOW: CInt = 2
  val SQLITE_STATUS_SCRATCH_USED: CInt = 3
  val SQLITE_STATUS_SCRATCH_OVERFLOW: CInt = 4
  val SQLITE_STATUS_MALLOC_SIZE: CInt = 5
  val SQLITE_STATUS_PARSER_STACK: CInt = 6
  val SQLITE_STATUS_PAGECACHE_SIZE: CInt = 7
  val SQLITE_STATUS_SCRATCH_SIZE: CInt = 8
  val SQLITE_STATUS_MALLOC_COUNT: CInt = 9
  val SQLITE_DBSTATUS_LOOKASIDE_USED: CInt = 0
  val SQLITE_DBSTATUS_CACHE_USED: CInt = 1
  val SQLITE_DBSTATUS_SCHEMA_USED: CInt = 2
  val SQLITE_DBSTATUS_STMT_USED: CInt = 3
  val SQLITE_DBSTATUS_LOOKASIDE_HIT: CInt = 4
  val SQLITE_DBSTATUS_LOOKASIDE_MISS_SIZE: CInt = 5
  val SQLITE_DBSTATUS_LOOKASIDE_MISS_FULL: CInt = 6
  val SQLITE_DBSTATUS_CACHE_HIT: CInt = 7
  val SQLITE_DBSTATUS_CACHE_MISS: CInt = 8
  val SQLITE_DBSTATUS_CACHE_WRITE: CInt = 9
  val SQLITE_DBSTATUS_DEFERRED_FKS: CInt = 10
  val SQLITE_DBSTATUS_CACHE_USED_SHARED: CInt = 11
  val SQLITE_DBSTATUS_MAX: CInt = 11
  val SQLITE_STMTSTATUS_FULLSCAN_STEP: CInt = 1
  val SQLITE_STMTSTATUS_SORT: CInt = 2
  val SQLITE_STMTSTATUS_AUTOINDEX: CInt = 3
  val SQLITE_STMTSTATUS_VM_STEP: CInt = 4
  val SQLITE_STMTSTATUS_REPREPARE: CInt = 5
  val SQLITE_STMTSTATUS_RUN: CInt = 6
  val SQLITE_STMTSTATUS_MEMUSED: CInt = 99
  val SQLITE_CHECKPOINT_PASSIVE: CInt = 0
  val SQLITE_CHECKPOINT_FULL: CInt = 1
  val SQLITE_CHECKPOINT_RESTART: CInt = 2
  val SQLITE_CHECKPOINT_TRUNCATE: CInt = 3
  val SQLITE_VTAB_CONSTRAINT_SUPPORT: CInt = 1
  val SQLITE_ROLLBACK: CInt = 1
  val SQLITE_FAIL: CInt = 3
  val SQLITE_REPLACE: CInt = 5
  val SQLITE_SCANSTAT_NLOOP: CInt = 0
  val SQLITE_SCANSTAT_NVISIT: CInt = 1
  val SQLITE_SCANSTAT_EST: CInt = 2
  val SQLITE_SCANSTAT_NAME: CInt = 3
  val SQLITE_SCANSTAT_EXPLAIN: CInt = 4
  val SQLITE_SCANSTAT_SELECTID: CInt = 5
  val NOT_WITHIN: CInt = 0
  val PARTLY_WITHIN: CInt = 1
  val FULLY_WITHIN: CInt = 2
  val FTS5_TOKENIZE_QUERY: CInt = 1
  val FTS5_TOKENIZE_PREFIX: CInt = 2
  val FTS5_TOKENIZE_DOCUMENT: CInt = 4
  val FTS5_TOKENIZE_AUX: CInt = 8
  val FTS5_TOKEN_COLOCATED: CInt = 1
}

object implicits {
  import sqlite._
  implicit class struct_sqlite3_file_ops(val p: Ptr[struct_sqlite3_file]) extends AnyVal {
    def pMethods: Ptr[struct_sqlite3_io_methods] = p._1
    def pMethods_=(value: Ptr[struct_sqlite3_io_methods]): Unit = p._1 = value
  }
  def struct_sqlite3_file()(implicit z: Zone): Ptr[struct_sqlite3_file] = alloc[struct_sqlite3_file]()

  implicit class struct_sqlite3_io_methods_ops(val p: Ptr[struct_sqlite3_io_methods]) extends AnyVal {
    def iVersion: CInt = p._1
    def iVersion_=(value: CInt): Unit = p._1 = value
    def xClose: CFuncPtr1[Ptr[sqlite3_file], CInt] = p._2.asInstanceOf[CFuncPtr1[Ptr[sqlite3_file], CInt]]
    def xClose_=(value: CFuncPtr1[Ptr[sqlite3_file], CInt]): Unit = p._2 = value.asInstanceOf[CFuncPtr1[Ptr[Byte], CInt]]
    def xRead: CFuncPtr4[Ptr[sqlite3_file], Ptr[Byte], CInt, sqlite3_int64, CInt] = p._3.asInstanceOf[CFuncPtr4[Ptr[sqlite3_file], Ptr[Byte], CInt, sqlite3_int64, CInt]]
    def xRead_=(value: CFuncPtr4[Ptr[sqlite3_file], Ptr[Byte], CInt, sqlite3_int64, CInt]): Unit = p._3 = value.asInstanceOf[CFuncPtr4[Ptr[Byte], Ptr[Byte], CInt, CLongLong, CInt]]
    def xWrite: CFuncPtr4[Ptr[sqlite3_file], Ptr[Byte], CInt, sqlite3_int64, CInt] = p._4.asInstanceOf[CFuncPtr4[Ptr[sqlite3_file], Ptr[Byte], CInt, sqlite3_int64, CInt]]
    def xWrite_=(value: CFuncPtr4[Ptr[sqlite3_file], Ptr[Byte], CInt, sqlite3_int64, CInt]): Unit = p._4 = value.asInstanceOf[CFuncPtr4[Ptr[Byte], Ptr[Byte], CInt, CLongLong, CInt]]
    def xTruncate: CFuncPtr2[Ptr[sqlite3_file], sqlite3_int64, CInt] = p._5.asInstanceOf[CFuncPtr2[Ptr[sqlite3_file], sqlite3_int64, CInt]]
    def xTruncate_=(value: CFuncPtr2[Ptr[sqlite3_file], sqlite3_int64, CInt]): Unit = p._5 = value.asInstanceOf[CFuncPtr2[Ptr[Byte], CLongLong, CInt]]
    def xSync: CFuncPtr2[Ptr[sqlite3_file], CInt, CInt] = p._6.asInstanceOf[CFuncPtr2[Ptr[sqlite3_file], CInt, CInt]]
    def xSync_=(value: CFuncPtr2[Ptr[sqlite3_file], CInt, CInt]): Unit = p._6 = value.asInstanceOf[CFuncPtr2[Ptr[Byte], CInt, CInt]]
    def xFileSize: CFuncPtr2[Ptr[sqlite3_file], Ptr[sqlite3_int64], CInt] = p._7.asInstanceOf[CFuncPtr2[Ptr[sqlite3_file], Ptr[sqlite3_int64], CInt]]
    def xFileSize_=(value: CFuncPtr2[Ptr[sqlite3_file], Ptr[sqlite3_int64], CInt]): Unit = p._7 = value.asInstanceOf[CFuncPtr2[Ptr[Byte], Ptr[CLongLong], CInt]]
    def xLock: CFuncPtr2[Ptr[sqlite3_file], CInt, CInt] = p._8.asInstanceOf[CFuncPtr2[Ptr[sqlite3_file], CInt, CInt]]
    def xLock_=(value: CFuncPtr2[Ptr[sqlite3_file], CInt, CInt]): Unit = p._8 = value.asInstanceOf[CFuncPtr2[Ptr[Byte], CInt, CInt]]
    def xUnlock: CFuncPtr2[Ptr[sqlite3_file], CInt, CInt] = p._9.asInstanceOf[CFuncPtr2[Ptr[sqlite3_file], CInt, CInt]]
    def xUnlock_=(value: CFuncPtr2[Ptr[sqlite3_file], CInt, CInt]): Unit = p._9 = value.asInstanceOf[CFuncPtr2[Ptr[Byte], CInt, CInt]]
    def xCheckReservedLock: CFuncPtr2[Ptr[sqlite3_file], Ptr[CInt], CInt] = p._10.asInstanceOf[CFuncPtr2[Ptr[sqlite3_file], Ptr[CInt], CInt]]
    def xCheckReservedLock_=(value: CFuncPtr2[Ptr[sqlite3_file], Ptr[CInt], CInt]): Unit = p._10 = value.asInstanceOf[CFuncPtr2[Ptr[Byte], Ptr[CInt], CInt]]
    def xFileControl: CFuncPtr3[Ptr[sqlite3_file], CInt, Ptr[Byte], CInt] = p._11.asInstanceOf[CFuncPtr3[Ptr[sqlite3_file], CInt, Ptr[Byte], CInt]]
    def xFileControl_=(value: CFuncPtr3[Ptr[sqlite3_file], CInt, Ptr[Byte], CInt]): Unit = p._11 = value.asInstanceOf[CFuncPtr3[Ptr[Byte], CInt, Ptr[Byte], CInt]]
    def xSectorSize: CFuncPtr1[Ptr[sqlite3_file], CInt] = p._12.asInstanceOf[CFuncPtr1[Ptr[sqlite3_file], CInt]]
    def xSectorSize_=(value: CFuncPtr1[Ptr[sqlite3_file], CInt]): Unit = p._12 = value.asInstanceOf[CFuncPtr1[Ptr[Byte], CInt]]
    def xDeviceCharacteristics: CFuncPtr1[Ptr[sqlite3_file], CInt] = p._13.asInstanceOf[CFuncPtr1[Ptr[sqlite3_file], CInt]]
    def xDeviceCharacteristics_=(value: CFuncPtr1[Ptr[sqlite3_file], CInt]): Unit = p._13 = value.asInstanceOf[CFuncPtr1[Ptr[Byte], CInt]]
    def xShmMap: CFuncPtr5[Ptr[sqlite3_file], CInt, CInt, CInt, Ptr[Ptr[Byte]], CInt] = p._14.asInstanceOf[CFuncPtr5[Ptr[sqlite3_file], CInt, CInt, CInt, Ptr[Ptr[Byte]], CInt]]
    def xShmMap_=(value: CFuncPtr5[Ptr[sqlite3_file], CInt, CInt, CInt, Ptr[Ptr[Byte]], CInt]): Unit = p._14 = value.asInstanceOf[CFuncPtr5[Ptr[Byte], CInt, CInt, CInt, Ptr[Ptr[Byte]], CInt]]
    def xShmLock: CFuncPtr4[Ptr[sqlite3_file], CInt, CInt, CInt, CInt] = p._15.asInstanceOf[CFuncPtr4[Ptr[sqlite3_file], CInt, CInt, CInt, CInt]]
    def xShmLock_=(value: CFuncPtr4[Ptr[sqlite3_file], CInt, CInt, CInt, CInt]): Unit = p._15 = value.asInstanceOf[CFuncPtr4[Ptr[Byte], CInt, CInt, CInt, CInt]]
    def xShmBarrier: CFuncPtr1[Ptr[sqlite3_file], Unit] = p._16.asInstanceOf[CFuncPtr1[Ptr[sqlite3_file], Unit]]
    def xShmBarrier_=(value: CFuncPtr1[Ptr[sqlite3_file], Unit]): Unit = p._16 = value.asInstanceOf[CFuncPtr1[Ptr[Byte], Unit]]
    def xShmUnmap: CFuncPtr2[Ptr[sqlite3_file], CInt, CInt] = p._17.asInstanceOf[CFuncPtr2[Ptr[sqlite3_file], CInt, CInt]]
    def xShmUnmap_=(value: CFuncPtr2[Ptr[sqlite3_file], CInt, CInt]): Unit = p._17 = value.asInstanceOf[CFuncPtr2[Ptr[Byte], CInt, CInt]]
    def xFetch: CFuncPtr4[Ptr[sqlite3_file], sqlite3_int64, CInt, Ptr[Ptr[Byte]], CInt] = p._18.asInstanceOf[CFuncPtr4[Ptr[sqlite3_file], sqlite3_int64, CInt, Ptr[Ptr[Byte]], CInt]]
    def xFetch_=(value: CFuncPtr4[Ptr[sqlite3_file], sqlite3_int64, CInt, Ptr[Ptr[Byte]], CInt]): Unit = p._18 = value.asInstanceOf[CFuncPtr4[Ptr[Byte], CLongLong, CInt, Ptr[Ptr[Byte]], CInt]]
    def xUnfetch: CFuncPtr3[Ptr[sqlite3_file], sqlite3_int64, Ptr[Byte], CInt] = p._19.asInstanceOf[CFuncPtr3[Ptr[sqlite3_file], sqlite3_int64, Ptr[Byte], CInt]]
    def xUnfetch_=(value: CFuncPtr3[Ptr[sqlite3_file], sqlite3_int64, Ptr[Byte], CInt]): Unit = p._19 = value.asInstanceOf[CFuncPtr3[Ptr[Byte], CLongLong, Ptr[Byte], CInt]]
  }
  def struct_sqlite3_io_methods()(implicit z: Zone): Ptr[struct_sqlite3_io_methods] = alloc[struct_sqlite3_io_methods]()

  implicit class struct_sqlite3_vfs_ops(val p: Ptr[struct_sqlite3_vfs]) extends AnyVal {
    def iVersion: CInt = p._1
    def iVersion_=(value: CInt): Unit = p._1 = value
    def szOsFile: CInt = p._2
    def szOsFile_=(value: CInt): Unit = p._2 = value
    def mxPathname: CInt = p._3
    def mxPathname_=(value: CInt): Unit = p._3 = value
    def pNext: Ptr[sqlite3_vfs] = p._4.asInstanceOf[Ptr[sqlite3_vfs]]
    def pNext_=(value: Ptr[sqlite3_vfs]): Unit = p._4 = value.asInstanceOf[Ptr[Byte]]
    def zName: CString = p._5
    def zName_=(value: CString): Unit = p._5 = value
    def pAppData: Ptr[Byte] = p._6
    def pAppData_=(value: Ptr[Byte]): Unit = p._6 = value
    def xOpen: CFuncPtr5[Ptr[sqlite3_vfs], CString, Ptr[sqlite3_file], CInt, Ptr[CInt], CInt] = p._7.asInstanceOf[CFuncPtr5[Ptr[sqlite3_vfs], CString, Ptr[sqlite3_file], CInt, Ptr[CInt], CInt]]
    def xOpen_=(value: CFuncPtr5[Ptr[sqlite3_vfs], CString, Ptr[sqlite3_file], CInt, Ptr[CInt], CInt]): Unit = p._7 = value.asInstanceOf[CFuncPtr5[Ptr[Byte], CString, Ptr[struct_sqlite3_file], CInt, Ptr[CInt], CInt]]
    def xDelete: CFuncPtr3[Ptr[sqlite3_vfs], CString, CInt, CInt] = p._8.asInstanceOf[CFuncPtr3[Ptr[sqlite3_vfs], CString, CInt, CInt]]
    def xDelete_=(value: CFuncPtr3[Ptr[sqlite3_vfs], CString, CInt, CInt]): Unit = p._8 = value.asInstanceOf[CFuncPtr3[Ptr[Byte], CString, CInt, CInt]]
    def xAccess: CFuncPtr4[Ptr[sqlite3_vfs], CString, CInt, Ptr[CInt], CInt] = p._9.asInstanceOf[CFuncPtr4[Ptr[sqlite3_vfs], CString, CInt, Ptr[CInt], CInt]]
    def xAccess_=(value: CFuncPtr4[Ptr[sqlite3_vfs], CString, CInt, Ptr[CInt], CInt]): Unit = p._9 = value.asInstanceOf[CFuncPtr4[Ptr[Byte], CString, CInt, Ptr[CInt], CInt]]
    def xFullPathname: CFuncPtr4[Ptr[sqlite3_vfs], CString, CInt, CString, CInt] = p._10.asInstanceOf[CFuncPtr4[Ptr[sqlite3_vfs], CString, CInt, CString, CInt]]
    def xFullPathname_=(value: CFuncPtr4[Ptr[sqlite3_vfs], CString, CInt, CString, CInt]): Unit = p._10 = value.asInstanceOf[CFuncPtr4[Ptr[Byte], CString, CInt, CString, CInt]]
    def xDlOpen: CFuncPtr2[Ptr[sqlite3_vfs], CString, Ptr[Byte]] = p._11.asInstanceOf[CFuncPtr2[Ptr[sqlite3_vfs], CString, Ptr[Byte]]]
    def xDlOpen_=(value: CFuncPtr2[Ptr[sqlite3_vfs], CString, Ptr[Byte]]): Unit = p._11 = value.asInstanceOf[CFuncPtr2[Ptr[Byte], CString, Ptr[Byte]]]
    def xDlError: CFuncPtr3[Ptr[sqlite3_vfs], CInt, CString, Unit] = p._12.asInstanceOf[CFuncPtr3[Ptr[sqlite3_vfs], CInt, CString, Unit]]
    def xDlError_=(value: CFuncPtr3[Ptr[sqlite3_vfs], CInt, CString, Unit]): Unit = p._12 = value.asInstanceOf[CFuncPtr3[Ptr[Byte], CInt, CString, Unit]]
    def xDlSym: CFuncPtr3[Ptr[sqlite3_vfs], Ptr[Byte], CString, CFuncPtr0[Unit]] = p._13.asInstanceOf[CFuncPtr3[Ptr[sqlite3_vfs], Ptr[Byte], CString, CFuncPtr0[Unit]]]
    def xDlSym_=(value: CFuncPtr3[Ptr[sqlite3_vfs], Ptr[Byte], CString, CFuncPtr0[Unit]]): Unit = p._13 = value.asInstanceOf[CFuncPtr3[Ptr[Byte], Ptr[Byte], CString, CFuncPtr0[Unit]]]
    def xDlClose: CFuncPtr2[Ptr[sqlite3_vfs], Ptr[Byte], Unit] = p._14.asInstanceOf[CFuncPtr2[Ptr[sqlite3_vfs], Ptr[Byte], Unit]]
    def xDlClose_=(value: CFuncPtr2[Ptr[sqlite3_vfs], Ptr[Byte], Unit]): Unit = p._14 = value.asInstanceOf[CFuncPtr2[Ptr[Byte], Ptr[Byte], Unit]]
    def xRandomness: CFuncPtr3[Ptr[sqlite3_vfs], CInt, CString, CInt] = p._15.asInstanceOf[CFuncPtr3[Ptr[sqlite3_vfs], CInt, CString, CInt]]
    def xRandomness_=(value: CFuncPtr3[Ptr[sqlite3_vfs], CInt, CString, CInt]): Unit = p._15 = value.asInstanceOf[CFuncPtr3[Ptr[Byte], CInt, CString, CInt]]
    def xSleep: CFuncPtr2[Ptr[sqlite3_vfs], CInt, CInt] = p._16.asInstanceOf[CFuncPtr2[Ptr[sqlite3_vfs], CInt, CInt]]
    def xSleep_=(value: CFuncPtr2[Ptr[sqlite3_vfs], CInt, CInt]): Unit = p._16 = value.asInstanceOf[CFuncPtr2[Ptr[Byte], CInt, CInt]]
    def xCurrentTime: CFuncPtr2[Ptr[sqlite3_vfs], Ptr[CDouble], CInt] = p._17.asInstanceOf[CFuncPtr2[Ptr[sqlite3_vfs], Ptr[CDouble], CInt]]
    def xCurrentTime_=(value: CFuncPtr2[Ptr[sqlite3_vfs], Ptr[CDouble], CInt]): Unit = p._17 = value.asInstanceOf[CFuncPtr2[Ptr[Byte], Ptr[CDouble], CInt]]
    def xGetLastError: CFuncPtr3[Ptr[sqlite3_vfs], CInt, CString, CInt] = p._18.asInstanceOf[CFuncPtr3[Ptr[sqlite3_vfs], CInt, CString, CInt]]
    def xGetLastError_=(value: CFuncPtr3[Ptr[sqlite3_vfs], CInt, CString, CInt]): Unit = p._18 = value.asInstanceOf[CFuncPtr3[Ptr[Byte], CInt, CString, CInt]]
    def xCurrentTimeInt64: CFuncPtr2[Ptr[sqlite3_vfs], Ptr[sqlite3_int64], CInt] = p._19.asInstanceOf[CFuncPtr2[Ptr[sqlite3_vfs], Ptr[sqlite3_int64], CInt]]
    def xCurrentTimeInt64_=(value: CFuncPtr2[Ptr[sqlite3_vfs], Ptr[sqlite3_int64], CInt]): Unit = p._19 = value.asInstanceOf[CFuncPtr2[Ptr[Byte], Ptr[CLongLong], CInt]]
    def xSetSystemCall: CFuncPtr3[Ptr[sqlite3_vfs], CString, CFuncPtr0[Unit], CInt] = p._20.asInstanceOf[CFuncPtr3[Ptr[sqlite3_vfs], CString, CFuncPtr0[Unit], CInt]]
    def xSetSystemCall_=(value: CFuncPtr3[Ptr[sqlite3_vfs], CString, CFuncPtr0[Unit], CInt]): Unit = p._20 = value.asInstanceOf[CFuncPtr3[Ptr[Byte], CString, CFuncPtr0[Unit], CInt]]
    def xGetSystemCall: CFuncPtr2[Ptr[sqlite3_vfs], CString, CFuncPtr0[Unit]] = p._21.asInstanceOf[CFuncPtr2[Ptr[sqlite3_vfs], CString, CFuncPtr0[Unit]]]
    def xGetSystemCall_=(value: CFuncPtr2[Ptr[sqlite3_vfs], CString, CFuncPtr0[Unit]]): Unit = p._21 = value.asInstanceOf[CFuncPtr2[Ptr[Byte], CString, CFuncPtr0[Unit]]]
    def xNextSystemCall: CFuncPtr2[Ptr[sqlite3_vfs], CString, CString] = p._22.asInstanceOf[CFuncPtr2[Ptr[sqlite3_vfs], CString, CString]]
    def xNextSystemCall_=(value: CFuncPtr2[Ptr[sqlite3_vfs], CString, CString]): Unit = p._22 = value.asInstanceOf[CFuncPtr2[Ptr[Byte], CString, CString]]
  }
  def struct_sqlite3_vfs()(implicit z: Zone): Ptr[struct_sqlite3_vfs] = alloc[struct_sqlite3_vfs]()

  implicit class struct_sqlite3_mem_methods_ops(val p: Ptr[struct_sqlite3_mem_methods]) extends AnyVal {
    def xMalloc: CFuncPtr1[CInt, Ptr[Byte]] = p._1
    def xMalloc_=(value: CFuncPtr1[CInt, Ptr[Byte]]): Unit = p._1 = value
    def xFree: CFuncPtr1[Ptr[Byte], Unit] = p._2
    def xFree_=(value: CFuncPtr1[Ptr[Byte], Unit]): Unit = p._2 = value
    def xRealloc: CFuncPtr2[Ptr[Byte], CInt, Ptr[Byte]] = p._3
    def xRealloc_=(value: CFuncPtr2[Ptr[Byte], CInt, Ptr[Byte]]): Unit = p._3 = value
    def xSize: CFuncPtr1[Ptr[Byte], CInt] = p._4
    def xSize_=(value: CFuncPtr1[Ptr[Byte], CInt]): Unit = p._4 = value
    def xRoundup: CFuncPtr1[CInt, CInt] = p._5
    def xRoundup_=(value: CFuncPtr1[CInt, CInt]): Unit = p._5 = value
    def xInit: CFuncPtr1[Ptr[Byte], CInt] = p._6
    def xInit_=(value: CFuncPtr1[Ptr[Byte], CInt]): Unit = p._6 = value
    def xShutdown: CFuncPtr1[Ptr[Byte], Unit] = p._7
    def xShutdown_=(value: CFuncPtr1[Ptr[Byte], Unit]): Unit = p._7 = value
    def pAppData: Ptr[Byte] = p._8
    def pAppData_=(value: Ptr[Byte]): Unit = p._8 = value
  }
  def struct_sqlite3_mem_methods()(implicit z: Zone): Ptr[struct_sqlite3_mem_methods] = alloc[struct_sqlite3_mem_methods]()

  // FIXME: https://github.com/scala-native/scala-native/issues/637 (DBO)
  /*
  implicit class struct_sqlite3_module_ops(val p: Ptr[struct_sqlite3_module]) extends AnyVal {
    def iVersion: CInt = p._1.asInstanceOf[Ptr[CInt]]
    def iVersion_=(value: CInt): Unit = p._1.asInstanceOf[Ptr[CInt]] = value
    def xCreate: CFuncPtr6[Ptr[sqlite3], Ptr[Byte], CInt, Ptr[CString], Ptr[Ptr[sqlite3_vtab]], Ptr[CString], CInt] = (p._1 + 8).asInstanceOf[Ptr[CFuncPtr6[Ptr[sqlite3], Ptr[Byte], CInt, Ptr[CString], Ptr[Ptr[sqlite3_vtab]], Ptr[CString], CInt]]]
    def xCreate_=(value: CFuncPtr6[Ptr[sqlite3], Ptr[Byte], CInt, Ptr[CString], Ptr[Ptr[sqlite3_vtab]], Ptr[CString], CInt]): Unit = (p._1 + 8).asInstanceOf[Ptr[CFuncPtr6[Ptr[sqlite3], Ptr[Byte], CInt, Ptr[CString], Ptr[Ptr[sqlite3_vtab]], Ptr[CString], CInt]]] = value
    def xConnect: CFuncPtr6[Ptr[sqlite3], Ptr[Byte], CInt, Ptr[CString], Ptr[Ptr[sqlite3_vtab]], Ptr[CString], CInt] = (p._1 + 16).asInstanceOf[Ptr[CFuncPtr6[Ptr[sqlite3], Ptr[Byte], CInt, Ptr[CString], Ptr[Ptr[sqlite3_vtab]], Ptr[CString], CInt]]]
    def xConnect_=(value: CFuncPtr6[Ptr[sqlite3], Ptr[Byte], CInt, Ptr[CString], Ptr[Ptr[sqlite3_vtab]], Ptr[CString], CInt]): Unit = (p._1 + 16).asInstanceOf[Ptr[CFuncPtr6[Ptr[sqlite3], Ptr[Byte], CInt, Ptr[CString], Ptr[Ptr[sqlite3_vtab]], Ptr[CString], CInt]]] = value
    def xBestIndex: CFuncPtr2[Ptr[sqlite3_vtab], Ptr[sqlite3_index_info], CInt] = (p._1 + 24).asInstanceOf[Ptr[CFuncPtr2[Ptr[sqlite3_vtab], Ptr[sqlite3_index_info], CInt]]]
    def xBestIndex_=(value: CFuncPtr2[Ptr[sqlite3_vtab], Ptr[sqlite3_index_info], CInt]): Unit = (p._1 + 24).asInstanceOf[Ptr[CFuncPtr2[Ptr[sqlite3_vtab], Ptr[sqlite3_index_info], CInt]]] = value
    def xDisconnect: CFuncPtr1[Ptr[sqlite3_vtab], CInt] = (p._1 + 32).asInstanceOf[Ptr[CFuncPtr1[Ptr[sqlite3_vtab], CInt]]]
    def xDisconnect_=(value: CFuncPtr1[Ptr[sqlite3_vtab], CInt]): Unit = (p._1 + 32).asInstanceOf[Ptr[CFuncPtr1[Ptr[sqlite3_vtab], CInt]]] = value
    def xDestroy: CFuncPtr1[Ptr[sqlite3_vtab], CInt] = (p._1 + 40).asInstanceOf[Ptr[CFuncPtr1[Ptr[sqlite3_vtab], CInt]]]
    def xDestroy_=(value: CFuncPtr1[Ptr[sqlite3_vtab], CInt]): Unit = (p._1 + 40).asInstanceOf[Ptr[CFuncPtr1[Ptr[sqlite3_vtab], CInt]]] = value
    def xOpen: CFuncPtr2[Ptr[sqlite3_vtab], Ptr[Ptr[sqlite3_vtab_cursor]], CInt] = (p._1 + 48).asInstanceOf[Ptr[CFuncPtr2[Ptr[sqlite3_vtab], Ptr[Ptr[sqlite3_vtab_cursor]], CInt]]]
    def xOpen_=(value: CFuncPtr2[Ptr[sqlite3_vtab], Ptr[Ptr[sqlite3_vtab_cursor]], CInt]): Unit = (p._1 + 48).asInstanceOf[Ptr[CFuncPtr2[Ptr[sqlite3_vtab], Ptr[Ptr[sqlite3_vtab_cursor]], CInt]]] = value
    def xClose: CFuncPtr1[Ptr[sqlite3_vtab_cursor], CInt] = (p._1 + 56).asInstanceOf[Ptr[CFuncPtr1[Ptr[sqlite3_vtab_cursor], CInt]]]
    def xClose_=(value: CFuncPtr1[Ptr[sqlite3_vtab_cursor], CInt]): Unit = (p._1 + 56).asInstanceOf[Ptr[CFuncPtr1[Ptr[sqlite3_vtab_cursor], CInt]]] = value
    def xFilter: CFuncPtr5[Ptr[sqlite3_vtab_cursor], CInt, CString, CInt, Ptr[Ptr[sqlite3_value]], CInt] = (p._1 + 64).asInstanceOf[Ptr[CFuncPtr5[Ptr[sqlite3_vtab_cursor], CInt, CString, CInt, Ptr[Ptr[sqlite3_value]], CInt]]]
    def xFilter_=(value: CFuncPtr5[Ptr[sqlite3_vtab_cursor], CInt, CString, CInt, Ptr[Ptr[sqlite3_value]], CInt]): Unit = (p._1 + 64).asInstanceOf[Ptr[CFuncPtr5[Ptr[sqlite3_vtab_cursor], CInt, CString, CInt, Ptr[Ptr[sqlite3_value]], CInt]]] = value
    def xNext: CFuncPtr1[Ptr[sqlite3_vtab_cursor], CInt] = (p._1 + 72).asInstanceOf[Ptr[CFuncPtr1[Ptr[sqlite3_vtab_cursor], CInt]]]
    def xNext_=(value: CFuncPtr1[Ptr[sqlite3_vtab_cursor], CInt]): Unit = (p._1 + 72).asInstanceOf[Ptr[CFuncPtr1[Ptr[sqlite3_vtab_cursor], CInt]]] = value
    def xEof: CFuncPtr1[Ptr[sqlite3_vtab_cursor], CInt] = (p._1 + 80).asInstanceOf[Ptr[CFuncPtr1[Ptr[sqlite3_vtab_cursor], CInt]]]
    */
  /*
    def xEof_=(value: CFuncPtr1[Ptr[sqlite3_vtab_cursor], CInt]): Unit = (p._1 + 80).asInstanceOf[Ptr[CFuncPtr1[Ptr[sqlite3_vtab_cursor], CInt]]] = value
    def xColumn: CFuncPtr3[Ptr[sqlite3_vtab_cursor], Ptr[sqlite3_context], CInt, CInt] = (p._1 + 88).asInstanceOf[Ptr[CFuncPtr3[Ptr[sqlite3_vtab_cursor], Ptr[sqlite3_context], CInt, CInt]]]
    def xColumn_=(value: CFuncPtr3[Ptr[sqlite3_vtab_cursor], Ptr[sqlite3_context], CInt, CInt]): Unit = (p._1 + 88).asInstanceOf[Ptr[CFuncPtr3[Ptr[sqlite3_vtab_cursor], Ptr[sqlite3_context], CInt, CInt]]] = value
    def xRowid: CFuncPtr2[Ptr[sqlite3_vtab_cursor], Ptr[sqlite3_int64], CInt] = (p._1 + 96).asInstanceOf[Ptr[CFuncPtr2[Ptr[sqlite3_vtab_cursor], Ptr[sqlite3_int64], CInt]]]
    def xRowid_=(value: CFuncPtr2[Ptr[sqlite3_vtab_cursor], Ptr[sqlite3_int64], CInt]): Unit = (p._1 + 96).asInstanceOf[Ptr[CFuncPtr2[Ptr[sqlite3_vtab_cursor], Ptr[sqlite3_int64], CInt]]] = value
    def xUpdate: CFuncPtr4[Ptr[sqlite3_vtab], CInt, Ptr[Ptr[sqlite3_value]], Ptr[sqlite3_int64], CInt] = (p._1 + 104).asInstanceOf[Ptr[CFuncPtr4[Ptr[sqlite3_vtab], CInt, Ptr[Ptr[sqlite3_value]], Ptr[sqlite3_int64], CInt]]]
    def xUpdate_=(value: CFuncPtr4[Ptr[sqlite3_vtab], CInt, Ptr[Ptr[sqlite3_value]], Ptr[sqlite3_int64], CInt]): Unit = (p._1 + 104).asInstanceOf[Ptr[CFuncPtr4[Ptr[sqlite3_vtab], CInt, Ptr[Ptr[sqlite3_value]], Ptr[sqlite3_int64], CInt]]] = value
    def xBegin: CFuncPtr1[Ptr[sqlite3_vtab], CInt] = (p._1 + 112).asInstanceOf[Ptr[CFuncPtr1[Ptr[sqlite3_vtab], CInt]]]
    def xBegin_=(value: CFuncPtr1[Ptr[sqlite3_vtab], CInt]): Unit = (p._1 + 112).asInstanceOf[Ptr[CFuncPtr1[Ptr[sqlite3_vtab], CInt]]] = value
    def xSync: CFuncPtr1[Ptr[sqlite3_vtab], CInt] = (p._1 + 120).asInstanceOf[Ptr[CFuncPtr1[Ptr[sqlite3_vtab], CInt]]]
    def xSync_=(value: CFuncPtr1[Ptr[sqlite3_vtab], CInt]): Unit = (p._1 + 120).asInstanceOf[Ptr[CFuncPtr1[Ptr[sqlite3_vtab], CInt]]] = value
    def xCommit: CFuncPtr1[Ptr[sqlite3_vtab], CInt] = (p._1 + 128).asInstanceOf[Ptr[CFuncPtr1[Ptr[sqlite3_vtab], CInt]]]
    def xCommit_=(value: CFuncPtr1[Ptr[sqlite3_vtab], CInt]): Unit = (p._1 + 128).asInstanceOf[Ptr[CFuncPtr1[Ptr[sqlite3_vtab], CInt]]] = value
    def xRollback: CFuncPtr1[Ptr[sqlite3_vtab], CInt] = (p._1 + 136).asInstanceOf[Ptr[CFuncPtr1[Ptr[sqlite3_vtab], CInt]]]
    def xRollback_=(value: CFuncPtr1[Ptr[sqlite3_vtab], CInt]): Unit = (p._1 + 136).asInstanceOf[Ptr[CFuncPtr1[Ptr[sqlite3_vtab], CInt]]] = value
    def xFindFunction: CFuncPtr5[Ptr[sqlite3_vtab], CInt, CString, Ptr[CFuncPtr3[Ptr[sqlite3_context], CInt, Ptr[Ptr[sqlite3_value]], Unit]], Ptr[Ptr[Byte]], CInt] = (p._1 + 144).asInstanceOf[Ptr[CFuncPtr5[Ptr[sqlite3_vtab], CInt, CString, Ptr[CFuncPtr3[Ptr[sqlite3_context], CInt, Ptr[Ptr[sqlite3_value]], Unit]], Ptr[Ptr[Byte]], CInt]]]
    def xFindFunction_=(value: CFuncPtr5[Ptr[sqlite3_vtab], CInt, CString, Ptr[CFuncPtr3[Ptr[sqlite3_context], CInt, Ptr[Ptr[sqlite3_value]], Unit]], Ptr[Ptr[Byte]], CInt]): Unit = (p._1 + 144).asInstanceOf[Ptr[CFuncPtr5[Ptr[sqlite3_vtab], CInt, CString, Ptr[CFuncPtr3[Ptr[sqlite3_context], CInt, Ptr[Ptr[sqlite3_value]], Unit]], Ptr[Ptr[Byte]], CInt]]] = value
    def xRename: CFuncPtr2[Ptr[sqlite3_vtab], CString, CInt] = (p._1 + 152).asInstanceOf[Ptr[CFuncPtr2[Ptr[sqlite3_vtab], CString, CInt]]]
    def xRename_=(value: CFuncPtr2[Ptr[sqlite3_vtab], CString, CInt]): Unit = (p._1 + 152).asInstanceOf[Ptr[CFuncPtr2[Ptr[sqlite3_vtab], CString, CInt]]] = value
    def xSavepoint: CFuncPtr2[Ptr[sqlite3_vtab], CInt, CInt] = (p._1 + 160).asInstanceOf[Ptr[CFuncPtr2[Ptr[sqlite3_vtab], CInt, CInt]]]
    def xSavepoint_=(value: CFuncPtr2[Ptr[sqlite3_vtab], CInt, CInt]): Unit = (p._1 + 160).asInstanceOf[Ptr[CFuncPtr2[Ptr[sqlite3_vtab], CInt, CInt]]] = value
    def xRelease: CFuncPtr2[Ptr[sqlite3_vtab], CInt, CInt] = (p._1 + 168).asInstanceOf[Ptr[CFuncPtr2[Ptr[sqlite3_vtab], CInt, CInt]]]
    def xRelease_=(value: CFuncPtr2[Ptr[sqlite3_vtab], CInt, CInt]): Unit = (p._1 + 168).asInstanceOf[Ptr[CFuncPtr2[Ptr[sqlite3_vtab], CInt, CInt]]] = value
    def xRollbackTo: CFuncPtr2[Ptr[sqlite3_vtab], CInt, CInt] = (p._1 + 176).asInstanceOf[Ptr[CFuncPtr2[Ptr[sqlite3_vtab], CInt, CInt]]]
    def xRollbackTo_=(value: CFuncPtr2[Ptr[sqlite3_vtab], CInt, CInt]): Unit = (p._1 + 176).asInstanceOf[Ptr[CFuncPtr2[Ptr[sqlite3_vtab], CInt, CInt]]] = value
  }
  def struct_sqlite3_module()(implicit z: Zone): Ptr[struct_sqlite3_module] = alloc[struct_sqlite3_module]()
  */

  implicit class struct_sqlite3_index_info_ops(val p: Ptr[struct_sqlite3_index_info]) extends AnyVal {
    def nConstraint: CInt = p._1
    def nConstraint_=(value: CInt): Unit = p._1 = value
    def aConstraint: Ptr[struct_sqlite3_index_constraint] = p._2
    def aConstraint_=(value: Ptr[struct_sqlite3_index_constraint]): Unit = p._2 = value
    def nOrderBy: CInt = p._3
    def nOrderBy_=(value: CInt): Unit = p._3 = value
    def aOrderBy: Ptr[struct_sqlite3_index_orderby] = p._4
    def aOrderBy_=(value: Ptr[struct_sqlite3_index_orderby]): Unit = p._4 = value
    def aConstraintUsage: Ptr[struct_sqlite3_index_constraint_usage] = p._5
    def aConstraintUsage_=(value: Ptr[struct_sqlite3_index_constraint_usage]): Unit = p._5 = value
    def idxNum: CInt = p._6
    def idxNum_=(value: CInt): Unit = p._6 = value
    def idxStr: CString = p._7
    def idxStr_=(value: CString): Unit = p._7 = value
    def needToFreeIdxStr: CInt = p._8
    def needToFreeIdxStr_=(value: CInt): Unit = p._8 = value
    def orderByConsumed: CInt = p._9
    def orderByConsumed_=(value: CInt): Unit = p._9 = value
    def estimatedCost: CDouble = p._10
    def estimatedCost_=(value: CDouble): Unit = p._10 = value
    def estimatedRows: sqlite3_int64 = p._11
    def estimatedRows_=(value: sqlite3_int64): Unit = p._11 = value
    def idxFlags: CInt = p._12
    def idxFlags_=(value: CInt): Unit = p._12 = value
    def colUsed: sqlite3_uint64 = p._13
    def colUsed_=(value: sqlite3_uint64): Unit = p._13 = value
  }
  def struct_sqlite3_index_info()(implicit z: Zone): Ptr[struct_sqlite3_index_info] = alloc[struct_sqlite3_index_info]()

  implicit class struct_sqlite3_index_constraint_ops(val p: Ptr[struct_sqlite3_index_constraint]) extends AnyVal {
    def iColumn: CInt = p._1
    def iColumn_=(value: CInt): Unit = p._1 = value
    def op: CUnsignedChar = p._2
    def op_=(value: CUnsignedChar): Unit = p._2 = value
    def usable: CUnsignedChar = p._3
    def usable_=(value: CUnsignedChar): Unit = p._3 = value
    def iTermOffset: CInt = p._4
    def iTermOffset_=(value: CInt): Unit = p._4 = value
  }
  def struct_sqlite3_index_constraint()(implicit z: Zone): Ptr[struct_sqlite3_index_constraint] = alloc[struct_sqlite3_index_constraint]()

  implicit class struct_sqlite3_index_orderby_ops(val p: Ptr[struct_sqlite3_index_orderby]) extends AnyVal {
    def iColumn: CInt = p._1
    def iColumn_=(value: CInt): Unit = p._1 = value
    def desc: CUnsignedChar = p._2
    def desc_=(value: CUnsignedChar): Unit = p._2 = value
  }
  def struct_sqlite3_index_orderby()(implicit z: Zone): Ptr[struct_sqlite3_index_orderby] = alloc[struct_sqlite3_index_orderby]()

  implicit class struct_sqlite3_index_constraint_usage_ops(val p: Ptr[struct_sqlite3_index_constraint_usage]) extends AnyVal {
    def argvIndex: CInt = p._1
    def argvIndex_=(value: CInt): Unit = p._1 = value
    def omit: CUnsignedChar = p._2
    def omit_=(value: CUnsignedChar): Unit = p._2 = value
  }
  def struct_sqlite3_index_constraint_usage()(implicit z: Zone): Ptr[struct_sqlite3_index_constraint_usage] = alloc[struct_sqlite3_index_constraint_usage]()

  // FIXME: https://github.com/scala-native/scala-native/issues/637 (DBO)
  /*implicit class struct_sqlite3_vtab_ops(val p: Ptr[struct_sqlite3_vtab]) extends AnyVal {
    def pModule: Ptr[sqlite3_module] = p._1.asInstanceOf[Ptr[sqlite3_module]]
    def pModule_=(value: Ptr[sqlite3_module]): Unit = p._1 = value.asInstanceOf[Ptr[Byte]]
    def nRef: CInt = p._2
    def nRef_=(value: CInt): Unit = p._2 = value
    def zErrMsg: CString = p._3
    def zErrMsg_=(value: CString): Unit = p._3 = value
  }
  def struct_sqlite3_vtab()(implicit z: Zone): Ptr[struct_sqlite3_vtab] = alloc[struct_sqlite3_vtab]()*/

  implicit class struct_sqlite3_vtab_cursor_ops(val p: Ptr[struct_sqlite3_vtab_cursor]) extends AnyVal {
    def pVtab: Ptr[sqlite3_vtab] = p._1.asInstanceOf[Ptr[sqlite3_vtab]]
    def pVtab_=(value: Ptr[sqlite3_vtab]): Unit = p._1 = value.asInstanceOf[Ptr[Byte]]
  }
  def struct_sqlite3_vtab_cursor()(implicit z: Zone): Ptr[struct_sqlite3_vtab_cursor] = alloc[struct_sqlite3_vtab_cursor]()

  implicit class struct_sqlite3_mutex_methods_ops(val p: Ptr[struct_sqlite3_mutex_methods]) extends AnyVal {
    def xMutexInit: CFuncPtr0[CInt] = p._1
    def xMutexInit_=(value: CFuncPtr0[CInt]): Unit = p._1 = value
    def xMutexEnd: CFuncPtr0[CInt] = p._2
    def xMutexEnd_=(value: CFuncPtr0[CInt]): Unit = p._2 = value
    def xMutexAlloc: CFuncPtr1[CInt, Ptr[sqlite3_mutex]] = p._3
    def xMutexAlloc_=(value: CFuncPtr1[CInt, Ptr[sqlite3_mutex]]): Unit = p._3 = value
    def xMutexFree: CFuncPtr1[Ptr[sqlite3_mutex], Unit] = p._4
    def xMutexFree_=(value: CFuncPtr1[Ptr[sqlite3_mutex], Unit]): Unit = p._4 = value
    def xMutexEnter: CFuncPtr1[Ptr[sqlite3_mutex], Unit] = p._5
    def xMutexEnter_=(value: CFuncPtr1[Ptr[sqlite3_mutex], Unit]): Unit = p._5 = value
    def xMutexTry: CFuncPtr1[Ptr[sqlite3_mutex], CInt] = p._6
    def xMutexTry_=(value: CFuncPtr1[Ptr[sqlite3_mutex], CInt]): Unit = p._6 = value
    def xMutexLeave: CFuncPtr1[Ptr[sqlite3_mutex], Unit] = p._7
    def xMutexLeave_=(value: CFuncPtr1[Ptr[sqlite3_mutex], Unit]): Unit = p._7 = value
    def xMutexHeld: CFuncPtr1[Ptr[sqlite3_mutex], CInt] = p._8
    def xMutexHeld_=(value: CFuncPtr1[Ptr[sqlite3_mutex], CInt]): Unit = p._8 = value
    def xMutexNotheld: CFuncPtr1[Ptr[sqlite3_mutex], CInt] = p._9
    def xMutexNotheld_=(value: CFuncPtr1[Ptr[sqlite3_mutex], CInt]): Unit = p._9 = value
  }
  def struct_sqlite3_mutex_methods()(implicit z: Zone): Ptr[struct_sqlite3_mutex_methods] = alloc[struct_sqlite3_mutex_methods]()

  implicit class struct_sqlite3_pcache_page_ops(val p: Ptr[struct_sqlite3_pcache_page]) extends AnyVal {
    def pBuf: Ptr[Byte] = p._1
    def pBuf_=(value: Ptr[Byte]): Unit = p._1 = value
    def pExtra: Ptr[Byte] = p._2
    def pExtra_=(value: Ptr[Byte]): Unit = p._2 = value
  }
  def struct_sqlite3_pcache_page()(implicit z: Zone): Ptr[struct_sqlite3_pcache_page] = alloc[struct_sqlite3_pcache_page]()

  implicit class struct_sqlite3_pcache_methods2_ops(val p: Ptr[struct_sqlite3_pcache_methods2]) extends AnyVal {
    def iVersion: CInt = p._1
    def iVersion_=(value: CInt): Unit = p._1 = value
    def pArg: Ptr[Byte] = p._2
    def pArg_=(value: Ptr[Byte]): Unit = p._2 = value
    def xInit: CFuncPtr1[Ptr[Byte], CInt] = p._3
    def xInit_=(value: CFuncPtr1[Ptr[Byte], CInt]): Unit = p._3 = value
    def xShutdown: CFuncPtr1[Ptr[Byte], Unit] = p._4
    def xShutdown_=(value: CFuncPtr1[Ptr[Byte], Unit]): Unit = p._4 = value
    def xCreate: CFuncPtr3[CInt, CInt, CInt, Ptr[sqlite3_pcache]] = p._5
    def xCreate_=(value: CFuncPtr3[CInt, CInt, CInt, Ptr[sqlite3_pcache]]): Unit = p._5 = value
    def xCachesize: CFuncPtr2[Ptr[sqlite3_pcache], CInt, Unit] = p._6
    def xCachesize_=(value: CFuncPtr2[Ptr[sqlite3_pcache], CInt, Unit]): Unit = p._6 = value
    def xPagecount: CFuncPtr1[Ptr[sqlite3_pcache], CInt] = p._7
    def xPagecount_=(value: CFuncPtr1[Ptr[sqlite3_pcache], CInt]): Unit = p._7 = value
    def xFetch: CFuncPtr3[Ptr[sqlite3_pcache], CUnsignedInt, CInt, Ptr[sqlite3_pcache_page]] = p._8
    def xFetch_=(value: CFuncPtr3[Ptr[sqlite3_pcache], CUnsignedInt, CInt, Ptr[sqlite3_pcache_page]]): Unit = p._8 = value
    def xUnpin: CFuncPtr3[Ptr[sqlite3_pcache], Ptr[sqlite3_pcache_page], CInt, Unit] = p._9
    def xUnpin_=(value: CFuncPtr3[Ptr[sqlite3_pcache], Ptr[sqlite3_pcache_page], CInt, Unit]): Unit = p._9 = value
    def xRekey: CFuncPtr4[Ptr[sqlite3_pcache], Ptr[sqlite3_pcache_page], CUnsignedInt, CUnsignedInt, Unit] = p._10
    def xRekey_=(value: CFuncPtr4[Ptr[sqlite3_pcache], Ptr[sqlite3_pcache_page], CUnsignedInt, CUnsignedInt, Unit]): Unit = p._10 = value
    def xTruncate: CFuncPtr2[Ptr[sqlite3_pcache], CUnsignedInt, Unit] = p._11
    def xTruncate_=(value: CFuncPtr2[Ptr[sqlite3_pcache], CUnsignedInt, Unit]): Unit = p._11 = value
    def xDestroy: CFuncPtr1[Ptr[sqlite3_pcache], Unit] = p._12
    def xDestroy_=(value: CFuncPtr1[Ptr[sqlite3_pcache], Unit]): Unit = p._12 = value
    def xShrink: CFuncPtr1[Ptr[sqlite3_pcache], Unit] = p._13
    def xShrink_=(value: CFuncPtr1[Ptr[sqlite3_pcache], Unit]): Unit = p._13 = value
  }
  def struct_sqlite3_pcache_methods2()(implicit z: Zone): Ptr[struct_sqlite3_pcache_methods2] = alloc[struct_sqlite3_pcache_methods2]()

  implicit class struct_sqlite3_pcache_methods_ops(val p: Ptr[struct_sqlite3_pcache_methods]) extends AnyVal {
    def pArg: Ptr[Byte] = p._1
    def pArg_=(value: Ptr[Byte]): Unit = p._1 = value
    def xInit: CFuncPtr1[Ptr[Byte], CInt] = p._2
    def xInit_=(value: CFuncPtr1[Ptr[Byte], CInt]): Unit = p._2 = value
    def xShutdown: CFuncPtr1[Ptr[Byte], Unit] = p._3
    def xShutdown_=(value: CFuncPtr1[Ptr[Byte], Unit]): Unit = p._3 = value
    def xCreate: CFuncPtr2[CInt, CInt, Ptr[sqlite3_pcache]] = p._4
    def xCreate_=(value: CFuncPtr2[CInt, CInt, Ptr[sqlite3_pcache]]): Unit = p._4 = value
    def xCachesize: CFuncPtr2[Ptr[sqlite3_pcache], CInt, Unit] = p._5
    def xCachesize_=(value: CFuncPtr2[Ptr[sqlite3_pcache], CInt, Unit]): Unit = p._5 = value
    def xPagecount: CFuncPtr1[Ptr[sqlite3_pcache], CInt] = p._6
    def xPagecount_=(value: CFuncPtr1[Ptr[sqlite3_pcache], CInt]): Unit = p._6 = value
    def xFetch: CFuncPtr3[Ptr[sqlite3_pcache], CUnsignedInt, CInt, Ptr[Byte]] = p._7
    def xFetch_=(value: CFuncPtr3[Ptr[sqlite3_pcache], CUnsignedInt, CInt, Ptr[Byte]]): Unit = p._7 = value
    def xUnpin: CFuncPtr3[Ptr[sqlite3_pcache], Ptr[Byte], CInt, Unit] = p._8
    def xUnpin_=(value: CFuncPtr3[Ptr[sqlite3_pcache], Ptr[Byte], CInt, Unit]): Unit = p._8 = value
    def xRekey: CFuncPtr4[Ptr[sqlite3_pcache], Ptr[Byte], CUnsignedInt, CUnsignedInt, Unit] = p._9
    def xRekey_=(value: CFuncPtr4[Ptr[sqlite3_pcache], Ptr[Byte], CUnsignedInt, CUnsignedInt, Unit]): Unit = p._9 = value
    def xTruncate: CFuncPtr2[Ptr[sqlite3_pcache], CUnsignedInt, Unit] = p._10
    def xTruncate_=(value: CFuncPtr2[Ptr[sqlite3_pcache], CUnsignedInt, Unit]): Unit = p._10 = value
    def xDestroy: CFuncPtr1[Ptr[sqlite3_pcache], Unit] = p._11
    def xDestroy_=(value: CFuncPtr1[Ptr[sqlite3_pcache], Unit]): Unit = p._11 = value
  }
  def struct_sqlite3_pcache_methods()(implicit z: Zone): Ptr[struct_sqlite3_pcache_methods] = alloc[struct_sqlite3_pcache_methods]()

  implicit class struct_sqlite3_snapshot_ops(val p: Ptr[struct_sqlite3_snapshot]) extends AnyVal {
    def hidden: Ptr[CArray[CUnsignedChar, Nat.Digit2[Nat._4, Nat._8]]] = p.at1
    def hidden_=(value: Ptr[CArray[CUnsignedChar, Nat.Digit2[Nat._4, Nat._8]]]): Unit = p._1 = value
  }
  def struct_sqlite3_snapshot()(implicit z: Zone): Ptr[struct_sqlite3_snapshot] = alloc[struct_sqlite3_snapshot]()

  implicit class struct_sqlite3_rtree_geometry_ops(val p: Ptr[struct_sqlite3_rtree_geometry]) extends AnyVal {
    def pContext: Ptr[Byte] = p._1
    def pContext_=(value: Ptr[Byte]): Unit = p._1 = value
    def nParam: CInt = p._2
    def nParam_=(value: CInt): Unit = p._2 = value
    def aParam: Ptr[sqlite3_rtree_dbl] = p._3
    def aParam_=(value: Ptr[sqlite3_rtree_dbl]): Unit = p._3 = value
    def pUser: Ptr[Byte] = p._4
    def pUser_=(value: Ptr[Byte]): Unit = p._4 = value
    def xDelUser: CFuncPtr1[Ptr[Byte], Unit] = p._5
    def xDelUser_=(value: CFuncPtr1[Ptr[Byte], Unit]): Unit = p._5 = value
  }
  def struct_sqlite3_rtree_geometry()(implicit z: Zone): Ptr[struct_sqlite3_rtree_geometry] = alloc[struct_sqlite3_rtree_geometry]()

  implicit class struct_sqlite3_rtree_query_info_ops(val p: Ptr[struct_sqlite3_rtree_query_info]) extends AnyVal {
    def pContext: Ptr[Byte] = p._1
    def pContext_=(value: Ptr[Byte]): Unit = p._1 = value
    def nParam: CInt = p._2
    def nParam_=(value: CInt): Unit = p._2 = value
    def aParam: Ptr[sqlite3_rtree_dbl] = p._3
    def aParam_=(value: Ptr[sqlite3_rtree_dbl]): Unit = p._3 = value
    def pUser: Ptr[Byte] = p._4
    def pUser_=(value: Ptr[Byte]): Unit = p._4 = value
    def xDelUser: CFuncPtr1[Ptr[Byte], Unit] = p._5
    def xDelUser_=(value: CFuncPtr1[Ptr[Byte], Unit]): Unit = p._5 = value
    def aCoord: Ptr[sqlite3_rtree_dbl] = p._6
    def aCoord_=(value: Ptr[sqlite3_rtree_dbl]): Unit = p._6 = value
    def anQueue: Ptr[CUnsignedInt] = p._7
    def anQueue_=(value: Ptr[CUnsignedInt]): Unit = p._7 = value
    def nCoord: CInt = p._8
    def nCoord_=(value: CInt): Unit = p._8 = value
    def iLevel: CInt = p._9
    def iLevel_=(value: CInt): Unit = p._9 = value
    def mxLevel: CInt = p._10
    def mxLevel_=(value: CInt): Unit = p._10 = value
    def iRowid: sqlite3_int64 = p._11
    def iRowid_=(value: sqlite3_int64): Unit = p._11 = value
    def rParentScore: sqlite3_rtree_dbl = p._12
    def rParentScore_=(value: sqlite3_rtree_dbl): Unit = p._12 = value
    def eParentWithin: CInt = p._13
    def eParentWithin_=(value: CInt): Unit = p._13 = value
    def eWithin: CInt = p._14
    def eWithin_=(value: CInt): Unit = p._14 = value
    def rScore: sqlite3_rtree_dbl = p._15
    def rScore_=(value: sqlite3_rtree_dbl): Unit = p._15 = value
    def apSqlParam: Ptr[Ptr[sqlite3_value]] = p._16
    def apSqlParam_=(value: Ptr[Ptr[sqlite3_value]]): Unit = p._16 = value
  }
  def struct_sqlite3_rtree_query_info()(implicit z: Zone): Ptr[struct_sqlite3_rtree_query_info] = alloc[struct_sqlite3_rtree_query_info]()

  implicit class struct_Fts5PhraseIter_ops(val p: Ptr[struct_Fts5PhraseIter]) extends AnyVal {
    def a: Ptr[CUnsignedChar] = p._1
    def a_=(value: Ptr[CUnsignedChar]): Unit = p._1 = value
    def b: Ptr[CUnsignedChar] = p._2
    def b_=(value: Ptr[CUnsignedChar]): Unit = p._2 = value
  }
  def struct_Fts5PhraseIter()(implicit z: Zone): Ptr[struct_Fts5PhraseIter] = alloc[struct_Fts5PhraseIter]()

  /*
  implicit class struct_Fts5ExtensionApi_ops(val p: Ptr[struct_Fts5ExtensionApi]) extends AnyVal {
    def iVersion: CInt = p._1
    def iVersion_=(value: CInt): Unit = p._1 = value
    def xUserData: CFuncPtr1[Ptr[Fts5Context], Ptr[Byte]] = p._2
    def xUserData_=(value: CFuncPtr1[Ptr[Fts5Context], Ptr[Byte]]): Unit = p._2 = value
    def xColumnCount: CFuncPtr1[Ptr[Fts5Context], CInt] = p._3
    def xColumnCount_=(value: CFuncPtr1[Ptr[Fts5Context], CInt]): Unit = p._3 = value
    def xRowCount: CFuncPtr2[Ptr[Fts5Context], Ptr[sqlite3_int64], CInt] = p._4
    def xRowCount_=(value: CFuncPtr2[Ptr[Fts5Context], Ptr[sqlite3_int64], CInt]): Unit = p._4 = value
    def xColumnTotalSize: CFuncPtr3[Ptr[Fts5Context], CInt, Ptr[sqlite3_int64], CInt] = p._5
    def xColumnTotalSize_=(value: CFuncPtr3[Ptr[Fts5Context], CInt, Ptr[sqlite3_int64], CInt]): Unit = p._5 = value
    def xTokenize: CFuncPtr5[Ptr[Fts5Context], CString, CInt, Ptr[Byte], CFuncPtr6[Ptr[Byte], CInt, CString, CInt, CInt, CInt, CInt], CInt] = p._6
    def xTokenize_=(value: CFuncPtr5[Ptr[Fts5Context], CString, CInt, Ptr[Byte], CFuncPtr6[Ptr[Byte], CInt, CString, CInt, CInt, CInt, CInt], CInt]): Unit = p._6 = value
    def xPhraseCount: CFuncPtr1[Ptr[Fts5Context], CInt] = p._7
    def xPhraseCount_=(value: CFuncPtr1[Ptr[Fts5Context], CInt]): Unit = p._7 = value
    def xPhraseSize: CFuncPtr2[Ptr[Fts5Context], CInt, CInt] = p._8
    def xPhraseSize_=(value: CFuncPtr2[Ptr[Fts5Context], CInt, CInt]): Unit = p._8 = value
    def xInstCount: CFuncPtr2[Ptr[Fts5Context], Ptr[CInt], CInt] = p._9
    def xInstCount_=(value: CFuncPtr2[Ptr[Fts5Context], Ptr[CInt], CInt]): Unit = p._9 = value
    def xInst: CFuncPtr5[Ptr[Fts5Context], CInt, Ptr[CInt], Ptr[CInt], Ptr[CInt], CInt] = p._10
    def xInst_=(value: CFuncPtr5[Ptr[Fts5Context], CInt, Ptr[CInt], Ptr[CInt], Ptr[CInt], CInt]): Unit = p._10 = value
    def xRowid: CFuncPtr1[Ptr[Fts5Context], sqlite3_int64] = p._11
    def xRowid_=(value: CFuncPtr1[Ptr[Fts5Context], sqlite3_int64]): Unit = p._11 = value
    def xColumnText: CFuncPtr4[Ptr[Fts5Context], CInt, Ptr[CString], Ptr[CInt], CInt] = p._12
    def xColumnText_=(value: CFuncPtr4[Ptr[Fts5Context], CInt, Ptr[CString], Ptr[CInt], CInt]): Unit = p._12 = value
    def xColumnSize: CFuncPtr3[Ptr[Fts5Context], CInt, Ptr[CInt], CInt] = p._13
    def xColumnSize_=(value: CFuncPtr3[Ptr[Fts5Context], CInt, Ptr[CInt], CInt]): Unit = p._13 = value
    def xQueryPhrase: CFuncPtr4[Ptr[Fts5Context], CInt, Ptr[Byte], CFuncPtr3[Ptr[Fts5ExtensionApi], Ptr[Fts5Context], Ptr[Byte], CInt], CInt] = p._14.asInstanceOf[CFuncPtr4[Ptr[Fts5Context], CInt, Ptr[Byte], CFuncPtr3[Ptr[Fts5ExtensionApi], Ptr[Fts5Context], Ptr[Byte], CInt], CInt]]
    def xQueryPhrase_=(value: CFuncPtr4[Ptr[Fts5Context], CInt, Ptr[Byte], CFuncPtr3[Ptr[Fts5ExtensionApi], Ptr[Fts5Context], Ptr[Byte], CInt], CInt]): Unit = p._14 = value.asInstanceOf[CFuncPtr4[Ptr[struct_Fts5Context], CInt, Ptr[Byte], CFuncPtr3[Ptr[struct_Fts5ExtensionApi], Ptr[struct_Fts5Context], Ptr[Byte], CInt], CInt]]
    def xSetAuxdata: CFuncPtr3[Ptr[Fts5Context], Ptr[Byte], CFuncPtr1[Ptr[Byte], Unit], CInt] = p._15
    def xSetAuxdata_=(value: CFuncPtr3[Ptr[Fts5Context], Ptr[Byte], CFuncPtr1[Ptr[Byte], Unit], CInt]): Unit = p._15 = value
    def xGetAuxdata: CFuncPtr2[Ptr[Fts5Context], CInt, Ptr[Byte]] = p._16
    def xGetAuxdata_=(value: CFuncPtr2[Ptr[Fts5Context], CInt, Ptr[Byte]]): Unit = p._16 = value
    def xPhraseFirst: CFuncPtr5[Ptr[Fts5Context], CInt, Ptr[Fts5PhraseIter], Ptr[CInt], Ptr[CInt], CInt] = p._17
    def xPhraseFirst_=(value: CFuncPtr5[Ptr[Fts5Context], CInt, Ptr[Fts5PhraseIter], Ptr[CInt], Ptr[CInt], CInt]): Unit = p._17 = value
    def xPhraseNext: CFuncPtr4[Ptr[Fts5Context], Ptr[Fts5PhraseIter], Ptr[CInt], Ptr[CInt], Unit] = p._18
    def xPhraseNext_=(value: CFuncPtr4[Ptr[Fts5Context], Ptr[Fts5PhraseIter], Ptr[CInt], Ptr[CInt], Unit]): Unit = p._18 = value
    def xPhraseFirstColumn: CFuncPtr4[Ptr[Fts5Context], CInt, Ptr[Fts5PhraseIter], Ptr[CInt], CInt] = p._19
    def xPhraseFirstColumn_=(value: CFuncPtr4[Ptr[Fts5Context], CInt, Ptr[Fts5PhraseIter], Ptr[CInt], CInt]): Unit = p._19 = value
    def xPhraseNextColumn: CFuncPtr3[Ptr[Fts5Context], Ptr[Fts5PhraseIter], Ptr[CInt], Unit] = p._20
    def xPhraseNextColumn_=(value: CFuncPtr3[Ptr[Fts5Context], Ptr[Fts5PhraseIter], Ptr[CInt], Unit]): Unit = p._20 = value
  }
  def struct_Fts5ExtensionApi()(implicit z: Zone): Ptr[struct_Fts5ExtensionApi] = alloc[struct_Fts5ExtensionApi]()
*/

  implicit class struct_fts5_tokenizer_ops(val p: Ptr[struct_fts5_tokenizer]) extends AnyVal {
    def xCreate: CFuncPtr4[Ptr[Byte], Ptr[CString], CInt, Ptr[Ptr[Fts5Tokenizer]], CInt] = p._1
    def xCreate_=(value: CFuncPtr4[Ptr[Byte], Ptr[CString], CInt, Ptr[Ptr[Fts5Tokenizer]], CInt]): Unit = p._1 = value
    def xDelete: CFuncPtr1[Ptr[Fts5Tokenizer], Unit] = p._2
    def xDelete_=(value: CFuncPtr1[Ptr[Fts5Tokenizer], Unit]): Unit = p._2 = value
    def xTokenize: CFuncPtr6[Ptr[Fts5Tokenizer], Ptr[Byte], CInt, CString, CInt, CFuncPtr6[Ptr[Byte], CInt, CString, CInt, CInt, CInt, CInt], CInt] = p._3
    def xTokenize_=(value: CFuncPtr6[Ptr[Fts5Tokenizer], Ptr[Byte], CInt, CString, CInt, CFuncPtr6[Ptr[Byte], CInt, CString, CInt, CInt, CInt, CInt], CInt]): Unit = p._3 = value
  }
  def struct_fts5_tokenizer()(implicit z: Zone): Ptr[struct_fts5_tokenizer] = alloc[struct_fts5_tokenizer]()

  /*
  implicit class struct_fts5_api_ops(val p: Ptr[struct_fts5_api]) extends AnyVal {
    def iVersion: CInt = p._1
    def iVersion_=(value: CInt): Unit = p._1 = value
    def xCreateTokenizer: CFuncPtr5[Ptr[fts5_api], CString, Ptr[Byte], Ptr[fts5_tokenizer], CFuncPtr1[Ptr[Byte], Unit], CInt] = p._2.asInstanceOf[CFuncPtr5[Ptr[fts5_api], CString, Ptr[Byte], Ptr[fts5_tokenizer], CFuncPtr1[Ptr[Byte], Unit], CInt]]
    def xCreateTokenizer_=(value: CFuncPtr5[Ptr[fts5_api], CString, Ptr[Byte], Ptr[fts5_tokenizer], CFuncPtr1[Ptr[Byte], Unit], CInt]): Unit = p._2 = value.asInstanceOf[CFuncPtr5[Ptr[Byte], CString, Ptr[Byte], Ptr[struct_fts5_tokenizer], CFuncPtr1[Ptr[Byte], Unit], CInt]]
    def xFindTokenizer: CFuncPtr4[Ptr[fts5_api], CString, Ptr[Ptr[Byte]], Ptr[fts5_tokenizer], CInt] = p._3.asInstanceOf[CFuncPtr4[Ptr[fts5_api], CString, Ptr[Ptr[Byte]], Ptr[fts5_tokenizer], CInt]]
    def xFindTokenizer_=(value: CFuncPtr4[Ptr[fts5_api], CString, Ptr[Ptr[Byte]], Ptr[fts5_tokenizer], CInt]): Unit = p._3 = value.asInstanceOf[CFuncPtr4[Ptr[Byte], CString, Ptr[Ptr[Byte]], Ptr[struct_fts5_tokenizer], CInt]]
    def xCreateFunction: CFuncPtr5[Ptr[fts5_api], CString, Ptr[Byte], CFuncPtr5[Ptr[Fts5ExtensionApi], Ptr[Fts5Context], Ptr[sqlite3_context], CInt, Ptr[Ptr[sqlite3_value]], Unit], CFuncPtr1[Ptr[Byte], Unit], CInt] = p._4.asInstanceOf[CFuncPtr5[Ptr[fts5_api], CString, Ptr[Byte], CFuncPtr5[Ptr[Fts5ExtensionApi], Ptr[Fts5Context], Ptr[sqlite3_context], CInt, Ptr[Ptr[sqlite3_value]], Unit], CFuncPtr1[Ptr[Byte], Unit], CInt]]
    def xCreateFunction_=(value: CFuncPtr5[Ptr[fts5_api], CString, Ptr[Byte], CFuncPtr5[Ptr[Fts5ExtensionApi], Ptr[Fts5Context], Ptr[sqlite3_context], CInt, Ptr[Ptr[sqlite3_value]], Unit], CFuncPtr1[Ptr[Byte], Unit], CInt]): Unit = p._4 = value.asInstanceOf[CFuncPtr5[Ptr[Byte], CString, Ptr[Byte], CFuncPtr5[Ptr[struct_Fts5ExtensionApi], Ptr[struct_Fts5Context], Ptr[struct_sqlite3_context], CInt, Ptr[Ptr[struct_sqlite3_value]], Unit], CFuncPtr1[Ptr[Byte], Unit], CInt]]
  }
  def struct_fts5_api()(implicit z: Zone): Ptr[struct_fts5_api] = alloc[struct_fts5_api]()*/
}
