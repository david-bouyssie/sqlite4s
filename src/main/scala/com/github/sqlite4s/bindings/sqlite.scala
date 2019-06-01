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

import scala.scalanative.native
import scala.scalanative.native._

@native.link("sqlite3")
@native.extern
object sqlite {
  type struct___va_list_tag = native.CStruct0 // incomplete type
  type struct_sqlite3 = native.CStruct0 // incomplete type
  type sqlite3 = struct_sqlite3
  type sqlite_int64 = native.CLongLong
  type sqlite_uint64 = native.CUnsignedLongLong
  type sqlite3_int64 = sqlite_int64
  type sqlite3_uint64 = sqlite_uint64
  type sqlite3_callback = native.CFunctionPtr4[native.Ptr[Byte], native.CInt, native.Ptr[native.CString], native.Ptr[native.CString], native.CInt]
  type struct_sqlite3_file = native.CStruct1[native.Ptr[struct_sqlite3_io_methods]]
  type sqlite3_file = struct_sqlite3_file
  type struct_sqlite3_io_methods = native.CStruct19[native.CInt, native.CFunctionPtr1[native.Ptr[Byte], native.CInt], native.CFunctionPtr4[native.Ptr[Byte], native.Ptr[Byte], native.CInt, native.CLongLong, native.CInt], native.CFunctionPtr4[native.Ptr[Byte], native.Ptr[Byte], native.CInt, native.CLongLong, native.CInt], native.CFunctionPtr2[native.Ptr[Byte], native.CLongLong, native.CInt], native.CFunctionPtr2[native.Ptr[Byte], native.CInt, native.CInt], native.CFunctionPtr2[native.Ptr[Byte], native.Ptr[native.CLongLong], native.CInt], native.CFunctionPtr2[native.Ptr[Byte], native.CInt, native.CInt], native.CFunctionPtr2[native.Ptr[Byte], native.CInt, native.CInt], native.CFunctionPtr2[native.Ptr[Byte], native.Ptr[native.CInt], native.CInt], native.CFunctionPtr3[native.Ptr[Byte], native.CInt, native.Ptr[Byte], native.CInt], native.CFunctionPtr1[native.Ptr[Byte], native.CInt], native.CFunctionPtr1[native.Ptr[Byte], native.CInt], native.CFunctionPtr5[native.Ptr[Byte], native.CInt, native.CInt, native.CInt, native.Ptr[native.Ptr[Byte]], native.CInt], native.CFunctionPtr4[native.Ptr[Byte], native.CInt, native.CInt, native.CInt, native.CInt], native.CFunctionPtr1[native.Ptr[Byte], Unit], native.CFunctionPtr2[native.Ptr[Byte], native.CInt, native.CInt], native.CFunctionPtr4[native.Ptr[Byte], native.CLongLong, native.CInt, native.Ptr[native.Ptr[Byte]], native.CInt], native.CFunctionPtr3[native.Ptr[Byte], native.CLongLong, native.Ptr[Byte], native.CInt]]
  type sqlite3_io_methods = struct_sqlite3_io_methods
  type struct_sqlite3_mutex = native.CStruct0 // incomplete type
  type sqlite3_mutex = struct_sqlite3_mutex
  type struct_sqlite3_vfs = native.CStruct22[native.CInt, native.CInt, native.CInt, native.Ptr[Byte], native.CString, native.Ptr[Byte], native.CFunctionPtr5[native.Ptr[Byte], native.CString, native.Ptr[struct_sqlite3_file], native.CInt, native.Ptr[native.CInt], native.CInt], native.CFunctionPtr3[native.Ptr[Byte], native.CString, native.CInt, native.CInt], native.CFunctionPtr4[native.Ptr[Byte], native.CString, native.CInt, native.Ptr[native.CInt], native.CInt], native.CFunctionPtr4[native.Ptr[Byte], native.CString, native.CInt, native.CString, native.CInt], native.CFunctionPtr2[native.Ptr[Byte], native.CString, native.Ptr[Byte]], native.CFunctionPtr3[native.Ptr[Byte], native.CInt, native.CString, Unit], native.CFunctionPtr3[native.Ptr[Byte], native.Ptr[Byte], native.CString, native.CFunctionPtr0[Unit]], native.CFunctionPtr2[native.Ptr[Byte], native.Ptr[Byte], Unit], native.CFunctionPtr3[native.Ptr[Byte], native.CInt, native.CString, native.CInt], native.CFunctionPtr2[native.Ptr[Byte], native.CInt, native.CInt], native.CFunctionPtr2[native.Ptr[Byte], native.Ptr[native.CDouble], native.CInt], native.CFunctionPtr3[native.Ptr[Byte], native.CInt, native.CString, native.CInt], native.CFunctionPtr2[native.Ptr[Byte], native.Ptr[native.CLongLong], native.CInt], native.CFunctionPtr3[native.Ptr[Byte], native.CString, native.CFunctionPtr0[Unit], native.CInt], native.CFunctionPtr2[native.Ptr[Byte], native.CString, native.CFunctionPtr0[Unit]], native.CFunctionPtr2[native.Ptr[Byte], native.CString, native.CString]]
  type sqlite3_vfs = struct_sqlite3_vfs
  type sqlite3_syscall_ptr = native.CFunctionPtr0[Unit]
  type struct_sqlite3_mem_methods = native.CStruct8[native.CFunctionPtr1[native.CInt, native.Ptr[Byte]], native.CFunctionPtr1[native.Ptr[Byte], Unit], native.CFunctionPtr2[native.Ptr[Byte], native.CInt, native.Ptr[Byte]], native.CFunctionPtr1[native.Ptr[Byte], native.CInt], native.CFunctionPtr1[native.CInt, native.CInt], native.CFunctionPtr1[native.Ptr[Byte], native.CInt], native.CFunctionPtr1[native.Ptr[Byte], Unit], native.Ptr[Byte]]
  type sqlite3_mem_methods = struct_sqlite3_mem_methods
  type struct_sqlite3_stmt = native.CStruct0 // incomplete type
  type sqlite3_stmt = struct_sqlite3_stmt
  type struct_sqlite3_value = native.CStruct0 // incomplete type
  type sqlite3_value = struct_sqlite3_value
  type struct_sqlite3_context = native.CStruct0 // incomplete type
  type sqlite3_context = struct_sqlite3_context
  type sqlite3_destructor_type = native.CFunctionPtr1[native.Ptr[Byte], Unit]
  type struct_sqlite3_vtab = native.CStruct3[native.Ptr[Byte], native.CInt, native.CString]
  type sqlite3_vtab = struct_sqlite3_vtab
  type struct_sqlite3_index_info = native.CStruct13[native.CInt, native.Ptr[struct_sqlite3_index_constraint], native.CInt, native.Ptr[struct_sqlite3_index_orderby], native.Ptr[struct_sqlite3_index_constraint_usage], native.CInt, native.CString, native.CInt, native.CInt, native.CDouble, sqlite3_int64, native.CInt, sqlite3_uint64]
  type sqlite3_index_info = struct_sqlite3_index_info
  type struct_sqlite3_vtab_cursor = native.CStruct1[native.Ptr[Byte]]
  type sqlite3_vtab_cursor = struct_sqlite3_vtab_cursor
  // FIXME: see https://github.com/scala-native/scala-native/issues/637 (DBO)
  //type struct_sqlite3_module = native.CStruct23[native.CInt, native.CFunctionPtr6[native.Ptr[sqlite3], native.Ptr[Byte], native.CInt, native.Ptr[native.CString], native.Ptr[native.Ptr[sqlite3_vtab]], native.Ptr[native.CString], native.CInt], native.CFunctionPtr6[native.Ptr[sqlite3], native.Ptr[Byte], native.CInt, native.Ptr[native.CString], native.Ptr[native.Ptr[sqlite3_vtab]], native.Ptr[native.CString], native.CInt], native.CFunctionPtr2[native.Ptr[sqlite3_vtab], native.Ptr[sqlite3_index_info], native.CInt], native.CFunctionPtr1[native.Ptr[sqlite3_vtab], native.CInt], native.CFunctionPtr1[native.Ptr[sqlite3_vtab], native.CInt], native.CFunctionPtr2[native.Ptr[sqlite3_vtab], native.Ptr[native.Ptr[sqlite3_vtab_cursor]], native.CInt], native.CFunctionPtr1[native.Ptr[sqlite3_vtab_cursor], native.CInt], native.CFunctionPtr5[native.Ptr[sqlite3_vtab_cursor], native.CInt, native.CString, native.CInt, native.Ptr[native.Ptr[sqlite3_value]], native.CInt], native.CFunctionPtr1[native.Ptr[sqlite3_vtab_cursor], native.CInt], native.CFunctionPtr1[native.Ptr[sqlite3_vtab_cursor], native.CInt], native.CFunctionPtr3[native.Ptr[sqlite3_vtab_cursor], native.Ptr[sqlite3_context], native.CInt, native.CInt], native.CFunctionPtr2[native.Ptr[sqlite3_vtab_cursor], native.Ptr[sqlite3_int64], native.CInt], native.CFunctionPtr4[native.Ptr[sqlite3_vtab], native.CInt, native.Ptr[native.Ptr[sqlite3_value]], native.Ptr[sqlite3_int64], native.CInt], native.CFunctionPtr1[native.Ptr[sqlite3_vtab], native.CInt], native.CFunctionPtr1[native.Ptr[sqlite3_vtab], native.CInt], native.CFunctionPtr1[native.Ptr[sqlite3_vtab], native.CInt], native.CFunctionPtr1[native.Ptr[sqlite3_vtab], native.CInt], native.CFunctionPtr5[native.Ptr[sqlite3_vtab], native.CInt, native.CString, native.Ptr[native.CFunctionPtr3[native.Ptr[sqlite3_context], native.CInt, native.Ptr[native.Ptr[sqlite3_value]], Unit]], native.Ptr[native.Ptr[Byte]], native.CInt], native.CFunctionPtr2[native.Ptr[sqlite3_vtab], native.CString, native.CInt], native.CFunctionPtr2[native.Ptr[sqlite3_vtab], native.CInt, native.CInt], native.CFunctionPtr2[native.Ptr[sqlite3_vtab], native.CInt, native.CInt], native.CFunctionPtr2[native.Ptr[sqlite3_vtab], native.CInt, native.CInt]]
  //type sqlite3_module = struct_sqlite3_module
  type struct_sqlite3_index_constraint = native.CStruct4[native.CInt, native.CUnsignedChar, native.CUnsignedChar, native.CInt]
  type struct_sqlite3_index_orderby = native.CStruct2[native.CInt, native.CUnsignedChar]
  type struct_sqlite3_index_constraint_usage = native.CStruct2[native.CInt, native.CUnsignedChar]
  type struct_sqlite3_blob = native.CStruct0 // incomplete type
  type sqlite3_blob = struct_sqlite3_blob
  type struct_sqlite3_mutex_methods = native.CStruct9[native.CFunctionPtr0[native.CInt], native.CFunctionPtr0[native.CInt], native.CFunctionPtr1[native.CInt, native.Ptr[sqlite3_mutex]], native.CFunctionPtr1[native.Ptr[sqlite3_mutex], Unit], native.CFunctionPtr1[native.Ptr[sqlite3_mutex], Unit], native.CFunctionPtr1[native.Ptr[sqlite3_mutex], native.CInt], native.CFunctionPtr1[native.Ptr[sqlite3_mutex], Unit], native.CFunctionPtr1[native.Ptr[sqlite3_mutex], native.CInt], native.CFunctionPtr1[native.Ptr[sqlite3_mutex], native.CInt]]
  type sqlite3_mutex_methods = struct_sqlite3_mutex_methods
  type struct_sqlite3_pcache = native.CStruct0 // incomplete type
  type sqlite3_pcache = struct_sqlite3_pcache
  type struct_sqlite3_pcache_page = native.CStruct2[native.Ptr[Byte], native.Ptr[Byte]]
  type sqlite3_pcache_page = struct_sqlite3_pcache_page
  type struct_sqlite3_pcache_methods2 = native.CStruct13[native.CInt, native.Ptr[Byte], native.CFunctionPtr1[native.Ptr[Byte], native.CInt], native.CFunctionPtr1[native.Ptr[Byte], Unit], native.CFunctionPtr3[native.CInt, native.CInt, native.CInt, native.Ptr[sqlite3_pcache]], native.CFunctionPtr2[native.Ptr[sqlite3_pcache], native.CInt, Unit], native.CFunctionPtr1[native.Ptr[sqlite3_pcache], native.CInt], native.CFunctionPtr3[native.Ptr[sqlite3_pcache], native.CUnsignedInt, native.CInt, native.Ptr[sqlite3_pcache_page]], native.CFunctionPtr3[native.Ptr[sqlite3_pcache], native.Ptr[sqlite3_pcache_page], native.CInt, Unit], native.CFunctionPtr4[native.Ptr[sqlite3_pcache], native.Ptr[sqlite3_pcache_page], native.CUnsignedInt, native.CUnsignedInt, Unit], native.CFunctionPtr2[native.Ptr[sqlite3_pcache], native.CUnsignedInt, Unit], native.CFunctionPtr1[native.Ptr[sqlite3_pcache], Unit], native.CFunctionPtr1[native.Ptr[sqlite3_pcache], Unit]]
  type sqlite3_pcache_methods2 = struct_sqlite3_pcache_methods2
  type struct_sqlite3_pcache_methods = native.CStruct11[native.Ptr[Byte], native.CFunctionPtr1[native.Ptr[Byte], native.CInt], native.CFunctionPtr1[native.Ptr[Byte], Unit], native.CFunctionPtr2[native.CInt, native.CInt, native.Ptr[sqlite3_pcache]], native.CFunctionPtr2[native.Ptr[sqlite3_pcache], native.CInt, Unit], native.CFunctionPtr1[native.Ptr[sqlite3_pcache], native.CInt], native.CFunctionPtr3[native.Ptr[sqlite3_pcache], native.CUnsignedInt, native.CInt, native.Ptr[Byte]], native.CFunctionPtr3[native.Ptr[sqlite3_pcache], native.Ptr[Byte], native.CInt, Unit], native.CFunctionPtr4[native.Ptr[sqlite3_pcache], native.Ptr[Byte], native.CUnsignedInt, native.CUnsignedInt, Unit], native.CFunctionPtr2[native.Ptr[sqlite3_pcache], native.CUnsignedInt, Unit], native.CFunctionPtr1[native.Ptr[sqlite3_pcache], Unit]]
  type sqlite3_pcache_methods = struct_sqlite3_pcache_methods
  type struct_sqlite3_backup = native.CStruct0 // incomplete type
  type sqlite3_backup = struct_sqlite3_backup
  type struct_sqlite3_snapshot = native.CStruct1[native.CArray[native.CUnsignedChar, native.Nat.Digit[native.Nat._4, native.Nat._8]]]
  type sqlite3_snapshot = struct_sqlite3_snapshot
  type struct_sqlite3_rtree_geometry = native.CStruct5[native.Ptr[Byte], native.CInt, native.Ptr[sqlite3_rtree_dbl], native.Ptr[Byte], native.CFunctionPtr1[native.Ptr[Byte], Unit]]
  type sqlite3_rtree_geometry = struct_sqlite3_rtree_geometry
  type struct_sqlite3_rtree_query_info = native.CStruct16[native.Ptr[Byte], native.CInt, native.Ptr[sqlite3_rtree_dbl], native.Ptr[Byte], native.CFunctionPtr1[native.Ptr[Byte], Unit], native.Ptr[sqlite3_rtree_dbl], native.Ptr[native.CUnsignedInt], native.CInt, native.CInt, native.CInt, sqlite3_int64, sqlite3_rtree_dbl, native.CInt, native.CInt, sqlite3_rtree_dbl, native.Ptr[native.Ptr[sqlite3_value]]]
  type sqlite3_rtree_query_info = struct_sqlite3_rtree_query_info
  type sqlite3_rtree_dbl = native.CDouble
  //type struct_Fts5ExtensionApi = native.CStruct20[native.CInt, native.CFunctionPtr1[native.Ptr[Fts5Context], native.Ptr[Byte]], native.CFunctionPtr1[native.Ptr[Fts5Context], native.CInt], native.CFunctionPtr2[native.Ptr[Fts5Context], native.Ptr[sqlite3_int64], native.CInt], native.CFunctionPtr3[native.Ptr[Fts5Context], native.CInt, native.Ptr[sqlite3_int64], native.CInt], native.CFunctionPtr5[native.Ptr[Fts5Context], native.CString, native.CInt, native.Ptr[Byte], native.CFunctionPtr6[native.Ptr[Byte], native.CInt, native.CString, native.CInt, native.CInt, native.CInt, native.CInt], native.CInt], native.CFunctionPtr1[native.Ptr[Fts5Context], native.CInt], native.CFunctionPtr2[native.Ptr[Fts5Context], native.CInt, native.CInt], native.CFunctionPtr2[native.Ptr[Fts5Context], native.Ptr[native.CInt], native.CInt], native.CFunctionPtr5[native.Ptr[Fts5Context], native.CInt, native.Ptr[native.CInt], native.Ptr[native.CInt], native.Ptr[native.CInt], native.CInt], native.CFunctionPtr1[native.Ptr[Fts5Context], sqlite3_int64], native.CFunctionPtr4[native.Ptr[Fts5Context], native.CInt, native.Ptr[native.CString], native.Ptr[native.CInt], native.CInt], native.CFunctionPtr3[native.Ptr[Fts5Context], native.CInt, native.Ptr[native.CInt], native.CInt], native.CFunctionPtr4[native.Ptr[struct_Fts5Context], native.CInt, native.Ptr[Byte], native.CFunctionPtr3[native.Ptr[struct_Fts5ExtensionApi], native.Ptr[struct_Fts5Context], native.Ptr[Byte], native.CInt], native.CInt], native.CFunctionPtr3[native.Ptr[Fts5Context], native.Ptr[Byte], native.CFunctionPtr1[native.Ptr[Byte], Unit], native.CInt], native.CFunctionPtr2[native.Ptr[Fts5Context], native.CInt, native.Ptr[Byte]], native.CFunctionPtr5[native.Ptr[Fts5Context], native.CInt, native.Ptr[Fts5PhraseIter], native.Ptr[native.CInt], native.Ptr[native.CInt], native.CInt], native.CFunctionPtr4[native.Ptr[Fts5Context], native.Ptr[Fts5PhraseIter], native.Ptr[native.CInt], native.Ptr[native.CInt], Unit], native.CFunctionPtr4[native.Ptr[Fts5Context], native.CInt, native.Ptr[Fts5PhraseIter], native.Ptr[native.CInt], native.CInt], native.CFunctionPtr3[native.Ptr[Fts5Context], native.Ptr[Fts5PhraseIter], native.Ptr[native.CInt], Unit]]
  //type Fts5ExtensionApi = struct_Fts5ExtensionApi
  type struct_Fts5Context = native.CStruct0 // incomplete type
  type Fts5Context = struct_Fts5Context
  type struct_Fts5PhraseIter = native.CStruct2[native.Ptr[native.CUnsignedChar], native.Ptr[native.CUnsignedChar]]
  type Fts5PhraseIter = struct_Fts5PhraseIter
  //type fts5_extension_function = native.CFunctionPtr5[native.Ptr[Fts5ExtensionApi], native.Ptr[Fts5Context], native.Ptr[sqlite3_context], native.CInt, native.Ptr[native.Ptr[sqlite3_value]], Unit]
  type struct_Fts5Tokenizer = native.CStruct0 // incomplete type
  type Fts5Tokenizer = struct_Fts5Tokenizer
  type struct_fts5_tokenizer = native.CStruct3[native.CFunctionPtr4[native.Ptr[Byte], native.Ptr[native.CString], native.CInt, native.Ptr[native.Ptr[Fts5Tokenizer]], native.CInt], native.CFunctionPtr1[native.Ptr[Fts5Tokenizer], Unit], native.CFunctionPtr6[native.Ptr[Fts5Tokenizer], native.Ptr[Byte], native.CInt, native.CString, native.CInt, native.CFunctionPtr6[native.Ptr[Byte], native.CInt, native.CString, native.CInt, native.CInt, native.CInt, native.CInt], native.CInt]]
  type fts5_tokenizer = struct_fts5_tokenizer
  //type struct_fts5_api = native.CStruct4[native.CInt, native.CFunctionPtr5[native.Ptr[Byte], native.CString, native.Ptr[Byte], native.Ptr[struct_fts5_tokenizer], native.CFunctionPtr1[native.Ptr[Byte], Unit], native.CInt], native.CFunctionPtr4[native.Ptr[Byte], native.CString, native.Ptr[native.Ptr[Byte]], native.Ptr[struct_fts5_tokenizer], native.CInt], native.CFunctionPtr5[native.Ptr[Byte], native.CString, native.Ptr[Byte], native.CFunctionPtr5[native.Ptr[struct_Fts5ExtensionApi], native.Ptr[struct_Fts5Context], native.Ptr[struct_sqlite3_context], native.CInt, native.Ptr[native.Ptr[struct_sqlite3_value]], Unit], native.CFunctionPtr1[native.Ptr[Byte], Unit], native.CInt]]
  //type fts5_api = struct_fts5_api
  val sqlite3_version: native.CString = native.extern
  val sqlite3_temp_directory: native.CString = native.extern
  val sqlite3_data_directory: native.CString = native.extern
  def sqlite3_libversion(): native.CString = native.extern
  def sqlite3_sourceid(): native.CString = native.extern
  def sqlite3_libversion_number(): native.CInt = native.extern
  def sqlite3_compileoption_used(zOptName: native.CString): native.CInt = native.extern
  def sqlite3_compileoption_get(N: native.CInt): native.CString = native.extern
  def sqlite3_threadsafe(): native.CInt = native.extern
  def sqlite3_close(p0: native.Ptr[sqlite3]): native.CInt = native.extern
  def sqlite3_close_v2(p0: native.Ptr[sqlite3]): native.CInt = native.extern
  def sqlite3_exec(p0: native.Ptr[sqlite3], sql: native.CString, callback: native.CFunctionPtr4[native.Ptr[Byte], native.CInt, native.Ptr[native.CString], native.Ptr[native.CString], native.CInt], p1: native.Ptr[Byte], errmsg: native.Ptr[native.CString]): native.CInt = native.extern
  def sqlite3_initialize(): native.CInt = native.extern
  def sqlite3_shutdown(): native.CInt = native.extern
  def sqlite3_os_init(): native.CInt = native.extern
  def sqlite3_os_end(): native.CInt = native.extern
  def sqlite3_config(p0: native.CInt, varArgs: native.CVararg*): native.CInt = native.extern
  def sqlite3_db_config(p0: native.Ptr[sqlite3], op: native.CInt, varArgs: native.CVararg*): native.CInt = native.extern
  def sqlite3_extended_result_codes(p0: native.Ptr[sqlite3], onoff: native.CInt): native.CInt = native.extern
  def sqlite3_last_insert_rowid(p0: native.Ptr[sqlite3]): sqlite3_int64 = native.extern
  def sqlite3_set_last_insert_rowid(p0: native.Ptr[sqlite3], p1: sqlite3_int64): Unit = native.extern
  def sqlite3_changes(p0: native.Ptr[sqlite3]): native.CInt = native.extern
  def sqlite3_total_changes(p0: native.Ptr[sqlite3]): native.CInt = native.extern
  def sqlite3_interrupt(p0: native.Ptr[sqlite3]): Unit = native.extern
  def sqlite3_complete(sql: native.CString): native.CInt = native.extern
  def sqlite3_complete16(sql: native.Ptr[Byte]): native.CInt = native.extern
  def sqlite3_busy_handler(p0: native.Ptr[sqlite3], p1: native.CFunctionPtr2[native.Ptr[Byte], native.CInt, native.CInt], p2: native.Ptr[Byte]): native.CInt = native.extern
  def sqlite3_busy_timeout(p0: native.Ptr[sqlite3], ms: native.CInt): native.CInt = native.extern
  def sqlite3_get_table(db: native.Ptr[sqlite3], zSql: native.CString, pazResult: native.Ptr[native.Ptr[native.CString]], pnRow: native.Ptr[native.CInt], pnColumn: native.Ptr[native.CInt], pzErrmsg: native.Ptr[native.CString]): native.CInt = native.extern
  def sqlite3_free_table(result: native.Ptr[native.CString]): Unit = native.extern
  def sqlite3_mprintf(p0: native.CString, varArgs: native.CVararg*): native.CString = native.extern
  def sqlite3_vmprintf(p0: native.CString, p1: native.Ptr[struct___va_list_tag]): native.CString = native.extern
  def sqlite3_snprintf(p0: native.CInt, p1: native.CString, p2: native.CString, varArgs: native.CVararg*): native.CString = native.extern
  def sqlite3_vsnprintf(p0: native.CInt, p1: native.CString, p2: native.CString, p3: native.Ptr[struct___va_list_tag]): native.CString = native.extern
  def sqlite3_malloc(p0: native.CInt): native.Ptr[Byte] = native.extern
  def sqlite3_malloc64(p0: sqlite3_uint64): native.Ptr[Byte] = native.extern
  def sqlite3_realloc(p0: native.Ptr[Byte], p1: native.CInt): native.Ptr[Byte] = native.extern
  def sqlite3_realloc64(p0: native.Ptr[Byte], p1: sqlite3_uint64): native.Ptr[Byte] = native.extern
  def sqlite3_free(p0: native.Ptr[Byte]): Unit = native.extern
  def sqlite3_msize(p0: native.Ptr[Byte]): sqlite3_uint64 = native.extern
  def sqlite3_memory_used(): sqlite3_int64 = native.extern
  def sqlite3_memory_highwater(resetFlag: native.CInt): sqlite3_int64 = native.extern
  def sqlite3_randomness(N: native.CInt, P: native.Ptr[Byte]): Unit = native.extern
  def sqlite3_set_authorizer(p0: native.Ptr[sqlite3], xAuth: native.CFunctionPtr6[native.Ptr[Byte], native.CInt, native.CString, native.CString, native.CString, native.CString, native.CInt], pUserData: native.Ptr[Byte]): native.CInt = native.extern
  def sqlite3_trace(p0: native.Ptr[sqlite3], xTrace: native.CFunctionPtr2[native.Ptr[Byte], native.CString, Unit], p1: native.Ptr[Byte]): native.Ptr[Byte] = native.extern
  def sqlite3_profile(p0: native.Ptr[sqlite3], xProfile: native.CFunctionPtr3[native.Ptr[Byte], native.CString, sqlite3_uint64, Unit], p1: native.Ptr[Byte]): native.Ptr[Byte] = native.extern
  def sqlite3_trace_v2(p0: native.Ptr[sqlite3], uMask: native.CUnsignedInt, xCallback: native.CFunctionPtr4[native.CUnsignedInt, native.Ptr[Byte], native.Ptr[Byte], native.Ptr[Byte], native.CInt], pCtx: native.Ptr[Byte]): native.CInt = native.extern
  def sqlite3_progress_handler(p0: native.Ptr[sqlite3], p1: native.CInt, p2: native.CFunctionPtr1[native.Ptr[Byte], native.CInt], p3: native.Ptr[Byte]): Unit = native.extern
  def sqlite3_open(filename: native.CString, ppDb: native.Ptr[native.Ptr[sqlite3]]): native.CInt = native.extern
  def sqlite3_open16(filename: native.Ptr[Byte], ppDb: native.Ptr[native.Ptr[sqlite3]]): native.CInt = native.extern
  def sqlite3_open_v2(filename: native.CString, ppDb: native.Ptr[native.Ptr[sqlite3]], flags: native.CInt, zVfs: native.CString): native.CInt = native.extern
  def sqlite3_uri_parameter(zFilename: native.CString, zParam: native.CString): native.CString = native.extern
  def sqlite3_uri_boolean(zFile: native.CString, zParam: native.CString, bDefault: native.CInt): native.CInt = native.extern
  def sqlite3_uri_int64(p0: native.CString, p1: native.CString, p2: sqlite3_int64): sqlite3_int64 = native.extern
  def sqlite3_errcode(db: native.Ptr[sqlite3]): native.CInt = native.extern
  def sqlite3_extended_errcode(db: native.Ptr[sqlite3]): native.CInt = native.extern
  def sqlite3_errmsg(p0: native.Ptr[sqlite3]): native.CString = native.extern
  def sqlite3_errmsg16(p0: native.Ptr[sqlite3]): native.Ptr[Byte] = native.extern
  def sqlite3_errstr(p0: native.CInt): native.CString = native.extern
  def sqlite3_limit(p0: native.Ptr[sqlite3], id: native.CInt, newVal: native.CInt): native.CInt = native.extern
  def sqlite3_prepare(db: native.Ptr[sqlite3], zSql: native.CString, nByte: native.CInt, ppStmt: native.Ptr[native.Ptr[sqlite3_stmt]], pzTail: native.Ptr[native.CString]): native.CInt = native.extern
  def sqlite3_prepare_v2(db: native.Ptr[sqlite3], zSql: native.CString, nByte: native.CInt, ppStmt: native.Ptr[native.Ptr[sqlite3_stmt]], pzTail: native.Ptr[native.CString]): native.CInt = native.extern
  def sqlite3_prepare_v3(db: native.Ptr[sqlite3], zSql: native.CString, nByte: native.CInt, prepFlags: native.CUnsignedInt, ppStmt: native.Ptr[native.Ptr[sqlite3_stmt]], pzTail: native.Ptr[native.CString]): native.CInt = native.extern
  def sqlite3_prepare16(db: native.Ptr[sqlite3], zSql: native.Ptr[Byte], nByte: native.CInt, ppStmt: native.Ptr[native.Ptr[sqlite3_stmt]], pzTail: native.Ptr[native.Ptr[Byte]]): native.CInt = native.extern
  def sqlite3_prepare16_v2(db: native.Ptr[sqlite3], zSql: native.Ptr[Byte], nByte: native.CInt, ppStmt: native.Ptr[native.Ptr[sqlite3_stmt]], pzTail: native.Ptr[native.Ptr[Byte]]): native.CInt = native.extern
  def sqlite3_prepare16_v3(db: native.Ptr[sqlite3], zSql: native.Ptr[Byte], nByte: native.CInt, prepFlags: native.CUnsignedInt, ppStmt: native.Ptr[native.Ptr[sqlite3_stmt]], pzTail: native.Ptr[native.Ptr[Byte]]): native.CInt = native.extern
  def sqlite3_sql(pStmt: native.Ptr[sqlite3_stmt]): native.CString = native.extern
  def sqlite3_expanded_sql(pStmt: native.Ptr[sqlite3_stmt]): native.CString = native.extern
  def sqlite3_stmt_readonly(pStmt: native.Ptr[sqlite3_stmt]): native.CInt = native.extern
  def sqlite3_stmt_busy(p0: native.Ptr[sqlite3_stmt]): native.CInt = native.extern
  def sqlite3_bind_blob(p0: native.Ptr[sqlite3_stmt], p1: native.CInt, p2: native.Ptr[Byte], n: native.CInt, p3: native.CFunctionPtr1[native.Ptr[Byte], Unit]): native.CInt = native.extern
  def sqlite3_bind_blob64(p0: native.Ptr[sqlite3_stmt], p1: native.CInt, p2: native.Ptr[Byte], p3: sqlite3_uint64, p4: native.CFunctionPtr1[native.Ptr[Byte], Unit]): native.CInt = native.extern
  def sqlite3_bind_double(p0: native.Ptr[sqlite3_stmt], p1: native.CInt, p2: native.CDouble): native.CInt = native.extern
  def sqlite3_bind_int(p0: native.Ptr[sqlite3_stmt], p1: native.CInt, p2: native.CInt): native.CInt = native.extern
  def sqlite3_bind_int64(p0: native.Ptr[sqlite3_stmt], p1: native.CInt, p2: sqlite3_int64): native.CInt = native.extern
  def sqlite3_bind_null(p0: native.Ptr[sqlite3_stmt], p1: native.CInt): native.CInt = native.extern
  def sqlite3_bind_text(p0: native.Ptr[sqlite3_stmt], p1: native.CInt, p2: native.CString, p3: native.CInt, p4: native.CFunctionPtr1[native.Ptr[Byte], Unit]): native.CInt = native.extern
  def sqlite3_bind_text16(p0: native.Ptr[sqlite3_stmt], p1: native.CInt, p2: native.Ptr[Byte], p3: native.CInt, p4: native.CFunctionPtr1[native.Ptr[Byte], Unit]): native.CInt = native.extern
  def sqlite3_bind_text64(p0: native.Ptr[sqlite3_stmt], p1: native.CInt, p2: native.CString, p3: sqlite3_uint64, p4: native.CFunctionPtr1[native.Ptr[Byte], Unit], encoding: native.CUnsignedChar): native.CInt = native.extern
  def sqlite3_bind_value(p0: native.Ptr[sqlite3_stmt], p1: native.CInt, p2: native.Ptr[sqlite3_value]): native.CInt = native.extern
  def sqlite3_bind_pointer(p0: native.Ptr[sqlite3_stmt], p1: native.CInt, p2: native.Ptr[Byte], p3: native.CString, p4: native.CFunctionPtr1[native.Ptr[Byte], Unit]): native.CInt = native.extern
  def sqlite3_bind_zeroblob(p0: native.Ptr[sqlite3_stmt], p1: native.CInt, n: native.CInt): native.CInt = native.extern
  def sqlite3_bind_zeroblob64(p0: native.Ptr[sqlite3_stmt], p1: native.CInt, p2: sqlite3_uint64): native.CInt = native.extern
  def sqlite3_bind_parameter_count(p0: native.Ptr[sqlite3_stmt]): native.CInt = native.extern
  def sqlite3_bind_parameter_name(p0: native.Ptr[sqlite3_stmt], p1: native.CInt): native.CString = native.extern
  def sqlite3_bind_parameter_index(p0: native.Ptr[sqlite3_stmt], zName: native.CString): native.CInt = native.extern
  def sqlite3_clear_bindings(p0: native.Ptr[sqlite3_stmt]): native.CInt = native.extern
  def sqlite3_column_count(pStmt: native.Ptr[sqlite3_stmt]): native.CInt = native.extern
  def sqlite3_column_name(p0: native.Ptr[sqlite3_stmt], N: native.CInt): native.CString = native.extern
  def sqlite3_column_name16(p0: native.Ptr[sqlite3_stmt], N: native.CInt): native.Ptr[Byte] = native.extern
  def sqlite3_column_database_name(p0: native.Ptr[sqlite3_stmt], p1: native.CInt): native.CString = native.extern
  def sqlite3_column_database_name16(p0: native.Ptr[sqlite3_stmt], p1: native.CInt): native.Ptr[Byte] = native.extern
  def sqlite3_column_table_name(p0: native.Ptr[sqlite3_stmt], p1: native.CInt): native.CString = native.extern
  def sqlite3_column_table_name16(p0: native.Ptr[sqlite3_stmt], p1: native.CInt): native.Ptr[Byte] = native.extern
  def sqlite3_column_origin_name(p0: native.Ptr[sqlite3_stmt], p1: native.CInt): native.CString = native.extern
  def sqlite3_column_origin_name16(p0: native.Ptr[sqlite3_stmt], p1: native.CInt): native.Ptr[Byte] = native.extern
  def sqlite3_column_decltype(p0: native.Ptr[sqlite3_stmt], p1: native.CInt): native.CString = native.extern
  def sqlite3_column_decltype16(p0: native.Ptr[sqlite3_stmt], p1: native.CInt): native.Ptr[Byte] = native.extern
  def sqlite3_step(p0: native.Ptr[sqlite3_stmt]): native.CInt = native.extern
  def sqlite3_data_count(pStmt: native.Ptr[sqlite3_stmt]): native.CInt = native.extern
  def sqlite3_column_blob(p0: native.Ptr[sqlite3_stmt], iCol: native.CInt): native.Ptr[Byte] = native.extern
  def sqlite3_column_double(p0: native.Ptr[sqlite3_stmt], iCol: native.CInt): native.CDouble = native.extern
  def sqlite3_column_int(p0: native.Ptr[sqlite3_stmt], iCol: native.CInt): native.CInt = native.extern
  def sqlite3_column_int64(p0: native.Ptr[sqlite3_stmt], iCol: native.CInt): sqlite3_int64 = native.extern
  def sqlite3_column_text(p0: native.Ptr[sqlite3_stmt], iCol: native.CInt): native.Ptr[native.CUnsignedChar] = native.extern
  def sqlite3_column_text16(p0: native.Ptr[sqlite3_stmt], iCol: native.CInt): native.Ptr[Byte] = native.extern
  def sqlite3_column_value(p0: native.Ptr[sqlite3_stmt], iCol: native.CInt): native.Ptr[sqlite3_value] = native.extern
  def sqlite3_column_bytes(p0: native.Ptr[sqlite3_stmt], iCol: native.CInt): native.CInt = native.extern
  def sqlite3_column_bytes16(p0: native.Ptr[sqlite3_stmt], iCol: native.CInt): native.CInt = native.extern
  def sqlite3_column_type(p0: native.Ptr[sqlite3_stmt], iCol: native.CInt): native.CInt = native.extern
  def sqlite3_finalize(pStmt: native.Ptr[sqlite3_stmt]): native.CInt = native.extern
  def sqlite3_reset(pStmt: native.Ptr[sqlite3_stmt]): native.CInt = native.extern
  def sqlite3_create_function(db: native.Ptr[sqlite3], zFunctionName: native.CString, nArg: native.CInt, eTextRep: native.CInt, pApp: native.Ptr[Byte], xFunc: native.CFunctionPtr3[native.Ptr[sqlite3_context], native.CInt, native.Ptr[native.Ptr[sqlite3_value]], Unit], xStep: native.CFunctionPtr3[native.Ptr[sqlite3_context], native.CInt, native.Ptr[native.Ptr[sqlite3_value]], Unit], xFinal: native.CFunctionPtr1[native.Ptr[sqlite3_context], Unit]): native.CInt = native.extern
  def sqlite3_create_function16(db: native.Ptr[sqlite3], zFunctionName: native.Ptr[Byte], nArg: native.CInt, eTextRep: native.CInt, pApp: native.Ptr[Byte], xFunc: native.CFunctionPtr3[native.Ptr[sqlite3_context], native.CInt, native.Ptr[native.Ptr[sqlite3_value]], Unit], xStep: native.CFunctionPtr3[native.Ptr[sqlite3_context], native.CInt, native.Ptr[native.Ptr[sqlite3_value]], Unit], xFinal: native.CFunctionPtr1[native.Ptr[sqlite3_context], Unit]): native.CInt = native.extern
  def sqlite3_create_function_v2(db: native.Ptr[sqlite3], zFunctionName: native.CString, nArg: native.CInt, eTextRep: native.CInt, pApp: native.Ptr[Byte], xFunc: native.CFunctionPtr3[native.Ptr[sqlite3_context], native.CInt, native.Ptr[native.Ptr[sqlite3_value]], Unit], xStep: native.CFunctionPtr3[native.Ptr[sqlite3_context], native.CInt, native.Ptr[native.Ptr[sqlite3_value]], Unit], xFinal: native.CFunctionPtr1[native.Ptr[sqlite3_context], Unit], xDestroy: native.CFunctionPtr1[native.Ptr[Byte], Unit]): native.CInt = native.extern
  def sqlite3_aggregate_count(p0: native.Ptr[sqlite3_context]): native.CInt = native.extern
  def sqlite3_expired(p0: native.Ptr[sqlite3_stmt]): native.CInt = native.extern
  def sqlite3_transfer_bindings(p0: native.Ptr[sqlite3_stmt], p1: native.Ptr[sqlite3_stmt]): native.CInt = native.extern
  def sqlite3_global_recover(): native.CInt = native.extern
  def sqlite3_thread_cleanup(): Unit = native.extern
  def sqlite3_memory_alarm(p0: native.CFunctionPtr3[native.Ptr[Byte], sqlite3_int64, native.CInt, Unit], p1: native.Ptr[Byte], p2: sqlite3_int64): native.CInt = native.extern
  def sqlite3_value_blob(p0: native.Ptr[sqlite3_value]): native.Ptr[Byte] = native.extern
  def sqlite3_value_double(p0: native.Ptr[sqlite3_value]): native.CDouble = native.extern
  def sqlite3_value_int(p0: native.Ptr[sqlite3_value]): native.CInt = native.extern
  def sqlite3_value_int64(p0: native.Ptr[sqlite3_value]): sqlite3_int64 = native.extern
  def sqlite3_value_pointer(p0: native.Ptr[sqlite3_value], p1: native.CString): native.Ptr[Byte] = native.extern
  def sqlite3_value_text(p0: native.Ptr[sqlite3_value]): native.Ptr[native.CUnsignedChar] = native.extern
  def sqlite3_value_text16(p0: native.Ptr[sqlite3_value]): native.Ptr[Byte] = native.extern
  def sqlite3_value_text16le(p0: native.Ptr[sqlite3_value]): native.Ptr[Byte] = native.extern
  def sqlite3_value_text16be(p0: native.Ptr[sqlite3_value]): native.Ptr[Byte] = native.extern
  def sqlite3_value_bytes(p0: native.Ptr[sqlite3_value]): native.CInt = native.extern
  def sqlite3_value_bytes16(p0: native.Ptr[sqlite3_value]): native.CInt = native.extern
  def sqlite3_value_type(p0: native.Ptr[sqlite3_value]): native.CInt = native.extern
  def sqlite3_value_numeric_type(p0: native.Ptr[sqlite3_value]): native.CInt = native.extern
  def sqlite3_value_nochange(p0: native.Ptr[sqlite3_value]): native.CInt = native.extern
  def sqlite3_value_subtype(p0: native.Ptr[sqlite3_value]): native.CUnsignedInt = native.extern
  def sqlite3_value_dup(p0: native.Ptr[sqlite3_value]): native.Ptr[sqlite3_value] = native.extern
  def sqlite3_value_free(p0: native.Ptr[sqlite3_value]): Unit = native.extern
  def sqlite3_aggregate_context(p0: native.Ptr[sqlite3_context], nBytes: native.CInt): native.Ptr[Byte] = native.extern
  def sqlite3_user_data(p0: native.Ptr[sqlite3_context]): native.Ptr[Byte] = native.extern
  def sqlite3_context_db_handle(p0: native.Ptr[sqlite3_context]): native.Ptr[sqlite3] = native.extern
  def sqlite3_get_auxdata(p0: native.Ptr[sqlite3_context], N: native.CInt): native.Ptr[Byte] = native.extern
  def sqlite3_set_auxdata(p0: native.Ptr[sqlite3_context], N: native.CInt, p1: native.Ptr[Byte], p2: native.CFunctionPtr1[native.Ptr[Byte], Unit]): Unit = native.extern
  def sqlite3_result_blob(p0: native.Ptr[sqlite3_context], p1: native.Ptr[Byte], p2: native.CInt, p3: native.CFunctionPtr1[native.Ptr[Byte], Unit]): Unit = native.extern
  def sqlite3_result_blob64(p0: native.Ptr[sqlite3_context], p1: native.Ptr[Byte], p2: sqlite3_uint64, p3: native.CFunctionPtr1[native.Ptr[Byte], Unit]): Unit = native.extern
  def sqlite3_result_double(p0: native.Ptr[sqlite3_context], p1: native.CDouble): Unit = native.extern
  def sqlite3_result_error(p0: native.Ptr[sqlite3_context], p1: native.CString, p2: native.CInt): Unit = native.extern
  def sqlite3_result_error16(p0: native.Ptr[sqlite3_context], p1: native.Ptr[Byte], p2: native.CInt): Unit = native.extern
  def sqlite3_result_error_toobig(p0: native.Ptr[sqlite3_context]): Unit = native.extern
  def sqlite3_result_error_nomem(p0: native.Ptr[sqlite3_context]): Unit = native.extern
  def sqlite3_result_error_code(p0: native.Ptr[sqlite3_context], p1: native.CInt): Unit = native.extern
  def sqlite3_result_int(p0: native.Ptr[sqlite3_context], p1: native.CInt): Unit = native.extern
  def sqlite3_result_int64(p0: native.Ptr[sqlite3_context], p1: sqlite3_int64): Unit = native.extern
  def sqlite3_result_null(p0: native.Ptr[sqlite3_context]): Unit = native.extern
  def sqlite3_result_text(p0: native.Ptr[sqlite3_context], p1: native.CString, p2: native.CInt, p3: native.CFunctionPtr1[native.Ptr[Byte], Unit]): Unit = native.extern
  def sqlite3_result_text64(p0: native.Ptr[sqlite3_context], p1: native.CString, p2: sqlite3_uint64, p3: native.CFunctionPtr1[native.Ptr[Byte], Unit], encoding: native.CUnsignedChar): Unit = native.extern
  def sqlite3_result_text16(p0: native.Ptr[sqlite3_context], p1: native.Ptr[Byte], p2: native.CInt, p3: native.CFunctionPtr1[native.Ptr[Byte], Unit]): Unit = native.extern
  def sqlite3_result_text16le(p0: native.Ptr[sqlite3_context], p1: native.Ptr[Byte], p2: native.CInt, p3: native.CFunctionPtr1[native.Ptr[Byte], Unit]): Unit = native.extern
  def sqlite3_result_text16be(p0: native.Ptr[sqlite3_context], p1: native.Ptr[Byte], p2: native.CInt, p3: native.CFunctionPtr1[native.Ptr[Byte], Unit]): Unit = native.extern
  def sqlite3_result_value(p0: native.Ptr[sqlite3_context], p1: native.Ptr[sqlite3_value]): Unit = native.extern
  def sqlite3_result_pointer(p0: native.Ptr[sqlite3_context], p1: native.Ptr[Byte], p2: native.CString, p3: native.CFunctionPtr1[native.Ptr[Byte], Unit]): Unit = native.extern
  def sqlite3_result_zeroblob(p0: native.Ptr[sqlite3_context], n: native.CInt): Unit = native.extern
  def sqlite3_result_zeroblob64(p0: native.Ptr[sqlite3_context], n: sqlite3_uint64): native.CInt = native.extern
  def sqlite3_result_subtype(p0: native.Ptr[sqlite3_context], p1: native.CUnsignedInt): Unit = native.extern
  def sqlite3_create_collation(p0: native.Ptr[sqlite3], zName: native.CString, eTextRep: native.CInt, pArg: native.Ptr[Byte], xCompare: native.CFunctionPtr5[native.Ptr[Byte], native.CInt, native.Ptr[Byte], native.CInt, native.Ptr[Byte], native.CInt]): native.CInt = native.extern
  def sqlite3_create_collation_v2(p0: native.Ptr[sqlite3], zName: native.CString, eTextRep: native.CInt, pArg: native.Ptr[Byte], xCompare: native.CFunctionPtr5[native.Ptr[Byte], native.CInt, native.Ptr[Byte], native.CInt, native.Ptr[Byte], native.CInt], xDestroy: native.CFunctionPtr1[native.Ptr[Byte], Unit]): native.CInt = native.extern
  def sqlite3_create_collation16(p0: native.Ptr[sqlite3], zName: native.Ptr[Byte], eTextRep: native.CInt, pArg: native.Ptr[Byte], xCompare: native.CFunctionPtr5[native.Ptr[Byte], native.CInt, native.Ptr[Byte], native.CInt, native.Ptr[Byte], native.CInt]): native.CInt = native.extern
  def sqlite3_collation_needed(p0: native.Ptr[sqlite3], p1: native.Ptr[Byte], p2: native.CFunctionPtr4[native.Ptr[Byte], native.Ptr[sqlite3], native.CInt, native.CString, Unit]): native.CInt = native.extern
  def sqlite3_collation_needed16(p0: native.Ptr[sqlite3], p1: native.Ptr[Byte], p2: native.CFunctionPtr4[native.Ptr[Byte], native.Ptr[sqlite3], native.CInt, native.Ptr[Byte], Unit]): native.CInt = native.extern
  def sqlite3_sleep(p0: native.CInt): native.CInt = native.extern
  def sqlite3_get_autocommit(p0: native.Ptr[sqlite3]): native.CInt = native.extern
  def sqlite3_db_handle(p0: native.Ptr[sqlite3_stmt]): native.Ptr[sqlite3] = native.extern
  def sqlite3_db_filename(db: native.Ptr[sqlite3], zDbName: native.CString): native.CString = native.extern
  def sqlite3_db_readonly(db: native.Ptr[sqlite3], zDbName: native.CString): native.CInt = native.extern
  def sqlite3_next_stmt(pDb: native.Ptr[sqlite3], pStmt: native.Ptr[sqlite3_stmt]): native.Ptr[sqlite3_stmt] = native.extern
  def sqlite3_commit_hook(p0: native.Ptr[sqlite3], p1: native.CFunctionPtr1[native.Ptr[Byte], native.CInt], p2: native.Ptr[Byte]): native.Ptr[Byte] = native.extern
  def sqlite3_rollback_hook(p0: native.Ptr[sqlite3], p1: native.CFunctionPtr1[native.Ptr[Byte], Unit], p2: native.Ptr[Byte]): native.Ptr[Byte] = native.extern
  def sqlite3_update_hook(p0: native.Ptr[sqlite3], p1: native.CFunctionPtr5[native.Ptr[Byte], native.CInt, native.CString, native.CString, sqlite3_int64, Unit], p2: native.Ptr[Byte]): native.Ptr[Byte] = native.extern
  def sqlite3_enable_shared_cache(p0: native.CInt): native.CInt = native.extern
  def sqlite3_release_memory(p0: native.CInt): native.CInt = native.extern
  def sqlite3_db_release_memory(p0: native.Ptr[sqlite3]): native.CInt = native.extern
  def sqlite3_soft_heap_limit64(N: sqlite3_int64): sqlite3_int64 = native.extern
  def sqlite3_soft_heap_limit(N: native.CInt): Unit = native.extern
  def sqlite3_table_column_metadata(db: native.Ptr[sqlite3], zDbName: native.CString, zTableName: native.CString, zColumnName: native.CString, pzDataType: native.Ptr[native.CString], pzCollSeq: native.Ptr[native.CString], pNotNull: native.Ptr[native.CInt], pPrimaryKey: native.Ptr[native.CInt], pAutoinc: native.Ptr[native.CInt]): native.CInt = native.extern
  def sqlite3_load_extension(db: native.Ptr[sqlite3], zFile: native.CString, zProc: native.CString, pzErrMsg: native.Ptr[native.CString]): native.CInt = native.extern
  def sqlite3_enable_load_extension(db: native.Ptr[sqlite3], onoff: native.CInt): native.CInt = native.extern
  def sqlite3_auto_extension(xEntryPoint: native.CFunctionPtr0[Unit]): native.CInt = native.extern
  def sqlite3_cancel_auto_extension(xEntryPoint: native.CFunctionPtr0[Unit]): native.CInt = native.extern
  def sqlite3_reset_auto_extension(): Unit = native.extern
  // FIXME: see https://github.com/scala-native/scala-native/issues/637 (DBO)
  //def sqlite3_create_module(db: native.Ptr[sqlite3], zName: native.CString, p: native.Ptr[sqlite3_module], pClientData: native.Ptr[Byte]): native.CInt = native.extern
  //def sqlite3_create_module_v2(db: native.Ptr[sqlite3], zName: native.CString, p: native.Ptr[sqlite3_module], pClientData: native.Ptr[Byte], xDestroy: native.CFunctionPtr1[native.Ptr[Byte], Unit]): native.CInt = native.extern
  def sqlite3_declare_vtab(p0: native.Ptr[sqlite3], zSQL: native.CString): native.CInt = native.extern
  def sqlite3_overload_function(p0: native.Ptr[sqlite3], zFuncName: native.CString, nArg: native.CInt): native.CInt = native.extern
  def sqlite3_blob_open(p0: native.Ptr[sqlite3], zDb: native.CString, zTable: native.CString, zColumn: native.CString, iRow: sqlite3_int64, flags: native.CInt, ppBlob: native.Ptr[native.Ptr[sqlite3_blob]]): native.CInt = native.extern
  def sqlite3_blob_reopen(p0: native.Ptr[sqlite3_blob], p1: sqlite3_int64): native.CInt = native.extern
  def sqlite3_blob_close(p0: native.Ptr[sqlite3_blob]): native.CInt = native.extern
  def sqlite3_blob_bytes(p0: native.Ptr[sqlite3_blob]): native.CInt = native.extern
  def sqlite3_blob_read(p0: native.Ptr[sqlite3_blob], Z: native.Ptr[Byte], N: native.CInt, iOffset: native.CInt): native.CInt = native.extern
  def sqlite3_blob_write(p0: native.Ptr[sqlite3_blob], z: native.Ptr[Byte], n: native.CInt, iOffset: native.CInt): native.CInt = native.extern
  def sqlite3_vfs_find(zVfsName: native.CString): native.Ptr[sqlite3_vfs] = native.extern
  def sqlite3_vfs_register(p0: native.Ptr[sqlite3_vfs], makeDflt: native.CInt): native.CInt = native.extern
  def sqlite3_vfs_unregister(p0: native.Ptr[sqlite3_vfs]): native.CInt = native.extern
  def sqlite3_mutex_alloc(p0: native.CInt): native.Ptr[sqlite3_mutex] = native.extern
  def sqlite3_mutex_free(p0: native.Ptr[sqlite3_mutex]): Unit = native.extern
  def sqlite3_mutex_enter(p0: native.Ptr[sqlite3_mutex]): Unit = native.extern
  def sqlite3_mutex_try(p0: native.Ptr[sqlite3_mutex]): native.CInt = native.extern
  def sqlite3_mutex_leave(p0: native.Ptr[sqlite3_mutex]): Unit = native.extern
  def sqlite3_mutex_held(p0: native.Ptr[sqlite3_mutex]): native.CInt = native.extern
  def sqlite3_mutex_notheld(p0: native.Ptr[sqlite3_mutex]): native.CInt = native.extern
  def sqlite3_db_mutex(p0: native.Ptr[sqlite3]): native.Ptr[sqlite3_mutex] = native.extern
  def sqlite3_file_control(p0: native.Ptr[sqlite3], zDbName: native.CString, op: native.CInt, p1: native.Ptr[Byte]): native.CInt = native.extern
  def sqlite3_test_control(op: native.CInt, varArgs: native.CVararg*): native.CInt = native.extern
  def sqlite3_status(op: native.CInt, pCurrent: native.Ptr[native.CInt], pHighwater: native.Ptr[native.CInt], resetFlag: native.CInt): native.CInt = native.extern
  def sqlite3_status64(op: native.CInt, pCurrent: native.Ptr[sqlite3_int64], pHighwater: native.Ptr[sqlite3_int64], resetFlag: native.CInt): native.CInt = native.extern
  def sqlite3_db_status(p0: native.Ptr[sqlite3], op: native.CInt, pCur: native.Ptr[native.CInt], pHiwtr: native.Ptr[native.CInt], resetFlg: native.CInt): native.CInt = native.extern
  def sqlite3_stmt_status(p0: native.Ptr[sqlite3_stmt], op: native.CInt, resetFlg: native.CInt): native.CInt = native.extern
  def sqlite3_backup_init(pDest: native.Ptr[sqlite3], zDestName: native.CString, pSource: native.Ptr[sqlite3], zSourceName: native.CString): native.Ptr[sqlite3_backup] = native.extern
  def sqlite3_backup_step(p: native.Ptr[sqlite3_backup], nPage: native.CInt): native.CInt = native.extern
  def sqlite3_backup_finish(p: native.Ptr[sqlite3_backup]): native.CInt = native.extern
  def sqlite3_backup_remaining(p: native.Ptr[sqlite3_backup]): native.CInt = native.extern
  def sqlite3_backup_pagecount(p: native.Ptr[sqlite3_backup]): native.CInt = native.extern
  def sqlite3_unlock_notify(pBlocked: native.Ptr[sqlite3], xNotify: native.CFunctionPtr2[native.Ptr[native.Ptr[Byte]], native.CInt, Unit], pNotifyArg: native.Ptr[Byte]): native.CInt = native.extern
  def sqlite3_stricmp(p0: native.CString, p1: native.CString): native.CInt = native.extern
  def sqlite3_strnicmp(p0: native.CString, p1: native.CString, p2: native.CInt): native.CInt = native.extern
  def sqlite3_strglob(zGlob: native.CString, zStr: native.CString): native.CInt = native.extern
  def sqlite3_strlike(zGlob: native.CString, zStr: native.CString, cEsc: native.CUnsignedInt): native.CInt = native.extern
  def sqlite3_log(iErrCode: native.CInt, zFormat: native.CString, varArgs: native.CVararg*): Unit = native.extern
  def sqlite3_wal_hook(p0: native.Ptr[sqlite3], p1: native.CFunctionPtr4[native.Ptr[Byte], native.Ptr[sqlite3], native.CString, native.CInt, native.CInt], p2: native.Ptr[Byte]): native.Ptr[Byte] = native.extern
  def sqlite3_wal_autocheckpoint(db: native.Ptr[sqlite3], N: native.CInt): native.CInt = native.extern
  def sqlite3_wal_checkpoint(db: native.Ptr[sqlite3], zDb: native.CString): native.CInt = native.extern
  def sqlite3_wal_checkpoint_v2(db: native.Ptr[sqlite3], zDb: native.CString, eMode: native.CInt, pnLog: native.Ptr[native.CInt], pnCkpt: native.Ptr[native.CInt]): native.CInt = native.extern
  def sqlite3_vtab_config(p0: native.Ptr[sqlite3], op: native.CInt, varArgs: native.CVararg*): native.CInt = native.extern
  def sqlite3_vtab_on_conflict(p0: native.Ptr[sqlite3]): native.CInt = native.extern
  def sqlite3_vtab_nochange(p0: native.Ptr[sqlite3_context]): native.CInt = native.extern
  def sqlite3_vtab_collation(p0: native.Ptr[sqlite3_index_info], p1: native.CInt): native.CString = native.extern
  def sqlite3_stmt_scanstatus(pStmt: native.Ptr[sqlite3_stmt], idx: native.CInt, iScanStatusOp: native.CInt, pOut: native.Ptr[Byte]): native.CInt = native.extern
  def sqlite3_stmt_scanstatus_reset(p0: native.Ptr[sqlite3_stmt]): Unit = native.extern
  def sqlite3_db_cacheflush(p0: native.Ptr[sqlite3]): native.CInt = native.extern
  def sqlite3_system_errno(p0: native.Ptr[sqlite3]): native.CInt = native.extern
  def sqlite3_snapshot_get(db: native.Ptr[sqlite3], zSchema: native.CString, ppSnapshot: native.Ptr[native.Ptr[sqlite3_snapshot]]): native.CInt = native.extern
  def sqlite3_snapshot_open(db: native.Ptr[sqlite3], zSchema: native.CString, pSnapshot: native.Ptr[sqlite3_snapshot]): native.CInt = native.extern
  def sqlite3_snapshot_free(p0: native.Ptr[sqlite3_snapshot]): Unit = native.extern
  def sqlite3_snapshot_cmp(p1: native.Ptr[sqlite3_snapshot], p2: native.Ptr[sqlite3_snapshot]): native.CInt = native.extern
  def sqlite3_snapshot_recover(db: native.Ptr[sqlite3], zDb: native.CString): native.CInt = native.extern
  def sqlite3_rtree_geometry_callback(db: native.Ptr[sqlite3], zGeom: native.CString, xGeom: native.CFunctionPtr4[native.Ptr[sqlite3_rtree_geometry], native.CInt, native.Ptr[sqlite3_rtree_dbl], native.Ptr[native.CInt], native.CInt], pContext: native.Ptr[Byte]): native.CInt = native.extern
  def sqlite3_rtree_query_callback(db: native.Ptr[sqlite3], zQueryFunc: native.CString, xQueryFunc: native.CFunctionPtr1[native.Ptr[sqlite3_rtree_query_info], native.CInt], pContext: native.Ptr[Byte], xDestructor: native.CFunctionPtr1[native.Ptr[Byte], Unit]): native.CInt = native.extern

  object SQLITE_CONSTANT {
    val SQLITE_VERSION: native.CString = c"3.22.0"
    val SQLITE_VERSION_NUMBER: native.CInt = 3022000
    val SQLITE_SOURCE_ID: native.CString = c"2018-01-22 18:45:57 0c55d179733b46d8d0ba4d88e01a25e10677046ee3da1d5b1581e86726f2alt1"
    val SQLITE_OK: native.CInt = 0
    val SQLITE_ERROR: native.CInt = 1
    val SQLITE_INTERNAL: native.CInt = 2
    val SQLITE_PERM: native.CInt = 3
    val SQLITE_ABORT: native.CInt = 4
    val SQLITE_BUSY: native.CInt = 5
    val SQLITE_LOCKED: native.CInt = 6
    val SQLITE_NOMEM: native.CInt = 7
    val SQLITE_READONLY: native.CInt = 8
    val SQLITE_INTERRUPT: native.CInt = 9
    val SQLITE_IOERR: native.CInt = 10
    val SQLITE_CORRUPT: native.CInt = 11
    val SQLITE_NOTFOUND: native.CInt = 12
    val SQLITE_FULL: native.CInt = 13
    val SQLITE_CANTOPEN: native.CInt = 14
    val SQLITE_PROTOCOL: native.CInt = 15
    val SQLITE_EMPTY: native.CInt = 16
    val SQLITE_SCHEMA: native.CInt = 17
    val SQLITE_TOOBIG: native.CInt = 18
    val SQLITE_CONSTRAINT: native.CInt = 19
    val SQLITE_MISMATCH: native.CInt = 20
    val SQLITE_MISUSE: native.CInt = 21
    val SQLITE_NOLFS: native.CInt = 22
    val SQLITE_AUTH: native.CInt = 23
    val SQLITE_FORMAT: native.CInt = 24
    val SQLITE_RANGE: native.CInt = 25
    val SQLITE_NOTADB: native.CInt = 26
    val SQLITE_NOTICE: native.CInt = 27
    val SQLITE_WARNING: native.CInt = 28
    val SQLITE_ROW: native.CInt = 100
    val SQLITE_DONE: native.CInt = 101
    val SQLITE_OPEN_READONLY: native.CInt = 1
    val SQLITE_OPEN_READWRITE: native.CInt = 2
    val SQLITE_OPEN_CREATE: native.CInt = 4
    val SQLITE_OPEN_DELETEONCLOSE: native.CInt = 8
    val SQLITE_OPEN_EXCLUSIVE: native.CInt = 16
    val SQLITE_OPEN_AUTOPROXY: native.CInt = 32
    val SQLITE_OPEN_URI: native.CInt = 64
    val SQLITE_OPEN_MEMORY: native.CInt = 128
    val SQLITE_OPEN_MAIN_DB: native.CInt = 256
    val SQLITE_OPEN_TEMP_DB: native.CInt = 512
    val SQLITE_OPEN_TRANSIENT_DB: native.CInt = 1024
    val SQLITE_OPEN_MAIN_JOURNAL: native.CInt = 2048
    val SQLITE_OPEN_TEMP_JOURNAL: native.CInt = 4096
    val SQLITE_OPEN_SUBJOURNAL: native.CInt = 8192
    val SQLITE_OPEN_MASTER_JOURNAL: native.CInt = 16384
    val SQLITE_OPEN_NOMUTEX: native.CInt = 32768
    val SQLITE_OPEN_FULLMUTEX: native.CInt = 65536
    val SQLITE_OPEN_SHAREDCACHE: native.CInt = 131072
    val SQLITE_OPEN_PRIVATECACHE: native.CInt = 262144
    val SQLITE_OPEN_WAL: native.CInt = 524288
    val SQLITE_IOCAP_ATOMIC: native.CInt = 1
    val SQLITE_IOCAP_ATOMIC512: native.CInt = 2
    val SQLITE_IOCAP_ATOMIC1K: native.CInt = 4
    val SQLITE_IOCAP_ATOMIC2K: native.CInt = 8
    val SQLITE_IOCAP_ATOMIC4K: native.CInt = 16
    val SQLITE_IOCAP_ATOMIC8K: native.CInt = 32
    val SQLITE_IOCAP_ATOMIC16K: native.CInt = 64
    val SQLITE_IOCAP_ATOMIC32K: native.CInt = 128
    val SQLITE_IOCAP_ATOMIC64K: native.CInt = 256
    val SQLITE_IOCAP_SAFE_APPEND: native.CInt = 512
    val SQLITE_IOCAP_SEQUENTIAL: native.CInt = 1024
    val SQLITE_IOCAP_UNDELETABLE_WHEN_OPEN: native.CInt = 2048
    val SQLITE_IOCAP_POWERSAFE_OVERWRITE: native.CInt = 4096
    val SQLITE_IOCAP_IMMUTABLE: native.CInt = 8192
    val SQLITE_IOCAP_BATCH_ATOMIC: native.CInt = 16384
    val SQLITE_LOCK_NONE: native.CInt = 0
    val SQLITE_LOCK_SHARED: native.CInt = 1
    val SQLITE_LOCK_RESERVED: native.CInt = 2
    val SQLITE_LOCK_PENDING: native.CInt = 3
    val SQLITE_LOCK_EXCLUSIVE: native.CInt = 4
    val SQLITE_SYNC_NORMAL: native.CInt = 2
    val SQLITE_SYNC_FULL: native.CInt = 3
    val SQLITE_SYNC_DATAONLY: native.CInt = 16
    val SQLITE_FCNTL_LOCKSTATE: native.CInt = 1
    val SQLITE_FCNTL_GET_LOCKPROXYFILE: native.CInt = 2
    val SQLITE_FCNTL_SET_LOCKPROXYFILE: native.CInt = 3
    val SQLITE_FCNTL_LAST_ERRNO: native.CInt = 4
    val SQLITE_FCNTL_SIZE_HINT: native.CInt = 5
    val SQLITE_FCNTL_CHUNK_SIZE: native.CInt = 6
    val SQLITE_FCNTL_FILE_POINTER: native.CInt = 7
    val SQLITE_FCNTL_SYNC_OMITTED: native.CInt = 8
    val SQLITE_FCNTL_WIN32_AV_RETRY: native.CInt = 9
    val SQLITE_FCNTL_PERSIST_WAL: native.CInt = 10
    val SQLITE_FCNTL_OVERWRITE: native.CInt = 11
    val SQLITE_FCNTL_VFSNAME: native.CInt = 12
    val SQLITE_FCNTL_POWERSAFE_OVERWRITE: native.CInt = 13
    val SQLITE_FCNTL_PRAGMA: native.CInt = 14
    val SQLITE_FCNTL_BUSYHANDLER: native.CInt = 15
    val SQLITE_FCNTL_TEMPFILENAME: native.CInt = 16
    val SQLITE_FCNTL_MMAP_SIZE: native.CInt = 18
    val SQLITE_FCNTL_TRACE: native.CInt = 19
    val SQLITE_FCNTL_HAS_MOVED: native.CInt = 20
    val SQLITE_FCNTL_SYNC: native.CInt = 21
    val SQLITE_FCNTL_COMMIT_PHASETWO: native.CInt = 22
    val SQLITE_FCNTL_WIN32_SET_HANDLE: native.CInt = 23
    val SQLITE_FCNTL_WAL_BLOCK: native.CInt = 24
    val SQLITE_FCNTL_ZIPVFS: native.CInt = 25
    val SQLITE_FCNTL_RBU: native.CInt = 26
    val SQLITE_FCNTL_VFS_POINTER: native.CInt = 27
    val SQLITE_FCNTL_JOURNAL_POINTER: native.CInt = 28
    val SQLITE_FCNTL_WIN32_GET_HANDLE: native.CInt = 29
    val SQLITE_FCNTL_PDB: native.CInt = 30
    val SQLITE_FCNTL_BEGIN_ATOMIC_WRITE: native.CInt = 31
    val SQLITE_FCNTL_COMMIT_ATOMIC_WRITE: native.CInt = 32
    val SQLITE_FCNTL_ROLLBACK_ATOMIC_WRITE: native.CInt = 33
    val SQLITE_GET_LOCKPROXYFILE: native.CInt = 2
    val SQLITE_SET_LOCKPROXYFILE: native.CInt = 3
    val SQLITE_LAST_ERRNO: native.CInt = 4
    val SQLITE_ACCESS_EXISTS: native.CInt = 0
    val SQLITE_ACCESS_READWRITE: native.CInt = 1
    val SQLITE_ACCESS_READ: native.CInt = 2
    val SQLITE_SHM_UNLOCK: native.CInt = 1
    val SQLITE_SHM_LOCK: native.CInt = 2
    val SQLITE_SHM_SHARED: native.CInt = 4
    val SQLITE_SHM_EXCLUSIVE: native.CInt = 8
    val SQLITE_SHM_NLOCK: native.CInt = 8
    val SQLITE_CONFIG_SINGLETHREAD: native.CInt = 1
    val SQLITE_CONFIG_MULTITHREAD: native.CInt = 2
    val SQLITE_CONFIG_SERIALIZED: native.CInt = 3
    val SQLITE_CONFIG_MALLOC: native.CInt = 4
    val SQLITE_CONFIG_GETMALLOC: native.CInt = 5
    val SQLITE_CONFIG_SCRATCH: native.CInt = 6
    val SQLITE_CONFIG_PAGECACHE: native.CInt = 7
    val SQLITE_CONFIG_HEAP: native.CInt = 8
    val SQLITE_CONFIG_MEMSTATUS: native.CInt = 9
    val SQLITE_CONFIG_MUTEX: native.CInt = 10
    val SQLITE_CONFIG_GETMUTEX: native.CInt = 11
    val SQLITE_CONFIG_LOOKASIDE: native.CInt = 13
    val SQLITE_CONFIG_PCACHE: native.CInt = 14
    val SQLITE_CONFIG_GETPCACHE: native.CInt = 15
    val SQLITE_CONFIG_LOG: native.CInt = 16
    val SQLITE_CONFIG_URI: native.CInt = 17
    val SQLITE_CONFIG_PCACHE2: native.CInt = 18
    val SQLITE_CONFIG_GETPCACHE2: native.CInt = 19
    val SQLITE_CONFIG_COVERING_INDEX_SCAN: native.CInt = 20
    val SQLITE_CONFIG_SQLLOG: native.CInt = 21
    val SQLITE_CONFIG_MMAP_SIZE: native.CInt = 22
    val SQLITE_CONFIG_WIN32_HEAPSIZE: native.CInt = 23
    val SQLITE_CONFIG_PCACHE_HDRSZ: native.CInt = 24
    val SQLITE_CONFIG_PMASZ: native.CInt = 25
    val SQLITE_CONFIG_STMTJRNL_SPILL: native.CInt = 26
    val SQLITE_CONFIG_SMALL_MALLOC: native.CInt = 27
    val SQLITE_DBCONFIG_MAINDBNAME: native.CInt = 1000
    val SQLITE_DBCONFIG_LOOKASIDE: native.CInt = 1001
    val SQLITE_DBCONFIG_ENABLE_FKEY: native.CInt = 1002
    val SQLITE_DBCONFIG_ENABLE_TRIGGER: native.CInt = 1003
    val SQLITE_DBCONFIG_ENABLE_FTS3_TOKENIZER: native.CInt = 1004
    val SQLITE_DBCONFIG_ENABLE_LOAD_EXTENSION: native.CInt = 1005
    val SQLITE_DBCONFIG_NO_CKPT_ON_CLOSE: native.CInt = 1006
    val SQLITE_DBCONFIG_ENABLE_QPSG: native.CInt = 1007
    val SQLITE_DBCONFIG_TRIGGER_EQP: native.CInt = 1008
    val SQLITE_DBCONFIG_MAX: native.CInt = 1008
    val SQLITE_DENY: native.CInt = 1
    val SQLITE_IGNORE: native.CInt = 2
    val SQLITE_CREATE_INDEX: native.CInt = 1
    val SQLITE_CREATE_TABLE: native.CInt = 2
    val SQLITE_CREATE_TEMP_INDEX: native.CInt = 3
    val SQLITE_CREATE_TEMP_TABLE: native.CInt = 4
    val SQLITE_CREATE_TEMP_TRIGGER: native.CInt = 5
    val SQLITE_CREATE_TEMP_VIEW: native.CInt = 6
    val SQLITE_CREATE_TRIGGER: native.CInt = 7
    val SQLITE_CREATE_VIEW: native.CInt = 8
    val SQLITE_DELETE: native.CInt = 9
    val SQLITE_DROP_INDEX: native.CInt = 10
    val SQLITE_DROP_TABLE: native.CInt = 11
    val SQLITE_DROP_TEMP_INDEX: native.CInt = 12
    val SQLITE_DROP_TEMP_TABLE: native.CInt = 13
    val SQLITE_DROP_TEMP_TRIGGER: native.CInt = 14
    val SQLITE_DROP_TEMP_VIEW: native.CInt = 15
    val SQLITE_DROP_TRIGGER: native.CInt = 16
    val SQLITE_DROP_VIEW: native.CInt = 17
    val SQLITE_INSERT: native.CInt = 18
    val SQLITE_PRAGMA: native.CInt = 19
    val SQLITE_READ: native.CInt = 20
    val SQLITE_SELECT: native.CInt = 21
    val SQLITE_TRANSACTION: native.CInt = 22
    val SQLITE_UPDATE: native.CInt = 23
    val SQLITE_ATTACH: native.CInt = 24
    val SQLITE_DETACH: native.CInt = 25
    val SQLITE_ALTER_TABLE: native.CInt = 26
    val SQLITE_REINDEX: native.CInt = 27
    val SQLITE_ANALYZE: native.CInt = 28
    val SQLITE_CREATE_VTABLE: native.CInt = 29
    val SQLITE_DROP_VTABLE: native.CInt = 30
    val SQLITE_FUNCTION: native.CInt = 31
    val SQLITE_SAVEPOINT: native.CInt = 32
    val SQLITE_COPY: native.CInt = 0
    val SQLITE_RECURSIVE: native.CInt = 33
    val SQLITE_TRACE_STMT: native.CInt = 1
    val SQLITE_TRACE_PROFILE: native.CInt = 2
    val SQLITE_TRACE_ROW: native.CInt = 4
    val SQLITE_TRACE_CLOSE: native.CInt = 8
    val SQLITE_LIMIT_LENGTH: native.CInt = 0
    val SQLITE_LIMIT_SQL_LENGTH: native.CInt = 1
    val SQLITE_LIMIT_COLUMN: native.CInt = 2
    val SQLITE_LIMIT_EXPR_DEPTH: native.CInt = 3
    val SQLITE_LIMIT_COMPOUND_SELECT: native.CInt = 4
    val SQLITE_LIMIT_VDBE_OP: native.CInt = 5
    val SQLITE_LIMIT_FUNCTION_ARG: native.CInt = 6
    val SQLITE_LIMIT_ATTACHED: native.CInt = 7
    val SQLITE_LIMIT_LIKE_PATTERN_LENGTH: native.CInt = 8
    val SQLITE_LIMIT_VARIABLE_NUMBER: native.CInt = 9
    val SQLITE_LIMIT_TRIGGER_DEPTH: native.CInt = 10
    val SQLITE_LIMIT_WORKER_THREADS: native.CInt = 11
    val SQLITE_PREPARE_PERSISTENT: native.CInt = 1
    val SQLITE_INTEGER: native.CInt = 1
    val SQLITE_FLOAT: native.CInt = 2
    val SQLITE_BLOB: native.CInt = 4
    val SQLITE_NULL: native.CInt = 5
    val SQLITE_TEXT: native.CInt = 3
    val SQLITE3_TEXT: native.CInt = 3
    val SQLITE_UTF8: native.CInt = 1
    val SQLITE_UTF16LE: native.CInt = 2
    val SQLITE_UTF16BE: native.CInt = 3
    val SQLITE_UTF16: native.CInt = 4
    val SQLITE_ANY: native.CInt = 5
    val SQLITE_UTF16_ALIGNED: native.CInt = 8
    val SQLITE_DETERMINISTIC: native.CInt = 2048
    val SQLITE_INDEX_SCAN_UNIQUE: native.CInt = 1
    val SQLITE_INDEX_CONSTRAINT_EQ: native.CInt = 2
    val SQLITE_INDEX_CONSTRAINT_GT: native.CInt = 4
    val SQLITE_INDEX_CONSTRAINT_LE: native.CInt = 8
    val SQLITE_INDEX_CONSTRAINT_LT: native.CInt = 16
    val SQLITE_INDEX_CONSTRAINT_GE: native.CInt = 32
    val SQLITE_INDEX_CONSTRAINT_MATCH: native.CInt = 64
    val SQLITE_INDEX_CONSTRAINT_LIKE: native.CInt = 65
    val SQLITE_INDEX_CONSTRAINT_GLOB: native.CInt = 66
    val SQLITE_INDEX_CONSTRAINT_REGEXP: native.CInt = 67
    val SQLITE_INDEX_CONSTRAINT_NE: native.CInt = 68
    val SQLITE_INDEX_CONSTRAINT_ISNOT: native.CInt = 69
    val SQLITE_INDEX_CONSTRAINT_ISNOTNULL: native.CInt = 70
    val SQLITE_INDEX_CONSTRAINT_ISNULL: native.CInt = 71
    val SQLITE_INDEX_CONSTRAINT_IS: native.CInt = 72
    val SQLITE_MUTEX_FAST: native.CInt = 0
    val SQLITE_MUTEX_RECURSIVE: native.CInt = 1
    val SQLITE_MUTEX_STATIC_MASTER: native.CInt = 2
    val SQLITE_MUTEX_STATIC_MEM: native.CInt = 3
    val SQLITE_MUTEX_STATIC_MEM2: native.CInt = 4
    val SQLITE_MUTEX_STATIC_OPEN: native.CInt = 4
    val SQLITE_MUTEX_STATIC_PRNG: native.CInt = 5
    val SQLITE_MUTEX_STATIC_LRU: native.CInt = 6
    val SQLITE_MUTEX_STATIC_LRU2: native.CInt = 7
    val SQLITE_MUTEX_STATIC_PMEM: native.CInt = 7
    val SQLITE_MUTEX_STATIC_APP1: native.CInt = 8
    val SQLITE_MUTEX_STATIC_APP2: native.CInt = 9
    val SQLITE_MUTEX_STATIC_APP3: native.CInt = 10
    val SQLITE_MUTEX_STATIC_VFS1: native.CInt = 11
    val SQLITE_MUTEX_STATIC_VFS2: native.CInt = 12
    val SQLITE_MUTEX_STATIC_VFS3: native.CInt = 13
    val SQLITE_TESTCTRL_FIRST: native.CInt = 5
    val SQLITE_TESTCTRL_PRNG_SAVE: native.CInt = 5
    val SQLITE_TESTCTRL_PRNG_RESTORE: native.CInt = 6
    val SQLITE_TESTCTRL_PRNG_RESET: native.CInt = 7
    val SQLITE_TESTCTRL_BITVEC_TEST: native.CInt = 8
    val SQLITE_TESTCTRL_FAULT_INSTALL: native.CInt = 9
    val SQLITE_TESTCTRL_BENIGN_MALLOC_HOOKS: native.CInt = 10
    val SQLITE_TESTCTRL_PENDING_BYTE: native.CInt = 11
    val SQLITE_TESTCTRL_ASSERT: native.CInt = 12
    val SQLITE_TESTCTRL_ALWAYS: native.CInt = 13
    val SQLITE_TESTCTRL_RESERVE: native.CInt = 14
    val SQLITE_TESTCTRL_OPTIMIZATIONS: native.CInt = 15
    val SQLITE_TESTCTRL_ISKEYWORD: native.CInt = 16
    val SQLITE_TESTCTRL_SCRATCHMALLOC: native.CInt = 17
    val SQLITE_TESTCTRL_LOCALTIME_FAULT: native.CInt = 18
    val SQLITE_TESTCTRL_EXPLAIN_STMT: native.CInt = 19
    val SQLITE_TESTCTRL_ONCE_RESET_THRESHOLD: native.CInt = 19
    val SQLITE_TESTCTRL_NEVER_CORRUPT: native.CInt = 20
    val SQLITE_TESTCTRL_VDBE_COVERAGE: native.CInt = 21
    val SQLITE_TESTCTRL_BYTEORDER: native.CInt = 22
    val SQLITE_TESTCTRL_ISINIT: native.CInt = 23
    val SQLITE_TESTCTRL_SORTER_MMAP: native.CInt = 24
    val SQLITE_TESTCTRL_IMPOSTER: native.CInt = 25
    val SQLITE_TESTCTRL_PARSER_COVERAGE: native.CInt = 26
    val SQLITE_TESTCTRL_LAST: native.CInt = 26
    val SQLITE_STATUS_MEMORY_USED: native.CInt = 0
    val SQLITE_STATUS_PAGECACHE_USED: native.CInt = 1
    val SQLITE_STATUS_PAGECACHE_OVERFLOW: native.CInt = 2
    val SQLITE_STATUS_SCRATCH_USED: native.CInt = 3
    val SQLITE_STATUS_SCRATCH_OVERFLOW: native.CInt = 4
    val SQLITE_STATUS_MALLOC_SIZE: native.CInt = 5
    val SQLITE_STATUS_PARSER_STACK: native.CInt = 6
    val SQLITE_STATUS_PAGECACHE_SIZE: native.CInt = 7
    val SQLITE_STATUS_SCRATCH_SIZE: native.CInt = 8
    val SQLITE_STATUS_MALLOC_COUNT: native.CInt = 9
    val SQLITE_DBSTATUS_LOOKASIDE_USED: native.CInt = 0
    val SQLITE_DBSTATUS_CACHE_USED: native.CInt = 1
    val SQLITE_DBSTATUS_SCHEMA_USED: native.CInt = 2
    val SQLITE_DBSTATUS_STMT_USED: native.CInt = 3
    val SQLITE_DBSTATUS_LOOKASIDE_HIT: native.CInt = 4
    val SQLITE_DBSTATUS_LOOKASIDE_MISS_SIZE: native.CInt = 5
    val SQLITE_DBSTATUS_LOOKASIDE_MISS_FULL: native.CInt = 6
    val SQLITE_DBSTATUS_CACHE_HIT: native.CInt = 7
    val SQLITE_DBSTATUS_CACHE_MISS: native.CInt = 8
    val SQLITE_DBSTATUS_CACHE_WRITE: native.CInt = 9
    val SQLITE_DBSTATUS_DEFERRED_FKS: native.CInt = 10
    val SQLITE_DBSTATUS_CACHE_USED_SHARED: native.CInt = 11
    val SQLITE_DBSTATUS_MAX: native.CInt = 11
    val SQLITE_STMTSTATUS_FULLSCAN_STEP: native.CInt = 1
    val SQLITE_STMTSTATUS_SORT: native.CInt = 2
    val SQLITE_STMTSTATUS_AUTOINDEX: native.CInt = 3
    val SQLITE_STMTSTATUS_VM_STEP: native.CInt = 4
    val SQLITE_STMTSTATUS_REPREPARE: native.CInt = 5
    val SQLITE_STMTSTATUS_RUN: native.CInt = 6
    val SQLITE_STMTSTATUS_MEMUSED: native.CInt = 99
    val SQLITE_CHECKPOINT_PASSIVE: native.CInt = 0
    val SQLITE_CHECKPOINT_FULL: native.CInt = 1
    val SQLITE_CHECKPOINT_RESTART: native.CInt = 2
    val SQLITE_CHECKPOINT_TRUNCATE: native.CInt = 3
    val SQLITE_VTAB_CONSTRAINT_SUPPORT: native.CInt = 1
    val SQLITE_ROLLBACK: native.CInt = 1
    val SQLITE_FAIL: native.CInt = 3
    val SQLITE_REPLACE: native.CInt = 5
    val SQLITE_SCANSTAT_NLOOP: native.CInt = 0
    val SQLITE_SCANSTAT_NVISIT: native.CInt = 1
    val SQLITE_SCANSTAT_EST: native.CInt = 2
    val SQLITE_SCANSTAT_NAME: native.CInt = 3
    val SQLITE_SCANSTAT_EXPLAIN: native.CInt = 4
    val SQLITE_SCANSTAT_SELECTID: native.CInt = 5
    val NOT_WITHIN: native.CInt = 0
    val PARTLY_WITHIN: native.CInt = 1
    val FULLY_WITHIN: native.CInt = 2
    val FTS5_TOKENIZE_QUERY: native.CInt = 1
    val FTS5_TOKENIZE_PREFIX: native.CInt = 2
    val FTS5_TOKENIZE_DOCUMENT: native.CInt = 4
    val FTS5_TOKENIZE_AUX: native.CInt = 8
    val FTS5_TOKEN_COLOCATED: native.CInt = 1
  }

  object implicits {
    implicit class struct_sqlite3_file_ops(val p: native.Ptr[struct_sqlite3_file]) extends AnyVal {
      def pMethods: native.Ptr[struct_sqlite3_io_methods] = !p._1
      def pMethods_=(value: native.Ptr[struct_sqlite3_io_methods]): Unit = !p._1 = value
    }
    def struct_sqlite3_file()(implicit z: native.Zone): native.Ptr[struct_sqlite3_file] = native.alloc[struct_sqlite3_file]

    implicit class struct_sqlite3_io_methods_ops(val p: native.Ptr[struct_sqlite3_io_methods]) extends AnyVal {
      def iVersion: native.CInt = !p._1
      def iVersion_=(value: native.CInt): Unit = !p._1 = value
      def xClose: native.CFunctionPtr1[native.Ptr[sqlite3_file], native.CInt] = (!p._2).cast[native.CFunctionPtr1[native.Ptr[sqlite3_file], native.CInt]]
      def xClose_=(value: native.CFunctionPtr1[native.Ptr[sqlite3_file], native.CInt]): Unit = !p._2 = value.cast[native.CFunctionPtr1[native.Ptr[Byte], native.CInt]]
      def xRead: native.CFunctionPtr4[native.Ptr[sqlite3_file], native.Ptr[Byte], native.CInt, sqlite3_int64, native.CInt] = (!p._3).cast[native.CFunctionPtr4[native.Ptr[sqlite3_file], native.Ptr[Byte], native.CInt, sqlite3_int64, native.CInt]]
      def xRead_=(value: native.CFunctionPtr4[native.Ptr[sqlite3_file], native.Ptr[Byte], native.CInt, sqlite3_int64, native.CInt]): Unit = !p._3 = value.cast[native.CFunctionPtr4[native.Ptr[Byte], native.Ptr[Byte], native.CInt, native.CLongLong, native.CInt]]
      def xWrite: native.CFunctionPtr4[native.Ptr[sqlite3_file], native.Ptr[Byte], native.CInt, sqlite3_int64, native.CInt] = (!p._4).cast[native.CFunctionPtr4[native.Ptr[sqlite3_file], native.Ptr[Byte], native.CInt, sqlite3_int64, native.CInt]]
      def xWrite_=(value: native.CFunctionPtr4[native.Ptr[sqlite3_file], native.Ptr[Byte], native.CInt, sqlite3_int64, native.CInt]): Unit = !p._4 = value.cast[native.CFunctionPtr4[native.Ptr[Byte], native.Ptr[Byte], native.CInt, native.CLongLong, native.CInt]]
      def xTruncate: native.CFunctionPtr2[native.Ptr[sqlite3_file], sqlite3_int64, native.CInt] = (!p._5).cast[native.CFunctionPtr2[native.Ptr[sqlite3_file], sqlite3_int64, native.CInt]]
      def xTruncate_=(value: native.CFunctionPtr2[native.Ptr[sqlite3_file], sqlite3_int64, native.CInt]): Unit = !p._5 = value.cast[native.CFunctionPtr2[native.Ptr[Byte], native.CLongLong, native.CInt]]
      def xSync: native.CFunctionPtr2[native.Ptr[sqlite3_file], native.CInt, native.CInt] = (!p._6).cast[native.CFunctionPtr2[native.Ptr[sqlite3_file], native.CInt, native.CInt]]
      def xSync_=(value: native.CFunctionPtr2[native.Ptr[sqlite3_file], native.CInt, native.CInt]): Unit = !p._6 = value.cast[native.CFunctionPtr2[native.Ptr[Byte], native.CInt, native.CInt]]
      def xFileSize: native.CFunctionPtr2[native.Ptr[sqlite3_file], native.Ptr[sqlite3_int64], native.CInt] = (!p._7).cast[native.CFunctionPtr2[native.Ptr[sqlite3_file], native.Ptr[sqlite3_int64], native.CInt]]
      def xFileSize_=(value: native.CFunctionPtr2[native.Ptr[sqlite3_file], native.Ptr[sqlite3_int64], native.CInt]): Unit = !p._7 = value.cast[native.CFunctionPtr2[native.Ptr[Byte], native.Ptr[native.CLongLong], native.CInt]]
      def xLock: native.CFunctionPtr2[native.Ptr[sqlite3_file], native.CInt, native.CInt] = (!p._8).cast[native.CFunctionPtr2[native.Ptr[sqlite3_file], native.CInt, native.CInt]]
      def xLock_=(value: native.CFunctionPtr2[native.Ptr[sqlite3_file], native.CInt, native.CInt]): Unit = !p._8 = value.cast[native.CFunctionPtr2[native.Ptr[Byte], native.CInt, native.CInt]]
      def xUnlock: native.CFunctionPtr2[native.Ptr[sqlite3_file], native.CInt, native.CInt] = (!p._9).cast[native.CFunctionPtr2[native.Ptr[sqlite3_file], native.CInt, native.CInt]]
      def xUnlock_=(value: native.CFunctionPtr2[native.Ptr[sqlite3_file], native.CInt, native.CInt]): Unit = !p._9 = value.cast[native.CFunctionPtr2[native.Ptr[Byte], native.CInt, native.CInt]]
      def xCheckReservedLock: native.CFunctionPtr2[native.Ptr[sqlite3_file], native.Ptr[native.CInt], native.CInt] = (!p._10).cast[native.CFunctionPtr2[native.Ptr[sqlite3_file], native.Ptr[native.CInt], native.CInt]]
      def xCheckReservedLock_=(value: native.CFunctionPtr2[native.Ptr[sqlite3_file], native.Ptr[native.CInt], native.CInt]): Unit = !p._10 = value.cast[native.CFunctionPtr2[native.Ptr[Byte], native.Ptr[native.CInt], native.CInt]]
      def xFileControl: native.CFunctionPtr3[native.Ptr[sqlite3_file], native.CInt, native.Ptr[Byte], native.CInt] = (!p._11).cast[native.CFunctionPtr3[native.Ptr[sqlite3_file], native.CInt, native.Ptr[Byte], native.CInt]]
      def xFileControl_=(value: native.CFunctionPtr3[native.Ptr[sqlite3_file], native.CInt, native.Ptr[Byte], native.CInt]): Unit = !p._11 = value.cast[native.CFunctionPtr3[native.Ptr[Byte], native.CInt, native.Ptr[Byte], native.CInt]]
      def xSectorSize: native.CFunctionPtr1[native.Ptr[sqlite3_file], native.CInt] = (!p._12).cast[native.CFunctionPtr1[native.Ptr[sqlite3_file], native.CInt]]
      def xSectorSize_=(value: native.CFunctionPtr1[native.Ptr[sqlite3_file], native.CInt]): Unit = !p._12 = value.cast[native.CFunctionPtr1[native.Ptr[Byte], native.CInt]]
      def xDeviceCharacteristics: native.CFunctionPtr1[native.Ptr[sqlite3_file], native.CInt] = (!p._13).cast[native.CFunctionPtr1[native.Ptr[sqlite3_file], native.CInt]]
      def xDeviceCharacteristics_=(value: native.CFunctionPtr1[native.Ptr[sqlite3_file], native.CInt]): Unit = !p._13 = value.cast[native.CFunctionPtr1[native.Ptr[Byte], native.CInt]]
      def xShmMap: native.CFunctionPtr5[native.Ptr[sqlite3_file], native.CInt, native.CInt, native.CInt, native.Ptr[native.Ptr[Byte]], native.CInt] = (!p._14).cast[native.CFunctionPtr5[native.Ptr[sqlite3_file], native.CInt, native.CInt, native.CInt, native.Ptr[native.Ptr[Byte]], native.CInt]]
      def xShmMap_=(value: native.CFunctionPtr5[native.Ptr[sqlite3_file], native.CInt, native.CInt, native.CInt, native.Ptr[native.Ptr[Byte]], native.CInt]): Unit = !p._14 = value.cast[native.CFunctionPtr5[native.Ptr[Byte], native.CInt, native.CInt, native.CInt, native.Ptr[native.Ptr[Byte]], native.CInt]]
      def xShmLock: native.CFunctionPtr4[native.Ptr[sqlite3_file], native.CInt, native.CInt, native.CInt, native.CInt] = (!p._15).cast[native.CFunctionPtr4[native.Ptr[sqlite3_file], native.CInt, native.CInt, native.CInt, native.CInt]]
      def xShmLock_=(value: native.CFunctionPtr4[native.Ptr[sqlite3_file], native.CInt, native.CInt, native.CInt, native.CInt]): Unit = !p._15 = value.cast[native.CFunctionPtr4[native.Ptr[Byte], native.CInt, native.CInt, native.CInt, native.CInt]]
      def xShmBarrier: native.CFunctionPtr1[native.Ptr[sqlite3_file], Unit] = (!p._16).cast[native.CFunctionPtr1[native.Ptr[sqlite3_file], Unit]]
      def xShmBarrier_=(value: native.CFunctionPtr1[native.Ptr[sqlite3_file], Unit]): Unit = !p._16 = value.cast[native.CFunctionPtr1[native.Ptr[Byte], Unit]]
      def xShmUnmap: native.CFunctionPtr2[native.Ptr[sqlite3_file], native.CInt, native.CInt] = (!p._17).cast[native.CFunctionPtr2[native.Ptr[sqlite3_file], native.CInt, native.CInt]]
      def xShmUnmap_=(value: native.CFunctionPtr2[native.Ptr[sqlite3_file], native.CInt, native.CInt]): Unit = !p._17 = value.cast[native.CFunctionPtr2[native.Ptr[Byte], native.CInt, native.CInt]]
      def xFetch: native.CFunctionPtr4[native.Ptr[sqlite3_file], sqlite3_int64, native.CInt, native.Ptr[native.Ptr[Byte]], native.CInt] = (!p._18).cast[native.CFunctionPtr4[native.Ptr[sqlite3_file], sqlite3_int64, native.CInt, native.Ptr[native.Ptr[Byte]], native.CInt]]
      def xFetch_=(value: native.CFunctionPtr4[native.Ptr[sqlite3_file], sqlite3_int64, native.CInt, native.Ptr[native.Ptr[Byte]], native.CInt]): Unit = !p._18 = value.cast[native.CFunctionPtr4[native.Ptr[Byte], native.CLongLong, native.CInt, native.Ptr[native.Ptr[Byte]], native.CInt]]
      def xUnfetch: native.CFunctionPtr3[native.Ptr[sqlite3_file], sqlite3_int64, native.Ptr[Byte], native.CInt] = (!p._19).cast[native.CFunctionPtr3[native.Ptr[sqlite3_file], sqlite3_int64, native.Ptr[Byte], native.CInt]]
      def xUnfetch_=(value: native.CFunctionPtr3[native.Ptr[sqlite3_file], sqlite3_int64, native.Ptr[Byte], native.CInt]): Unit = !p._19 = value.cast[native.CFunctionPtr3[native.Ptr[Byte], native.CLongLong, native.Ptr[Byte], native.CInt]]
    }
    def struct_sqlite3_io_methods()(implicit z: native.Zone): native.Ptr[struct_sqlite3_io_methods] = native.alloc[struct_sqlite3_io_methods]

    implicit class struct_sqlite3_vfs_ops(val p: native.Ptr[struct_sqlite3_vfs]) extends AnyVal {
      def iVersion: native.CInt = !p._1
      def iVersion_=(value: native.CInt): Unit = !p._1 = value
      def szOsFile: native.CInt = !p._2
      def szOsFile_=(value: native.CInt): Unit = !p._2 = value
      def mxPathname: native.CInt = !p._3
      def mxPathname_=(value: native.CInt): Unit = !p._3 = value
      def pNext: native.Ptr[sqlite3_vfs] = (!p._4).cast[native.Ptr[sqlite3_vfs]]
      def pNext_=(value: native.Ptr[sqlite3_vfs]): Unit = !p._4 = value.cast[native.Ptr[Byte]]
      def zName: native.CString = !p._5
      def zName_=(value: native.CString): Unit = !p._5 = value
      def pAppData: native.Ptr[Byte] = !p._6
      def pAppData_=(value: native.Ptr[Byte]): Unit = !p._6 = value
      def xOpen: native.CFunctionPtr5[native.Ptr[sqlite3_vfs], native.CString, native.Ptr[sqlite3_file], native.CInt, native.Ptr[native.CInt], native.CInt] = (!p._7).cast[native.CFunctionPtr5[native.Ptr[sqlite3_vfs], native.CString, native.Ptr[sqlite3_file], native.CInt, native.Ptr[native.CInt], native.CInt]]
      def xOpen_=(value: native.CFunctionPtr5[native.Ptr[sqlite3_vfs], native.CString, native.Ptr[sqlite3_file], native.CInt, native.Ptr[native.CInt], native.CInt]): Unit = !p._7 = value.cast[native.CFunctionPtr5[native.Ptr[Byte], native.CString, native.Ptr[struct_sqlite3_file], native.CInt, native.Ptr[native.CInt], native.CInt]]
      def xDelete: native.CFunctionPtr3[native.Ptr[sqlite3_vfs], native.CString, native.CInt, native.CInt] = (!p._8).cast[native.CFunctionPtr3[native.Ptr[sqlite3_vfs], native.CString, native.CInt, native.CInt]]
      def xDelete_=(value: native.CFunctionPtr3[native.Ptr[sqlite3_vfs], native.CString, native.CInt, native.CInt]): Unit = !p._8 = value.cast[native.CFunctionPtr3[native.Ptr[Byte], native.CString, native.CInt, native.CInt]]
      def xAccess: native.CFunctionPtr4[native.Ptr[sqlite3_vfs], native.CString, native.CInt, native.Ptr[native.CInt], native.CInt] = (!p._9).cast[native.CFunctionPtr4[native.Ptr[sqlite3_vfs], native.CString, native.CInt, native.Ptr[native.CInt], native.CInt]]
      def xAccess_=(value: native.CFunctionPtr4[native.Ptr[sqlite3_vfs], native.CString, native.CInt, native.Ptr[native.CInt], native.CInt]): Unit = !p._9 = value.cast[native.CFunctionPtr4[native.Ptr[Byte], native.CString, native.CInt, native.Ptr[native.CInt], native.CInt]]
      def xFullPathname: native.CFunctionPtr4[native.Ptr[sqlite3_vfs], native.CString, native.CInt, native.CString, native.CInt] = (!p._10).cast[native.CFunctionPtr4[native.Ptr[sqlite3_vfs], native.CString, native.CInt, native.CString, native.CInt]]
      def xFullPathname_=(value: native.CFunctionPtr4[native.Ptr[sqlite3_vfs], native.CString, native.CInt, native.CString, native.CInt]): Unit = !p._10 = value.cast[native.CFunctionPtr4[native.Ptr[Byte], native.CString, native.CInt, native.CString, native.CInt]]
      def xDlOpen: native.CFunctionPtr2[native.Ptr[sqlite3_vfs], native.CString, native.Ptr[Byte]] = (!p._11).cast[native.CFunctionPtr2[native.Ptr[sqlite3_vfs], native.CString, native.Ptr[Byte]]]
      def xDlOpen_=(value: native.CFunctionPtr2[native.Ptr[sqlite3_vfs], native.CString, native.Ptr[Byte]]): Unit = !p._11 = value.cast[native.CFunctionPtr2[native.Ptr[Byte], native.CString, native.Ptr[Byte]]]
      def xDlError: native.CFunctionPtr3[native.Ptr[sqlite3_vfs], native.CInt, native.CString, Unit] = (!p._12).cast[native.CFunctionPtr3[native.Ptr[sqlite3_vfs], native.CInt, native.CString, Unit]]
      def xDlError_=(value: native.CFunctionPtr3[native.Ptr[sqlite3_vfs], native.CInt, native.CString, Unit]): Unit = !p._12 = value.cast[native.CFunctionPtr3[native.Ptr[Byte], native.CInt, native.CString, Unit]]
      def xDlSym: native.CFunctionPtr3[native.Ptr[sqlite3_vfs], native.Ptr[Byte], native.CString, native.CFunctionPtr0[Unit]] = (!p._13).cast[native.CFunctionPtr3[native.Ptr[sqlite3_vfs], native.Ptr[Byte], native.CString, native.CFunctionPtr0[Unit]]]
      def xDlSym_=(value: native.CFunctionPtr3[native.Ptr[sqlite3_vfs], native.Ptr[Byte], native.CString, native.CFunctionPtr0[Unit]]): Unit = !p._13 = value.cast[native.CFunctionPtr3[native.Ptr[Byte], native.Ptr[Byte], native.CString, native.CFunctionPtr0[Unit]]]
      def xDlClose: native.CFunctionPtr2[native.Ptr[sqlite3_vfs], native.Ptr[Byte], Unit] = (!p._14).cast[native.CFunctionPtr2[native.Ptr[sqlite3_vfs], native.Ptr[Byte], Unit]]
      def xDlClose_=(value: native.CFunctionPtr2[native.Ptr[sqlite3_vfs], native.Ptr[Byte], Unit]): Unit = !p._14 = value.cast[native.CFunctionPtr2[native.Ptr[Byte], native.Ptr[Byte], Unit]]
      def xRandomness: native.CFunctionPtr3[native.Ptr[sqlite3_vfs], native.CInt, native.CString, native.CInt] = (!p._15).cast[native.CFunctionPtr3[native.Ptr[sqlite3_vfs], native.CInt, native.CString, native.CInt]]
      def xRandomness_=(value: native.CFunctionPtr3[native.Ptr[sqlite3_vfs], native.CInt, native.CString, native.CInt]): Unit = !p._15 = value.cast[native.CFunctionPtr3[native.Ptr[Byte], native.CInt, native.CString, native.CInt]]
      def xSleep: native.CFunctionPtr2[native.Ptr[sqlite3_vfs], native.CInt, native.CInt] = (!p._16).cast[native.CFunctionPtr2[native.Ptr[sqlite3_vfs], native.CInt, native.CInt]]
      def xSleep_=(value: native.CFunctionPtr2[native.Ptr[sqlite3_vfs], native.CInt, native.CInt]): Unit = !p._16 = value.cast[native.CFunctionPtr2[native.Ptr[Byte], native.CInt, native.CInt]]
      def xCurrentTime: native.CFunctionPtr2[native.Ptr[sqlite3_vfs], native.Ptr[native.CDouble], native.CInt] = (!p._17).cast[native.CFunctionPtr2[native.Ptr[sqlite3_vfs], native.Ptr[native.CDouble], native.CInt]]
      def xCurrentTime_=(value: native.CFunctionPtr2[native.Ptr[sqlite3_vfs], native.Ptr[native.CDouble], native.CInt]): Unit = !p._17 = value.cast[native.CFunctionPtr2[native.Ptr[Byte], native.Ptr[native.CDouble], native.CInt]]
      def xGetLastError: native.CFunctionPtr3[native.Ptr[sqlite3_vfs], native.CInt, native.CString, native.CInt] = (!p._18).cast[native.CFunctionPtr3[native.Ptr[sqlite3_vfs], native.CInt, native.CString, native.CInt]]
      def xGetLastError_=(value: native.CFunctionPtr3[native.Ptr[sqlite3_vfs], native.CInt, native.CString, native.CInt]): Unit = !p._18 = value.cast[native.CFunctionPtr3[native.Ptr[Byte], native.CInt, native.CString, native.CInt]]
      def xCurrentTimeInt64: native.CFunctionPtr2[native.Ptr[sqlite3_vfs], native.Ptr[sqlite3_int64], native.CInt] = (!p._19).cast[native.CFunctionPtr2[native.Ptr[sqlite3_vfs], native.Ptr[sqlite3_int64], native.CInt]]
      def xCurrentTimeInt64_=(value: native.CFunctionPtr2[native.Ptr[sqlite3_vfs], native.Ptr[sqlite3_int64], native.CInt]): Unit = !p._19 = value.cast[native.CFunctionPtr2[native.Ptr[Byte], native.Ptr[native.CLongLong], native.CInt]]
      def xSetSystemCall: native.CFunctionPtr3[native.Ptr[sqlite3_vfs], native.CString, native.CFunctionPtr0[Unit], native.CInt] = (!p._20).cast[native.CFunctionPtr3[native.Ptr[sqlite3_vfs], native.CString, native.CFunctionPtr0[Unit], native.CInt]]
      def xSetSystemCall_=(value: native.CFunctionPtr3[native.Ptr[sqlite3_vfs], native.CString, native.CFunctionPtr0[Unit], native.CInt]): Unit = !p._20 = value.cast[native.CFunctionPtr3[native.Ptr[Byte], native.CString, native.CFunctionPtr0[Unit], native.CInt]]
      def xGetSystemCall: native.CFunctionPtr2[native.Ptr[sqlite3_vfs], native.CString, native.CFunctionPtr0[Unit]] = (!p._21).cast[native.CFunctionPtr2[native.Ptr[sqlite3_vfs], native.CString, native.CFunctionPtr0[Unit]]]
      def xGetSystemCall_=(value: native.CFunctionPtr2[native.Ptr[sqlite3_vfs], native.CString, native.CFunctionPtr0[Unit]]): Unit = !p._21 = value.cast[native.CFunctionPtr2[native.Ptr[Byte], native.CString, native.CFunctionPtr0[Unit]]]
      def xNextSystemCall: native.CFunctionPtr2[native.Ptr[sqlite3_vfs], native.CString, native.CString] = (!p._22).cast[native.CFunctionPtr2[native.Ptr[sqlite3_vfs], native.CString, native.CString]]
      def xNextSystemCall_=(value: native.CFunctionPtr2[native.Ptr[sqlite3_vfs], native.CString, native.CString]): Unit = !p._22 = value.cast[native.CFunctionPtr2[native.Ptr[Byte], native.CString, native.CString]]
    }
    def struct_sqlite3_vfs()(implicit z: native.Zone): native.Ptr[struct_sqlite3_vfs] = native.alloc[struct_sqlite3_vfs]

    implicit class struct_sqlite3_mem_methods_ops(val p: native.Ptr[struct_sqlite3_mem_methods]) extends AnyVal {
      def xMalloc: native.CFunctionPtr1[native.CInt, native.Ptr[Byte]] = !p._1
      def xMalloc_=(value: native.CFunctionPtr1[native.CInt, native.Ptr[Byte]]): Unit = !p._1 = value
      def xFree: native.CFunctionPtr1[native.Ptr[Byte], Unit] = !p._2
      def xFree_=(value: native.CFunctionPtr1[native.Ptr[Byte], Unit]): Unit = !p._2 = value
      def xRealloc: native.CFunctionPtr2[native.Ptr[Byte], native.CInt, native.Ptr[Byte]] = !p._3
      def xRealloc_=(value: native.CFunctionPtr2[native.Ptr[Byte], native.CInt, native.Ptr[Byte]]): Unit = !p._3 = value
      def xSize: native.CFunctionPtr1[native.Ptr[Byte], native.CInt] = !p._4
      def xSize_=(value: native.CFunctionPtr1[native.Ptr[Byte], native.CInt]): Unit = !p._4 = value
      def xRoundup: native.CFunctionPtr1[native.CInt, native.CInt] = !p._5
      def xRoundup_=(value: native.CFunctionPtr1[native.CInt, native.CInt]): Unit = !p._5 = value
      def xInit: native.CFunctionPtr1[native.Ptr[Byte], native.CInt] = !p._6
      def xInit_=(value: native.CFunctionPtr1[native.Ptr[Byte], native.CInt]): Unit = !p._6 = value
      def xShutdown: native.CFunctionPtr1[native.Ptr[Byte], Unit] = !p._7
      def xShutdown_=(value: native.CFunctionPtr1[native.Ptr[Byte], Unit]): Unit = !p._7 = value
      def pAppData: native.Ptr[Byte] = !p._8
      def pAppData_=(value: native.Ptr[Byte]): Unit = !p._8 = value
    }
    def struct_sqlite3_mem_methods()(implicit z: native.Zone): native.Ptr[struct_sqlite3_mem_methods] = native.alloc[struct_sqlite3_mem_methods]

    // FIXME: https://github.com/scala-native/scala-native/issues/637 (DBO)
    /*
    implicit class struct_sqlite3_module_ops(val p: native.Ptr[struct_sqlite3_module]) extends AnyVal {
      def iVersion: native.CInt = !p._1.cast[native.Ptr[native.CInt]]
      def iVersion_=(value: native.CInt): Unit = !p._1.cast[native.Ptr[native.CInt]] = value
      def xCreate: native.CFunctionPtr6[native.Ptr[sqlite3], native.Ptr[Byte], native.CInt, native.Ptr[native.CString], native.Ptr[native.Ptr[sqlite3_vtab]], native.Ptr[native.CString], native.CInt] = !(p._1 + 8).cast[native.Ptr[native.CFunctionPtr6[native.Ptr[sqlite3], native.Ptr[Byte], native.CInt, native.Ptr[native.CString], native.Ptr[native.Ptr[sqlite3_vtab]], native.Ptr[native.CString], native.CInt]]]
      def xCreate_=(value: native.CFunctionPtr6[native.Ptr[sqlite3], native.Ptr[Byte], native.CInt, native.Ptr[native.CString], native.Ptr[native.Ptr[sqlite3_vtab]], native.Ptr[native.CString], native.CInt]): Unit = !(p._1 + 8).cast[native.Ptr[native.CFunctionPtr6[native.Ptr[sqlite3], native.Ptr[Byte], native.CInt, native.Ptr[native.CString], native.Ptr[native.Ptr[sqlite3_vtab]], native.Ptr[native.CString], native.CInt]]] = value
      def xConnect: native.CFunctionPtr6[native.Ptr[sqlite3], native.Ptr[Byte], native.CInt, native.Ptr[native.CString], native.Ptr[native.Ptr[sqlite3_vtab]], native.Ptr[native.CString], native.CInt] = !(p._1 + 16).cast[native.Ptr[native.CFunctionPtr6[native.Ptr[sqlite3], native.Ptr[Byte], native.CInt, native.Ptr[native.CString], native.Ptr[native.Ptr[sqlite3_vtab]], native.Ptr[native.CString], native.CInt]]]
      def xConnect_=(value: native.CFunctionPtr6[native.Ptr[sqlite3], native.Ptr[Byte], native.CInt, native.Ptr[native.CString], native.Ptr[native.Ptr[sqlite3_vtab]], native.Ptr[native.CString], native.CInt]): Unit = !(p._1 + 16).cast[native.Ptr[native.CFunctionPtr6[native.Ptr[sqlite3], native.Ptr[Byte], native.CInt, native.Ptr[native.CString], native.Ptr[native.Ptr[sqlite3_vtab]], native.Ptr[native.CString], native.CInt]]] = value
      def xBestIndex: native.CFunctionPtr2[native.Ptr[sqlite3_vtab], native.Ptr[sqlite3_index_info], native.CInt] = !(p._1 + 24).cast[native.Ptr[native.CFunctionPtr2[native.Ptr[sqlite3_vtab], native.Ptr[sqlite3_index_info], native.CInt]]]
      def xBestIndex_=(value: native.CFunctionPtr2[native.Ptr[sqlite3_vtab], native.Ptr[sqlite3_index_info], native.CInt]): Unit = !(p._1 + 24).cast[native.Ptr[native.CFunctionPtr2[native.Ptr[sqlite3_vtab], native.Ptr[sqlite3_index_info], native.CInt]]] = value
      def xDisconnect: native.CFunctionPtr1[native.Ptr[sqlite3_vtab], native.CInt] = !(p._1 + 32).cast[native.Ptr[native.CFunctionPtr1[native.Ptr[sqlite3_vtab], native.CInt]]]
      def xDisconnect_=(value: native.CFunctionPtr1[native.Ptr[sqlite3_vtab], native.CInt]): Unit = !(p._1 + 32).cast[native.Ptr[native.CFunctionPtr1[native.Ptr[sqlite3_vtab], native.CInt]]] = value
      def xDestroy: native.CFunctionPtr1[native.Ptr[sqlite3_vtab], native.CInt] = !(p._1 + 40).cast[native.Ptr[native.CFunctionPtr1[native.Ptr[sqlite3_vtab], native.CInt]]]
      def xDestroy_=(value: native.CFunctionPtr1[native.Ptr[sqlite3_vtab], native.CInt]): Unit = !(p._1 + 40).cast[native.Ptr[native.CFunctionPtr1[native.Ptr[sqlite3_vtab], native.CInt]]] = value
      def xOpen: native.CFunctionPtr2[native.Ptr[sqlite3_vtab], native.Ptr[native.Ptr[sqlite3_vtab_cursor]], native.CInt] = !(p._1 + 48).cast[native.Ptr[native.CFunctionPtr2[native.Ptr[sqlite3_vtab], native.Ptr[native.Ptr[sqlite3_vtab_cursor]], native.CInt]]]
      def xOpen_=(value: native.CFunctionPtr2[native.Ptr[sqlite3_vtab], native.Ptr[native.Ptr[sqlite3_vtab_cursor]], native.CInt]): Unit = !(p._1 + 48).cast[native.Ptr[native.CFunctionPtr2[native.Ptr[sqlite3_vtab], native.Ptr[native.Ptr[sqlite3_vtab_cursor]], native.CInt]]] = value
      def xClose: native.CFunctionPtr1[native.Ptr[sqlite3_vtab_cursor], native.CInt] = !(p._1 + 56).cast[native.Ptr[native.CFunctionPtr1[native.Ptr[sqlite3_vtab_cursor], native.CInt]]]
      def xClose_=(value: native.CFunctionPtr1[native.Ptr[sqlite3_vtab_cursor], native.CInt]): Unit = !(p._1 + 56).cast[native.Ptr[native.CFunctionPtr1[native.Ptr[sqlite3_vtab_cursor], native.CInt]]] = value
      def xFilter: native.CFunctionPtr5[native.Ptr[sqlite3_vtab_cursor], native.CInt, native.CString, native.CInt, native.Ptr[native.Ptr[sqlite3_value]], native.CInt] = !(p._1 + 64).cast[native.Ptr[native.CFunctionPtr5[native.Ptr[sqlite3_vtab_cursor], native.CInt, native.CString, native.CInt, native.Ptr[native.Ptr[sqlite3_value]], native.CInt]]]
      def xFilter_=(value: native.CFunctionPtr5[native.Ptr[sqlite3_vtab_cursor], native.CInt, native.CString, native.CInt, native.Ptr[native.Ptr[sqlite3_value]], native.CInt]): Unit = !(p._1 + 64).cast[native.Ptr[native.CFunctionPtr5[native.Ptr[sqlite3_vtab_cursor], native.CInt, native.CString, native.CInt, native.Ptr[native.Ptr[sqlite3_value]], native.CInt]]] = value
      def xNext: native.CFunctionPtr1[native.Ptr[sqlite3_vtab_cursor], native.CInt] = !(p._1 + 72).cast[native.Ptr[native.CFunctionPtr1[native.Ptr[sqlite3_vtab_cursor], native.CInt]]]
      def xNext_=(value: native.CFunctionPtr1[native.Ptr[sqlite3_vtab_cursor], native.CInt]): Unit = !(p._1 + 72).cast[native.Ptr[native.CFunctionPtr1[native.Ptr[sqlite3_vtab_cursor], native.CInt]]] = value
      def xEof: native.CFunctionPtr1[native.Ptr[sqlite3_vtab_cursor], native.CInt] = !(p._1 + 80).cast[native.Ptr[native.CFunctionPtr1[native.Ptr[sqlite3_vtab_cursor], native.CInt]]]
      */
    /*
      def xEof_=(value: native.CFunctionPtr1[native.Ptr[sqlite3_vtab_cursor], native.CInt]): Unit = !(p._1 + 80).cast[native.Ptr[native.CFunctionPtr1[native.Ptr[sqlite3_vtab_cursor], native.CInt]]] = value
      def xColumn: native.CFunctionPtr3[native.Ptr[sqlite3_vtab_cursor], native.Ptr[sqlite3_context], native.CInt, native.CInt] = !(p._1 + 88).cast[native.Ptr[native.CFunctionPtr3[native.Ptr[sqlite3_vtab_cursor], native.Ptr[sqlite3_context], native.CInt, native.CInt]]]
      def xColumn_=(value: native.CFunctionPtr3[native.Ptr[sqlite3_vtab_cursor], native.Ptr[sqlite3_context], native.CInt, native.CInt]): Unit = !(p._1 + 88).cast[native.Ptr[native.CFunctionPtr3[native.Ptr[sqlite3_vtab_cursor], native.Ptr[sqlite3_context], native.CInt, native.CInt]]] = value
      def xRowid: native.CFunctionPtr2[native.Ptr[sqlite3_vtab_cursor], native.Ptr[sqlite3_int64], native.CInt] = !(p._1 + 96).cast[native.Ptr[native.CFunctionPtr2[native.Ptr[sqlite3_vtab_cursor], native.Ptr[sqlite3_int64], native.CInt]]]
      def xRowid_=(value: native.CFunctionPtr2[native.Ptr[sqlite3_vtab_cursor], native.Ptr[sqlite3_int64], native.CInt]): Unit = !(p._1 + 96).cast[native.Ptr[native.CFunctionPtr2[native.Ptr[sqlite3_vtab_cursor], native.Ptr[sqlite3_int64], native.CInt]]] = value
      def xUpdate: native.CFunctionPtr4[native.Ptr[sqlite3_vtab], native.CInt, native.Ptr[native.Ptr[sqlite3_value]], native.Ptr[sqlite3_int64], native.CInt] = !(p._1 + 104).cast[native.Ptr[native.CFunctionPtr4[native.Ptr[sqlite3_vtab], native.CInt, native.Ptr[native.Ptr[sqlite3_value]], native.Ptr[sqlite3_int64], native.CInt]]]
      def xUpdate_=(value: native.CFunctionPtr4[native.Ptr[sqlite3_vtab], native.CInt, native.Ptr[native.Ptr[sqlite3_value]], native.Ptr[sqlite3_int64], native.CInt]): Unit = !(p._1 + 104).cast[native.Ptr[native.CFunctionPtr4[native.Ptr[sqlite3_vtab], native.CInt, native.Ptr[native.Ptr[sqlite3_value]], native.Ptr[sqlite3_int64], native.CInt]]] = value
      def xBegin: native.CFunctionPtr1[native.Ptr[sqlite3_vtab], native.CInt] = !(p._1 + 112).cast[native.Ptr[native.CFunctionPtr1[native.Ptr[sqlite3_vtab], native.CInt]]]
      def xBegin_=(value: native.CFunctionPtr1[native.Ptr[sqlite3_vtab], native.CInt]): Unit = !(p._1 + 112).cast[native.Ptr[native.CFunctionPtr1[native.Ptr[sqlite3_vtab], native.CInt]]] = value
      def xSync: native.CFunctionPtr1[native.Ptr[sqlite3_vtab], native.CInt] = !(p._1 + 120).cast[native.Ptr[native.CFunctionPtr1[native.Ptr[sqlite3_vtab], native.CInt]]]
      def xSync_=(value: native.CFunctionPtr1[native.Ptr[sqlite3_vtab], native.CInt]): Unit = !(p._1 + 120).cast[native.Ptr[native.CFunctionPtr1[native.Ptr[sqlite3_vtab], native.CInt]]] = value
      def xCommit: native.CFunctionPtr1[native.Ptr[sqlite3_vtab], native.CInt] = !(p._1 + 128).cast[native.Ptr[native.CFunctionPtr1[native.Ptr[sqlite3_vtab], native.CInt]]]
      def xCommit_=(value: native.CFunctionPtr1[native.Ptr[sqlite3_vtab], native.CInt]): Unit = !(p._1 + 128).cast[native.Ptr[native.CFunctionPtr1[native.Ptr[sqlite3_vtab], native.CInt]]] = value
      def xRollback: native.CFunctionPtr1[native.Ptr[sqlite3_vtab], native.CInt] = !(p._1 + 136).cast[native.Ptr[native.CFunctionPtr1[native.Ptr[sqlite3_vtab], native.CInt]]]
      def xRollback_=(value: native.CFunctionPtr1[native.Ptr[sqlite3_vtab], native.CInt]): Unit = !(p._1 + 136).cast[native.Ptr[native.CFunctionPtr1[native.Ptr[sqlite3_vtab], native.CInt]]] = value
      def xFindFunction: native.CFunctionPtr5[native.Ptr[sqlite3_vtab], native.CInt, native.CString, native.Ptr[native.CFunctionPtr3[native.Ptr[sqlite3_context], native.CInt, native.Ptr[native.Ptr[sqlite3_value]], Unit]], native.Ptr[native.Ptr[Byte]], native.CInt] = !(p._1 + 144).cast[native.Ptr[native.CFunctionPtr5[native.Ptr[sqlite3_vtab], native.CInt, native.CString, native.Ptr[native.CFunctionPtr3[native.Ptr[sqlite3_context], native.CInt, native.Ptr[native.Ptr[sqlite3_value]], Unit]], native.Ptr[native.Ptr[Byte]], native.CInt]]]
      def xFindFunction_=(value: native.CFunctionPtr5[native.Ptr[sqlite3_vtab], native.CInt, native.CString, native.Ptr[native.CFunctionPtr3[native.Ptr[sqlite3_context], native.CInt, native.Ptr[native.Ptr[sqlite3_value]], Unit]], native.Ptr[native.Ptr[Byte]], native.CInt]): Unit = !(p._1 + 144).cast[native.Ptr[native.CFunctionPtr5[native.Ptr[sqlite3_vtab], native.CInt, native.CString, native.Ptr[native.CFunctionPtr3[native.Ptr[sqlite3_context], native.CInt, native.Ptr[native.Ptr[sqlite3_value]], Unit]], native.Ptr[native.Ptr[Byte]], native.CInt]]] = value
      def xRename: native.CFunctionPtr2[native.Ptr[sqlite3_vtab], native.CString, native.CInt] = !(p._1 + 152).cast[native.Ptr[native.CFunctionPtr2[native.Ptr[sqlite3_vtab], native.CString, native.CInt]]]
      def xRename_=(value: native.CFunctionPtr2[native.Ptr[sqlite3_vtab], native.CString, native.CInt]): Unit = !(p._1 + 152).cast[native.Ptr[native.CFunctionPtr2[native.Ptr[sqlite3_vtab], native.CString, native.CInt]]] = value
      def xSavepoint: native.CFunctionPtr2[native.Ptr[sqlite3_vtab], native.CInt, native.CInt] = !(p._1 + 160).cast[native.Ptr[native.CFunctionPtr2[native.Ptr[sqlite3_vtab], native.CInt, native.CInt]]]
      def xSavepoint_=(value: native.CFunctionPtr2[native.Ptr[sqlite3_vtab], native.CInt, native.CInt]): Unit = !(p._1 + 160).cast[native.Ptr[native.CFunctionPtr2[native.Ptr[sqlite3_vtab], native.CInt, native.CInt]]] = value
      def xRelease: native.CFunctionPtr2[native.Ptr[sqlite3_vtab], native.CInt, native.CInt] = !(p._1 + 168).cast[native.Ptr[native.CFunctionPtr2[native.Ptr[sqlite3_vtab], native.CInt, native.CInt]]]
      def xRelease_=(value: native.CFunctionPtr2[native.Ptr[sqlite3_vtab], native.CInt, native.CInt]): Unit = !(p._1 + 168).cast[native.Ptr[native.CFunctionPtr2[native.Ptr[sqlite3_vtab], native.CInt, native.CInt]]] = value
      def xRollbackTo: native.CFunctionPtr2[native.Ptr[sqlite3_vtab], native.CInt, native.CInt] = !(p._1 + 176).cast[native.Ptr[native.CFunctionPtr2[native.Ptr[sqlite3_vtab], native.CInt, native.CInt]]]
      def xRollbackTo_=(value: native.CFunctionPtr2[native.Ptr[sqlite3_vtab], native.CInt, native.CInt]): Unit = !(p._1 + 176).cast[native.Ptr[native.CFunctionPtr2[native.Ptr[sqlite3_vtab], native.CInt, native.CInt]]] = value
    }
    def struct_sqlite3_module()(implicit z: native.Zone): native.Ptr[struct_sqlite3_module] = native.alloc[struct_sqlite3_module]
    */

    implicit class struct_sqlite3_index_info_ops(val p: native.Ptr[struct_sqlite3_index_info]) extends AnyVal {
      def nConstraint: native.CInt = !p._1
      def nConstraint_=(value: native.CInt): Unit = !p._1 = value
      def aConstraint: native.Ptr[struct_sqlite3_index_constraint] = !p._2
      def aConstraint_=(value: native.Ptr[struct_sqlite3_index_constraint]): Unit = !p._2 = value
      def nOrderBy: native.CInt = !p._3
      def nOrderBy_=(value: native.CInt): Unit = !p._3 = value
      def aOrderBy: native.Ptr[struct_sqlite3_index_orderby] = !p._4
      def aOrderBy_=(value: native.Ptr[struct_sqlite3_index_orderby]): Unit = !p._4 = value
      def aConstraintUsage: native.Ptr[struct_sqlite3_index_constraint_usage] = !p._5
      def aConstraintUsage_=(value: native.Ptr[struct_sqlite3_index_constraint_usage]): Unit = !p._5 = value
      def idxNum: native.CInt = !p._6
      def idxNum_=(value: native.CInt): Unit = !p._6 = value
      def idxStr: native.CString = !p._7
      def idxStr_=(value: native.CString): Unit = !p._7 = value
      def needToFreeIdxStr: native.CInt = !p._8
      def needToFreeIdxStr_=(value: native.CInt): Unit = !p._8 = value
      def orderByConsumed: native.CInt = !p._9
      def orderByConsumed_=(value: native.CInt): Unit = !p._9 = value
      def estimatedCost: native.CDouble = !p._10
      def estimatedCost_=(value: native.CDouble): Unit = !p._10 = value
      def estimatedRows: sqlite3_int64 = !p._11
      def estimatedRows_=(value: sqlite3_int64): Unit = !p._11 = value
      def idxFlags: native.CInt = !p._12
      def idxFlags_=(value: native.CInt): Unit = !p._12 = value
      def colUsed: sqlite3_uint64 = !p._13
      def colUsed_=(value: sqlite3_uint64): Unit = !p._13 = value
    }
    def struct_sqlite3_index_info()(implicit z: native.Zone): native.Ptr[struct_sqlite3_index_info] = native.alloc[struct_sqlite3_index_info]

    implicit class struct_sqlite3_index_constraint_ops(val p: native.Ptr[struct_sqlite3_index_constraint]) extends AnyVal {
      def iColumn: native.CInt = !p._1
      def iColumn_=(value: native.CInt): Unit = !p._1 = value
      def op: native.CUnsignedChar = !p._2
      def op_=(value: native.CUnsignedChar): Unit = !p._2 = value
      def usable: native.CUnsignedChar = !p._3
      def usable_=(value: native.CUnsignedChar): Unit = !p._3 = value
      def iTermOffset: native.CInt = !p._4
      def iTermOffset_=(value: native.CInt): Unit = !p._4 = value
    }
    def struct_sqlite3_index_constraint()(implicit z: native.Zone): native.Ptr[struct_sqlite3_index_constraint] = native.alloc[struct_sqlite3_index_constraint]

    implicit class struct_sqlite3_index_orderby_ops(val p: native.Ptr[struct_sqlite3_index_orderby]) extends AnyVal {
      def iColumn: native.CInt = !p._1
      def iColumn_=(value: native.CInt): Unit = !p._1 = value
      def desc: native.CUnsignedChar = !p._2
      def desc_=(value: native.CUnsignedChar): Unit = !p._2 = value
    }
    def struct_sqlite3_index_orderby()(implicit z: native.Zone): native.Ptr[struct_sqlite3_index_orderby] = native.alloc[struct_sqlite3_index_orderby]

    implicit class struct_sqlite3_index_constraint_usage_ops(val p: native.Ptr[struct_sqlite3_index_constraint_usage]) extends AnyVal {
      def argvIndex: native.CInt = !p._1
      def argvIndex_=(value: native.CInt): Unit = !p._1 = value
      def omit: native.CUnsignedChar = !p._2
      def omit_=(value: native.CUnsignedChar): Unit = !p._2 = value
    }
    def struct_sqlite3_index_constraint_usage()(implicit z: native.Zone): native.Ptr[struct_sqlite3_index_constraint_usage] = native.alloc[struct_sqlite3_index_constraint_usage]

    // FIXME: https://github.com/scala-native/scala-native/issues/637 (DBO)
    /*implicit class struct_sqlite3_vtab_ops(val p: native.Ptr[struct_sqlite3_vtab]) extends AnyVal {
      def pModule: native.Ptr[sqlite3_module] = (!p._1).cast[native.Ptr[sqlite3_module]]
      def pModule_=(value: native.Ptr[sqlite3_module]): Unit = !p._1 = value.cast[native.Ptr[Byte]]
      def nRef: native.CInt = !p._2
      def nRef_=(value: native.CInt): Unit = !p._2 = value
      def zErrMsg: native.CString = !p._3
      def zErrMsg_=(value: native.CString): Unit = !p._3 = value
    }
    def struct_sqlite3_vtab()(implicit z: native.Zone): native.Ptr[struct_sqlite3_vtab] = native.alloc[struct_sqlite3_vtab]*/

    implicit class struct_sqlite3_vtab_cursor_ops(val p: native.Ptr[struct_sqlite3_vtab_cursor]) extends AnyVal {
      def pVtab: native.Ptr[sqlite3_vtab] = (!p._1).cast[native.Ptr[sqlite3_vtab]]
      def pVtab_=(value: native.Ptr[sqlite3_vtab]): Unit = !p._1 = value.cast[native.Ptr[Byte]]
    }
    def struct_sqlite3_vtab_cursor()(implicit z: native.Zone): native.Ptr[struct_sqlite3_vtab_cursor] = native.alloc[struct_sqlite3_vtab_cursor]

    implicit class struct_sqlite3_mutex_methods_ops(val p: native.Ptr[struct_sqlite3_mutex_methods]) extends AnyVal {
      def xMutexInit: native.CFunctionPtr0[native.CInt] = !p._1
      def xMutexInit_=(value: native.CFunctionPtr0[native.CInt]): Unit = !p._1 = value
      def xMutexEnd: native.CFunctionPtr0[native.CInt] = !p._2
      def xMutexEnd_=(value: native.CFunctionPtr0[native.CInt]): Unit = !p._2 = value
      def xMutexAlloc: native.CFunctionPtr1[native.CInt, native.Ptr[sqlite3_mutex]] = !p._3
      def xMutexAlloc_=(value: native.CFunctionPtr1[native.CInt, native.Ptr[sqlite3_mutex]]): Unit = !p._3 = value
      def xMutexFree: native.CFunctionPtr1[native.Ptr[sqlite3_mutex], Unit] = !p._4
      def xMutexFree_=(value: native.CFunctionPtr1[native.Ptr[sqlite3_mutex], Unit]): Unit = !p._4 = value
      def xMutexEnter: native.CFunctionPtr1[native.Ptr[sqlite3_mutex], Unit] = !p._5
      def xMutexEnter_=(value: native.CFunctionPtr1[native.Ptr[sqlite3_mutex], Unit]): Unit = !p._5 = value
      def xMutexTry: native.CFunctionPtr1[native.Ptr[sqlite3_mutex], native.CInt] = !p._6
      def xMutexTry_=(value: native.CFunctionPtr1[native.Ptr[sqlite3_mutex], native.CInt]): Unit = !p._6 = value
      def xMutexLeave: native.CFunctionPtr1[native.Ptr[sqlite3_mutex], Unit] = !p._7
      def xMutexLeave_=(value: native.CFunctionPtr1[native.Ptr[sqlite3_mutex], Unit]): Unit = !p._7 = value
      def xMutexHeld: native.CFunctionPtr1[native.Ptr[sqlite3_mutex], native.CInt] = !p._8
      def xMutexHeld_=(value: native.CFunctionPtr1[native.Ptr[sqlite3_mutex], native.CInt]): Unit = !p._8 = value
      def xMutexNotheld: native.CFunctionPtr1[native.Ptr[sqlite3_mutex], native.CInt] = !p._9
      def xMutexNotheld_=(value: native.CFunctionPtr1[native.Ptr[sqlite3_mutex], native.CInt]): Unit = !p._9 = value
    }
    def struct_sqlite3_mutex_methods()(implicit z: native.Zone): native.Ptr[struct_sqlite3_mutex_methods] = native.alloc[struct_sqlite3_mutex_methods]

    implicit class struct_sqlite3_pcache_page_ops(val p: native.Ptr[struct_sqlite3_pcache_page]) extends AnyVal {
      def pBuf: native.Ptr[Byte] = !p._1
      def pBuf_=(value: native.Ptr[Byte]): Unit = !p._1 = value
      def pExtra: native.Ptr[Byte] = !p._2
      def pExtra_=(value: native.Ptr[Byte]): Unit = !p._2 = value
    }
    def struct_sqlite3_pcache_page()(implicit z: native.Zone): native.Ptr[struct_sqlite3_pcache_page] = native.alloc[struct_sqlite3_pcache_page]

    implicit class struct_sqlite3_pcache_methods2_ops(val p: native.Ptr[struct_sqlite3_pcache_methods2]) extends AnyVal {
      def iVersion: native.CInt = !p._1
      def iVersion_=(value: native.CInt): Unit = !p._1 = value
      def pArg: native.Ptr[Byte] = !p._2
      def pArg_=(value: native.Ptr[Byte]): Unit = !p._2 = value
      def xInit: native.CFunctionPtr1[native.Ptr[Byte], native.CInt] = !p._3
      def xInit_=(value: native.CFunctionPtr1[native.Ptr[Byte], native.CInt]): Unit = !p._3 = value
      def xShutdown: native.CFunctionPtr1[native.Ptr[Byte], Unit] = !p._4
      def xShutdown_=(value: native.CFunctionPtr1[native.Ptr[Byte], Unit]): Unit = !p._4 = value
      def xCreate: native.CFunctionPtr3[native.CInt, native.CInt, native.CInt, native.Ptr[sqlite3_pcache]] = !p._5
      def xCreate_=(value: native.CFunctionPtr3[native.CInt, native.CInt, native.CInt, native.Ptr[sqlite3_pcache]]): Unit = !p._5 = value
      def xCachesize: native.CFunctionPtr2[native.Ptr[sqlite3_pcache], native.CInt, Unit] = !p._6
      def xCachesize_=(value: native.CFunctionPtr2[native.Ptr[sqlite3_pcache], native.CInt, Unit]): Unit = !p._6 = value
      def xPagecount: native.CFunctionPtr1[native.Ptr[sqlite3_pcache], native.CInt] = !p._7
      def xPagecount_=(value: native.CFunctionPtr1[native.Ptr[sqlite3_pcache], native.CInt]): Unit = !p._7 = value
      def xFetch: native.CFunctionPtr3[native.Ptr[sqlite3_pcache], native.CUnsignedInt, native.CInt, native.Ptr[sqlite3_pcache_page]] = !p._8
      def xFetch_=(value: native.CFunctionPtr3[native.Ptr[sqlite3_pcache], native.CUnsignedInt, native.CInt, native.Ptr[sqlite3_pcache_page]]): Unit = !p._8 = value
      def xUnpin: native.CFunctionPtr3[native.Ptr[sqlite3_pcache], native.Ptr[sqlite3_pcache_page], native.CInt, Unit] = !p._9
      def xUnpin_=(value: native.CFunctionPtr3[native.Ptr[sqlite3_pcache], native.Ptr[sqlite3_pcache_page], native.CInt, Unit]): Unit = !p._9 = value
      def xRekey: native.CFunctionPtr4[native.Ptr[sqlite3_pcache], native.Ptr[sqlite3_pcache_page], native.CUnsignedInt, native.CUnsignedInt, Unit] = !p._10
      def xRekey_=(value: native.CFunctionPtr4[native.Ptr[sqlite3_pcache], native.Ptr[sqlite3_pcache_page], native.CUnsignedInt, native.CUnsignedInt, Unit]): Unit = !p._10 = value
      def xTruncate: native.CFunctionPtr2[native.Ptr[sqlite3_pcache], native.CUnsignedInt, Unit] = !p._11
      def xTruncate_=(value: native.CFunctionPtr2[native.Ptr[sqlite3_pcache], native.CUnsignedInt, Unit]): Unit = !p._11 = value
      def xDestroy: native.CFunctionPtr1[native.Ptr[sqlite3_pcache], Unit] = !p._12
      def xDestroy_=(value: native.CFunctionPtr1[native.Ptr[sqlite3_pcache], Unit]): Unit = !p._12 = value
      def xShrink: native.CFunctionPtr1[native.Ptr[sqlite3_pcache], Unit] = !p._13
      def xShrink_=(value: native.CFunctionPtr1[native.Ptr[sqlite3_pcache], Unit]): Unit = !p._13 = value
    }
    def struct_sqlite3_pcache_methods2()(implicit z: native.Zone): native.Ptr[struct_sqlite3_pcache_methods2] = native.alloc[struct_sqlite3_pcache_methods2]

    implicit class struct_sqlite3_pcache_methods_ops(val p: native.Ptr[struct_sqlite3_pcache_methods]) extends AnyVal {
      def pArg: native.Ptr[Byte] = !p._1
      def pArg_=(value: native.Ptr[Byte]): Unit = !p._1 = value
      def xInit: native.CFunctionPtr1[native.Ptr[Byte], native.CInt] = !p._2
      def xInit_=(value: native.CFunctionPtr1[native.Ptr[Byte], native.CInt]): Unit = !p._2 = value
      def xShutdown: native.CFunctionPtr1[native.Ptr[Byte], Unit] = !p._3
      def xShutdown_=(value: native.CFunctionPtr1[native.Ptr[Byte], Unit]): Unit = !p._3 = value
      def xCreate: native.CFunctionPtr2[native.CInt, native.CInt, native.Ptr[sqlite3_pcache]] = !p._4
      def xCreate_=(value: native.CFunctionPtr2[native.CInt, native.CInt, native.Ptr[sqlite3_pcache]]): Unit = !p._4 = value
      def xCachesize: native.CFunctionPtr2[native.Ptr[sqlite3_pcache], native.CInt, Unit] = !p._5
      def xCachesize_=(value: native.CFunctionPtr2[native.Ptr[sqlite3_pcache], native.CInt, Unit]): Unit = !p._5 = value
      def xPagecount: native.CFunctionPtr1[native.Ptr[sqlite3_pcache], native.CInt] = !p._6
      def xPagecount_=(value: native.CFunctionPtr1[native.Ptr[sqlite3_pcache], native.CInt]): Unit = !p._6 = value
      def xFetch: native.CFunctionPtr3[native.Ptr[sqlite3_pcache], native.CUnsignedInt, native.CInt, native.Ptr[Byte]] = !p._7
      def xFetch_=(value: native.CFunctionPtr3[native.Ptr[sqlite3_pcache], native.CUnsignedInt, native.CInt, native.Ptr[Byte]]): Unit = !p._7 = value
      def xUnpin: native.CFunctionPtr3[native.Ptr[sqlite3_pcache], native.Ptr[Byte], native.CInt, Unit] = !p._8
      def xUnpin_=(value: native.CFunctionPtr3[native.Ptr[sqlite3_pcache], native.Ptr[Byte], native.CInt, Unit]): Unit = !p._8 = value
      def xRekey: native.CFunctionPtr4[native.Ptr[sqlite3_pcache], native.Ptr[Byte], native.CUnsignedInt, native.CUnsignedInt, Unit] = !p._9
      def xRekey_=(value: native.CFunctionPtr4[native.Ptr[sqlite3_pcache], native.Ptr[Byte], native.CUnsignedInt, native.CUnsignedInt, Unit]): Unit = !p._9 = value
      def xTruncate: native.CFunctionPtr2[native.Ptr[sqlite3_pcache], native.CUnsignedInt, Unit] = !p._10
      def xTruncate_=(value: native.CFunctionPtr2[native.Ptr[sqlite3_pcache], native.CUnsignedInt, Unit]): Unit = !p._10 = value
      def xDestroy: native.CFunctionPtr1[native.Ptr[sqlite3_pcache], Unit] = !p._11
      def xDestroy_=(value: native.CFunctionPtr1[native.Ptr[sqlite3_pcache], Unit]): Unit = !p._11 = value
    }
    def struct_sqlite3_pcache_methods()(implicit z: native.Zone): native.Ptr[struct_sqlite3_pcache_methods] = native.alloc[struct_sqlite3_pcache_methods]

    implicit class struct_sqlite3_snapshot_ops(val p: native.Ptr[struct_sqlite3_snapshot]) extends AnyVal {
      def hidden: native.Ptr[native.CArray[native.CUnsignedChar, native.Nat.Digit[native.Nat._4, native.Nat._8]]] = p._1
      def hidden_=(value: native.Ptr[native.CArray[native.CUnsignedChar, native.Nat.Digit[native.Nat._4, native.Nat._8]]]): Unit = !p._1 = !value
    }
    def struct_sqlite3_snapshot()(implicit z: native.Zone): native.Ptr[struct_sqlite3_snapshot] = native.alloc[struct_sqlite3_snapshot]

    implicit class struct_sqlite3_rtree_geometry_ops(val p: native.Ptr[struct_sqlite3_rtree_geometry]) extends AnyVal {
      def pContext: native.Ptr[Byte] = !p._1
      def pContext_=(value: native.Ptr[Byte]): Unit = !p._1 = value
      def nParam: native.CInt = !p._2
      def nParam_=(value: native.CInt): Unit = !p._2 = value
      def aParam: native.Ptr[sqlite3_rtree_dbl] = !p._3
      def aParam_=(value: native.Ptr[sqlite3_rtree_dbl]): Unit = !p._3 = value
      def pUser: native.Ptr[Byte] = !p._4
      def pUser_=(value: native.Ptr[Byte]): Unit = !p._4 = value
      def xDelUser: native.CFunctionPtr1[native.Ptr[Byte], Unit] = !p._5
      def xDelUser_=(value: native.CFunctionPtr1[native.Ptr[Byte], Unit]): Unit = !p._5 = value
    }
    def struct_sqlite3_rtree_geometry()(implicit z: native.Zone): native.Ptr[struct_sqlite3_rtree_geometry] = native.alloc[struct_sqlite3_rtree_geometry]

    implicit class struct_sqlite3_rtree_query_info_ops(val p: native.Ptr[struct_sqlite3_rtree_query_info]) extends AnyVal {
      def pContext: native.Ptr[Byte] = !p._1
      def pContext_=(value: native.Ptr[Byte]): Unit = !p._1 = value
      def nParam: native.CInt = !p._2
      def nParam_=(value: native.CInt): Unit = !p._2 = value
      def aParam: native.Ptr[sqlite3_rtree_dbl] = !p._3
      def aParam_=(value: native.Ptr[sqlite3_rtree_dbl]): Unit = !p._3 = value
      def pUser: native.Ptr[Byte] = !p._4
      def pUser_=(value: native.Ptr[Byte]): Unit = !p._4 = value
      def xDelUser: native.CFunctionPtr1[native.Ptr[Byte], Unit] = !p._5
      def xDelUser_=(value: native.CFunctionPtr1[native.Ptr[Byte], Unit]): Unit = !p._5 = value
      def aCoord: native.Ptr[sqlite3_rtree_dbl] = !p._6
      def aCoord_=(value: native.Ptr[sqlite3_rtree_dbl]): Unit = !p._6 = value
      def anQueue: native.Ptr[native.CUnsignedInt] = !p._7
      def anQueue_=(value: native.Ptr[native.CUnsignedInt]): Unit = !p._7 = value
      def nCoord: native.CInt = !p._8
      def nCoord_=(value: native.CInt): Unit = !p._8 = value
      def iLevel: native.CInt = !p._9
      def iLevel_=(value: native.CInt): Unit = !p._9 = value
      def mxLevel: native.CInt = !p._10
      def mxLevel_=(value: native.CInt): Unit = !p._10 = value
      def iRowid: sqlite3_int64 = !p._11
      def iRowid_=(value: sqlite3_int64): Unit = !p._11 = value
      def rParentScore: sqlite3_rtree_dbl = !p._12
      def rParentScore_=(value: sqlite3_rtree_dbl): Unit = !p._12 = value
      def eParentWithin: native.CInt = !p._13
      def eParentWithin_=(value: native.CInt): Unit = !p._13 = value
      def eWithin: native.CInt = !p._14
      def eWithin_=(value: native.CInt): Unit = !p._14 = value
      def rScore: sqlite3_rtree_dbl = !p._15
      def rScore_=(value: sqlite3_rtree_dbl): Unit = !p._15 = value
      def apSqlParam: native.Ptr[native.Ptr[sqlite3_value]] = !p._16
      def apSqlParam_=(value: native.Ptr[native.Ptr[sqlite3_value]]): Unit = !p._16 = value
    }
    def struct_sqlite3_rtree_query_info()(implicit z: native.Zone): native.Ptr[struct_sqlite3_rtree_query_info] = native.alloc[struct_sqlite3_rtree_query_info]

    implicit class struct_Fts5PhraseIter_ops(val p: native.Ptr[struct_Fts5PhraseIter]) extends AnyVal {
      def a: native.Ptr[native.CUnsignedChar] = !p._1
      def a_=(value: native.Ptr[native.CUnsignedChar]): Unit = !p._1 = value
      def b: native.Ptr[native.CUnsignedChar] = !p._2
      def b_=(value: native.Ptr[native.CUnsignedChar]): Unit = !p._2 = value
    }
    def struct_Fts5PhraseIter()(implicit z: native.Zone): native.Ptr[struct_Fts5PhraseIter] = native.alloc[struct_Fts5PhraseIter]

    /*
    implicit class struct_Fts5ExtensionApi_ops(val p: native.Ptr[struct_Fts5ExtensionApi]) extends AnyVal {
      def iVersion: native.CInt = !p._1
      def iVersion_=(value: native.CInt): Unit = !p._1 = value
      def xUserData: native.CFunctionPtr1[native.Ptr[Fts5Context], native.Ptr[Byte]] = !p._2
      def xUserData_=(value: native.CFunctionPtr1[native.Ptr[Fts5Context], native.Ptr[Byte]]): Unit = !p._2 = value
      def xColumnCount: native.CFunctionPtr1[native.Ptr[Fts5Context], native.CInt] = !p._3
      def xColumnCount_=(value: native.CFunctionPtr1[native.Ptr[Fts5Context], native.CInt]): Unit = !p._3 = value
      def xRowCount: native.CFunctionPtr2[native.Ptr[Fts5Context], native.Ptr[sqlite3_int64], native.CInt] = !p._4
      def xRowCount_=(value: native.CFunctionPtr2[native.Ptr[Fts5Context], native.Ptr[sqlite3_int64], native.CInt]): Unit = !p._4 = value
      def xColumnTotalSize: native.CFunctionPtr3[native.Ptr[Fts5Context], native.CInt, native.Ptr[sqlite3_int64], native.CInt] = !p._5
      def xColumnTotalSize_=(value: native.CFunctionPtr3[native.Ptr[Fts5Context], native.CInt, native.Ptr[sqlite3_int64], native.CInt]): Unit = !p._5 = value
      def xTokenize: native.CFunctionPtr5[native.Ptr[Fts5Context], native.CString, native.CInt, native.Ptr[Byte], native.CFunctionPtr6[native.Ptr[Byte], native.CInt, native.CString, native.CInt, native.CInt, native.CInt, native.CInt], native.CInt] = !p._6
      def xTokenize_=(value: native.CFunctionPtr5[native.Ptr[Fts5Context], native.CString, native.CInt, native.Ptr[Byte], native.CFunctionPtr6[native.Ptr[Byte], native.CInt, native.CString, native.CInt, native.CInt, native.CInt, native.CInt], native.CInt]): Unit = !p._6 = value
      def xPhraseCount: native.CFunctionPtr1[native.Ptr[Fts5Context], native.CInt] = !p._7
      def xPhraseCount_=(value: native.CFunctionPtr1[native.Ptr[Fts5Context], native.CInt]): Unit = !p._7 = value
      def xPhraseSize: native.CFunctionPtr2[native.Ptr[Fts5Context], native.CInt, native.CInt] = !p._8
      def xPhraseSize_=(value: native.CFunctionPtr2[native.Ptr[Fts5Context], native.CInt, native.CInt]): Unit = !p._8 = value
      def xInstCount: native.CFunctionPtr2[native.Ptr[Fts5Context], native.Ptr[native.CInt], native.CInt] = !p._9
      def xInstCount_=(value: native.CFunctionPtr2[native.Ptr[Fts5Context], native.Ptr[native.CInt], native.CInt]): Unit = !p._9 = value
      def xInst: native.CFunctionPtr5[native.Ptr[Fts5Context], native.CInt, native.Ptr[native.CInt], native.Ptr[native.CInt], native.Ptr[native.CInt], native.CInt] = !p._10
      def xInst_=(value: native.CFunctionPtr5[native.Ptr[Fts5Context], native.CInt, native.Ptr[native.CInt], native.Ptr[native.CInt], native.Ptr[native.CInt], native.CInt]): Unit = !p._10 = value
      def xRowid: native.CFunctionPtr1[native.Ptr[Fts5Context], sqlite3_int64] = !p._11
      def xRowid_=(value: native.CFunctionPtr1[native.Ptr[Fts5Context], sqlite3_int64]): Unit = !p._11 = value
      def xColumnText: native.CFunctionPtr4[native.Ptr[Fts5Context], native.CInt, native.Ptr[native.CString], native.Ptr[native.CInt], native.CInt] = !p._12
      def xColumnText_=(value: native.CFunctionPtr4[native.Ptr[Fts5Context], native.CInt, native.Ptr[native.CString], native.Ptr[native.CInt], native.CInt]): Unit = !p._12 = value
      def xColumnSize: native.CFunctionPtr3[native.Ptr[Fts5Context], native.CInt, native.Ptr[native.CInt], native.CInt] = !p._13
      def xColumnSize_=(value: native.CFunctionPtr3[native.Ptr[Fts5Context], native.CInt, native.Ptr[native.CInt], native.CInt]): Unit = !p._13 = value
      def xQueryPhrase: native.CFunctionPtr4[native.Ptr[Fts5Context], native.CInt, native.Ptr[Byte], native.CFunctionPtr3[native.Ptr[Fts5ExtensionApi], native.Ptr[Fts5Context], native.Ptr[Byte], native.CInt], native.CInt] = (!p._14).cast[native.CFunctionPtr4[native.Ptr[Fts5Context], native.CInt, native.Ptr[Byte], native.CFunctionPtr3[native.Ptr[Fts5ExtensionApi], native.Ptr[Fts5Context], native.Ptr[Byte], native.CInt], native.CInt]]
      def xQueryPhrase_=(value: native.CFunctionPtr4[native.Ptr[Fts5Context], native.CInt, native.Ptr[Byte], native.CFunctionPtr3[native.Ptr[Fts5ExtensionApi], native.Ptr[Fts5Context], native.Ptr[Byte], native.CInt], native.CInt]): Unit = !p._14 = value.cast[native.CFunctionPtr4[native.Ptr[struct_Fts5Context], native.CInt, native.Ptr[Byte], native.CFunctionPtr3[native.Ptr[struct_Fts5ExtensionApi], native.Ptr[struct_Fts5Context], native.Ptr[Byte], native.CInt], native.CInt]]
      def xSetAuxdata: native.CFunctionPtr3[native.Ptr[Fts5Context], native.Ptr[Byte], native.CFunctionPtr1[native.Ptr[Byte], Unit], native.CInt] = !p._15
      def xSetAuxdata_=(value: native.CFunctionPtr3[native.Ptr[Fts5Context], native.Ptr[Byte], native.CFunctionPtr1[native.Ptr[Byte], Unit], native.CInt]): Unit = !p._15 = value
      def xGetAuxdata: native.CFunctionPtr2[native.Ptr[Fts5Context], native.CInt, native.Ptr[Byte]] = !p._16
      def xGetAuxdata_=(value: native.CFunctionPtr2[native.Ptr[Fts5Context], native.CInt, native.Ptr[Byte]]): Unit = !p._16 = value
      def xPhraseFirst: native.CFunctionPtr5[native.Ptr[Fts5Context], native.CInt, native.Ptr[Fts5PhraseIter], native.Ptr[native.CInt], native.Ptr[native.CInt], native.CInt] = !p._17
      def xPhraseFirst_=(value: native.CFunctionPtr5[native.Ptr[Fts5Context], native.CInt, native.Ptr[Fts5PhraseIter], native.Ptr[native.CInt], native.Ptr[native.CInt], native.CInt]): Unit = !p._17 = value
      def xPhraseNext: native.CFunctionPtr4[native.Ptr[Fts5Context], native.Ptr[Fts5PhraseIter], native.Ptr[native.CInt], native.Ptr[native.CInt], Unit] = !p._18
      def xPhraseNext_=(value: native.CFunctionPtr4[native.Ptr[Fts5Context], native.Ptr[Fts5PhraseIter], native.Ptr[native.CInt], native.Ptr[native.CInt], Unit]): Unit = !p._18 = value
      def xPhraseFirstColumn: native.CFunctionPtr4[native.Ptr[Fts5Context], native.CInt, native.Ptr[Fts5PhraseIter], native.Ptr[native.CInt], native.CInt] = !p._19
      def xPhraseFirstColumn_=(value: native.CFunctionPtr4[native.Ptr[Fts5Context], native.CInt, native.Ptr[Fts5PhraseIter], native.Ptr[native.CInt], native.CInt]): Unit = !p._19 = value
      def xPhraseNextColumn: native.CFunctionPtr3[native.Ptr[Fts5Context], native.Ptr[Fts5PhraseIter], native.Ptr[native.CInt], Unit] = !p._20
      def xPhraseNextColumn_=(value: native.CFunctionPtr3[native.Ptr[Fts5Context], native.Ptr[Fts5PhraseIter], native.Ptr[native.CInt], Unit]): Unit = !p._20 = value
    }
    def struct_Fts5ExtensionApi()(implicit z: native.Zone): native.Ptr[struct_Fts5ExtensionApi] = native.alloc[struct_Fts5ExtensionApi]
*/

    implicit class struct_fts5_tokenizer_ops(val p: native.Ptr[struct_fts5_tokenizer]) extends AnyVal {
      def xCreate: native.CFunctionPtr4[native.Ptr[Byte], native.Ptr[native.CString], native.CInt, native.Ptr[native.Ptr[Fts5Tokenizer]], native.CInt] = !p._1
      def xCreate_=(value: native.CFunctionPtr4[native.Ptr[Byte], native.Ptr[native.CString], native.CInt, native.Ptr[native.Ptr[Fts5Tokenizer]], native.CInt]): Unit = !p._1 = value
      def xDelete: native.CFunctionPtr1[native.Ptr[Fts5Tokenizer], Unit] = !p._2
      def xDelete_=(value: native.CFunctionPtr1[native.Ptr[Fts5Tokenizer], Unit]): Unit = !p._2 = value
      def xTokenize: native.CFunctionPtr6[native.Ptr[Fts5Tokenizer], native.Ptr[Byte], native.CInt, native.CString, native.CInt, native.CFunctionPtr6[native.Ptr[Byte], native.CInt, native.CString, native.CInt, native.CInt, native.CInt, native.CInt], native.CInt] = !p._3
      def xTokenize_=(value: native.CFunctionPtr6[native.Ptr[Fts5Tokenizer], native.Ptr[Byte], native.CInt, native.CString, native.CInt, native.CFunctionPtr6[native.Ptr[Byte], native.CInt, native.CString, native.CInt, native.CInt, native.CInt, native.CInt], native.CInt]): Unit = !p._3 = value
    }
    def struct_fts5_tokenizer()(implicit z: native.Zone): native.Ptr[struct_fts5_tokenizer] = native.alloc[struct_fts5_tokenizer]

    /*
    implicit class struct_fts5_api_ops(val p: native.Ptr[struct_fts5_api]) extends AnyVal {
      def iVersion: native.CInt = !p._1
      def iVersion_=(value: native.CInt): Unit = !p._1 = value
      def xCreateTokenizer: native.CFunctionPtr5[native.Ptr[fts5_api], native.CString, native.Ptr[Byte], native.Ptr[fts5_tokenizer], native.CFunctionPtr1[native.Ptr[Byte], Unit], native.CInt] = (!p._2).cast[native.CFunctionPtr5[native.Ptr[fts5_api], native.CString, native.Ptr[Byte], native.Ptr[fts5_tokenizer], native.CFunctionPtr1[native.Ptr[Byte], Unit], native.CInt]]
      def xCreateTokenizer_=(value: native.CFunctionPtr5[native.Ptr[fts5_api], native.CString, native.Ptr[Byte], native.Ptr[fts5_tokenizer], native.CFunctionPtr1[native.Ptr[Byte], Unit], native.CInt]): Unit = !p._2 = value.cast[native.CFunctionPtr5[native.Ptr[Byte], native.CString, native.Ptr[Byte], native.Ptr[struct_fts5_tokenizer], native.CFunctionPtr1[native.Ptr[Byte], Unit], native.CInt]]
      def xFindTokenizer: native.CFunctionPtr4[native.Ptr[fts5_api], native.CString, native.Ptr[native.Ptr[Byte]], native.Ptr[fts5_tokenizer], native.CInt] = (!p._3).cast[native.CFunctionPtr4[native.Ptr[fts5_api], native.CString, native.Ptr[native.Ptr[Byte]], native.Ptr[fts5_tokenizer], native.CInt]]
      def xFindTokenizer_=(value: native.CFunctionPtr4[native.Ptr[fts5_api], native.CString, native.Ptr[native.Ptr[Byte]], native.Ptr[fts5_tokenizer], native.CInt]): Unit = !p._3 = value.cast[native.CFunctionPtr4[native.Ptr[Byte], native.CString, native.Ptr[native.Ptr[Byte]], native.Ptr[struct_fts5_tokenizer], native.CInt]]
      def xCreateFunction: native.CFunctionPtr5[native.Ptr[fts5_api], native.CString, native.Ptr[Byte], native.CFunctionPtr5[native.Ptr[Fts5ExtensionApi], native.Ptr[Fts5Context], native.Ptr[sqlite3_context], native.CInt, native.Ptr[native.Ptr[sqlite3_value]], Unit], native.CFunctionPtr1[native.Ptr[Byte], Unit], native.CInt] = (!p._4).cast[native.CFunctionPtr5[native.Ptr[fts5_api], native.CString, native.Ptr[Byte], native.CFunctionPtr5[native.Ptr[Fts5ExtensionApi], native.Ptr[Fts5Context], native.Ptr[sqlite3_context], native.CInt, native.Ptr[native.Ptr[sqlite3_value]], Unit], native.CFunctionPtr1[native.Ptr[Byte], Unit], native.CInt]]
      def xCreateFunction_=(value: native.CFunctionPtr5[native.Ptr[fts5_api], native.CString, native.Ptr[Byte], native.CFunctionPtr5[native.Ptr[Fts5ExtensionApi], native.Ptr[Fts5Context], native.Ptr[sqlite3_context], native.CInt, native.Ptr[native.Ptr[sqlite3_value]], Unit], native.CFunctionPtr1[native.Ptr[Byte], Unit], native.CInt]): Unit = !p._4 = value.cast[native.CFunctionPtr5[native.Ptr[Byte], native.CString, native.Ptr[Byte], native.CFunctionPtr5[native.Ptr[struct_Fts5ExtensionApi], native.Ptr[struct_Fts5Context], native.Ptr[struct_sqlite3_context], native.CInt, native.Ptr[native.Ptr[struct_sqlite3_value]], Unit], native.CFunctionPtr1[native.Ptr[Byte], Unit], native.CInt]]
    }
    def struct_fts5_api()(implicit z: native.Zone): native.Ptr[struct_fts5_api] = native.alloc[struct_fts5_api]*/
  }
}
