---
title: "Keybindings"
---

This page contains all the keybindings added by the mod.

You can change these keybindings in the settings (open `Options...` then `Controls..` then `Key Binds..`), all keybinding setting groups that are provided by this mod have a `Minecraft Access:` prefix to differentiate them from the original keybinding settings.

You may find that some features have duplicate keys, such as I, J, K, L as the arrow keys in various features.
It's ok since the same key takes effect in different interfaces for different functions.

You may want to take a look at [all the original game controls](https://minecraft.wiki/w/Controls#Java_Edition) as well.

## Camera Controls

| Single Key               | Default Keybinding | Description                                                                                                                          |
|--------------------------|--------------------|--------------------------------------------------------------------------------------------------------------------------------------|
| `Look Up`                | Number Pad 8       | Move the camera vertically up by the `Normal Rotating Angle` config value                                                            |
| `Look Right`             | Number Pad 6       | Move the camera horizontally right by the `Normal Rotating Angle` config value                                                       |
| `Look Down`              | Number Pad 2       | Move the camera vertically down by the `Normal Rotating Angle` config value                                                          |
| `Look Left`              | Number Pad 6       | Move the camera horizontally left by the `Normal Rotating Angle` config value                                                        |
| `Look North`             | Number Pad 7       | Turn the camera to the north                                                                                                         |
| `Look East`              | Number Pad 9       | Turn the camera to the east                                                                                                          |
| `Look South`             | Number Pad 3       | Turn the camera to the south                                                                                                         |
| `Look West`              | Number Pad 1       | Turn the camera to the west                                                                                                          |
| `Center Camera`          | Number Pad 5       | Look straight ahead: Turn the camera to the closest of the eight cardinal directions and reset vertical angle to horizontal position |
| `Look Straight Up`       | Number Pad Plus    | Turn the camera to the look above head direction                                                                                     |
| `Look Straight Down`     | Number Pad Enter   | Turn the camera to the look down at feet direction                                                                                   |
| `Speak Facing Direction` | Number Pad 0       | Speak current horizontal facing direction                                                                                            |

| Key Combination        | Description                                                                                                                                         |
|------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------|
| `Alt` + `Number Pad 8` | Move the camera vertically up by the `Modified Rotating Angle` config value                                                                         |
| `Alt` + `Number Pad 6` | Move the camera vertically right by the `Modified Rotating Angle` config value                                                                      |
| `Alt` + `Number Pad 2` | Move the camera vertically down by the `Modified Rotating Angle` config value                                                                       |
| `Alt` + `Number Pad 4` | Move the camera vertically left by the `Modified Rotating Angle` config value                                                                       |
| `Alt` + `Number Pad 5` | Look straight back: Turn the camera to the opposite of the closest of the eight cardinal directions and reset vertical angle to horizontal position |
| `Alt` + `Number Pad 0` | Speak current vertical facing direction                                                                                                             |

See also: [Feature Description]({{% relref "/features#camera-controls" %}}), [Configuration]({{% relref "/config#camera-controls" %}})

## Mouse Simulation

| Single Key                | Default Keybinding | Description                                                                        |
|---------------------------|--------------------|------------------------------------------------------------------------------------|
| `Left Mouse Sim`          | [                  | Simulate left mouse key, default value of the original `Attack/Destroy` key        |
| `Middle Mouse Sim`        | \                  | Simulate middle mouse key, default value of the original `Pick Block` key          |
| `Right Mouse Sim`         | ]                  | Simulate right mouse key, default value of the original `Use Item/Place Block` key |
| `Mouse Wheel Scroll Up`   | ;                  | Simulate mouse wheel scroll up, switching items in hotbar forward                  |
| `Mouse Wheel Scroll Down` | '                  | Simulate mouse wheel scroll down, switching items in hotbar backward               |

See also: [Feature Description]({{% relref "/features#mouse-simulation" %}}), [Configuration]({{% relref "/config#mouse-simulation" %}})

## Inventory Controls

| Single Key         | Default Keybinding | Description                                                                                                      |
|--------------------|--------------------|------------------------------------------------------------------------------------------------------------------|
| `Menu Move Up`     | I                  | Focus to the slot above                                                                                          |
| `Menu Move Right`  | L                  | Focus to the slot right                                                                                          |
| `Menu Move Down`   | K                  | Focus to the slot below                                                                                          |
| `Menu Move Left`   | J                  | Focus to the slot left                                                                                           |
| `Next Group`       | C                  | Select next slot group                                                                                           |
| `Next Tab`         | V                  | Select next tab                                                                                                  |
| `Toggle Craftable` | R                  | Switch between `show all` and `show only` craftable recipes in recipe book group                                 |
| `Fuel Status`      | U                  | Narrate the remaining percent on fuel and time until item is done being processed in furnaces and brewing stands |
| `Jump to Textbox`  | T                  | Select the search box or text box                                                                                |
| Enter              | not re-mappable    | Deselect the search box or text box                                                                              |

| Key Combination | Description                             |
|-----------------|-----------------------------------------|
| `Shift` + `C`   | Select previous slot group              |
| `Shift` + `V`   | Select previous tab                     |
| `Shift` + `I`   | Select previous page of the Recipe Book |
| `Shift` + `K`   | Select next page of the Recipe Book     |

`Switching Tabs`, `Toggling Craftable`, `Jumping to Textbox` and `Enter` keys only works when there is a corresponding component in the opened screen.
Recipe Book page turning only works when `Recipe Book Group` is selected.
Search on the [wiki](https://minecraft.wiki/?search) for the description of screens if you're not familiar with them.

See also: [Feature Description]({{% relref "/features#inventory-controls" %}}), [Configuration]({{% relref "/config#inventory-controls" %}})

## Point of Interest

| Single Key               | Default Keybinding | Description                                                                          |
|--------------------------|--------------------|--------------------------------------------------------------------------------------|
| `Next Item`              | Page Down          | Select next object in current group                                                  |
| `Previous Item`          | Page Up            | Select previous object in current group                                              |
| `Narrate current object` | Home               | Narrate current object tracker object                                                |
| `Target nearest object`  | End                | Target the nearest object relative to your current position, regardless of its group |
| `Locking Key`            | Y                  | Lock onto the block or entity that's currently being targetted by the object tracker |

| Key Combination         | Description                                                                          |
|-------------------------|--------------------------------------------------------------------------------------|
| `Control` + `Page Down` | Select next object tracker group                                                     |
| `Control` + `Page Up`   | Select previous object tracker group                                                 |
| `Control` + `End`       | Target the nearest entity relative to your current position, regardless of its group |
| `Shift` + `End`         | Target the nearest block relative to your current position, regardless of its group  |
| `Alt` + `Y`             | Unlock from the currently locked entity or block                                     |
| `Control` + `Y`         | Mark the block or entity currently targeted with crosshair                           |
| `Control` + `Alt` + `Y` | Unmark from the target                                                               |

See also: [Feature Description]({{% relref "/features#points-of-interest" %}}),
[Configuration]({{% relref "/config#point-of-interest" %}})

## Position Narrator

| Single Key              | Default Keybinding | Description                       |
|-------------------------|--------------------|-----------------------------------|
| `Speak Player Position` | V                  | Speak the player's x y z position |

| Key Combination  | Description               |
|------------------|---------------------------|
| `Left Alt` + `Z` | Speak the player's z-axis |
| `Left Alt` + `X` | Speak the player's x-axis |
| `Left Alt` + `C` | Speak the player's y-axis |

See also: [Feature Description]({{% relref "/features#position-narrator" %}}), [Configuration]({{% relref "/config#position-narrator" %}})

## Speak Player Status

| Single Key             | Default Keybinding | Description                                                                                |
|------------------------|--------------------|--------------------------------------------------------------------------------------------|
| `Speak Player Status`  | R                  | Speak the player's current health, hunger, armor, and air and frost exposure if applicable |
| `Narrate Held Item`    | \`                 | Narrate the item the player currently holds in their main hand                             |
| `Narrate Next Bossbar` | U                  | Narrates the next visible bossbar on screen                                                |

| Key Combination | Description                                                                   |
|-----------------|-------------------------------------------------------------------------------|
| `Alt` + `R`     | Speak only the conditional statuses of the player like air and frost exposure |
| `Control` + `R` | Speak currently active effects                                                |
| `Alt` + `\``    | Narrate the item the player currently holds in their offhand                  |
| `Shift` + `U`   | Narrates the previous visible bossbar on screen                               |

See also: [Feature Description]({{% relref "/features#player-status" %}}), [Configuration]({{% relref "/config#player-status" %}})

## Access Menu

| Single Key       | Default Keybinding | Description                                                             |
|------------------|--------------------|-------------------------------------------------------------------------|
| Open Access Menu | F4                 | Open or close the Access Menu                                           |
| `Narrate Target` | B                  | Narrates the thing you are looking at                                   |
| Upper number row | not re-mappable    | When Access Menu is opened, execute corresponding Access Menu functions |

| Key Combination          | Description                                                                                                                                                       |
|--------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `Alt` + Upper number row | Execute corresponding Access Menu functions. PS: the `Find Closest Water/Lava Source` functions are not supported by this key combination since they lag the game |

All functions in the Access Menu have unique keybindings that can be set in the game's controls settings menu.
The only function that is bound by default is the narrate target function, and all other function keys are left up to you to bind if you want to use them.

See also: [Feature Description]({{% relref "/features#access-menu" %}}), [Configuration]({{% relref "/config#access-menu" %}})

## Book Reading

| Single Key           | Default Keybinding | Description                               |
|----------------------|--------------------|-------------------------------------------|
| Repeat Page Contents | R                  | Repeats the text of the current book page |

## Speak Chat Messages

| Key Combination                                                      | Description                                                                                                                                    |
|----------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------|
| `Alt` + Number Keys (Upper number keys and key pad keys) (1 threw 0) | Speak previous chat message (again) corresponding to the number, 1 for the most recent message, 2 for the second most recent message and so on |
| `alt` + - or `alt` + Numer Pad Minus                                 | Go back a page in the chat history to the next set of 10 messages                                                                              |
| `alt` + `control` + - or `alt` + `control` + Number Pad Minus        | Move back in the chat history by 5 pages (if there is less than 5 pages left you will be moved to the last available page)                     |
| `alt` + = or `alt` + Number Pad plus                                 | Go forward a page towards the most recent message                                                                                              |
| `alt` + `control` + = or `alt` + `control` + Number Pad Plus         | Move back in the chat history by 5 pages (if there is less than 5 pages left you will be moved to the last available page)                     |
| `alt` + \` or `alt` + Number Pad Multiply                            | Go to the most recent page of messages                                                                                                         |
| `alt` + `control` + \` or `alt` + `control` + Number Pad multiply    | Go to the oldest page of messages available to your client                                                                                     |

This feature only works while the Chat Screen is open.
These keys aren’t re-mappable.
The chat message will be spoken when that message shows up, whether the sender is you or not.
These keys are used to repeat previous chat messages.

See also: [Feature Description]({{% relref "/features#speak-chat-messages" %}})

## Cloth Config Menu Controls

These controls work on any config menu that is powered by Cloth Config.

| Single Key         | Default Keybinding | Description          |
|--------------------|--------------------|----------------------|
| `Tab`              | not re-mappable    | Focus on next option |
| `Enter` or `Space` | not re-mappable    | Interact             |

| Key Combination          | Description                        |
|--------------------------|------------------------------------|
| `Shift` + `Tab`          | Focus on previous option           |
| `Ctrl` + `Tab`           | Switch to next config category     |
| `Ctrl` + `Shift` + `Tab` | Switch to previous config category |

## General

| Key Combination  | Description                                                                   |
|------------------|-------------------------------------------------------------------------------|
| `Alt` + `R` | Perform [`Menu Fix`]({{% relref "/features#menu-fix" %}}) feature if possible |
