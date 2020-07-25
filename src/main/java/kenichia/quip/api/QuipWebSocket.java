package kenichia.quip.api;

import com.google.gson.JsonObject;

public class QuipWebSocket extends QuipJsonObject {

	// ============================================
	// Constructor
	// ============================================

	protected QuipWebSocket(JsonObject json) {
		super(json);
	}

	// ============================================
	// Properties
	// ============================================

	public String getUserId() {
		return _getString("user_id");
	}

	public String getUrl() {
		return _getString("url");
	}

	// ============================================
	// Create
	// ============================================

	public static QuipWebSocket create() throws Exception {
		return new QuipWebSocket(_getToJsonObject("https://platform.quip.com/1/websockets/new"));
	}
}
