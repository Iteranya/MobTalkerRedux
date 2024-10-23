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
            Dialogue start = new Dialogue(0,"Oh, a visitor~", List.of(),1);
            Dialogue greeting = new Dialogue(1,"Hello there, traveler! It's nice to see you.", List.of(),2);
            Dialogue question = new Dialogue(2,"How can I help you today?", List.of(
                    new Choice("Tell me more about this place.", 0, 3),
                    new Choice("Just passing by, thank you.", 0, 4)
            ),null);
            Dialogue answer1 = new Dialogue(3,"This land is filled with wonders and dangers alike. Be cautious!", List.of(),5);
            Dialogue answer2 = new Dialogue(4,"Ah, passing by then? Have fun! I bid you adieu", List.of(),5);
            Dialogue goodbye = new Dialogue(5,"Safe travels, stranger! Remember, you're always welcome here.", List.of(),null);
            // Create a DialogueManager with the example dialogues
            DialogueManager dialogueManager = new DialogueManager(
                    List.of(start,greeting, question, answer1, answer2, goodbye)
            );
            Minecraft.getInstance().execute(() ->
                    {
                        Minecraft.getInstance().setScreen(new DialogueScreen(dialogueManager));
                        dialogueManager.allowInteraction();
                    }
            );


        }
        return InteractionResult.SUCCESS;
    }


}
