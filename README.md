<div align="center">

# HomeScreen

**Universal Multi-Universe Animated Home Launcher & Cinema Story Screensaver for Android TV, Google TV, Foldables, Phones & Tablets**

[![Version](https://img.shields.io/badge/version-5.1.1-d4af37.svg?style=flat-square)](https://github.com/revumukka/HomeOS/releases)
[![Size](https://img.shields.io/badge/APK_Size-409_KB-2ecc71.svg?style=flat-square)](https://github.com/revumukka/HomeOS/releases)
[![Platform](https://img.shields.io/badge/Android-5.0%2B_(TV_%7C_Foldable_%7C_Phone_%7C_Tablet)-3ddc84.svg?style=flat-square)](https://github.com/revumukka/HomeOS)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg?style=flat-square)](LICENSE)

*Six iconic cinematic universes. 248 live procedural 60 FPS story scenes. Zero ads. Zero bloat. 409 KB.*  
*Created by [Revanth Mukka](https://github.com/revumukka) — vibe-coded from scratch with **Google Gemini Antigravity**.*

</div>

---

## Overview

**HomeScreen** replaces cluttered, ad-heavy stock launchers across **Android TV / Google TV**, **Foldables** (Samsung Galaxy Z Fold cover + inner display), **Phones**, and **Tablets** with a fast, 60 FPS procedural-canvas home screen (`409 KB` total APK size, zero external dependencies, zero trackers).

Every element—from twinkling sky constellations and floating candles to custom crest banners, vector icons, and the **248-scene animated saga stage** along the bottom horizon—is drawn procedurally in real time on Android's hardware-accelerated `Canvas` with **zero per-frame heap allocations**.

---

## 6 Interactive Universe Themes (248 Live Story Scenes)

Switch seamlessly at any time via the bottom **Theme** icon button:

| # | Universe Theme | Sky & Atmospheric Elements | Crest Banners | Story Stage |
|---|---|---|---|---|
| 1 | **Harry Potter — Hogwarts Saga** | Enchanted Great Hall night sky, floating candles, Hedwig, Golden Snitch, Patronus & Dementors | Gryffindor, Slytherin, Ravenclaw & Hufflepuff double-bordered crests | **68 chronological scenes** across Books 1–7 (Privet Drive to Battle of Hogwarts) |
| 2 | **Naruto Shippuden — Shinobi Chronicles** | Blood-Moon & Konoha night sky, kunai, shuriken, drifting Hokage embers & Akatsuki clouds | Konoha Leaf, Uchiha Sharingan, Six Paths Sage & Akatsuki crests | **36 iconic scenes** (Nine-Tails Attack, Chunin Exams, Pain's Assault, Legendary Sannin Deadlock, Final Valley) |
| 3 | **Demon Slayer — Kimetsu no Yaiba** | Wisteria-lit Taisho sky, glowing lanterns, Kasugai crows & Nichirin sparks | Hinokami Kagura, Water Breathing, Thunderclap & Mist Hashira crests | **36 iconic scenes** (Final Selection, Mugen Train, Entertainment District, Infinity Castle, Dawn Over Tokyo) |
| 4 | **Star Wars — The Skywalker Saga** | Hyperspace starfield, binary twin suns of Tatooine, TIE fighters, X-Wings & Death Star silhouette | Jedi Order, Galactic Empire, Rebel Alliance & Mandalorian Beskar crests | **36 iconic scenes** (Binary Sunset, Trench Run, Battle of Hoth, Duel of the Fates, Order 66, Throne Room) |
| 5 | **Marvel Cinematic Universe — The Infinity Saga** | Cosmic Quantum & Bifrost sky, Stark HUD rings, Mjolnir lightning & Sling-Ring portals | Avengers Initiative, Stark Arc Reactor, Wakanda Forever & Sanctum crests | **36 iconic scenes** (Mark I Cave Escape, Battle of New York, Wakanda Entrance, Portals Assembly, "I Am Iron Man") |
| 6 | **DC Universe — Dawn of Justice League** | Gotham storm clouds, Bat-Signal projection, Kryptonian sunbeams & Speed Force lightning | Wayne Enterprises Dark Knight, House of El, Themyscira & Speed Force crests | **36 iconic scenes** (First Flight, Knightmare, No Man's Land, At the Speed of Force, Justice League United) |

---

## Key Features

- **5.1 Cinema Story Screensaver (TV, Landscape & Unfolded Foldables)**:
  - After **2 minutes of inactivity** on Android TV, any landscape screen, or an unfolded foldable display, all app tiles, page dots, and buttons gently fade away while the top-left **Date, Time & Live Weather** and **SkyView** remain visible.
  - The bottom story theater scales up (`2.30x × 2.55x`) to take over the **bottom 3/4th of the screen** while keeping the bottom story caption & progress dots at their crisp native size.
  - Shuffles all **248 story scenes across all 6 universes** into a single randomized cinema pool—automatically switching the sky background, atmospheric particles, and top-left clock typography to match each scene's universe. Any touch, key press, or D-pad movement immediately restores the launcher.
- **Posture-Aware Multi-Device Layout**:
  - **Cover Screen / Portrait Phones (`4×4` Paged Grid)**: Up to **16 apps per page** with full-sized `54dp–56dp` squircle icons, compact icon-only action capsules (`Theme`, `Add app`, `Settings`), and 2-line wrapped story captions so the bottom story stage is always unobstructed.
  - **Unfolded Foldables / Landscape Phones (`4×2` Crest Banner Grid)**: **8 apps per page** in rich `16:9` multi-bordered franchise crest banners (`68dp` height), leaving generous open space above the bottom story stage.
  - **Android TV / Google TV / Tablets (`4×4` Crest Banner Grid)**: Full 16-card widescreen showcase with D-pad focus zoom, animated focus borders, and a **Glow On/Off** quick toggle.
- **Swipe-Up Frosted Glass All-Apps Drawer**:
  - Swipe up anywhere on the home screen (or press Down past the bottom row) to open the full alphabetical app drawer with instant search filtering, tap-to-launch, and long-press app actions.
- **Bespoke Procedural Typography**:
  - Custom `MetricAffectingSpan` renderers dynamically style the clock, date, weather, and card titles per theme (Lightning-cut Harry Potter serifs, Ninja Brush strokes, Taisho Woodblock serifs, Galactic aurebesh-inspired geometry, Avengers chamfered display, and Gotham gothic serifs).
- **Live Local Weather & Time**:
  - Displays current temperature and sky condition alongside the date and clock using zero-key HTTPS weather lookup.

---

## Installation

### Download Prebuilt APK
Grab the latest signed universal APK from the [**Releases**](https://github.com/revumukka/HomeOS/releases) page:
- `HomeScreen_Release_v5.1.1_52.apk` (Universal APK for Android TV, Google TV, Foldables, Phones & Tablets)

### Install via ADB
```sh
adb install -r out/HomeScreen_Release_v5.1.1_52.apk
```

---

## Building from Source

Requires **Java 17** and the **Android SDK** (`platforms;android-34` and `build-tools;34.0.0`). No Gradle daemon or external Maven dependencies are needed—builds complete in **under 3 seconds**:

```sh
sh build.sh
```

Output APKs are generated in `out/`:
- `out/HomeScreen_Release_v5.1.1_52.apk`
- `out/HomeScreen-Universal-TV-Foldable-Tablet-Release-v5.1.1-b52.apk`
- `out/HomeScreen.apk`

---

## Optional TV Debloat Script

For slow Android TV / Google TV sets, `debloat.sh` provides an interactive, reversible menu over ADB to disable factory bloatware while keeping Play Store, Chromecast, voice search, and system settings intact. See [`docs/debloat.md`](docs/debloat.md) for details.

---

## Credits & License

- Created and designed by **Revanth Mukka** ([@revumukka](https://github.com/revumukka)), vibe-coded from scratch with **Google Gemini Antigravity**.
- Released under the [MIT License](LICENSE).
