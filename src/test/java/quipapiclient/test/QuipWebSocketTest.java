package quipapiclient.test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import quipapiclient.QuipClient;
import quipapiclient.QuipWebSocket;

public class QuipWebSocketTest {
	@BeforeAll
	static void init() throws Exception {
		QuipClient.enableDebug(true);
		QuipClient.setAccessToken(System.getenv("QUIP_ACCESS_TOKEN"));
	}

	@Test
	void createWebSocket() throws Exception {
		QuipWebSocket ws = QuipWebSocket.create();
		assertNotNull(ws);
		assertFalse(ws.getUserId().isEmpty());
		assertFalse(ws.getUrl().isEmpty());

		QuipWebSocketClient client = new QuipWebSocketClient(new URI(ws.getUrl()));
		assertNotNull(client);
		client.connect();

		// TODO
		// [QuipWebSocketClient] onClose is invoked: 1002, Invalid status code received:
		// 403 Status line: HTTP/1.1 403 Forbidden
	}
}

class QuipWebSocketClient extends WebSocketClient {

	public QuipWebSocketClient(URI serverUri) {
		super(serverUri);
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		System.out.println("[QuipWebSocketClient] onClose is invoked: " + String.valueOf(code) + ", " + reason);
	}

	@Override
	public void onError(Exception e) {
		System.out.println("[QuipWebSocketClient] onError is invoked: " + e);
	}

	@Override
	public void onMessage(String message) {
		System.out.println("[QuipWebSocketClient] onMessage is invoked: " + message);
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		System.out.println("[QuipWebSocketClient] onOpen is invoked: " + handshakedata);
	}
}