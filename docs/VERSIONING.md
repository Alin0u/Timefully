# Versioning and branches

Timefully targets multiple Minecraft versions through separate branches,
not through a single branch with a wide `minecraft` range in
`fabric.mod.json`. Branches are named after the **exact patch version
they were built and launch-tested against**, and `minecraft` in
`fabric.mod.json` is always an exact pin (e.g. `~1.21.1`), never an
open-ended range, unless a range has actually been verified across every
patch it claims to cover.

## Repo layout

- `main` - no mod source. Just this file, `README.md`, `LICENSE` and
  `LICENSE.GPL`. The landing page for the repo; links out to the version
  branches below.
- `fabric_<exact-mc-version>` - one branch per confirmed-working Minecraft
  version, e.g. `fabric_1.21.1`. Each is a complete, independent mod
  project (full source, same `README.md` content as `main`, its own
  `gradle.properties`).

Branches are independent siblings, not a line you move "backward" or
"forward" through. A new version branch is created once, by branching off
the closest existing version branch and adjusting
`minecraft_version`/`yarn_mappings` plus whatever mixin targets changed
for that version. After that it lives on its own: pushing one version
branch never requires touching another. A fix that applies to more than
one branch gets cherry-picked across, it isn't merged between them.

## Releases

File naming: `timefully-<minecraft_version>_v<mod_version>.jar`, e.g.

```
timefully-1.21.1_v0.1.0.jar
```

Tag naming: `<minecraft_version>_v<mod_version>`, e.g.

```
1.21.1_v0.1.0
```

Each release gets a GitHub Release from that tag, built from the matching
`fabric_<mc-version>` branch, with the jar attached. The release
description states the exact Minecraft version (or verified range) and
Fabric Loader version required. 
