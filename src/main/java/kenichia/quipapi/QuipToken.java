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

import com.google.gson.JsonObject;

public class QuipToken extends QuipJsonObject {

  // ============================================
  // Constructor
  // ============================================

  protected QuipToken(JsonObject json) {
    super(json);
  }

  // ============================================
  // Properties
  // ============================================

  public String getAccessToken() {
    return _getString("access_token");
  }

  public String getRefreshToken() {
    return _getString("refresh_token");
  }

  public int getExpiresIn() {
    return _getInt("expires_in");
  }

  public String getScope() {
    return _getString("scope");
  }

  public String getTokenType() {
    return _getString("token_type");
  }
}