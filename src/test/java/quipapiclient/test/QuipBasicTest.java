package quipapiclient.test;

import java.io.File;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import quipapiclient.QuipBlob;
import quipapiclient.QuipClient;
import quipapiclient.QuipThread;
import quipapiclient.QuipThread.Format;
import quipapiclient.QuipThread.Type;
import quipapiclient.QuipToken;

public class QuipBasicTest {
	private static String QUIP_ACCESS_TOKEN;
	private static String QUIP_REFRESH_TOKEN;
	private static String QUIP_CLIENT_ID;
	private static String QUIP_CLIENT_SECRET;
	private static String QUIP_REDIRECT_URI;
	private static String IMAGE_FILE_PATH;

	@BeforeAll
	static void init() throws Exception {
		QUIP_ACCESS_TOKEN = System.getenv("QUIP_ACCESS_TOKEN");
		QUIP_REFRESH_TOKEN = System.getenv("QUIP_REFRESH_TOKEN");
		QUIP_CLIENT_ID = System.getenv("QUIP_CLIENT_ID");
		QUIP_CLIENT_SECRET = System.getenv("QUIP_CLIENT_SECRET");
		QUIP_REDIRECT_URI = System.getenv("QUIP_REDIRECT_URI");
		IMAGE_FILE_PATH = "/tmp/image.png";
		QuipClient.enableDebug(true);
	}

	@Test
	void example() throws Exception {
		String accessToken = QUIP_ACCESS_TOKEN;
		if (!QUIP_CLIENT_ID.isEmpty()) {
			QuipToken token = null;
			if (!QUIP_REFRESH_TOKEN.isEmpty()) {
				// Try to renew a new token by refresh token
				token = QuipClient.refreshToken(QUIP_CLIENT_ID, QUIP_CLIENT_SECRET, QUIP_REFRESH_TOKEN);
			}
			if (token == null) {
				// Generate a new access token by authorization code
				String authUrl = QuipClient.getAuthorizationUrl(QUIP_CLIENT_ID, QUIP_CLIENT_SECRET, QUIP_REDIRECT_URI, null);
				System.out.print("Open this URL:\n" + authUrl + "\n");
				Scanner scanner = new Scanner(System.in);
				String code = scanner.nextLine();
				scanner.close();
				token = QuipClient.generateToken(QUIP_CLIENT_ID, QUIP_CLIENT_SECRET, QUIP_REDIRECT_URI, code);
			}
			if (token != null) {
				System.out.println(token.getAccessToken());
				System.out.println(token.getRefreshToken());
				System.out.println(token.getTokenType());
				System.out.println(token.getScope());
				System.out.println(token.getExpiresIn());
				accessToken = token.getAccessToken();
			}
		}

		QuipClient.setAccessToken(accessToken);

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