# VaroX - Professional Varo Management for Spigot/PaperMC

[![GitHub license](https://img.shields.io/github/license/FrostleafDev/VaroX?style=flat-square)](LICENSE)
[![GitHub release (latest by date)](https://img.shields.io/github/v/release/FrostleafDev/VaroX?style=flat-square&include_prereleases)](https://github.com/FrostleafDev/VaroX/releases)
[![GitHub last commit](https://img.shields.io/github/last-commit/FrostleafDev/VaroX?style=flat-square)](https://github.com/FrostleafDev/VaroX/commits/main)

---

## 1. Project Overview

VaroX is a dedicated Hardcore PvP plugin designed to fully manage the **Varo format (Vanilla Roleplay)** on Spigot and PaperMC servers.

The plugin provides administrators with a robust and secure toolset for managing teams, game states, and world borders, all while maintaining a minimal footprint and requiring **no external dependencies**.

## 2. Core Features

VaroX implements the critical mechanics necessary for hosting a Varo event:

* **Team and Elimination Management:** Handles team creation, player kills, strike counts, and automatic elimination of players and teams upon death.
* **Dynamic World Border:** Features a fully integrated system for automatically reducing the world border size over configurable, sequential phases throughout the event duration.
* **Start Protection (Grace Period):** Configurable period of invulnerability upon game start and player join, ensuring a fair initial phase.
* **State Control:** Direct administrative control over the game status (`Open`, `Running`, `Ended`) and the ability to perform a complete system reset.
* **Data Integrity:** All critical game and team data is saved instantly to the `data/` directory (JSON format) after every modification, guaranteeing maximum data protection against server crashes.
* **Localization:** Includes five default languages (DE, EN, FR, ES, RU) with all messages easily customizable via dedicated `YAML` files.
    * **Varo 4 Immersion (DE):** **The default German configuration uses the exact core messages from the Varo 4 format to ensure an authentic Varo experience.**

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
