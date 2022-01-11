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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

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

  /**
   * Gets an array of files with name and hash as JsonArray
   * @return String "[{"name":"xxxxx","hash":"xxxx"}]"
   */
  public String getFilesWithHashAsString() {
    JsonArray arr = _getJsonArray("files");
    if (arr == null) {
      return null;
    }
    return arr.toString();
  }

  /**
   * Gets an array of files with name and hash
   * @return String[] [{"name":"xxxxx","hash":"xxxx"}]
   */
  public String[] getFilesWithHash() {
      JsonArray arr = _getJsonArray("files");
      if (arr == null) {
          return null;
      }
      return StreamSupport.stream(arr.spliterator(), false)
            .map(e -> {
              JsonObject file = e.getAsJsonObject();
              if(Objects.nonNull(file)){
                return file.toString();
              }
              return null;
            }).filter(Objects::nonNull)
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

    /**
     * Method fetches messages for the relevant thread based on maxCreated,updatedSince and lastUpdatedSince time filters.
     * @param threadId - The ID of the thread to return recent messages for.
     * @param count - Number of messages to return.
     * @param maxCreatedUsec - A UNIX timestamp in microseconds returns messages created at and before the given timestamp.
     * @param updatedSinceUsec - A UNIX timestamp in microseconds returns messages last updated at and after the given timestamp.
     * @param lastUpdatedSinceUsec - A UNIX timestamp in microseconds returns messages last updated before the given timestamp.
     * @param sortedBy - Determines whether to sort messages in ascending or descending order.
     * @param messageType - Determines the type of messages to return.
     * @return - QuipMessage[] for the specified thread.
     * @throws Exception - 400,401,403,500
     */
  public static QuipMessage[] getRecentMessages(String threadId, Integer count, Instant maxCreatedUsec, Instant updatedSinceUsec,
                                                Instant lastUpdatedSinceUsec, QuipThread.SortedBy sortedBy,
                                                QuipThread.MessageType messageType) throws Exception {
    List<NameValuePair> params = new ArrayList<>();
    if (Objects.nonNull(count)) {
      params.add(new BasicNameValuePair("count", String.valueOf(count)));
    }

    if (maxCreatedUsec != null) {
      params.add(new BasicNameValuePair("max_created_usec",
              String.valueOf(ChronoUnit.MICROS.between(Instant.EPOCH,
                      maxCreatedUsec))));
    }

    if (Objects.nonNull(updatedSinceUsec)){
      params.add(new BasicNameValuePair("updated_since_usec",
              String.valueOf(ChronoUnit.MICROS.between(Instant.EPOCH,
                      updatedSinceUsec))));
    }

    if (Objects.nonNull(lastUpdatedSinceUsec)){
      params.add(new BasicNameValuePair("last_updated_since_usec",
              String.valueOf(ChronoUnit.MICROS.between(Instant.EPOCH,
                      lastUpdatedSinceUsec))));
    }

    if (!Objects.equals(sortedBy, QuipThread.SortedBy.NONE)){
        params.add(new BasicNameValuePair("sorted_by", sortedBy.name()));
    }

    if (messageType != null) {
      params.add(new BasicNameValuePair("message_type", messageType.name().toLowerCase()));
    }
    JsonArray arr = _getToJsonArray(
            new URIBuilder(QuipAccess.ENDPOINT + "/messages/" + threadId)
                    .addParameters(params).build());
    return StreamSupport.stream(arr.spliterator(), false)
            .map(obj -> new QuipMessage(obj.getAsJsonObject()))
            .toArray(QuipMessage[]::new);
  }
}