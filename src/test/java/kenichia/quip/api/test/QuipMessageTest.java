package kenichia.quip.api.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import kenichia.quip.api.QuipBlob;
import kenichia.quip.api.QuipClient;
import kenichia.quip.api.QuipMessage;
import kenichia.quip.api.QuipThread;
import kenichia.quip.api.QuipThread.Format;
import kenichia.quip.api.QuipThread.Frame;
import kenichia.quip.api.QuipThread.Type;

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
	void addMessageToSction() throws Exception {
		QuipThread doc = QuipThread.createDocument("テスト🐷", "# タイトル１🐷\n## セクション１🐷\n## セクション２🐷\n## セクション３🐷", null, Format.MARKDOWN, Type.DOCUMENT);
		String html = doc.getHtml();
		List<String> sectionIds = new ArrayList<>();
		int index = 0;
		while (true) {
			index = html.indexOf("id='", index);
			if (index == -1)
				break;
			index += 4;
			String sectionId = html.substring(index, html.indexOf("'", index));
			sectionIds.add(sectionId);
		}
		QuipMessage msg1 = doc.addMessage(Frame.BUBBLE, "バブル🐷", null, true, null, null, sectionIds.get(1));
		QuipMessage msg2 = doc.addMessage(Frame.LINE, "ライン🐷", null, true, null, null, sectionIds.get(1));
		QuipMessage msg3 = doc.addMessage(Frame.CARD, "カード🐷", null, true, null, null, sectionIds.get(1));
		assertEquals("バブル🐷", msg1.getText());
		assertFalse(msg1.getAnnotationId().isEmpty());
		assertFalse(msg1.getHighlightSectionIds()[0].isEmpty());
		assertEquals("ライン🐷", msg2.getText());
		assertFalse(msg2.getAnnotationId().isEmpty());
		assertFalse(msg2.getHighlightSectionIds()[0].isEmpty());
		assertEquals("カード🐷", msg3.getText());
		assertFalse(msg3.getAnnotationId().isEmpty());
		assertFalse(msg3.getHighlightSectionIds()[0].isEmpty());
		doc.reload();
		doc.delete();
	}

	@Test
	void addMessageToAnnotation() throws Exception {
		QuipThread doc = QuipThread.createDocument("テスト🐷", "# タイトル１🐷", null, Format.MARKDOWN, Type.DOCUMENT);
		String html = doc.getHtml();
		int index = html.indexOf("id='") + 4;
		String sectionId = html.substring(index, html.indexOf("'", index));
		QuipMessage msg1 = doc.addMessage(Frame.BUBBLE, "バブル１🐷", null, true, null, null, sectionId);
		String annotationId = msg1.getAnnotationId();
		String highlightSectionId = msg1.getHighlightSectionIds()[0];
		assertEquals("バブル１🐷", msg1.getText());
		assertFalse(annotationId.isEmpty());
		assertFalse(highlightSectionId.isEmpty());
		QuipMessage msg2 = doc.addMessage(Frame.BUBBLE, "バブル２🐷", null, true, null, null, sectionId);
		assertEquals("バブル２🐷", msg2.getText());
		assertEquals(annotationId, msg2.getAnnotationId());
		assertEquals(highlightSectionId, msg2.getHighlightSectionIds()[0]);
		QuipMessage msg3 = doc.addMessage(Frame.BUBBLE, "バブル３🐷", null, true, null, annotationId, null);
		assertEquals("バブル３🐷", msg3.getText());
		assertEquals(annotationId, msg3.getAnnotationId());
		assertEquals(highlightSectionId, msg3.getHighlightSectionIds()[0]);
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
		QuipBlob blob = chat.addBlob(new File("/tmp/image.png"));
		QuipMessage msg = chat.addMessage(Frame.BUBBLE, "添付🔥", null, false, new String[] { blob.getId() }, null, null);
		assertFalse(msg.getId().isEmpty());
		assertFalse(msg.getAuthorId().isEmpty());
		assertFalse(msg.getAuthorName().isEmpty());
		assertTrue(msg.isVisible());
		assertNotNull(msg.getCreatedUsec());
		assertNotNull(msg.getUpdatedUsec());
		assertEquals("添付🔥", msg.getText());
		assertEquals(1, msg.getFiles().length);
		assertEquals("image.png", msg.getFiles()[0]);
		assertNull(msg.getParts());
		assertNull(msg.getAnnotationId());
		chat.delete();
	}
}