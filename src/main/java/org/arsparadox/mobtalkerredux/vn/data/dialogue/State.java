package org.arsparadox.mobtalkerredux.vn.data.dialogue;

import java.util.List;
import java.util.Map;

public record State(
        List<Action> actions,
        Map<String, String> choices
) {}