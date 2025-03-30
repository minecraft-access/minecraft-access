# I18N

This directory containers all the translation files from [Weblate].

## Developers must know

When your code involves changes to language files, please note that:

- **Add new keys**: Only add to [en_us.json]
- **Remove existing keys**: Only remove from [en_us.json]
- **Rename existing keys**: Rename in all translation files

If what you're trying to do isn't one of those things above, you need to make changes via [Weblate].

[![Weblate panel showing translation progress]][Weblate]

## How it works

[Weblate] has [its own git repo](https://hosted.weblate.org/git/minecraft-access/mod/) maintained on Weblate platform, and kept in sync with the main branch of this repo.
The [release workflow](https://github.com/minecraft-access/minecraft-access/blob/dev/.github/workflows/release.yml#L164) is set to automatically pull the changes from Weblate when releasing a new version of the mod.
We also manually pull the changes when there is a substantial translation changes.

[Weblate]: https://hosted.weblate.org/engage/minecraft-access/
[en_us.json]: ./en_us.json
[Weblate panel showing translation progress]: https://hosted.weblate.org/widget/minecraft-access/open-graph.png
