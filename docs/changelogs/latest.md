[//]: # (Manually copy the latest.md to /docs/changelog.md, then copy the default.md to the latest.md at every release time.)

### New Features

- Speak current perspective when switched to [#314](https://github.com/khanshoaib3/minecraft-access/issues/314)
- Add basic aim assist for bows (temporarily lock onto the nearest hostile mob when drawing a bow, play sounds indicating how much the bow has been drawn and if the target can be shot)
Closes #212
Edit: https://github.com/khanshoaib3/minecraft-access-i18n/pull/39 should be merged before this because it contains fields required by this feature
- Implement mouse simulation on MacOS
- Implement speech support on MacOS with queuing and interruption support
- Add a speech settings config menu with a speech rate option for changing the speech rate on MacOS
- Warn the user if Minecraft has not been granted the accessibility permission, which is needed for mouse simulation to work
- Blocks and entities can now be narrated with Jade, providing much more detailed information.

### Feature Updates

- [Vault](https://minecraft.wiki/w/Vault) and [Trial Spawner](https://minecraft.wiki/w/Trial_Spawner) are added as POI blocks [#306](https://github.com/khanshoaib3/minecraft-access/issues/306)
- Remove `Position Narrator Format` config since it seems duplicate with single number narrating formats

### Bug Fixes

- Let `Enable Facing Direction` config controls auto direction speaking in `Camera Controls` [#327](https://github.com/khanshoaib3/minecraft-access/issues/327)
- Make the `Look Straight Back` key combination (left alt + numpad 5) works again [#328](https://github.com/khanshoaib3/minecraft-access/issues/328)
- Fixed smithing table slots being mislabele

### Translation Changes

- Add four items for speaking perspectives [I18N PR 37](https://github.com/khanshoaib3/minecraft-access-i18n/pull/37)
- Add translation identifiers for the speech settings and speech rate buttons in the configuration menu
- Add a translation identifier for the warning about the accessibility permission not being granted on MacOS
- Added 2 new keys for the smithing table and removed the now unused one
- [Weblate](https://hosted.weblate.org/engage/minecraft-access/) is now used for translations.

### Others

Updated modpack setup instructions to describe when users might want or need a screenreader such as NVDA or JAWS.
- Added an error message when trying to run the mod server-side

### Development Chores

- Refactor `PlayerPositionUtils`
- Enhance `build` workflow's edge case handling
- My pull request to the minecraft-access-i18n repository is needed for the extra text strings I added in this pull request
I have implemented speech and mouse simulation support for MacOS using JNA calls to the Objective C runtime and other native MacOS libraries, so no external libraries or tools need to be installed.
There is a corresponding pull request in the i18n repo that should be merged alongside this
- Removed unnecessary use of Fabric api in a few random places
- Deleted MainClassFabric because the mod does two completely different things depending on which side it's running on and a common entry point for both sides is not needed (and also unnecessarily complicates things with the server-side error message)
- The I18N submodule has been merged into the main repo to make code contributions involving translations easier.
