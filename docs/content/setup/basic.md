---
title: "Mod Setup"
---

Currently, this mod supports the Windows, Linux, and MacOS operating systems
(on MacOS you may need to use an external monitor or decrease the GUI scale for inventory controls to work, but this is
being worked on).
The latest version of this mod (as well as other mod dependencies) can be downloaded at the [releases page].


## Tutorial for Beginners: From Purchasing the Game To Installing This Mod

This tutorial tries to guide you step by step on how to set the whole thing up.

If you want to chat with this mod's users and developers, please join [our Discord server].
If you know where this tutorial could be improved, please also let us know.

> The paragraphs in the block quotes are additional nonsense.
> They don’t provide help with the installation,
> but they can give you a little more insight into things that are relevant.
> If you don't want to read them, skip them and move on, you won't miss anything important.

## Purchase the Game and Download the Launcher

### Purchase the Game

There are two editions of Minecraft. Minecraft: Java Edition and Minecraft: Bedrock Edition
(usually just referred to as Minecraft).
This Mod is only available for the Java Edition.
Don't worry about buying the wrong one, the two editions are sold in a bundle.

Before you purchase the game,
make sure you have a computer that meets the [minimum requirements] of Minecraft: Java Edition!

> While we needn't concern ourselves with the Bedrock Edition,
> I would like you to know the differences between the two editions:
>
> At one time Minecraft only had one edition, the Java Edition.
> Since Microsoft acquired Mojang Studios, they’ve launched a new Bedrock Edition.
> The Java Edition is available for PC (Windows, Mac, and Linux),
> while the newer Bedrock Edition is available for windows, smartphones, game consoles, etc.
>
> Please also note that the Bedrock Edition in the next purchase link only contains the version that runs on Windows.
> Bedrock Edition on other platforms, such as on smartphones,
> needs to be purchased separately in the platform's respective application store.

If you have a redeem code for this game, [here is the redeem page][redeem], no need to buy the game.
Or if you have Xbox Game Pass, you can also get Minecraft using your active subscription.

There are currently two purchasable versions of Minecraft, [the Deluxe Collection] and [the bare game].
It is worth noting that things that the Deluxe Collection has included that the bare game doesn't are just virtual
currency (Minecoins) and items only usable in the Bedrock Edition that we can't use in the Java Edition.

The links will jump to the pages that correspond to the language of your browser, and there is a `CHECKOUT` button.
Click it,
and then the webpage will redirect you to the payment flow, then it will require you to sign in with Microsoft.
Your purchase will be tied to a Microsoft account;
when you log into the launcher, you also need to log into this account.
Keep going after you’ve logged in, fill in the purchase information, and click on `confirm`, that's all.

### Use a Modpack Version of the Mod

If this is your first time playing Minecraft, it may be desirable to use a premade,
community created modpack with essential mods and configurations that are set up out of the box.
You can find a list of available packs [here][modpacks].
If you choose to utilize a modpack, there is no need to return to this guide after you finish that one,
as it is not relevant to modpack users.

### Download the Launcher

> You can use the launcher to choose which version of Minecraft you want to run
> or to modify the game launch configuration.
> There are many kinds of launchers,
> anyone can write their own launcher to provide a customized Minecraft game launching experience.
> Microsoft provides The launcher we will download, let's call it the official launcher.

At the end of the purchase process there will be a `Download Game` button to download the launcher.
If you've missed it, [here is the launcher installer download page][minecraft-download].

On Windows, the launcher installer is a normal executable file, run it and wait for it to finish.
On MacOS, the launcher is a DMG file. Open it, then copy the Minecraft application bundle by pressing `command+C` on it,
then open the `Applications` folder with `command+shift+A`, then paste the application you just copied with `command+V`.
On Linux, there will be instructions for various distributions linked further down the downloads page.
If the installation is successful, you will now have a new application called `Minecraft Launcher`.

The next steps require starting the launcher once first before installing the mod loader to let it generate the game
folder, so try [starting the launcher](#start-the-game), then start Minecraft: Java Edition.
Something may need to be prepared every time the launcher starts,
especially the first time you launch it as it has to install the entire game.
This may take a few minutes,
and you can listen to the screen reader's progress bar sound effect to find out the progress.
The first time you start the launcher, it will require you to sign in with your Microsoft account.

## Dependencies for Speech

The mod uses [whisp-rs] for speech output on every platform.
The required libraries and an eSpeak NG fallback are bundled inside the mod jar,
so no extra files need to be downloaded into the game folder.

On Windows, the mod will speak through whichever screen reader you have running (NVDA or JAWS) and fall back to OneCore, SAPI, then eSpeak NG if none is running.
NVDA is free and open source while JAWS is proprietary and costs money.

On Linux, the mod uses [Speech Dispatcher] when it is available.
You should be able to install it with your distribution's package manager,
probably under the name `speech-dispatcher` or `speechd`.
If you do not hear speech,
run the `spd-say test` command before you start the game to make sure Speech Dispatcher has started and is working.
If you do not hear anything after running that command,
check your Speech Dispatcher configuration in either `~/.config/speech-dispatcher/` or `/etc/speech-dispatcher/`,
and kill the `speech-dispatcher` process before testing again. If you use Orca, it will already be installed and set up.
If Speech Dispatcher is not available the mod will fall back to the bundled eSpeak NG.

## Choose and Install Your Chosen Mod Loader

> Like any other game, the mod loader is responsible for loading the mod files into the game at launch.
> The two mod loaders that Minecraft Access currently supports are Fabric and NeoForge.


Mods for Fabric and NeoForge aren’t compatible with each other.
Also, the same loader for a certain game version is almost always not compatible with mod files which support other game
versions.
E.G.: if I install a mod for 1.17, it usually will not work on later or earlier versions of the game like 1.16 or 1.18.

Which one should you choose? Fabric or NeoForge?
Fabric is the preferred loader for most of this mod's users,
as it has a simple installer and fast updates to the latest game version.
If you’re new to Minecraft, Fabric will be easier to set up and provide you with all the mods you will need.

### Install Fabric

Fabric is the recommended loader for new players who’re using this guide.
The Fabric mod loader can be downloaded [here][fabric].
Fabric provides an executable installer for Windows, click the `Download for Windows` button to download it.
The executable file is named `fabric-installer-<installer-version-number>.exe`,
for example, `fabric-installer-0.15.0.exe`.
We’ve received reports that some screen readers such as NVDA can’t read the installer's interface.
If your current input method is not English.
Try to switch to the English input method first,
if the problem still persists,
please download the jar form of the installer by clicking the `Download universal jar` button,
just below the `Download for Windows` button.

If you are on Linux or MacOS,
download the jar version of the installer using the "Download universal jar" button, and execute it.
It will be named the same as the `.exe` file except for the file extension,
for example, `fabric-installer-0.15.0.jar`.
You must have Java installed for this to work.
On MacOS, download Java from the [downloads page][adoptium].
Make sure to choose aarch64 if you are on Apple Silicon and x86_64 if you are on Intel.
On Linux, google how to install OpenJDK on your distribution.

Start the installer, a window pops up for you to choose the installation configurations:

1. The `Client` tab is selected by default, don't change it.
2. The first combo box is for selecting the game version you want to install,
   please refer to the current game version that is supported by this mod on the [releases page]
   (under the `Mod Version Compatibility` section), and change the combo box to select that game version.
3. Next, there is a checkbox for showing game snapshot versions; you can ignore it.
4. The second combo box is for selecting the Fabric loader version, the latest version is selected by default,
   you should also not touch this either.
5. Then there is an input field for specifying the installation location,
   the installer will recognize the correct folder automatically if you’ve been following this guide correctly,
   for example `C:\Users\username\AppData\Roaming\.minecraft`,
   that's where we want it to install so no need to change this either.
   Copy this path to somewhere like Notepad, TextEdit, or Pluma for further usage when installing mod files.
6. And then a checkbox for `Create profile`, checked by default,
   we want it to create a profile in the official launcher, so there is no need to change this option either.
7. Finally, click the `Install` button to start the installation.
   It will download some files from the Internet, if the network is fast, the installation takes less than a minute.
   A pop-up will show up to notify you that the installation is successful.

### Install NeoForge

NeoForge is for advanced users who wish to heavily modify their game; the installation process for it is more complex.
See the [advanced guide] for some more detailed instructions for installing Java, NeoForge,
and having more than one mod directory.

## Install Your Mods

There are several popular mod download platforms,
such as [Modrinth] and [CurseForge];
generally, mod developers will release their work on multiple mod listing platforms at the same time.
Mod developers will release mod files that are compatible with different game versions.
Please always keep this in mind when downloading mods, as incompatible mods will crash the game at startup.
It is worth mentioning that some mods can support multiple versions with one same mod file,
and generally this will be mentioned in the mod file name.
It is also good to note that all mod files have the `.jar` extension,
but some downloader or browsers may download these files in a compressed format like `.zip`,
in this case you will have to extract the real mod file from the compressed one.

Neither mod loaders nor the original game provides the ability to manage mod files; you need to manage them manually.
This guide will describe how to download and install mods manually;
you can also manage them automatically with an extra application such as
[Modrinth][modrinth-app] or [CurseForge][curseforge-app].

Now let's download mod files.
To download this mod and the dependencies of this mod,
it is recommended you download them from the Minecraft Access's official [releases page],
where you can find download links of suitable versions of the required mods,
under the `Mod Version Compatibility` section of each release.
By the way, you may be interested in the mods provided in the [good resources] page of the documentation,
they are good mods that our visually impaired users have found and tested through practice.

After all the mods you want are downloaded,
you can move on to putting them into the right location for the mod loader to recognize them.
The default path on Windows is: `%appdata%\.minecraft\mods`
(the `%appdata%` is a shortcut for `C:\Users\username\AppData\Roaming`),
you can directly paste it into File Explorer then press the enter key to jump to it or paste it into the run box
accessed with the Windows+R keys and click enter.
Note that on Linux, the default game directory is `~/.minecraft` and the mod directory is `~/.minecraft/mods`.
On MacOS, the default game directory is `~/Library/Application Support/minecraft`
and the mod directory is `~/Library/Application Support/minecraft/mods`.
Press `command+shift+G` to open `Go to Folder`, then type or paste this folder path and press enter.
We'll put downloaded mod files inside it,
both Fabric and NeoForge will load mods from this folder by default if installed.
You can look up how to use alternate installation locations if you wish to maintain both Fabric and NeoForge
installations at the same time.
You will need to start the game once after installing the mod loader for the `mods` folder to be created.

## Start the Game

The first time you start the launcher, it requires you to sign in with your Microsoft account.

The launcher's sidebar contains not only `Minecraft: Java Edition`, it also includes `Minecraft for Windows`,
and even two derivative games of Minecraft.
The sidebar also contains an entry for the settings of the launcher itself.
If you want to change your username or player appearance, for Java Edition,
you need to go to the launcher settings screen, `Accounts` tab, select your account, `More Options` button,
`Manage Minecraft: Java Edition profile`, then a web page will be opened in your browser,
which leads you to the profile management page.

The main screen of `Minecraft: Java Edition` in the launcher contains four tabs:

* `Play` - Where you can choose which profile to play and start the game.
* `Installations` - Where you can manage different game profiles for different game versions or game configurations.
* `Skins` - You can upload skin files here to change your appearance in game.
* `Patch Notes` - Update logs about the game.

Select the profile created by the mod loader installer and start it, for Fabric,
the profile name is `fabric-loader-<game-version>`,
for NeoForge, the name is simply `NeoForge` but the subtitle contains the game version.
If everything has worked correctly, you can start exploring the game.

When you launch the game for the first time, you should hear a message that says `Press enter to enable narrator`.
If you hear this, press enter before closing the game again.
If you don't hear this message, or want to make sure the narrator is enabled,
you can delete `options.txt` from the game directory (the `mods` folder without `\mods` or `/mods`).
In that case, the narrator should be enabled by default when you first launch the game with Minecraft Access.
If you do not hear speech, press `Control+B` to enable the narrator,
keep pressing until it switches to `Narrator Narrates All`.

If the game crashes and a bunch of error logs pop up,
the biggest probability is that there is an incompatibility between the mod files and mod loader,
or between the mod files and game version.
Please read the [Update the Game and Mods](#update-the-game-and-mods) section.
And here is a [complex self-help FAQ] for you to address the problem,
from not being able to hear the narration to a full game crash.

## Changing Speech Settings

By default, Minecraft Access will use whatever speech settings the active back-end provides:
on Windows that is the active screen reader (NVDA or JAWS) or your OneCore or SAPI defaults,
on Linux it is your Speech Dispatcher configuration
(usually found in `~/.config/speech-dispatcher/speechd.conf` or `/etc/speech-dispatcher/speechd.conf`,
and Speech Dispatcher must be restarted for changes to take effect),
and on MacOS it is the voice configured in System Settings > Accessibility > Spoken Content.

To override the speech rate, volume, or pitch from the mod's own configuration menu:

1. Open Minecraft and enter a world
2. Press F4 followed by 0 to open the config menu
3. Press Control+Tab until you hear "Speech Tab"
4. Press tab until you reach "Rate", "Volume", or "Pitch"
5. Type in a value between 0 and 100, or leave the field blank to use the back-end's default
6. Exit the config menu by pressing escape

## Update the Game and Mods
For [Fabric](#install-fabric),
run the fabric installer again and change the installed game version in the Fabric installer screen,
for [NeoForge](#install-neoforge), download another installer for the game version you want and install it.
You need to check all mod files in the `mods` folder for compatibility with the new game version,
and replace or remove incompatible mods.

To update mods, first recall what is mentioned above:

1. Mods of Fabric and NeoForge aren’t compatible with each other.
2. The same loader for a certain game version is almost always not compatible with mod files which support other game
   versions.
   E.G.: if I install a mod for 1.17,
   it usually will not work on later or earlier versions of the game like 1.16 or 1.18.
3. By default, both Fabric and NeoForge load mods from the folder `%appdata%\.minecraft\mods` on Windows,
   `~/.minecraft/mods` on Linux, and `~/Library/Application Support/minecraft/mods` on MacOS.

Here are some examples to help you understand:

* Fabric loader with any NeoForge mod file in the `mods` folder, will crash, and vice versa.
* Fabric loader for game version `1.19.3`, with a Fabric mod file for game version `1.20.1` will crash.
* All mod files in the `mods` folder need to be conflict-free for the game to start.

Based on these conditions, if you want to update some mod versions while keeping the game version unchanged,
you should replace the corresponding mod files.
For mods that have dependencies like Minecraft Access,
you also need to be aware if any of the dependent mods' versions have changed.

Another situation that causes the game to crash or misbehave is a conflict between mods,
which is challenging to show in the error log because it is hard to predict.
For this type of error, there is a simple yet cumbersome method for all modded games:
move all mod files out of the Mods folder and into a temporary folder,
then add them back to the Mods folder one by one, try to start the game every time you move back a file.

[releases page]: https://github.com/minecraft-access/minecraft-access/releases/latest
[our discord server]: https://discord.mcaccess.org/
[minecraft]: https://www.minecraft.net/en-us/store/minecraft-java-bedrock-edition-pc
[minimum requirements]: https://help.minecraft.net/hc/en-us/articles/4409225939853-System-Requirements-for-Minecraft-Java-Edition
[redeem]: https://www.minecraft.net/en-us/redeem
[the Deluxe Collection]: https://www.minecraft.net/en-us/store/minecraft-deluxe-collection-pc
[the bare game]: https://www.minecraft.net/store/minecraft-java-bedrock-edition-pc
[modpacks]: {{% relref "/good-resources#community-modpacks" %}}
[minecraft-download]: https://www.minecraft.net/download
[NVDA]: https://nvaccess.org/download/
[JAWS]: https://support.freedomscientific.com/Downloads/JAWS
[Speech Dispatcher]: https://freebsoft.org/speechd
[whisp-rs]: https://github.com/minecraft-access/whisp-rs
[fabric]: https://fabricmc.net/use/installer/
[adoptium]: https://adoptium.net/temurin/releases/?os=any&arch=x64&package=jdk
[releases page]: https://github.com/minecraft-access/minecraft-access/releases/latest
[advanced guide]: {{% relref "./advanced" %}}
[Modrinth]: https://modrinth.com/mods
[CurseForge]: https://legacy.curseforge.com/minecraft/mc-mods
[modrinth-app]: https://modrinth.com/app
[curseforge-app]: https://www.curseforge.com/download/app
[good resources]: {{% relref "/good-resources#quality-of-life-mods" %}}
[complex self-help FAQ]: https://mcaccess.org/faq#self-help-guide-for-abnormal-situations
