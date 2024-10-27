-- Global state for dialogue compilation
local DialogueCompiler = {
    currentId = 0,
    variables = {},
    flags = {},
    labelToId = {},
    pendingJumps = {}
}

-- Initialize compiler state
function DialogueCompiler:new()
    local o = {
        currentId = 0,
        variables = {},
        flags = {},
        labelToId = {},
        pendingJumps = {},
        dialogue = {}
    }
    setmetatable(o, self)
    self.__index = self
    return o
end

-- Core dialogue creation function
function DialogueCompiler:say(character, text, sprite, label)
    local entry = {
        dialogueId = self.currentId,
        content = text,
        choices = nil,
        nextDialogue = self.currentId + 1,
        name = character,
        sprite = sprite or character:lower() .. "_normal.png"
    }

    if label then
        self.labelToId[label] = self.currentId
    end

    self.dialogue[self.currentId] = entry
    self.currentId = self.currentId + 1
    return entry
end

-- Create choice menu
function DialogueCompiler:menu(character, text, choices, sprite)
    local formattedChoices = {}
    for i, choice in ipairs(choices) do
        table.insert(formattedChoices, {
            text = choice[1],
            nextDialogue = self.currentId + i
        })
    end

    local entry = {
        dialogueId = self.currentId,
        content = text,
        choices = formattedChoices,
        name = character,
        sprite = sprite or character:lower() .. "_normal.png"
    }

    self.dialogue[self.currentId] = entry
    self.currentId = self.currentId + #choices
    return entry
end

-- Set a flag
function DialogueCompiler:setFlag(flagName, value)
    local entry = self:say("SYSTEM", "")
    entry.setFlag = {flag = flagName, value = value}
    return entry
end

-- Check a flag and branch
function DialogueCompiler:checkFlag(flagName, trueLabel, falseLabel)
    local entry = self:say("SYSTEM", "")
    entry.checkFlag = {
        flag = flagName,
        trueJump = trueLabel,
        falseJump = falseLabel
    }
    table.insert(self.pendingJumps, entry)
    return entry
end

-- Jump to a label
function DialogueCompiler:jump(label)
    local entry = self:say("SYSTEM", "")
    entry.jump = label
    table.insert(self.pendingJumps, entry)
    return entry
end

-- Resolve all jumps and labels after compilation
function DialogueCompiler:resolve()
    for _, entry in ipairs(self.pendingJumps) do
        if entry.jump then
            entry.nextDialogue = self.labelToId[entry.jump]
        elseif entry.checkFlag then
            entry.nextDialogue = {
                [true] = self.labelToId[entry.checkFlag.trueJump],
                [false] = self.labelToId[entry.checkFlag.falseJump]
            }
        end
    end
end