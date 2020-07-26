package kenichia.quipapi.test;

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

		qws.open(new QuipWebSocketEventImpl());
		qws.checkAlive();
		Thread.sleep(60000);
		qws.close();
	}
}

class QuipWebSocketEventImpl implements QuipWebSocketEvent {

	@Override
	public void onMessage(QuipMessage message, QuipUser user, QuipThread thread) {
		System.out.println("onMessage: " + message.getText() + " by " + message.getAuthorName());
	}

	@Override
	public void onAlive(String message) {
		System.out.println("onAlive: " + message);
	}

	@Override
	public void onError(String debug) {
		System.out.println("onError: " + debug);
	}
}