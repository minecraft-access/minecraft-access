# Minecraft Access Features

This page contains details for all the features that are currently in the mod.

If you have any questions about original game functions, please search on the [Minecraft wiki](https://minecraft.wiki/?search) first before asking for help.
If you have any suggestions on improvements to existing features or about a new feature, you can [join the discord server](https://discord.gg/yQjjsDqWQX) or [post an issue](https://github.com/khanshoaib3/minecraft-access/issues).

There is also a page for [viewing all sound effects](https://html-preview.github.io/?url=https://github.com/khanshoaib3/minecraft-access/blob/1.21/docs/sounds.html) used in this mod.

## Table Of Contents

1. [Camera Controls](#camera-controls)
2. [Mouse Simulation](#mouse-simulation)
3. [Read Crosshair](#read-crosshair)
    * [Relative Position Sound Cue](#relative-position-sound-cue)
    * [Partial Speaking](#partial-speaking)
4. [Inventory Controls](#inventory-controls)
5. [Points of Interest](#points-of-interest)
    * [POI Locking](#poi-locking)
    * [POI Marking](#poi-marking)
6. [Fall Detector](#fall-detector)
7. [Access Menu](#access-menu)
8. [Play Status](#player-status)
    * [Position Narrator](#position-narrator)
    * [Health n Hunger](#health-n-hunger)
    * [Player Warnings](#player-warnings)
9. [Book Editing](#book-editing)
10. [Speak Text Editing](#speak-text-editing)
11. [Other Small Features](#other-small-features)
    * [Speak held item](#speak-held-item)
    * [Biome Indicator](#biome-indicator)
    * [XP Indicator](#xp-indicator)
    * [Speak Harvest Of Fishing](#speak-picked-up-items-while-holding-fishing-rod)
    * [Speak Chat Messages](#speak-chat-messages)
    * [Speak Action Bar Updates](#speak-action-bar-messages)
    * [I18N Fallback Mechanism](#i18n-fallback-mechanism)

## Camera Controls

Like most 3D games, you can rotate the camera 360 degrees freely in Minecraft with the mouse, and the direction you look is the direction to go forward while you press the W key.
This feature allows you to control the camera (your in-game facing direction and crosshair) through the keyboard.
The mod will automatically report the current direction as the camera moves,
such as `North`, `South East`, `Up`, `Down`, and `Straight`.

See also: [Keybindings](/docs/keybindings.md#camera-controls), [Configuration](/docs/config.md#camera-controls)

## Mouse Simulation

This feature allows you to simulate five mouse operations (left middle right click, scroll up and down) through the keyboard, while the original mouse input still works.
This feature supports continuous key pressing, e.g., destroying a block requires continuous pressing of the left mouse button against that block.

You MUST keep the vanilla `Attack/Destroy` key, `Use Item/Place Block`
key and `Pick Block` key set to the default mouse keys.
This feature will only simulate mouse operations,
not directly execute the attack or place operation; it needs the mouse key bindings as a medium.

See also: [Keybindings](/docs/keybindings.md#mouse-simulation), [Configuration](/docs/config.md#mouse-simulation)

## Read Crosshair

This mod will automatically speak the information of the block and entity
(i.e., living creatures and moving objects) you’re targeting with your crosshair
(i.e., what you're looking at).
The crosshair is in the center of the screen.

If you're looking at a block, the mod will speak which side of that block you’re looking at;
this is useful for recognizing directions and placing blocks accurately.
For example, if the mod says `Stone North`, it means you're looking at the north side of a stone block;
this indicates that you’re within 180 degrees of where this side is facing, not necessarily directly in front of it.
You can determine your current location with the help of your relative position to the static block.

For simple targets, the mod will only speak the name (and side if it's a block),
such as `Pig`, `Cow`, `Grass Block Up`, `Stone North`.
For functional blocks or entities with multiple forms, the mod will also speak current state of the block,
such as `Ripe Wheat Crops Up`, `White Sheep Shearable`, `Opened Oak Door South`, `Powered Dispenser West Facing East`.

According to the [wiki](https://minecraft.wiki/w/Breaking#Basics_of_breaking), the breaking distance in survival mode is 4.5 blocks (in Java Edition), but the `ReadCrosshair` feature will speak targets at most 6 blocks away. So if the mod says something, but you can't interact with it, move forward a little closer to it.

See also: [Configuration](/docs/config.md#read-crosshair)

### Relative Position Sound Cue

Whenever you're looking at a block or entity,
the mod will play a piano sound cue to indicate the relative location between you and the target.
Volume to represent distance, the louder the sound, the closer the distance.
Pitch to represent elevation, the higher the sound, the higher the target relative to you.
You can turn off this feature or change the sound volume in config.

See also: [Configuration](/docs/config.md#relative-position-sound-cue)

### Partial Speaking

Make the mod only (or only not) speak entities/blocks that you've configured.
This feature can be easily misused, so it is not enabled by default.
This works as a whitelist or a blacklist depending on how you choose to set it up.
For example,
you can disable the announcement of grass blocks from the narration
by setting the mode to blacklist and adding grass as a phrase.

See also: [Configuration](/docs/config.md#partial-speaking)

## Inventory Controls

This feature allows you to operate various screens using the keyboard instead of the mouse.
In fact, this is a feature that makes almost all screens accessible, not just the inventory screen.
The vast majority of screens contain operations for transferring and using items, so it's appropriate to call this feature `Inventory Controls`.

We divide the various parts of each screen into different slot groups, following the original design of the screens.
One slot group contains one or more slots, arranged in a grid shape, each of which may be empty or contain one type of items (one stack at max).
You can switch focused group, move to different slots within a group, or pick up items from slots in one group and place them into slots in another group (to transfer or use them).

Use the mouse simulation keys in the [`Mouse Simulation`](#mouse-simulation) feature to preform operations such as item transferring and button clicking.
The left mouse button will pick up and put down the full number of items in the slot.
The right mouse button will pick up half of the items or put one item down if you're already holding a compatible item.
The middle mouse key can be used in creative mode to pick up a full stack of items from the creative inventory item list.
The mod will speak what is currently in the slot, such as `Empty Slot` or `64 Stone`.
When you pick up the full number of items in a slot, the mod will speak `Empty Slot` to represent the current state of the slot.
When you try to place items in an occupied slot,
it will instead swap with the items already in the slot, resulting in the items in the slot being placed onto your cursor.

Please note that:

1. Item speaking in `Scrollable Recipes Group` under the `Crafting Screen` will be delayed about one second, please wait after pressing the move slot key to hear the item name.
2. In the `Scrollable Recipes Group` on the `Crafting Screen`, except the first page, sometimes you will hear nothing while you're moving between slots. It's best to keep items you want to craft within the first page of the recipe book by moving unused items out of your inventory.
3. After you put things into an [Anvil](https://minecraft.wiki/w/Anvil), the cursor will be automatically focused onto the rename input text field, all you need to do is press the `Enter` key to go back to slot selection.
4. Servers will build custom inventory screens to serve as special option menus, some servers like hypixel will continuously change the option (item) names, which will cause this mod to continuously speak the changed item name. Disable the `Speak Focused Slot Changes` config to solve this problem.

See also: [Configuration](/docs/config.md#inventory-controls), [Keybindings](/docs/keybindings.md#inventory-controls)

## Points of Interest

This feature will scan and notify (with sound cues) you of (pre-configured) special blocks and all types of entities around you. You can listen to the sound cues for different types of blocks and entities on the Sound Demo page.

What blocks are considered special? Those you tend to miss.
In Minecraft, not every block is cube-shaped — for example, buttons are tiny squares, glass panes are thin bars, ladders are a thin layer attached to another block, you'll find it difficult to point precisely at these blocks.
Precious blocks like raw ores can be seen at a glance by sighted players, but visually impaired players will need to point at them to know their existence.

By the same logic, entities (players, animals, monsters) are movable, so scanning and locking onto them helps you get close to and interact with them.
Be aware that if you keep any menu screen open, the mod will stop scanning and notifying you of entities for a cleaner screen narration, which may get you in danger if there are monsters around you.

See also: [Configuration](/docs/config.md#point-of-interest), [Keybindings](/docs/keybindings.md#point-of-interest)

### POI Locking

You can lock onto the closest target with a key, then your camera will follow the target as it moves, so you can approach it more easily.
For example, when you want to mine an ore, capture an animal or fight a monster.

Note that:

* The mod will continue locking on the position where the eye of ender disappears (if you enabled `Auto Lock on to Eye of Ender when Used` in the config).
* The mod will automatically stop locking on ladders when you start climbing them, so you can directly climb the ladder without manually pressing the unlock key.
* The mod will automatically stop locking on blocks if they are destroyed or changed (for example, a [door](https://minecraft.wiki/w/Door) is opened or closed, or a [piston](https://minecraft.wiki/w/Piston) is activated).

#### Bow aim assist

When you start drawing the bow, you will automatically lock onto the nearest hostile mob. A sound will play indicating if you can hit your target and how far the bow has been drawn.
If the sound playing is a piano, you can shoot the target, and if it's a bass, you can't shoot the target. The sound will increase in pitch 3 times as you draw the bow, stopping when the bow is fully drawn.

See also: [Configuration](/docs/config.md#entitiesblocks-locking), [Keybindings](/docs/keybindings.md#point-of-interest)

### POI Marking

Sometimes you may need to search for a special type of block or entity that is not in the pre-configured list.
You can mark the type while you’re pointing at it (with your crosshair).
You can set the mod to only scan and notify for marked types
or suppress scanning and notifying for pre-configured types.

See also: [Configuration](/docs/config.md#entitiesblocks-marking), [Keybindings](/docs/keybindings.md#point-of-interest)

## Fall Detector

This feature will alert you with a foot stomping sound cue when you're near the edge of a big drop.
It will play a sound effect for every location that meets the set threshold distance, the louder the sound, the closer you are to the edge.

See also: [Configuration](/docs/config.md#fall-detector)

## Access Menu

This menu can be accessed by pressing the F4 key by default
and integrates a number of helper functions which can be executed from within the menu 
or by using the individual hotkey directly. 
Note that the individual function keys aren't bound by default, aside from narrate target,
so you will need to bind the ones you want to use yourself.

| Function                              | Description                                                                                                                                                                                  |
|---------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1. Block and fluid target information | Speak the name and information of the targeted block, the range (20 blocks) is much more than the `Read Crosshair` triggering distance (6 blocks).                                           |
| 2. Block and fluid target position    | Speak the x y z position of the targeted block                                                                                                                                               |
| 3. Light level                        | Speak the [light](https://minecraft.wiki/w/Light) level of the player's position                                                                                                             |
| 4. Find Closest Water Source          | Find the closest [water](https://minecraft.wiki/w/Water) source block in the set range, play a [Item plops](https://minecraft.wiki/w/Item_(entity)#Sounds) bubbling sound at target position |
| 5. Find Closest Lava Source           | Find the closest [lava](https://minecraft.wiki/w/Lava) source block in the set range,  play a [Item plops](https://minecraft.wiki/w/Item_(entity)#Sounds) bubbling sound at target position  |
| 6. Biome                              | Speak the name of the [biome](https://minecraft.wiki/w/Biome) that the player is currently in                                                                                                |
| 7. Time of Day                        | Speak the current time of the in-game [Daylight Cycle](https://minecraft.wiki/w/Daylight_cycle)                                                                                              |
| 8. XP                                 | Speak the player's current [experience](https://minecraft.wiki/w/Experience) level and progress                                                                                              |
| 9. Refresh screen Reader              | Refresh the screen Reader                                                                                                                                                                    |
| 0. Open Config Menu                   | Opens the Config Menu which can be used to change the configs of this mod that take effect immediately                                                                                       |

See also: [Configuration](/docs/config.md#access-menu), [Keybindings](/docs/keybindings.md#access-menu)

## Player Status

Some features that help you understand the status of the character you control.

### Position Narrator

Minecraft has a built-in [absolute coordinate system](https://minecraft.wiki/w/Coordinates), x-axis for the longitude, z for the latitude, and y for the elevation. This feature provides keystrokes to read out coordinates.

See also: [Configuration](/docs/config.md#position-narrator), [Keybindings](/docs/keybindings.md#position-narrator)

### Health n Hunger

This feature adds a key to speak your health and hunger.

See also: [Configuration](/docs/config.md#health-n-hunger), [Keybindings](/docs/keybindings.md#health-n-hunger)

### Player Warnings

This feature warns you using a metal sound when your [air](https://minecraft.wiki/w/Damage#Drowning)
(only tracked when you're submerged in water),
[health](https://minecraft.wiki/w/Health), or [hunger](https://minecraft.wiki/w/Hunger) fall below the set thresholds.
You'll hear something like `Warning, Health is {current health}`.
If you enable the `Play Sound` config, you'll also hear a sound cue along with the warning words.
You may also want to learn about [the various damage types](https://minecraft.wiki/w/Damage) in the game.

See also: [Configuration](/docs/config.md#player-warnings)

## Book Editing

The [Book Editing Screen](https://minecraft.wiki/w/Book_and_Quill#Writing) is a special screen that has nothing to do with item management thus has no slot group.
We've added some not re-mappable keys and narration cues to make it 100% accessible.
This section as a brief guide on how to use this screen, listing the keys corresponding to the functions.

See also: [Keybindings](/docs/keybindings.md#book-editing)

## Speak Text Editing

The mod will simulate the feedback you get when typing text in other software's input boxes.
It will speak the text that you delete, select, cursor over on the [Chat Screen](https://minecraft.wiki/w/Chat),
[Book Edit Screen](#book-editing),
[Command Block Screen](https://minecraft.wiki/w/Command_Block#Modification), and so on.
Note that we have
to speak the whole unfinished book title while signing a book, since you can't move the cursor in that state.
This means that the cursor will stay at the end of the title and ignore your cursor movement operations.

The mod also simplifies the original [command](https://minecraft.wiki/w/Commands) suggestion narration for a better typing experience with lesser annoying too-detailed narrations.
The content of one command suggestion narration will be like `{the order of focused suggestion}x{total number of suggestions} {suggestion} selected`,
the format can be customized in the config.

See also: [Configuration](/docs/config.md#other-configurations)

## Other Small Features

Some small features that are automatically triggered based on your actions.

See also: [Configuration](/docs/config.md#other-configurations)

### Speak Held Item

When you switch held items, speak the name and number of items in your hand (main hand only).
The item [durability](https://minecraft.wiki/w/Durability) information is included.
If you feel that continuous item count reporting is annoying, you can disable it in the [configuration](/docs/config.md#other-configurations).

### Biome Indicator

Speak the name of the [biome](https://minecraft.wiki/w/Biome) when you enter one.

### XP Indicator

Speak when your [experience](https://minecraft.wiki/w/Experience) level is increased or decreased.

### Speak Picked Up Items While Holding Fishing Rod

This feature will speak the items that you pick up while you're holding a fishing rod,
even if you're not actually fishing.

### Speak Chat Messages

Speak [chat](https://minecraft.wiki/w/Chat) messages, so you won't miss your friends' conversation.
If you feel that too many chat messages are too noisy,
you can turn off showing chat messages in the original `Chat Settings...` options,
or press `P`
to open the [Social Interactions Screen](https://minecraft.wiki/w/Social_interactions) to mute particular players.

See also: [Keybindings](/docs/keybindings.md#speak-chat-messages)

### Speak Action Bar Messages

Messages shown in the form of the [action bar](https://minecraft.wiki/w/Commands/title) are common in modded multiplayer servers,
usually they are used as server announcements or for showing mod-specific information,
for example, in hypixel it shows extra `health, defense, mana` values of players.
When you feel like the mod is repeating action bar messages,
that's because the messages are partially updated but the mod will speak the whole sentence,
try enabling the `Only Speak Action Bar Updates` config in the [configuration](/docs/config.md#other-configurations)
and find what you like the most.

### I18N Fallback Mechanism

Minecraft has support for many languages, when referring to languages that this mod supports, we mean text that is introduced by this mod and doesn't exist in the original game.
This mod has a fallback mechanism for I18n in case it fails on unsupported languages or text that is not currently translated in supported languages.
If any text is not translated to your language in I18N yet, the mod will use the English version instead.
Setting the game to your familiar language is recommended, even if it's not supported by this mod,
since you can still benefit from the translation of the game's original text, such as the names of blocks or entities.

## Other Pages

* [Home](/README.md)