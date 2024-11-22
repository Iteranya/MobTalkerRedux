package org.arsparadox.mobtalkerredux.command;

import org.arsparadox.mobtalkerredux.vn.view.DialogueScreen;

public class MobFreezer {
    public static void freezeMob(DialogueScreen ds) {
        ds.mob.setNoAi(true);
        ds.mob.setNoGravity(true);
    }

    public static void unfreezeMob(DialogueScreen ds) {
        ds.mob.setNoAi(false);
        ds.mob.setNoGravity(false);
    }
}