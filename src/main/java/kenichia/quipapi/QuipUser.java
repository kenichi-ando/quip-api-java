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

import java.time.Instant;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.http.client.fluent.Form;
import org.apache.http.client.utils.URIBuilder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class QuipUser extends QuipJsonObject {

	// ============================================
	// Constructor
	// ============================================

	protected QuipUser(JsonObject json) {
		super(json);
	}
	
	// ============================================
	// Properties
	// ============================================

	public String getId() {
		return _getString("id");
	}

	public String getName() {
		return _getString("name");
	}

	public double getAffinity() {
		return _getDouble("affinity");
	}

	public String getProfilePictureUrl() {
		return _getString("profile_picture_url");
	}

	public String getChatThreadId() {
		return _getString("chat_thread_id");
	}

	public String getDesktopFolderId() {
		return _getString("desktop_folder_id");
	}

	public String getArchiveFolderId() {
		return _getString("archive_folder_id");
	}

	public String getStarredFolderId() {
		return _getString("starred_folder_id");
	}

	public String getPrivateFolderId() {
		return _getString("private_folder_id");
	}

	public String getTrashFolderId() {
		return _getString("trash_folder_id");
	}

	public String[] getGroupFolderIds() {
		return _getStringArray("group_folder_ids");
	}

	public String[] getSharedFolderIds() {
		return _getStringArray("shared_folder_ids");
	}

	public boolean isDisabled() {
		return _getBoolean("disabled");
	}

	public Instant getCreatedUsec() {
		return _getInstant("created_usec");
	}

	public String[] getEmails() {
		return _getStringArray("emails");
	}

	public String getSubDomain() {
		return _getString("subdomain");
	}

	public String getUrl() {
		return _getString("url");
	}

	public boolean isRobot() {
		return _getBoolean("is_robot");
	}

	// ============================================
	// Read
	// ============================================

	public static QuipUser getCurrentUser() throws Exception {
		return new QuipUser(_getToJsonObject(QuipAccess.ENDPOINT + "/users/current"));
	}

	public static QuipUser getUser(String userIdOrEmail) throws Exception {
		return new QuipUser(_getToJsonObject(QuipAccess.ENDPOINT + "/users/" + userIdOrEmail));
	}

	public static QuipUser[] getUsers(String[] userIdOrEmails) throws Exception {
		JsonObject json = _getToJsonObject(new URIBuilder(QuipAccess.ENDPOINT + "/users/")
				.addParameter("ids", Stream.of(userIdOrEmails).collect(Collectors.joining(","))).build());
		return json.keySet().stream()
				.map(id -> new QuipUser(json.get(id).getAsJsonObject()))
				.toArray(QuipUser[]::new);
	}

	public static QuipUser[] getContacts() throws Exception {
		JsonArray json = _getToJsonArray(QuipAccess.ENDPOINT + "/users/contacts");
		return StreamSupport.stream(json.spliterator(), false)
				.map(obj -> new QuipUser(obj.getAsJsonObject()))
				.toArray(QuipUser[]::new);
	}

	public boolean reload() throws Exception {
		JsonObject object = _getToJsonObject(QuipAccess.ENDPOINT + "/users/" + getId());
		if (object == null)
			return false;
		_replace(object);
		return true;
	}

	// ============================================
	// Update
	// ============================================
	
	public boolean update(String profilePictureUrl) throws Exception {
		Form form = Form.form()
				.add("user_id", getId())
				.add("profile_picture_url", profilePictureUrl);
		JsonObject object = _postToJsonObject(QuipAccess.ENDPOINT + "/users/update", form);
		if (object == null)
			return false;
		_replace(object);
		return true;
	}
}
