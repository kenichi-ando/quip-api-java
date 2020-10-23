package kenichia.quipapi;

import java.util.stream.Stream;

import com.google.gson.JsonObject;

public class QuipDiff extends QuipJsonObject {

	// ============================================
	// Enum
	// ============================================

	public enum DiffClass {
		TRACK_CHANGES("track_changes"),
		DELETE_ONLY("delete_only"),
		INSERT_ONLY("insert_only"),
		INSERT_COMPLETELY("insert_completely");

		private final String _value;

		private DiffClass(String value) {
			_value = value;
		}

		private static DiffClass find(String value) {
			return Stream.of(values()).filter(e -> e._value.equals(value)).findFirst().orElse(null);
		}
	}

	// ============================================
	// Constructor
	// ============================================

	protected QuipDiff(JsonObject json) {
		super(json);
	}

	// ============================================
	// Properties
	// ============================================

	public String getRtml() {
		return _getString("rtml");
	}

	public String getList() {
		return _getString("list");
	}

	public DiffClass getDiffClass() {
		return DiffClass.find(_getString("diff_class"));
	}

	public String getSectionId() {
		return _getString("section_id");
	}

	public String getStyle() {
		return _getString("style");
	}
}