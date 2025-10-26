---
title: "Sound Effects Overview"
---

This page lists the sound effects used in the Minecraft Access mod.
Sounds are grouped by [feature].
Sounds can be played by pressing the play button.

## Read Crosshair

| Description                                                                                                                                                                                                                                                                                                                                                                             | Sound                                                 |
|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------|
| When you're looking at a block and an entity, the mod will play a sound cue to indicate the relative location between you and the target. Volume to represent distance, the louder the sound, the closer the distance. Pitch to represent altitude, the higher the pitch, the higher the target is relative to you. You can turn off this feature or change the sound volume in config. | {{< wikiSound "Note_block_harp_scale" >}} Piano sound |

## Points of Interest

### Scanner

| Description                                                             | Sound                                                                              |
|-------------------------------------------------------------------------|------------------------------------------------------------------------------------|
| Position of functional blocks without screens (e.g. [Door], [Ladder])   | {{< wikiSound sound="Note_block_bit" pitch=2 >}} Beeping electronic sound          |
| Position of functional blocks that have screens (e.g. [Crafting Table]) | {{< wikiSound sound="Note_block_banjo" pitch=0.5 >}} String sound                  |
| Position of [ore] blocks                                                | {{< wikiSound sound="Pop" pitch=.5 >}} Bubbling sound                              |
| Position of [dropped items]                                             | {{< wikiSound sound="Click" pitch=1.5 >}} Click sound                              |
| Position of [passive mobs] (animals)                                    | {{< wikiSound sound="Note_block_bell" pitch=0.5 >}} Low pitch bell sound           |
| Position of [hostile mobs] (monsters)                                   | {{< wikiSound sound="Note_block_bell" pitch=2 >}} High pitch bell sound            |
| Position of your pets                                                   | {{< wikiSound "Note_block_flute" >}} Flute sound                                   |
| Position of other player's pets                                         | {{< wikiSound "Note_block_cow_bell" >}} Cow bell sound                             |
| Position of bosses                                                      | {{< wikiSound sound="Note_block_pling" pitch=2 >}} High pitch electric piano sound |
| Position of other players                                               | {{< wikiSound "Note_block_icechime" >}} Chime sound                                |
| Position of vehicles                                                    | {{< wikiSound "Note_block_iron_xylophone" >}} Xylophone sound                      |
| Position of displays                                                    | {{< wikiSound "In" >}} Toast shown sound                                           |

### Object Tracker and Locking

| Description                               | Sound                                                        |
|-------------------------------------------|--------------------------------------------------------------|
| Unlocking action in POI locking feature   | {{< sound sound="Note_block_bass_drum_pitch2" >}} Drum sound |
| Position of current object tracker object | {{< wikiSound "Note_block_bell" >}} Bell sound               |

### Bow Aim Assist

| Description                                                               | Sound                                                     |
|---------------------------------------------------------------------------|-----------------------------------------------------------|
| Plays when target is visible, pitch indicates how much the bow is pulled  | {{< wikiSound "Note_block_pling" >}} Electric piano sound |
| Plays when target is obscured, pitch indicates how much the bow is pulled | {{< wikiSound "Note_block_bass" >}} String bass sound     |

## Misc Sounds

| Description                                                                                                | Sound                                                                       |
|------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------|
| Position with high drop-off                                                                                | {{< wikiSound "Stone_dig1" >}} Foot stamping sound                          |
| A first player warning threshold was reached (less evere in the case of warnings with multiple thresholds) | {{< wikiSound "Respawn_anchor_deplete1" >}} Shatter and woosh sound         |
| A severe player warning threshold was reached                                                              | {{< wikiSound "Anvil_land" >}} Metal clang sound                            |
| Plays when [attack indicator] is filled and is visible on screen                                           | {{< wikiSound sound="Note_block_hat" pitch=1.0 >}} High pitched clack sound |

### Chat

| Description                                    | Sound                                         |
|------------------------------------------------|-----------------------------------------------|
| Plays when sending or recieving a chat message | {{< wikiSound "Successful_hit" >}} Ding sound |

### Movement Sounds

| Description                        | Sound                                                                                        |
|------------------------------------|----------------------------------------------------------------------------------------------|
| Player begins to sneak             | {{< wikiSound sound="Shovel_flatten2" pitch=0.5 >}} Low pitched dirt crunch sound            |
| Player stops sneaking or sprinting | {{< wikiSound sound="Shovel_flatten2" pitch=0.9 >}} Medium pitched dirt chrunch sound        |
| Player begins sprinting            | {{< wikiSound sound="Shovel_flatten2" pitch=2.0 >}} High pitched and quick dirt crunch sound |

## Disclaimer

Most sounds are taken from Minecraft and are property of Mojang,
the [Minecraft Usage Guidelines] apply.

[feature]: {{% relref "/features" %}}
[Door]: https://minecraft.wiki/w/Door
[Ladder]: https://minecraft.wiki/w/Ladder
[Crafting Table]: https://minecraft.wiki/w/Crafting_table
[ore]: https://minecraft.wiki/w/Ore
[dropped items]: https://minecraft.wiki/w/Item_(entity)
[passive mobs]: https://minecraft.wiki/w/Mob?so=search#Passive_mobs
[hostile mobs]: https://minecraft.wiki/w/Mob?so=search#Hostile_mobs
[attack indicator]: https://minecraft.wiki/Cooldown
[Minecraft Usage Guidelines]: https://www.minecraft.net/en-us/usage-guidelines
