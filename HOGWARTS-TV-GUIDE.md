# HomeScreen — Multi-Universe Animated Home Launcher Guide

**HomeScreen (`v6.0.0`)** is an ultra-lightweight (`~414 KB`), zero-dependency Android launcher for **Android TV**, **Google TV**, **Foldables**, **Phones**, and **Tablets** featuring **6 interactive universe themes** and **300 live procedural up-to-120 FPS story scenes**. Created by **Revanth Mukka** (`@revumukka`), vibe-coded from scratch with **Google Gemini Antigravity**.

---

## 1. What Makes HomeScreen Unique

### Top Animated Sky (`SkyView`)
- **6 Iconic Franchises**:
  1. **Harry Potter — Hogwarts Saga** (Great Hall floating candles, twinkling stars, Hedwig, Golden Snitch, Patronus & Dementors)
  2. **Naruto Shippuden — Shinobi Chronicles** (Blood-Moon & Konoha sky, kunai, shuriken, Hokage embers & Akatsuki clouds)
  3. **Demon Slayer — Kimetsu no Yaiba** (Wisteria-lit Taisho sky, glowing lanterns, Kasugai crows & Nichirin sparks)
  4. **Star Wars — The Skywalker Saga** (Hyperspace starfield, binary suns, X-Wings, TIE fighters & Death Star)
  5. **Marvel Cinematic Universe — The Infinity Saga** (Cosmic Bifrost & Quantum sky, Stark HUD rings & Sling-Ring portals)
  6. **DC Universe — Dawn of Justice League** (Gotham storm clouds, Bat-Signal, Kryptonian sunbeams & Speed Force lightning)

### Bottom 300-Scene Saga Theater Stage (Dynamic Up to 120 FPS)
- Renders **300 chronological story scenes** (`80` Harry Potter scenes + `44` scenes each for Naruto, Demon Slayer, Star Wars, MCU, and DCU) along the bottom horizon at **up to 120 FPS** (adaptive 60Hz / 90Hz / 120Hz display negotiation) with **zero heap allocations**.
- **Cinema Story Screensaver**: After **2 minutes of inactivity** on Android TV, any landscape orientation, or an unfolded foldable display, app tiles and buttons fade out while the top-left **Date, Time & Weather** and **SkyView** stay visible. The bottom story stage scales up to fill the **bottom 3/4th of the screen** and plays randomized scenes from a single shuffled pool across all 6 universes—dynamically updating the sky and clock typography to match each scene's theme.

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

Output APKs are written to `out/HomeScreen_Release_v6.0.0_60.apk` and copied to `~/Documents/HomeScreen App/`.

---

## 3. Installing Over ADB

1. Enable **Developer Options** and **USB / Wireless Debugging** on your Android TV, phone, foldable, or tablet.
2. Connect via ADB and install:
   ```sh
   adb install -r out/HomeScreen_Release_v6.0.0_60.apk
   ```
3. Press the **Home** button on your device or remote and select **HomeScreen** as your default launcher.
   - *Note for Google TV devices*: If Google TV locks the default launcher role, open **Settings > Accessibility > Home button** and enable the optional Home button service so pressing Home opens **HomeScreen**.
