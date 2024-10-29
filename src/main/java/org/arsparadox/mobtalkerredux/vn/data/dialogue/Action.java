package org.arsparadox.mobtalkerredux.vn.data.dialogue;

import java.util.List;

public sealed interface Action {
    record ShowSpriteAction(String sprite) implements Action {}
    record DialogueAction(String label, String content) implements Action {}
    record ModifyVariableAction(
            String variable,
            String operation,
            Object value
    ) implements Action {}
    record TransitionAction(String action, String label) implements Action {}
    record GiveItemAction(String item, int amount) implements Action {}
    record FinishDialogueAction() implements Action {}
    record ConditionalAction(
            String condition,
            String variable,
            Object value,
            List<Action> actions
    ) implements Action {}
}
