package kenichia.quipapi.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import kenichia.quipapi.QuipClient;
import kenichia.quipapi.QuipMessage;
import kenichia.quipapi.QuipThread;
import kenichia.quipapi.QuipUser;
import kenichia.quipapi.QuipWebSocket;
import kenichia.quipapi.QuipWebSocketEvent;

public class QuipWebSocketTest implements QuipWebSocketEvent {

	private int aliveCounter = 0;

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

		qws.open(this);
		qws.checkAlive();
		qws.checkAlive();
		qws.checkAlive();
		Thread.sleep(5000);
		assertEquals(3, aliveCounter);
		qws.close();
	}

	@Override
	public void onMessage(QuipMessage message, QuipUser user, QuipThread thread) {
		System.out.println("onMessage: " + message.getText() + " by " + message.getAuthorName());
	}

	@Override
	public void onAlive(String message) {
		System.out.println("onAlive: " + message);
		aliveCounter++;
	}

	@Override
	public void onError(String debug) {
		System.out.println("onError: " + debug);
	}
}