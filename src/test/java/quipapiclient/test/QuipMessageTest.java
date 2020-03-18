package quipapiclient.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import quipapiclient.QuipBlob;
import quipapiclient.QuipClient;
import quipapiclient.QuipMessage;
import quipapiclient.QuipThread;
import quipapiclient.QuipThread.Format;
import quipapiclient.QuipThread.Frame;
import quipapiclient.QuipThread.Type;

public class QuipMessageTest {
	@BeforeAll
	static void init() throws Exception {
		QuipClient.enableDebug(true);
		QuipClient.setAccessToken(System.getenv("QUIP_ACCESS_TOKEN"));
	}

	@Test
	void addMessage() throws Exception {
		QuipThread doc = QuipThread.createDocument("ドキュメント１🌈", "あいうえお🔥", null, Format.HTML, Type.DOCUMENT);
		QuipMessage msg = doc.addMessage(Frame.BUBBLE, "コメント１🔥", null, false, null, null, null);
		assertFalse(msg.getId().isEmpty());
		assertFalse(msg.getAuthorId().isEmpty());
		assertFalse(msg.getAuthorName().isEmpty());
		assertTrue(msg.isVisible());
		assertNotNull(msg.getCreatedUsec());
		assertNotNull(msg.getUpdatedUsec());
		assertEquals("コメント１🔥", msg.getText());
		assertNull(msg.getParts());
		assertNull(msg.getAnnotationId());
		doc.delete();
	}

	@Test
	void getRecentMessages() throws Exception {
		QuipThread doc = QuipThread.createDocument("ドキュメント１🌈", "あいうえお🔥", null, Format.HTML, Type.DOCUMENT);
		doc.addMessage(Frame.BUBBLE, "コメント１🔥", null, false, null, null, null);
		doc.addMessage(Frame.LINE, "コメント２🔥", null, false, null, null, null);
		doc.addMessage(Frame.CARD, "コメント３🔥", null, false, null, null, null);

		QuipMessage[] msgs = doc.getRecentMessages();
		assertEquals(3, msgs.length);
		assertEquals("コメント３🔥", msgs[0].getText());
		assertEquals("コメント２🔥", msgs[1].getText());
		assertEquals("コメント１🔥", msgs[2].getText());
		doc.delete();
	}

	@Test
	void addMessageWithAttachment() throws Exception {
		QuipThread chat = QuipThread.createChat("チャットルーム１🌈", "メッセージ１🔥", null);
		QuipBlob blob = chat.addBlob(new File("/tmp/swu.log"));
		QuipMessage msg = chat.addMessage(Frame.BUBBLE, "添付🔥", null, false, new String[] { blob.getId() }, null, null);
		assertFalse(msg.getId().isEmpty());
		assertFalse(msg.getAuthorId().isEmpty());
		assertFalse(msg.getAuthorName().isEmpty());
		assertTrue(msg.isVisible());
		assertNotNull(msg.getCreatedUsec());
		assertNotNull(msg.getUpdatedUsec());
		assertEquals("添付🔥", msg.getText());
		assertEquals(1, msg.getFiles().length);
		assertEquals("swu.log", msg.getFiles()[0]);
		assertNull(msg.getParts());
		assertNull(msg.getAnnotationId());
		chat.delete();
	}
}