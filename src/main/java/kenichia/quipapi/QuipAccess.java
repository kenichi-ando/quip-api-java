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

import java.io.IOException;
import java.net.URI;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.apache.http.Consts;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.util.EntityUtils;

class QuipAccess {

  public static String ENDPOINT = "https://platform.quip.com/1";

  // ============================================
  // Protected
  // ============================================

  protected static JsonObject _getToJsonObject(String uri) throws IOException {
    return _toJsonObject(_requestGet(uri));
  }

  protected static JsonObject _getToJsonObject(URI uri) throws IOException {
    return _toJsonObject(_requestGet(uri));
  }

  protected static JsonArray _getToJsonArray(String uri) throws IOException {
    return _toJsonArray(_requestGet(uri));
  }

  protected static JsonArray _getToJsonArray(URI uri) throws IOException {
    return _toJsonArray(_requestGet(uri));
  }

  protected static byte[] _getToByteArray(String uri) throws IOException {
    HttpResponse res = _requestGet(uri);
    if (res.getStatusLine().getStatusCode() == 200) {
      byte[] buff = new byte[(int) res.getEntity().getContentLength()];
      res.getEntity().getContent().read(buff);
      return buff;
    } else {
      _checkError(_toJsonObject(res));
      return null;
    }
  }

  protected static int _getToStatusCode(String uri) throws IOException {
    return _requestGet(uri).getStatusLine().getStatusCode();
  }

  protected static String _getToString(URI uri) throws IOException {
    return _toString(_requestGet(uri));
  }

  protected static JsonObject _postToJsonObject(String uri, Form form)
      throws IOException {
    return _toJsonObject(_requestPost(uri, form));
  }

  protected static JsonObject _postToJsonObject(String uri,
      MultipartEntityBuilder multi) throws IOException {
    return _toJsonObject(_requestPost(uri, multi));
  }

  protected static JsonObject _postToJsonObject(URI uri) throws IOException {
    return _toJsonObject(_requestPost(uri));
  }

  protected static JsonArray _postToJsonArray(String uri, Form form)
      throws IOException {
    return _toJsonArray(_requestPost(uri, form));
  }

  // ============================================
  // Private
  // ============================================

  private static HttpResponse _requestGet(String uri) throws IOException {
    return _sendRequest(Request.Get(uri));
  }

  private static HttpResponse _requestGet(URI uri) throws IOException {
    return _sendRequest(Request.Get(uri));
  }

  private static HttpResponse _requestPost(String uri, Form form)
      throws IOException {
    return _sendRequest(Request.Post(uri)
        .body(new UrlEncodedFormEntity(form.build(), Consts.UTF_8)));
  }

  private static HttpResponse _requestPost(String uri,
      MultipartEntityBuilder multi) throws IOException {
    return _sendRequest(Request.Post(uri).body(multi.build()));
  }

  private static HttpResponse _requestPost(URI uri) throws IOException {
    return _sendRequest(Request.Post(uri));
  }

  private static HttpResponse _sendRequest(Request req) throws IOException {
    if (QuipClient._isDebugEnabled())
      System.out.println(System.lineSeparator() + "Request> " + req.toString());
    String token = QuipClient._getBearerToken();
    if (token != null)
      req.addHeader(HttpHeaders.AUTHORIZATION, token);
    HttpResponse response = req.execute().returnResponse();
    if (QuipClient._isDebugEnabled())
      System.out.println("Response> " + response.getStatusLine().toString()
          + " " + response.getEntity().toString());
    return response;
  }

  private static String _toString(HttpResponse response) throws IOException {
    return EntityUtils.toString(response.getEntity());
  }

  private static JsonObject _toJsonObject(HttpResponse response)
      throws IOException {
    JsonObject json = new Gson().fromJson(_toString(response),
        JsonObject.class);
    if (QuipClient._isDebugEnabled())
      System.out.println("Json> " + json.toString());
    if (_checkError(json))
      return null;
    return json;
  }

  private static JsonArray _toJsonArray(HttpResponse response)
      throws IOException {
    JsonArray json = new Gson().fromJson(_toString(response), JsonArray.class);
    if (QuipClient._isDebugEnabled())
      System.out.println("Json> " + json.toString());
    return json;
  }

  private static boolean _checkError(JsonObject json) {
    if (json.get("error") == null)
      return false;
    String errorCode = (json.get("error_code") != null)
        ? json.get("error_code").getAsString()
        : "";
    String error = (json.get("error") != null)
        ? json.get("error").getAsString()
        : "";
    String errorDescription = (json.get("error_description") != null)
        ? json.get("error_description").getAsString()
        : "";
    System.out.println(
        "Error> " + errorCode + " " + error + " (" + errorDescription + ")");
    return true;
  }
}