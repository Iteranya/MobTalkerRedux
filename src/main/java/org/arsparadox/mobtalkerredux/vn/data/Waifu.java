// Waifu.java
package org.arsparadox.mobtalkerredux.vn.data;

import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Waifu {
    private final UUID id; // Unique identifier for each waifu
    private String name;
    private String mobType;
    private String dialoguesFolder;
    private String spritesFolder;
    private String questsFolder;
    private String lastConversation;
    private String lastDialogue;
    private int reputation;
    private List<String> activeQuests;
    private List<String> completedQuests;
    private List<String> traits;
    private List<String> personalities;
    private List<String> thoughts;
    private int affection;

    // Updated constructor with UUID
    public Waifu(
            String name,
            String mobType,
            String spritesFolder,
            String dialoguesFolder,
            String questsFolder,
            String lastConversation,
            String lastDialogue,
            int affection,
            int reputation,
            List<String> activeQuests,
            List<String> completedQuests,
            List<String> thoughts,
            List<String> traits,
            List<String> personalities
    ) {
        this.id = UUID.randomUUID(); // Generate unique ID for new waifus
        this.name = name;
        this.mobType = mobType;
        this.dialoguesFolder = dialoguesFolder;
        this.questsFolder = questsFolder;
        this.spritesFolder = spritesFolder;
        this.lastDialogue = lastDialogue;
        this.lastConversation = lastConversation;
        this.affection = affection;
        this.reputation = reputation;
        this.activeQuests = activeQuests != null ? activeQuests : new ArrayList<>();
        this.completedQuests = completedQuests != null ? completedQuests : new ArrayList<>();
        this.thoughts = thoughts != null ? thoughts : new ArrayList<>();
        this.traits = traits != null ? traits : new ArrayList<>();
        this.personalities = personalities != null ? personalities : new ArrayList<>();
    }

    // Getter for UUID
    public UUID getId() {
        return id;
    }

    // Modern getters and setters with null checks
    public String getName() {
        return name != null ? name : "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobType() {
        return mobType != null ? mobType : "";
    }

    public void setMobType(String mobType) {
        this.mobType = mobType;
    }

    public String getDialoguesFolder() {
        return dialoguesFolder != null ? dialoguesFolder : "";
    }

    public void setDialoguesFolder(String dialoguesFolder) {
        this.dialoguesFolder = dialoguesFolder;
    }

    public int getAffection() {
        return affection;
    }

    public void setAffection(int affection) {
        this.affection = affection;
        // You can implement affection-based events here
        onAffectionChanged();
    }

    public void loadFromNBT(CompoundTag tag) {
        name = tag.getString("Name");
        mobType = tag.getString("MobType");
        dialoguesFolder = tag.getString("DialoguesFolder");
        questsFolder = tag.getString("QuestsFolder");
        spritesFolder = tag.getString("SpritesFolder");
        lastConversation = tag.getString("LastConversation");
        lastDialogue = tag.getString("LastDialogue");
        affection = tag.getInt("Affection");
        reputation = tag.getInt("Reputation");

        // Load lists
        activeQuests = loadStringList(tag, "ActiveQuests");
        completedQuests = loadStringList(tag, "CompletedQuests");
        traits = loadStringList(tag, "Traits");
        personalities = loadStringList(tag, "Personalities");
        thoughts = loadStringList(tag, "Thoughts");
    }

    private void onAffectionChanged() {
        // Implement affection level checks and triggers
        if (affection >= 100) {
            // Unlock special dialogues or features
        }
    }

    // Helper method to save string lists to NBT
    private void saveStringList(CompoundTag tag, String key, List<String> list) {
        if (list != null && !list.isEmpty()) {
            tag.putInt(key + "Size", list.size());
            for (int i = 0; i < list.size(); i++) {
                tag.putString(key + i, list.get(i));
            }
        }
    }

    // Helper method to load string lists from NBT
    private List<String> loadStringList(CompoundTag tag, String key) {
        List<String> list = new ArrayList<>();
        int size = tag.getInt(key + "Size");
        for (int i = 0; i < size; i++) {
            list.add(tag.getString(key + i));
        }
        return list;
    }

    public CompoundTag saveToNBT() {
        CompoundTag tag = new CompoundTag();

        // Save UUID and basic properties
        tag.putUUID("UUID", getId());
        tag.putString("Name", getName());
        tag.putString("MobType", getMobType());
        tag.putString("DialoguesFolder", getDialoguesFolder());
        tag.putString("QuestsFolder", questsFolder);
        tag.putString("SpritesFolder", spritesFolder);
        tag.putString("LastConversation", lastConversation);
        tag.putString("LastDialogue", lastDialogue);
        tag.putInt("Affection", affection);
        tag.putInt("Reputation", reputation);

        // Save lists
        saveStringList(tag, "ActiveQuests", activeQuests);
        saveStringList(tag, "CompletedQuests", completedQuests);
        saveStringList(tag, "Traits", traits);
        saveStringList(tag, "Personalities", personalities);
        saveStringList(tag, "Thoughts", thoughts);

        return tag;
    }

    // Helper methods remain the same...

}
