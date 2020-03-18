package quipapiclient.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import quipapiclient.QuipClient;
import quipapiclient.QuipMessage;
import quipapiclient.QuipThread;
import quipapiclient.QuipThread.Format;
import quipapiclient.QuipThread.Frame;
import quipapiclient.QuipThread.Mode;
import quipapiclient.QuipThread.Type;

public class QuipThreadTest {
	@BeforeAll
	static void init() throws Exception {
		QuipClient.enableDebug(true);
		QuipClient.setAccessToken(System.getenv("QUIP_ACCESS_TOKEN"));
	}

	@Test
	void getThread() throws Exception {
		QuipThread doc1 = QuipThread.createDocument("ドキュメント１🌈", "あいうえお🔥", null, Format.HTML, Type.DOCUMENT);
		QuipThread doc1a = QuipThread.getThread(doc1.getId());
		assertEquals(doc1.getId(), doc1a.getId());
		doc1.delete();
	}

	@Test
	void getThreads() throws Exception {
		QuipThread doc1 = QuipThread.createDocument("ドキュメント１🌈", "あいうえお🔥", null, Format.HTML, Type.DOCUMENT);
		QuipThread[] docs = QuipThread.getThreads(new String[] { doc1.getId(), doc1.getId() } );
		assertEquals(doc1.getId(), docs[0].getId());
		doc1.delete();
	}

	@Test
	void getRecentThreads() throws Exception {
		QuipThread[] threads = QuipThread.getRecentThreads();
		for (QuipThread t: threads) {
			System.out.println(t.getId() + ", " + t.getTitle() + ", " + t.getLink());
		}
	}
	
	@Test
	void createDocument() throws Exception {
		QuipThread doc = QuipThread.createDocument("ドキュメント１🌈", "あいうえお🔥", null, Format.HTML, Type.DOCUMENT);
		assertFalse(doc.getId().isEmpty());
		assertEquals("ドキュメント１🌈", doc.getTitle());
		assertTrue(doc.getHtml().contains("あいうえお🔥"));
		assertEquals(Type.DOCUMENT, doc.getType());
		assertFalse(doc.getLink().isEmpty());
		assertNotNull(doc.getCreatedUsec());
		assertNotNull(doc.getUpdatedUsec());
		assertNull(doc.getSharing());
		assertFalse(doc.getAuthorId().isEmpty());
		assertEquals(0, doc.getSharedFolderIds().length);
		assertEquals(1, doc.getUserIds().length);
		assertEquals(1, doc.getExpandedUserIds().length);
		doc.delete();
	}

	@Test
	void createChatRoom() throws Exception {
		QuipThread chat = QuipThread.createChat("チャットルーム１🌈", "メッセージ１🔥", null);
		assertFalse(chat.getId().isEmpty());
		assertEquals("チャットルーム１🌈", chat.getTitle());
		assertNull(chat.getHtml());
		assertEquals(Type.CHAT, chat.getType());
		assertFalse(chat.getLink().isEmpty());
		assertNotNull(chat.getCreatedUsec());
		assertNotNull(chat.getUpdatedUsec());
		assertNull(chat.getSharing());
		assertFalse(chat.getAuthorId().isEmpty());
		assertEquals(0, chat.getSharedFolderIds().length);
		assertEquals(1, chat.getUserIds().length);
		assertEquals(1, chat.getExpandedUserIds().length);
		
		QuipMessage msg = chat.addMessage(Frame.BUBBLE, "メッセージ２🔥", null, false, null, null, null);
		assertFalse(msg.getId().isEmpty());
		assertFalse(msg.getAuthorId().isEmpty());
		assertFalse(msg.getAuthorName().isEmpty());
		assertTrue(msg.isVisible());
		assertNotNull(msg.getCreatedUsec());
		assertNotNull(msg.getUpdatedUsec());
		assertEquals("メッセージ２🔥", msg.getText());
		assertNull(msg.getParts());
		assertNull(msg.getAnnotationId());

		chat.delete();
	}

	@Test
	void searchThreads() throws Exception {
		QuipThread[] results = QuipThread.searchThreads("コピー先ドキュメント", 10, false);
		for (QuipThread t: results) {
			System.out.println(t.getTitle());
		}
	}

	@Test
	void copyDocument() throws Exception {
		QuipThread thread1 = QuipThread.createDocument("コピー元ドキュメント🌈", "コンテント...", null, null, null);
		QuipThread thread2 = thread1.copyDocument("コピー先ドキュメント🔥", null, null, null);
		assertTrue(thread1.getTitle().equals("コピー元ドキュメント🌈"));
		assertTrue(thread2.getTitle().equals("コピー先ドキュメント🔥"));
		thread1.delete();
		thread2.delete();
	}

	@Test
	void lockEdits() throws Exception {
		QuipThread thread1 = QuipThread.createDocument("ロック🌈", "コンテント...", null, null, null);
		thread1.lockEdits(true);
		thread1.lockEdits(false);
		thread1.delete();
	}
	
	@Test
	void editShareLinkSettings() throws Exception {
		QuipThread thread1 = QuipThread.createDocument("シェア🌈", "コンテント...", null, null, null);
		thread1.editShareLinkSettings(Mode.EDIT, true, true, true, true, true, true);
		thread1.editShareLinkSettings(Mode.VIEW, true, false, true, false, true, false);
		thread1.editShareLinkSettings(Mode.NONE, true, true, true, true, true, true);
		thread1.delete();
	}
}