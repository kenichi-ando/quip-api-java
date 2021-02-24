/*
 * Copyright 2021 Kenichi Ando
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package kenichia.quipapi.test;

import java.io.File;
import java.util.Scanner;
import kenichia.quipapi.QuipBlob;
import kenichia.quipapi.QuipClient;
import kenichia.quipapi.QuipThread;
import kenichia.quipapi.QuipThread.Format;
import kenichia.quipapi.QuipThread.Type;
import kenichia.quipapi.QuipToken;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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
        String authUrl =
            QuipClient.getAuthorizationUrl(
                QUIP_CLIENT_ID, QUIP_CLIENT_SECRET, QUIP_REDIRECT_URI, null);
        System.out.print("Open this URL:\n" + authUrl + "\n");
        Scanner scanner = new Scanner(System.in);
        String code = scanner.nextLine();
        scanner.close();
        token =
            QuipClient.generateToken(QUIP_CLIENT_ID, QUIP_CLIENT_SECRET, QUIP_REDIRECT_URI, code);
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
    QuipThread thread =
        QuipThread.createDocument("Document1", "Let's start!", null, Format.HTML, Type.DOCUMENT);
    QuipBlob blob = thread.addBlob(new File(IMAGE_FILE_PATH));
    thread.editDocument("Here is the image.", Format.HTML, null, null);
    thread.editDocument("<img src='" + blob.getUrl() + "'>", Format.HTML, null, null);

    // Delete the document
    thread.delete();
  }
}