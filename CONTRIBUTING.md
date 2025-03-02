# Contributing to the minecraft-access

Thank you for helping make this mod accessible to more people around the world.

## Translations

To manage translations we use [Weblate], an open source web-based translation platform.
If you're interested in contributing to translations, please visit our [Weblate] project where you will find all the languages we are currently translating to and any untranslated strings.
If you would like to help translate into a language which isn't there, please reach out to us via [Discord].

## Required knowledge

First, general Java project development knowledge is required: Java programming, concepts of writing clean code, git operations, basic knowledge of Linux shell commands, collaboration with GitHub...
If you are not familiar with how to clone a project and submit a Pull Request using GitHub, please read [the GitHub official documentation](https://docs.github.com/en/get-started/quickstart/contributing-to-projects).

Then there is some knowledge for Minecraft modding you'd like to learn before coding.
You're better off learning by doing, first git clone the project and set up the local environment, then read the project files, start the mod in debug mode and set breakpoints to understand the order of execution.

This project uses [Gradle](https://docs.gradle.org/current/userguide/getting_started_eng.html) as dependency manager and Groovy DSL for writing Gradle scripts;
thus you need to at least know the basics of Gradle, like how to start a Gradle task, how to add new dependencies, how to build the project...
[here is a tutorial from Gradle official](https://docs.gradle.org/current/userguide/getting_started_eng.html#getting_started).

This project supports Fabric and NeoForge (stopped supporting Forge after 1.20) with the help of [Architectury Loom](https://docs.architectury.dev/loom/introduction), a framework for multi-platform modding that is derived from [Fabric Loom](https://fabricmc.net/wiki/documentation:fabric_loom).
Architectury Loom functions as a Gradle plugin and mod configurator, it won't affect the code.
For questions about architectury, you can ask in [their Discord server](https://discord.architectury.dev/) instead of searching for [documentation](https://docs.architectury.dev/loom/introduction), but it doesn't harm to search before asking.
Since Architectury Loom is a derivative of Fabric Loom, the [Fabric Loom's documentation](https://fabricmc.net/wiki/documentation:fabric_loom) also applies to Architectury Loom in most cases, which is more detail than Architectury Loom's.

For the convenience of reusing the same set of code for all platforms, this mod doesn't directly depend on platform-specific APIs; instead we directly modify original game code, injecting our logic into it using a framework called `Mixin`.
We also depend on the [Architectury API](https://docs.architectury.dev/api/introduction) to handle functions that are different for each platform with one piece of code for both.
Well, to be precise, 95% of the code is platform-independent, and if we really need platform-specific APIs to achieve the goal, don't shy away from using them.

We heavily depend on [SpongePowered Mixin](https://github.com/SpongePowered/Mixin/wiki) and [LlamaLad7 MixinExtras](https://github.com/LlamaLad7/MixinExtras/wiki), `Mixin` in short, a framework for modifying Java bytecode.
Mixin is a complex but handy framework, for learning it, I recommend reading [the tutorial by Fabric](https://fabricmc.net/wiki/tutorial:mixin_introduction), [`Mixin`'s official wiki](https://github.com/SpongePowered/Mixin/wiki) and [wiki of `MixinExtras` library](https://github.com/LlamaLad7/MixinExtras/wiki). Please note that the SpongePowered Mixin wiki is very technical and detailed and is not suitable for usage as a user manual, use it as the final choice. Use Fabric wiki and MixinExtras wiki as reference when developing.
For questions about Mixin, you can ask in `mod-dev` channels of [the Fabric Discord server](https://discord.com/invite/v6v4pMv) or the `mixin` channel of the [NeoForge Discord server](https://discord.com/invite/neoforged), they are a friendly bunch and willing to answer any questions you may have.

Finally, to search for a suitable injection point, we need to [read the original code](https://fabricmc.net/wiki/tutorial:reading_mc_code).
Thanks to the [MojMap mappings](https://minecraft.wiki/w/Obfuscation_map) tool and [ParchmentMC](https://parchmentmc.org/) we can read the source code that de-obfuscated from the game's bytecode.
You don't need to manually operate the mappings, Loom will do it for you while running related gradle tasks.
If you'd like to know some terms of Minecraft project structure like [`client side` vs `server side`](https://fabricmc.net/wiki/tutorial:side), read related sections of [the Fabric wiki](https://fabricmc.net/wiki/), by the way this mod is a pure client side mod for now.

## Setting up Local Environment

Java 21 is required to be pre-installed for this project.
IntelliJ IDEA is recommended since it has some convenient Minecraft modding plugins: [Architectury](https://plugins.jetbrains.com/plugin/16210-architectury) and [Minecraft Development](https://plugins.jetbrains.com/plugin/8327-minecraft-development).
Can't find corresponding vscode extensions, but since Gradle is platform-independent, you can use any IDE as you like.

Now clone the project and run `gradlew build`, to let Gradle automatically download dependencies, it takes minutes to set up at first time.
After finishing the first build, you should run `gradlew genSources` to generate the de-obfuscated source code, set generated source jar as compiled jar's source according to the `Generating Minecraft Sources` section of the [tutorial](https://fabricmc.net/wiki/tutorial:setup).

For IntelliJ, Architectury Loom will generate some [Run Configurations](https://www.jetbrains.com/help/idea/run-debug-configuration.html) like `Minecraft Client (:fabric)`, `Minecraft Server (:fabric)`,  `Minecraft Client (:neoforge)`, `Minecraft Server (:neoforge)`, you can tell the purpose of each configuration by their name, for example, `Minecraft Client (:fabric)` is for starting the client side game on Fabric.

PS: If the Run Configurations get broken accidentally, delete `.idea/runConfigurations` then re-open the project in IntelliJ. Loom will re-generate them automatically.

Minecraft is essentially a Java program in the form of executable jar files (one jar for the client and one jar for the server), so you can modify the run configuration of the jar files as you need.
For example, `-Dmixin.debug.export=true` is for exporting `mixined` bytecode (`.class` files) when [debugging mixin](https://fabricmc.net/wiki/tutorial:mixin_export), `--username=<minecraft username> --password=<minecraft password>` is for providing user information to client side game when developing.

Now you can explore the project.

### Developing Tips

- When running the game with IDE, the main directory is `{platform directory}/run` (`.minecraft` in production environment), you can see familiar directories like `config`, `mods`, `saves` under it.
- You can run `gradlew {platform}:build` to build mod for a specific platform (when root Gradle `build` task fails for some reason), built mod jars are under `{platform directory}/build/libs`, the one without a suffix.

For more developing tips, see [this section of Fabric wiki](https://docs.fabricmc.net/develop/ide-tips-and-tricks).

## Project Structure

### File Structure

- `.github`: GitHub related stuff like workflow (CI) files.
- `gradle`: Mainly contains Gradle wrapper configuration, Gradle wrapper's version is passively upgraded as Loom's version is upgraded.
- `doc`: Project documentations.
- `build.gradle`: Root Gradle build script of this Gradle managing project. ([Gradle doc about `build.gradle` file](https://docs.gradle.org/current/userguide/build_file_basics.html))
- `gradle.properties`: Configuration about mod and dependencies versioning.
- `settings.gradle`: Defines that this project has three subprojects: `common`, `fabric`, `neoforge`. ([Gradle doc about `settings.gradle` file](https://docs.gradle.org/current/userguide/settings_file_basics.html))
- `common`: Gradle subproject, Platform-independent code and configurations, normal Java project structure.
  - `build.gradle`: Gradle build script of `common` subproject.
  - `src/main/java`: Source code.
  - `src/main/resources`
    - `log4j.xml`: Log configuration.
    - `-.mixins.json`: Mixin configuration that tells the Mixin framework where to find Mixin classes under `common` module.
      - `assets/minecraft_access`: Contains custom resource files of this mod, structured in the [Minecraft Resource Pack](https://minecraft.wiki/w/Resource_pack) format.
        - `lang`: The I18N text used by this mod for narration purpose, managed with [Weblate].
  - `src/test`: Unit test suite.
- `fabric`: Gradle subproject, Fabric-dependent mod code.
  - `build.gradle`: Gradle build script of the Fabric version of the mod.
  - `src/main/resources`:
    - `fabric.mod.json`: [Fabric mod configuration](https://fabricmc.net/wiki/documentation:fabric_mod_json).
- `neoforge`: Gradle subproject, NeoForge-dependent mod code.
  - `build.gradle`: Gradle build script of the NeoForge version of the mod.
  - `src/main/resources`:
    - `META-INF/neoforge.mods.toml`: [NeoForge mod configuration](https://docs.neoforged.net/docs/gettingstarted/modfiles/#neoforgemodstoml).

Please note that this repository has one repository as its git submodule - the website theme for generated documentation site.

### Program Structure

The root package class path of this project is `org.mcaccess.minecraftaccess`, below I'll use relative paths to describe code.
There are two types of logic in this project, classified by the execution entry point.

One is triggered by original game logic invoking.
With the help of the Mixin framework, we inject logic into the original code, so when original code is called, the injected logic will be executed too.
This execution type is suitable for making existing game content accessible, like speaking the held item when switching items in hotbar.

The other type of logic is achieved by adding custom function invoking logic into original game's tick processing method in platform-dependent code, which is called every [tick](https://minecraft.fandom.com/wiki/Tick).
The `MainClass.clientTickEventsMethod()` is invoked when platform tick event is fired, then it calls every execution function inside itself, each feature will check if its condition is met, if so, it will execute their particular logic, or it will directly return as skipping this tick.
This execution type is suitable for implementing new features that not exist in the original game, like `Camera Controls` that rotating the player view with keyboard.

The `Config` class is responsible for loading mod-specific configuration, and user-specific configuration is stored in `{main directory}/config/minecraft-access.json` file.
We use [Cloth Config](https://shedaniel.gitbook.io/cloth-config) to generate the config menu GUI.
Adding configuration options is very simple, just add fields at the right position in `Config` class, don't forget to initialize them with default values.
Every config-requiring feature to check their condition before every execution.

The `mixin` subpackage contains Mixin related things like `FooMixin` and `BarAccessor`.
`...Mixin` classes are responsible for injecting logic into corresponding original game classes.
`...Accessor` interfaces are used for accessing fields and methods of original game classes, they're used in `...Mixin` classes and custom features.

The `compat` subpackage contains the implementation of compatibility for other mods like Cloth Config, Jade, etc.

The `features` subpackage contains the implementation of custom features that being triggered by tick mechanism.
If one mixin-based feature has complex logic, we'll also extract the non-mixin part as an independent class in the `features` subpackage to separate the injection points and real logic.

The `screen_reader` subpackage contains the implementation of the operating system specific screen reader proxy.
Following the interface segregation principle, we invoke this proxy everywhere where it needs to speak some text through a screen reader, then this proxy is responsible for passing text to the real screen reader (or another layer of interface).

The `utils` subpackage is home for common utility codes.
We'd keep functions in `utils` as static methods ([`pure function`](https://en.wikipedia.org/wiki/Pure_function) concept in functional programming) so we can easily reuse them.

This project has very few unit tests, hopefully we will be able to add more tests in the future.
Applying less mock and embracing programming strategies like [`Functional Core, Imperative Shell`](https://news.ycombinator.com/item?id=34860164), [`Red - Green - Refactor`](https://martinfowler.com/bliki/TestDrivenDevelopment.html) and more described in [this article](https://www.amazingcto.com/mocking-is-an-antipattern-how-to-test-without-mocking/).

## CI

This project has an automatic test-build-release pipeline thanks to [@TheSuperGamer20578](https://github.com/TheSuperGamer20578).
Since this project is hosted on GitHub, it's natural for us to choose GitHub Action as the CI system.

- For every PR commit and building, `push` workflow will be triggered for building and running the test suite against changes.
- The `publish-pr-build` workflow is responsible for building snapshot versions and uploading them to Discord. Snapshot versions are built when PR is merged to main branch or a PR is tagged with `bedrock-breakers` label.
- When a new version is ready, we'll manually run the `release` workflow to automatically tag the version, collect changelog, publish mod to the GitHub release, [CurseForge](https://www.curseforge.com/minecraft/mc-mods/minecraft-access) and [Modrinth](https://modrinth.com/mod/minecraft-access).
- The `docs` workflow is for building the documentation and uploading them to GitHub pages.

## Documentation

This project has a [Hugo](https://gohugo.io/) based static website serves as wiki and gate for contact ways and distribution channels - [mcaccess.org](https://mcaccess.org/).
The website theme has its [independent repository](https://github.com/minecraft-access/hugo-themes), it's because we'd like to reuse same theme across other possible websites like a blog.

## Recommended Approaches

### Tell Us What You Intend To Do Before Doing It

When you decide to make contributions, we expect you to comment in the corresponding issue, make a new issue, or talk in the `mc-dev` channel of our [Discord server](https://discord.gg/yQjjsDqWQX).
Industry experience tells us that shift-left checks and discussions are good for software development — for example, we can provide more relevant knowledge, project details, and advice to help you do things better.
It can only be done if we know what you want to do, though.

Of course there is no mandatory requirement (praise [the freewheeling open source community](http://www.catb.org/~esr/writings/cathedral-bazaar/)!), the code for this project is open source under an opensource license.
This means that you do not need to get anyone's permission to make changes to your fork of this project, you just need to make it work for **your personal needs**.
If you feel that your changes will also benefit the upstream (main project), you can submit a Pull Request to [the main repository](https://github.com/minecraft-access/minecraft-access), and only then does the `contributing` begin.

### Modify Along With Refactor

Keeping public environments (the whole repository, I mean) clean.
If you find that there are grammar mistakes or typos in the documentation, please correct them.
If you find that there are [IDE inspection notices](https://www.jetbrains.com/help/idea/code-inspection.html) on the file you're working on, please accept them.

### Make all related changes in one pull request

Update the documentation along with the code.
Make sure that there is no unused code in the submitted pull request.
"There are more subsequent PRs" is not a valid reason.

### Review first

This is for who has review authority for this project.
Start reviewing as soon as you can.
If there are PRs waiting for your review while you have time to work on this project, you should review them first, rather than continuing with your own features.

> When you’re actually reading the code and giving feedback, take your time, but start your review immediately — ideally, within minutes.
> 
> When you start reviews immediately, you create a virtuous cycle.
> Your review turnaround becomes purely a function of the size and complexity of the author’s changelist.
> This incentivizes authors to send small, narrowly-scoped changelists.
> These are easier and more pleasant for you to review, so you review them faster, and the cycle continues.
> 
> If you’re forced to decline reviews more than about once per month, it likely means that your team needs to reduce its pace so that you can maintain sane development practices.
> 
> cite: [How to Do Code Reviews Like a Human - Michael Lynch](https://mtlynch.io/human-code-reviews-1/)

[Weblate]: https://hosted.weblate.org/engage/minecraft-access/
[Discord]: https://discord.gg/yQjjsDqWQX
