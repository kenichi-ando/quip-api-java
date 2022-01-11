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

public class QuipBlob extends QuipJsonObject {

    // ============================================
    // Constructor
    // ============================================

    protected QuipBlob(JsonObject json) {
        super(json);
    }

    /**
     * Method to get images and attachments from a thread or thread messages.
     *
     * @param threadId - thread id.
     * @param blobId   - id of the blob to be fetched.
     * @return - byte[] of the blob
     * @throws Exception - if the blob not exit on the thread or if the
     *                   thread id is invalid.
     */
    public static byte[] getBlob(String threadId, String blobId) throws Exception {
        return _getToByteArray(
                QuipAccess.ENDPOINT + "/blob/" + threadId + "/" + blobId);
    }

    // ============================================
    // Properties
    // ============================================

    public String getId() {
        return _getString("id");
    }

    public String getUrl() {
        return _getString("url");
    }
}