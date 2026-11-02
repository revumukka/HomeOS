# Vibe-Coded Android App → Google Play Store Safety, Security & Regulatory Checklist

This checklist synthesizes the industry-standard GitHub repositories used to audit AI-assisted ("vibe-coded") and indie Android apps prior to a final **Google Play Store** launch, together with the concrete hardening applied in **HomeScreen `v7.1.1` (`versionCode = 72`)**, created by **Revanth Mukka (`@revumukka`)** and vibe-coded from scratch with **Google Gemini Antigravity**.

---

## 📚 Key Reference GitHub Repositories Used

1. **[`OWASP/owasp-mastg`](https://github.com/OWASP/owasp-mastg) & [`OWASP/owasp-masvs`](https://github.com/OWASP/owasp-masvs)**  
   *The official OWASP Mobile Application Security Verification Standard (`MASVS-STORAGE`, `MASVS-CRYPTO`, `MASVS-AUTH`, `MASVS-NETWORK`, `MASVS-PLATFORM`, `MASVS-CODE`, `MASVS-RESILIENCE`, `MASVS-PRIVACY`).*
2. **[`b4b4r07/android-release-checklist`](https://github.com/b4b4r07/android-release-checklist) & [`futurice/android-best-practices`](https://github.com/futurice/android-best-practices)**  
   *Pre-release manifest, signing, SDK target, ProGuard/R8, and Google Play Console verification checklists.*
3. **[`MobSF/Mobile-Security-Framework-MobSF`](https://github.com/MobSF/Mobile-Security-Framework-MobSF)**  
   *Automated static analysis rules for Android Manifest permissions, exported components, intent spoofing, cleartext traffic, and hardcoded IPs/secrets.*
4. **Vibe-Coding Security Audit Checklists (`securing-vibe-coded-apps` / `vibe-coding-security-checklist`)**  
   *Targeted checks for AI-generated code traps: hardcoded LAN IPs, unvalidated `Intent` extras, over-privileged `AndroidManifest.xml` permissions, and missing `network_security_config.xml`.*

---

## 🛡️ Phase 1: Secrets, Keystores & Supply Chain (`MASVS-CODE` / Vibe-Coding Audit)

- [x] **No Hardcoded API Keys or Cloud Secrets**: Audited all `.java`, `.xml`, and `.sh` files. Weather lookup uses zero-key public endpoints (`wttr.in` / `open-meteo.com`).
- [x] **Keystore Excluded from Git (`key.jks`)**: Verified via `git ls-files` and `.gitignore` (`*.jks`) that signing keystores are never committed to the repository.
- [x] **Zero Hallucinated / Vulnerable Third-Party Dependencies**: Compiles directly against the official Android SDK (`android.jar`) with **zero external Gradle/Maven dependencies**, eliminating supply-chain CVE risks.
- [x] **Removed Hardcoded Private LAN IP Polling (`10.0.0.153:6668`)**:
  - *Audit Finding*: `SkyView.java` previously attempted a TCP socket connection to `10.0.0.153:6668` every 4 seconds on all devices.
  - *Fix Applied (`v4.2.0`)*: Removed the hardcoded `10.0.0.153` TCP probe and restricted the passive UDP listener (`port 6667`) strictly to Android TV devices when Edge Glow is enabled.

---

## 🔒 Phase 2: Android Manifest, Permissions & Component Hardening (`MASVS-PLATFORM`)

- [x] **Removed `WRITE_SECURE_SETTINGS` & Self-Enabling Accessibility Code (Critical Play Protect Blocker)**:
  - *Audit Finding*: `AndroidManifest.xml` requested `android.permission.WRITE_SECURE_SETTINGS` and `HomeButton.ensureBound()` wrote to `Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES`. Google Play Protect / Bouncer automatically flags and suspends apps that programmatically enable their own `AccessibilityService`.
  - *Fix Applied (`v4.2.0`)*: Removed `WRITE_SECURE_SETTINGS` from `AndroidManifest.xml` and removed all `Settings.Secure.putString/putInt` calls from `HomeButton.java`.
- [x] **Hardened Exported `BootReceiver` Against Intent Spoofing**:
  - *Audit Finding*: `HomeButton$BootReceiver` was exported without action verification, allowing third-party apps to spoof broadcasts.
  - *Fix Applied (`v4.2.0`)*: Added strict system action verification (`ACTION_BOOT_COMPLETED`, `ACTION_MY_PACKAGE_REPLACED`) and restricted auto-start behavior to TV devices.
- [x] **Hardened `MainActivity.saveConfig(Intent)` Against Preference Injection**:
  - *Audit Finding*: `MainActivity` (`android:exported="true"`) accepted unvalidated `apps`, `quick`, `clock`, and `theme` extras from any external caller.
  - *Fix Applied (`v4.2.0`)*: Required a valid CLI timestamp `stamp` token and strict package-name regex validation (`[a-zA-Z0-9_.]+`) before persisting any `Intent` configuration extras.
- [x] **Scoped Package Visibility (`<queries>` Instead of `QUERY_ALL_PACKAGES`)**:
  - Uses explicit `<queries>` for `Intent.ACTION_MAIN` with `CATEGORY_LAUNCHER` and `CATEGORY_LEANBACK_LAUNCHER` rather than the restricted `QUERY_ALL_PACKAGES` permission, avoiding Play Console's high-risk permission rejection while still discovering all launchable apps.

---

## 🌐 Phase 3: Network & Storage Security (`MASVS-NETWORK` & `MASVS-STORAGE`)

- [x] **Global HTTPS Enforcement (`res/xml/network_security_config.xml`)**:
  - Added [`res/xml/network_security_config.xml`](res/xml/network_security_config.xml) with `<base-config cleartextTrafficPermitted="false">` and set `android:usesCleartextTraffic="false"` and `android:networkSecurityConfig="@xml/network_security_config"` in [`AndroidManifest.xml`](AndroidManifest.xml).
- [x] **Private Local Storage (`Context.MODE_PRIVATE`)**:
  - All `SharedPreferences` use mode `0` (`Context.MODE_PRIVATE`), and `android:allowBackup="false"` prevents unencrypted ADB backup extraction.

---

## 📋 Phase 4: Google Play Console Regulatory & Policy Checklist

Before uploading the `.aab` / `.apk` to **Google Play Console**, complete these store-level requirements:

1. **Privacy Policy URL & In-App Disclosure (`MASVS-PRIVACY`)**:
   - [x] Hosted [`PRIVACY_POLICY.md`](PRIVACY_POLICY.md) in the repository (`https://github.com/revumukka/HomeOS/blob/main/PRIVACY_POLICY.md`).
   - [x] Added an in-app **"Privacy & Safety"** viewer inside **Settings → Privacy & Safety** so users and Play Store reviewers can read the privacy and permission disclosures directly inside the launcher.
2. **Google Play Data Safety Form Answers**:
   - *Does your app collect or share any of the required user data types?* → **No**.
   - *Is all user data encrypted in transit?* → **Yes** (enforced via `network_security_config.xml`).
   - *Do you provide a way for users to request that their data is deleted?* → **N/A** (no user accounts or server-side data storage; all preferences live locally on-device).
3. **Accessibility Service Declaration (If Keeping `HomeButton` for Google TV)**:
   - In Play Console → **App Content → Accessibility**, if submitting to Phone/Tablet/TV tracks, note that Google Play reviewers often reject non-accessibility apps that declare `BIND_ACCESSIBILITY_SERVICE`.
   - **Recommendation for Public Play Store Track**: On phones, foldables, tablets, and standard Android TVs, `<category android:name="android.intent.category.HOME" />` handles the Home button natively without needing `HomeButton`. Only enable `HomeButton` if distributing specifically for locked-down Google TV boxes, or comment out the `<service android:name=".HomeButton">` block when building your public Google Play Store `.aab`.
4. **Intellectual Property / Trademark Policy (Public Play Store vs. Personal GitHub Build)**:
   - Google Play's **Impersonation and Intellectual Property Policy** prohibits third-party apps from using trademarked franchise names (*Harry Potter*, *Naruto*, *Demon Slayer*, *Star Wars*, *Marvel / MCU*, *DC / Batman / Superman*) or proprietary replica fonts in public Play Store listings without a license.
   - **Recommendation for Public Play Store Listing**: Keep the procedural animations, sky palettes, and geometry, but rename the public Play Store build's theme labels to generic archetype titles (e.g., *Wizard Academy*, *Shinobi Village*, *Blade of Wisteria*, *Galactic Empire*, *Cyber Armor*, *Gothic Vigilante*) and use SIL Open Font License (OFL) fonts (`cinzel.ttf`).

