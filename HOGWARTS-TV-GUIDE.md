# HomeScreen — Multi-Universe Animated Home Launcher Guide

**HomeScreen (`v7.1.1`)** is an ultra-lightweight (`~560 KB`), zero-dependency Android launcher for **Android TV**, **Google TV**, **Foldables**, **Phones**, and **Tablets** featuring **7 interactive Theme Universes** and **344 live procedural up-to-120 FPS story scenes**. Created by **Revanth Mukka** (`@revumukka`), vibe-coded from scratch with **Google Gemini Antigravity**.

---

## 1. What Makes HomeScreen Unique

### Top Animated Sky (`SkyView`)
- **7 Iconic Theme Universes**:
  1. **Harry Potter — Hogwarts Saga** (Great Hall floating candles, twinkling stars, Hedwig, Golden Snitch, Patronus & Dementors)
  2. **Naruto Shippuden — Shinobi Chronicles** (Blood-Moon & Konoha sky, kunai, shuriken, Hokage embers & Akatsuki clouds)
  3. **Demon Slayer — Kimetsu no Yaiba** (Wisteria-lit Taisho sky, glowing lanterns, Kasugai crows & Nichirin sparks)
  4. **Star Wars — The Skywalker Saga** (Hyperspace starfield, binary suns, X-Wings, TIE fighters & Death Star)
  5. **Marvel Cinematic Universe — The Infinity Saga** (Cosmic Bifrost & Quantum sky, Stark HUD rings & Sling-Ring portals)
  6. **DC Universe — Dawn of Justice League** (Gotham storm clouds, Bat-Signal, Kryptonian sunbeams & Speed Force lightning)
  7. **Formula 1 — Grand Prix World Championship** (Floodlit night circuit sky, FIA 5-Red-Lights starting gantry, TV broadcast helicopters, Pirelli blimps & sector telemetry pods)

### Bottom 344-Scene Saga Theater Stage (Dynamic Up to 120 FPS)
- Renders **344 chronological story scenes** (`80` Harry Potter scenes + `44` scenes each for Naruto, Demon Slayer, Star Wars, MCU, DCU, and Formula 1) along the bottom horizon at **up to 120 FPS** (adaptive 60Hz / 90Hz / 120Hz display negotiation) with **zero heap allocations**.
- **Cinema Story Screensaver**: After your chosen inactivity timeout (`15 sec` to `30 min`, default `2 min`) across any device type or orientation, app tiles and buttons fade out while the top-left **Date, Time & Weather** and **SkyView** stay visible. The bottom story stage scales up to fill the **bottom 3/4th of the screen** and plays randomized scenes from a single shuffled pool across all 7 Theme Universes—dynamically updating the sky and clock typography to match each scene's theme.

### Posture-Aware Multi-Device Grid
- **Portrait Phone / Cover Screen**: `4×4` paged squircle icon grid (`16 apps/page`), compact icon-only `Theme`, `Add app`, and `Settings` capsules, and 2-line wrapped story titles.
- **Landscape Phone / Unfolded Foldable**: `4×2` crest banner grid (`8 apps/page`), keeping the bottom story stage completely clear.
- **Android TV / Google TV / Tablet**: `4×4` widescreen crest banner grid (`16 apps/page`) with D-pad focus zoom and a **Glow On/Off** quick toggle.

---

## 2. Building the APK on macOS / Linux

Requires **Java 17** and the **Android SDK** (`platforms;android-34` and `build-tools;34.0.0`):

```sh
sh build.sh
```

Output APKs are written to `out/HomeScreen_Release_v7.0.0_70.apk` and copied to `~/Documents/HomeScreen App/`.

---

## 3. Installing Over ADB

1. Enable **Developer Options** and **USB / Wireless Debugging** on your Android TV, phone, foldable, or tablet.
2. Connect via ADB and install:
   ```sh
   adb install -r out/HomeScreen_Release_v7.0.0_70.apk
   ```
3. Press the **Home** button on your device or remote and select **HomeScreen** as your default launcher.
   - *Note for Google TV devices*: If Google TV locks the default launcher role, open **Settings > Accessibility > Home button** and enable the optional Home button service so pressing Home opens **HomeScreen**.
