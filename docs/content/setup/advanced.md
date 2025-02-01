---
title: "Set up NeoForge (ADVANCED)"
---

Setting up NeoForge is a more advanced process. This guide is only recommended for those who **KNOW** they need it.

## Install Java

> Java is a programming language, Minecraft Java Edition and mod loaders are written in this language.
> It's like installing a driver on your computer to use a certain piece of hardware.

The NeoForge installer requires Java to be installed on your PC to run it.
You should use the latest LTS (long-term support) version of Java, currently 21,
to ensure maximum compatibility and future support.
[Here is the download page for Java 21](https://adoptium.net/temurin/releases/?os=any&arch=x64&version=21&package=jdk).
Run the installer and keep clicking Next, there's nothing to say.
You must restart your computer after the Java installation is completed.

## Install NeoForge

NeoForge requires you to select the right version of the game at the time you download the installer.
Reports have been received that higher versions of NeoForge that are compatible with the game version may not be compatible with this mod (this mod is always built on one specific NeoForge version), so it is recommended to download the tested version of NeoForge from the link in the [releases page](https://github.com/khanshoaib3/minecraft-access/releases/latest) (under the `Mod Version Compatibility` section).
If you still want to download the latest version of NeoForge for some reason, [here is the download page for that version of NeoForge](https://neoforged.net/).
Select the `Installer` button under the `Download Recommended` section.
An executable jar file will be downloaded with the name format `neoforge-<neoforge-version>-installer.jar`, for example `neoforge-21.1.4-installer.jar`.

Before running the installer, download and run the patch file from [here](https://johann.loefflmann.net/en/software/jarfix/index.html) so that you can run the jar file directly in Windows File Explorer. If you downloaded the installer for Windows, skip this step.
Now you can run the NeoForge jar file like a normal executable file, a window will pop up for you to choose the installation configuration:
The NeoForge installer supports multiple languages, so if you want to change the language, you can do this here.

1. First is a radio box for selecting install client or server, `Install client` is selected by default, don't change this.
2. Then there is an input field for specifying the installation location, normally the installer will automatically recognize the official loader's folder.
   For example `C:\Users\username\AppData\Roaming\.minecraft`, that's where we want it to install so no need to change this either.
   Copy this path to somewhere like the Notepad for further usage when installing mod files.
3. Now click the `OK` button to start the installation.
   It will download some files from the Internet, if the network is fast, the installation should take about two to three minutes.
   A pop-up will show up to remind you that the installation is successful

All done, you can now continue onto installing mods.

## Set up multiple installations in the Minecraft Launcher

If you wish
to have both Fabric and NeoForge installations of the game
using the official launcher, you can change the installation location in the launcher
to achieve this.
Note that when you’re installing a mod loader,
you must install it to the default directory
and then change the profile game directory separately using the steps below.
Also note that each game directory has not only its own mods folder,
but also its own worlds, screenshots, resource packs, etc. folders.

1. Open the Minecraft Launcher.
2. Go to the `Minecraft: Java Edition` tab.
3. Click the installations tab.
4. Find the profile you wish to edit, most likely Fabric <game version> or NeoForge.
5. Click the more options button that shows up after/to the right of the profile name, play, and locate folder buttons.
6. Click the edit button.
7. Tab until you hear `game directory`.
8. Click the browse button and find where you want this installation to be located.
9. Click the select button.
10. You should be returned to the profile options menu.
11. Click the save button at the bottom of the menu.
12. launch the game to create all the files and folders.