package quipapiclient;

import java.util.Objects;

import com.google.gson.JsonObject;

class QuipJsonObject extends QuipAccess {

	protected JsonObject _json;

	// ============================================
	// Constructor
	// ============================================

	protected QuipJsonObject(JsonObject json) {
		Objects.requireNonNull(json);
		_json = json;
	}

	// ============================================
	// Protected
	// ============================================

	@Override
	public String toString() {
		return _json.toString();
	}
}
