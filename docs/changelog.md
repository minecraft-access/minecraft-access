<!-- INSERT CHANGELOG HERE -->

## Release v1.9.0 (2024-12)
### New Features
- Speak current perspective when switched to [#314](https://github.com/khanshoaib3/minecraft-access/issues/314)
- Blocks and entities can now be narrated with Jade, providing much more detailed information.
- Add basic aim assist for bows (temporarily lock onto the nearest hostile mob when drawing a bow, play sounds indicating how much the bow has been drawn and if the target can be shot)
- Implement support for running  the mod on MacOS
- Each function in the access menu can now be bound as a key on your keyboard (all unbound by default aside from narrate target)
- HealthNHunger is now player status
- Made the readout of player status more concise which also delivers values faster
- This includes new information like air, frost exposure, armor, and absorption hearts
- Added a warning for when frost exposure gets above a configurable value (default 30%)
- Hold Alt+R to hear only conditional information like air and frost exposure
- Renamed enableHealthNHunger in the config to enablePlayerStatus (**THIS IS A BREAKING CHANGE**)

### Feature Updates
- [Vault](https://minecraft.wiki/w/Vault) and [Trial Spawner](https://minecraft.wiki/w/Trial_Spawner) are added as POI blocks [#306](https://github.com/khanshoaib3/minecraft-access/issues/306)
- Remove `Position Narrator Format` config since it seems duplicate with single number narrating formats
- The narration menu function key key is now deprecated, and has been replaced by the narrate target key as each thing can be bound independently
- The narrator menu was renamed to the access menu to avoid confusion with the existing narrator feature in Microsoft products
- **BREAKING CHANGES**: Rename **Narrator Menu** aka F4 Menu to Access Menu
- **Access Menu**: Access Menu functions can be triggered with Alt + upper number keys
- Your pets, other's pets, bosses, players, and vehicles now have their own sounds
- Dropped item detection also now extends to projectiles on the ground which can be picked up like arrows and tridents
- Air is now measured roughly in seconds remaining, as it is not clear how fast air ticks down for blind players usually
- The default air threshold is now 5 seconds of air remaining, which is roughly the same overall percentage (30%) air remaining
- The mod now announces the biome you entered when you join a world instead of the xp level.

### Bug Fixes
- Let `Enable Facing Direction` config controls auto direction speaking in `Camera Controls` [#327](https://github.com/khanshoaib3/minecraft-access/issues/327)
- Make the `Look Straight Back` key combination (left alt + numpad 5) works again [#328](https://github.com/khanshoaib3/minecraft-access/issues/328)
- You can now hold down keys without them speaking their output over and over
- Fixed smithing table slots being mislabeled
- Some neutral mobs are now recognized as hostile when angry like wolves, instead of always being passive
- Fix equipment narration triggering even if entity doesn't have equipment
- When the locked on block is destroyed, it will automatically be unlocked

### Others
- Added an error message when trying to run the mod server-side

### Translation Changes
- [Weblate](https://hosted.weblate.org/engage/minecraft-access/) is now used for translations.
- Removed coppied strings that were already in use in vanilla, mainly the seperate color strings that Minecraft access had which vanilla translation already provides.

### Support 1.21.4
This mod doesn't yet support new features such as [crafter](https://minecraft.wiki/w/Crafter) and [pale garden](https://minecraft.wiki/w/Pale_Garden), but at least you can play the latest version now! Compatibility for 1.21.4:
* Minecraft: 1.21.4 ([read setup guide](/docs/setup/basic.md)))
* Fabric Loader: >=0.16.9 ([download Fabric Loader](https://fabricmc.net/use/installer/))
* NeoForge: 21.4.33-beta ([download NeoForge installer](https://maven.neoforged.net/releases/net/neoforged/neoforge/21.3.4-beta/neoforge-21.3.4-beta-installer.jar))
* Java: >=21 ([download x64 Windows installer](https://download.oracle.com/java/21/latest/jdk-21_windows-x64_bin.msi), [download page for all Operating Systems](https://www.oracle.com/java/technologies/downloads/#java21))

Release v1.8.0 (2024-08)
---------------------------

### Bug Fixes

- Fix binding sprint to the `R` key isn't work [#310](https://github.com/khanshoaib3/minecraft-access/issues/310)
- Fix can't speak music discs name in inventory screen [#311](https://github.com/khanshoaib3/minecraft-access/issues/311)
- Improve vertical facing direction speaking [#313](https://github.com/khanshoaib3/minecraft-access/issues/313)
- Now the two methods of look straight up/down work like what manual says (single `.` and `0`, or hold `Right Alt` then press `Up` nad `Down`) . [#321](https://github.com/khanshoaib3/minecraft-access/issues/321)

### Others

- Game version upgrade to 1.21.1
- Add Braille Bennett's modpack to good resource page
- Update Windows Setup guide to catch up mod changes

### Development Chores

- Add new `CONTRIBUTING.md` doc


Release v1.7.0 (2024-07)
---------------------------

### Feature Updates

- Added better naming for unknown (modded) item groups in GUIs

### Bug Fixes

- Fixed detection of unknown (modded) item groups in GUIs
- Fix the problem that camera look straight up/down keys don't work [#298](https://github.com/khanshoaib3/minecraft-access/issues/298)
- Fix the problem that mod keybindings aren't shown in settings menu

### Others

- Update good resource page to include TrueBlindGaming tutorial videos, update beginner guide
- Switch from `MinecraftForge` to `NeoForge` from now on

### Development Chores

- Add 1.21 mod compatibility doc
- Change `forge` text to `neoforge` in workflows
- Change `1.20` text to `1.21` in docs
- Add command to replace links in changelog-for-platform to github links (in release workflow)

Release v1.6.1 (2024-06)
---------------------------

### Others

- New guide page: [Good Resources](/docs/good-resources.md)
- Remove `fabric-api` dependence for Fabric
- forge_version: `1.20.6-50.0.0` -> `1.20.6-50.1.0`
- architectury_loom_version: `1.6-SNAPSHOT` -> `1.7-SNAPSHOT`
- grade: `8.6` -> `8.8` (required by loom 1.7)
- fabric_yarn_version: `1.20.6+build.1:v2` -> `1.20.6+build.3:v2`
- Add mixin dependency `io.github.llamalad7:mixinextras:0.4.0` for forge

Release v1.6.0 (2024-06)
---------------------------

### New Features

- Speak variant of dog, cat, axolotl
- Speak animals and monsters currently wearing equipment
- Speak enchant cost in anvil [#277](https://github.com/khanshoaib3/minecraft-access/pull/277)
- Speak hovered tooltip when `Inventory Control` feature is disabled [#281](https://github.com/khanshoaib3/minecraft-access/pull/281)

### Bug Fixes

- Fix broken logic after upgrading to 1.20.6

### Translation Changes

- Add Italian translation thanks to Discord user Vabax_YT
- New field: entity wearing equipment format
- New field: animal variant format
- New field: variant of dog, cat, axolotl

### Others

- Remove `architectury-api` dependence
- minecraft_version: `1.20.4` -> `1.20.6`
- forge_version: `1.20.4-49.0.19` -> `1.20.6-50.0.31`
- architectury_loom_version: `1.4-SNAPSHOT` -> `1.6-SNAPSHOT`
- fabric_loader_version: `0.15.1` -> `0.15.11`

### Development Chores

- Add Fast-forward workflow.
- New `NamedFormatter` class with modifications to `I18NMixin`, for easier I18N translation

Release v1.5.4 (2024-03)
---------------------------

### Refactoring, Documentation and Chores

* Add [Sound Cue Listening Page](/docs/sounds.html) for making users refer to the sound cue they heard in the game.

Release v1.5.3 (2024-02)
---------------------------

### Feature Changes

* Now the Read Crosshair feature will only play the position sound cue once when player looks at different sides of same block.
* When locking on an entity, POI Locking feature will always try to target at a valid position that you can interact with, thus you can attack a monster even it expose small part of its body to you.
* Now POI Locking feature will never let you make eye contact with an [Enderman](https://minecraft.wiki/w/Enderman).
* [Boat](https://minecraft.wiki/w/Boat), [mine cart](https://minecraft.wiki/w/Minecart), [slime](https://minecraft.wiki/w/Slime), [magma cube](https://minecraft.wiki/w/Magma_cube), and [ancient debris](https://minecraft.wiki/w/Ancient_debris) can be scanned and locked with POI feature now. [#157](https://github.com/khanshoaib3/minecraft_access/issues/157) [#203](https://github.com/khanshoaib3/minecraft_access/issues/203) [#215](https://github.com/khanshoaib3/minecraft_access/issues/215)

### Bug Fixes

* Fix the bug that looking at fluid will make Read Crosshair spam. [#262](https://github.com/khanshoaib3/minecraft_access/issues/262)

Release v1.5.2 (2024-01)
---------------------------

### Bug Fixes

* Fix the bug that locking on Entity will immediately unlock [#248](https://github.com/khanshoaib3/minecraft_access/issues/248)
* Fix the bug that mod's INFO log will be logged twice in console and log files [#247](https://github.com/khanshoaib3/minecraft_access/issues/247)
* Fix the bug that speaking recipes in Inventory Screen will cause the game crush. [#251](https://github.com/khanshoaib3/minecraft_access/issues/251)

### Refactoring, Documentation and Chores

Now there are [three workflows](../.github/workflows):

* `test`: Triggered by PR, runs tests suite
* `build`: Manually triggered, builds mod files, you can download built files from action page
* `publish`: Manually triggered, creates GitHub release, publish mod files to Modrinth and CurseForge

For running `release` workflow, the repo must have `MODRINTH_TOKEN` and `CURSEFORGE_TOKEN` in repo's secret settings.
For running `build` workflow to build previous version of mod, [lang repo](https://github.com/khanshoaib3/minecraft-access-i18n) must have same version tag (git tag) as target build version.

Release v1.5.1 (2024-01)
---------------------------

### Feature Changes

* Add a new config `Speak Focused Slot Changes` with default `true` value for `Inventory Controls` feature. Close this config if you hear the mod is continuously repeating changes of item name. [#242](https://github.com/khanshoaib3/minecraft_access/issues/242)

Release v1.5.0 (2024-01)
---------------------------

### Feature Changes

* Breaking change: The mod will speak `unlock` on every unlock (used to be only on manually press unlock key). Config `Play Unlocking Sound` is replaced by [`Play Sound Instead Of Speak`](/docs/config.md#entitiesblocks-locking), default value is `false`, so if you find out speak is annoying, switch this config to `ture` for original base drum sound.

### Refactoring, Documentation and Chores

* Add [lombok](https://projectlombok.org/features/) (1.18.30) as `common` module dependency.
* Use lombok's `@Slf4j` instead of `MainClass.debug()` to let every class has its own logger.
* Change some log message's level to `DEBUG`, make these logs controlled by config `Debug Mode` (and tested in dev and prod environments).
* Use lombok's `@Getter` `@Setter` to replace `*ConfigMap`'s boilerplate code.

To developers: Please remove `.idea/runConfigurations` directory (which generated by `architectury-plugin`?) and refresh gradle project to let architectury to generate new `IntelliJ Run/Debug Configurations`, or starting `:forge:client` with resalt in an Exception: `java.lang.ClassNotFoundException: cpw.mods.bootstraplauncher.BootstrapLauncher`.

### Dependencies Changes

* minecraft_version: `1.20.1` -> `1.20.4`
* architectury_version: `9.0.8` -> `11.0.9`
* fabric_loader_version: `0.14.21` -> `0.15.1`
* fabric_api_version: `0.84.0+1.20.1` -> `0.93.1+1.20.4`
* forge_version: `1.20.1-47.0.19` -> `1.20.4-49.0.19`
* architectury_loom_version: `1.1-SNAPSHOT` -> `1.4-SNAPSHOT`

Release v1.4.3 (2024-01)
---------------------------

### Bug Fixes

* Fix the bug that you can't use `Left Alt` + `Center Camera Key` to look straight back. [#225](https://github.com/khanshoaib3/minecraft-access/issues/225)

Release v1.4.2 (2023-12)
---------------------------

### Bug Fixes

* Fix the bug that ReadCrosshair spam when the player in the fluid and looks at fluid. [#222](https://github.com/khanshoaib3/minecraft-access/issues/222)

Release v1.4.1 (2023-12)
---------------------------

### Bug Fixes

* Fix the bug that can not speak hotbar in latest version of forge (1.20.1-47.2.17) [#218](https://github.com/khanshoaib3/minecraft-access/issues/218)

### Feature Changes

* Breaking change: config `Speak Action Bar Updates` is renamed to `Speak Action Bar Messages`.
* New config `Only Speak Action Bar Updates` for only speak changed part of action bar message when the message is partially updated, useful for some mods like hypixel. [#220](https://github.com/khanshoaib3/minecraft-access/issues/220)

### Dependencies Changes

* forge_version: `47.0.19` -> `47.2.17`

Release v1.4.0 (2023-11)
---------------------------

### New Features

#### Speak Text Editing

* Now the mod will simulate the feedback you get when typing text in other software's input boxes, it will speak the text that you delete, select, pass with cursor, on Chat Screen, Book Edit Screen, Command Block Screen and so on.
* Suppress annoying original `speak whole sentence` narration on every text editing operation.
* Simplify the original command suggestion narration for a better typing experience with lesser annoying too-detailed narrations.

See also: [description of this feature](/docs/features.md#speak-text-editing) [#187](https://github.com/khanshoaib3/minecraft-access/issues/187)

#### I18N Fallback Mechanism

A fallback mechanism is added to I18n in case it fails on unsupported languages, or text that not translated in time in supported languages.
If any I18N failed on your language, the mod will use the English version instead.
Set the game to your familiar language is recommended, even if it's not supported by this mod, since you can still be benefited from translatable game original text, such as name of blocks or creatures.
If you want to contribute to translating work, we manage the translation of this mod in [another GitHub repository](https://github.com/khanshoaib3/minecraft-access-i18n#minecraft-access-i18n), take a look.

### Feature Changes

* Now whenever you're looking at a block and entity, the mod will play a piano sound cue to indicate relative location between you and target. see also: [description of this feature](/docs/features.md#relative-position-sound-cue) [#191](https://github.com/khanshoaib3/minecraft-access/issues/191)
* Now the mod will only speak item count when held item type isn't changed (when picking up or spending items), with a new config `Report Held Items Count When Changed`, see the [configuration](/docs/config.md#other-configurations).
* Now the mod will speak sitting state of cat, dog, parrot, [and so on](https://minecraft.wiki/w/Sitting#Mechanics) in `Read Crosshair`. [#192](https://github.com/khanshoaib3/minecraft-access/issues/192)
* Not the mod will speak if an animal is tamed in `Read Crosshair`.
* Now the mod can tell if a zombie villager is under curing in `Read Crosshair`. [#195](https://github.com/khanshoaib3/minecraft-access/issues/195)
* Now the mod will additionally speak an entity's type if it has a custom name given by the name tag.
* Move `Shearable` word in front of sheep information in `Read Crosshair`.
* Now the mod will tell if your [taming operation](https://minecraft.fandom.com/wiki/Taming) is successful or not.
* The default value of config `Disable Speaking Consecutive Blocks With Same Name` in feature `Read Crosshair` is changed from `true` to `false`, to help newcomers understanding the game better.

### Bug Fixes

* Fix the bug that keybindings can't be rebound to mouse 4,5 keys (mouse side keys) [#190](https://github.com/khanshoaib3/minecraft-access/issues/190)
* Fix the bug that a dead entity can be locked before it disappears. [#208](https://github.com/khanshoaib3/minecraft-access/issues/208)

### Dependencies Changes

### Refactoring, Documentation and Chores

* Now the change log should be added as a small independent markdown file (fragment) under `./docs/news`.
* Add introduction of missing chat message feature into manual.

Release v1.3.0 (2023-09)
---------------------------

### Feature Changes

* BREAKING CHANGE: Remove left and right mouse simulation keys in `Inventory Controls` and have the `Mouse Simulation` feature replace their functions. [#168](https://github.com/khanshoaib3/minecraft-access/issues/168)

### Bug Fixes

* Fix a bug that `PositionNarrator` speaks multiple times with single keystroke. [#164](https://github.com/khanshoaib3/minecraft-access/issues/164)
* Fix a bug that custom keybindings can not take effect immediately until restart the game. [#167](https://github.com/khanshoaib3/minecraft-access/issues/167)
* Fix a bug that all vanilla keys rebinding are invalid. [#171](https://github.com/khanshoaib3/minecraft-access/issues/171)
* Fix a bug that POI Entity config can not take effect immediately until restart the game.

### Refactoring, Documentation and Chores

* Rewrite big README into [manual structure](https://github.com/khanshoaib3/minecraft-access/blob/1.20/README.md) for better readability. [#134](https://github.com/khanshoaib3/minecraft-access/issues/134)

Release v1.2.2 (2023-09-12)
---------------------------

### Bug Fixes

* Fix camera straight up down without right alt [#159](https://github.com/khanshoaib3/minecraft-access/pull/159) [#160](https://github.com/khanshoaib3/minecraft-access/pull/160)

### Refactoring, Documentation and Chores

* Add `CHANGELOG.md` for persistent change log and `PULL_REQUEST_TEMPLATE.md` for reminding PR submitters.
* Move `PlayerPositionUtils`, `PositionUtils` into `utils.position` subpackage.
* Move `MouseUtils`, `OSUtils`, `KeyUtils` into `utils.system` subpackage.
* Add several reusable junit `*Extension` for testing.
* Add `Orientation` enum for representing directions, refactor related code to apply it.
* Add `IntervalKeystroke` for interval executed keystrokes.

Release v1.2.1 (2023-08-31)
---------------------------

### Changes and Fixes

* Remove `speak different slots with same item content` function in `held item (hotbar) narration`, since it will cause the mod to speak in more places it shouldn't, and there is no alternative way to reimplement this function. [#151](https://github.com/khanshoaib3/minecraft-access/pull/151) [#152](https://github.com/khanshoaib3/minecraft-access/pull/152)

Release v1.2.0 (2023-08-30)
---------------------------

### New Features

* Camera Look straight Up and Down. [#131](https://github.com/khanshoaib3/minecraft-access/pull/131) [#132](https://github.com/khanshoaib3/minecraft-access/pull/132) [#133](https://github.com/khanshoaib3/minecraft-access/pull/133) [#135](https://github.com/khanshoaib3/minecraft-access/pull/135)
* New Brazilian Portuguese Translation. <https://github.com/khanshoaib3/minecraft-access-i18n/pull/18>

### Changes and Fixes

* Improve held item (hotbar) narration: speak empty slots, speak different slots with same item content. [#121](https://github.com/khanshoaib3/minecraft-access/pull/121) [#122](https://github.com/khanshoaib3/minecraft-access/pull/122)
* The direction and power of `Redstone Wire` are now be spoken in `Read Crosshair` feature. [#142](https://github.com/khanshoaib3/minecraft-access/pull/142) [#143](https://github.com/khanshoaib3/minecraft-access/pull/143)
* Fix a bug that `Find Closest Water/Lava Source` will recognize the water/lava fall as a valid still fluid block. [#144](https://github.com/khanshoaib3/minecraft-access/pull/144) [#145](https://github.com/khanshoaib3/minecraft-access/pull/145)

Release v1.1.1-beta.1 (for 1.20.1) v1.0.4-beta.1 (for 1.19.3) (2023-08-05)
---------------------------

## Changes and Fixes

* Optimize screen groups order, in the order of interactive frequency, for reducing the number of user keystrokes. [#107](https://github.com/khanshoaib3/minecraft-access/pull/107) [#108](https://github.com/khanshoaib3/minecraft-access/pull/108)

Release v1.1.0 (for 1.20.1) v1.0.3 (for 1.19.3) (2023-08-02)
---------------------------

### New Features

* Simulate mouse with keyboard. [#94](https://github.com/khanshoaib3/minecraft-access/pull/94) [#95](https://github.com/khanshoaib3/minecraft-access/pull/95)
* Speak monster spawner mob type. [#97](https://github.com/khanshoaib3/minecraft-access/pull/97) [#100](https://github.com/khanshoaib3/minecraft-access/pull/100)

## Changes and Fixes

* Fixed speaking multiple items in one recipe book slot when item icon changed. [#79](https://github.com/khanshoaib3/minecraft-access/pull/79) [#80](https://github.com/khanshoaib3/minecraft-access/pull/80)
* Close the `Narrator Menu` if the F4 key is pressed while the menu is open. [#83](https://github.com/khanshoaib3/minecraft-access/pull/83) [#84](https://github.com/khanshoaib3/minecraft-access/pull/84)

Release v1.1.0-beta.2 (for 1.20.1) v1.0.2 (for 1.19.3) (2023-07-08)
---------------------------

### New Features

* Customizable partial speaking in `Read CrossHair`. [#69](https://github.com/khanshoaib3/minecraft-access/pull/69) [#70](https://github.com/khanshoaib3/minecraft-access/pull/70)

## Changes and Fixes

* Fix a bug that recipe book has no next page button clicking in `1.20`. [#71](https://github.com/khanshoaib3/minecraft-access/pull/71)
* Fixed inventory control in `1.20`. [#73](https://github.com/khanshoaib3/minecraft-access/pull/73)
* Fix mod crashing on loading wrong config.json [#76](https://github.com/khanshoaib3/minecraft-access/pull/76) [#77](https://github.com/khanshoaib3/minecraft-access/pull/77)
* Fix the failure that mouse click event is performed before mouse cursor moving event in Windows. [#65](https://github.com/khanshoaib3/minecraft-access/pull/65) [#66](https://github.com/khanshoaib3/minecraft-access/pull/66)
* Fix a bug about KeyBindingsHandler's initialization. [#63](https://github.com/khanshoaib3/minecraft-access/pull/63) [#64](https://github.com/khanshoaib3/minecraft-access/pull/64)

**Full Changelog**: <https://github.com/khanshoaib3/minecraft-access/compare/v1.1.0-beta.1...v1.1.0-beta.2>

Release v1.1.0-beta.1 (for 1.20.1) (2023-07-02)
---------------------------

### New Features

* Added sign's front and back text narration.
* Added torch flower and pitcher crop's ripe level info in `Read Crosshair`.

**Full Changelog**: <https://github.com/khanshoaib3/minecraft-access/compare/v1.0.1...v1.1.0-beta.1>

Release v1.0.1 (for 1.19.3) (2023-07-01)
---------------------------

### New Features

* Fall detector is back but in this version instead of speaking `warning fall detected`, a sound is played at block (Anvil Block Hit sound to be specific). There will be an alternate sound option in the next update.
* The mod now automatically setups the screen reader libraries. You would still need to download the other required mods (fabric api and architectury api).
* Narrator menu hotkeys - You can now trigger the narrator menu options without opening the menu. More about this [here](https://github.com/khanshoaib3/minecraft-access#narrator-menu).
* Added a mod configuration menu. You can open this menu from the `Open Config Menu` option in Narrator/F4 Menu.
* POI marking feature, with this feature you can select to only detect a specific type of block/entity, more on this [here](https://github.com/khanshoaib3/minecraft-access#points-of-interest)
* The mod now reports the items picked up when fishing.

## Changes and Fixes

* Chat message reader improvements, now you can use alt + number keys to speak last 9 chat messages.
* Added configs to disable speaking action bar updates, speak time in 12 hours format and to repeat speaking the block/entity name.
* Simplified Chinese translations.
* Added sculk sensor/catalyst/shrieker to poi blocks
* The group names in inventory controls are now internationalized.
* Added beehive info to the read crosshair feature.
* Added narration about crops ripe level and farmland wet state
* Fixed default narrator override in forge version.
* Fixed enchant name not being spoken when in non-creative world.
* Removed the power level info from comparators because it isn't working.

<details><summary>Auto Generated Release Notes</summary>
<p>

## What's Changed

* Added config option to speak time in 12 hours format by @khanshoaib3 in <https://github.com/khanshoaib3/minecraft-access/pull/21>
* Chat History Reader by @khanshoaib3 in <https://github.com/khanshoaib3/minecraft-access/pull/23>
* Misc by @khanshoaib3 in <https://github.com/khanshoaib3/minecraft-access/pull/24>
* Bug Fixes by @khanshoaib3 in <https://github.com/khanshoaib3/minecraft-access/pull/28>
* Auto Library Installer by @khanshoaib3 in <https://github.com/khanshoaib3/minecraft-access/pull/29>
* I18N SlotsGroup by @boholder in <https://github.com/khanshoaib3/minecraft-access/pull/30>
* Mainly move facing-direction functions by @boholder in <https://github.com/khanshoaib3/minecraft-access/pull/31>
* Reset previousQuery after every 5000ms by @boholder in <https://github.com/khanshoaib3/minecraft-access/pull/32>
* Fix some SlotGroups non-config-key groupNames. by @boholder in <https://github.com/khanshoaib3/minecraft-access/pull/34>
* Bee Hive Info by @khanshoaib3 in <https://github.com/khanshoaib3/minecraft-access/pull/40>
* Chore Narration improvements by @boholder in <https://github.com/khanshoaib3/minecraft-access/pull/38>
* Add FluidDetector config. by @boholder in <https://github.com/khanshoaib3/minecraft-access/pull/37>
* Config Menu by @khanshoaib3 in <https://github.com/khanshoaib3/minecraft-access/pull/45>
* Add narration about crops ripe level and farmland wet state. Refactor block narration methods. close [#44](https://github.com/khanshoaib3/minecraft-access/pull/44) by @boholder in <https://github.com/khanshoaib3/minecraft-access/pull/46>
* Add Narrator Menu hotkey and Narrator Menu number key listening by @boholder in <https://github.com/khanshoaib3/minecraft-access/pull/47>
* Add speak harvest of fishing feature, to be clear it will report what your picked up when you're holding a fishing rod. by @boholder in <https://github.com/khanshoaib3/minecraft-access/pull/52>
* Comparator info fix; Bug fix for redstone related blocks; Refactored i18n entries; by @khanshoaib3 in <https://github.com/khanshoaib3/minecraft-access/pull/53>
* Add speak harvest of fishing config by @boholder in <https://github.com/khanshoaib3/minecraft-access/pull/54>
* Fix debugMode dynamic config. close [#50](https://github.com/khanshoaib3/minecraft-access/pull/50) by @boholder in <https://github.com/khanshoaib3/minecraft-access/pull/57>
* Add marked poi scan feature by @boholder in <https://github.com/khanshoaib3/minecraft-access/pull/59>
* Refactor key related code by @boholder in <https://github.com/khanshoaib3/minecraft-access/pull/56>
* Fixes buggy Time Utils with camera and inventory controls key inputs. by @khanshoaib3 in <https://github.com/khanshoaib3/minecraft-access/pull/63>

**Full Changelog**: <https://github.com/khanshoaib3/minecraft-access/compare/v1.0.0...v1.0.1>
</p>
</details>

Beta 1.0.1-beta.2 (for 1.19.3) (2023-06-18)
---------------------------

## Changelog

Note that you may face issue in accessing the F4 menu, this can be resolved by deleting the mod's configuration file. You can delete the `minecraft-access` folder found in the `.minecraft\config\` folder to do this.*

* (By @boholder) Various i18n related improvements including internationalizing the group names in inventory controls and Chinese translations for various entries.
* Added a mod configuration menu. You can open this menu from the `Open Config Menu` option in Narrator/F4 Menu.
* (By @boholder) Added fluid detector configurations and a read crosshair config to repeat speaking the block/entity name.
* The mod now automatically setups the screen reader libraries. You would still need to download the other required mods (fabric api and architectury api).
* Added beehive info to the read crosshair feature.
* Fixed system messages not being read in chat menu.
* Fixed default narrator override in forge version.
* Fixed enchant name not being spoken when in non-creative world.
* Code refactoring, also by @boholder

<details><summary>Auto Generated Release Notes</summary>
<p>

## Auto Generated Release Notes

### What's Changed

* Bug Fixes by @khanshoaib3 in <https://github.com/khanshoaib3/minecraft-access/pull/28>
* Auto Library Installer by @khanshoaib3 in <https://github.com/khanshoaib3/minecraft-access/pull/29>
* I18N SlotsGroup by @boholder in <https://github.com/khanshoaib3/minecraft-access/pull/30>
* Mainly move facing-direction functions by @boholder in <https://github.com/khanshoaib3/minecraft-access/pull/31>
* Reset previousQuery after every 5000ms by @boholder in <https://github.com/khanshoaib3/minecraft-access/pull/32>
* Fix some SlotGroups non-config-key groupNames. by @boholder in <https://github.com/khanshoaib3/minecraft-access/pull/34>
* Bee Hive Info by @khanshoaib3 in <https://github.com/khanshoaib3/minecraft-access/pull/40>
* Chore Narration improvements by @boholder in <https://github.com/khanshoaib3/minecraft-access/pull/38>
* Add FluidDetector config. by @boholder in <https://github.com/khanshoaib3/minecraft-access/pull/37>
* Config Menu by @khanshoaib3 in <https://github.com/khanshoaib3/minecraft-access/pull/45>

### New Contributors

* @boholder made their first contribution in <https://github.com/khanshoaib3/minecraft-access/pull/30>

**Full Changelog**: <https://github.com/khanshoaib3/minecraft-access/compare/v1.0.1-beta.1...v1.0.1-beta.2>

</p>
</details>

Beta 1.0.1-beta.1 (for 1.19.3) (2023-04-30)
---------------------------

### New Features

* Simplified Chinese translations (thanks to @onion108 for this).
* Fall detector is back but in this version instead of speaking `warning fall detected`, a sound is played at block (Anvil Block Hit sound to be specific). Do let me know if you think that previous implementation was better :)

## Changes and Fixes

* Chat Message Reader, now you can use alt + number keys to speak last 9 chat messages (this atm does not include any system sent messages).
* Added config to disable speaking action bar updates.
* Added sculk sensor/catalyst/shrieker to poi blocks

**Full Changelog**: <https://github.com/khanshoaib3/minecraft-access/compare/v1.0.0...v1.0.1-beta.1>

Release 1.0.0 (for 1.19.3) (2023-02-05)
---------------------------

This marks the release of Minecraft Access. All the features from the previous mods have been implemented to this mod (except fall detector). Some have been changed or improved. Here are some noticeable changes:

### Noticeable Changes

* The mod now works with both fabric and forge now.
* The mod now uses [Tolk](https://github.com/ndarilek/tolk) library for accessing screen reader in Windows and [libSpeechdWrapper](https://github.com/khanshoaib3/libspeechdwrapper) for Linux.
* Inventory controls key mapping is changed and now supports all the menus (including creative inventory)
* Key mappings for camera controls have also been updated to better suit keyboard with no numpad/keypad.
* The feature that reads the crosshair target also gives additional information like whether a block is being powered by a redstone source, a door is open or closed, sheep's color, etc.
* The narrator menu now has a few more options including time of day which now works for servers/multiplayer also.
* There is no longer a dedicated menu for mod customization, instead we have to manually edit the config.json file. (more on this [here](https://github.com/khanshoaib3/minecraft-access#mod-configuration))

**Full Changelog**: <https://github.com/khanshoaib3/minecraft-access/compare/v0.1.6-beta...v1.0.0>

Beta Release 0.1.6 (for 1.19.3) (2023-01-07)
---------------------------

_Note that this will only work on 1.19.3 and for fabric it is recommended to use fabric api version 0.72.0 with fabric loader version 0.14.12. For forge, version 44.1.0 is recommended and both require architectury api to be installed as well (version 7.0.66 recommended)_

### Changelog

* Patched book screen.
    * Use Left alt + r to repeat the contents of current page
* Added health and hunger feature (keybind: R)
* Added fluid block detection in read crosshair feature
* Added player warnings feature
* Added point of interest feature with keybind to lock on to nearest entity or block. Use Y to lock and alt + Y to unlock.

**Full Changelog**: <https://github.com/khanshoaib3/minecraft-access/compare/v0.1.0-beta...v0.1.6-beta>

Beta Release 0.1.0 (for 1.19.3) (2022-11-20)
---------------------------

### Changelog

* Camera Controls
* Inventory Controls
* Read Block
* Position Narrator
* Facing Direction
* Biome Indicator

**Full Changelog**: <https://github.com/khanshoaib3/minecraft-access/compare/v0.0.1...v0.1.0-beta>
