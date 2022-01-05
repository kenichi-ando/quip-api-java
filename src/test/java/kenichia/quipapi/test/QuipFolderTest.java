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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import kenichia.quipapi.QuipClient;
import kenichia.quipapi.QuipFolder;
import kenichia.quipapi.QuipFolder.Color;

public class QuipFolderTest {
    @BeforeAll
    static void init() throws Exception {
        QuipClient.enableDebug(true);
        QuipClient.setAccessToken(System.getenv("QUIP_ACCESS_TOKEN"));
    }

    @Test
    void getFolder() throws Exception {
        QuipFolder folder = QuipFolder.create("フォルダー１🌈", Color.LIGHT_PURPLE,
                null, null);
        QuipFolder folder1 = QuipFolder.getFolder(folder.getId());
        assertEquals(folder.getId(), folder1.getId());
    }

    @Test
    void getFolders() throws Exception {
        QuipFolder folder1 = QuipFolder.create("フォルダー１🌈", Color.LIGHT_PURPLE,
                null, null);
        QuipFolder[] folders = QuipFolder.getFolders(
                new String[]{folder1.getId(), folder1.getId()}, false);
        assertEquals(folder1.getId(), folders[0].getId());
    }

    @Test
    void createFolder() throws Exception {
        QuipFolder folder = QuipFolder.create("フォルダー１🌈", Color.LIGHT_PURPLE,
                null, null);
        assertNotNull(folder);
        assertFalse(folder.getId().isEmpty());
        assertEquals("フォルダー１🌈", folder.getTitle());
        assertEquals(Color.LIGHT_PURPLE, folder.getColor());
        assertFalse(folder.getParentId().isEmpty());
        assertFalse(folder.getCreatorId().isEmpty());
        assertNotNull(folder.getCreatedUsec());
        assertNotNull(folder.getUpdatedUsec());
        assertEquals(0, folder.getMemberIds().length);
        assertEquals(0, folder.getChildren().length);
    }

    @Test
    void createSubFolder() throws Exception {
        QuipFolder parent = QuipFolder.create("親フォルダー🌈", Color.LIGHT_PURPLE,
                null, null);
        QuipFolder child = QuipFolder.create("子フォルダー🌈", Color.LIGHT_GREEN,
                parent.getId(), null);
        assertEquals("子フォルダー🌈", child.getTitle());
        assertEquals(parent.getId(), child.getParentId());

        parent.reload();
        QuipFolder.Node node = parent.getChildren()[0];
        assertTrue(node.isFolder());
        assertEquals(child.getId(), node.getId());
    }
}