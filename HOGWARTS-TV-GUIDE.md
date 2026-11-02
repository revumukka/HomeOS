# ⚡ Hogwarts & FancyLEDs Android TV Launcher — Shareable Notebook & Setup Guide

A **121 KB**, zero-dependency, hardware-accelerated custom launcher (`com.ansub.tvhome`) that transforms any slow, ad-heavy Google TV / Android TV into an enchanted **Hogwarts Great Hall & 24-Scene Storybook Theater** while freeing **400+ MB of RAM**.

---

## ✨ What's Inside

| Feature | Details |
| :--- | :--- |
| **APK Size & RAM** | **121 KB APK** \| **~36 MB RAM** \| Pure Android SDK Java (zero third-party libraries) |
| **Enchanted Ceiling Sky** | Twinkling constellations, full moon, drifting `Lumos` gold dust, **18 flickering floating candles**, and **5 translucent Hogwarts House Ghosts** (*Nearly Headless Nick, The Grey Lady, The Fat Friar, The Bloody Baron, Moaning Myrtle*) |
| **Custom House Crest Cards** | Bespoke GPU-cached Grimoire cards for your favorite apps (*Gryffindor Crimson, Slytherin Emerald, Ravenclaw Sapphire, Hufflepuff Gold, Astronomy Amethyst*) |
| **FancyLEDs Sync Box Reactive Ambilight** | Auto-discovers FancyLEDs / Tuya Sync Boxes on LAN (`UDP 6667` / `TCP 6668`) and bathes all 4 edges of the screen in the focused app's House color (with an instant **`GLOW ON` / `GLOW OFF`** toggle on the top bar) |
| **24-Scene Animated Harry Potter Story Loop** | Expanded bottom stage (`y = 576..1080`) continuously animating **24 iconic scenes** across all 8 films from *Privet Drive* & *The Hogwarts Express* to *Expecto Patronum*, *The Hungarian Horntail*, and the *Harry vs. Voldemort Final Duel* |
| **Interactive Remote "Wow" Twists** | **Marauder's Map Footsteps** walk between tiles as you press D-Pad arrows, an autonomous **Golden Snitch** flies to hover above your focused app, and a **`Lumos Maxima` spark burst** radiates on focus |
| **Live Weather & Authentic Fonts** | Bundled `harryp.ttf` + `cinzel.ttf` typography with asynchronous live weather (`wttr.in` + Open-Meteo fallback) |

---

## 🛠️ Prerequisites

1. **Any Android TV or Google TV** (TCL, Sony, Hisense, Philips, Chromecast with Google TV, NVIDIA Shield, Xiaomi, Onn, etc.) on the same Wi-Fi network as your computer.
2. **`adb` (Android Platform Tools)** installed on Mac, Windows, or Linux:
   - **macOS**: `brew install android-platform-tools`
   - **Windows**: `winget install Google.PlatformTools`
   - **Linux**: `sudo apt install adb`
3. **The compiled `out/tvhome.apk`** included in this folder.

---

## 🚀 Step-by-Step Installation on Any TV (5 Minutes)

### Step 1: Turn on "USB / Network Debugging" on the TV
1. On the TV, open **Settings → System → About**.
2. Scroll down to **Android TV OS build** and press **OK** on the remote **7 times** until it says *"You are now a developer!"*.
3. Press **Back**, open **Developer options**, and turn ON **USB debugging** (and **Network debugging** / **Wireless debugging** if shown).
4. Note the TV's **IP Address** under **Settings → Network & Internet → (Your Wi-Fi Name) → IP address** (e.g., `192.168.1.50` or `10.0.0.238`).

---

### Step 2: Connect & Install the Launcher
Open a terminal in this folder and run (replace `TV_IP` with your TV's IP address):

```bash
export TV_IP="10.0.0.238"
adb connect $TV_IP:5555
```
*(Look at the TV screen and click **"Always allow from this computer" → Allow** on the popup prompt.)*

Now install `out/tvhome.apk`:

```bash
adb -s $TV_IP:5555 install -r out/tvhome.apk
```

---

### Step 3: Configure Apps, Quick Toggles & Make It the Default Home Screen
Run this single command block to enable the top-right toggles (`GLOW ON/OFF`, `BLUETOOTH`, `SOUND`, `PICTURE`, `WI-FI`), bind the remote's **Home** button, and disable the heavy stock Google TV launcher bloat:

```bash
# 1. Enable the Accessibility Home-Button interceptor so pressing Home always opens Hogwarts TV
adb -s $TV_IP:5555 shell "settings put secure enabled_accessibility_services com.ansub.tvhome/com.ansub.tvhome.HomeButton; settings put secure accessibility_enabled 1"

# 2. Launch with your preferred top-row apps and quick settings bar
adb -s $TV_IP:5555 shell 'am start -n com.ansub.tvhome/.MainActivity \
  --es apps "com.google.android.youtube.tv,com.surfshark.vpnclient.android,in.startv.hotstar,com.plexapp.android,com.hulu.livingroomplus" \
  --es quick "bluetooth,sound,picture" \
  --es clock "12" \
  --es stamp "setup_v1"'

# 3. Set as the system default Home activity (and optionally disable the stock Google TV ad launcher)
adb -s $TV_IP:5555 shell "cmd package set-home-activity com.ansub.tvhome/.MainActivity"
adb -s $TV_IP:5555 shell "pm disable-user --user 0 com.google.android.apps.tv.launcherx 2>/dev/null; pm disable-user --user 0 com.google.android.tungsten.setupwraith 2>/dev/null"
```

---

## 🎮 Remote Controls & Hidden Tricks

- **Rearrange, Replace, or Add Apps (No Computer Needed)**:
  - **Hold `OK`** (or press **`≡ Menu`**) on any app card on the TV to **Move**, **Replace**, **Remove**, or **Uninstall** it.
  - Click **`+ ADD APP`** on the bottom-right pill to add any installed TV app to your home row.
- **Toggle the FancyLEDs Ambilight Border Glow**:
  - Click **`GLOW ON` / `GLOW OFF`** in the top-right quick settings bar (or open **`SETTINGS` → `Turn Glow On/Off`**).
- **Skip Through All 24 Harry Potter Story Scenes**:
  - Click **`SETTINGS` → `Next Story Scene`** on the bottom row to jump forward through the 24 animated scenes anytime.
- **Change the Enchanted Sky Palette**:
  - Click **`SETTINGS` → `Wallpaper`** to preview and switch between 14 sky color palettes live.

---

## 🔄 How to Uninstall / Restore Stock Google TV Anytime

```bash
adb -s $TV_IP:5555 shell "pm enable com.google.android.apps.tv.launcherx; pm enable com.google.android.tungsten.setupwraith; pm uninstall com.ansub.tvhome"
```
