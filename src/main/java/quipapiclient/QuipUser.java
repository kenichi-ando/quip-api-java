package quipapiclient;

import java.time.Instant;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.http.client.fluent.Form;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class QuipUser extends QuipJsonObject {

	// ============================================
	// Constructor
	// ============================================

	protected QuipUser(JsonObject json) {
		super(json);
	}
	
	// ============================================
	// Properties
	// ============================================

	public String getId() {
		return _json.get("id").getAsString();
	}

	public String getName() {
		return _json.get("name").getAsString();
	}

	public double getAffinity() {
		return _json.get("affinity").getAsDouble();
	}

	public String getProfilePictureUrl() {
		return _json.get("profile_picture_url").getAsString();
	}

	public String getChatThreadId() {
		JsonElement element = _json.get("chat_thread_id");
		return (element == null) ? null : element.getAsString();
	}

	public String getDesktopFolderId() {
		return _json.get("desktop_folder_id").getAsString();
	}

	public String getArchiveFolderId() {
		return _json.get("archive_folder_id").getAsString();
	}

	public String getStarredFolderId() {
		return _json.get("starred_folder_id").getAsString();
	}

	public String getPrivateFolderId() {
		return _json.get("private_folder_id").getAsString();
	}

	public String getTrashFolderId() {
		return _json.get("trash_folder_id").getAsString();
	}

	public String[] getGroupFolderIds() {
		return _toStringArray(_json, "group_folder_ids");
	}

	public String[] getSharedFolderIds() {
		return _toStringArray(_json, "shared_folder_ids");
	}

	public boolean isDisabled() {
		return _json.get("disabled").getAsBoolean();
	}

	public Instant getCreatedUsec() {
		return _toInstant(_json, "created_usec");
	}

	public String[] getEmails() {
		return _toStringArray(_json, "emails");
	}

	public String getSubDomain() {
		return _json.get("subdomain").getAsString();
	}

	public String getUrl() {
		return _json.get("url").getAsString();
	}

	public boolean isRobot() {
		return _json.get("is_robot").getAsBoolean();
	}

	// ============================================
	// Read
	// ============================================

	public static QuipUser getCurrentUser() throws Exception {
		return new QuipUser(_getToJsonObject("https://platform.quip.com/1/users/current"));
	}

	public static QuipUser getUser(String userIdOrEmail) throws Exception {
		return new QuipUser(_getToJsonObject("https://platform.quip.com/1/users/" + userIdOrEmail));
	}

	public static QuipUser[] getUsers(String[] userIdOrEmails) throws Exception {
		JsonObject json = _getToJsonObject("https://platform.quip.com/1/users/",
				"ids=" + Stream.of(userIdOrEmails).collect(Collectors.joining(",")));
		return json.keySet().stream()
				.map(id -> new QuipUser(json.get(id).getAsJsonObject()))
				.toArray(QuipUser[]::new);
	}

	public static QuipUser[] getContacts() throws Exception {
		JsonArray json = _getToJsonArray("https://platform.quip.com/1/users/contacts");
		return StreamSupport.stream(json.spliterator(), false)
				.map(obj -> new QuipUser(obj.getAsJsonObject()))
				.toArray(QuipUser[]::new);
	}

	public void reload() throws Exception {
		_json = _getToJsonObject("https://platform.quip.com/1/users/" + getId());
	}

	// ============================================
	// Update
	// ============================================
	
	public void update(String profilePictureUrl) throws Exception {
		Form form = Form.form()
				.add("user_id", getId())
				.add("profile_picture_url", profilePictureUrl);
		_json = _postToJsonObject("https://platform.quip.com/1/users/update", form);
	}
}
