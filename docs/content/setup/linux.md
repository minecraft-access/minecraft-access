---
title: "Set up on Linux"
---

We don't have many Linux users yet, so all we can is listing some known Linux specific things:

- For installing Fabric on Linux, see [Fabric's installation guide](https://docs.fabricmc.net/players/installing-fabric).
- On Linux, the default game directory is `~/.minecraft` and the mod directory is `~/.minecraft/mods`.

## Additional Installation for Linux

Other than java, game and mod platform, we need to install a few extra things that this mod depends on after installing the mod to make this mod work properly if you’re using Linux.

1. We need [speech-dispatcher](https://freebsoft.org/speechd) to be installed for invoking the screen reader's API, it's also a dependency of [Orca](https://help.gnome.org/users/orca/stable/index.html.en) screen reader so if you're using Orca, ignore this step.

2. Although the mod overrides the library used for TTS, Minecraft still needs the `flite` library to be installed, so you can install it with your distro's package manager.

3. We also need to install [xdotool](https://github.com/jordansissel/xdotool) which is used for simulating the mouse actions. Follow the [instructions](https://github.com/jordansissel/xdotool#installation) to install it.
