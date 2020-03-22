package quipapiclient;

import java.io.IOException;
import java.util.Objects;

public class QuipClient extends QuipAccess {

	private static QuipClient _instance = new QuipClient();
	private String _accessToken;
	private boolean _isDebugEnabled = false;

	// ============================================
	// Authentication
	// ============================================
	
	public static void setAccessToken(String accessToken) throws Exception {
		Objects.requireNonNull(accessToken);
		_instance._accessToken = accessToken;
		if (!_verifyToken())
			throw new IOException("The access token is invalid.");
	}

	public static void enableDebug(boolean isEnabled) {
		_instance._isDebugEnabled = isEnabled;
	}

	// ============================================
	// Protected
	// ============================================

	protected static String _getBearerToken() {
		return "Bearer " + _instance._accessToken;
	}

	protected static boolean _isDebugEnabled() {
		return _instance._isDebugEnabled;
	}

	// ============================================
	// Private
	// ============================================

	private static boolean _verifyToken() throws IOException {
		return (_getToStatusCode("https://platform.quip.com/1/oauth/verify_token") == 200);
	}
}