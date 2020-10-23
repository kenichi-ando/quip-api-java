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
