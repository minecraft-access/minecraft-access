---
title: "Using the Visually Impaired Access Modpack"
---

This modpack is an unofficial community project created by [@BrailleBennett](https://github.com/BrailleBennett).
It was made for the Fabric mod loader for usage on the client side only.
Here is a link to its [Modrinth page](https://modrinth.com/modpack/vi-access),
where you can read more about it.

## Optional Screenreader

You may want to make use of a screenreader, such as one of these:
1. [NVDA](https://nvaccess.org/) (free and open source)
2. [JAWS](https://www.freedomscientific.com/products/software/jaws/) (requires payment, not open source)
   The modpack will work fine without a screenreader if you don't need one, but some words on the screen won't be read out to you, such as the names of blocks you are looking at. Using a screenreader is recommended for those with very limited vision who cannot read words on the screen easily.

## Installing the Modpack

### Method 1 - Modrinth (recommended)
To install the Visually Impaired Access modpack, you're going to want to install the [Modrinth launcher app](https://modrinth.com/app)

1. Install the Modrinth Launcher: After downloading the launcher from the provided link, follow the installation prompts.
2. Once opening the launcher or pulling it into your screen reader's focus, to get your screen reader to recognize buttons on the screen you will need to click anywhere with your mouse. If you have no mouse, simply use `NVDA+numpad multiply` to jump the mouse cursor to the window after using `alt+tab` to put the window in focus. Then use `NVDA+numpad divide`, this should force NVDA to recognize buttons on the screen and read them to you. This workaround will need to be done when bringing Modrinth into focus after it has been alt-tabbed away from.
3. Sign in to Minecraft: Once the launcher is installed, sign in to your Microsoft account. This step is necessary to verify that you own the game and to allow the launcher to download game files for you.
4. Find the modpacks Tab: After signing in, navigate to the `Modpacks` tab in the launcher.
5. Search for the Pack: Use the search function to look for `vi-access` or a similar term. You should see `Visually Impaired Access Mods - Fabric` by `BrailleBennett` as the top result.
6. Install the Pack: Click on the `Visually Impaired Access Mods - Fabric` pack and find the `Install` button. Click `Install` to add the mod pack to your library.
7. Go to Your Library: Navigate back to the “Library” tab, which is located at the top of the launcher interface.
8. Find the Installed Pack: In your library, look for the entry labeled `Visually Impaired Access Mods - Fabric` followed by `Fabric <game version>`. This entry should be near the bottom of your library screen.
   A good trick to finding it is to navigate by links using your screen reader until there are no more links and then arrow down a few times.
9. Launch the Game: Once you’ve located the pack, click on it, then press the `Play` button to start the installation and launch the game.

### Method 2 - manual (not recommended)
There are some players who find the Modrinth launcher to not be accessible enough. In this case it is advisable for them to manually install the pack within their base Minecraft installation. If you wish to do this, return to [the basic setup guide]({{% relref "/setup/basic" %}}) and continue following the instructions until you get to the point where you are downloading mods and then come back here.
1. If you want to use manual installation you can download a compressed zip file of the pack files [from this 3rd party tool](https://fabulously-optimized.github.io/mrpack-to-zip/?project=TAT3EDpw). This tool downloads and compresses all files on your device, and it is all run on your device. It may take a little while to complete due to the amount of files it has to download.
2. Unzip the downloaded file
3. Use `control+A` and then `control+C` to copy all of its contents
4. Next, find the Minecraft directory which will be located at `%appdata%\.minecraft` on Windows by default
5. Delete the existing `options.txt` file as well as the mods, resourcepacks, and Config folders
6. Paste in the files from the zip file you downloaded in step 1 using `control+V`
7. Launch the game and verify that Minecraft Access is speaking by entering a world and looking around.

## Updating the Modpack

### Method 1 - Modrinth
1. Click on the installation of the modpack in your library.
2. Find the `Update Pack` button on the same screen as the `Play` button.
3. Look for the heading that says `Select Pack Version`.
4. Find the top version number in the list.
5. Click the button to the left of the top version number to apply it.
6. Refresh the page.
7. Launch the game and verify that Minecraft Access is speaking by entering a world and looking around.

### Method 2 - manual
1. Use [the 3rd party tool](https://fabulously-optimized.github.io/mrpack-to-zip/?project=TAT3EDpw) to download the latest pack file. It may take a little while to complete due to the amount of files it has to download.
2. Unzip the pack file
3. Copy all files contained within with `control+A` and then `control+C`
4. Go to your Minecraft installation (defaulted to `%appdata%\.minecraft` on Windows.
5. Delete the mods folder (optionally excluding any mods you personally added yourself)
6. Delete `config\yosbr`
7. Delete `config\viafabricplus\jars`
8. Delete `resourcepacks` (optionally excluding any resource packs you added yourself))
9. Paste in the files from the zip file you downloaded in step 1 using `control+V`
10. Launch the game and verify that Minecraft Access is speaking by entering a world and looking around.
