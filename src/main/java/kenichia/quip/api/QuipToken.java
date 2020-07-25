package kenichia.quip.api;

import com.google.gson.JsonObject;

public class QuipToken extends QuipJsonObject {

	// ============================================
	// Constructor
	// ============================================

	protected QuipToken(JsonObject json) {
		super(json);
	}

	// ============================================
	// Properties
	// ============================================

	public String getAccessToken() {
		return _getString("access_token");
	}

	public String getRefreshToken() {
		return _getString("refresh_token");
	}

	public int getExpiresIn() {
		return _getInt("expires_in");
	}

	public String getScope() {
		return _getString("scope");
	}

	public String getTokenType() {
		return _getString("token_type");
	}
}