# Privacy Policy — HomeScreen Launcher

**Effective Date:** November 2, 2026  
**Package Name:** `com.ansub.tvhome`  
**Developer:** Revanth Mukka ([@revumukka](https://github.com/revumukka))

---

## 1. Zero Personal Data Collection (`MASVS-PRIVACY`)
**HomeScreen** does **not** collect, store, transmit, rent, or sell any personally identifiable information (PII), analytics, telemetry, advertising identifiers (`AAID`), crash logs, or device fingerprints.

- **No Accounts or Sign-In:** HomeScreen requires no user registration or authentication.
- **No Third-Party Analytics or Ad SDKs:** HomeScreen contains zero external tracking libraries, zero advertising SDKs, and zero commercial telemetry.

## 2. How Permissions Are Used (`MASVS-PLATFORM`)

| Permission / Declaration | Purpose | Leaves Device? |
| :--- | :--- | :--- |
| **`<queries>` (Installed App Visibility)** | Required for core Home Screen Launcher functionality to list, display icons for, and launch user-installed applications on your Home Screen and All Apps Drawer. | **Never.** Your list of installed apps is processed strictly in memory and local `SharedPreferences` (`MODE_PRIVATE`). |
| **`INTERNET` & `ACCESS_NETWORK_STATE`** | Used exclusively to display your local network connection status icon (Wi-Fi / Ethernet / Offline) and to fetch anonymous current weather conditions over HTTPS (`https://wttr.in` / `https://api.open-meteo.com`). | Only standard anonymous HTTPS requests to public weather endpoints. |
| **`ACCESS_WIFI_STATE`** | Used on Android TV to display the Wi-Fi signal strength bars (1–3 bars) in the top-right status dock. | **Never.** |
| **`EXPAND_STATUS_BAR`** | Used on phones, foldables, and tablets to expand the native Android Notification Shade when you swipe down on the Home Screen. | **Never.** |
| **`REQUEST_DELETE_PACKAGES`** | Triggers the standard Android system uninstall confirmation dialog when you select **Uninstall** from an app's long-press menu. | **Never.** |
| **`BIND_ACCESSIBILITY_SERVICE` (`HomeButton`, Optional on Google TV)** | Strictly opt-in helper on Google TV devices where the stock launcher cannot be disabled. Detects when `com.google.android.apps.tv.launcherx` opens on pressing the remote Home button and brings HomeScreen to the front. Does **not** read screen content (`canRetrieveWindowContent="false"`), does **not** log keystrokes, and never transmits data. | **Never.** |

## 3. Data Storage & Retention (`MASVS-STORAGE`)
All user preferences (pinned Home Screen app package names, selected Universe Theme index, Sky Palette index, 12/24-hour clock format, and custom HDMI input labels) are stored locally on your device inside private application storage (`Context.MODE_PRIVATE`). Uninstalling the application or clearing app data permanently deletes all stored preferences.

## 4. Children's Privacy
HomeScreen does not knowingly collect any personal information from anyone, including children under the age of 13 (COPPA / GDPR-K compliant).

## 5. Contact
For privacy inquiries or source code verification, visit the open-source repository:  
https://github.com/revumukka/HomeOS
