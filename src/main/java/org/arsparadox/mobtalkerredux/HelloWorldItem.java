package org.arsparadox.mobtalkerredux;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;

public class HelloWorldItem extends Item {
    public HelloWorldItem() {
        super(new Item.Properties().tab(CreativeModeTab.TAB_MISC));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        if (!world.isClientSide) { // Only run on server side
            context.getPlayer().sendMessage(new TextComponent("Hewwo World"),context.getPlayer().getUUID());
        }
        else {
            Dialogue greeting = new Dialogue("Hello there, traveler! It's nice to see you.", List.of());
            Dialogue question = new Dialogue("How can I help you today?", List.of(
                    new Choice("Tell me more about this place.", 0, 3),
                    new Choice("Just passing by, thank you.", 0, 4)
            ));
            Dialogue answer1 = new Dialogue("This land is filled with wonders and dangers alike. Be cautious!", List.of());
            Dialogue answer2 = new Dialogue("Safe travels, stranger! Remember, you're always welcome here.", List.of());

            // Create a DialogueManager with the example dialogues
            DialogueManager dialogueManager = new DialogueManager(
                    List.of(greeting, question, answer1, answer2)
            );

            // Open the dialogue screen with the DialogueManager
            Minecraft.getInstance().execute(() ->
                    Minecraft.getInstance().setScreen(new DialogueScreen(dialogueManager))
            );
        }
        return InteractionResult.SUCCESS;
    }


}
