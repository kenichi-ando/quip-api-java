package kenichia.quipapi;

import java.time.Instant;
import java.util.Objects;
import java.util.stream.StreamSupport;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

class QuipJsonObject extends QuipAccess {

	private JsonObject _jsonObject;

	// ============================================
	// Constructor
	// ============================================

	protected QuipJsonObject(JsonObject object) {
		Objects.requireNonNull(object);
		_jsonObject = object;
	}

	// ============================================
	// Public
	// ============================================

	@Override
	public String toString() {
		return _jsonObject.toString();
	}

	// ============================================
	// Protected
	// ============================================

	protected void _replace(JsonObject object) {
		Objects.requireNonNull(object);
		_jsonObject = object;
	}

	protected String _getString(String key) {
		JsonElement element = _jsonObject.get(key);
		return (element == null) ? null : element.getAsString();
	}

	protected boolean _getBoolean(String key) {
		JsonElement element = _jsonObject.get(key);
		return (element == null) ? null : element.getAsBoolean();
	}

	protected int _getInt(String key) {
		JsonElement element = _jsonObject.get(key);
		return (element == null) ? null : element.getAsInt();
	}

	protected double _getDouble(String key) {
		JsonElement element = _jsonObject.get(key);
		return (element == null) ? null : element.getAsDouble();
	}

	protected JsonObject _getJsonObject(String key) {
		JsonElement element = _jsonObject.get(key);
		return (element == null) ? null : element.getAsJsonObject();
	}

	protected String _getString(String keyToJsonObject, String keyToString) {
		JsonObject object = _getJsonObject(keyToJsonObject);
		if (object == null)
			return null;
		JsonElement element = object.get(keyToString);
		return (element == null) ? null : element.getAsString();
	}

	protected JsonArray _getJsonArray(String key) {
		JsonElement element = _jsonObject.get(key);
		return (element == null) ? null : element.getAsJsonArray();
	}

	protected String[] _getStringArray(String key) {
		JsonArray array = _getJsonArray(key);
		if (array == null)
			return null;
		return _toStringArray(array);
	}

	protected String[] _getStringArray(String keyToJsonObject, String keyToArray) {
		JsonObject object = _getJsonObject(keyToJsonObject);
		if (object == null)
			return null;
		JsonArray array = object.get(keyToArray).getAsJsonArray();
		if (array == null)
			return null;
		return _toStringArray(array);
	}

	protected Instant _getInstant(String key) {
		return _toInstant(_jsonObject, key);
	}

	protected Instant _getInstant(String keyToJsonObject, String keyToInstant) {
		JsonObject object = _getJsonObject(keyToJsonObject);
		if (object == null)
			return null;
		return _toInstant(object, keyToInstant);
	}

	// ============================================
	// Private
	// ============================================

	private String[] _toStringArray(JsonArray array) {
		return StreamSupport.stream(array.spliterator(), false)
				.map(e -> e.getAsString())
				.toArray(String[]::new);
	}

	private Instant _toInstant(JsonObject object, String key) {
		long usec = object.get(key).getAsLong();
		return Instant.ofEpochSecond(usec / 1000000, (usec % 1000000) * 1000);
	}
}