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
import org.apache.http.client.fluent.Form;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class QuipThread extends QuipJsonObject {

    // ============================================
    // Enum
    // ============================================

    public enum Type {
        DOCUMENT("document"), SPREADSHEET("spreadsheet"), CHAT("chat"), SLIDES("slides");

        private final String _value;

        Type(String value) {
            _value = value;
        }

        private static Type find(String value) {
            return Stream.of(values()).filter(e -> e._value.equals(value))
                    .findFirst().orElse(null);
        }
    }

    public enum Format {
        HTML("html"), MARKDOWN("markdown");

        private final String _value;

        Format(String value) {
            _value = value;
        }
    }

    public enum Location {
        APPEND(0), PREPEND(1), AFTER_SECTION(2), BEFORE_SECTION(
                3), REPLACE_SECTION(4), DELETE_SECTION(5), AFTER_DOCUMENT_RANGE(
                6), BEFORE_DOCUMENT_RANGE(7), REPLACE_DOCUMENT_RANGE(
                8), DELETE_DOCUMENT_RANGE(9);

        private final int _value;

        Location(int value) {
            _value = value;
        }
    }

    public enum Frame {
        BUBBLE("bubble"), CARD("card"), LINE("line");

        private final String _value;

        Frame(String value) {
            _value = value;
        }
    }

    ;

    public enum Mode {
        VIEW("view"), EDIT("edit"), NONE("none");

        private final String _value;

        Mode(String value) {
            _value = value;
        }
    }

    ;

    public enum MessageType {
        MESSAGE("message"), EDIT("edit");

        private final String _value;

        MessageType(String value) {
            _value = value;
        }
    }

    public enum SortedBy {
        ASC("asc"),
        DESC("desc"),
        NONE("none");

        private final String sortOrder;

        SortedBy(String sortOder) {
            this.sortOrder = sortOder;
        }
    }

    ;

    // ============================================
    // Constructor
    // ============================================

    protected QuipThread(JsonObject json) {
        super(json);
    }

    // ============================================
    // Properties
    // ============================================

    public String getId() {
        return _getString("thread", "id");
    }

    public String getTitle() {
        return _getString("thread", "title");
    }

    public String getLink() {
        return _getString("thread", "link");
    }

    public Instant getCreatedUsec() {
        return _getInstant("thread", "created_usec");
    }

    public Instant getUpdatedUsec() {
        return _getInstant("thread", "updated_usec");
    }

    public String getSharing() {
        return _getString("thread", "sharing");
    }

    public Type getType() {
        return Type.find(_getString("thread", "type"));
    }

    public String getAuthorId() {
        return _getString("thread", "author_id");
    }

    public boolean isDeleted() {
        return _getBoolean("thread", "is_deleted");
    }

    public String[] getSharedFolderIds() {
        return _getStringArray("shared_folder_ids");
    }

    public String[] getUserIds() {
        return _getStringArray("user_ids");
    }

    public String[] getExpandedUserIds() {
        return _getStringArray("expanded_user_ids");
    }

    public String getHtml() {
        return _getString("html");
    }

    public String getOwningCompanyId() {
        return _getString("thread", "owning_company_id");
    }

    // ============================================
    // Read / Search
    // ============================================

    public static QuipThread getThread(String threadId) throws Exception {
        return new QuipThread(
                _getToJsonObject(QuipAccess.ENDPOINT + "/threads/" + threadId));
    }

    public static QuipThread[] getThreads(String[] threadIds) throws Exception {
        JsonObject json = _getToJsonObject(
                new URIBuilder(QuipAccess.ENDPOINT + "/threads/")
                        .addParameter("ids",
                                Stream.of(threadIds)
                                        .collect(Collectors.joining(",")))
                        .build());
        return json.keySet().stream()
                .map(id -> new QuipThread(json.get(id).getAsJsonObject()))
                .toArray(QuipThread[]::new);
    }

    public static QuipThread[] getRecentThreads() throws Exception {
        JsonObject json = _getToJsonObject(
                QuipAccess.ENDPOINT + "/threads/recent");
        return json.entrySet().stream()
                .map(obj -> new QuipThread((JsonObject) obj.getValue()))
                .toArray(QuipThread[]::new);
    }

    /**
     * @param count          - number of threads to fetch
     * @param maxUpdatedUsec - max updated time
     * @param includeHidden  - include hidden chats
     * @return - QuipThread[] of the most recently updated where
     * updated_usec is less or equal to the maxUpdatedUsec passed.
     * @throws Exception - 403,401,404,500
     */
    public static QuipThread[] getRecentThreads(Integer count, Instant maxUpdatedUsec, boolean includeHidden) throws Exception {
        URIBuilder uriBuilder = new URIBuilder(QuipAccess.ENDPOINT + "/threads/recent");
        if (Objects.nonNull(count) && count > 0) {
            uriBuilder.addParameter("count", String.valueOf(count));
        }

        if (Objects.nonNull(maxUpdatedUsec)) {
            uriBuilder.addParameter("max_updated_usec", String.valueOf(ChronoUnit.MICROS.between(Instant.EPOCH,
                    maxUpdatedUsec)));
        }

        JsonObject json = _getToJsonObject(uriBuilder.addParameter("include_hidden", String.valueOf(includeHidden)).build());
        return json.entrySet().stream()
                .map(obj -> new QuipThread((JsonObject) obj.getValue()))
                .toArray(QuipThread[]::new);
    }

    public static QuipThread[] searchThreads(String query, Integer count,
                                             Boolean isOnlyMatchTitles) throws Exception {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("query", query));
        if (count != null)
            params.add(new BasicNameValuePair("count", String.valueOf(count)));
        if (isOnlyMatchTitles != null)
            params.add(new BasicNameValuePair("only_match_titles",
                    String.valueOf(isOnlyMatchTitles)));
        JsonArray arr = _getToJsonArray(
                new URIBuilder(QuipAccess.ENDPOINT + "/threads/search")
                        .addParameters(params).build());
        return StreamSupport.stream(arr.spliterator(), false)
                .map(obj -> new QuipThread(obj.getAsJsonObject()))
                .toArray(QuipThread[]::new);
    }

    public boolean reload() throws Exception {
        JsonObject object = _getToJsonObject(
                QuipAccess.ENDPOINT + "/threads/" + getId());
        if (object == null)
            return false;
        _replace(object);
        return true;
    }

    // ============================================
    // Create / Update / Delete
    // ============================================

    public static QuipThread createDocument(String title, String content,
                                            String[] memberIds, Format format, Type type) throws Exception {
        Form form = Form.form();
        if (title != null)
            form.add("title", title);
        if (content != null)
            form.add("content", content);
        if (memberIds != null)
            form.add("member_ids",
                    Stream.of(memberIds).collect(Collectors.joining(",")));
        if (format != null)
            form.add("format", format._value);
        if (type != null)
            form.add("type", type._value);
        return new QuipThread(_postToJsonObject(
                QuipAccess.ENDPOINT + "/threads/new-document", form));
    }

    public static QuipThread createChat(String title, String message,
                                        String[] memberIds) throws Exception {
        Form form = Form.form();
        if (title != null)
            form.add("title", title);
        if (message != null)
            form.add("message", message);
        if (memberIds != null)
            form.add("member_ids",
                    Stream.of(memberIds).collect(Collectors.joining(",")));
        return new QuipThread(_postToJsonObject(
                QuipAccess.ENDPOINT + "/threads/new-chat", form));
    }

    public QuipThread copyDocument(String title, String values,
                                   String[] memberIds, String[] folderIds) throws Exception {
        Form form = Form.form().add("thread_id", getId());
        if (title != null)
            form.add("title", title);
        if (values != null)
            form.add("values", values);
        if (memberIds != null)
            form.add("member_ids",
                    Stream.of(memberIds).collect(Collectors.joining(",")));
        if (folderIds != null)
            form.add("folder_ids",
                    Stream.of(folderIds).collect(Collectors.joining(",")));
        return new QuipThread(_postToJsonObject(
                QuipAccess.ENDPOINT + "/threads/copy-document", form));
    }

    public boolean editDocument(String content, Format format,
                                Location location, String sectionIdOrDocumentRange)
            throws Exception {
        Form form = Form.form().add("thread_id", getId());
        if (format != null)
            form.add("format", format._value);
        if (content != null)
            form.add("content", content);
        if (location != null)
            form.add("location", String.valueOf(location._value));
        if (sectionIdOrDocumentRange != null) {
            if (location == Location.AFTER_SECTION
                    || location == Location.BEFORE_SECTION
                    || location == Location.REPLACE_SECTION
                    || location == Location.DELETE_SECTION)
                form.add("section_id", sectionIdOrDocumentRange);
            else if (location == Location.AFTER_DOCUMENT_RANGE
                    || location == Location.BEFORE_DOCUMENT_RANGE
                    || location == Location.REPLACE_DOCUMENT_RANGE
                    || location == Location.DELETE_DOCUMENT_RANGE)
                form.add("document_range", sectionIdOrDocumentRange);
        }
        JsonObject object = _postToJsonObject(
                QuipAccess.ENDPOINT + "/threads/edit-document", form);
        if (object == null)
            return false;
        _replace(object);
        return true;
    }

    public boolean createLivePasteSection(String sourceThreadId,
                                          Boolean isDocumentRangeSource,
                                          String[] sourceSectionIdsOrDocumentRange, Location location,
                                          String destinationSectionIdOrDocumentRange,
                                          Boolean isUpdateAutomatic) throws Exception {
        Form form = Form.form().add("destination_thread_id", getId());
        if (sourceThreadId != null)
            form.add("source_thread_id", sourceThreadId);
        if (isDocumentRangeSource != null)
            form.add("is_document_range_source",
                    String.valueOf(isDocumentRangeSource));
        if (sourceSectionIdsOrDocumentRange != null)
            form.add(
                    isDocumentRangeSource
                            ? "source_document_range"
                            : "source_section_ids",
                    Stream.of(sourceSectionIdsOrDocumentRange)
                            .collect(Collectors.joining(",")));
        if (location != null) {
            if (location == Location.DELETE_SECTION
                    || location == Location.DELETE_DOCUMENT_RANGE)
                throw new IllegalArgumentException(
                        "The location " + location + " is not supported.");
            form.add("location", String.valueOf(location._value));
        }
        if (destinationSectionIdOrDocumentRange != null)
            if (location == Location.AFTER_DOCUMENT_RANGE
                    || location == Location.BEFORE_DOCUMENT_RANGE
                    || location == Location.REPLACE_DOCUMENT_RANGE)
                form.add("destination_document_range",
                        destinationSectionIdOrDocumentRange);
            else
                form.add("destination_section_id",
                        destinationSectionIdOrDocumentRange);
        if (isUpdateAutomatic != null)
            form.add("update_automatic", String.valueOf(isUpdateAutomatic));
        JsonObject object = _postToJsonObject(
                QuipAccess.ENDPOINT + "/threads/live-paste", form);
        if (object == null)
            return false;
        _replace(object);
        return true;
    }

    public void delete() throws Exception {
        _postToJsonObject(QuipAccess.ENDPOINT + "/threads/delete",
                Form.form().add("thread_id", getId()));
    }

    // ============================================
    // Lock
    // ============================================

    public void lockEdits(Boolean isEditsDisabled) throws Exception {
        _postToJsonObject(QuipAccess.ENDPOINT + "/threads/lock-edits",
                Form.form().add("thread_id", getId()).add("edits_disabled",
                        String.valueOf(isEditsDisabled)));
    }

    public void lockSectionEdits(String sectionId, Boolean isEditsDisabled)
            throws Exception {
        _postToJsonObject(QuipAccess.ENDPOINT + "/threads/lock-section-edits",
                Form.form().add("thread_id", getId())
                        .add("section_id", sectionId).add("edits_disabled",
                                String.valueOf(isEditsDisabled)));
    }

    // ============================================
    // Import / Export
    // ============================================

    public static QuipThread importFile(File file, Type type, String title,
                                        String[] memberIds) throws Exception {
        MultipartEntityBuilder multipart = MultipartEntityBuilder.create()
                .addBinaryBody("file", file);
        if (type != null)
            multipart.addTextBody("type", type._value);
        if (title != null)
            multipart.addTextBody("title", title);
        if (memberIds != null)
            multipart.addTextBody("member_ids",
                    Stream.of(memberIds).collect(Collectors.joining(",")));
        return new QuipThread(_postToJsonObject(
                QuipAccess.ENDPOINT + "/threads/import-file", multipart));
    }

    public byte[] exportAsDocx() throws Exception {
        return _getToByteArray(
                QuipAccess.ENDPOINT + "/threads/" + getId() + "/export/docx");
    }

    public byte[] exportAsXlsx() throws Exception {
        return _getToByteArray(
                QuipAccess.ENDPOINT + "/threads/" + getId() + "/export/xlsx");
    }

    public byte[] exportAsPdf() throws Exception {
        return _getToByteArray(
                QuipAccess.ENDPOINT + "/threads/" + getId() + "/export/pdf");
    }

    public String createExportPdfRequest(String destinationThreadId)
            throws Exception {
        Form form = Form.form();
        if (destinationThreadId != null)
            form.add("destination_thread_id", destinationThreadId);
        JsonObject json = _postToJsonObject(QuipAccess.ENDPOINT + "/threads/"
                + getId() + "/export/pdf/async", form);
        return json.get("request_id").getAsString();
    }

    public String retrieveExportPdfResponse(String requestId) throws Exception {
        List<NameValuePair> params = new ArrayList<>();
        if (requestId != null)
            params.add(new BasicNameValuePair("request_id", requestId));
        JsonObject json = _getToJsonObject(
                new URIBuilder(QuipAccess.ENDPOINT + "/threads/" + getId()
                        + "/export/pdf/async").addParameters(params).build());
        return (json.get("status").getAsString().equals("SUCCESS")
                || json.get("status").getAsString().equals("PARTIAL_SUCCESS"))
                ? json.get("pdf_url").getAsString()
                : null;
    }

    // ============================================
    // Messages
    // ============================================

    public QuipMessage[] getRecentMessages(Integer count,
                                           Instant maxCreatedUsec, MessageType messageType) throws Exception {
        return QuipMessage
                .getRecentMessages(getId(), count, maxCreatedUsec, null, null, SortedBy.NONE, messageType);
    }

    public QuipMessage addMessage(Frame frame, String content, String parts,
                                  Boolean isSilent, String[] blobIds, String annotationId,
                                  String sectionId) throws Exception {
        Form form = Form.form().add("thread_id", getId());
        if (frame != null)
            form.add("frame", frame._value);
        if (content != null)
            form.add("content", content);
        if (parts != null)
            form.add("parts", parts);
        if (isSilent != null)
            form.add("silent", String.valueOf(isSilent));
        if (blobIds != null)
            form.add("attachments",
                    Stream.of(blobIds).collect(Collectors.joining(",")));
        if (annotationId != null)
            form.add("annotation_id", annotationId);
        if (sectionId != null)
            form.add("section_id", sectionId);
        return new QuipMessage(
                _postToJsonObject(QuipAccess.ENDPOINT + "/messages/new", form));
    }

    // ============================================
    // Blobs
    // ============================================

    public byte[] getBlob(String blobId) throws Exception {
        return QuipBlob.getBlob(getId(), blobId);
    }

    public QuipBlob addBlob(File file) throws Exception {
        return new QuipBlob(_postToJsonObject(
                QuipAccess.ENDPOINT + "/blob/" + getId(),
                MultipartEntityBuilder.create().addBinaryBody("blob", file)));
    }

    // ============================================
    // Members
    // ============================================

    public boolean addMember(String folderOrUserId) throws Exception {
        return addMembers(new String[]{folderOrUserId});
    }

    public boolean addMembers(String[] folderOrUserIds) throws Exception {
        JsonObject object = _postToJsonObject(
                QuipAccess.ENDPOINT + "/threads/add-members",
                Form.form().add("thread_id", getId()).add("member_ids", Stream
                        .of(folderOrUserIds).collect(Collectors.joining(","))));
        if (object == null)
            return false;
        _replace(object);
        return true;
    }

    public boolean removeMember(String folderOrUserId) throws Exception {
        return removeMembers(new String[]{folderOrUserId});
    }

    public boolean removeMembers(String[] folderOrUserIds) throws Exception {
        JsonObject object = _postToJsonObject(
                QuipAccess.ENDPOINT + "/folders/remove-members",
                Form.form().add("thread_id", getId()).add("member_ids", Stream
                        .of(folderOrUserIds).collect(Collectors.joining(","))));
        if (object == null)
            return false;
        _replace(object);
        return true;
    }

    public boolean editShareLinkSettings(Mode mode, Boolean allowExternalAccess,
                                         Boolean showConversation, Boolean showEditHistory,
                                         Boolean allowMessages, Boolean allowComments,
                                         Boolean enableRequestAccess) throws Exception {
        Form form = Form.form().add("thread_id", getId());
        if (mode != null)
            form.add("mode", mode._value);
        if (allowExternalAccess != null)
            form.add("allow_external_access",
                    String.valueOf(allowExternalAccess));
        if (showConversation != null)
            form.add("show_conversation", String.valueOf(showConversation));
        if (showEditHistory != null)
            form.add("show_edit_history", String.valueOf(showEditHistory));
        if (allowMessages != null)
            form.add("allow_messages", String.valueOf(allowMessages));
        if (allowComments != null)
            form.add("allow_comments", String.valueOf(allowComments));
        if (enableRequestAccess != null)
            form.add("enable_request_access",
                    String.valueOf(enableRequestAccess));
        JsonObject json = _postToJsonObject(
                QuipAccess.ENDPOINT + "/threads/edit-share-link-settings",
                form);
        return json.get(getId()).getAsString().equals("success");
    }

    // ============================================
    // Table
    // ============================================

    public String[] getTableIds() {
        String html = getHtml();
        if (html == null)
            return null;
        Elements tables = Jsoup.parse(html).getElementsByTag("table");
        if (tables == null)
            return null;
        return tables.stream().map(e -> e.attr("id")).toArray(String[]::new);
    }

    public QuipTable getTableById(String tableId) {
        Element table = getTableElementById_(tableId);
        if (table == null)
            return null;
        return new QuipTable(this, table);
    }

    Element getTableElementById_(String tableId) {
        String html = getHtml();
        if (html == null)
            return null;
        return Jsoup.parse(html).getElementsByAttributeValue("id", tableId)
                .first();
    }
}
