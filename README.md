# Visual Novel Framework

#### This is a Framework to give Mod Maker and others to add VN-like cutscenes/scenes inside the game.

## Description

This mod requires a Script to Play.

The mod leverages Vanilla Resource Pack system, so installing a script is identical to installing a Resource Pack. Drag and drop into the Resource Pack folder. Inside the script, usually, there should be a 'trigger word'

Name a mob this 'trigger word' and right click it with the item to talk with it.

Have fun!!!

#### Features

*   Dialogue System, VN Style
*   Custom Scripting / Pseudo DSL and a Framework and an SDK for Script Development
*   Branching Dialogue
*   Variable Tracking System (inside the game)
*   2D Sprite, like Visual Novel
*   Multiple Sprite System!
*   Align Character Position and Such~

#### CUSTOM SCRIPTS!?!?

*   YES!
*   I made a scripting language just for this in Python!
*   Use the Hikarin SDK to create and compile scripts!
*   More info can be found in the SDK Read Me

#### Compatibility?

Okay this is an interesting topic.

Since this mod relies on two things, the Mod Itself and the SDK/Framework there are some notes about compatibility:

*   Script written using the newest SDK will always be compatible with the Older Version of the mod.
*   If the script contains a missing feature (Say, you made a script using the latest SDK and run it in the Mod Beta version that doesn't support background), well, the script will run, just the background won't change.
*   Script written using the older SDK will always be compatible with the Newer Version of the mod. (Though, this may vary, but backwards compatibility should always be possible. Report a bug for problems with Compatibility.).

What about Cross Mod Compatibility? Modpacks?

*   The Visual Novel Engine running inside Minecraft is written in Pure Java it is independent of Minecraft itself.
*   The 'modded' / 'minecraft' part of thing is the UI. A mod that messes with how Minecraft handle UI (overrides the Guigraphics for example), will not be compatible.
*   On that note, feel free to use this on your mod, modpacks, whatever~

#### Coming Soon

*   DBMS for permanent variable and state tracking (Might be out of scope, but ironically available in Alpha version)
*   Animation Feature (Not out of scope and is being worked on, but ironically is the one getting put on hold)

Mod Github Page: [Minecraft Visual Novel Framework](https://github.com/Iteranya/MinecraftHikarinMod)

SDK Github Page (to make scripts): [Hikarin Visual Novel SDK](https://github.com/Iteranya/hikarin-framework)

### Can I Use This In My Modpack???

Yes, Go Ahead, This Thing Is Made For That!!!

But… Consider letting me know / discuss it with me so I can help you with getting it set up.

Since script development is somewhat separate from Modding, it can be a bit confusing if you're not used to Python.

Discussion + Query: [My Discord Server](https://discord.gg/Apr4MTE3vm)

In case you missed it:

## Multiplayer Incompatible
