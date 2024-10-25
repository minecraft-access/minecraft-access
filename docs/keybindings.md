# Minecraft Access Keybindings

This page contains all the keybindings added by the mod.

You can change these keybindings in the settings (open `Options...` then `Controls..` then `Key Binds..`), all keybinding setting groups that are provided by this mod have a `Minecraft Access:` prefix to differentiate them from the original keybinding settings.

You may find that some features have duplicate keys, such as I, J, K, L as the arrow keys in various features.
It's ok since the same key takes effect in different interfaces for different functions.

You may want to take a look at [all the original controls](https://minecraft.wiki/w/Controls#Java_Edition) as well.

## Table Of Contents

1. [Camera Controls](#camera-controls)
2. [Mouse Simulation](#mouse-simulation)
3. [Inventory Controls](#inventory-controls)
4. [Point of Interest](#point-of-interest)
5. [Position Narrator](#position-narrator)
6. [Health n Hunger](#health-n-hunger)
7. [Access Menu](#access-menu)
8. [Book Editing](#book-editing)
9. [Speak Chat Messages](#speak-chat-messages)

## Camera Controls

| Single Key               | Default Keybinding | Description                                                                                                                          |
|--------------------------|--------------------|--------------------------------------------------------------------------------------------------------------------------------------|
| `Look Up`                | I                  | Move the camera vertically up by the `Normal Rotating Angle` config value                                                            |
| `Alternate Look Up`      | Number Pad 8       | Same as `Look Up Key`, move the camera vertically up                                                                                 |
| `Look Right`             | L                  | Move the camera horizontally right by the `Normal Rotating Angle` config value                                                       |
| `Alternate Look Right`   | Number Pad 6       | Same as `Look Right Key`, move the camera horizontally right                                                                         |
| `Look Down`              | K                  | Move the camera vertically down by the `Normal Rotating Angle` config value                                                          |
| `Alternate Look Down`    | Number Pad 2       | Same as `Look Down Key`, Move the camera vertically down                                                                             |
| `Look Left`              | J                  | Move the camera horizontally left by the `Normal Rotating Angle` config value                                                        |
| `Alternate Look Left`    | Number Pad 4       | Same as `Look Left Key`, move the camera horizontally left                                                                           |
| `Look North`             | Number Pad 7       | Turn the camera to the north                                                                                                         |
| `Look East`              | Number Pad 9       | Turn the camera to the east                                                                                                          |
| `Look South`             | Number Pad 3       | Turn the camera to the south                                                                                                         |
| `Look West`              | Number Pad 1       | Turn the camera to the west                                                                                                          |
| `Center Camera`          | Number Pad 5       | Look straight ahead: Turn the camera to the closest of the eight cardinal directions and reset vertical angle to horizontal position |
| `Look Straight Up`       | Number Pad 0       | Turn the camera to the look above head direction                                                                                     |
| `Look Straight Down`     | Number Pad .       | Turn the camera to the look down at feet direction                                                                                   |
| `Speak Facing Direction` | H                  | Speak current horizontal facing direction                                                                                            |

| Key Combination                                     | Description                                                                                                                                         |
|-----------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------|
| `Left Alt` + `Look Up` or `Alternate Look Up`       | Move the camera vertically up by the `Modified Rotating Angle` config value                                                                         |
| `Left Alt` + `Look Right` or `Alternate Look Right` | Move the camera vertically right by the `Modified Rotating Angle` config value                                                                      |
| `Left Alt` + `Look Down` or `Alternate Look Down`   | Move the camera vertically down by the `Modified Rotating Angle` config value                                                                       |
| `Left Alt` + `Look Left` or `Alternate Look Left`   | Move the camera vertically left by the `Modified Rotating Angle` config value                                                                       |
| `Left Alt` + `Center Camera`                        | Look straight back: Turn the camera to the opposite of the closest of the eight cardinal directions and reset vertical angle to horizontal position |
| `Right Alt` + `Look Up`                             | Same as single `Look North Key` and single `Look Straight Up Key`, alternately move the camera to the north or up                                   |
| `Right Alt` + `Look Right`                          | Same as single `Look East Key`, turn the camera to the east                                                                                         |
| `Right Alt` + `Look Down`                           | Same as single `Look South Key` and single `Look Straight Down Key`, alternately move the camera to the south or down                               |
| `Right Alt` + `Look Left Key`                       | Same as single `Look West Key`, turn the camera to the west                                                                                         |
| `Left Alt` + `Speak Facing Direction Key`           | Speak current vertical facing direction                                                                                                             |

See also: [Feature Description](/docs/features.md#camera-controls), [Configuration](/docs/config.md#camera-controls)

## Mouse Simulation

| Single Key                | Default Keybinding | Description                                                                        |
|---------------------------|--------------------|------------------------------------------------------------------------------------|
| `Left Mouse Sim`          | [                  | Simulate left mouse key, default value of the original `Attack/Destroy` key        |
| `Middle Mouse Sim`        | \                  | Simulate middle mouse key, default value of the original `Pick Block` key          |
| `Right Mouse Sim`         | ]                  | Simulate right mouse key, default value of the original `Use Item/Place Block` key |
| `Mouse Wheel Scroll Up`   | ;                  | Simulate mouse wheel scroll up, switching items in hotbar forward                  |
| `Mouse Wheel Scroll Down` | '                  | Simulate mouse wheel scroll down, switching items in hotbar backward               |

See also: [Feature Description](/docs/features.md#mouse-simulation), [Configuration](/docs/config.md#mouse-simulation)

## Inventory Controls

| Single Key         | Default Keybinding | Description                                                                      |
|--------------------|--------------------|----------------------------------------------------------------------------------|
| `Menu Move Up`     | I                  | Focus to the slot above                                                          |
| `Menu Move Right`  | L                  | Focus to the slot right                                                          |
| `Menu Move Down`   | K                  | Focus to the slot below                                                          |
| `Menu Move Left`   | J                  | Focus to the slot left                                                           |
| `Change Group`     | C                  | Select next slot group                                                           |
| `Switch Tab`       | V                  | Select next tab                                                                  |
| `Toggle Craftable` | R                  | Switch between `show all` and `show only` craftable recipes in recipe book group |
| T                  | not re-mappable    | Select the search box or text box                                                |
| Enter              | not re-mappable    | Deselect the search box or text box                                              |

| Key Combination                 | Description                             |
|---------------------------------|-----------------------------------------|
| `Left Shift` + `Change Group`   | Select previous slot group              |
| `Left Shift` + `Switch Tab`     | Select previous tab                     |
| `Left Shift` + `Menu Move Up`   | Select previous page of the Recipe Book |
| `Left Shift` + `Menu Move Down` | Select next page of the Recipe Book     |

`Switch Tab`, `Toggle Craftable`, `T` key and `Enter` key only works when there is a corresponding component in the opened screen.
Recipe Book page turning only works when `Recipe Book Group` is selected.
Search on the [wiki](https://minecraft.wiki/?search) for the description of screens if you're not familiar with them.

See also: [Feature Description](/docs/features.md#inventory-controls), [Configuration](/docs/config.md#inventory-controls)

## Point of Interest

| Single Key    | Default Keybinding | Description                               |
|---------------|--------------------|-------------------------------------------|
| `Locking Key` | Y                  | Lock onto the closest POI block or entity |

| Key Combination                   | Description                                                |
|-----------------------------------|------------------------------------------------------------|
| `Alt` + `Locking Key`             | Unlock from the currently locked entity or block           |
| `Control` + `Locking Key`         | Mark the block or entity currently targeted with crosshair |
| `Control` + `Alt` + `Locking Key` | Unmark from the target                                     |

See also: [Feature Description](/docs/features.md#points-of-interest),
[Configuration](/docs/config.md#point-of-interest)

## Position Narrator

| Single Key              | Default Keybinding | Description                       |
|-------------------------|--------------------|-----------------------------------|
| `Speak Player Position` | G                  | Speak the player's x y z position |

| Key Combination  | Description               |
|------------------|---------------------------|
| `Left Alt` + `Z` | Speak the player's z-axis |
| `Left Alt` + `X` | Speak the player's x-axis |
| `Left Alt` + `C` | Speak the player's y-axis |

See also: [Feature Description](/docs/features.md#position-narrator), [Configuration](/docs/config.md#position-narrator)

## Health n Hunger

| Single Key                       | Default Keybinding | Description                                  |
|----------------------------------|--------------------|----------------------------------------------|
| `Speak Player Health and Hunger` | R                  | Speak the player's current health and hunger |

See also: [Feature Description](/docs/features.md#health-n-hunger), [Configuration](/docs/config.md#health-n-hunger)

## Access Menu

| Single Key                                              | Default Keybinding                          | Description                                                                                                                                                                                                                                                            |
|---------------------------------------------------------|---------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Open Access Menu                                        | F4                                          | Open or close the Access Menu                                                                                                                                                                                                                                          |
| `Narrate Target`                                        | B                                           | Narrates the thing you are looking at                                                                                                                                                                                                                                  |
| Number Keys                                             | not re-mappable                             | When Narrator Menu is opened, press number keys to execute corresponding desired functions, without pressing `Tab` several times to select                                                                                                                             |
| All Access Menu Functions                               | not bound by default                        | All functions in the access menu have unique keybinds that can be set in the game's controls settings menu. The only function that is bound by default is the narrate target function, and all other function keys are left up to you to bind if you want to use them. |

See also: [Feature Description](/docs/features.md#access-menu), [Configuration](/docs/config.md#access-menu)

## Book Editing

| Single Key                                       | Default Keybinding | Description                                                                                                 |
|--------------------------------------------------|--------------------|-------------------------------------------------------------------------------------------------------------|
| `Use Item/Place Block` (the original keybinding) | right mouse key    | Open the book editing screen while holding a [Book and Quill](https://minecraft.wiki/w/Book_and_Quill) item |
| Tab                                              | not re-mappable    | Select buttons                                                                                              |
| Space                                            | not re-mappable    | Click selected button                                                                                       |
| `Attack/Destroy` (the original keybinding)       | left mouse key     | Same as `Space` key, click selected button                                                                  |
| Page Up                                          | not re-mappable    | Switch to previous page                                                                                     |
| Page Down                                        | not re-mappable    | Switch to next page                                                                                         |
| `Done` button                                    | not re-mappable    | Save your unfinished work and quit editing screen                                                           |
| `Sign` button                                    | not re-mappable    | And enter a title for the book to make it permanently non-editable                                          |

See also: [Feature Description](/docs/features.md#book-editing)

### Speak Chat Messages

| Key Combination | Description                                                                                                                                    |
|-----------------|------------------------------------------------------------------------------------------------------------------------------------------------|
| `Alt` + `1`-`9` | Speak previous chat message (again) corresponding to the number, 1 for the most recent message, 2 for the second most recent message and so on |

This feature only works while the Chat Screen is open.
These keys aren’t re-mappable.
The chat message will be spoken when that message shows up, whether the sender is you or not.
These keys are used to repeat previous chat messages.

See also: [Feature Description](/docs/features.md#speak-chat-messages)

## Other Pages

* [Home](/README.md)