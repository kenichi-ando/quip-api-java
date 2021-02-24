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

import kenichia.quipapi.QuipClient;
import kenichia.quipapi.QuipTable;
import kenichia.quipapi.QuipThread;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class QuipTableTest {
  @BeforeAll
  static void init() throws Exception {
    QuipClient.enableDebug(true);
    QuipClient.setAccessToken(System.getenv("QUIP_ACCESS_TOKEN"));
  }

  @Test
  void createMultipleTableInDocument() throws Exception {
    QuipThread doc =
        QuipThread.createDocument(
            "複数埋め込みテーブルテスト",
            QuipTable.createTableHtml(2, 3),
            null,
            QuipThread.Format.HTML,
            QuipThread.Type.DOCUMENT);
    doc.editDocument(
        QuipTable.createTableHtml(1, 2), QuipThread.Format.HTML, QuipThread.Location.APPEND, null);
    String[] tableIds = doc.getTableIds();
    assertEquals(2, tableIds.length);
    QuipTable table1 = doc.getTableById(tableIds[0]);
    assertEquals(2, table1.getColumnSize());
    assertEquals(3, table1.getRowSize());
    table1.updateCellValue(0, 0, "🐤");
    table1.updateCellValue(1, 2, "🐔");
    assertEquals("🐤", table1.getCellValue(0, 0));
    assertEquals("🐔", table1.getCellValue(1, 2));

    QuipTable table2 = doc.getTableById(tableIds[1]);
    assertEquals(1, table2.getColumnSize());
    assertEquals(2, table2.getRowSize());
    table2.updateCellValue(0, 0, "🐤");
    table2.updateCellValue(0, 1, "🐔");
    table2.addRow(1, new String[] {"🐣"});
    assertEquals("🐤", table2.getCellValue(0, 0));
    assertEquals("🐣", table2.getCellValue(0, 1));
    assertEquals("🐔", table2.getCellValue(0, 2));
  }

  @Test
  void createTableInDocument() throws Exception {
    String html =
        QuipTable.createTableHtml(
            new String[] {"列A🚀", "列B💫", "列C🌕", "列D🌛"},
            new String[][] {
              new String[] {"セルA1", "セルB1", "セルC1", "セルD1"},
              new String[] {"セルA2", "セルB2", "セルC2", "セルD2"},
              new String[] {"セルA3", "セルB3", "セルC3", "セルD3"}
            });
    QuipThread doc =
        QuipThread.createDocument(
            "埋め込みテーブルテスト", html, null, QuipThread.Format.HTML, QuipThread.Type.DOCUMENT);
    String[] tableIds = doc.getTableIds();
    assertEquals(1, tableIds.length);
    QuipTable table = doc.getTableById(tableIds[0]);
    assertEquals("セルB2", table.getCellValue(1, 1));
    assertEquals("セルA3", table.getCellValue(0, 2));
    assertEquals("列B💫", table.getColumnHeader(1));
  }

  @Test
  void createSpreadsheet() throws Exception {
    String html =
        QuipTable.createTableHtml(
            new String[] {"列A🚀", "列B💫", "列C🌕", "列D🌛"},
            new String[][] {
              new String[] {"セルA1", "セルB1", "セルC1", "セルD1"},
              new String[] {"セルA2", "セルB2", "セルC2", "セルD2"},
              new String[] {"セルA3", "セルB3", "セルC3", "セルD3"}
            });
    QuipThread sheet =
        QuipThread.createDocument(
            "スプレッドシートテスト", html, null, QuipThread.Format.HTML, QuipThread.Type.SPREADSHEET);
    String[] tableIds = sheet.getTableIds();
    assertEquals(1, tableIds.length);
    QuipTable table = sheet.getTableById(tableIds[0]);
    assertEquals("セルB2", table.getCellValue(1, 1));
    assertEquals("セルA3", table.getCellValue(0, 2));
    assertEquals("列B💫", table.getColumnHeader(1));

    table.updateCellValue(1, 2, "更新🌋");
    assertEquals("更新🌋", table.getCellValue(1, 2));

    table.addRow(new String[] {"あ🚀", "い💫", "う🌕", "え🌛"});
    table.addRow();
    assertEquals(5, table.getRowSize());
    table.removeRow(1);
    assertEquals(4, table.getRowSize());
    table.addRow(0, new String[] {"あ🚀", "い💫", "う🌕", "え🌛"});
    assertEquals(5, table.getRowSize());
  }
}