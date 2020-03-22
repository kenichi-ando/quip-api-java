package quipapiclient.test;

import java.io.File;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import quipapiclient.QuipBlob;
import quipapiclient.QuipClient;
import quipapiclient.QuipThread;
import quipapiclient.QuipThread.Format;
import quipapiclient.QuipThread.Type;

public class QuipBasicTest {
	private static String QUIP_ACCESS_TOKEN;
	private static String IMAGE_FILE_PATH;

	@BeforeAll
	static void init() throws Exception {
		QUIP_ACCESS_TOKEN = System.getenv("QUIP_ACCESS_TOKEN");
		IMAGE_FILE_PATH = "/tmp/image.png";
		QuipClient.enableDebug(true);
	}

	@Test
	void example() throws Exception {
		// Set your personal access token
		QuipClient.setAccessToken(QUIP_ACCESS_TOKEN);

		// Get a list of documents recently updated
		QuipThread[] threads = QuipThread.getRecentThreads();
		for (QuipThread thread : threads) {
			System.out.println(thread.getId() + ": " + thread.getTitle() + ", " + thread.getLink());
		}

		// Create a new document and insert an image into it
		QuipThread thread = QuipThread.createDocument("Document1", "Let's start!", null, Format.HTML, Type.DOCUMENT);
		QuipBlob blob = thread.addBlob(new File(IMAGE_FILE_PATH));
		thread.editDocument("Here is the image.", Format.HTML, null, null);
		thread.editDocument("<img src='" + blob.getUrl() + "'>", Format.HTML, null, null);

		// Delete the document
		thread.delete();
	}
}