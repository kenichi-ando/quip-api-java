package kenichia.quip.api.test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.http.WebSocket;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import kenichia.quip.api.QuipClient;
import kenichia.quip.api.QuipWebSocket;

public class QuipWebSocketTest {

	@BeforeAll
	static void init() throws Exception {
		QuipClient.enableDebug(true);
		QuipClient.setAccessToken(System.getenv("QUIP_ACCESS_TOKEN"));
	}

	@Test
	void createWebSocket() throws Exception {
		QuipWebSocket qws = QuipWebSocket.create();
		assertNotNull(qws);
		assertFalse(qws.getUserId().isEmpty());
		assertFalse(qws.getUrl().isEmpty());

		WebSocket webSocket = qws.createWebSocket();
		assertNotNull(webSocket);
		Thread.sleep(30000);
	}
}