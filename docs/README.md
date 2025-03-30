# Documentation

This directory containers source file of documentations for building the [docs.mcaccess.org](https://docs.mcaccess.org/) website.

## Contributors must know

`README.md` (under root directory) isn't same as `_index.md`, the `README.md` is for displaying on GitHub, the `_index.md` is for serving as the [home page](https://docs.mcaccess.org/) of the documentation website.

How to link other documentations inside a documentation file:

- For files that are not part of the website like `README.md` `CONTRIBUTING.md`, directly use the full url to link other files and documentation pages on the website.
- For files that used to build website (files under `docs/content/`), use the hugo syntax `{{< relref "..." >}}` to link other documentation pages on the website, and use the full url for files stored in GitHub.

## How it works

This project has a [Hugo](https://gohugo.io/) based static website serves as wiki and gate for contact ways and distribution channels - [mcaccess.org](https://mcaccess.org/).
The website theme has its [independent repository](https://github.com/minecraft-access/hugo-themes), it's because we'd like to reuse same theme across other possible websites like a blog.
We manually modify the documentations along with code.
The [`docs` workflow](https://github.com/minecraft-access/minecraft-access/blob/dev/.github/workflows/docs.yml) is responsible for building and deploying the website when documentation files are merged into main branch.