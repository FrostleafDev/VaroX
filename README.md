# VaroX - Professionelle Varo-Verwaltung für Spigot/PaperMC

[![GitHub license](https://img.shields.io/github/license/FrostleafDev/VaroX?style=flat-square)](LICENSE)
[![GitHub release (latest by date)](https://img.shields.io/github/v/release/FrostleafDev/VaroX?style=flat-square&include_prereleases)](https://github.com/FrostleafDev/VaroX/releases)
[![GitHub last commit](https://img.shields.io/github/last-commit/FrostleafDev/VaroX?style=flat-square)](https://github.com/FrostleafDev/VaroX/commits/main)

---

## 1. Übersicht

VaroX ist ein dezidiertes Hardcore-PvP-Plugin, das die vollständige Administration des **Varo-Formats (Vanilla Roleplay)** auf Spigot- und PaperMC-Servern übernimmt.

Das Plugin ist ressourcenschonend konzipiert, benötigt keine externen Abhängigkeiten und bietet Administratoren eine robuste und sichere Verwaltung der Teams, des Spielstatus und der Weltgrenzen (World Border System).

## 2. Kernfunktionen

VaroX implementiert die zentralen Mechaniken des Varo-Formats:

* **Teams und Eliminierung:** Verwaltung von Teams, automatische Eliminierung von Spielern/Teams nach dem Tod sowie Zählung von Kills und Strikes.
* **Dynamische World Border:** Integriertes System zur automatischen Reduzierung der Weltgrenze in konfigurierbaren Phasen über die gesamte Spieldauer.
* **Startschutz:** Konfigurierbare Unverwundbarkeitsphase nach dem Start des Events und beim Join.
* **Zustandsverwaltung:** Direkte Kontrolle über den Spielstatus (`Offen`, `Laufend`, `Beendet`) und vollständiger Reset des Events.
* **Datensicherheit:** Speicherung aller relevanten Spiel- und Teamdaten unmittelbar nach jeder Änderung, um Datenverlust bei Serverproblemen zu verhindern.
* **Lokalisierung:** Fünf Standard-Sprachen (DE, EN, FR, ES, RU) und einfache Anpassung aller Nachrichten über die `lang.yml` Dateien.

## 3. Kompatibilität und Download

VaroX wird separat für mehrere Hauptversionen von Minecraft kompiliert. Bitte laden Sie die passende JAR-Datei für Ihren Server von der [Releases-Seite](https://github.com/FrostleafDev/VaroX/releases) herunter.

| Minecraft Version | Java | Getestet auf | Besonderheit |
| :--- | :--- | :--- | :--- |
| **1.20.10** | Java 17+ | PaperMC / Spigot | Aktuellste Version mit voller Performance-Optimierung. |
| **1.16.5** | Java 8/11 | PaperMC / Spigot | Stabile, weit verbreitete Generation. |
| **1.12.2** | Java 8 | Spigot | Support für ältere Modding-Umgebungen. |
| **1.8.8** | Java 8 | Spigot | Legacy-Support für die klassische PvP-Engine. |

## 4. Wichtige Admin-Befehle

Alle Befehle beginnen mit `/varo` (Alias: `/vr`). Die Ausführung erfordert die Berechtigung `varox.admin`.

| Befehl | Kategorie | Funktion |
| :--- | :--- | :--- |
| `/varo start` | **State** | Startet den Countdown und leitet das Event ein. |
| `/varo end` | **State** | Beendet das laufende Varo sofort (Kickt alle Spieler). |
| `/varo team add <Name> <Spieler...>` | **Team** | Erstellt ein Team mit den angegebenen Spielernamen. |
| `/varo team remove <Name>` | **Team** | Löscht ein Team und dessen Daten. |
| `/varo team list` | **Team** | Listet alle registrierten Teams und deren Status auf. |
| `/varo spawn set <ID>` | **Setup** | Setzt einen Team-Spawn-Punkt an Ihrer aktuellen Position. |
| `/varo reset` | **Maintenance** | Setzt das Plugin in den Anfangszustand zurück. |
| `/varo reload` | **Maintenance** | Lädt Konfiguration und Sprachen neu. |

## 5. Support und Kontakt

Für technische Fragen, Bug-Reports oder Feature-Vorschläge bitten wir Sie, die folgenden Kanäle zu nutzen:

* **GitHub Issues:** [Hier einen neuen Issue eröffnen](https://github.com/FrostleafDev/VaroX/issues)
* **Discord:** [Link zu deinem Discord]
* **Webseite:** [jozelot.de](https://jozelot.de)

---

> VaroX ist ein Projekt von Frostleaf (jozelot_).
