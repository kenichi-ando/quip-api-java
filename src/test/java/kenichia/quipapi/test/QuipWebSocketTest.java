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

import kenichia.quipapi.QuipClient;
import kenichia.quipapi.QuipMessage;
import kenichia.quipapi.QuipThread;
import kenichia.quipapi.QuipUser;
import kenichia.quipapi.QuipWebSocket;
import kenichia.quipapi.QuipWebSocketEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class QuipWebSocketTest implements QuipWebSocketEvent {

  private int aliveCounter = 0;

  @BeforeAll
  static void init() throws Exception {
    QuipClient.enableDebug(true);
    QuipClient.setAccessToken(System.getenv("QUIP_ACCESS_TOKEN"));
  }

  @Test
  void createWebSocket() throws Exception {
    QuipWebSocket qws = QuipWebSocket.create();
    assertNotNull(qws);
    assertFalse(qws.getUserId().isEmpty());
    assertFalse(qws.getUrl().isEmpty());

    qws.open(this);
    qws.checkAlive();
    qws.checkAlive();
    qws.checkAlive();
    Thread.sleep(5000);
    assertEquals(3, aliveCounter);
    qws.close();
  }

  @Override
  public void onMessage(QuipMessage message, QuipUser user, QuipThread thread) {
    System.out.println("onMessage: " + message.getText() + " by " + message.getAuthorName());
  }

  @Override
  public void onAlive(String message) {
    System.out.println("onAlive: " + message);
    aliveCounter++;
  }

  @Override
  public void onError(String debug) {
    System.out.println("onError: " + debug);
  }
}