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

import java.time.Instant;
import java.util.stream.StreamSupport;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class QuipMessage extends QuipJsonObject {

  // ============================================
  // Constructor
  // ============================================

  protected QuipMessage(JsonObject json) {
    super(json);
  }

  // ============================================
  // Properties
  // ============================================

  public String getId() {
    return _getString("id");
  }

  public String getAuthorId() {
    return _getString("author_id");
  }

  public String getAuthorName() {
    return _getString("author_name");
  }

  public Instant getCreatedUsec() {
    return _getInstant("created_usec");
  }

  public Instant getUpdatedUsec() {
    return _getInstant("updated_usec");
  }

  public String getText() {
    return _getString("text");
  }

  public String getParts() {
    return _getString("parts");
  }

  public String getAnnotationId() {
    return _getString("annotation", "id");
  }

  public String[] getHighlightSectionIds() {
    return _getStringArray("annotation", "highlight_section_ids");
  }

  public boolean isVisible() {
    return _getBoolean("visible");
  }

  public String[] getFiles() {
    JsonArray arr = _getJsonArray("files");
    if (arr == null)
      return null;
    return StreamSupport.stream(arr.spliterator(), false)
        .map(e -> e.getAsJsonObject().get("name").getAsString())
        .toArray(String[]::new);
  }

  public QuipDiffGroup[] getDiffGroups() {
    JsonArray arr = _getJsonArray("diff_groups");
    if (arr == null)
      return null;
    QuipDiffGroup[] diffGroups = StreamSupport.stream(arr.spliterator(), false)
        .map(obj -> new QuipDiffGroup(obj.getAsJsonObject()))
        .toArray(QuipDiffGroup[]::new);
    return diffGroups;
  }
}