package kenichia.quip.api;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class QuipThread extends QuipJsonObject {

	// ============================================
	// Enum
	// ============================================

	public enum Type {
		DOCUMENT("document"),
		SPREADSHEET("spreadsheet"),
		SLIDES("slides"),
		CHAT("chat");

		private final String _value;

		private Type(String value) {
			_value = value;
		}

		private static Type find(String value) {
			return Stream.of(values()).filter(e -> e._value.equals(value)).findFirst().orElse(DOCUMENT);
		}
	}

	public enum Format {
		HTML("html"),
		MARKDOWN("markdown");

		private final String _value;

		private Format(String value) {
			_value = value;
		}
	}
	
	public enum Location {
		APPEND(0),
		PREPEND(1),
		AFTER_SECTION(2),
		BEFORE_SECTION(3),
		REPLACE_SECTION(4),
		DELETE_SECTION(5);

		private final int _value;

		private Location(int value) {
			_value = value;
		}
	}

	public enum Frame {
		BUBBLE("bubble"),
		CARD("card"),
		LINE("line");

		private final String _value;

		private Frame(String value) {
			_value = value;
		}
	};

	public enum Mode {
		VIEW("view"),
		EDIT("edit"),
		NONE("none");

		private final String _value;

		private Mode(String value) {
			_value = value;
		}
	};

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

	// ============================================
	// Read / Search
	// ============================================

	public static QuipThread getThread(String threadId) throws Exception {
		return new QuipThread(_getToJsonObject("https://platform.quip.com/1/threads/" + threadId));
	}

	public static QuipThread[] getThreads(String[] threadIds) throws Exception {
		JsonObject json = _getToJsonObject(new URIBuilder("https://platform.quip.com/1/threads/")
				.addParameter("ids", Stream.of(threadIds).collect(Collectors.joining(","))).build());
		return json.keySet().stream().map(id -> new QuipThread(json.get(id).getAsJsonObject()))
				.toArray(QuipThread[]::new);
	}

	public static QuipThread[] getRecentThreads() throws Exception {
		JsonObject json = _getToJsonObject("https://platform.quip.com/1/threads/recent");
		return StreamSupport.stream(json.entrySet().spliterator(), false)
				.map(obj -> new QuipThread((JsonObject) obj.getValue()))
				.toArray(QuipThread[]::new);
	}

	public static QuipThread[] searchThreads(String query, Integer count, Boolean isOnlyMatchTitles) throws Exception {
		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("query", query));
		if (count != null)
			params.add(new BasicNameValuePair("count", String.valueOf(count)));
		if (isOnlyMatchTitles != null)
			params.add(new BasicNameValuePair("only_match_titles", String.valueOf(isOnlyMatchTitles)));
		JsonArray arr = _getToJsonArray(
				new URIBuilder("https://platform.quip.com/1/threads/search").addParameters(params).build());
		return StreamSupport.stream(arr.spliterator(), false)
				.map(obj -> new QuipThread(obj.getAsJsonObject()))
				.toArray(QuipThread[]::new);
	}

	public boolean reload() throws Exception {
		JsonObject object = _getToJsonObject("https://platform.quip.com/1/threads/" + getId());
		if (object == null)
			return false;
		_replace(object);
		return true;
	}

	// ============================================
	// Create / Update / Delete
	// ============================================
	
	public static QuipThread createDocument(String title, String content, String[] memberIds, Format format, Type type) throws Exception {
		Form form = Form.form();
		if (title != null)
			form.add("title", title);
		if (content != null)
			form.add("content", content);
		if (memberIds != null)
			form.add("member_ids", Stream.of(memberIds).collect(Collectors.joining(",")));
		if (format != null)
			form.add("format", format._value);
		if (type != null)
			form.add("type", type._value);
		return new QuipThread(_postToJsonObject("https://platform.quip.com/1/threads/new-document", form));
	}
	
	public static QuipThread createChat(String title, String message, String[] memberIds) throws Exception {
		Form form = Form.form();
		if (title != null)
			form.add("title", title);
		if (message != null)
			form.add("message", message);
		if (memberIds != null)
			form.add("member_ids", Stream.of(memberIds).collect(Collectors.joining(",")));
		return new QuipThread(_postToJsonObject("https://platform.quip.com/1/threads/new-chat", form));
	}

	public QuipThread copyDocument(String title, String values, String[] memberIds, String[] folderIds) throws Exception {
		Form form = Form.form().add("thread_id", getId());
		if (title != null)
			form.add("title", title);
		if (values != null)
			form.add("values", values);
		if (memberIds != null)
			form.add("member_ids", Stream.of(memberIds).collect(Collectors.joining(",")));
		if (folderIds != null)
			form.add("folder_ids", Stream.of(folderIds).collect(Collectors.joining(",")));
		return new QuipThread(_postToJsonObject("https://platform.quip.com/1/threads/copy-document", form));
	}

	public boolean editDocument(String content, Format format, String sectionId, Location location) throws Exception {
		Form form = Form.form().add("thread_id", getId());
		if (format != null)
			form.add("format", format._value);
		if (content != null)
			form.add("content", content);
		if (sectionId != null)
			form.add("section_id", sectionId);
		if (location != null)
			form.add("location", String.valueOf(location._value));
		JsonObject object = _postToJsonObject("https://platform.quip.com/1/threads/edit-document", form);
		if (object == null)
			return false;
		_replace(object);
		return true;
	}

	public boolean createLivePasteSection(String sourceThreadId, String[] sourceSectionIds, String destinationSectionId,
			Location location, Boolean isUpdateAutomatic) throws Exception {
		Form form = Form.form().add("destination_thread_id", getId());
		if (sourceThreadId != null)
			form.add("source_thread_id", sourceThreadId);
		if (sourceSectionIds != null)
			form.add("source_section_ids", Stream.of(sourceSectionIds).collect(Collectors.joining(",")));
		if (destinationSectionId != null)
			form.add("destination_section_id", destinationSectionId);
		if (location != null) {
			if (location == Location.DELETE_SECTION)
				throw new IllegalArgumentException("The location of DELETE_SECTION is not supported.");
			form.add("location", String.valueOf(location._value));
		}
		if (isUpdateAutomatic != null)
			form.add("update_automatic", String.valueOf(isUpdateAutomatic));
		JsonObject object = _postToJsonObject("https://platform.quip.com/1/threads/live-paste", form);
		if (object == null)
			return false;
		_replace(object);
		return true;
	}

	public void delete() throws Exception {
		_postToJsonObject("https://platform.quip.com/1/threads/delete",
				Form.form()
				.add("thread_id", getId()));
	}

	public void lockEdits(Boolean isEditsDisabled) throws Exception {
		_postToJsonObject("https://platform.quip.com/1/threads/lock-edits",
				Form.form()
				.add("thread_id", getId())
				.add("edits_disabled", String.valueOf(isEditsDisabled)));
	}

	// ============================================
	// Import / Export
	// ============================================

	public static QuipThread importFile(File file, Type type, String title, String[] memberIds) throws Exception {
		MultipartEntityBuilder multipart = MultipartEntityBuilder.create().addBinaryBody("file", file);
		if (type != null)
			multipart.addTextBody("type", type._value);
		if (title != null)
			multipart.addTextBody("title", title);
		if (memberIds != null)
			multipart.addTextBody("member_ids", Stream.of(memberIds).collect(Collectors.joining(",")));
		return new QuipThread(_postToJsonObject("https://platform.quip.com/1/threads/import-file", multipart));
	}

	public byte[] exportAsDocx() throws Exception {
		return _getToByteArray("https://platform.quip.com/1/threads/" + getId() + "/export/docx");
	}

	public byte[] exportAsXlsx() throws Exception {
		return _getToByteArray("https://platform.quip.com/1/threads/" + getId() + "/export/xlsx");
	}

	public byte[] exportAsPdf() throws Exception {
		return _getToByteArray("https://platform.quip.com/1/threads/" + getId() + "/export/pdf");
	}

	// ============================================
	// Messages
	// ============================================

	public QuipMessage[] getRecentMessages() throws Exception {
		JsonArray arr = _getToJsonArray("https://platform.quip.com/1/messages/" + getId());
		return StreamSupport.stream(arr.spliterator(), false)
				.map(obj -> new QuipMessage(obj.getAsJsonObject()))
				.toArray(QuipMessage[]::new);
	}

	public QuipMessage addMessage(Frame frame, String content, String parts, Boolean isSilent, String[] blobIds, String annotationId, String sectionId) throws Exception {
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
			form.add("attachments", Stream.of(blobIds).collect(Collectors.joining(",")));
		if (annotationId != null)
			form.add("annotation_id", annotationId);
		if (sectionId != null)
			form.add("section_id", sectionId);
		return new QuipMessage(_postToJsonObject("https://platform.quip.com/1/messages/new", form));
	}

	// ============================================
	// Blobs
	// ============================================

	public byte[] getBlob(String blobId) throws Exception {
		return _getToByteArray("https://platform.quip.com/1/blob/" + getId() + "/" + blobId);
	}

	public QuipBlob addBlob(File file) throws Exception {
		return new QuipBlob(_postToJsonObject("https://platform.quip.com/1/blob/" + getId(),
				MultipartEntityBuilder.create().addBinaryBody("blob", file)));
	}

	// ============================================
	// Members
	// ============================================

	public boolean addMember(String folderOrUserId) throws Exception {
		return addMembers(new String[] { folderOrUserId });
	}

	public boolean addMembers(String[] folderOrUserIds) throws Exception {
		JsonObject object = _postToJsonObject("https://platform.quip.com/1/threads/add-members",
				Form.form()
				.add("thread_id", getId())
				.add("member_ids", Stream.of(folderOrUserIds).collect(Collectors.joining(","))));
		if (object == null)
			return false;
		_replace(object);
		return true;
	}

	public boolean removeMember(String folderOrUserId) throws Exception {
		return removeMembers(new String[] { folderOrUserId });
	}

	public boolean removeMembers(String[] folderOrUserIds) throws Exception {
		JsonObject object = _postToJsonObject("https://platform.quip.com/1/folders/remove-members",
				Form.form()
				.add("thread_id", getId())
				.add("member_ids", Stream.of(folderOrUserIds).collect(Collectors.joining(","))));
		if (object == null)
			return false;
		_replace(object);
		return true;
	}

	public boolean editShareLinkSettings(Mode mode, Boolean allowExternalAccess, Boolean showConversation, Boolean showEditHistory,
			Boolean allowMessages, Boolean allowComments, Boolean enableRequestAccess) throws Exception {
		Form form = Form.form().add("thread_id", getId());
		if (mode != null)
			form.add("mode", mode._value);
		if (allowExternalAccess != null)
			form.add("allow_external_access", String.valueOf(allowExternalAccess));
		if (showConversation != null)
			form.add("show_conversation", String.valueOf(showConversation));
		if (showEditHistory != null)
			form.add("show_edit_history", String.valueOf(showEditHistory));
		if (allowMessages != null)
			form.add("allow_messages", String.valueOf(allowMessages));
		if (allowComments != null)
			form.add("allow_comments", String.valueOf(allowComments));
		if (enableRequestAccess != null)
			form.add("enable_request_access", String.valueOf(enableRequestAccess));
		JsonObject json = _postToJsonObject("https://platform.quip.com/1/threads/edit-share-link-settings", form);
		return json.get(getId()).getAsString().equals("success");
	}
}
