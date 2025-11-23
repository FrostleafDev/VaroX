# VaroX - Professional Varo Management for Spigot/PaperMC 

[![GitHub license](https://img.shields.io/github/license/FrostleafDev/VaroX?style=flat-square)](LICENSE)
[![GitHub release (latest by date)](https://img.shields.io/github/v/release/FrostleafDev/VaroX?style=flat-square&include_prereleases)](https://github.com/FrostleafDev/VaroX/releases)
[![GitHub last commit](https://img.shields.io/github/last-commit/FrostleafDev/VaroX?style=flat-square)](https://github.com/FrostleafDev/VaroX/commits/main)

---

## 1. Project Overview

VaroX is a dedicated Hardcore PvP plugin designed to fully manage the **Varo format (Vanilla Roleplay)** on Spigot and PaperMC servers.

The plugin provides administrators with a robust and secure toolset for managing teams, game states, and world borders, all while maintaining a minimal footprint and requiring **no external dependencies**.

## 2. Features

- Banned items + potions list
- Friendly fire option
- Custom tablist and Motd
- Game Phases
- Teams, Spawns and Team chests
- Multi local support: de, en, fr, es, ru
- Custom worldboarder with phases

### More Features will come soon:
- Sessions
- Strikes
- Custom win stuff
- Spectator support in creative and spectator
- Admin mode with vanish, invsee etc

## 3. Compatibility and Installation

VaroX is compiled separately to provide stable support across multiple major Minecraft versions. Please download the specific JAR file corresponding to your server version from the [Releases page](https://github.com/FrostleafDev/VaroX/releases).
All versions do have the same features.

| Minecraft Version | Java Requirement | Tested On | Notes |
| :--- | :--- | :--- | :--- |
| **1.20.10** | Java 17+ | PaperMC / Spigot | Latest version, optimized for modern environments. |
| **1.16.5** | Java 8/11 | PaperMC / Spigot | Stable and commonly used modern version. |
| **1.12.2** | Java 8 | Spigot | Maintained for compatibility with older setups. |
| **1.8.8** | Java 8 | Spigot | Legacy support for the classic PvP engine. |

## 4. Essential Admin Commands

All administrative commands start with `/varo` (Alias: `/vr`) and require the permission `varox.admin`.

| Command | Category | Function |
| :--- | :--- | :--- |
| `/varo start` | **State** | Initiates the countdown and starts the event. |
| `/varo end` | **State** | Immediately terminates the running Varo event (kicks all players). |
| `/varo team add <Name> <Player...>` | **Team** | Creates a new team with the specified player names. |
| `/varo team remove <Name>` | **Team** | Deletes a team and all associated data. |
| `/varo team list` | **Team** | Lists all registered teams and their current status. |
| `/varo spawn set <ID>` | **Setup** | Sets a team spawn point at the administrator's current location. |
| `/varo reset` | **Maintenance** | Resets the plugin to its initial state, allowing a fresh start. |
| `/varo reload` | **Maintenance** | Reloads configuration and language files without restarting the server. |

## 5. Support and Contribution

For technical questions, bug reports, or feature suggestions, please use the following channels:

* **GitHub Issues:** [Open a new issue here](https://github.com/FrostleafDev/VaroX/issues)
* **Discord:** [jozelot.de/discord](https://jozelot.de/discord)
* **Website:** [jozelot.de](https://jozelot.de)

---

> VaroX is a project developed by Frostleaf (jozelot_).
