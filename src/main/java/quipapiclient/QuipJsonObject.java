package quipapiclient;

import java.time.Instant;
import java.util.Objects;
import java.util.stream.StreamSupport;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

class QuipJsonObject extends QuipAccess {

	private JsonObject _json;

	// ============================================
	// Constructor
	// ============================================

	protected QuipJsonObject(JsonObject json) {
		Objects.requireNonNull(json);
		_json = json;
	}

	// ============================================
	// Public
	// ============================================

	@Override
	public String toString() {
		return _json.toString();
	}

	// ============================================
	// Protected
	// ============================================

	protected void _replace(JsonObject json) {
		_json = json;
	}

	protected String _getString(String key) {
		JsonElement element = _json.get(key);
		return (element == null) ? null : element.getAsString();
	}

	protected boolean _getBoolean(String key) {
		JsonElement element = _json.get(key);
		return (element == null) ? null : element.getAsBoolean();
	}

	protected double _getDouble(String key) {
		JsonElement element = _json.get(key);
		return (element == null) ? null : element.getAsDouble();
	}

	protected JsonObject _getJsonObject(String key) {
		JsonElement element = _json.get(key);
		return (element == null) ? null : element.getAsJsonObject();
	}

	protected String _getString(String keyToJsonObject, String keyToString) {
		JsonObject json = _getJsonObject(keyToJsonObject);
		if (json == null)
			return null;
		JsonElement element = json.get(keyToString);
		return (element == null) ? null : element.getAsString();
	}

	protected JsonArray _getJsonArray(String key) {
		JsonElement element = _json.get(key);
		return (element == null) ? null : element.getAsJsonArray();
	}

	protected String[] _getStringArray(String key) {
		JsonArray arr = _getJsonArray(key);
		if (arr == null)
			return null;
		return StreamSupport.stream(arr.spliterator(), false)
				.map(e -> e.getAsString())
				.toArray(String[]::new);
	}

	protected Instant _getInstant(String key) {
		return _toInstant(_json, key);
	}

	protected Instant _getInstant(String keyToJsonObject, String keyToInstant) {
		JsonObject json = _getJsonObject(keyToJsonObject);
		if (json == null)
			return null;
		return _toInstant(json, keyToInstant);
	}

	// ============================================
	// Private
	// ============================================

	private Instant _toInstant(JsonObject json, String key) {
		long usec = json.get(key).getAsLong();
		return Instant.ofEpochSecond(usec / 1000000, (usec % 1000000) * 1000);
	}
}