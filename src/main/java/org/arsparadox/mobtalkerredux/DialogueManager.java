package org.arsparadox.mobtalkerredux;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import java.util.*;

public class DialogueManager {

    public interface DialogueStateObserver {
        void onDialogueStateChanged();
    }

    private List<DialogueStateObserver> observers;
    // State classes
    public record DialogueBoxState(String content) {}
    public record CharacterLabelState(String content) {}
    public record CharacterSpriteState(String content) {}
    public record ChoiceState(Map<String, String> choices) {
        public ChoiceState(Map<String, String> choices) {
            this.choices = new HashMap<>(choices);
        }

        @Override
        public Map<String, String> choices() {
            return Collections.unmodifiableMap(choices);
        }
    }

    private enum ActionType {
        DIALOGUE,
        CHARACTER,
        CHOICES
    }

    private record DialogueAction(ActionType type, Object content) {}

    // State management
    private Optional<DialogueBoxState> dialogueBox;
    private Optional<CharacterLabelState> characterLabel;
    private Optional<CharacterSpriteState> characterSprite;
    private Optional<ChoiceState> choicesBox;

    // Queue and mapping management
    private final Queue<DialogueAction> dialogueQueue;
    private final Map<String, String> choiceMappings;

    // Lua integration
    private Globals globals;

    public DialogueManager() {
        this.dialogueBox = Optional.empty();
        this.characterLabel = Optional.empty();
        this.characterSprite = Optional.empty();
        this.choicesBox = Optional.empty();
        this.dialogueQueue = new LinkedList<>();
        this.choiceMappings = new HashMap<>();
        this.observers = new ArrayList<>();
    }

    public void addObserver(DialogueStateObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(DialogueStateObserver observer) {
        observers.remove(observer);
    }

    // Queue management methods
    public void queueDialogue(String content) {
        System.out.println("public void queueDialogue offer" +  content);
        this.dialogueQueue.offer(new DialogueAction(ActionType.DIALOGUE, content));
    }

    public void queueCharacterName(String name) {
        this.dialogueQueue.offer(new DialogueAction(ActionType.CHARACTER, name));
    }

    public void queueChoices(Map<String, String> choices) {
        this.dialogueQueue.offer(new DialogueAction(ActionType.CHOICES, choices));
    }

    // State setters
    public void setDialogue(String content) {
        this.dialogueBox = Optional.of(new DialogueBoxState(content));
    }

    public void setCharacterName(String name) {
        this.characterLabel = Optional.of(new CharacterLabelState(name));
    }

    public void setCharacterSprite(String spriteIdentifier) {
        this.characterSprite = Optional.of(new CharacterSpriteState(spriteIdentifier));
    }

    public void setChoices(Map<String, String> choices) {
        this.choicesBox = Optional.of(new ChoiceState(choices));
    }

//    public Optional<Dialogue> getCurrentDialogue() {
//        return dialogues.stream()
//                .filter(dialogue -> dialogue.getDialogueId().equals(currentDialogue))
//                .findFirst();
//    }

    // State getters
    public Optional<DialogueBoxState> getDialogueBox() {
        return dialogueBox.stream().findFirst();
    }

    public Optional<CharacterLabelState> getCharacterLabel() {
        return characterLabel;
    }

    public Optional<CharacterSpriteState> getCharacterSprite() {
        return characterSprite;
    }

    public Optional<ChoiceState> getChoicesBox() {
        return choicesBox;
    }

    // Clear methods
    public void clearDialogue() {
        this.dialogueBox = Optional.empty();
    }

    public void clearCharacterName() {
        this.characterLabel = Optional.empty();
    }

    public void clearCharacterSprite() {
        this.characterSprite = Optional.empty();
    }

    public void clearChoices() {
        this.choicesBox = Optional.empty();
    }

    public void clearAll() {
        clearDialogue();
        clearCharacterName();
        clearCharacterSprite();
        clearChoices();
        dialogueQueue.clear();
        choiceMappings.clear();
    }

    // Choice mapping management
    public void addChoiceMapping(String choice, String nextScene) {
        choiceMappings.put(choice, nextScene);
    }

    // Lua integration
    public void setGlobals(Globals globals) {
        this.globals = globals;
    }

    // Display and navigation methods
    public void displayNext() {
        if (this.dialogueQueue.isEmpty()) {
            System.out.println("Queue is Empty");
            return;
        }

        // Clear appropriate states based on the next action
        DialogueAction action = dialogueQueue.poll();
        switch (action.type) {
            case DIALOGUE -> {
                clearDialogue();
                setDialogue((String) action.content);
            }
            case CHARACTER -> {
                clearCharacterName();
                setCharacterName((String) action.content);
            }
            case CHOICES -> {
                clearChoices();
                @SuppressWarnings("unchecked")
                Map<String, String> choices = (Map<String, String>) action.content;
                setChoices(choices);
            }
        }

        // Notify UI of state change
        notifyDialogueScreenOfUpdate();
    }

    public void buttonNav(String label) {
        if (label == null) {
            // No choice selected, just advance dialogue
            displayNext();
            return;
        }

        if (choicesBox.isPresent()) {
            String nextScene = choiceMappings.get(label);
            if (nextScene != null && globals != null) {
                clearAll();
                globals.set("currentScene", LuaValue.valueOf(nextScene));
                // Queue up new scene's dialogue
                globals.get("scenes").get(nextScene).call();
                // Display first dialogue of new scene
                displayNext();
            }
        } else {
            // If no choices are present, advance to next dialogue
            displayNext();
        }
    }

    // UI notification method - implement this based on your UI system
    private void notifyDialogueScreenOfUpdate() {
        for (DialogueStateObserver observer : observers) {
            observer.onDialogueStateChanged();
        }
    }

    // Helper method to check if there's more content
    public boolean hasMoreContent() {
        return !dialogueQueue.isEmpty() || choicesBox.isPresent();
    }

    // Helper method to get current queue size
    public int getQueueSize() {
        return dialogueQueue.size();
    }
}
