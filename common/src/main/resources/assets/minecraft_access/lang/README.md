# I18N

This directory containers all the translation files from [Weblate].

## Developers must know

When your code involves changes to language files, please note that if what you're trying to do isn't one of those things below (for example, you want to change the value of existing keys), you need to make changes via Weblate.

- **Add new keys**: Only add to [en_us.json]
- **Remove existing keys**: Only remove from [en_us.json]
- **Rename existing keys**: Rename in all translation files

Operations other than above might cause an auto pulling conflict on Weblate git repo and affect the documentation contribution work since Weblate would stop work until the conflict is resolved.

There is an **exception** though: introducing Weblate makes the code and translation not in sync, so if your code makes the meaning of existing keys changed and keep the value unchanged will affect the review or snapshot test, change the value of existing keys in [en_us.json] directly.

[![Weblate panel showing translation progress]][Weblate]

## How it works

[Weblate] has [its own git repo](https://hosted.weblate.org/git/minecraft-access/mod/) maintained on Weblate platform, and kept in sync with the main branch of this repo.
The [release workflow](https://github.com/minecraft-access/minecraft-access/blob/dev/.github/workflows/release.yml#L164) is set to automatically pull the changes from Weblate when releasing a new version of the mod.
We also manually pull the changes when there is a substantial translation changes.

[Weblate]: https://hosted.weblate.org/engage/minecraft-access/
[en_us.json]: ./en_us.json
[Weblate panel showing translation progress]: https://hosted.weblate.org/widget/minecraft-access/open-graph.png
