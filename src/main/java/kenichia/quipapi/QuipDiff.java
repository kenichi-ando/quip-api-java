/*
 * Copyright 2021 Kenichi Ando
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package kenichia.quipapi;

import java.util.stream.Stream;

import com.google.gson.JsonObject;

public class QuipDiff extends QuipJsonObject {

  // ============================================
  // Enum
  // ============================================

  public enum DiffClass {
    TRACK_CHANGES("track_changes"), DELETE_ONLY("delete_only"), INSERT_ONLY(
        "insert_only"), INSERT_COMPLETELY("insert_completely");

    private final String _value;

    private DiffClass(String value) {
      _value = value;
    }

    private static DiffClass find(String value) {
      return Stream.of(values()).filter(e -> e._value.equals(value)).findFirst()
          .orElse(null);
    }
  }

  // ============================================
  // Constructor
  // ============================================

  protected QuipDiff(JsonObject json) {
    super(json);
  }

  // ============================================
  // Properties
  // ============================================

  public String getRtml() {
    return _getString("rtml");
  }

  public String getList() {
    return _getString("list");
  }

  public DiffClass getDiffClass() {
    return DiffClass.find(_getString("diff_class"));
  }

  public String getSectionId() {
    return _getString("section_id");
  }

  public String getStyle() {
    return _getString("style");
  }
}