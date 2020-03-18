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
		return _json.get("id").getAsString();
	}

	public String getUrl() {
		return _json.get("url").getAsString();
	}	
}
