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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import kenichia.quipapi.QuipClient;
import kenichia.quipapi.QuipFolder;
import kenichia.quipapi.QuipUser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class QuipUserTest {
  @BeforeAll
  static void init() throws Exception {
    QuipClient.enableDebug(true);
    QuipClient.setAccessToken(System.getenv("QUIP_ACCESS_TOKEN"));
  }

  @Test
  void getCurrentUser() throws Exception {
    QuipUser user = QuipUser.getCurrentUser();
    assertNotNull(user);

    assertTrue(user.getId().length() > 0);
    assertTrue(user.getName().length() > 0);
    assertTrue(user.getEmails()[0].length() > 0);
    assertFalse(user.isDisabled());
    assertFalse(user.isRobot());
    assertNotNull(user.getCreatedUsec());
    assertTrue(user.getProfilePictureUrl().length() > 0);
    assertTrue(user.getAffinity() >= 0);
    assertTrue(user.getArchiveFolderId().length() > 0);
    assertTrue(user.getPrivateFolderId().length() > 0);
    assertTrue(user.getStarredFolderId().length() > 0);
    assertTrue(user.getDesktopFolderId().length() > 0);
    assertTrue(user.getTrashFolderId().length() > 0);
    assertTrue(user.getGroupFolderIds()[0].length() > 0);
    assertTrue(user.getUrl().length() > 0);
    assertTrue(user.getSubDomain().length() > 0);

    System.out.println(user.getChatThreadId());
    System.out.println(Arrays.toString(user.getSharedFolderIds()));
    for (String fid : user.getSharedFolderIds()) {
      QuipFolder f = QuipFolder.getFolder(fid);
      System.out.println(f.getTitle());
    }
  }

  @Test
  void getUser() throws Exception {
    QuipUser user = QuipUser.getCurrentUser();
    QuipUser user1 = QuipUser.getUser(user.getId());
    assertEquals(user.getId(), user1.getId());
  }

  @Test
  void getUsers() throws Exception {
    QuipUser user = QuipUser.getCurrentUser();
    QuipUser[] users = QuipUser.getUsers(new String[] {user.getId(), user.getId()});
    assertEquals(user.getId(), users[0].getId());
  }
}