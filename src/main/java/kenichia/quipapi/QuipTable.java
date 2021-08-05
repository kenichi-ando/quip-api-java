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
package kenichia.quipapi;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class QuipTable {

  private final QuipThread _parentThread;
  private final String _id;
  private QuipColumn[] _columns;
  private QuipRow[] _rows;

  QuipTable(QuipThread parentThread, Element element) {
    _parentThread = parentThread;
    _id = element.attr("id");
    _construct(this, element);
  }

  public int getColumnSize() {
    return _columns.length;
  }

  public int getRowSize() {
    return _rows.length;
  }

  public void refresh() {
    Document document = Jsoup.parse(_parentThread.getHtml());
    Elements elements = document.getElementsByAttributeValue("id", _id);
    if (elements.size() > 0) {
      _construct(this, elements.first());
    }
  }

  public String getCellValue(int column, int row) {
    QuipCell cell = _getCell(column, row);
    if (cell == null) return null;
    return _rows[row].getCell(column).getValue();
  }

  public String getColumnHeader(int column) {
    if (column < 0 || column >= _columns.length) return null;
    return _columns[column].getHeader();
  }

  public boolean updateCellValue(int column, int row, String value) throws Exception {
    QuipCell cell = _getCell(column, row);
    if (cell == null) return false;
    if (!_parentThread.editDocument(
        value, QuipThread.Format.HTML, QuipThread.Location.REPLACE_SECTION, cell.getId()))
      return false;
    refresh();
    return true;
  }

  public boolean addRow() throws Exception {
    return addRow(new String[_columns.length]);
  }

  public boolean addRow(String[] values) throws Exception {
    if (_rows.length == 0) return false;
    return _addRow(values, _rows[_rows.length - 1].getId(), QuipThread.Location.AFTER_SECTION);
  }

  public boolean addRow(int row) throws Exception {
    return addRow(row, new String[_columns.length]);
  }

  public boolean addRow(int row, String[] values) throws Exception {
    if (row < 0 || row >= _rows.length) return false;
    return _addRow(values, _rows[row].getId(), QuipThread.Location.BEFORE_SECTION);
  }

  private boolean _addRow(String[] values, String id, QuipThread.Location loc) throws Exception {
    if (!_parentThread.editDocument(_makeRowHtml(values), QuipThread.Format.HTML, loc, id))
      return false;
    refresh();
    return true;
  }

  public boolean removeRow(int row) throws Exception {
    if (row < 0 || row >= _rows.length || _rows.length <= 1) return false;
    if (!_parentThread.editDocument(
        null, QuipThread.Format.HTML, QuipThread.Location.DELETE_SECTION, _rows[row].getId()))
      return false;
    refresh();
    return true;
  }

  public static String createTableHtml(int columnSize, int rowSize) {
    return createTableHtml(new String[columnSize], new String[rowSize][columnSize]);
  }

  public static String createTableHtml(String[] columnHeaders, String[][] cellValues) {
    StringBuffer sb = new StringBuffer("<table>");
    sb.append("<thead>");
    sb.append("<tr>");
    for (String header : columnHeaders) {
      sb.append("<th>");
      sb.append((header == null) ? "" : header);
      sb.append("</th>");
    }
    sb.append("</tr>");
    sb.append("</thead>");
    sb.append("<tbody>");
    for (String[] rowValues : cellValues) {
      sb.append(_makeRowHtml(rowValues));
    }
    sb.append("</tbody>");
    return sb.toString();
  }

  private static String _makeRowHtml(String[] values) {
    StringBuffer sb = new StringBuffer("<tr>");
    for (String v : values) {
      sb.append("<td>");
      sb.append((v == null) ? "" : v);
      sb.append("</td>");
    }
    sb.append("</tr>");
    return sb.toString();
  }

  private QuipCell _getCell(int column, int row) {
    if (column < 0 || column >= _columns.length || row < 0 || row >= _rows.length) return null;
    return _rows[row].getCell(column);
  }

  private void _construct(QuipTable table, Element element) {
    Elements rows = element.getElementsByTag("tbody").first().getElementsByTag("tr");
    table._rows = new QuipRow[rows.size()];

    Element header = element.getElementsByTag("thead").first();
    if (header != null) {
        Elements cols = header.getElementsByTag("th");
        table._columns =
            cols.stream()
                .filter(e -> e.hasAttr("id"))
                .map(col -> new QuipColumn(col.attr("id"), col.text()))
                .toArray(QuipColumn[]::new);
    } else {
        table._columns =
                rows.get(0).getElementsByTag("td").stream()
                    .filter(e -> e.hasAttr("id"))
                    .map(col -> new QuipColumn(null, null))
                    .toArray(QuipColumn[]::new);
    }

    for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
      Element row = rows.get(rowIndex);
      QuipCell[] cells =
          row.getElementsByTag("td").stream()
              .filter(e -> e.hasAttr("id"))
              .map(e -> new QuipCell(e.attr("id"), e.text()))
              .toArray(QuipCell[]::new);
      table._rows[rowIndex] = new QuipRow(row.attr("id"), cells);
    }
  }
}

class QuipColumn {
  private final String _id;
  private final String _header;

  QuipColumn(String id, String header) {
    _id = id;
    _header = header;
  }

  String getId() {
    return _id;
  }

  String getHeader() {
    return _header;
  }
}

class QuipRow {
  private final String _id;
  private final QuipCell[] _cells;

  QuipRow(String id, QuipCell[] cells) {
    _id = id;
    _cells = cells;
  }

  String getId() {
    return _id;
  }

  QuipCell getCell(int column) {
    if (column < 0 || column >= _cells.length) return null;
    return _cells[column];
  }
}

class QuipCell {
  private final String _id;
  private final String _value;

  QuipCell(String id, String value) {
    _id = id;
    _value = value;
  }

  String getId() {
    return _id;
  }

  String getValue() {
    return _value;
  }
}