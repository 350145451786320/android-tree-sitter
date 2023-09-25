/*
 *  This file is part of android-tree-sitter.
 *
 *  android-tree-sitter library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  android-tree-sitter library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *  along with android-tree-sitter.  If not, see <https://www.gnu.org/licenses/>.
 */

#include "utils/ts_obj_utils.h"

#include <iostream>

extern "C" JNIEXPORT jlong JNICALL
Java_com_itsaky_androidide_treesitter_TSQueryCursor_00024Native_newCursor(
    JNIEnv *env, jclass self) {
  return (jlong)ts_query_cursor_new();
}

extern "C" JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_TSQueryCursor_00024Native_delete(
    JNIEnv *env, jclass self, jlong cursor) {
  ts_query_cursor_delete((TSQueryCursor *)cursor);
}

extern "C" JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_TSQueryCursor_00024Native_exec(
    JNIEnv *env, jclass self, jlong cursor, jlong query, jobject node) {
  ts_query_cursor_exec((TSQueryCursor *)cursor, (TSQuery *)query,
                       _unmarshalNode(env, node));
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_itsaky_androidide_treesitter_TSQueryCursor_00024Native_exceededMatchLimit(
    JNIEnv *env, jclass self, jlong cursor) {
  return (jboolean)ts_query_cursor_did_exceed_match_limit(
      (TSQueryCursor *)cursor);
}

extern "C" JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_TSQueryCursor_00024Native_matchLimit__JI(
    JNIEnv *env, jclass self, jlong cursor, jint newLimit) {
  ts_query_cursor_set_match_limit((TSQueryCursor *)cursor, newLimit);
}

extern "C" JNIEXPORT jint JNICALL
Java_com_itsaky_androidide_treesitter_TSQueryCursor_00024Native_matchLimit__J(
    JNIEnv *env, jclass self, jlong cursor) {
  return ts_query_cursor_match_limit((TSQueryCursor *)cursor);
}

extern "C" JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_TSQueryCursor_00024Native_setByteRange(
    JNIEnv *env, jclass self, jlong cursor, jint start, jint end) {
  ts_query_cursor_set_byte_range((TSQueryCursor *)cursor, start, end);
}

extern "C" JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_TSQueryCursor_00024Native_setPointRange(
    JNIEnv *env, jclass self, jlong cursor, jobject start, jobject end) {
  ts_query_cursor_set_point_range((TSQueryCursor *)cursor,
                                  _unmarshalPoint(env, start),
                                  _unmarshalPoint(env, end));
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_itsaky_androidide_treesitter_TSQueryCursor_00024Native_nextMatch(
    JNIEnv *env, jclass self, jlong cursor) {
  TSQueryMatch m;
  bool b = ts_query_cursor_next_match((TSQueryCursor *)cursor, &m);
  if (!b) {
    return nullptr;
  }
  return _marshalMatch(env, m);
}

extern "C" JNIEXPORT void JNICALL
Java_com_itsaky_androidide_treesitter_TSQueryCursor_00024Native_removeMatch(
    JNIEnv *env, jclass self, jlong cursor, jint id) {
  ts_query_cursor_remove_match((TSQueryCursor *)cursor, id);
}