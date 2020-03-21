package quipapiclient;

import java.time.Instant;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.http.client.fluent.Form;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
			return Stream.of(values()).filter(e -> e._value.equals(value)).findFirst().orElse(MANILA);
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
			} else if (obj.has("thread_id")){
				_isFolder = false;
				_folderOrThreadId = obj.get("thread_id").getAsString();
			} else {
				throw new IllegalArgumentException("Invalid json object: " + obj);
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
		return _json.get("folder").getAsJsonObject().get("id").getAsString();
	}

	public String getTitle() {
		return _json.get("folder").getAsJsonObject().get("title").getAsString();
	}

	public Instant getCreatedUsec() {
		return _toInstant(_json.get("folder").getAsJsonObject(), "created_usec");
	}

	public Instant getUpdatedUsec() {
		return _toInstant(_json.get("folder").getAsJsonObject(), "updated_usec");
	}

	public String getCreatorId() {
		return _json.get("folder").getAsJsonObject().get("creator_id").getAsString();
	}

	public Color getColor() {
		return Color.find(_json.get("folder").getAsJsonObject().get("color").getAsString());
	}

	public String getParentId() {
		return _json.get("folder").getAsJsonObject().get("parent_id").getAsString();
	}

	public String[] getMemberIds() {
		return _toStringArray(_json, "member_ids");
	}

	public Node[] getChildren() {
		JsonArray json = new Gson().fromJson(_json.get("children"), JsonArray.class);
		return StreamSupport.stream(json.spliterator(), false)
				.map(child -> new Node((JsonObject) child))
				.toArray(Node[]::new);
	}

	// ============================================
	// Read
	// ============================================

	public static QuipFolder getFolder(String folderId) throws Exception {
		return new QuipFolder(_getToJsonObject("https://platform.quip.com/1/folders/" + folderId));
	}

	public static QuipFolder[] getFolders(String[] folderIds) throws Exception {
		JsonObject json = _getToJsonObject("https://platform.quip.com/1/folders/",
				"ids=" + Stream.of(folderIds).collect(Collectors.joining(",")));
		return json.keySet().stream().map(id -> new QuipFolder(json.get(id).getAsJsonObject()))
				.toArray(QuipFolder[]::new);
	}

	public void reload() throws Exception {
		_json = _getToJsonObject("https://platform.quip.com/1/folders/" + getId());
	}

	// ============================================
	// Create / Update
	// ============================================

	public static QuipFolder create(String title, Color color, String parentId, String[] memberIds) throws Exception {
		Form form = Form.form();
		if (title != null)
			form.add("title", title);
		if (color != null)
			form.add("color", color._value);
		if (parentId != null)
			form.add("parent_id", parentId);
		if (memberIds != null)
			form.add("member_ids", Stream.of(memberIds).collect(Collectors.joining(",")));
		return new QuipFolder(_postToJsonObject("https://platform.quip.com/1/folders/new", form));
	}

	public void update(String title, Color color) throws Exception {
		Form form = Form.form().add("folder_id", getId());
		if (title != null)
			form.add("title", title);
		if (color != null)
			form.add("color", color._value);
		_json = _postToJsonObject("https://platform.quip.com/1/folders/update", form);
	}

	// ============================================
	// Members
	// ============================================

	public void addMember(String userId) throws Exception {
		addMembers(new String[] { userId });
	}

	public void addMembers(String[] userIds) throws Exception {
		_json = _postToJsonObject("https://platform.quip.com/1/folders/add-members",
				Form.form()
				.add("folder_id", getId())
				.add("member_ids", Stream.of(userIds).collect(Collectors.joining(","))));
	}

	public void removeMember(String userId) throws Exception {
		removeMembers(new String[] { userId });
	}

	public void removeMembers(String[] userIds) throws Exception {
		_json = _postToJsonObject("https://platform.quip.com/1/folders/remove-members",
				Form.form()
				.add("folder_id", getId())
				.add("member_ids", Stream.of(userIds).collect(Collectors.joining(","))));
	}
}