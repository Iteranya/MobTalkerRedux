package org.arsparadox.mobtalkerredux.vn.data.dialogue;

import java.util.List;

public record Dialogue(
	String action,
	int id,
	String type,
	String label,
	String content,
	String sprite,
	int amount,
	String itemId,
	String condition,
	String var,
	int end,
	Object value,
	List<ActionsItem> actions,
	List<ChoiceItem> choice,
	int init
) {
}
