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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import kenichia.quipapi.QuipBlob;
import kenichia.quipapi.QuipClient;
import kenichia.quipapi.QuipDiff;
import kenichia.quipapi.QuipDiffGroup;
import kenichia.quipapi.QuipMessage;
import kenichia.quipapi.QuipThread;
import kenichia.quipapi.QuipThread.Format;
import kenichia.quipapi.QuipThread.Frame;
import kenichia.quipapi.QuipThread.Location;
import kenichia.quipapi.QuipThread.MessageType;
import kenichia.quipapi.QuipThread.Type;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class QuipMessageTest {
  @BeforeAll
  static void init() throws Exception {
    QuipClient.enableDebug(true);
    QuipClient.setAccessToken(System.getenv("QUIP_ACCESS_TOKEN"));
  }

  @Test
  void getRecentMessages() throws Exception {
    QuipThread doc =
        QuipThread.createDocument("ドキュメント１🌈", "あいうえお🔥", null, Format.HTML, Type.DOCUMENT);
    doc.addMessage(Frame.BUBBLE, "コメント１🔥", null, false, null, null, null);
    doc.addMessage(Frame.LINE, "コメント２🔥", null, false, null, null, null);
    doc.addMessage(Frame.CARD, "コメント３🔥", null, false, null, null, null);

    QuipMessage[] msgs = doc.getRecentMessages(null, null, MessageType.MESSAGE);
    assertEquals(3, msgs.length);
    assertEquals("コメント３🔥", msgs[0].getText());
    assertEquals("コメント２🔥", msgs[1].getText());
    assertEquals("コメント１🔥", msgs[2].getText());
    doc.delete();
  }

  @Test
  void getRecentMessagesWithCount() throws Exception {
    QuipThread doc =
        QuipThread.createDocument("ドキュメント１🌈", "あいうえお🔥", null, Format.HTML, Type.DOCUMENT);
    doc.addMessage(Frame.BUBBLE, "コメント１🔥", null, false, null, null, null);
    doc.addMessage(Frame.LINE, "コメント２🔥", null, false, null, null, null);
    doc.addMessage(Frame.CARD, "コメント３🔥", null, false, null, null, null);

    QuipMessage[] msgs = doc.getRecentMessages(2, null, MessageType.MESSAGE);
    assertEquals(2, msgs.length);
    assertEquals("コメント３🔥", msgs[0].getText());
    assertEquals("コメント２🔥", msgs[1].getText());
    doc.delete();
  }

  @Test
  void getRecentEdits() throws Exception {
    QuipThread doc =
        QuipThread.createDocument("テスト編集履歴🐯", "# タイトル１🐯", null, Format.MARKDOWN, Type.DOCUMENT);
    doc.editDocument("アペンド１🐯", Format.HTML, Location.APPEND, null);
    doc.editDocument("アペンド２🐯", Format.HTML, Location.APPEND, null);
    doc.editDocument("アペンド３🐯", Format.HTML, Location.APPEND, null);
    QuipMessage[] edits = doc.getRecentMessages(2, null, MessageType.EDIT);
    assertEquals(1, edits.length);
    QuipDiffGroup[] diffGroups = edits[0].getDiffGroups();
    assertEquals(1, diffGroups.length);
    QuipDiff[] diffs = diffGroups[0].getDiffs();
    assertEquals(3, diffs.length);

    assertTrue(diffs[0].getRtml().contains("アペンド１🐯"));
    assertEquals(QuipDiff.DiffClass.INSERT_COMPLETELY, diffs[0].getDiffClass());
    assertNotNull(diffs[0].getSectionId());
    assertEquals("text_plain_style", diffs[0].getStyle());

    assertTrue(diffs[1].getRtml().contains("アペンド２🐯"));
    assertEquals(QuipDiff.DiffClass.INSERT_COMPLETELY, diffs[1].getDiffClass());
    assertNotNull(diffs[1].getSectionId());
    assertEquals("text_plain_style", diffs[1].getStyle());

    assertTrue(diffs[2].getRtml().contains("アペンド３🐯"));
    assertEquals(QuipDiff.DiffClass.INSERT_COMPLETELY, diffs[2].getDiffClass());
    assertNotNull(diffs[2].getSectionId());
    assertEquals("text_plain_style", diffs[2].getStyle());
    doc.delete();
  }

  @Test
  void addMessage() throws Exception {
    QuipThread doc =
        QuipThread.createDocument("ドキュメント１🌈", "あいうえお🔥", null, Format.HTML, Type.DOCUMENT);
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
    QuipThread doc =
        QuipThread.createDocument(
            "テスト🐷",
            "# タイトル１🐷\n## セクション１🐷\n## セクション２🐷\n## セクション３🐷",
            null,
            Format.MARKDOWN,
            Type.DOCUMENT);
    String html = doc.getHtml();
    List<String> sectionIds = new ArrayList<>();
    int index = 0;
    while (true) {
      index = html.indexOf("id='", index);
      if (index == -1) break;
      index += 4;
      String sectionId = html.substring(index, html.indexOf("'", index));
      sectionIds.add(sectionId);
    }
    QuipMessage msg1 =
        doc.addMessage(Frame.BUBBLE, "バブル🐷", null, true, null, null, sectionIds.get(1));
    QuipMessage msg2 =
        doc.addMessage(Frame.LINE, "ライン🐷", null, true, null, null, sectionIds.get(1));
    QuipMessage msg3 =
        doc.addMessage(Frame.CARD, "カード🐷", null, true, null, null, sectionIds.get(1));
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
    QuipThread doc =
        QuipThread.createDocument("テスト🐷", "# タイトル１🐷", null, Format.MARKDOWN, Type.DOCUMENT);
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
  void addMessageWithAttachment() throws Exception {
    QuipThread chat = QuipThread.createChat("チャットルーム１🌈", "メッセージ１🔥", null);
    QuipBlob blob = chat.addBlob(new File("/tmp/image.png"));
    QuipMessage msg =
        chat.addMessage(Frame.BUBBLE, "添付🔥", null, false, new String[] {blob.getId()}, null, null);
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