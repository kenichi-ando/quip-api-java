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

import java.util.stream.StreamSupport;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class QuipDiffGroup extends QuipJsonObject {

	// ============================================
	// Constructor
	// ============================================

	protected QuipDiffGroup(JsonObject json) {
		super(json);
	}

	// ============================================
	// Properties
	// ============================================

	public QuipDiff[] getDiffs() {
		JsonArray arr = _getJsonArray("diffs");
		if (arr == null)
			return null;
		QuipDiff[] diffs = StreamSupport.stream(arr.spliterator(), false)
				.map(obj -> new QuipDiff(obj.getAsJsonObject()))
				.toArray(QuipDiff[]::new);
		return diffs;
	}
}
