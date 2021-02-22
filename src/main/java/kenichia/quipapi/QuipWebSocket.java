/*
 * Copyright 2021 Kenichi Ando
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package kenichia.quipapi;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.util.Objects;
import java.util.concurrent.CompletionStage;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class QuipWebSocket extends QuipJsonObject implements Listener {

	private WebSocket _webSocket = null;
	private QuipWebSocketEvent _event = null;

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

	public void open(QuipWebSocketEvent event) {
		Objects.requireNonNull(event);
		URI uri = URI.create(getUrl());
		_event = event;
		_webSocket = HttpClient.newBuilder().build().newWebSocketBuilder()
				.header("Origin", "http://" + uri.getHost())
				.buildAsync(uri, this).join();
	}

	public void close() throws Exception {
		if (_webSocket != null) {
			_webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "ok").get();
			_webSocket = null;
			_event = null;
		}
	}

	public void checkAlive() {
		if (_webSocket != null) {
			JsonObject json = new JsonObject();
			json.addProperty("type", "heartbeat");
			_webSocket.sendText(json.toString(), true);
		}
	}

	// ============================================
	// Create
	// ============================================

	public static QuipWebSocket create() throws Exception {
		return new QuipWebSocket(_getToJsonObject(QuipAccess.ENDPOINT + "/websockets/new"));
	}

	// ============================================
	// Listener
	// ============================================

	@Override
	public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
		if (QuipClient._isDebugEnabled())
			System.out.println("WebSocket> " + data + ", last=" + last);

		JsonObject json = new Gson().fromJson(data.toString(), JsonObject.class);
		switch (json.get("type").getAsString()) {
		case "message":
			_event.onMessage(new QuipMessage(json.get("message").getAsJsonObject()),
					new QuipUser(json.get("user").getAsJsonObject()),
					new QuipThread(json.get("thread").getAsJsonObject()));
			break;
		case "heartbeat":
			_event.onHeartbeat();
			break;
		case "alive":
			_event.onAlive(json.get("message").getAsString());
			break;
		case "error":
			_event.onError(json.get("debug").getAsString());
			break;
		}
		return Listener.super.onText(webSocket, data, last);
	}
}