dialogue = {
    [0] = {
        dialogueId = 0,
        content = "Oh, a visitor~",
        choices = nil,
        nextDialogue = 1,
        name = "Cupa",
        sprite = "creeper/soliah_default.png"
    },
    [1] = {
        dialogueId = 1,
        content = "Hello there, traveler! It's nice to see you.",
        choices = nil,
        nextDialogue = 2,
        name = "Cupa",
        sprite = "creeper/soliah_greet.png"
    },
    [2] = {
        dialogueId = 2,
        content = "How can I help you today?",
        choices = {
            { text = "Tell me more about this place.", nextDialogue = 3 },
            { text = "Just passing by, thank you.", nextDialogue = 4 }
        },
        name = "Cupa",
        sprite = "creeper/soliah_help.png"
    },
    [3] = {
        dialogueId = 3,
        content = "This land is filled with wonders and dangers alike. Be cautious!",
        choices = nil,
        nextDialogue = 6,
        name = "Cupa",
        sprite = "creeper/soliah_explaining.png"
    },
    [4] = {
        dialogueId = 4,
        content = "Ah, passing by then? Have fun! I bid you adieu",
        choices = nil,
        nextDialogue = 5,
        name = "Cupa",
        sprite = "creeper/soliah_adieu.png"
    },
    [5] = {
        dialogueId = 5,
        content = "Safe travels, stranger! Remember, you're always welcome here.",
        choices = nil,
        nextDialogue = -1,
        name = "Cupa",
        sprite = "creeper/soliah_goodbye.png"
    },
    [6] = {
        dialogueId = 6,
        content = "Anyway, can you fight?",
        choices = {
            { text = "Yep", nextDialogue = 7 },
            { text = "Nope", nextDialogue = 8 },
            { text = "Maybe?", nextDialogue = 9 }
        },
        name = "Cupa",
        sprite = "creeper/soliah_fight.png"
    },
    [7] = {
        dialogueId = 7,
        content = "Great! Have fun then!",
        choices = nil,
        nextDialogue = -1,
        name = "Cupa",
        sprite = "creeper/soliah_fun.png"
    },
    [8] = {
        dialogueId = 8,
        content = "Good luck lmao",
        choices = nil,
        nextDialogue = -1,
        name = "Cupa",
        sprite = "creeper/soliah_lmao.png"
    },
    [9] = {
        dialogueId = 9,
        content = "That's good enough~",
        choices = nil,
        nextDialogue = -1,
        name = "Cupa",
        sprite = "creeper/soliah_enough.png"
    }
}
