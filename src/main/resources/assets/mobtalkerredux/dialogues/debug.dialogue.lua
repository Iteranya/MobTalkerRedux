dialogue = {
    [0] = {
        dialogueId = 0,
        content = "Oh, a visitor~",
        choices = nil,
        nextDialogue = 1,
        name = "Cupa",
        sprite = "creeper/normal.png"
    },
    [1] = {
        dialogueId = 1,
        content = "Hello there, traveler! It's nice to see you.",
        choices = nil,
        nextDialogue = 2,
        name = "Cupa",
        sprite = "creeper/happy.png"
    },
    [2] = {
        dialogueId = 2,
        content = "How can I help you today?",
        choices = {
            { text = "Tell me more about this place.", nextDialogue = 3 },
            { text = "Just passing by, thank you.", nextDialogue = 4 }
        },
        name = "Cupa",
        sprite ="creeper/normal.png"
    },
    [3] = {
        dialogueId = 3,
        content = "This land is filled with wonders and dangers alike. Be cautious!",
        choices = nil,
        nextDialogue = 6,
        name = "Cupa",
        sprite = "creeper/normal.png"
    },
    [4] = {
        dialogueId = 4,
        content = "Ah, passing by then? Have fun! I bid you adieu",
        choices = nil,
        nextDialogue = 5,
        name = "Cupa",
        sprite = "creeper/happy.png"
    },
    [5] = {
        dialogueId = 5,
        content = "Safe travels, stranger! Remember, you're always welcome here.",
        choices = nil,
        nextDialogue = -1,
        name = "Cupa",
        sprite = "creeper/happy.png"
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
        sprite = "creeper/normal.png"
    },
    [7] = {
        dialogueId = 7,
        content = "Great! Have fun then!",
        choices = nil,
        nextDialogue = -1,
        name = "Cupa",
        sprite = "creeper/happy.png"
    },
    [8] = {
        dialogueId = 8,
        content = "Good luck lmao",
        choices = nil,
        nextDialogue = -1,
        name = "Cupa",
        sprite = "creeper/happy.png"
    },
    [9] = {
        dialogueId = 9,
        content = "That's good enough~",
        choices = nil,
        nextDialogue = -1,
        name = "Cupa",
        sprite = "creeper/normal.png"
    }
}
return {
    dialogue = dialogue
}