package kenichia.quip.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.util.concurrent.CompletionStage;

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
	// Methods
	// ============================================

	public WebSocket createWebSocket() {
		URI uri = URI.create(getUrl());
		return HttpClient.newBuilder().build().newWebSocketBuilder()
				.header("Origin", "http://" + uri.getHost())
				.buildAsync(uri, new QuipWebSocketListener()).join();
	}

	// ============================================
	// Create
	// ============================================

	public static QuipWebSocket create() throws Exception {
		return new QuipWebSocket(_getToJsonObject("https://platform.quip.com/1/websockets/new"));
	}
}

class QuipWebSocketListener implements Listener {

	@Override
	public void onOpen(WebSocket webSocket) {
		System.out.println("[WebSocket] onOpen: " + webSocket.toString());
		Listener.super.onOpen(webSocket);
	}

	@Override
	public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
		System.out.println(
				"[WebSocket] onClose: status code=" + String.valueOf(statusCode) + ", reason=" + reason);
		return Listener.super.onClose(webSocket, statusCode, reason);
	}

	@Override
	public void onError(WebSocket webSocket, Throwable error) {
		System.out.println("[WebSocket] onError: " + error.getMessage());
		Listener.super.onError(webSocket, error);
	}

	@Override
	public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
		System.out.println("[WebSocket] onText: " + data);
		return Listener.super.onText(webSocket, data, last);
	}
}