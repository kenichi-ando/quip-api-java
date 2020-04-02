package quipapiclient;

import com.google.gson.JsonObject;

public class QuipBlob extends QuipJsonObject {

	// ============================================
	// Constructor
	// ============================================

	protected QuipBlob(JsonObject json) {
		super(json);
	}

	// ============================================
	// Properties
	// ============================================

	public String getId() {
		return _getString("id");
	}

	public String getUrl() {
		return _getString("url");
	}
}