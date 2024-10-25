# Minecraft Access

[![Weblate translation status badge](https://hosted.weblate.org/widget/minecraft-access/svg-badge.svg)](https://hosted.weblate.org/engage/minecraft-access/)

Minecraft Access is a [Minecraft](https://www.minecraft.net) mod that specifically helps visually impaired players play Minecraft.
It is an integration and replacement for [a series of previous mods](https://github.com/accessible-minecraft).
This mod primarily borrows the help of a screen reader to describe (narrate) the game interface, and incorporates sound cues to provide orientation perception in this 3D world.
Currently, this mod [has enough features](docs/faq.md#is-the-mod-enough-to-play-the-game-normally) to help visually impaired players play the game normally.

This mod supports:

* Game version `1.21`, `1.20.6`, `1.20.4`, `1.20.1`, `1.19.3`
* On [Fabric](https://fabricmc.net/use/installer/) and [NeoForge](https://neoforged.net) mod loaders
* On Windows and Linux operating systems ([Help us port the mod to macOS](https://github.com/khanshoaib3/minecraft-access/issues/22))
* Works despite the language setting of the game (though the mod-specific narration will [fall back to English](/docs/features.md#i18n-fallback-mechanism) if the mod does not support the language yet)

Each version of this mod will be pre-released on [GitHub](https://github.com/khanshoaib3/minecraft-access/releases) and announced in the [Playability Discord server](https://discord.gg/yQjjsDqWQX) first as a beta testing stage, after one week of feedback collection, the version will be released on [Modrinth](https://modrinth.com/mod/minecraft-access/versions) and [CurseForge](https://legacy.curseforge.com/minecraft/mc-mods/blind-accessibility/files). 

## Other Pages

* [Basic Setup](/docs/setup/basic.md)
* [Modpack Version Setup](/docs/setup/modpack.md)
* [Advanced Setup](/docs/setup/advanced.md)
* [Features](/docs/features.md)
* [Keybindings](/docs/keybindings.md)
* [Minecraft Access Configuration](/docs/config.md)
* [Sound Effects Used in the Mod](https://html-preview.github.io/?url=https://github.com/khanshoaib3/minecraft-access/blob/1.21/docs/sounds.html)
* [FAQ](/docs/faq.md)
* [Good Resources](/docs/good-resources.md)

## Useful Links

* [Playability Discord server](https://discord.gg/yQjjsDqWQX) - Join our Discord server if you want to chat with this mod's users and developers.
* [Primary developer's X account](https://x.com/shoaib_mk0) - You can follow the developer on X to get notified when a new update drops.
* [Patreon](https://www.patreon.com/shoaibkhan)

## Known Issues

1. The default narrator speaks even if the narrator is turned off.
2. (Linux only) xdotool is not recognised even if it is installed.
3. (Linux only) Minecraft says no narrator is available even if Flite is installed.

## Contributions

Any type of contribution is welcome:

* Be one of the first to try out new versions and help us find bugs and issues.
* Improve this mod's documentation for better readability and accessibility.
* Help us translate this mod into other languages via [Weblate](https://hosted.weblate.org/engage/minecraft-access/).
* Create more text or video tutorials about how to play the game with this mod ([examples](/docs/good-resources.md#gameplay-with-this-mod)).
* Make sound effects for this mod.
* For development contributions, please read [CONTRIBUTING.md](CONTRIBUTING.md) for more details.
