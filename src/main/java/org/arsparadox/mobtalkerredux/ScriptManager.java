package org.arsparadox.mobtalkerredux;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.arsparadox.mobtalkerredux.MobTalkerRedux.LOGGER;

public class ScriptManager {
    private Globals globals;

    public ScriptManager() {
        this.globals = JsePlatform.standardGlobals();
    }

    public LuaTable loadDialogueScript(ResourceLocation resourcePath) {
        try {
            LOGGER.info("Attempting to load dialogue script from: {}", resourcePath);

            InputStream inputStream = Minecraft.getInstance()
                    .getResourceManager()
                    .getResource(resourcePath)
                    .getInputStream();

            LOGGER.info("Successfully opened input stream");

            // Let's first read the content to verify what we're loading
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            reader.close();

            LOGGER.info("Script content:\n{}", content.toString());

            // Reset the stream for Lua parsing
            inputStream = new ByteArrayInputStream(content.toString().getBytes());

            LuaValue script = globals.load(new InputStreamReader(inputStream), resourcePath.getPath(), globals);
            LOGGER.info("Loaded script chunk: {}", script);

            LuaValue result = script.call();
            LOGGER.info("Executed script, result: {}", result);

            if (result == null || result.isnil()) {
                throw new RuntimeException("Script execution returned null for: " + resourcePath);
            }

            LuaValue dialogueValue = result.get("dialogue");
            if (dialogueValue == null || dialogueValue.isnil()) {
                throw new RuntimeException("Dialogue table not found in script result: " + resourcePath);
            }

            return dialogueValue.checktable();
        } catch (Exception e) {
            LOGGER.error("Failed to load dialogue script: " + resourcePath, e);
            throw new RuntimeException("Failed to load dialogue script: " + resourcePath, e);
        }
    }

    public List<Dialogue> parseLuaDialogue(LuaTable dialogueTable) {
        List<Dialogue> dialogues = new ArrayList<>();

        LuaValue key = LuaValue.NIL;
        while (true) {
            Varargs nkey = dialogueTable.next(key);
            if ((key = nkey.arg1()).isnil()) break;
            LuaValue value = nkey.arg(2);

            Integer dialogueId = value.get("dialogueId").toint();
            String content = value.get("content").tojstring();
            Integer nextDialogue = value.get("nextDialogue").optint(-1);
            String name = value.get("name").tojstring();
            String sprite = value.get("sprite").tojstring();

            List<Choice> choices = new ArrayList<>();
            LuaValue luaChoices = value.get("choices");
            if (!luaChoices.isnil()) {
                for (int i = 1; i <= luaChoices.length(); i++) {
                    LuaValue luaChoice = luaChoices.get(i);
                    String choiceText = luaChoice.get("text").tojstring();
                    Integer nextId = luaChoice.get("nextDialogue").toint();
                    choices.add(new Choice(choiceText, 0, nextId));
                }
            }

            dialogues.add(new Dialogue(dialogueId, content, choices, nextDialogue, name, sprite));
        }

        return dialogues;
    }

    public DialogueManager loadDialogue(String scriptName) {
        ResourceLocation location = new ResourceLocation("mobtalkerredux", "dialogues/"+scriptName);
        LuaTable dialogueTable = loadDialogueScript(location);
        List<Dialogue> dialogues = parseLuaDialogue(dialogueTable);

        return new DialogueManager(dialogues);
    }
}