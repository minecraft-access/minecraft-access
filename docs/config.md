# Minecraft Access Configuration

This page contains all the configuration that controls the features on-off and behavior.

The configuration can be modified in two ways: via the config menu or directly editing the configuration file.
The `Config Menu` can be opened via opening the `Access Menu` (press F4) then click `Open Config Menu` button.
The configuration file (named as `config.json`) can be found in the `{minecraft directory}/config/minecraft_access/` directory.
You can use Notepad or any text editor to edit the file.
You can also use online JSON editor like [Json Editor Online](https://jsoneditoronline.org/), [Json Formatter](https://jsonformatter.org/json-editor) or [Code Beautify JSON Online Editor](https://jsonformatter.org/json-editor).

If you want to reset the config back to default values, delete the configuration file while the game is not running,
or click `Reset Config` button in the `Config Menu`.

## Table Of Contents

1. [Camera Controls](#camera-controls)
2. [Mouse Simulation](#mouse-simulation)
3. [Read Crosshair](#read-crosshair)
    * [Relative Position Sound Cue](#relative-position-sound-cue)
    * [Partial Speaking](#partial-speaking)
4. [Inventory Controls](#inventory-controls)
5. [Point of Interest](#point-of-interest)
6. [Position Narrator](#position-narrator)
7. [Health n Hunger](#health-n-hunger)
8. [Player Warnings](#player-warnings)
9. [Fall Detector](#fall-detector)
10. [Access Menu](#access-menu)
    * [Fluid Detector](#fluid-detector)
11. [Other Configurations](#other-configurations)

## Camera Controls

| Configuration           | Default Value | Description                                                                                   |
|-------------------------|---------------|-----------------------------------------------------------------------------------------------|
| Enabled                 | true          | Whether to enable this feature                                                                |
| Normal Rotating Angle   | 22.5          | The rotation angle when we press the camera moving keys                                       |
| Modified Rotating Angle | 11.25         | The rotation angle when we press the camera moving keys while holding down the `Left Alt` key |
| Delay (in milliseconds) | 250           | Cooldown between two feature executions                                                       |

This config is under `Other Configurations` section in the config file and config menu.

| Configuration           | Default Value | Description                                                              |
|-------------------------|---------------|--------------------------------------------------------------------------|
| Enable Facing Direction | true          | Whether to automatically speak the current direction as the camera moves |

See also: [Feature Description](/docs/features.md#camera-controls), [keybindings](/docs/keybindings.md#camera-controls)

## Mouse Simulation

| Configuration                  | Default Value | Description                                         |
|--------------------------------|---------------|-----------------------------------------------------|
| Enabled                        | true          | Whether to enable this feature                      |
| Scroll Delay (in milliseconds) | 150           | Cooldown between two mouse wheel scroll simulations |

See also: [Feature Description](/docs/features.md#mouse-simulation), [keybindings](/docs/keybindings.md#mouse-simulation)

## Read Crosshair

| Configuration                                      | Default Value         | Description                                                                     |
|----------------------------------------------------|-----------------------|---------------------------------------------------------------------------------|
| Enabled                                            | true                  | Whether to enable this feature                                                  |
| Speak Block Sides                                  | true                  | Enable speaking of the side of block as well                                    |
| Disable Speaking Consecutive Blocks With Same Name | false                 | Disable speaking the block if the previous block was also same                  |
| Repeat Speaking Interval (in milliseconds)         | 0 (for tuning it off) | Repeat speaking for the given amount of time, even you haven't moved the camera |

Config `Disable Speaking Consecutive Blocks With Same Name` option can be useful
when you don't want to hear repetitive speaking of a large area with the same block type.

See also: [Feature Description](/docs/features.md#read-crosshair)

### Relative Position Sound Cue

| Configuration    | Default Value | Description                    |
|------------------|---------------|--------------------------------|
| Enabled          | true          | Whether to enable this feature |
| Min Sound Volume | 0.25          | Min volume of the sound cue    |
| Max Sound Volume | 0.4           | Max volume of the sound cue    |

See also: [Feature Description](/docs/features.md#relative-position-sound-cue)

### Partial Speaking

| Configuration   | Default Value                            | Description                                                                                                            |
|-----------------|------------------------------------------|------------------------------------------------------------------------------------------------------------------------|
| Enabled         | true                                     | Whether to enable this feature                                                                                         |
| White List Mode | true                                     | If true, only speak what have been configured. Set to false for blacklist mode, only not speak what you've configured  |
| Fuzzy Mode      | true                                     | Whether to do fuzzy matching, for example `bed` will match all colors of beds, `door` will match all textures of doors |
| Target Mode     | `block`                                  | Which type would you like to apply this feature to, either `all`, `entity` or `block`                                  |
| Targets         | [`slab`,`planks`,`block`,`stone`,`sign`] | Indicated what to be spoken                                                                                            |

The `Targets` config can only be configured in `config.json` file.
Values are written in Minecraft resource location format, the so-called
`snake_case` (consists of lowercase letters with underscores).
For example, the White Bed is written in `white_bed`.
There are lots of exceptions, the `Smooth Quartz Block` is written in `smooth_quartz`,
the `Block of Diamond` is written in `diamond_block`,
so please check the correct values in [this wiki link](https://minecraft.wiki/w/Java_Edition_data_values#Blocks)
(expand to show the list by clicking `Blocks[show]` then `Item form's ID[show]`).

See also: [Feature Description](/docs/features.md#partial-speaking)

## Inventory Controls

| Configuration                                                                             | Default Value | Description                                                                                                                                                        |
|-------------------------------------------------------------------------------------------|---------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Enabled                                                                                   | true          | Whether to enable this feature                                                                                                                                     |
| Auto Open Recipe Book                                                                     | true          | Automatically opens the recipe                                                                                                                                     |
| book on opening the inventory (in creative/survival inventory screen and crafting screen) |               |                                                                                                                                                                    |
| Row and Column Format in Crafting Input Slots                                             | `%dx%d`       | The speaking format of row and column prefix, two `%d` are representing the position of row and column                                                             |
| Speak Focused Slot Changes                                                                | true          | Speak if the content of focused slot changes, close it if you hear the mod is continuously repeating changes of item name, some mods like Hypixel will cause that. |
| Delay (in milliseconds)                                                                   | 250           | Cooldown between two feature executions                                                                                                                            |

Most recipes [require](https://minecraft.wiki/w/Crafting) their ingredients to be arranged in a specific way on the crafting grid (a.k.a. our crafting input group).
That's how `Row and Column Format in Crafting Input Slots` config can help you, you'll hear something like `1x2 Empty Slot` which represent you're locating at row one and column two slot inside the crafting input group, and it contains nothing.

See also: [Feature Description](/docs/features.md#inventory-controls), [keybindings](/docs/keybindings.md#inventory-controls)

## Point of Interest

See also: [Feature Description](/docs/features.md#points-of-interest),
[keybindings](/docs/keybindings.md#point-of-interest)

### Blocks

| Configuration                       | Default Value | Description                                                 |
|-------------------------------------|---------------|-------------------------------------------------------------|
| Enabled                             | true          | Whether to enable scanning blocks                           |
| Detect Fluid Blocks                 | true          | Enable detecting fluid blocks (water and lava)              |
| Range                               | 6             | Range of blocks to scan                                     |
| Play Sound                          | true          | Play a sound cue at positions of detected blocks            |
| Sound Volume                        | 0.25          | Volume of the sound cue                                     |
| Play Sound for Other Blocks as well | false         | Play sound cue for other blocks other than the ores as well |
| Delay (in milliseconds)             | 3000          | Execute at set intervals                                    |

### Entities

| Configuration           | Default Value | Description                                        |
|-------------------------|---------------|----------------------------------------------------|
| Enabled                 | true          | Whether to enable scanning entities                |
| Range                   | 6             | Range of entities to scan                          |
| Play Sound              | true          | Play a sound cue at positions of detected entities |
| Sound Volume            | 0.25          | Volume of the sound cue                            |
| Delay (in milliseconds) | 3000          | Execute at set intervals                           |

### Entities/Blocks Locking

| Configuration                           | Default Value | Description                                                                                  |
|-----------------------------------------|---------------|----------------------------------------------------------------------------------------------|
| Enabled                                 | true          | Whether to enable this feature                                                               |
| Lock on Blocks                          | true          | Enable lock on POI blocks as well, if false, only entities can be locked                     |
| Play Sound                              | true          | Play a sound cue at positions of detected entities                                           |
| Speak Relative Distance to Entity/Block | false         | Speak relative distance to the target when locking on                                        |
| Play Sound Instead Of Speak             | false         | Play a base drum sound cue on unlock instead of speak `unlock`                               |
| Auto Lock on to Eye of Ender when Used  | true          | Automatically lock on to the [Eye of Ender](https://minecraft.wiki/w/Eye_of_Ender) when used |
| Delay (in milliseconds)                 | 100           | Cooldown between two feature executions                                                      |
| Bow aim assist                          | true          | Enable automatic temporary locking onto the nearest monster when using a bow                 |
| Aim assist sound                        | true          | Enables audio cues for bow aim assist                                                        |
| Aim assist sound volume                 | 0.5           | Controls the volume of the aim assist audio cues                                             |

### Entities/Blocks Marking

| Configuration                      | Default Value | Description                                                             |
|------------------------------------|---------------|-------------------------------------------------------------------------|
| Enabled                            | true          | Whether to enable this feature                                          |
| Suppress Other POI When Marking On | true          | When marking on, suppress scanning and notifying on pre-configured ones |

## Position Narrator

These configs are under `Other Configurations` section in the config file and config menu.

| Configuration            | Default Value      | Description                                                                                          |
|--------------------------|--------------------|------------------------------------------------------------------------------------------------------|
| Enable Position Narrator | true               | Whether to enable this feature                                                                       |
| Position Narrator Format | `{x}x, {y}y, {z}z` | The speaking format of the position, `{x}`, `{y}`, `{z}` represent the corresponding axis's position |

See also: [Feature Description](/docs/features.md#position-narrator), [keybindings](/docs/keybindings.md#position-narrator)

## Health n Hunger

This config is under `Other Configurations` section in the config file and config menu.

| Configuration          | Default Value | Description                    |
|------------------------|---------------|--------------------------------|
| Enable Health n Hunger | true          | Whether to enable this feature |

See also: [Feature Description](/docs/features.md#health-n-hunger), [keybindings](/docs/keybindings.md#health-n-hunger)

## Player Warnings

| Configuration           | Default Value | Description                                                 |
|-------------------------|---------------|-------------------------------------------------------------|
| Enabled                 | true          | Whether to enable this feature                              |
| Play Sound              | true          | Play a sound cue when any of the status reach the threshold |
| Health Threshold First  | 6             | The first threshold for health                              |
| Health Threshold Second | 3             | The seconds threshold for health                            |
| Hunger Threshold        | 3             | The threshold for hunger/food                               |
| Air Threshold           | 3             | The threshold for air when you're submerged in water        |

See also: [Feature Description](/docs/features.md#player-warnings)

## Fall Detector

| Configuration           | Default Value | Description                         |
|-------------------------|---------------|-------------------------------------|
| Enabled                 | true          | Whether to enable this feature      |
| Range                   | 6             | Range of drop to scan               |
| Depth Threshold         | 4             | The threshold for playing the sound |
| Play Alternate Sound    | true          | Unused                              |
| Sound Volume            | 0.25          | Volume of the sound cue             |
| Delay (in milliseconds) | 2500          | Execute at set intervals            |

See also: [Feature Description](/docs/features.md#fall-detector)

## Access Menu

| Configuration | Default Value | Description                    |
|---------------|---------------|--------------------------------|
| Enabled       | true          | Whether to enable this feature |

See also: [Feature Description](/docs/features.md#access-menu), [keybindings](/docs/keybindings.md#access-menu)

### Fluid Detector

| Configuration | Default Value | Description                                                                                                  |
|---------------|---------------|--------------------------------------------------------------------------------------------------------------|
| Range         | 10            | Range of fluid to scan, be careful, the default 10 blocks will already make the game lag for a second or two |
| Sound Volume  | 0.25          | Volume of the sound cue                                                                                      |

## Other Configurations

| Configuration                          | Default Value | Description                                                                                                                                                               |
|----------------------------------------|---------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Enable Biome Indicator                 | true          | Whether to enable [`Biome Indicator`](/docs/features.md#biome-indicator) feature                                                                                          |
| Enable XP Indicator                    | true          | Whether to enable [`XP Indicator`](/docs/features.md#xp-indicator) feature                                                                                                |
| Command Suggestion Narrator Format     | `%dx%d %s`    | The speaking format of the command suggestion, two `%d` represent the order of focused suggestion and total number of suggestions, `%s` represents the suggestion content |
| Use 12 Hour Time Format                | false         | Whether to use 12 hour time format when speaking the time                                                                                                                 | 
| Speak Action Bar Messages              | true          | Whether to speak the messages updated in [action bar](https://minecraft.wiki/w/Commands/title), useful when you're in modded multiplayer servers                          |
| Only Speak Action Bar Updates          | false         | Only speak changed part of action bar message when the message is partially updated, useful for some mods like Hypixel                                                    |
| Speak Harvest Of Fishing               | true          | Whether to speak the harvest of fishing                                                                                                                                   |
| Report Held Items Count When Changed   | true          | Whether to report the number of held items when it changed                                                                                                                |
| Enable Menu Fix                        | true          | Whether to reset the cursor position when opening a menu (to prevent speak out unnecessary content)                                                                       |
| Debug Mode                             | true          | Developer config, whether to print debug messages into log                                                                                                                |
| Multiple Click Speed (in milliseconds) | 750           | The maximum time interval between two keystrokes in multiple click operations like `double-click`                                                                         |

See also: [Feature Description](/docs/features.md#other-small-features)

## Other Pages

* [Home](/README.md)