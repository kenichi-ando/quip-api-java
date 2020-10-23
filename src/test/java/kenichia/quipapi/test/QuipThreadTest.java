package kenichia.quipapi.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import kenichia.quipapi.QuipClient;
import kenichia.quipapi.QuipMessage;
import kenichia.quipapi.QuipThread;
import kenichia.quipapi.QuipThread.Format;
import kenichia.quipapi.QuipThread.Frame;
import kenichia.quipapi.QuipThread.Location;
import kenichia.quipapi.QuipThread.Mode;
import kenichia.quipapi.QuipThread.Type;

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
        assertEquals(false, doc.isDeleted());
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

		results = QuipThread.searchThreads("追加テスト🌈", 10, false);
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
	void copyDocumentFromTemplate() throws Exception {
		QuipThread template = QuipThread.createDocument("テンプレート🌈", "名前 [[user.name]] 年齢 [[user.age]]歳", null, null, null);
		QuipThread doc1 = template.copyDocument("新規ドキュメント１🔥", "{ \"user\": {\"name\": \"アーニー😊\", \"age\": \"22\" }}", null, null);
		QuipThread doc2 = template.copyDocument("新規ドキュメント２🔥", "{ \"user\": {\"name\": \"テーラー😊\", \"age\": \"33\" }}", null, null);
		assertTrue(doc1.getHtml().contains("アーニー😊"));
		assertTrue(doc1.getHtml().contains("22歳"));
		assertTrue(doc2.getHtml().contains("テーラー😊"));
		assertTrue(doc2.getHtml().contains("33歳"));
		doc1.delete();
		doc2.delete();
		template.delete();
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

	@Test
	void editDocumentWithLocation() throws Exception {
		QuipThread doc = QuipThread.createDocument("テスト編集🐷", "# タイトル１🐷\n## セクション１🐷\n## セクション２🐷\n## セクション３🐷", null, Format.MARKDOWN, Type.DOCUMENT);
		List<String> sectionIds = getSectionIds(doc);
		doc.editDocument("アペンド🐷", Format.HTML, null, Location.APPEND);
		doc.editDocument("プリペンド🐷", Format.HTML, null, Location.PREPEND);
		doc.editDocument("アフターセクション１🐷", Format.HTML, sectionIds.get(1), Location.AFTER_SECTION);
		doc.editDocument("ビフォーセクション３🐷", Format.HTML, sectionIds.get(3), Location.BEFORE_SECTION);
		doc.editDocument("リプレースセクション２🐷", Format.HTML, sectionIds.get(2), Location.REPLACE_SECTION);
		doc.editDocument("削除セクション１🐷", Format.HTML, sectionIds.get(1), Location.DELETE_SECTION);
		assertTrue(doc.getHtml().contains("アペンド🐷"));
		assertTrue(doc.getHtml().contains("プリペンド🐷"));
		assertTrue(doc.getHtml().contains("アフターセクション１🐷"));
		assertTrue(doc.getHtml().contains("ビフォーセクション３🐷"));
		assertTrue(doc.getHtml().contains("リプレースセクション２🐷"));
		assertFalse(doc.getHtml().contains("削除セクション１🐷"));
		doc.delete();
	}

	@Test
	void createLivePasteSection() throws Exception {
		QuipThread srcDoc = QuipThread.createDocument("ライブペースト：コピー元🐷", "# タイトル１🐷\n## セクション１🐷\n## セクション２🐷\n## セクション３🐷", null, Format.MARKDOWN, Type.DOCUMENT);
		QuipThread dstDoc = QuipThread.createDocument("ライブペースト：貼り付け先🐧", "# タイトル１🐧\n## セクション１🐧\n## セクション２🐧\n## セクション３🐧", null, Format.MARKDOWN, Type.DOCUMENT);
		List<String> srcSecIds = getSectionIds(srcDoc);
		List<String> dstSecIds = getSectionIds(dstDoc);
		dstDoc.createLivePasteSection(srcDoc.getId(), new String[] { srcSecIds.get(1) }, dstSecIds.get(1), Location.APPEND, true);
		dstDoc.createLivePasteSection(srcDoc.getId(), new String[] { srcSecIds.get(1) }, dstSecIds.get(1), Location.PREPEND, true);
		dstDoc.createLivePasteSection(srcDoc.getId(), new String[] { srcSecIds.get(2) }, dstSecIds.get(2), Location.AFTER_SECTION, true);
		dstDoc.createLivePasteSection(srcDoc.getId(), new String[] { srcSecIds.get(2) }, dstSecIds.get(2), Location.BEFORE_SECTION, true);
		dstDoc.createLivePasteSection(srcDoc.getId(), new String[] { srcSecIds.get(3) }, dstSecIds.get(3), Location.REPLACE_SECTION, true);
		srcDoc.editDocument("変更🐄１", Format.HTML, srcSecIds.get(1), Location.REPLACE_SECTION);
		srcDoc.editDocument("変更🐄２", Format.HTML, srcSecIds.get(2), Location.REPLACE_SECTION);
		srcDoc.editDocument("変更🐄３", Format.HTML, srcSecIds.get(3), Location.REPLACE_SECTION);
		assertTrue(srcDoc.getHtml().contains("変更🐄１"));
		assertTrue(srcDoc.getHtml().contains("変更🐄２"));
		assertTrue(srcDoc.getHtml().contains("変更🐄３"));
		srcDoc.delete();
		dstDoc.delete();
	}

	@Test
	void testExportPdf() throws Exception {
		QuipThread thread = QuipThread.createDocument("スライドテスト２２🔥", "あいうえお🌈🌈🌈", null, Format.HTML, Type.SLIDES);
		byte[] data = thread.exportAsPdf();
		FileOutputStream fileOuputStream = new FileOutputStream("/tmp/export.pdf");
		fileOuputStream.write(data);
		fileOuputStream.close();
		thread.delete();
	}

    @Test
    void testLockSection() throws Exception {
        QuipThread thread = QuipThread.createDocument("テストロックセクション🐷", "# タイトル１🐷\n## セクション１🐷\n## セクション２🐷\n## セクション３🐷", null, Format.MARKDOWN, Type.DOCUMENT);
        List<String> sectionIds = getSectionIds(thread);
        thread.lockSectionEdits(sectionIds.get(1), true);
        thread.editDocument("変更１🐄", Format.HTML, sectionIds.get(1), Location.REPLACE_SECTION);
        assertTrue(thread.getHtml().contains("セクション１🐷"));
        assertFalse(thread.getHtml().contains("変更１🐄"));
        thread.lockSectionEdits(sectionIds.get(1), false);
        thread.editDocument("変更２🐄", Format.HTML, sectionIds.get(1), Location.REPLACE_SECTION);
        assertFalse(thread.getHtml().contains("セクション１🐷"));
        assertTrue(thread.getHtml().contains("変更２🐄"));
        thread.delete();
    }

	private List<String> getSectionIds(QuipThread doc) {
		String html = doc.getHtml();
		List<String> sectionIds = new ArrayList<>();
		int offset = 0;
		while (true) {
			int start = html.indexOf("id='", offset);
			if (start == -1)
				break;
			start += 4;
			int end = html.indexOf("'", start);
			String sectionId = html.substring(start, end);
			sectionIds.add(sectionId);
			offset = end + 1;
		}
		return sectionIds;
	}
}