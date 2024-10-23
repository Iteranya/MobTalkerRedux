package org.arsparadox.mobtalkerredux;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

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
//            Dialogue start = new Dialogue(0,"Oh, a visitor~", List.of(),1,"cupa","creeper/soliah.png");
//            Dialogue greeting = new Dialogue(1,"Hello there, traveler! It's nice to see you.", List.of(),2,"cupa","creeper/soliah.png");
//            Dialogue question = new Dialogue(2,"How can I help you today?", List.of(
//                    new Choice("Tell me more about this place.", 0, 3),
//                    new Choice("Just passing by, thank you.", 0, 4)
//            ),null,"cupa","creeper/soliah.png");
//            Dialogue answer1 = new Dialogue(3,"This land is filled with wonders and dangers alike. Be cautious!", List.of(),6,"cupa","creeper/soliah.png");
//            Dialogue answer2 = new Dialogue(4,"Ah, passing by then? Have fun! I bid you adieu", List.of(),null,"cupa","creeper/soliah.png");
//            Dialogue goodbye = new Dialogue(5,"Safe travels, stranger! Remember, you're always welcome here.", List.of(),null,"cupa","creeper/soliah.png");
//            Dialogue question2 = new Dialogue(6,"Anyway, can you fight?", List.of(
//                    new Choice("Yep", 0, 7),
//                    new Choice("Nope", 0, 8),
//                    new Choice("Maybe?", 0, 9)
//            ),null,"cupa","creeper/soliah.png");
//            Dialogue answer3 = new Dialogue(7,"Great! Have fun then!", List.of(),null,"cupa","creeper/soliah.png");
//            Dialogue answer4 = new Dialogue(8,"Good luck lmao", List.of(),null,"cupa","creeper/soliah.png");
//            Dialogue answer5 = new Dialogue(9,"That's good enough~", List.of(),null,"cupa","creeper/soliah.png");
//            // Create a DialogueManager with the example dialogues
//            DialogueManager dialogueManager = new DialogueManager(
//                    List.of(start,greeting, question, question2,answer1, answer2, goodbye,answer3,answer4,answer5)
//            );
            ScriptManager scriptManager = new ScriptManager();
            DialogueManager dialogue = scriptManager.loadDialogue("debug.dialogue.lua");
            Minecraft.getInstance().execute(() -> {
                        Minecraft.getInstance().setScreen(new DialogueScreen(dialogue));
                    }
            );


        }
        return InteractionResult.SUCCESS;
    }


}
