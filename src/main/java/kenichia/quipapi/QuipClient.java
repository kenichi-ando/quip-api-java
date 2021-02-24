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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;

public class QuipClient extends QuipAccess {

  private static QuipClient _instance = new QuipClient();
  private String _accessToken;
  private boolean _isDebugEnabled = false;

  // ============================================
  // Authentication
  // ============================================

  public static void setAccessToken(String accessToken) throws Exception {
    Objects.requireNonNull(accessToken);
    _instance._accessToken = accessToken;
    if (!_verifyToken()) throw new IOException("The access token is invalid.");
  }

  public static String getAuthorizationUrl(
      String clientId, String clientSecret, String redirectUri, String state) throws Exception {
    Objects.requireNonNull(clientId);
    Objects.requireNonNull(clientSecret);
    Objects.requireNonNull(redirectUri);
    List<NameValuePair> params = new ArrayList<>();
    params.add(new BasicNameValuePair("client_id", clientId));
    params.add(new BasicNameValuePair("client_secret", clientSecret));
    params.add(new BasicNameValuePair("redirect_uri", redirectUri));
    if (state != null) params.add(new BasicNameValuePair("state", state));
    return new URIBuilder(QuipAccess.ENDPOINT + "/oauth/login")
        .addParameters(params)
        .build()
        .toString();
  }

  public static QuipToken generateToken(
      String clientId, String clientSecret, String redirectUri, String authorizationCode)
      throws Exception {
    Objects.requireNonNull(clientId);
    Objects.requireNonNull(clientSecret);
    Objects.requireNonNull(redirectUri);
    Objects.requireNonNull(authorizationCode);
    List<NameValuePair> params = new ArrayList<>();
    params.add(new BasicNameValuePair("grant_type", "authorization_code"));
    params.add(new BasicNameValuePair("client_id", clientId));
    params.add(new BasicNameValuePair("client_secret", clientSecret));
    params.add(new BasicNameValuePair("redirect_uri", redirectUri));
    params.add(new BasicNameValuePair("code", authorizationCode));
    return new QuipToken(
        _postToJsonObject(
            new URIBuilder(QuipAccess.ENDPOINT + "/oauth/access_token")
                .addParameters(params)
                .build()));
  }

  public static QuipToken refreshToken(String clientId, String clientSecret, String refreshToken)
      throws Exception {
    Objects.requireNonNull(clientId);
    Objects.requireNonNull(clientSecret);
    Objects.requireNonNull(refreshToken);
    List<NameValuePair> params = new ArrayList<>();
    params.add(new BasicNameValuePair("grant_type", "refresh_token"));
    params.add(new BasicNameValuePair("client_id", clientId));
    params.add(new BasicNameValuePair("client_secret", clientSecret));
    params.add(new BasicNameValuePair("refresh_token", refreshToken));
    return new QuipToken(
        _postToJsonObject(
            new URIBuilder(QuipAccess.ENDPOINT + "/oauth/access_token")
                .addParameters(params)
                .build()));
  }

  public static void revokeToken(String clientId, String clientSecret) throws Exception {
    Objects.requireNonNull(clientId);
    Objects.requireNonNull(clientSecret);
    List<NameValuePair> params = new ArrayList<>();
    params.add(new BasicNameValuePair("client_id", clientId));
    params.add(new BasicNameValuePair("client_secret", clientSecret));
    params.add(new BasicNameValuePair("token", _instance._accessToken));
    _postToJsonObject(
        new URIBuilder(QuipAccess.ENDPOINT + "/oauth/revoke").addParameters(params).build());
  }

  public static void enableDebug(boolean isEnabled) {
    _instance._isDebugEnabled = isEnabled;
  }

  // ============================================
  // Protected
  // ============================================

  protected static String _getBearerToken() {
    return "Bearer " + _instance._accessToken;
  }

  protected static boolean _isDebugEnabled() {
    return _instance._isDebugEnabled;
  }

  // ============================================
  // Private
  // ============================================

  private static boolean _verifyToken() throws IOException {
    return (_getToStatusCode(QuipAccess.ENDPOINT + "/oauth/verify_token") == 200);
  }
}