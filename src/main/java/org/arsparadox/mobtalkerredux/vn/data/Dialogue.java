package org.arsparadox.mobtalkerredux.vn.data;

import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Objects;

// Dialogue class representing a single dialogue entry
class Dialogue {
    private Integer dialogueId;
    private String content;
    private List<Choice> choices;

    private Integer nextDialogue;

    private String name;
    private ResourceLocation sprite;

    public Dialogue(Integer dialogueId, String content, List<Choice> choices, Integer next, String name, String sprite) {
        this.dialogueId = dialogueId;
        this.content = content;
        this.choices = choices;
        this.nextDialogue = Objects.requireNonNullElseGet(next, () -> -1);
        this.name = name;
        this.sprite = new ResourceLocation("mobtalkerredux", "textures/characters/" + sprite);
    }

    public String getContent() {
        return content;
    }

    public Integer getDialogueId() {
        return this.dialogueId;
    }

    public Integer getNext() {
        return this.nextDialogue;
    }

    public List<Choice> getChoices() {
        return choices == null ? List.of() : choices;
    }

    public ResourceLocation getSprite() {
        return this.sprite;
    }

    public String getName() {
        return this.name;
    }
}
