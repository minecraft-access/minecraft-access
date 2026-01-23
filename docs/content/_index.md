---
title: "Home"
---

Minecraft Access is a [Minecraft] mod that specifically helps visually impaired players play Minecraft.
It is an integration and replacement for [a series of previous mods][accessible-minecraft].
This mod primarily borrows the help of a screen reader to describe (narrate) the game interface,
and incorporates sound cues to provide orientation perception in this 3D world.
Currently, this mod [has enough features][features] to help visually impaired players play the game normally.

This mod supports:

* Game version `1.21`, `1.20.6`, `1.20.4`, `1.20.1`, `1.19.3`
* On [Fabric] and [NeoForge] mod loaders
* On Windows, Linux, and MacOS operating systems
  (on MacOS you may need to use an external monitor or decrease the GUI scale for inventory controls to work,
  but this is being worked on)
* On [Pojav Launcher] on iOS (Android is not supported yet)
* Works despite the language setting of the game
  (though the mod-specific narration will [fall back to English][i18n-fallback]
  if the mod does not support the language yet)

Each version of this mod will be pre-released on [GitHub] and announced in the [Playability Discord server]
first as a beta testing stage, after one week of feedback collection,
the version will be released on [Modrinth] and [CurseForge].

## Useful Links

* [Playability Discord server] - Join our Discord server if you want to chat with this mod's users and developers.

## Known Issues

Check [GitHub issues] for known issues.
If you are having problems with the mod, it is probably best to ask in the [Playability Discord server]
before creating an issue, unless you know for sure that it is a bug in the mod.

## Contributions

Any type of contribution is welcome:

* Be one of the first to try out new versions and help us find bugs and issues.
* Improve this mod's documentation for better readability and accessibility.
* Help us [translate] this mod into other languages.
* Create more text or video tutorials about how to play the game with this mod ([examples][tutorials]).
* Make sound effects for this mod.
* For development contributions, please read [CONTRIBUTING.md] for more details.

## Developer API
Information about the client-side API is available at its [Javadoc][client-javadoc].

[Minecraft]: https://minecraft.net
[accessible-minecraft]: https://github.com/accessible-minecraft
[features]: https://mcaccess.org/faq#is-the-mod-enough-to-play-the-game-normally
[Fabric]: https://fabricmc.net/use/installer/
[NeoForge]: https://neoforged.net
[Pojav Launcher]: https://pojavlauncher.app
[i18n-fallback]: {{% relref "/features#i18n-fallback-mechanism" %}}
[GitHub]: https://github.com/minecraft-access/minecraft-access/releases
[Playability Discord server]: https://discord.mcaccess.org/
[Modrinth]: https://modrinth.com/mod/minecraft-access/versions
[CurseForge]: https://legacy.curseforge.com/minecraft/mc-mods/blind-accessibility/files
[GitHub issues]: https://github.com/minecraft-access/minecraft-access/issues
[translate]: https://mcaccess.org/faq#how-can-i-contribute-to-i18n
[tutorials]: {{% relref "/good-resources#gameplay-with-this-mod" %}}
[CONTRIBUTING.md]: https://github.com/minecraft-access/minecraft-access/blob/dev/CONTRIBUTING.md
[client-javadoc]: /api/client/javadoc/
