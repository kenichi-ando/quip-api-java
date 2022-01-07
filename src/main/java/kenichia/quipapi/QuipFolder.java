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
import org.apache.http.client.fluent.Form;
import org.apache.http.client.utils.URIBuilder;

import java.time.Instant;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class QuipFolder extends QuipJsonObject {

    // ============================================
    // Enum
    // ============================================

    public enum Color {
        MANILA("manila"),
        RED("red"),
        ORANGE("orange"),
        GREEN("green"),
        BLUE("manila"),
        PURPLE("purple"),
        YELLOW("yellow"),
        LIGHT_RED("light_red"),
        LIGHT_ORANGE("light_orange"),
        LIGHT_GREEN("light_green"),
        LIGHT_BLUE("light_blue"),
        LIGHT_PURPLE("light_purple");

        private final String _value;

        private Color(String value) {
            _value = value;
        }

        private static Color find(String value) {
            return Stream.of(values()).filter(e -> e._value.equals(value))
                    .findFirst().orElse(null);
        }
    }

    // ============================================
    // Node class
    // ============================================

    public class Node {
        private final boolean _isFolder;
        private final String _folderOrThreadId;

        private Node(JsonObject obj) {
            if (obj.has("folder_id")) {
                _isFolder = true;
                _folderOrThreadId = obj.get("folder_id").getAsString();
            } else if (obj.has("thread_id")) {
                _isFolder = false;
                _folderOrThreadId = obj.get("thread_id").getAsString();
            } else {
                throw new IllegalArgumentException(
                        "Invalid json object: " + obj);
            }
        }

        public boolean isFolder() {
            return _isFolder;
        }

        public String getId() {
            return _folderOrThreadId;
        }
    }

    // ============================================
    // Constructor
    // ============================================

    protected QuipFolder(JsonObject json) {
        super(json);
    }

    // ============================================
    // Properties
    // ============================================

    public String getId() {
        return _getString("folder", "id");
    }

    public String getTitle() {
        return _getString("folder", "title");
    }

    public Instant getCreatedUsec() {
        return _getInstant("folder", "created_usec");
    }

    public Instant getUpdatedUsec() {
        return _getInstant("folder", "updated_usec");
    }

    public String getCreatorId() {
        return _getString("folder", "creator_id");
    }

    public Color getColor() {
        return Color.find(_getString("folder", "color"));
    }

    public String getParentId() {
        return _getString("folder", "parent_id");
    }

    public String[] getMemberIds() {
        return _getStringArray("member_ids");
    }

    public Node[] getChildren() {
        JsonArray json = _getJsonArray("children");
        return StreamSupport.stream(json.spliterator(), false)
                .map(child -> new Node((JsonObject) child))
                .toArray(Node[]::new);
    }

    // ============================================
    // Read
    // ============================================

    public static QuipFolder getFolder(String folderId, boolean includeChats)
            throws Exception {
        return new QuipFolder(_getToJsonObject(
                new URIBuilder(QuipAccess.ENDPOINT + "/folders/" + folderId)
                        .addParameter("include_chats",
                                String.valueOf(includeChats))
                        .build()));
    }

    public static QuipFolder[] getFolders(String[] folderIds, boolean includeChats) throws Exception {
        JsonObject json = _getToJsonObject(
                new URIBuilder(QuipAccess.ENDPOINT + "/folders/")
                        .addParameter("ids", String.join(",", folderIds))
                        .addParameter("include_chats",
                                String.valueOf(includeChats))
                        .build());
        return json.keySet().stream()
                .map(id -> new QuipFolder(json.get(id).getAsJsonObject()))
                .toArray(QuipFolder[]::new);
    }

    public boolean reload() throws Exception {
        JsonObject object = _getToJsonObject(
                QuipAccess.ENDPOINT + "/folders/" + getId());
        if (object == null)
            return false;
        _replace(object);
        return true;
    }

    // ============================================
    // Create / Update
    // ============================================

    public static QuipFolder create(String title, Color color, String parentId,
                                    String[] memberIds, boolean includeChats) throws Exception {
        Form form = Form.form();
        if (title != null)
            form.add("title", title);
        if (color != null)
            form.add("color", color._value);
        if (parentId != null)
            form.add("parent_id", parentId);
        if (memberIds != null)
            form.add("member_ids",
                    String.join(",", memberIds));
        return new QuipFolder(
                _postToJsonObject(
                        new URIBuilder(QuipAccess.ENDPOINT + "/folders/new")
                                .addParameter("include_chats",
                                        String.valueOf(includeChats))
                                .build(),
                        form));
    }

    public boolean update(String title, Color color, boolean includeChats)
            throws Exception {
        Form form = Form.form().add("folder_id", getId());
        if (title != null)
            form.add("title", title);
        if (color != null)
            form.add("color", color._value);
        JsonObject object = _postToJsonObject(
                new URIBuilder(QuipAccess.ENDPOINT + "/folders/update")
                        .addParameter("include_chats",
                                String.valueOf(includeChats))
                        .build(),
                form);
        if (object == null)
            return false;
        _replace(object);
        return true;
    }

    // ============================================
    // Members
    // ============================================

    public boolean addMember(String userId) throws Exception {
        return addMembers(new String[]{userId});
    }

    public boolean addMembers(String[] userIds) throws Exception {
        JsonObject object = _postToJsonObject(
                QuipAccess.ENDPOINT + "/folders/add-members",
                Form.form().add("folder_id", getId()).add("member_ids",
                        String.join(",", userIds)));
        if (object == null)
            return false;
        _replace(object);
        return true;
    }

    public boolean removeMember(String userId) throws Exception {
        return removeMembers(new String[]{userId});
    }

    public boolean removeMembers(String[] userIds) throws Exception {
        JsonObject object = _postToJsonObject(
                QuipAccess.ENDPOINT + "/folders/remove-members",
                Form.form().add("folder_id", getId()).add("member_ids",
                        String.join(",", userIds)));
        if (object == null)
            return false;
        _replace(object);
        return true;
    }
}