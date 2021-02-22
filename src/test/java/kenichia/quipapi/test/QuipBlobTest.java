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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import kenichia.quipapi.QuipBlob;
import kenichia.quipapi.QuipClient;
import kenichia.quipapi.QuipThread;
import kenichia.quipapi.QuipThread.Format;
import kenichia.quipapi.QuipThread.Type;

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
