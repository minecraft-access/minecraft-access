# Frequently Asked Questions

## Table Of Contents

1. [Is the mod enough to play the game normally?](#is-the-mod-enough-to-play-the-game-normally)
2. [Can this mod be used to play multiplayer servers?](#can-this-mod-be-used-to-play-multiplayer-servers)
3. [Why is nobody answering my questions in the Discord?](#why-is-nobody-answering-my-questions-in-the-discord)
4. [Why is this mod updated slowly / the feature I asked for was not released in the new version?](#why-is-this-mod-updated-slowly--the-feature-i-asked-for-wasnt-released-in-the-new-version)
5. [Has this mod been uploaded to mod platforms?](#has-this-mod-been-uploaded-to-mod-platforms)
6. [Does this mod support X language?](#does-this-mod-support-x-language)
7. [How can I contribute to I18N?](#how-can-i-contribute-to-i18n)
8. [Self-help guide for abnormal situations](#self-help-guide-for-abnormal-situations)

## Is the mod enough to play the game normally?

Yes and no.
Overall, the mod is enough for basic playing,
but that doesn't mean you can play it as well as a sighted player, for now at least.

We can claim that the current functionality of the mod is enough for normal play,
given that even totally blind users have already played with the help of this mod.
Some of them are publishing their experiences,
you can listen to the uploaded videos at:
[Logic Pro X Gaming](https://www.youtube.com/@LogicProXGaming/search?query=minecraft) and [TrueBlindGaming](https://www.youtube.com/@TrueBlindGaming/search?query=minecraft).

The mod primarily borrows the help of a screen reader to describe the game interface
and incorporates sound cues to provide orientation perception in this 3D world.
Currently,
the Mod has implemented such functions as `Read Crosshair` (read out blocks and creatures you're pointing at),
`100% accessibility for all screens`, `Camera Controls`, and `Mouse Simulation` with keyboard,
`Points of Interest`
(scan nearby special blocks and creatures and lock on them),
and a new F4 `Access Menu` which contains assistant functions like `Read Light Level`,
`Find Closest Water Source`, etc.

You can play the game within the keyboard's main typing part (which means a number pad isn't necessary),
without a mouse.
You can walk around the world, gather materials, farm the land, raise animals, mine,
fight monsters, and even build buildings and Redstone machines.

The `No` part of the answer contains two perspectives.

First, this mod doesn't achieve 100% accessibility.
For example, you'll find rowing a boat is challenging (since the relevant feature hasn't been implemented yet).
You also can't directly recognize blocks that are placed together as a structure
(since you can't see the entire appearance of a group of blocks, and there's very little this mod can do about that).
This mod is gradually adding new features, but it's highly unlikely that Minecraft will ever achieve 100% accessibility.

Second, Minecraft, although it has a large number of players, is not easy to get started with.
You'll need to learn a lot of new concepts and knowledge to play it.
You can find some good tutorials in [this page](/docs/good-resources.md#tutorial-resources).

So if you haven't bought the game yet and know little about Minecraft, think again before paying.

## Can this mod be used to play multiplayer servers?

Yeah! This mod is a pure client-sided mod, which means you can play either single player or multiplayer with this mod without additional operations.
However, this also limits what the mod can do, and most of the possible changes that are powerful and convenient for visually impaired players often involve modifying server-side logic.
The mod is sticking to a pure client-side strategy for now.
Since if the mod was for both server-side and client-side,
that would mean you would have to install a mod on the server.
You can't achieve this if the server doesn’t belong to you.

## Why is nobody answering my questions in the Discord?

Sorry for the inconvenience. Here are some self-help materials.
If you have any questions about installing, please read the [set-up guide](/docs/setup/basic.md).
If you have any questions about original game functions, please search on the [wiki](https://minecraft.wiki/w/Special:Search?scope=internal) or just google it.
If you want to learn to play the game,
there are some detailed text tutorials on the [wiki](https://minecraft.wiki/w/Tutorials).
You can also listen to [Pixlriffs's Minecraft Survival Guide](https://www.youtube.com/watch?v=VfpHTJsn9I4&list=PLgENJ0iY3XBjmydGuzYTtDwfxuR6lN8KC) on YouTube,
or just google the question and search for answers on forums.
If you have any questions about this mod, please read the [documentation](/README.md).

## Why is this mod updated slowly / the feature I asked for wasn't released in the new version?

Uh, maintaining or updating this mod is a volunteer effort.
There are currently only two active developers.
Any type of contribution is welcome, please go to the [issues page](https://github.com/khanshoaib3/minecraft-access/issues) for more details.
If you know developers who might be willing to help contribute to this mod, please send them information about this mod and encourage them to do so.

## Has this mod been uploaded to mod platforms?

Yes, it has been uploaded to Modrinth as [Minecraft Access](https://modrinth.com/mod/minecraft-access) and CurseForge as [Blind Accessibility](https://www.curseforge.com/minecraft/mc-mods/blind-accessibility).

## Does this mod support X language?

See the [I18N Fallback Mechanism](/docs/features.md#i18n-fallback-mechanism) feature.

## How can I contribute to I18N?

We manage the translation of this mod with [Weblate](https://hosted.weblate.org/engage/minecraft-access/), take a look.

## Self-help guide for abnormal situations

Choose the section that fits your progress:

### Haven't run the game yet

I assume you have taken a look at the [setup guide](/docs/setup/basic.md).

If you’re on a path that the setup guide doesn't mention
(for example, you're not purchasing the game from the official site),
developers can't help you since they can't follow the same path.
But you can still ask users in the [Playability Discord server](https://discord.gg/yQjjsDqWQX)
(mc-help channel) for help,
maybe there are users that have already solved your problem before.
Then we can add your question and the working solution to the setup guide so everyone in the Future can benefit from it.

If your experience is different from the setup guide describes,
it means there is something wrong or misleading in the setup guide.
Describe which step you’re currently on and what you found that is not the same as the guide says.
Through talking to the developers and community of this mod, they can help figure out what's wrong and help you fix it.

### The game crashed when I started it from the launcher

Great!
Believe it or not, this is easier to solve than `abnormally running`,
as the game crashed because it KNEW something was wrong, better than knowing nothing.
If the crash pop up dialog says something about `mod incompatibility`,
please refer to [this section](/docs/setup/basic.md#update-the-game-and-mods) in the basic setup guide.

If you’re using Fabric, there will be crash report files under the `crash-reports` folder in your `.minecraft` folder,
developers need the latest `crash-...` file.
If you’re using NeoForge, there will be log files under the `logs` folder in your `.minecraft` folder,
developers need the `debug.log` file.
Please upload it to wherever the developers can view it,
for example, you can upload it to Discord channels while asking for help.

### The game is running but acts abnormal

#### Can't hear the game narration

If you're using Windows, please check:

1. There are no missing Tolk files under the game folder.
2. You're using one of Tolk's [supported screen readers](https://github.com/ndarilek/tolk?tab=readme-ov-file#supported-screen-readers), please note that Windows built-in Narrator is not supported by tolk.
3. You’ve set the [`Narrator`](https://minecraft.wiki/w/Options#Chat_Settings) game setting (under `Options`, `Chat Settings`) to the `Narrator narrates All` choice.

Now open the launcher and start the game,
when you're at the main screen (which has `singleplayer game`, `multiplayer game`...),
disable your screen reader
and enable the built-in [Windows Narrator](https://support.microsoft.com/en-us/windows/complete-guide-to-narrator-e4397a0d-ef4f-b386-d8ae-c172f109bdb1) feature.
Can you hear the menu selection narration through Windows Narrator?
If so the problem is in your screen reader, maybe the Narrator setting is wrong (for example, you should disable NVDA's
speak typed characters setting since it will interrupt the narration of this mod),
or there is a compatibility problem between it and `tolk`.

#### This mod doesn't work as expected

I will assume you have searched on the [Minecraft wiki](https://minecraft.wiki) or the Internet to make sure you understand the original game part of your question.
Please provide, at minimum, this information in the `mc-issues` channel of the Playability Discord:

* Your operation
* What you expected to happen or what mod docs step you were on
* What feedback the game and mod gave you
* In case you’re asked to provide log files: you can find them under the `logs` folder in your `.minecraft` folder, developers need the `debug.log` file, please upload it to where you asked your question.

It might be a bug in this mod, something in the mod docs is misleading, or you misunderstand how the game works.

## Other Pages

* [Home](/README.md)
