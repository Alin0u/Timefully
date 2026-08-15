# Timefully

A customizable HUD clock for Fabric that blends real-life and in-game time
with weather, day-cycle icons, alarms and timers that sync across both.

## Features

- Show real-life time and in-game time side by side, or either on its own
- Day-cycle icons that reflect the current time of day (dawn, day, dusk, night)
- In-game weather at a glance (clear, rain, thunder)
- Alarms on either clock: ring at 07:30 real time, or at dawn in-game
- Configurable background so the widget fits your interface
- Every element can be moved, resized, or switched off individually

Timefully is client side only. It works in singleplayer and on multiplayer
servers, and no server-side plugin is required.

## Requirements

- [Fabric Loader](https://fabricmc.net/)
- [Fabric API](https://modrinth.com/mod/fabric-api)
- [Mod Menu](https://modrinth.com/mod/modmenu) (optional, for the config screen)

Timefully is developed on a separate branch per supported Minecraft version.
See [docs/VERSIONING.md](docs/VERSIONING.md) for the branch naming scheme.

## Building from source

```
./gradlew build
```

The built jar will be in `build/libs/`.

## Credits

The idea comes from [WorldTime](https://www.curseforge.com/minecraft/mc-mods/worldtime)
by Giselbaer ([gbl](https://github.com/gbl)), which showed in-game time and
real-world time on the HUD. WorldTime is end of life and no longer updated.
Timefully picks up the same everyday need and takes it further, with a design
of its own and features WorldTime never had.

- Original idea and inspiration: Giselbaer ([gbl/WorldTime](https://github.com/gbl/WorldTime))
- Timefully: [alinou](https://github.com/Alin0u)

## License

LGPL-3.0, see [LICENSE](LICENSE). The LGPL-3.0 is written as a set of
additional permissions on top of the GPL-3.0, so the GPL-3.0 text it refers
to is included as [COPYING](COPYING).
