package quipapiclient.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import quipapiclient.QuipBlob;
import quipapiclient.QuipClient;
import quipapiclient.QuipThread;
import quipapiclient.QuipThread.Format;
import quipapiclient.QuipThread.Type;

public class QuipBlobTest {
	@BeforeAll
	static void init() throws Exception {
		QuipClient.enableDebug(true);
		QuipClient.setAccessToken(System.getenv("QUIP_ACCESS_TOKEN"));
	}

	@Test
	void addBlob() throws Exception {
		QuipThread thread = QuipThread.createDocument("Sample document", "Let's start!", null, Format.HTML, Type.DOCUMENT);
		File file = new File("/tmp/image.png");
		QuipBlob blob = thread.addBlob(file);
		assertFalse(blob.getId().isEmpty());
		assertFalse(blob.getUrl().isEmpty());
		thread.editDocument("<img src='" + blob.getUrl() + "'>", Format.HTML, null, null);
		assertTrue(thread.getHtml().contains("/blob/"));
		thread.delete();
	}

	@Test
	void getBlob() throws Exception {
		QuipThread thread = QuipThread.createDocument("Sample document", "Let's start!", null, Format.HTML, Type.DOCUMENT);
		File file = new File("/tmp/image.png");
		QuipBlob blob = thread.addBlob(file);
		byte[] data = thread.getBlob(blob.getId());
		assertEquals(file.length(), data.length);
		thread.delete();
	}

	@Test
	void addBlob2() throws Exception {
		QuipThread thread = QuipThread.createDocument("タイトル🌈", "新規作成🔥", null, Format.HTML, Type.DOCUMENT);
		File file = new File("/tmp/image.png");
		QuipBlob blob = thread.addBlob(file);
		String img = "<img src='" + blob.getUrl() + "'>";
		String html = "<table>";
		html += "<thead><tr><th>名前🔥</th><th>画像🔥</th></tr></thead>";
		html += "<tbody>";
		html += "<tr><td>あいうえお🔥</td><td>" + img + "</td></tr>";
		html += "<tr><td>かきくけこ🔥</td><td>" + img + "</td></tr>";
		html += "</tbody></table>";
		thread.editDocument(html, Format.HTML, null, null);
		thread.delete();
	}
}
