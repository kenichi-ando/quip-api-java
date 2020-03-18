package quipapiclient;

import java.time.Instant;
import java.util.stream.StreamSupport;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
		return _json.get("id").getAsString();
	}

	public String getAuthorId() {
		return _json.get("author_id").getAsString();
	}

	public String getAuthorName() {
		return _json.get("author_name").getAsString();
	}

	public Instant getCreatedUsec() {
		return _instant(_json, "created_usec");
	}

	public Instant getUpdatedUsec() {
		return _instant(_json, "updated_usec");
	}

	public String getText() {
		return _json.get("text").getAsString();
	}

	public String getParts() {
		JsonElement element = _json.get("parts");
		return (element == null) ? null : element.getAsString();
	}

	public String getAnnotationId() {
		//TODO: check highlight_section_ids
		JsonElement element = _json.get("annotation");
		return (element == null) ? null : element.getAsJsonObject().get("id").getAsString();
	}

	public boolean isVisible() {
		return _json.get("visible").getAsBoolean();
	}

	public String[] getFiles() {
		// TODO: check hash attribute
		JsonElement element = _json.get("files");
		if (element == null)
			return null;
		JsonArray arr = element.getAsJsonArray();
		return StreamSupport.stream(arr.spliterator(), false)
				.map(e -> e.getAsJsonObject().get("name").getAsString())
				.toArray(String[]::new);
	}
}