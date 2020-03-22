package quipapiclient;

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
}