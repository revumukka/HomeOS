package com.ansub.tvhome;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.media.tv.TvContract;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextClock;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.List;
import java.util.WeakHashMap;

public class MainActivity extends Activity {

    // Top-row apps, in this order of preference:
    //  1. a list saved at install time:  am start -n com.ansub.tvhome/.MainActivity --es apps "pkg1,pkg2"
    //     (--es apps auto  goes back to 2; add --es stamp <unique> so it's applied only once, see saveConfig)
    //  2. apps.txt (built in as Apps.LIST), topped up with other TV apps installed on this TV.
    // Missing apps are skipped. Topping up stops at six apps, but a chosen list can be any length:
    // apps wrap into rows of six, like Apple TV, and the page scrolls down once they don't all fit.
    // On the TV, hold OK on an app (or press Menu) to move or remove it, or add another; that saves list 1.
    // A network tile always sits at the top right, level with the clock. Settings shortcuts can join
    // it there, off until picked the same way:
    //   --es quick "bluetooth,sound,picture"
    // (--es quick none  takes them away again; "wifi" and "ethernet" are the network tile now).
    private static final String PREFS = "config";
    private static final List<String> NOT_APPS = Arrays.asList("com.android.vending", "com.android.tv.settings",
            "com.android.tv", "com.google.android.tvlauncher", "com.google.android.apps.tv.launcherx",
            "com.google.android.leanbacklauncher");
    private static final String PLAY_STORE = "com.android.vending";
    private static final String SETTINGS = "settings";
    private static final String ADD = "add";
    private static final String[] EDIT_ITEMS = {"Move", "Replace", "Remove from home", "Uninstall"};
    private static final String[] EDIT_ITEMS_SYSTEM = {"Move", "Replace", "Remove from home", "App info"};
    private static final int PER_ROW = 6, MAX_INPUTS = 4;

    // Settings shortcuts: name, label, icon. Tile keys are QUICK + name.
    private static final String QUICK = "quick:";
    private static final String GLOW_TOGGLE = "glow_toggle";
    private static final String THEME_PICK = "theme_pick";
    private static final String ALL_APPS = "all_apps";
    private static final String PREF_THEME = "universe_theme";
    private static final String[] UNIVERSE_THEMES = {
        "Harry Potter",
        "Naruto",
        "Demon Slayer",
        "Star Wars",
        "MCU",
        "DCU"
    };
    private static final int[] UNIVERSE_THEME_COLORS = {
        0xFFFFD54F, // 1. Harry Potter
        0xFFFF5722, // 2. Naruto
        0xFF00E5FF, // 3. Demon Slayer
        0xFF2979FF, // 4. Star Wars
        0xFFB388FF, // 5. MCU
        0xFF00B0FF  // 6. DCU
    };
    private static final String[] QUICK_NAMES = {"bluetooth", "sound", "picture"};
    private static final String[] QUICK_LABELS = {"Bluetooth", "Sound", "Picture"};
    private static final int[][] QUICK_ICONS = {LineIcon.BLUETOOTH, LineIcon.SOUND, LineIcon.PICTURE};
    private static final String NETWORK = "network";
    private static final int OFFLINE = 0, ETHERNET = 4; // network states; 1 to 3 are Wi-Fi bars

    // Saved on the TV: custom input names ("id=name" per line), hidden inputs (one id per line),
    // and the clock override ("12" or "24"; absent follows the TV).
    private static final String PREF_INPUT_NAMES = "inputnames", PREF_HIDDEN_INPUTS = "hiddeninputs";
    private static final String PREF_CLOCK = "clock", PREF_FAKE_INPUTS = "fakeinputs";
    // The throwaway input install.sh adds with FAKE_INPUT=1. It has no HDMI port behind it; it is
    // there so an emulator, which has no inputs of its own, still has a tile to test with.
    private static final String FAKE_INPUT_PKG = "com.ansub.fakeinput";
    // Names an input can be given with the remote alone: no keyboard, no typing.
    private static final String[] INPUT_PRESETS = {"TV", "PlayStation", "Xbox", "Switch", "Laptop", "PC", "Blu-ray", "Soundbar"};
    // TCL keeps its own Picture and Sound panels in a service; Android's Sound page is missing there.
    private static final String TCL_SHOW_WINDOW = "com.tcl.settings.SHOW_WINDOW";

    // Layout is designed at 1920x1080 and scaled to the TV's real UI size by px().
    private static final int MARGIN = 84, GAP = 32, RADIUS = 28;
    // The focus lift: the tile grows, rises, brightens and settles.
    // Long enough to read as motion, short enough that a run of d-pad presses never feels behind.
    private static final int FOCUS_MS = 180, RADIUS_UP = 0;
    private static final Interpolator FOCUS_EASE = new DecelerateInterpolator(1.8f);
    private static final int ROW1_TOP = 288, ROW_STEP = 198;
    private static final int GRID_MIN_TOP = 240; // more rows push the grid up, but not into the clock
    private static final int ICON_TOP = 56, ICON_BUTTON = 68, ICON_SIZE = 34, ICON_GAP = 12; // level with the date

    // The Settings tile opens a small menu: the home screen's own settings, then the TV's.
    private static final String[] SETTINGS_ITEMS = {"Wallpaper", "Brightness", "TV settings"};
    // Android TV 11 and later (Google TV too) has no Display page, but its "Display & Sound" page holds Picture.
    private static final String[] TV_DISPLAY_SOUND = {"com.android.tv.settings",
            "com.android.tv.settings.device.displaysound.DisplaySoundActivity"};
    // Wallpapers, saved by name: a gradient top to bottom, then a glow from the top left and one from
    // the bottom right. Dark ones first, then bright ones that get darker towards the bottom, so the
    // white labels stay easy to read. Only colours, no images, so every one costs the same as Dusk,
    // the default.
    private static final String[] WALLPAPER_NAMES = {"Dusk", "Midnight", "Plum", "Forest", "Graphite", "Ocean", "Ember",
            "Sunset", "Lagoon", "Aurora", "Coral", "Iris", "Meadow", "Gold"};
    private static final int[][] WALLPAPERS = {
        {0xFF1A1416, 0xFF0E0C10, 0xFF08070B, 0x48FF8850, 0x426848D8},
        {0xFF0E1220, 0xFF080A13, 0xFF040509, 0x384A6CFF, 0x2E5A3CC8},
        {0xFF170E19, 0xFF0C070D, 0xFF050306, 0x38C050B4, 0x2E6440D2},
        {0xFF0C150F, 0xFF070C09, 0xFF030604, 0x3050B478, 0x28289682},
        {0xFF151617, 0xFF0B0C0D, 0xFF050506, 0x1CFFFFFF, 0x16A0B4C8},
        {0xFF0A1517, 0xFF060C0E, 0xFF030607, 0x3426B4C8, 0x2E2A64C0},
        {0xFF190D08, 0xFF0D0605, 0xFF060303, 0x40FF5A24, 0x32C8283C},
        {0xFF8C3A5E, 0xFF5E2348, 0xFF22101E, 0xA0FF8A4C, 0x80E0457A},
        {0xFF14588E, 0xFF15366E, 0xFF0A1632, 0x9030C8E0, 0x807A4CE0},
        {0xFF0F4048, 0xFF0F2E3A, 0xFF08141C, 0x9038E0A0, 0x90A050F0},
        {0xFFC0604E, 0xFF8A3456, 0xFF2E1230, 0x90FFB070, 0x70E04090},
        {0xFF5A3CAE, 0xFF3A2A84, 0xFF16123A, 0x90C080FF, 0x804A7CFF},
        {0xFF2E6E4C, 0xFF1C4A40, 0xFF0A201E, 0x90A8E070, 0x6030A8A8},
        {0xFFB07624, 0xFF86401E, 0xFF2A1410, 0x90FFD060, 0x70FF5040},
    };
    private float scale = 1f;
    private float uiScale = 1f;
    private float settingsScale = 1f;
    private static final int[] THEME_ROW_FONT_SIZES = {30, 29, 23, 32, 33, 27};

    private static Typeface regular, semibold, defaultThemeFont, cinzelFont;
    private static final Typeface[] themeFonts = new Typeface[6];

    private FrameLayout root; // the page, inside a vertical scroller
    private SkyView skyView;
    private LinearLayout clockContainer;
    private LinearLayout dateRowView;
    private Dialog activeDialog;
    private TextClock dateClockView, timeClockView;
    private TextView weatherView;
    private TextView fancySyncView;
    private long lastWeatherFetch = 0L;
    private int tileW, tileH, rowStepPx, gapPx, gridTop, cols = PER_ROW; // app grid geometry, set by buildTiles
    private static final int MAX_PAGE_APPS = 16; // max 16 apps (4x4) per horizontal page on phones/foldables
    private android.widget.HorizontalScrollView appPager;
    private int currentAppPage = 0;
    private final List<View> pageDots = new ArrayList<>();
    private final List<View> tiles = new ArrayList<>();
    private final List<View> chrome = new ArrayList<>();
    private final Map<View, ValueAnimator> lifts = new WeakHashMap<>(); // a running focus fade per tile
    private boolean dirty = true;
    private java.util.Locale shownLocale; // the TV's language (and region) when the page was built
    private String focusedKey;
    private final List<String> rowApps = new ArrayList<>(); // the top row as shown, in order
    private String movingKey;   // the app being moved with left/right, if any
    private TextView moveHint;  // shown above the apps while moving
    private boolean longPressed; // OK was held: swallow the key-up so the app doesn't open
    private boolean backHeld; // Back was held and opened Google TV's home screen: swallow the key-up
    // The tile OK went down on. An app that closes on OK's key-down (Netflix's "Exit") hands the matching
    // key-up to the tile still focused here, so a key-up only opens the app it went down on (#30).
    private String okDownKey;

    // Apps installed, removed or updated. This often lands once Home is already back in front (an app
    // uninstalled from the menu is gone only after Android's prompt has closed), so catch up then.
    private final BroadcastReceiver packagesChanged = new BroadcastReceiver() {
        @Override public void onReceive(Context c, Intent i) {
            dirty = true;
            refreshApps();
        }
    };

    // The network tile follows the connection live: Wi-Fi bars, Ethernet or Offline. Only its icon and
    // label change, so nothing is rebuilt.
    private ImageView netIcon;
    private TextView netLabelView;
    private ImageView glowIconView;
    private TextView glowLabelView;
    private int netState = -1;
    private final ConnectivityManager.NetworkCallback networkChanged = new ConnectivityManager.NetworkCallback() {
        @Override public void onAvailable(Network n) { runOnUiThread(MainActivity.this::refreshNetwork); }
        @Override public void onLost(Network n) { runOnUiThread(MainActivity.this::refreshNetwork); }
        // Also fires when the Wi-Fi signal gets stronger or weaker.
        @Override public void onCapabilitiesChanged(Network n, NetworkCapabilities c) {
            runOnUiThread(MainActivity.this::refreshNetwork);
        }
    };

    private int dp(float v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }

    private int sp(float v) {
        return Math.round(v * getResources().getDisplayMetrics().scaledDensity);
    }

    private int getStatusBarTopInset() {
        int inset = 0;
        try {
            int resId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resId > 0) inset = getResources().getDimensionPixelSize(resId);
        } catch (Throwable ignored) {}
        try {
            if (android.os.Build.VERSION.SDK_INT >= 28 && getWindow() != null && getWindow().getDecorView() != null) {
                android.view.WindowInsets wi = getWindow().getDecorView().getRootWindowInsets();
                if (wi != null && wi.getDisplayCutout() != null) {
                    inset = Math.max(inset, wi.getDisplayCutout().getSafeInsetTop());
                }
            }
        } catch (Throwable ignored) {}
        return Math.max(inset, dp(28));
    }

    private float computeAdaptiveScale() {
        android.util.DisplayMetrics dm = getResources().getDisplayMetrics();
        float sw = dm.widthPixels / 1920f;
        float sh = dm.heightPixels / 1080f;
        if (!isTvDevice() && dm.heightPixels > dm.widthPixels) {
            // Portrait phone / cover screen: scale sky particles & details with real display density
            return Math.max(0.68f, Math.max(sw * 1.32f, dm.density * 0.32f));
        }
        // On 16:9 TVs sw == sh; on square/unfolded Foldables or 16:10 Tablets, clamp so tiles & stage never clip
        return Math.max(0.42f, Math.min(sw, sh * 1.18f));
    }

    // Portrait/Outer-screen aware UI scale for Clock, Top-Right Quick Icons, and Utility Buttons
    // so portrait foldable screens scale to native Android density instead of a shrunk 1920p TV width.
    private float computeUiScale() {
        android.util.DisplayMetrics dm = getResources().getDisplayMetrics();
        if (!isTvDevice()) {
            if (dm.heightPixels > dm.widthPixels) {
                return Math.max(dm.density * 0.62f, dm.widthPixels / 720f);
            }
            return Math.max(scale * 1.12f, dm.density * 0.52f);
        }
        if (dm.heightPixels > dm.widthPixels) {
            return Math.max(scale * 1.48f, Math.min(dm.widthPixels / 1080f, dm.heightPixels / 1680f));
        }
        return scale;
    }

    private int upx(int v) {
        return Math.round(v * uiScale);
    }

    // Density-aware uniform scale for Settings UI (buttons, quick dock, dropdowns, and panels)
    // across Foldable cover screens, inner unfolded screens, Tablets, and TVs.
    private float computeSettingsScale() {
        android.util.DisplayMetrics dm = getResources().getDisplayMetrics();
        float longSide = Math.max(dm.widthPixels, dm.heightPixels) / 1920f;
        float shortSide = Math.min(dm.widthPixels, dm.heightPixels) / 1080f;
        float base = Math.min(longSide, shortSide);
        if (!isTvDevice()) {
            // Map 1080p TV spx() tokens directly to native Android phone/foldable dp/sp density
            return Math.max(dm.density * 0.72f, base);
        }
        if (dm.heightPixels > dm.widthPixels) {
            base = Math.min(base, dm.widthPixels / 1280f);
        }
        return Math.max(0.65f, base);
    }

    private int spx(int v) {
        return Math.round(v * settingsScale);
    }

    private boolean isTvDevice() {
        try {
            android.app.UiModeManager um = (android.app.UiModeManager) getSystemService(Context.UI_MODE_SERVICE);
            if (um != null && um.getCurrentModeType() == android.content.res.Configuration.UI_MODE_TYPE_TELEVISION) {
                return true;
            }
        } catch (Throwable ignored) {}
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_LEANBACK);
    }

    // Expands Android's native Notification Center & Quick Settings shade on Swipe-Down gesture
    @SuppressWarnings("WrongConstant")
    private void expandNotifications() {
        try {
            Object sbService = getSystemService("statusbar");
            if (sbService != null) {
                java.lang.reflect.Method expand = sbService.getClass().getMethod("expandNotificationsPanel");
                expand.invoke(sbService);
                return;
            }
        } catch (Throwable ignored) {}
        try {
            Object sbService = getSystemService("statusbar");
            if (sbService != null) {
                java.lang.reflect.Method expand = sbService.getClass().getMethod("expand");
                expand.invoke(sbService);
                return;
            }
        } catch (Throwable ignored) {}
    }

    private float gestureDownX, gestureDownY;
    private boolean gestureHandled;
    private boolean gestureLockedHorizontal;

    @Override
    public boolean dispatchTouchEvent(android.view.MotionEvent ev) {
        if (!isTvDevice() && (activeDialog == null || !activeDialog.isShowing())) {
            int action = ev.getActionMasked();
            if (action == android.view.MotionEvent.ACTION_DOWN) {
                gestureDownX = ev.getRawX();
                gestureDownY = ev.getRawY();
                gestureHandled = false;
                gestureLockedHorizontal = false;
            } else if (action == android.view.MotionEvent.ACTION_MOVE && !gestureHandled) {
                float dx = ev.getRawX() - gestureDownX;
                float dy = ev.getRawY() - gestureDownY;
                float touchSlop = ViewConfiguration.get(this).getScaledTouchSlop();
                float vertSlop = Math.max(dp(56), touchSlop * 2.6f);
                float horizSlop = Math.max(dp(38), touchSlop * 2.0f);
                int screenH = getResources().getDisplayMetrics().heightPixels;
                // Lock to horizontal scrolling if the user starts dragging sideways on the app grid
                if (!gestureLockedHorizontal && gestureDownY < screenH * 0.64f
                        && Math.abs(dx) > touchSlop * 1.15f && Math.abs(dx) >= Math.abs(dy) * 0.9f) {
                    gestureLockedHorizontal = true;
                }
                if (!gestureLockedHorizontal && Math.abs(dy) > vertSlop && Math.abs(dy) > Math.abs(dx) * 1.75f) {
                    gestureHandled = true;
                    android.view.MotionEvent cancel = android.view.MotionEvent.obtain(ev);
                    cancel.setAction(android.view.MotionEvent.ACTION_CANCEL);
                    super.dispatchTouchEvent(cancel);
                    cancel.recycle();
                    if (dy < 0) {
                        showAllAppsDrawer();
                    } else {
                        expandNotifications();
                    }
                    return true;
                } else if (gestureDownY >= screenH * 0.64f
                        && Math.abs(dx) > horizSlop && Math.abs(dx) > Math.abs(dy) * 1.25f && skyView != null) {
                    gestureHandled = true;
                    android.view.MotionEvent cancel = android.view.MotionEvent.obtain(ev);
                    cancel.setAction(android.view.MotionEvent.ACTION_CANCEL);
                    super.dispatchTouchEvent(cancel);
                    cancel.recycle();
                    if (dx < 0) skyView.nextStoryScene();
                    else skyView.prevStoryScene();
                    return true;
                }
            } else if ((action == android.view.MotionEvent.ACTION_UP || action == android.view.MotionEvent.ACTION_CANCEL)
                    && gestureHandled) {
                gestureHandled = false;
                gestureLockedHorizontal = false;
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        saveConfig(getIntent());
        loadFonts(this);
        scale = computeAdaptiveScale();
        uiScale = computeUiScale();
        settingsScale = computeSettingsScale();
        Window win = getWindow();
        win.setBackgroundDrawable(new ColorDrawable(0xFF060916));
        if (!isTvDevice()) {
            try {
                win.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                win.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                win.setStatusBarColor(Color.TRANSPARENT);
                win.setNavigationBarColor(Color.TRANSPARENT);
                if (android.os.Build.VERSION.SDK_INT >= 28) {
                    WindowManager.LayoutParams wlp = win.getAttributes();
                    wlp.layoutInDisplayCutoutMode = android.os.Build.VERSION.SDK_INT >= 30
                            ? WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
                            : WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
                    win.setAttributes(wlp);
                }
                win.getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            } catch (Throwable ignored) {}
        }

        FrameLayout viewport = new FrameLayout(this);
        skyView = new SkyView(this, scale, wallpaper(), cinzelFont);
        skyView.setUniverseTheme(universeTheme());
        boolean glowOn = prefs().getBoolean("edge_glow", true);
        skyView.setEdgeGlowEnabled(glowOn);
        viewport.addView(skyView, new FrameLayout.LayoutParams(-1, -1));

        ScrollView scroller = new ScrollView(this);
        scroller.setFillViewport(true);
        scroller.setVerticalScrollBarEnabled(false);
        scroller.setOverScrollMode(View.OVER_SCROLL_NEVER);
        scroller.setClipChildren(false);
        root = new FrameLayout(this);
        root.setClipChildren(false);
        // Touch & Stylus gesture support for Foldables and Tablets on the bottom Story Stage:
        // Tap right half / swipe left -> Next Story Scene; Tap left half / swipe right -> Previous Story Scene
        root.setOnTouchListener(new View.OnTouchListener() {
            private float downX, downY;
            @Override
            public boolean onTouch(View v, android.view.MotionEvent ev) {
                int h = v.getHeight();
                if (h <= 0 || ev.getY() < h * 0.62f) return false;
                if (ev.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    downX = ev.getX();
                    downY = ev.getY();
                    return true;
                } else if (ev.getAction() == android.view.MotionEvent.ACTION_UP && skyView != null) {
                    float dx = ev.getX() - downX;
                    if (Math.abs(dx) > px(42)) {
                        if (dx < 0) skyView.nextStoryScene();
                        else skyView.prevStoryScene();
                    } else if (Math.abs(ev.getY() - downY) < px(36)) {
                        if (ev.getX() >= v.getWidth() * 0.5f) skyView.nextStoryScene();
                        else skyView.prevStoryScene();
                    }
                    return true;
                }
                return false;
            }
        });
        scroller.addView(root, new FrameLayout.LayoutParams(-1, -2));
        viewport.addView(scroller, new FrameLayout.LayoutParams(-1, -1));

        LinearLayout clock = new LinearLayout(this);
        clockContainer = clock;
        clock.setOrientation(LinearLayout.VERTICAL);
        shownLocale = java.util.Locale.getDefault();
        String day = Strings.datePattern();
        LinearLayout dateRow = new LinearLayout(this);
        dateRowView = dateRow;
        dateRow.setOrientation(LinearLayout.HORIZONTAL);
        dateRow.setGravity(Gravity.CENTER_VERTICAL);
        TextClock date = clock(day, day, px(36), 0xF0FFE8B0, themeFontFor(universeTheme()));
        dateClockView = date;
        date.setLetterSpacing(0.05f);
        date.setShadowLayer(px(8), 0, px(2), 0xCC000000);
        dateRow.addView(date, new LinearLayout.LayoutParams(-2, -2));

        weatherView = text(px(36), 0xF0FFE8B0, themeFontFor(universeTheme()));
        weatherView.setLetterSpacing(0.05f);
        weatherView.setShadowLayer(px(8), 0, px(2), 0xCC000000);
        LinearLayout.LayoutParams wp = new LinearLayout.LayoutParams(-2, -2);
        wp.leftMargin = isTvDevice() ? upx(16) : dp(10);
        dateRow.addView(weatherView, wp);
        setWeatherText(prefs().getString("weather_text", "66°F Cloudy"));

        fancySyncView = text(isTvDevice() ? upx(16) : sp(11), 0xEEFFE082, cinzelFont);
        fancySyncView.setLetterSpacing(0.14f);
        fancySyncView.setText("|   FANCYLEDS SYNC");
        fancySyncView.setShadowLayer(px(8), 0, px(2), 0xAA000000);
        fancySyncView.setVisibility((glowOn && isTvDevice()) ? View.VISIBLE : View.GONE);
        LinearLayout.LayoutParams fp = new LinearLayout.LayoutParams(-2, -2);
        fp.leftMargin = upx(16);
        dateRow.addView(fancySyncView, fp);
        skyView.setHubStatusListener((online, ip) -> {
            if (fancySyncView != null) {
                boolean en = skyView != null && skyView.isEdgeGlowEnabled();
                fancySyncView.setVisibility((online && en) ? View.VISIBLE : View.GONE);
            }
        });

        // The clock follows the TV unless --es clock 12 or 24 says otherwise.
        String clockPref = prefs().getString(PREF_CLOCK, null);
        TextClock time = clock("24".equals(clockPref) ? "H:mm" : "h:mm", "12".equals(clockPref) ? "h:mm" : "H:mm",
                upx(144), 0xFFFFF4D2, themeFontFor(universeTheme()));
        timeClockView = time;
        time.setLetterSpacing(0.04f);
        time.setIncludeFontPadding(false);
        time.setPadding(dp(4), dp(2), dp(8), dp(6));
        time.setShadowLayer(dp(12), 0, dp(3), 0x99D4942A);
        // Wrap, not the default match_parent: the column must be as wide as the date, not just the time,
        // or "0:00" to "9:59" (one digit less) cuts a long date off.
        clock.addView(dateRow, new LinearLayout.LayoutParams(-2, -2));
        LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(-2, -2);
        tp.topMargin = isTvDevice() ? upx(-12) : -dp(4); // snug under the date + weather line
        clock.addView(time, tp);
        root.addView(clock, at(px(MARGIN), px(38), -2, -2));

        moveHint = text(upx(26), 0xFFE6FFE8, themeFontFor(universeTheme()));
        moveHint.setText(Strings.t("Use the arrows to move it, then press OK"));
        moveHint.setVisibility(View.GONE);
        root.addView(moveHint, at(px(MARGIN), px(ROW1_TOP - 64), -2, -2));
        applyThemeTypography(universeTheme());

        setContentView(viewport);

        IntentFilter f = new IntentFilter();
        f.addAction(Intent.ACTION_PACKAGE_ADDED);
        f.addAction(Intent.ACTION_PACKAGE_REMOVED);
        f.addAction(Intent.ACTION_PACKAGE_CHANGED);
        f.addDataScheme("package");
        if (android.os.Build.VERSION.SDK_INT >= 33) registerReceiver(packagesChanged, f, Context.RECEIVER_EXPORTED);
        else registerReceiver(packagesChanged, f);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) try {
            if (android.os.Build.VERSION.SDK_INT >= 24) cm.registerDefaultNetworkCallback(networkChanged);
            else cm.registerNetworkCallback(new NetworkRequest.Builder().build(), networkChanged);
        } catch (Exception ignored) {}
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(packagesChanged);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) try { cm.unregisterNetworkCallback(networkChanged); } catch (Exception ignored) {}
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        // A clock change needs the whole page rebuilt; a new app list only needs the tiles.
        boolean clock = intent != null && intent.hasExtra("clock");
        if (saveConfig(intent)) {
            if (clock) recreate();
            else buildTiles();
        }
    }

    // Saves an app list passed with --es apps "...", shortcuts passed with --es quick "...", and a
    // clock override passed with --es clock 12|24|auto. Returns true if it changed.
    // When Android kills the home screen in the background, it can start it again with the same old
    // intent; the stamp install.sh sends stops that from undoing changes made on the TV since.
    private boolean saveConfig(Intent intent) {
        String apps = intent == null ? null : intent.getStringExtra("apps");
        String quick = intent == null ? null : intent.getStringExtra("quick");
        String clock = intent == null ? null : intent.getStringExtra("clock");
        String fake = intent == null ? null : intent.getStringExtra("fakeinputs");
        String theme = intent == null ? null : intent.getStringExtra("theme");
        if (apps == null && quick == null && clock == null && fake == null && theme == null) return false;
        SharedPreferences prefs = getSharedPreferences(PREFS, 0);
        String stamp = intent.getStringExtra("stamp");
        // OWASP MASVS-PLATFORM-1: Require a valid numeric stamp from install.sh so untrusted 3rd-party
        // apps cannot inject arbitrary SharedPreferences via exported MainActivity intents.
        if (stamp == null || !stamp.matches("\\d{8,15}") || stamp.equals(prefs.getString("stamp", null))) return false;
        SharedPreferences.Editor e = prefs.edit();
        e.putString("stamp", stamp);
        if (theme != null) {
            try {
                int th = Integer.parseInt(theme.trim());
                if (th >= 0 && th < UNIVERSE_THEMES.length) {
                    e.putInt(PREF_THEME, th);
                    if (skyView != null) skyView.setUniverseTheme(th);
                }
            } catch (Exception ignored) {}
        }
        if (apps != null) {
            apps = apps.trim();
            if (apps.isEmpty() || apps.equalsIgnoreCase("auto")) {
                e.remove("apps");
            } else {
                StringBuilder valid = new StringBuilder();
                for (String p : apps.split("[,\\s]+")) {
                    if (p.matches("[a-zA-Z0-9_.]{3,128}")) {
                        if (valid.length() > 0) valid.append(',');
                        valid.append(p);
                    }
                }
                if (valid.length() > 0) e.putString("apps", valid.toString());
            }
        }
        if (quick != null) {
            quick = quick.trim();
            if (quick.isEmpty() || quick.equalsIgnoreCase("none")) e.remove("quick");
            else e.putString("quick", quick);
        }
        if (clock != null) {
            clock = clock.trim();
            if (clock.isEmpty() || clock.equalsIgnoreCase("auto")) e.remove(PREF_CLOCK);
            else e.putString(PREF_CLOCK, clock);
        }
        // Set only by install.sh's FAKE_INPUT=1, which also installs the throwaway input itself.
        // It is for an emulator, which has no HDMI ports, and never for a real TV.
        if (fake != null) {
            if (fake.trim().equals("1")) {
                e.putString(PREF_FAKE_INPUTS, "1");
                // An input taken off the home screen stays off, even when FAKE_INPUT=1 adds it back
                // (undo.sh uninstalls it, but running install.sh again does not). Left alone, the
                // emulator would come back with no input tile at all, so put the test input back.
                Set<String> hidden = hiddenInputs();
                for (java.util.Iterator<String> it = hidden.iterator(); it.hasNext(); )
                    if (it.next().startsWith(FAKE_INPUT_PKG + "/")) it.remove();
                e.putString(PREF_HIDDEN_INPUTS, android.text.TextUtils.join("\n", new TreeSet<>(hidden)));
            } else e.remove(PREF_FAKE_INPUTS);
        }
        e.apply();
        dirty = true;
        return true;
    }

    private List<String> appList(PackageManager pm) {
        List<String> out = new ArrayList<>();
        String saved = getSharedPreferences(PREFS, 0).getString("apps", null);
        if (saved != null) {
            for (String p : saved.split("[,\\s]+")) {
                if (!p.isEmpty() && !out.contains(p) && launchIntent(pm, p) != null) out.add(p);
            }
            if (!out.isEmpty()) return out;
        }
        // Primary 5-app custom icon pack slots (pick at most 1 installed package per slot first)
        String[][] iconPackSlots = {
            {"com.google.android.youtube.tv", "com.google.android.youtube"},
            {"com.surfshark.vpnclient.android"},
            {"in.startv.hotstar", "com.disney.disneyplus"},
            {"com.plexapp.android"},
            {"com.hulu.livingroomplus", "com.hulu.plus", "com.oumi.utility", "com.vidhub"}
        };
        for (String[] slot : iconPackSlots) {
            for (String p : slot) {
                if (!out.contains(p) && launchIntent(pm, p) != null) {
                    out.add(p);
                    break;
                }
            }
        }
        // Fallback to popular media & device default apps when the 5-app icon pack is not all present
        String[] deviceDefaults = {
            "com.netflix.ninja",
            "com.netflix.mediaclient",
            "com.amazon.amazonvideo.livingroom",
            "com.amazon.avod.thirdpartyclient",
            "com.android.chrome",
            "com.sec.android.app.sbrowser",
            "com.google.android.apps.photos",
            "com.sec.android.gallery3d",
            "com.google.android.apps.messaging",
            "com.samsung.android.messaging",
            "com.google.android.dialer",
            "com.samsung.android.dialer",
            "com.google.android.gm",
            "com.google.android.apps.maps",
            "com.google.android.GoogleCamera",
            "com.sec.android.app.camera",
            "com.android.camera2"
        };
        for (String p : deviceDefaults) {
            if (out.size() >= 5) break;
            if (!out.contains(p) && launchIntent(pm, p) != null) out.add(p);
        }
        if (out.size() < 5) {
            for (String p : Apps.LIST) {
                if (out.size() >= 5) break;
                if (!p.isEmpty() && !out.contains(p) && launchIntent(pm, p) != null) out.add(p);
            }
            // Query user-installed and updated apps across Android TV and Foldable/Tablet launchers
            for (String category : new String[] {Intent.CATEGORY_LEANBACK_LAUNCHER, Intent.CATEGORY_LAUNCHER}) {
                if (out.size() >= 5) break;
                Intent q = new Intent(Intent.ACTION_MAIN).addCategory(category);
                for (ResolveInfo ri : pm.queryIntentActivities(q, 0)) {
                    if (out.size() >= 5) break;
                    String p = ri.activityInfo.packageName;
                    int flags = ri.activityInfo.applicationInfo.flags;
                    boolean stockSystem = (flags & ApplicationInfo.FLAG_SYSTEM) != 0
                            && (flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0;
                    if (stockSystem || out.contains(p) || NOT_APPS.contains(p) || p.equals(getPackageName())) continue;
                    out.add(p);
                }
            }
            // Final fallback: include any default device launcher app so all 5 slots are always populated
            for (String category : new String[] {Intent.CATEGORY_LEANBACK_LAUNCHER, Intent.CATEGORY_LAUNCHER}) {
                if (out.size() >= 5) break;
                Intent q = new Intent(Intent.ACTION_MAIN).addCategory(category);
                for (ResolveInfo ri : pm.queryIntentActivities(q, 0)) {
                    if (out.size() >= 5) break;
                    String p = ri.activityInfo.packageName;
                    if (out.contains(p) || NOT_APPS.contains(p) || p.equals(getPackageName())) continue;
                    out.add(p);
                }
            }
        }
        return out;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Rebuild only when apps were installed, removed or updated.
        if (dirty) {
            dirty = false;
            buildTiles();
        }
        refreshNetwork();
        fetchWeatherAsync();
        if (skyView != null) skyView.setRunning(true);
        View f = getCurrentFocus();
        if (f == null && !tiles.isEmpty()) tiles.get(0).requestFocus();
        HomeButton.onHomeResumed(this); // back on this home screen: Home comes here again
        HomeButton.ensureBound(this);
        // Only counted as opened once it has stayed up a moment, so a crash right after opening still
        // counts as a failed launch.
        getWindow().getDecorView().postDelayed(homeOk, 1500);
    }

    private final Runnable homeOk = () -> HomeButton.onHomeOk(this);

    @Override
    protected void onPause() {
        super.onPause();
        if (skyView != null) skyView.setRunning(false);
        // Left normally (an app or Settings opened), so it didn't crash.
        getWindow().getDecorView().removeCallbacks(homeOk);
        HomeButton.onHomeOk(this);
    }

    // Back from a menu or a window on top (like the uninstall prompt): catch up on apps that changed.
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (skyView != null) skyView.setRunning(hasFocus);
        if (hasFocus) refreshApps();
        else okDownKey = null; // an OK still held finishes in the window that took over
    }

    // Rebuilds the tiles if apps changed, but only while Home is in front with no menu open and no
    // app being moved; otherwise the next onResume, focus change or end of the move does it.
    private void refreshApps() {
        if (!dirty || !hasWindowFocus() || movingKey != null) return;
        dirty = false;
        buildTiles();
    }

    // Adaptive configuration handling for Android TV, Foldables (unfold/fold postures), and Tablets (multi-window/rotation).
    @Override
    public void onConfigurationChanged(android.content.res.Configuration c) {
        super.onConfigurationChanged(c);
        if (!java.util.Locale.getDefault().equals(shownLocale)) {
            recreate();
            return;
        }
        if (activeDialog != null && activeDialog.isShowing()) {
            try { activeDialog.dismiss(); } catch (Exception ignored) {}
            activeDialog = null;
        }
        scale = computeAdaptiveScale();
        uiScale = computeUiScale();
        settingsScale = computeSettingsScale();
        if (skyView != null) skyView.setScale(scale);
        if (weatherView != null && weatherView.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            ((LinearLayout.LayoutParams) weatherView.getLayoutParams()).leftMargin = isTvDevice() ? upx(16) : dp(10);
        }
        if (fancySyncView != null) {
            fancySyncView.setTextSize(TypedValue.COMPLEX_UNIT_PX, isTvDevice() ? upx(16) : sp(11));
            if (fancySyncView.getLayoutParams() instanceof LinearLayout.LayoutParams) {
                ((LinearLayout.LayoutParams) fancySyncView.getLayoutParams()).leftMargin = upx(16);
            }
        }
        if (timeClockView != null) {
            timeClockView.setPadding(dp(4), dp(2), dp(8), dp(6));
            if (timeClockView.getLayoutParams() instanceof LinearLayout.LayoutParams) {
                ((LinearLayout.LayoutParams) timeClockView.getLayoutParams()).topMargin = isTvDevice() ? upx(-12) : -dp(4);
            }
        }
        if (moveHint != null) {
            moveHint.setTextSize(TypedValue.COMPLEX_UNIT_PX, isTvDevice() ? upx(26) : sp(14));
        }
        applyThemeTypography(universeTheme());
        buildTiles();
    }

    @Override
    public void onBackPressed() { /* home screen: nothing to go back to */ }

    // Holding Back opens Google TV's own home screen, where it stays on (see HomeButton). Spotted on a
    // key repeat, or when Back is let go if the remote sends none.
    @Override
    public boolean onKeyLongPress(int code, KeyEvent ev) {
        if (code == KeyEvent.KEYCODE_BACK && HomeButton.openGoogleTv(this)) {
            backHeld = true;
            return true;
        }
        return super.onKeyLongPress(code, ev);
    }

    @Override
    public boolean onKeyUp(int code, KeyEvent ev) {
        if (code == KeyEvent.KEYCODE_BACK) {
            boolean held = backHeld;
            backHeld = false;
            if (held) return true;
            if (ev.isTracking() && !ev.isCanceled()
                    && ev.getEventTime() - ev.getDownTime() >= ViewConfiguration.getLongPressTimeout()
                    && HomeButton.openGoogleTv(this)) return true;
        }
        return super.onKeyUp(code, ev);
    }

    // Static gradient, drawn by the window: no extra view, no animation. It's painted once into a
    // screen-sized picture: as three gradients, low-end GPUs paint it again on every frame of a focus
    // move, and those frames take two to six times as long (#69). On Android 8+ the picture moves to
    // the GPU, so the home screen uses no more memory than before; if anything fails, the gradients stay.
    private Drawable background(int[] c) {
        GradientDrawable base = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[] {c[0], c[1], c[2]});
        GradientDrawable top = radial(c[3], 0.12f, -0.08f, px(1450));
        GradientDrawable bottom = radial(c[4], 0.92f, 1.05f, px(1350));
        for (GradientDrawable g : new GradientDrawable[] {base, top, bottom}) g.setDither(true);
        Drawable layers = new LayerDrawable(new Drawable[] {base, top, bottom});
        int w = getResources().getDisplayMetrics().widthPixels, h = getResources().getDisplayMetrics().heightPixels;
        try {
            Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            layers.setBounds(0, 0, w, h);
            layers.draw(new Canvas(bm));
            if (android.os.Build.VERSION.SDK_INT >= 26) {
                Bitmap hw = bm.copy(Bitmap.Config.HARDWARE, false);
                if (hw != null) {
                    bm.recycle();
                    bm = hw;
                }
            }
            return new BitmapDrawable(getResources(), bm);
        } catch (Throwable t) {
            return layers;
        }
    }

    // The saved wallpaper, as an index into WALLPAPERS (Dusk if none is saved).
    private int wallpaper() {
        String saved = getSharedPreferences(PREFS, 0).getString("wallpaper", null);
        return Math.max(0, Arrays.asList(WALLPAPER_NAMES).indexOf(saved));
    }

    private void showWallpaper(int i) {
        if (skyView != null) skyView.setPalette(i);
    }

    // The wallpapers, each with a small dot of its colours. Moving through the list shows each one
    // behind the panel; OK keeps it, Back goes back to the one before.
    private void showWallpapers() {
        final int size = spx(44);
        panel(Strings.t("Wallpaper"), Arrays.asList(WALLPAPER_NAMES), pos -> {
            int[] c = WALLPAPERS[pos];
            GradientDrawable dot = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                    new int[] {glow(c[3], c[0]), c[1], glow(c[4], c[0])});
            dot.setShape(GradientDrawable.OVAL);
            dot.setStroke(Math.max(1, spx(2)), 0x26FFFFFF);
            dot.setSize(size, size);
            return dot;
        }, size, wallpaper(), this::showWallpaper, which -> {
            getSharedPreferences(PREFS, 0).edit().putString("wallpaper", WALLPAPER_NAMES[which]).apply();
            showWallpaper(which);
        });
    }

    // A glow colour made solid over the base, three times as strong, so a small dot shows it.
    private static int glow(int glow, int base) {
        float a = Math.min(1f, 3 * (glow >>> 24) / 255f);
        int r = Math.round(Color.red(base) + (Color.red(glow) - Color.red(base)) * a);
        int g = Math.round(Color.green(base) + (Color.green(glow) - Color.green(base)) * a);
        int b = Math.round(Color.blue(base) + (Color.blue(glow) - Color.blue(base)) * a);
        return Color.rgb(r, g, b);
    }

    private static GradientDrawable radial(int color, float cx, float cy, float r) {
        GradientDrawable g = new GradientDrawable();
        g.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        g.setColors(new int[] {color, color & 0x00FFFFFF});
        g.setGradientCenter(cx, cy);
        g.setGradientRadius(r);
        return g;
    }

    private void buildTiles() {
        // While tiles are swapped out, removing the focused one would hand focus down the row (and
        // overwrite focusedKey), so hold focus off until the new tiles are in.
        String want = focusedKey;
        int wantAt = rowApps.indexOf(want); // where it was, in case the app is gone now
        root.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        for (View t : tiles) {
            View wrap = (View) t.getParent();
            if (wrap != null && wrap.getParent() != null) {
                ((ViewGroup) wrap.getParent()).removeView(wrap);
            }
        }
        for (View c : chrome) {
            if (c.getParent() != null) ((ViewGroup) c.getParent()).removeView(c);
        }
        tiles.clear();
        chrome.clear();
        pageDots.clear();
        appPager = null;
        rowApps.clear();
        movingKey = null;
        moveHint.setVisibility(View.GONE);

        PackageManager pm = getPackageManager();
        int screenW = getResources().getDisplayMetrics().widthPixels;
        int screenH = getResources().getDisplayMetrics().heightPixels;
        boolean tvMode = isTvDevice();
        boolean isNarrowVertical = !tvMode && (screenW < screenH * 0.75f);
        int m = isNarrowVertical ? dp(18) : (!tvMode ? dp(22) : px(MARGIN));
        int g = isNarrowVertical ? dp(10) : (!tvMode ? dp(12) : px(GAP));
        int topInset = !tvMode ? getStatusBarTopInset() : 0;
        int clockTop = isNarrowVertical ? (topInset + dp(2)) : (!tvMode ? (topInset + dp(4)) : px(38));
        gapPx = g;

        if (clockContainer != null && clockContainer.getLayoutParams() instanceof FrameLayout.LayoutParams) {
            FrameLayout.LayoutParams clp = (FrameLayout.LayoutParams) clockContainer.getLayoutParams();
            if (isNarrowVertical) {
                clp.width = -1;
                clp.leftMargin = 0;
                clp.topMargin = clockTop;
                clockContainer.setGravity(Gravity.CENTER_HORIZONTAL);
                if (dateRowView != null) dateRowView.setGravity(Gravity.CENTER);
            } else {
                clp.width = -2;
                clp.leftMargin = m;
                clp.topMargin = clockTop;
                clockContainer.setGravity(Gravity.START);
                if (dateRowView != null) dateRowView.setGravity(Gravity.CENTER_VERTICAL);
            }
            clockContainer.setLayoutParams(clp);
        }

        List<String> apps = appList(pm);
        int launchable = 0;
        for (String pkg : apps) if (launchIntent(pm, pkg) != null) launchable++;

        boolean store = !apps.contains(PLAY_STORE) && launchIntent(pm, PLAY_STORE) != null;

        if (!tvMode) {
            // Dual-Posture Continuity for Phones, Foldables & Tablets with 16-App Horizontal Paging:
            // - Up to 16 apps per page (4 columns x 4 rows). Additional apps (>16) scroll horizontally across pages.
            // - Folded Cover Screen / Vertical Phone (isNarrowVertical):
            //   Centered Hero Clock + 4-column Native One UI / Pixel Squircle App Icons + crisp single-line labels.
            // - Unfolded Foldable Inner Screen / Tablet (!isNarrowVertical):
            //   Signature 16:9 Widescreen Banner Cards (Left Icon Medallion + Right Title/Lore Subtitle).
            for (String pkg : apps) {
                if (launchIntent(pm, pkg) != null) rowApps.add(pkg);
            }
            gridTop = isNarrowVertical ? (clockTop + dp(70)) : (clockTop + dp(72));
            ((FrameLayout.LayoutParams) moveHint.getLayoutParams()).leftMargin = m;
            ((FrameLayout.LayoutParams) moveHint.getLayoutParams()).topMargin = gridTop - dp(22);

            final int totalPages = Math.max(1, (launchable + MAX_PAGE_APPS - 1) / MAX_PAGE_APPS);
            final int firstPageApps = Math.min(launchable, MAX_PAGE_APPS);
            if (want != null && rowApps.contains(want)) {
                currentAppPage = rowApps.indexOf(want) / MAX_PAGE_APPS;
            }
            if (currentAppPage >= totalPages) currentAppPage = Math.max(0, totalPages - 1);

            // Reserve the bottom ~38% of the screen completely clear for the animated Story Stage
            int btnH = dp(24);
            int chevW = dp(80), chevH = dp(18);
            int dotsReserve = totalPages > 1 ? dp(14) : 0;
            int controlsReserve = dotsReserve + btnH + chevH + dp(14);
            int stageSafeBottom = Math.round(screenH * (isNarrowVertical ? 0.62f : 0.64f));
            int maxAvailGridH = Math.max(dp(160), stageSafeBottom - gridTop - controlsReserve);

            int visibleRows;
            int iconSize = dp(44);
            int labelH = dp(15);
            if (isNarrowVertical) {
                cols = 4;
                visibleRows = Math.min(4, Math.max(1, (firstPageApps + cols - 1) / cols));
                int w = (screenW - 2 * m - g * (cols - 1)) / cols;
                int maxRowStep = maxAvailGridH / visibleRows;
                int rowGap = visibleRows >= 4 ? dp(5) : (visibleRows == 3 ? dp(7) : dp(9));
                int targetIcon = visibleRows >= 4 ? dp(42) : (visibleRows == 3 ? dp(46) : dp(50));
                iconSize = Math.max(dp(34), Math.min(targetIcon, Math.min(Math.round(w * 0.58f), maxRowStep - labelH - rowGap)));
                int h = iconSize + labelH;
                tileW = w;
                tileH = h;
                rowStepPx = h + rowGap;
            } else {
                cols = firstPageApps <= 6 ? Math.min(3, Math.max(1, firstPageApps)) : 4;
                visibleRows = Math.min(4, Math.max(1, (firstPageApps + cols - 1) / cols));
                int w = (screenW - 2 * m - g * (cols - 1)) / cols;
                int h = w * 9 / 16;
                int maxRowH = (maxAvailGridH - (visibleRows - 1) * g) / visibleRows;
                if (h > maxRowH) {
                    h = Math.max(dp(54), maxRowH);
                }
                tileW = w;
                tileH = h;
                rowStepPx = h + g;
            }

            final int pagerH = visibleRows * rowStepPx + dp(4);
            appPager = new android.widget.HorizontalScrollView(this) {
                private float startX;
                private int startPage;
                @Override
                public boolean onTouchEvent(android.view.MotionEvent ev) {
                    int act = ev.getActionMasked();
                    if (act == android.view.MotionEvent.ACTION_DOWN) {
                        startX = ev.getRawX();
                        startPage = currentAppPage;
                    } else if ((act == android.view.MotionEvent.ACTION_UP || act == android.view.MotionEvent.ACTION_CANCEL)
                            && totalPages > 1) {
                        float dx = ev.getRawX() - startX;
                        int target = startPage;
                        int flipThreshold = dp(34);
                        if (dx < -flipThreshold && startPage < totalPages - 1) {
                            target = startPage + 1;
                        } else if (dx > flipThreshold && startPage > 0) {
                            target = startPage - 1;
                        } else {
                            target = Math.max(0, Math.min(totalPages - 1, Math.round((float) getScrollX() / screenW)));
                        }
                        currentAppPage = target;
                        updatePageDots(target);
                        final int scrollTarget = target * screenW;
                        post(() -> smoothScrollTo(scrollTarget, 0));
                        return true;
                    }
                    return super.onTouchEvent(ev);
                }
                @Override
                protected void onScrollChanged(int l, int t, int oldl, int oldt) {
                    super.onScrollChanged(l, t, oldl, oldt);
                    if (totalPages > 1 && screenW > 0) {
                        int p = Math.max(0, Math.min(totalPages - 1, Math.round((float) l / screenW)));
                        if (p != currentAppPage) {
                            currentAppPage = p;
                            updatePageDots(p);
                        }
                    }
                }
            };
            appPager.setHorizontalScrollBarEnabled(false);
            appPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
            appPager.setClipChildren(false);
            appPager.setClipToPadding(false);

            FrameLayout pagerContent = new FrameLayout(this);
            pagerContent.setClipChildren(false);
            int totalW = totalPages * screenW;
            appPager.addView(pagerContent, new FrameLayout.LayoutParams(totalW, pagerH));
            // Touch-through on empty space inside the app grid so tapping empty sky still works
            pagerContent.setMinimumWidth(totalW);
            pagerContent.setMinimumHeight(pagerH);
            root.addView(appPager, at(0, gridTop, screenW, pagerH));
            chrome.add(appPager);

            if (isNarrowVertical) {
                for (int i = 0; i < rowApps.size(); i++) {
                    String pkg = rowApps.get(i);
                    String label = pkg;
                    Drawable defaultIcon = null;
                    try { label = pm.getApplicationLabel(pm.getApplicationInfo(pkg, 0)).toString(); } catch (Exception ignored) {}
                    try { defaultIcon = pm.getApplicationIcon(pkg); } catch (Exception ignored) {}
                    addMobileAppTile(pagerContent, pkg, label, null, defaultIcon, gridX(i), gridY(i), tileW, tileH, iconSize);
                }
            } else {
                float bannerScale = Math.min(uiScale * 0.95f, tileH / 185f);
                for (int i = 0; i < rowApps.size(); i++) {
                    String pkg = rowApps.get(i);
                    String label = pkg;
                    Drawable defaultIcon = null;
                    try { label = pm.getApplicationLabel(pm.getApplicationInfo(pkg, 0)).toString(); } catch (Exception ignored) {}
                    try { defaultIcon = pm.getApplicationIcon(pkg); } catch (Exception ignored) {}
                    Drawable banner = SkyView.createThemeBanner(
                            getResources(), pkg, label, tileW, tileH, bannerScale,
                            themeFontFor(universeTheme()), cinzelFont, universeTheme(), defaultIcon, i, true);
                    addTile(pagerContent, pkg, label, banner, null, defaultIcon, gridX(i), gridY(i), tileW, tileH);
                }
            }

            if (currentAppPage > 0) {
                final int initialScroll = currentAppPage * screenW;
                appPager.post(() -> {
                    if (appPager != null) appPager.scrollTo(initialScroll, 0);
                });
            }

            int afterGridY = gridTop + pagerH;
            if (totalPages > 1) {
                LinearLayout dotsBar = new LinearLayout(this);
                dotsBar.setOrientation(LinearLayout.HORIZONTAL);
                dotsBar.setGravity(Gravity.CENTER);
                for (int p = 0; p < totalPages; p++) {
                    final int pageIndex = p;
                    View dot = new View(this);
                    dot.setOnClickListener(v -> {
                        currentAppPage = pageIndex;
                        updatePageDots(pageIndex);
                        if (appPager != null) appPager.smoothScrollTo(pageIndex * screenW, 0);
                    });
                    pageDots.add(dot);
                    dotsBar.addView(dot);
                }
                updatePageDots(currentAppPage);
                int dotsH = dp(14);
                root.addView(dotsBar, at(0, afterGridY + dp(2), screenW, dotsH));
                chrome.add(dotsBar);
                afterGridY += dotsH + dp(4);
            }

            int bottomTop = afterGridY + dp(4);

            // Compact 3-Button Control Row: [ Theme ]  [ + Add app ]  [ Settings ] + compact All Apps Handle below
            int pillGap = dp(6);
            int maxDockW = Math.min(screenW - 2 * m, dp(264));
            int btnW = (maxDockW - 2 * pillGap) / 3;
            int dockStartX = (screenW - (3 * btnW + 2 * pillGap)) / 2;

            addTile(root, THEME_PICK, "Theme", null, LineIcon.THEMES, null, dockStartX, bottomTop, btnW, btnH);
            addTile(root, ADD, Strings.t("Add app"), null, LineIcon.PLUS, null, dockStartX + btnW + pillGap, bottomTop, btnW, btnH);
            addTile(root, SETTINGS, Strings.t("Settings"), null, LineIcon.SETTINGS, null,
                    dockStartX + 2 * (btnW + pillGap), bottomTop, btnW, btnH);

            addWhisperChevron(root, (screenW - chevW) / 2, bottomTop + btnH + dp(4), chevW, chevH);

            // Never exceed screenH so the home screen never scrolls vertically on phones/foldables
            root.setMinimumHeight(0);
            netIcon = null;
            glowIconView = null;
        } else {
            int topCols = launchable <= PER_ROW ? Math.max(1, launchable) : PER_ROW;
            List<TvInputInfo> inputs = hdmiInputs();
            int room = Math.max(1, topCols - 2 - (store ? 1 : 0));
            if (inputs.size() > room) inputs = inputs.subList(0, room);
            int bottomCount = (store ? 1 : 0) + inputs.size() + 2;
            cols = Math.max(5, Math.min(PER_ROW, Math.max(topCols, bottomCount)));

            int w = (screenW - 2 * m - g * (cols - 1)) / cols;
            int h = w * 9 / 16;
            int bottomW = w;
            int bottomH = px(52);
            tileW = w;
            tileH = h;
            rowStepPx = Math.max(px(ROW_STEP), h + px(22));

            // Top-right quick toggles dock (icon-only): Glow On/Off, Theme Picker, Bluetooth, Sound, Picture, Wi-Fi.
            List<Integer> quick = quickList();
            int totalTopItems = quick.size() + 3; // Glow toggle + Theme picker + quick items + Wi-Fi
            int itemW = Math.round(56 * scale);
            int itemH = Math.round(50 * scale);
            int ig = Math.round(6 * scale), dockPad = Math.round(8 * scale);
            int gridRight = (screenW + (cols * w + (cols - 1) * g)) / 2;
            int dockW = totalTopItems * itemW + (totalTopItems - 1) * ig + 2 * dockPad;
            int dockH = itemH + 2 * dockPad;
            int dockX = Math.max(m, gridRight - dockW);
            int dockY = Math.max(px(20), px(58) - dockPad);
            int iconY = dockY + dockPad;

            int rows = Math.max(1, (launchable + cols - 1) / cols);
            gridTop = Math.max(px(GRID_MIN_TOP), px(ROW1_TOP) - (rows - 1) * rowStepPx);
            int bottomTop = gridTop + rows * rowStepPx + px(6);
            ((FrameLayout.LayoutParams) moveHint.getLayoutParams()).topMargin = gridTop - upx(42);
            root.setMinimumHeight(bottomTop + bottomH + m);

            for (String pkg : apps) {
                if (launchIntent(pm, pkg) != null) rowApps.add(pkg);
            }
            for (int i = 0; i < rowApps.size(); i++) {
                String pkg = rowApps.get(i);
                String label = pkg;
                Drawable defaultIcon = null;
                try { label = pm.getApplicationLabel(pm.getApplicationInfo(pkg, 0)).toString(); } catch (Exception ignored) {}
                try { defaultIcon = pm.getApplicationIcon(pkg); } catch (Exception ignored) {}
                Drawable banner = SkyView.createThemeBanner(
                        getResources(), pkg, label, w, h, scale, themeFontFor(universeTheme()), cinzelFont, universeTheme(), defaultIcon, i);
                addTile(root, pkg, label, banner, null, defaultIcon, gridX(i), gridY(i), w, h);
            }

            // Compact bottom row: aligned column-for-column with the app card columns above.
            int col = 0;
            int bottomRowW = bottomCount * bottomW + (bottomCount - 1) * g;
            int bottomStartX = (screenW - bottomRowW) / 2;
            if (store)
                addTile(root, PLAY_STORE, "Play Store", null, LineIcon.PLAY, null, bottomStartX + col++ * (bottomW + g), bottomTop, bottomW, bottomH);
            for (TvInputInfo in : inputs)
                addTile(root, in.getId(), inputLabel(in), null, LineIcon.HDMI, null, bottomStartX + col++ * (bottomW + g), bottomTop, bottomW, bottomH);
            addTile(root, SETTINGS, Strings.t("Settings"), null, LineIcon.SETTINGS, null, bottomStartX + col++ * (bottomW + g), bottomTop, bottomW, bottomH);
            addTile(root, ADD, Strings.t("Add app"), null, LineIcon.PLUS, null, bottomStartX + col * (bottomW + g), bottomTop, bottomW, bottomH);

            View dock = new View(this);
            GradientDrawable dockBg = new GradientDrawable();
            dockBg.setCornerRadius(dockH / 2f);
            dockBg.setColor(0x22181210);
            dockBg.setStroke(Math.max(1, px(1)), 0x38F5D078);
            dock.setBackground(dockBg);
            root.addView(dock, at(dockX, dockY, dockW, dockH));
            chrome.add(dock);

            int qx = dockX + dockPad;
            boolean glowEnabled = skyView == null || skyView.isEdgeGlowEnabled();
            glowIconView = addIcon(GLOW_TOGGLE, glowEnabled ? "Glow On" : "Glow Off", createGlowDrawable(glowEnabled), qx, iconY, itemW, itemH);
            qx += itemW + ig;
            addIcon(THEME_PICK, "Theme", createThemeDrawable(universeTheme()), qx, iconY, itemW, itemH);
            qx += itemW + ig;
            for (int q : quick) {
                addIcon(QUICK + QUICK_NAMES[q], Strings.t(QUICK_LABELS[q]), new LineIcon(QUICK_ICONS[q]), qx, iconY, itemW, itemH);
                qx += itemW + ig;
            }
            netIcon = addIcon(NETWORK, "Wi-Fi", null, qx, iconY, itemW, itemH);
            netState = -1;
            refreshNetwork();
        }

        root.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        // An app that was uninstalled hands focus to the one now in its place, as Remove does.
        if (wantAt >= 0 && !rowApps.contains(want))
            want = rowApps.isEmpty() ? ADD : rowApps.get(Math.min(wantAt, rowApps.size() - 1));
        View restore = null;
        for (View t : tiles) if (t.getTag().equals(want)) restore = t;
        if (restore != null) restore.requestFocus();
    }

    private void updatePageDots(int activePage) {
        for (int i = 0; i < pageDots.size(); i++) {
            View dot = pageDots.get(i);
            boolean active = (i == activePage);
            int dw = active ? dp(16) : dp(5);
            int dh = dp(5);
            GradientDrawable g = new GradientDrawable();
            g.setCornerRadius(dh / 2f);
            g.setColor(active ? 0xF0FFE082 : 0x55FFE082);
            dot.setBackground(g);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dw, dh);
            lp.leftMargin = dp(3);
            lp.rightMargin = dp(3);
            dot.setLayoutParams(lp);
        }
    }

    // Compact Swipe-Up Handle ("︿ ALL APPS") for phones, foldables, and tablets.
    private void addWhisperChevron(ViewGroup parent, int x, int y, int w, int h) {
        LinearLayout pill = new LinearLayout(this);
        pill.setOrientation(LinearLayout.HORIZONTAL);
        pill.setGravity(Gravity.CENTER);
        final GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(h / 2f);
        bg.setColor(0x3212101C);
        bg.setStroke(Math.max(1, dp(1)), 0x42F5D078);
        pill.setBackground(bg);
        pill.setPadding(dp(8), 0, dp(8), 0);

        ImageView iv = new ImageView(this);
        LineIcon li = new LineIcon(LineIcon.CHEVRON_UP);
        li.setColorFilter(0xFFFFE498, android.graphics.PorterDuff.Mode.SRC_IN);
        iv.setImageDrawable(li);
        int sz = dp(9);
        pill.addView(iv, new LinearLayout.LayoutParams(sz, sz));

        TextView tv = text(sp(8f), 0xDCFFE8B0, cinzelFont != null ? cinzelFont : semibold);
        tv.setText("ALL APPS");
        tv.setLetterSpacing(0.08f);
        tv.setSingleLine(true);
        LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(-2, -2);
        tlp.leftMargin = dp(3);
        pill.addView(tv, tlp);

        // Gentle breathing pulse on the chevron icon
        ValueAnimator pulse = ValueAnimator.ofFloat(0.55f, 1.0f);
        pulse.setDuration(1800);
        pulse.setRepeatCount(ValueAnimator.INFINITE);
        pulse.setRepeatMode(ValueAnimator.REVERSE);
        pulse.addUpdateListener(a -> {
            float v = (float) a.getAnimatedValue();
            iv.setAlpha(v);
            iv.setTranslationY(-dp(1f) * (v - 0.55f));
        });
        pulse.start();

        pill.setFocusable(true);
        pill.setTag(ALL_APPS);
        pill.setContentDescription("All Apps");
        pill.setOnClickListener(v -> showAllAppsDrawer(false));
        pill.setOnFocusChangeListener((v, has) -> {
            bg.setStroke(Math.max(1, dp(has ? 2 : 1)), has ? 0xEEFFE082 : 0x42F5D078);
            lift(v, bg, has, 0x3212101C, 0x55382A18, h / 2, h / 2);
        });

        FrameLayout wrap = new FrameLayout(this);
        wrap.setClipChildren(false);
        wrap.addView(pill, new FrameLayout.LayoutParams(w, h));
        parent.addView(wrap, at(x, y, w, h));
        tiles.add(pill);
    }

    // Native Phone / Foldable Cover Screen App Icon Tile:
    // Full-bleed One UI / Pixel style squircle app icon + crisp 13sp single-line label underneath.
    private void addMobileAppTile(ViewGroup parent, final String key, String label, Drawable badge, Drawable appIcon,
                                  int x, int y, int w, int h, int iconSize) {
        LinearLayout cell = new LinearLayout(this);
        cell.setOrientation(LinearLayout.VERTICAL);
        cell.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        cell.setClipChildren(false);
        cell.setFocusable(true);
        cell.setTag(key);

        final int badgeRadius = Math.round(iconSize * 0.26f);
        final FrameLayout iconBox = new FrameLayout(this);
        final GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(badgeRadius);
        bg.setColor(0x480E101E);
        iconBox.setBackground(bg);
        iconBox.setClipToOutline(true);
        iconBox.setElevation(dp(4));

        if (appIcon != null) {
            ImageView iconIv = new ImageView(this);
            iconIv.setScaleType(ImageView.ScaleType.FIT_CENTER);
            iconIv.setImageDrawable(appIcon);
            FrameLayout.LayoutParams ilp = new FrameLayout.LayoutParams(-1, -1);
            ilp.gravity = Gravity.CENTER;
            iconBox.addView(iconIv, ilp);
        } else if (badge != null) {
            ImageView iv = new ImageView(this);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            iv.setImageDrawable(badge);
            iconBox.addView(iv, new FrameLayout.LayoutParams(-1, -1));
        }

        final GradientDrawable border = new GradientDrawable();
        border.setCornerRadius(badgeRadius);
        border.setColor(Color.TRANSPARENT);
        border.setStroke(Math.max(1, dp(1)), 0x38FFE082);
        iconBox.setForeground(border);
        cell.addView(iconBox, new LinearLayout.LayoutParams(iconSize, iconSize));

        final TextView titleView = text(sp(11f), 0xF8FFF8EC, semibold);
        titleView.setText(label);
        titleView.setIncludeFontPadding(false);
        titleView.setSingleLine(true);
        titleView.setEllipsize(android.text.TextUtils.TruncateAt.END);
        titleView.setGravity(Gravity.CENTER_HORIZONTAL);
        titleView.setShadowLayer(dp(4), 0, dp(1.5f), 0xEE000000);
        LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(w, -2);
        tlp.topMargin = dp(3);
        cell.addView(titleView, tlp);

        final int houseCol = SkyView.themeAccentFor(key);
        final int focusBorderCol = blend(0xFFFFE082, (0xFF << 24) | (houseCol & 0xFFFFFF), 0.45f);
        cell.setOnFocusChangeListener((v, has) -> {
            if (has) {
                focusedKey = key;
                if (skyView != null) {
                    skyView.setReactiveColor(houseCol, false);
                    skyView.onTileFocused(x + w * 0.5f, y + iconSize * 0.5f, y, houseCol);
                }
            }
            iconBox.animate().cancel();
            iconBox.animate().scaleX(has ? 1.08f : 1f).scaleY(has ? 1.08f : 1f)
                    .translationY(has ? -dp(3) : 0)
                    .setDuration(FOCUS_MS).setInterpolator(FOCUS_EASE).withLayer().start();
            if (!key.equals(movingKey)) {
                border.setStroke(Math.max(1, dp(has ? 2 : 1)), has ? focusBorderCol : 0x38FFE082);
            }
            titleView.setTextColor(has ? 0xFFFFFFFF : 0xF8FFF8EC);
        });

        cell.setOnClickListener(v -> open(key));
        cell.setOnLongClickListener(v -> { showEditMenu(key); return true; });
        cell.setOnKeyListener((v, code, ev) -> {
            boolean ok = code == KeyEvent.KEYCODE_DPAD_CENTER || code == KeyEvent.KEYCODE_ENTER
                    || code == KeyEvent.KEYCODE_NUMPAD_ENTER;
            boolean down = ev.getAction() == KeyEvent.ACTION_DOWN;
            if (key.equals(movingKey)) return moveKey(key, code, ok, down);
            if (code == KeyEvent.KEYCODE_MENU) {
                if (!down) showEditMenu(key);
                return true;
            }
            if (!ok) return false;
            if (down) {
                if (ev.getRepeatCount() == 0) {
                    longPressed = false;
                    okDownKey = key;
                } else if (!longPressed && (ev.isLongPress()
                        || ev.getEventTime() - ev.getDownTime() >= ViewConfiguration.getLongPressTimeout())) {
                    longPressed = true;
                    showEditMenu(key);
                }
            } else {
                boolean pressed = key.equals(okDownKey) && !ev.isCanceled();
                okDownKey = null;
                if (pressed && !longPressed) open(key);
            }
            return true;
        });

        FrameLayout wrap = new FrameLayout(this);
        wrap.setClipChildren(false);
        wrap.addView(cell, new FrameLayout.LayoutParams(w, -2));
        parent.addView(wrap, at(x, y, w, -2));
        tiles.add(cell);
    }

    private void addTile(ViewGroup parent, final String key, String label, Drawable banner, int[] lineIcon, Drawable appIcon,
                         int x, int y, int w, int h) {
        FrameLayout card = new FrameLayout(this);
        final GradientDrawable bg = new GradientDrawable();
        final int cardRadius = banner != null ? px(RADIUS) : (h / 2);
        bg.setCornerRadius(cardRadius);
        if (banner != null) {
            bg.setColor(0xFF1E1A1C);
            ImageView iv = new ImageView(this);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            iv.setImageDrawable(banner);
            card.addView(iv, new FrameLayout.LayoutParams(-1, -1));
            if (appIcon != null && (!isTvDevice() || !SkyView.isCustomIconPackApp(key))) {
                // Hardware-accelerated circular medallion ImageView for default device app icons
                // (On phones/foldables/tablets, always show the real default device icon on the left; on TV, keep centered)
                boolean horiz = !isTvDevice();
                float ir = SkyView.getMedallionRadius(w, h, horiz);
                float mcx = SkyView.getMedallionCenterX(w, h, horiz);
                float mcy = SkyView.getMedallionCenterY(w, h, horiz);
                int d = Math.max(1, Math.round(ir * 2f));
                ImageView iconIv = new ImageView(this);
                iconIv.setScaleType(ImageView.ScaleType.FIT_CENTER);
                iconIv.setImageDrawable(appIcon);
                GradientDrawable medBg = new GradientDrawable();
                medBg.setShape(GradientDrawable.OVAL);
                medBg.setColor(0xFF0C0914);
                iconIv.setBackground(medBg);
                iconIv.setClipToOutline(true);
                FrameLayout.LayoutParams ilp = new FrameLayout.LayoutParams(d, d);
                ilp.leftMargin = Math.round(mcx - ir);
                ilp.topMargin = Math.round(mcy - ir);
                card.addView(iconIv, ilp);
            }
        } else {
            // Compact dark-gold capsule pill for bottom utility tiles, scaled with native dp/sp on phones/foldables.
            boolean mobilePill = !isTvDevice();
            bg.setColor(mobilePill ? 0x3812101C : 0x28181210);
            LinearLayout inner = new LinearLayout(this);
            boolean pill = lineIcon != null;
            inner.setOrientation(pill ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL);
            inner.setGravity(Gravity.CENTER);
            int padX = pill ? (mobilePill ? dp(6) : Math.max(px(8), Math.round(h * 0.20f))) : px(14);
            inner.setPadding(padX, 0, padX, 0);
            ImageView iv = new ImageView(this);
            int size = pill ? (mobilePill ? dp(12) : Math.max(px(16), Math.round(h * 0.42f))) : px(64);
            if (lineIcon != null) {
                LineIcon li = new LineIcon(lineIcon);
                li.setColorFilter(0xFFFFE498, android.graphics.PorterDuff.Mode.SRC_IN);
                iv.setImageDrawable(li);
            } else if (appIcon != null) {
                iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
                iv.setImageDrawable(appIcon);
            }
            inner.addView(iv, new LinearLayout.LayoutParams(size, size));
            int gapX = pill ? (mobilePill ? dp(4) : Math.max(px(5), Math.round(h * 0.14f))) : 0;
            int textPx = pill ? (mobilePill ? sp(10f) : Math.max(px(12), Math.round(h * 0.30f))) : px(15);
            TextView t = text(textPx, 0xF5FFF4D2, mobilePill ? semibold : (cinzelFont != null ? cinzelFont : semibold));
            t.setText(label);
            if (pill && label != null) {
                float availW = w - 2 * padX - size - gapX - (mobilePill ? dp(2) : px(4));
                float measured = t.getPaint().measureText(label);
                if (availW > 0 && measured > availW) {
                    t.setTextSize(TypedValue.COMPLEX_UNIT_PX, Math.max(mobilePill ? sp(8.5f) : px(10), textPx * (availW / measured)));
                }
            }
            t.setSingleLine(true);
            t.setEllipsize(android.text.TextUtils.TruncateAt.END);
            t.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -2);
            if (pill) lp.leftMargin = gapX;
            else lp.topMargin = px(10);
            inner.addView(t, lp);
            card.addView(inner, new FrameLayout.LayoutParams(-1, -1));
        }
        card.setBackground(bg);
        card.setClipToOutline(true);
        card.setFocusable(true);
        card.setTag(key);

        // Custom theme banners already include the title & lore subtitle inside the card.
        final TextView caption = null;

        final GradientDrawable border = new GradientDrawable();
        border.setCornerRadius(cardRadius);
        border.setColor(Color.TRANSPARENT);
        border.setStroke(Math.max(1, px(2)), banner != null ? 0x38F5D078 : 0x42F5D078);
        card.setForeground(border);

        final boolean glass = banner == null;
        final int houseCol = SkyView.themeAccentFor(key);
        final int focusBorderCol = blend(0xFFFFE082, (0xFF << 24) | (houseCol & 0xFFFFFF), 0.42f);
        card.setOnFocusChangeListener((v, has) -> {
            if (has) {
                focusedKey = key;
                boolean prism = key.contains("HW17") || key.contains("HDMI300004");
                if (skyView != null) {
                    skyView.setReactiveColor(houseCol, prism);
                    skyView.onTileFocused(x + w * 0.5f, y + h * 0.5f, y, houseCol);
                }
                if (fancySyncView != null) {
                    fancySyncView.setTextColor(blend(0xFFFFE8A8, (0xFF << 24) | (houseCol & 0xFFFFFF), 0.45f));
                }
            }
            lift(v, bg, has, glass ? 0x28181210 : 0xFF141018, glass ? 0x44382A18 : 0xFF141018,
                    cardRadius, cardRadius);
            if (!key.equals(movingKey)) {
                border.setCornerRadius(cardRadius);
                border.setStroke(Math.max(1, px(has ? 3 : 2)), has ? focusBorderCol : (glass ? 0x42F5D078 : 0x38F5D078));
            }
            if (caption != null) {
                caption.setTextColor(has ? 0xFFFFF4D2 : 0x98FFE8B8);
                caption.setTypeface(has ? semibold : regular);
            }
        });
        final boolean editable = rowApps.contains(key);
        card.setOnClickListener(v -> open(key));
        if (editable) card.setOnLongClickListener(v -> { showEditMenu(key); return true; });
        // What a long OK (or Menu) on this tile opens: the app row's menu, an input's menu, or, on
        // the + tile, the inputs taken off the home screen.
        final Runnable menu = editable ? () -> showEditMenu(key)
                : isInput(key) ? () -> showInputMenu(key)
                : ADD.equals(key) && !hiddenInputs().isEmpty() ? this::showHiddenInputs : null;
        card.setOnKeyListener((v, code, ev) -> {
            boolean ok = code == KeyEvent.KEYCODE_DPAD_CENTER || code == KeyEvent.KEYCODE_ENTER
                    || code == KeyEvent.KEYCODE_NUMPAD_ENTER;
            boolean down = ev.getAction() == KeyEvent.ACTION_DOWN;
            if (key.equals(movingKey)) return moveKey(key, code, ok, down);
            if (menu != null && code == KeyEvent.KEYCODE_MENU) {
                if (!down) menu.run();
                return true;
            }
            if (!ok) return false;
            if (down) {
                if (ev.getRepeatCount() == 0) {
                    longPressed = false;
                    okDownKey = key;
                } else if (menu != null && !longPressed && (ev.isLongPress()
                        || ev.getEventTime() - ev.getDownTime() >= ViewConfiguration.getLongPressTimeout())) {
                    longPressed = true;
                    menu.run();
                }
            } else {
                // Only the key-up of an OK that went down on this tile opens the app, and not a cancelled
                // one (focus left while OK was held). View does the same for a click with isPressed().
                boolean pressed = key.equals(okDownKey) && !ev.isCanceled();
                okDownKey = null;
                if (pressed && !longPressed) open(key);
            }
            return true;
        });

        FrameLayout wrap = new FrameLayout(this);
        wrap.setClipChildren(false);
        wrap.addView(card, new FrameLayout.LayoutParams(w, h));
        if (caption != null) {
            FrameLayout.LayoutParams cp = new FrameLayout.LayoutParams(w, -2);
            cp.topMargin = h + px(22);
            wrap.addView(caption, cp);
        }
        parent.addView(wrap, at(x, y, w, -2));
        tiles.add(card);
    }

    private void addSectionLabel(String title, int x, int y) {
        TextView tv = text(px(20), 0xB8F5D488, cinzelFont != null ? cinzelFont : semibold);
        tv.setText(title);
        tv.setLetterSpacing(0.16f);
        tv.setShadowLayer(px(6), 0, px(2), 0x88000000);
        root.addView(tv, at(x + px(4), y, -2, -2));
        chrome.add(tv);
    }

    // The focus lift: the view grows and rises while its background fades and rounds off. A new
    // change cancels the fade the view was running, so a fast run of d-pad presses only ever shows
    // the tile in front and the one it just left mid-move.
    private void lift(View v, GradientDrawable bg, boolean has, int restColor, int focusColor,
                      int restRadius, int focusRadius) {
        v.animate().cancel();
        v.animate().scaleX(has ? 1.06f : 1f).scaleY(has ? 1.06f : 1f)
                .translationY(has ? -px(6) : 0).translationZ(has ? px(26) : 0)
                .setDuration(FOCUS_MS).setInterpolator(FOCUS_EASE).withLayer().start();
        tween(v, bg, has, restColor, focusColor, restRadius, focusRadius);
    }

    // Just the background part: colour and corner radius, with no view animation. Used for the
    // dropdown rows, whose text already changes colour on focus.
    private void tween(View v, GradientDrawable bg, boolean has, int restColor, int focusColor,
                       int restRadius, int focusRadius) {
        if (bg == null) return;
        ValueAnimator old = lifts.remove(v);
        if (old != null) old.cancel();
        final int from = has ? restColor : focusColor, to = has ? focusColor : restColor;
        final int r0 = has ? restRadius : focusRadius, r1 = has ? focusRadius : restRadius;
        if (from == to && r0 == r1) return;
        // Tweened by hand so no objects are made per frame: this runs on TVs with 2 GB of RAM.
        final ValueAnimator a = ValueAnimator.ofFloat(0f, 1f);
        a.setDuration(FOCUS_MS);
        a.setInterpolator(FOCUS_EASE);
        a.addUpdateListener(an -> {
            float t = (float) an.getAnimatedValue();
            if (from != to) bg.setColor(blend(from, to, t));
            if (r0 != r1) bg.setCornerRadius(r0 + (r1 - r0) * t);
        });
        a.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator an) { if (lifts.get(v) == a) lifts.remove(v); }
        });
        lifts.put(v, a);
        a.start();
    }

    private static int blend(int from, int to, float t) {
        return (channel(from, to, 24, t) << 24) | (channel(from, to, 16, t) << 16)
                | (channel(from, to, 8, t) << 8) | channel(from, to, 0, t);
    }

    private static int channel(int from, int to, int shift, float t) {
        int a = (from >>> shift) & 0xFF, b = (to >>> shift) & 0xFF;
        return Math.round(a + (b - a) * t);
    }

    // Top-right quick icon button (icon-only, no text wording) inside the glass dock pill.
    private ImageView addIcon(final String key, String label, Drawable icon, int x, final int y, int w, int h) {
        int size = Math.max(px(22), Math.round(h * 0.56f));
        FrameLayout card = new FrameLayout(this);
        final GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(h / 2f);
        bg.setColor(Color.TRANSPARENT);
        card.setBackground(bg);

        ImageView iv = new ImageView(this);
        if (icon != null) {
            icon.setColorFilter(0xFFFFE8B0, android.graphics.PorterDuff.Mode.SRC_IN);
            iv.setImageDrawable(icon);
        }
        FrameLayout.LayoutParams ip = new FrameLayout.LayoutParams(size, size);
        ip.gravity = Gravity.CENTER;
        card.addView(iv, ip);

        card.setFocusable(true);
        card.setTag(key);
        card.setContentDescription(label);
        card.setOnFocusChangeListener((v, has) -> {
            if (has) {
                focusedKey = key;
                if (skyView != null) skyView.onTileFocused(x + w * 0.5f, y + h * 0.5f, y, 0xFFFFE082);
            }
            bg.setStroke(has ? Math.max(1, px(2)) : 0, has ? 0xEEFFE082 : Color.TRANSPARENT);
            lift(v, bg, has, Color.TRANSPARENT, 0x38382A18, h / 2, h / 2);
        });
        card.setOnClickListener(v -> open(key));
        FrameLayout wrap = new FrameLayout(this);
        wrap.setClipChildren(false);
        wrap.addView(card, new FrameLayout.LayoutParams(w, h));
        root.addView(wrap, at(x, y, w, h));
        tiles.add(card);
        return iv;
    }

    private Drawable createGlowDrawable(boolean on) {
        LineIcon li = new LineIcon(on ? LineIcon.GLOW_ON : LineIcon.GLOW_OFF);
        li.setColorFilter(0xFFFFE8B0, android.graphics.PorterDuff.Mode.SRC_IN);
        if (!on) li.setAlpha(0x88);
        return li;
    }

    private int universeTheme() {
        return Math.max(0, Math.min(UNIVERSE_THEMES.length - 1,
                getSharedPreferences(PREFS, 0).getInt(PREF_THEME, 0)));
    }

    private static Typeface themeFontFor(int theme) {
        int idx = Math.max(0, Math.min(themeFonts.length - 1, theme));
        return themeFonts[idx] != null ? themeFonts[idx] : (defaultThemeFont != null ? defaultThemeFont : semibold);
    }

    private void applyThemeTypography(int theme) {
        Typeface tf = themeFontFor(theme);
        android.util.DisplayMetrics dm = getResources().getDisplayMetrics();
        boolean isPortrait = dm.heightPixels > dm.widthPixels;
        int datePx, timePx;
        if (!isTvDevice()) {
            datePx = isPortrait ? (theme == 0 ? sp(15f) : sp(12.5f)) : (theme == 0 ? sp(16f) : sp(13f));
            timePx = isPortrait ? (theme == 0 ? sp(50f) : sp(42f)) : (theme == 0 ? sp(54f) : sp(44f));
        } else {
            datePx = isPortrait ? (theme == 0 ? upx(32) : upx(24)) : (theme == 0 ? px(36) : px(26));
            timePx = isPortrait ? (theme == 0 ? upx(118) : upx(92)) : (theme == 0 ? px(144) : px(108));
        }
        if (dateClockView != null) {
            dateClockView.setTypeface(tf);
            dateClockView.setTextSize(TypedValue.COMPLEX_UNIT_PX, datePx);
        }
        if (weatherView != null) {
            weatherView.setTypeface(tf);
            weatherView.setTextSize(TypedValue.COMPLEX_UNIT_PX, datePx);
            setWeatherText(prefs().getString("weather_text", "66°F Cloudy"));
        }
        if (timeClockView != null) {
            timeClockView.setTypeface(tf);
            timeClockView.setTextSize(TypedValue.COMPLEX_UNIT_PX, timePx);
        }
        if (moveHint != null) moveHint.setTypeface(tf);
    }

    /**
     * Consistent LineIcon for the top-right Android 3-Card Swatch Fan Theme button.
     */
    private Drawable createThemeDrawable(int theme) {
        LineIcon li = new LineIcon(LineIcon.THEMES);
        li.setColorFilter(0xFFFFE8B0, android.graphics.PorterDuff.Mode.SRC_IN);
        return li;
    }

    /**
     * High-detail, unmistakable franchise icon next to each Universe Theme in the Theme Picker list:
     * 0: Harry Potter — Round Spectacles + Lightning Bolt Scar + Golden Snitch Wings
     * 1: Naruto — Crimson 3-Tomoe Sharingan Eye with Curved Tomoe Commas & Pupil
     * 2: Demon Slayer — Tanjiro's Rising-Sun Hanafuda Earring & Checkered Haori
     * 3: Star Wars — Darth Vader Helmet Mask & Crossed Blue/Red Lightsabers
     * 4: MCU — Iron Man Gold/Crimson Faceplate Mask & Glowing Cyan Arc Reactor
     * 5: DCU — Classic Gotham Golden Oval Bat-Emblem & Superman Diamond Shield
     */
    private Drawable createThemeRowIcon(int pos, int size) {
        Bitmap bm = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        float cx = size * 0.5f, cy = size * 0.5f, r = size * 0.45f;
        int col = UNIVERSE_THEME_COLORS[Math.max(0, Math.min(UNIVERSE_THEME_COLORS.length - 1, pos))];

        // Rich badge medallion background with inner rim
        p.setStyle(Paint.Style.FILL);
        p.setColor(blend(col, 0xFF080610, 0.78f));
        c.drawCircle(cx, cy, r, p);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(Math.max(1.8f, 2.2f * scale));
        p.setColor(col);
        c.drawCircle(cx, cy, r, p);

        float er = r * 0.68f;
        if (pos == 0) {
            // HARRY POTTER: Round Glasses + Golden Lightning Bolt Scar above bridge
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(Math.max(2f, 2.4f * scale));
            p.setColor(0xFFFFF4D2);
            c.drawCircle(cx - er * 0.46f, cy + er * 0.18f, er * 0.36f, p);
            c.drawCircle(cx + er * 0.46f, cy + er * 0.18f, er * 0.36f, p);
            c.drawLine(cx - er * 0.10f, cy + er * 0.15f, cx + er * 0.10f, cy + er * 0.15f, p);
            // Golden Lightning Scar above glasses
            p.setColor(0xFFFFD54F);
            Path scar = new Path();
            scar.moveTo(cx + er * 0.12f, cy - er * 0.85f);
            scar.lineTo(cx - er * 0.12f, cy - er * 0.40f);
            scar.lineTo(cx + er * 0.10f, cy - er * 0.40f);
            scar.lineTo(cx - er * 0.10f, cy - er * 0.02f);
            c.drawPath(scar, p);
        } else if (pos == 1) {
            // NARUTO: Authentic Crimson 3-Tomoe Sharingan Eye
            p.setStyle(Paint.Style.FILL);
            p.setColor(0xFFE51C23);
            c.drawCircle(cx, cy, er * 0.90f, p);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(Math.max(1.5f, 1.8f * scale));
            p.setColor(0xFF140406);
            c.drawCircle(cx, cy, er * 0.90f, p);
            c.drawCircle(cx, cy, er * 0.54f, p);
            p.setStyle(Paint.Style.FILL);
            c.drawCircle(cx, cy, er * 0.24f, p);
            for (int tm = 0; tm < 3; tm++) {
                double a = tm * Math.PI * 2.0 / 3.0 - Math.PI / 2.0;
                float tx = cx + (float) Math.cos(a) * er * 0.54f;
                float ty = cy + (float) Math.sin(a) * er * 0.54f;
                c.drawCircle(tx, ty, er * 0.16f, p);
                // Comma tail
                double ta = a + 0.42;
                c.drawCircle(cx + (float) Math.cos(ta) * er * 0.62f, cy + (float) Math.sin(ta) * er * 0.62f, er * 0.09f, p);
            }
        } else if (pos == 2) {
            // DEMON SLAYER: Tanjiro's Rising Sun Hanafuda Card & Nichirin Flame Wheel
            p.setStyle(Paint.Style.FILL);
            p.setColor(0xFFFDF8F0);
            c.drawRoundRect(new android.graphics.RectF(cx - er * 0.56f, cy - er * 0.82f, cx + er * 0.56f, cy + er * 0.82f), er * 0.12f, er * 0.12f, p);
            p.setColor(0xFFE52028);
            c.drawCircle(cx, cy - er * 0.22f, er * 0.32f, p);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(Math.max(1.5f, 1.8f * scale));
            for (int ry = 0; ry < 7; ry++) {
                double a = Math.PI * 0.15 + ry * Math.PI * 0.70 / 6.0;
                c.drawLine(cx, cy - er * 0.22f, cx + (float) Math.cos(a) * er * 0.54f, cy - er * 0.22f + (float) Math.sin(a) * er * 0.85f, p);
            }
        } else if (pos == 3) {
            // STAR WARS: Darth Vader Helmet Silhouette + Crossed Blue & Red Sabers
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(Math.max(2f, 2.5f * scale));
            p.setColor(0xFF29B6F6);
            c.drawLine(cx - er * 0.80f, cy + er * 0.75f, cx + er * 0.80f, cy - er * 0.75f, p);
            p.setColor(0xFFFF1744);
            c.drawLine(cx - er * 0.80f, cy - er * 0.75f, cx + er * 0.80f, cy + er * 0.75f, p);
            // Vader Helmet Dome & Triangular Grille in center
            p.setStyle(Paint.Style.FILL);
            p.setColor(0xFF1C222E);
            Path vd = new Path();
            vd.moveTo(cx, cy - er * 0.72f);
            vd.quadTo(cx + er * 0.65f, cy - er * 0.65f, cx + er * 0.78f, cy + er * 0.35f);
            vd.lineTo(cx, cy + er * 0.68f);
            vd.lineTo(cx - er * 0.78f, cy + er * 0.35f);
            vd.quadTo(cx - er * 0.65f, cy - er * 0.65f, cx, cy - er * 0.72f);
            vd.close();
            c.drawPath(vd, p);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(Math.max(1.4f, 1.7f * scale));
            p.setColor(0xFFD8E4F0);
            c.drawPath(vd, p);
            // Vader eyes & mouth triangle
            c.drawOval(new android.graphics.RectF(cx - er * 0.44f, cy - er * 0.15f, cx - er * 0.08f, cy + er * 0.08f), p);
            c.drawOval(new android.graphics.RectF(cx + er * 0.08f, cy - er * 0.15f, cx + er * 0.44f, cy + er * 0.08f), p);
            Path vt = new Path();
            vt.moveTo(cx, cy + er * 0.12f);
            vt.lineTo(cx - er * 0.28f, cy + er * 0.48f);
            vt.lineTo(cx + er * 0.28f, cy + er * 0.48f);
            vt.close();
            c.drawPath(vt, p);
        } else if (pos == 4) {
            // MCU: Iron Man Red/Gold Helmet Mask with Glowing Cyan Eyes
            p.setStyle(Paint.Style.FILL);
            p.setColor(0xFFD32F2F);
            Path im = new Path();
            im.moveTo(cx, cy - er * 0.85f);
            im.lineTo(cx + er * 0.68f, cy - er * 0.55f);
            im.lineTo(cx + er * 0.62f, cy + er * 0.38f);
            im.lineTo(cx + er * 0.28f, cy + er * 0.85f);
            im.lineTo(cx - er * 0.28f, cy + er * 0.85f);
            im.lineTo(cx - er * 0.62f, cy + er * 0.38f);
            im.lineTo(cx - er * 0.68f, cy - er * 0.55f);
            im.close();
            c.drawPath(im, p);
            // Gold faceplate
            p.setColor(0xFFFFCA28);
            Path fp = new Path();
            fp.moveTo(cx - er * 0.48f, cy - er * 0.38f);
            fp.lineTo(cx + er * 0.48f, cy - er * 0.38f);
            fp.lineTo(cx + er * 0.42f, cy + er * 0.55f);
            fp.lineTo(cx - er * 0.42f, cy + er * 0.55f);
            fp.close();
            c.drawPath(fp, p);
            // Glowing Cyan Iron Man Eyes
            p.setColor(0xFF00E5FF);
            c.drawRect(cx - er * 0.40f, cy - er * 0.12f, cx - er * 0.10f, cy + er * 0.04f, p);
            c.drawRect(cx + er * 0.10f, cy - er * 0.12f, cx + er * 0.40f, cy + er * 0.04f, p);
        } else {
            // DCU: Classic Golden Oval Batman Bat-Symbol
            p.setStyle(Paint.Style.FILL);
            p.setColor(0xFFFFD54F);
            c.drawOval(new android.graphics.RectF(cx - er * 0.96f, cy - er * 0.62f, cx + er * 0.96f, cy + er * 0.62f), p);
            p.setColor(0xFF0A0E18);
            Path bt = new Path();
            bt.moveTo(cx, cy - er * 0.22f);
            bt.lineTo(cx + er * 0.12f, cy - er * 0.45f); // Right bat ear
            bt.lineTo(cx + er * 0.20f, cy - er * 0.20f);
            bt.lineTo(cx + er * 0.82f, cy - er * 0.28f); // Right wingtip
            bt.quadTo(cx + er * 0.62f, cy + er * 0.25f, cx + er * 0.32f, cy + er * 0.22f);
            bt.lineTo(cx, cy + er * 0.48f); // Tail point
            bt.lineTo(cx - er * 0.32f, cy + er * 0.22f);
            bt.quadTo(cx - er * 0.62f, cy + er * 0.25f, cx - er * 0.82f, cy - er * 0.28f);
            bt.lineTo(cx - er * 0.20f, cy - er * 0.20f);
            bt.lineTo(cx - er * 0.12f, cy - er * 0.45f); // Left bat ear
            bt.close();
            c.drawPath(bt, p);
        }
        return new BitmapDrawable(getResources(), bm);
    }

    private void showThemePicker() {
        final int size = spx(44);
        final int current = universeTheme();
        panel("Universe Theme", Arrays.asList(UNIVERSE_THEMES), pos -> createThemeRowIcon(pos, size), size, current, pos -> {
            if (skyView != null) skyView.setUniverseTheme(pos);
            applyThemeTypography(pos);
        }, which -> {
            getSharedPreferences(PREFS, 0).edit().putInt(PREF_THEME, which).apply();
            if (skyView != null) skyView.setUniverseTheme(which);
            applyThemeTypography(which);
            focusedKey = THEME_PICK;
            buildTiles();
        });
    }

    private void toggleEdgeGlow() {
        if (skyView == null) return;
        boolean next = !skyView.isEdgeGlowEnabled();
        skyView.setEdgeGlowEnabled(next);
        prefs().edit().putBoolean("edge_glow", next).apply();
        if (glowLabelView != null) glowLabelView.setText(next ? "Glow On" : "Glow Off");
        if (glowIconView != null) glowIconView.setImageDrawable(createGlowDrawable(next));
        if (fancySyncView != null) fancySyncView.setVisibility(next ? View.VISIBLE : View.GONE);
    }

    private void open(String key) {
        Intent i;
        if (GLOW_TOGGLE.equals(key)) {
            toggleEdgeGlow();
            return;
        } else if (THEME_PICK.equals(key)) {
            showThemePicker();
            return;
        } else if (ALL_APPS.equals(key)) {
            showAllAppsDrawer(false);
            return;
        } else if (ADD.equals(key)) {
            showAddMenu(-1);
            return;
        } else if (SETTINGS.equals(key)) {
            // Deduplicated Settings menu tailored to device class:
            // Theme has its own dedicated button; on TV, Glow also has its own dock button.
            List<String> list = new ArrayList<>();
            if (!isTvDevice()) {
                list.add("Pin / Manage Apps");
            }
            list.add("Sky Palette");
            if (!isTvDevice()) {
                boolean glowOn = skyView == null || skyView.isEdgeGlowEnabled();
                list.add(glowOn ? "Turn Glow Off" : "Turn Glow On");
            }
            list.add("Next Story Scene");
            if (isTvDevice()) {
                if (!isBox(this)) list.add("Brightness");
                list.add("TV settings");
            } else {
                list.add("System Settings");
            }
            list.add("Privacy & Safety");
            final String[] items = list.toArray(new String[0]);
            dropdown(tile(SETTINGS), items, which -> {
                String picked = items[which];
                if ("Pin / Manage Apps".equals(picked)) {
                    showAllAppsDrawer(true);
                } else if ("Sky Palette".equals(picked) || "Wallpaper".equals(picked)) {
                    showWallpapers();
                } else if ("Next Story Scene".equals(picked)) {
                    if (skyView != null) skyView.nextStoryScene();
                } else if (picked.startsWith("Turn Glow")) {
                    toggleEdgeGlow();
                } else if ("Brightness".equals(picked)) {
                    openBrightness();
                } else if ("Privacy & Safety".equals(picked)) {
                    showPrivacySafetyDialog();
                } else {
                    openTvSettings();
                }
            });
            return;
        } else if (isInput(key)) {
            openInput(key);
            return;
        } else if (NETWORK.equals(key)) {
            openNetworkSettings();
            return;
        } else if (key.startsWith(QUICK)) {
            openQuick(key.substring(QUICK.length()));
            return;
        } else {
            i = launchIntent(getPackageManager(), key);
            if (i == null) return;
        }
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try { startActivity(i); } catch (Exception ignored) {}
    }

    // Live TV apps an HDMI tile can open, in order. Droidlogic (Amlogic) TVs ship Live TV under their
    // own package, with TvActivity as its entry point.
    private static final String[][] LIVE_TV = {{"com.android.tv", "com.android.tv.MainActivity"},
            {"com.droidlogic.android.tv", "com.android.tv.TvActivity"}};
    private static final String INPUT_PLAYER = "com.google.android.tv.inputplayer";

    // Opens an HDMI input: the stock Live TV app first, then Droidlogic's, then whatever the TV picks.
    // Each one is tried only when the one before it can't start, so a TV where the stock app already
    // worked takes exactly the same path as before. The one exception: when the TV itself sends HDMI to
    // Google TV's input player, that goes first, because some Google TVs ship com.android.tv as a stub
    // that starts fine but only says "Hello World!" (issue #5).
    private void openInput(String id) {
        Intent view = new Intent(Intent.ACTION_VIEW, TvContract.buildChannelUriForPassthroughInput(id));
        List<Intent> tries = new ArrayList<>();
        ResolveInfo def = getPackageManager().resolveActivity(view, PackageManager.MATCH_DEFAULT_ONLY);
        if (def != null && def.activityInfo != null && INPUT_PLAYER.equals(def.activityInfo.packageName))
            tries.add(new Intent(view).setClassName(INPUT_PLAYER, def.activityInfo.name));
        for (String[] live : LIVE_TV) {
            Intent t = new Intent(view).setClassName(live[0], live[1]);
            if (t.resolveActivity(getPackageManager()) != null) tries.add(t);
        }
        tries.add(view);
        for (Intent t : tries) {
            t.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                startActivity(t);
                return;
            } catch (Exception ignored) {}
        }
    }

    // The shortcuts picked with --es quick, in that order, as indexes into QUICK_NAMES. A shortcut
    // this TV has no screen for is left out, so it never shows a tile that does nothing.
    private List<Integer> quickList() {
        List<Integer> out = new ArrayList<>();
        String saved = getSharedPreferences(PREFS, 0).getString("quick", "bluetooth,sound,picture");
        if (saved == null) return out;
        for (String name : saved.split("[,\\s]+")) {
            int q = Arrays.asList(QUICK_NAMES).indexOf(name.toLowerCase(java.util.Locale.ROOT));
            if (q < 0 || out.contains(q) || quickIntents(QUICK_NAMES[q]).isEmpty()) continue;
            out.add(q);
        }
        return out;
    }

    // OFFLINE, 1 to 3 for Wi-Fi with that many bars, or ETHERNET (any other link, which on a TV is a cable).
    private int networkState() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return OFFLINE;
        try {
            int rssi = Integer.MIN_VALUE;
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                NetworkCapabilities c = cm.getNetworkCapabilities(cm.getActiveNetwork());
                if (c == null) return OFFLINE;
                if (!c.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) return ETHERNET;
                if (android.os.Build.VERSION.SDK_INT >= 29) rssi = c.getSignalStrength();
            } else {
                android.net.NetworkInfo ni = cm.getActiveNetworkInfo();
                if (ni == null || !ni.isConnected()) return OFFLINE;
                if (ni.getType() != ConnectivityManager.TYPE_WIFI) return ETHERNET;
            }
            if (rssi == Integer.MIN_VALUE) { // Android 9 and older: ask the Wi-Fi service
                WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (wm != null && wm.getConnectionInfo() != null) rssi = wm.getConnectionInfo().getRssi();
            }
            return 1 + WifiManager.calculateSignalLevel(rssi, 3);
        } catch (Exception e) {
            return OFFLINE;
        }
    }

    private void refreshNetwork() {
        if (netIcon == null) return;
        int state = networkState();
        if (state == netState) return;
        netState = state;
        if (state == ETHERNET) {
            LineIcon eth = new LineIcon(LineIcon.ETHERNET);
            eth.setColorFilter(0xFFFFE8B0, android.graphics.PorterDuff.Mode.SRC_IN);
            netIcon.setImageDrawable(eth);
        } else {
            // The whole Wi-Fi fan dim, with the bars it has bright on top. Offline is a slash instead.
            LineIcon fan = new LineIcon(LineIcon.WIFI);
            fan.setColorFilter(0xFFFFE8B0, android.graphics.PorterDuff.Mode.SRC_IN);
            fan.setAlpha(0x47);
            int[] top = state == OFFLINE ? LineIcon.SLASH : state == 1 ? LineIcon.WIFI_1
                    : state == 2 ? LineIcon.WIFI_2 : LineIcon.WIFI;
            LineIcon topIcon = new LineIcon(top);
            topIcon.setColorFilter(0xFFFFE8B0, android.graphics.PorterDuff.Mode.SRC_IN);
            netIcon.setImageDrawable(new LayerDrawable(new Drawable[] {fan, topIcon}));
        }
        String netLabel = state == OFFLINE ? Strings.t("Offline") : state == ETHERNET ? "Ethernet" : "Wi-Fi";
        if (netLabelView != null) netLabelView.setText(netLabel);
        ((View) netIcon.getParent()).setContentDescription(netLabel);
    }

    // Wi-Fi settings (on Android TV that page lists Ethernet too), else the TV's network page, else Settings.
    private void openNetworkSettings() {
        for (String a : new String[] {Settings.ACTION_WIFI_SETTINGS, Settings.ACTION_WIRELESS_SETTINGS,
                Settings.ACTION_SETTINGS}) {
            try {
                startActivity(new Intent(a).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                return;
            } catch (Exception ignored) {}
        }
    }

    // Screens a shortcut can open, best first, keeping only those this TV has.
    private List<Intent> quickIntents(String name) {
        List<Intent> all = new ArrayList<>();
        String tclType = name.equals("sound") ? "SoundPreset" : name.equals("picture") ? "picture" : null;
        // Some TCL Google TVs (Q651G, Android 12) ship the same service inside the TV's own settings app.
        if (tclType != null)
            for (String pkg : new String[] {"com.tcl.settings", "com.android.tv.settings"})
                all.add(new Intent(TCL_SHOW_WINDOW).setClassName(pkg, "com.tcl.settings.ShowWindowService")
                        .putExtra("Type", tclType));
        switch (name) {
            case "bluetooth":
                all.add(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                all.add(new Intent("com.google.android.intent.action.CONNECT_INPUT")); // Android TV's pairing screen
                break;
            case "sound": all.add(new Intent(Settings.ACTION_SOUND_SETTINGS)); break;
            case "picture": all.add(new Intent(Settings.ACTION_DISPLAY_SETTINGS)); break;
        }
        PackageManager pm = getPackageManager();
        List<Intent> out = new ArrayList<>();
        for (Intent i : all) {
            boolean service = TCL_SHOW_WINDOW.equals(i.getAction());
            if (service ? pm.resolveService(i, 0) != null : i.resolveActivity(pm) != null) out.add(i);
        }
        return out;
    }

    // Opens a shortcut's screen: the first one that starts wins. False if none did.
    private boolean openQuick(String name) {
        for (Intent i : quickIntents(name)) {
            try {
                if (TCL_SHOW_WINDOW.equals(i.getAction())) startService(i);
                else startActivity(i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                return true;
            } catch (Exception ignored) {}
        }
        return false;
    }

    // The TV's own picture screen, where brightness lives: the Picture shortcut's screens first (TCL's
    // panel, then Android's Display page), then Android TV's Display & Sound page, else TV settings.
    // Only system screens are started, so it needs no permission and changes nothing by itself.
    private void openBrightness() {
        if (openQuick("picture")) return;
        Intent i = new Intent().setClassName(TV_DISPLAY_SOUND[0], TV_DISPLAY_SOUND[1]);
        if (i.resolveActivity(getPackageManager()) != null) {
            try {
                startActivity(i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                return;
            } catch (Exception ignored) {}
        }
        openTvSettings();
    }

    private void openTvSettings() {
        try { startActivity(new Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)); }
        catch (Exception ignored) {}
    }

    interface Pick { void at(int which); }
    interface Icon { Drawable at(int which) throws Exception; }

    private void panel(String title, List<String> labels, Pick pick) {
        panel(title, labels, null, 0, -1, null, pick);
    }

    // A side panel in the home screen's own look: a title, then one row per choice, with the same
    // glass focus as the tiles. Back closes it. icon (or null) puts an icon of that size by each name.
    // With preview, the list starts on selected and moving through it calls preview; Back calls it
    // with selected again, to put things back.
    private void panel(String title, final List<String> labels, final Icon icon, final int size,
                       final int selected, final Pick preview, final Pick pick) {
        if (activeDialog != null && activeDialog.isShowing()) {
            try { activeDialog.dismiss(); } catch (Exception ignored) {}
        }
        final Dialog d = new Dialog(this, android.R.style.Theme_Material_Dialog_NoActionBar);
        activeDialog = d;
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(spx(36), spx(56), spx(36), spx(36));

        TextView head = text(spx(32), 0xFFFFF4D2, cinzelFont != null ? cinzelFont : semibold);
        head.setLetterSpacing(0.08f);
        head.setText(title);
        head.setSingleLine(true);
        head.setEllipsize(android.text.TextUtils.TruncateAt.END);
        head.setPadding(spx(20), 0, spx(20), 0);
        box.addView(head);

        ListView list = new ListView(this);
        list.setDivider(null);
        list.setVerticalScrollBarEnabled(false);
        list.setOverScrollMode(View.OVER_SCROLL_NEVER);
        GradientDrawable focus = new GradientDrawable();
        focus.setCornerRadius(spx(RADIUS));
        focus.setColor(0x38FFE8B0);
        focus.setStroke(Math.max(1, spx(2)), 0x99FFE082);
        list.setSelector(focus);
        // Icons load as rows appear, drawn small to save RAM.
        final Drawable[] icons = new Drawable[labels.size()];
        final int iconBox = size > 0 ? spx(44) : 0;
        list.setAdapter(new BaseAdapter() {
            @Override public int getCount() { return labels.size(); }
            @Override public Object getItem(int pos) { return labels.get(pos); }
            @Override public long getItemId(int pos) { return pos; }
            @Override public View getView(int pos, View convert, ViewGroup parent) {
                boolean isThemePanel = "Universe Theme".equals(title);
                Typeface rowTf = isThemePanel ? themeFontFor(pos) : (cinzelFont != null ? cinzelFont : semibold);
                int rowSize = isThemePanel
                        ? spx(THEME_ROW_FONT_SIZES[Math.max(0, Math.min(THEME_ROW_FONT_SIZES.length - 1, pos))])
                        : spx(24);
                TextView t = convert != null ? (TextView) convert : text(rowSize, 0xF2FFF4D2, rowTf);
                t.setTypeface(rowTf);
                t.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowSize);
                t.setLetterSpacing(0.05f);
                t.setText(labels.get(pos));
                t.setSingleLine(true);
                t.setEllipsize(android.text.TextUtils.TruncateAt.END);
                t.setGravity(Gravity.CENTER_VERTICAL);
                t.setMinHeight(spx(78));
                t.setPadding(spx(20), 0, spx(20), 0);
                if (icon != null && icons[pos] == null) {
                    try { icons[pos] = icon.at(pos); }
                    catch (Exception e) { icons[pos] = new ColorDrawable(Color.TRANSPARENT); }
                    icons[pos].setBounds(0, 0, iconBox, iconBox);
                }
                t.setCompoundDrawablesRelative(icons[pos], null, null, null);
                t.setCompoundDrawablePadding(spx(20));
                return t;
            }
        });
        list.setOnItemClickListener((parent, v, pos, id) -> {
            d.dismiss();
            pick.at(pos);
        });
        if (preview != null) {
            list.setSelection(selected);
            list.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override public void onItemSelected(android.widget.AdapterView<?> p, View v, int pos, long id) {
                    preview.at(pos);
                }
                @Override public void onNothingSelected(android.widget.AdapterView<?> p) {}
            });
            d.setOnCancelListener(x -> preview.at(selected));
        }
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, 0, 1f);
        lp.topMargin = spx(24);
        box.addView(list, lp);

        d.setContentView(box);
        int screenW = getResources().getDisplayMetrics().widthPixels;
        int panelW = Math.min(spx(600), Math.round(screenW * 0.84f));
        Window w = d.getWindow();
        w.setBackgroundDrawable(new ColorDrawable(preview != null ? 0xFF161113 : 0xF7161113));
        w.setGravity(Gravity.END);
        w.setDimAmount(preview != null ? 0f : 0.5f); // a preview needs the home screen undimmed
        w.setLayout(panelW, -1);
        d.show();
        w.setLayout(panelW, -1);
    }

    // A small menu that drops down under a tile, in the same glass as the tiles. Opens above the
    // tile when there's no room below. Back closes it.
    private void dropdown(View anchor, String[] items, final Pick pick) {
        if (anchor == null) return;
        if (activeDialog != null && activeDialog.isShowing()) {
            try { activeDialog.dismiss(); } catch (Exception ignored) {}
        }
        final Dialog d = new Dialog(this, android.R.style.Theme_Material_Dialog_NoActionBar);
        activeDialog = d;
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        int pad = spx(10), rowH = spx(58);
        box.setPadding(pad, pad, pad, pad);
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(spx(RADIUS));
        bg.setColor(0xF5141018);
        bg.setStroke(Math.max(1, spx(2)), 0x55F5D078);
        box.setBackground(bg);
        float widest = 0; // some languages need more room than the tile is wide
        Typeface menuTf = cinzelFont != null ? cinzelFont : semibold;
        for (int i = 0; i < items.length; i++) {
            final int which = i;
            final TextView t = text(spx(22), 0xEBFFF4D2, menuTf);
            t.setLetterSpacing(0.06f);
            t.setText(Strings.t(items[i]));
            t.setSingleLine(true);
            t.setEllipsize(android.text.TextUtils.TruncateAt.END);
            t.setGravity(Gravity.CENTER_VERTICAL);
            t.setPadding(spx(22), 0, spx(22), 0);
            widest = Math.max(widest, t.getPaint().measureText(t.getText().toString()) * 1.12f + 2 * spx(22) + 2 * pad);
            final GradientDrawable focus = new GradientDrawable();
            focus.setCornerRadius(spx(RADIUS - 8));
            focus.setColor(Color.TRANSPARENT);
            t.setBackground(focus);
            t.setFocusable(true);
            t.setOnFocusChangeListener((v, has) -> {
                tween(t, focus, has, Color.TRANSPARENT, 0x38FFE8B0, spx(RADIUS - 8), spx(RADIUS - 8));
                t.setTextColor(has ? 0xFFFFF8E0 : 0xEBFFF4D2);
            });
            t.setOnClickListener(v -> {
                d.dismiss();
                pick.at(which);
            });
            box.addView(t, new LinearLayout.LayoutParams(-1, rowH));
        }
        d.setContentView(box);

        // Line it up with the tile as drawn (focused tiles are scaled up), clamped cleanly on Foldables/Tablets.
        int screenW = getResources().getDisplayMetrics().widthPixels;
        int screenH = getResources().getDisplayMetrics().heightPixels;
        int[] at = new int[2];
        anchor.getLocationOnScreen(at);
        int tileBottom = at[1] + Math.round(anchor.getHeight() * anchor.getScaleY());
        int w = Math.min(screenW - spx(32),
                Math.max(Math.max(Math.round(anchor.getWidth() * anchor.getScaleX()), spx(300)), (int) Math.ceil(widest)));
        int h = items.length * rowH + 2 * pad;
        int y = tileBottom + spx(12);
        if (y + h > screenH - spx(24)) y = Math.max(spx(24), at[1] - h - spx(12));
        int x = Math.max(spx(16), Math.min(at[0], screenW - w - spx(16)));
        Window win = d.getWindow();
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        win.clearFlags(android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        win.setGravity(Gravity.TOP | Gravity.START);
        android.view.WindowManager.LayoutParams lp = win.getAttributes();
        lp.x = x;
        lp.y = y;
        lp.width = w;
        lp.height = h;
        win.setAttributes(lp);
        d.show();
        win.setAttributes(lp);
        win.setLayout(w, h);
    }

    // Hold OK (or Menu) on a top-row app: move it, swap it for another, or take it off the home
    // screen. Removing only takes the tile away: the app stays installed and can be added back.
    // Apps the user installed can also be uninstalled; Android asks to confirm, and the tile goes
    // once the app is gone. Apps that came with the TV can't be, so they get App info instead,
    // where Android offers to turn them off.
    private void showEditMenu(final String key) {
        final int index = rowApps.indexOf(key);
        if (index < 0) return;
        final boolean user = userInstalled(key);
        dropdown(tile(key), user ? EDIT_ITEMS : EDIT_ITEMS_SYSTEM, which -> {
            if (which == 0) startMove(key);
            else if (which == 1) showAddMenu(index);
            else if (which == 3) {
                Uri pkg = Uri.parse("package:" + key);
                try { startActivity(user ? new Intent(Intent.ACTION_DELETE, pkg)
                        : new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, pkg)); }
                catch (Exception ignored) {}
            } else {
                rowApps.remove(index);
                saveRow();
                focusedKey = rowApps.isEmpty() ? ADD : rowApps.get(Math.min(index, rowApps.size() - 1));
                buildTiles();
            }
        });
    }

    // Hold OK on an HDMI tile: name it after the box plugged into it, or take it off the home
    // screen. Hiding keeps the input in the TV's list, so nothing about the TV changes.
    private void showInputMenu(final String id) {
        View anchor = tile(id);
        if (anchor == null) return;
        dropdown(anchor, new String[] {"Rename", "Remove from home"}, which -> {
            if (which == 0) showInputNames(id);
            else hideInput(id);
        });
    }

    // The TV's own name comes first, and picking it resets the custom one; the rest are the usual
    // things people plug in, followed by an option to type a name with the TV's keyboard.
    private void showInputNames(final String id) {
        TvInputInfo in = inputById(id);
        if (in == null) return;
        String own = tvInputLabel(in);
        List<String> names = new ArrayList<>();
        names.add(own);
        for (String p : INPUT_PRESETS) if (!p.equals(own)) names.add(p);
        // Keep canonical preset names in storage so a language change also updates the tile.
        List<String> labels = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) labels.add(i == 0 ? own : Strings.t(names.get(i)));
        labels.add(Strings.t("Type a name…"));
        panel(Strings.t("Rename"), labels, which -> {
            if (which == names.size()) {
                showInputNameEditor(id, own);
                return;
            }
            setInputName(id, which == 0 ? null : names.get(which));
            focusedKey = id;
            buildTiles();
        });
    }

    private void showInputNameEditor(final String id, String own) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        EditText name = new EditText(builder.getContext());
        name.setSingleLine(true);
        name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        name.setImeOptions(EditorInfo.IME_ACTION_DONE);
        name.setFilters(new InputFilter[] {new InputFilter.LengthFilter(24)});
        String custom = inputNames().get(id);
        name.setText(custom != null ? custom : own);
        final String initialName = name.getText().toString();
        name.setSelectAllOnFocus(true);
        name.setTextSize(TypedValue.COMPLEX_UNIT_PX, px(32));
        name.setTextColor(Color.WHITE);
        FrameLayout box = new FrameLayout(builder.getContext());
        box.setPadding(px(40), px(16), px(40), 0);
        box.addView(name, new FrameLayout.LayoutParams(-1, -2));
        final AlertDialog dialog = builder.setTitle(Strings.t("Rename"))
                .setView(box)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        Runnable save = () -> {
            setInputName(id, name.getText().toString());
            dialog.dismiss();
            focusedKey = id;
            buildTiles();
        };
        name.setOnEditorActionListener((v, action, event) -> {
            if (action == EditorInfo.IME_ACTION_DONE) {
                save.run();
                return true;
            }
            return false;
        });
        Window window = dialog.getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(px(RADIUS));
        bg.setColor(0xFF231D1F);
        bg.setStroke(Math.max(1, px(2)), 0x17FFFFFF);
        window.setBackgroundDrawable(bg);
        dialog.show();
        window.setLayout(px(800), ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> save.run());
        name.requestFocus();
        name.selectAll();
        name.post(() -> {
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                    .showSoftInput(name, InputMethodManager.SHOW_IMPLICIT);
            // Some TV keyboards move the cursor on startup; reselect after their opening animation.
            name.postDelayed(() -> {
                if (dialog.isShowing() && name.hasFocus()
                        && initialName.equals(name.getText().toString())) name.selectAll();
            }, 500);
        });
    }

    private void hideInput(String id) {
        Set<String> hidden = hiddenInputs();
        hidden.add(id);
        saveHiddenInputs(hidden);
        focusedKey = ADD;
        buildTiles();
    }

    // Long-press on the + tile: the inputs that were taken off the home screen, to put back.
    private void showHiddenInputs() {
        final List<String> ids = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        Set<String> hidden = hiddenInputs();
        for (TvInputInfo in : allInputs()) {
            if (!hidden.contains(in.getId())) continue;
            ids.add(in.getId());
            labels.add(inputLabel(in));
        }
        if (ids.isEmpty()) return;
        panel(Strings.t("Show hidden inputs"), labels, which -> {
            Set<String> now = hiddenInputs();
            now.remove(ids.get(which));
            saveHiddenInputs(now);
            focusedKey = ids.get(which);
            buildTiles();
        });
    }

    private SharedPreferences prefs() {
        return getSharedPreferences(PREFS, 0);
    }

    // Custom input names, keyed by the input's id. Split only at the first '=', so names can contain '='.
    private Map<String, String> inputNames() {
        Map<String, String> out = new HashMap<>();
        for (String line : prefs().getString(PREF_INPUT_NAMES, "").split("\n")) {
            int eq = line.indexOf('=');
            if (eq > 0) out.put(line.substring(0, eq), line.substring(eq + 1));
        }
        return out;
    }

    // A null or blank name goes back to the TV's own label. Keep names on one storage line.
    private void setInputName(String id, String name) {
        if (name != null) {
            name = name.replace("\r", "").replace("\n", "").trim();
            if (name.isEmpty()) name = null;
        }
        Map<String, String> names = inputNames();
        if (name == null) names.remove(id);
        else names.put(id, name);
        StringBuilder out = new StringBuilder();
        for (Map.Entry<String, String> e : names.entrySet()) {
            if (out.length() > 0) out.append('\n');
            out.append(e.getKey()).append('=').append(e.getValue());
        }
        prefs().edit().putString(PREF_INPUT_NAMES, out.toString()).apply();
    }

    private Set<String> hiddenInputs() {
        Set<String> out = new HashSet<>();
        for (String id : prefs().getString(PREF_HIDDEN_INPUTS, "").split("\n")) if (!id.isEmpty()) out.add(id);
        return out;
    }

    private void saveHiddenInputs(Set<String> ids) {
        prefs().edit().putString(PREF_HIDDEN_INPUTS, android.text.TextUtils.join("\n", new TreeSet<>(ids))).apply();
    }

    // Every launchable app that isn't on the top row yet, by name. The pick is added at the end,
    // or takes the place of the app at replaceIndex (-1 to add).
    private void showAddMenu(final int replaceIndex) {
        final PackageManager pm = getPackageManager();
        TreeMap<String, String> byLabel = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (String category : new String[] {Intent.CATEGORY_LEANBACK_LAUNCHER, Intent.CATEGORY_LAUNCHER}) {
            Intent q = new Intent(Intent.ACTION_MAIN).addCategory(category);
            for (ResolveInfo ri : pm.queryIntentActivities(q, 0)) {
                String p = ri.activityInfo.packageName;
                if (rowApps.contains(p) || byLabel.containsValue(p) || p.equals(getPackageName())
                        || (NOT_APPS.contains(p) && !p.equals(PLAY_STORE))) continue;
                String label = labelOf(p);
                if (!byLabel.containsKey(label)) byLabel.put(label, p);
            }
        }
        if (byLabel.isEmpty()) {
            panel(Strings.t("Every app is already here"), Collections.singletonList("OK"), which -> {});
            return;
        }
        final List<String> pkgs = new ArrayList<>(byLabel.values());
        String title = replaceIndex < 0 ? Strings.t("Add an app")
                : String.format(Strings.t("Replace %s"), labelOf(rowApps.get(replaceIndex)));
        final int iconSz = spx(44);
        panel(title, new ArrayList<>(byLabel.keySet()), pos -> pm.getApplicationIcon(pkgs.get(pos)),
                iconSz, -1, null, which -> {
            if (replaceIndex < 0) rowApps.add(pkgs.get(which));
            else rowApps.set(replaceIndex, pkgs.get(which));
            saveRow();
            focusedKey = pkgs.get(which);
            buildTiles();
        });
    }

    private void showAllAppsDrawer() {
        showAllAppsDrawer(false);
    }

    // Native Phone / Foldable / Tablet Searchable All Apps Bottom Sheet triggered by Swipe-Up or the All Apps Handle.
    // Built with true Android dp() & sp() dimensions so icons, typography, and touch targets feel 100% native.
    private void showAllAppsDrawer(final boolean startInPinMode) {
        if (activeDialog != null && activeDialog.isShowing()) {
            try { activeDialog.dismiss(); } catch (Exception ignored) {}
        }
        final PackageManager pm = getPackageManager();
        TreeMap<String, String> byLabel = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (String category : new String[] {Intent.CATEGORY_LAUNCHER, Intent.CATEGORY_LEANBACK_LAUNCHER}) {
            Intent q = new Intent(Intent.ACTION_MAIN).addCategory(category);
            for (ResolveInfo ri : pm.queryIntentActivities(q, 0)) {
                String p = ri.activityInfo.packageName;
                if (byLabel.containsValue(p) || p.equals(getPackageName())) continue;
                String label = labelOf(p);
                String key = label;
                int dup = 2;
                while (byLabel.containsKey(key)) key = label + " (" + (dup++) + ")";
                byLabel.put(key, p);
            }
        }
        final List<String> allLabels = new ArrayList<>(byLabel.keySet());
        final List<String> allPkgs = new ArrayList<>(byLabel.values());
        final List<String> filteredLabels = new ArrayList<>(allLabels);
        final List<String> filteredPkgs = new ArrayList<>(allPkgs);
        final Map<String, Drawable> iconCache = new HashMap<>();
        final boolean[] pinMode = {startInPinMode};

        final Dialog d = new Dialog(this, android.R.style.Theme_Material_Dialog_NoActionBar);
        activeDialog = d;

        int screenW = getResources().getDisplayMetrics().widthPixels;
        int screenH = getResources().getDisplayMetrics().heightPixels;

        LinearLayout sheet = new LinearLayout(this);
        sheet.setOrientation(LinearLayout.VERTICAL);
        sheet.setPadding(dp(20), dp(14), dp(20), dp(20));
        GradientDrawable sheetBg = new GradientDrawable();
        float topRad = dp(28);
        sheetBg.setCornerRadii(new float[] {topRad, topRad, topRad, topRad, 0, 0, 0, 0});
        sheetBg.setColor(0xF60B0E1A);
        sheetBg.setStroke(Math.max(1, dp(1)), 0x44F5D078);
        sheet.setBackground(sheetBg);

        // Top drag handle pill
        View handle = new View(this);
        GradientDrawable hBg = new GradientDrawable();
        hBg.setCornerRadius(dp(3));
        hBg.setColor(0x66FFE8B0);
        handle.setBackground(hBg);
        LinearLayout.LayoutParams hlp = new LinearLayout.LayoutParams(dp(42), dp(5));
        hlp.gravity = Gravity.CENTER_HORIZONTAL;
        hlp.bottomMargin = dp(14);
        sheet.addView(handle, hlp);

        // Header row: Title + App Count + One-Tap "★ Edit Home" / "✓ Done" toggle pill
        LinearLayout headerRow = new LinearLayout(this);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setGravity(Gravity.CENTER_VERTICAL);
        final TextView headTitle = text(sp(20f), 0xFFFFF4D2, semibold);
        headTitle.setSingleLine(true);
        headTitle.setEllipsize(android.text.TextUtils.TruncateAt.END);
        headerRow.addView(headTitle, new LinearLayout.LayoutParams(0, -2, 1f));

        final TextView pinToggleBtn = text(sp(13f), 0xFFFFF4D2, semibold);
        final GradientDrawable pinBtnBg = new GradientDrawable();
        pinBtnBg.setCornerRadius(dp(18));
        final Runnable updatePinHeader = () -> {
            headTitle.setText(pinMode[0] ? "Tap Apps to Pin / Unpin" : ("All Apps  \u00B7  " + filteredPkgs.size()));
            pinToggleBtn.setText(pinMode[0] ? "\u2713 Done" : "\u2605 Edit Home");
            pinToggleBtn.setTextColor(pinMode[0] ? 0xFF100D18 : 0xFFFFE8A8);
            pinBtnBg.setColor(pinMode[0] ? 0xFFFFD54F : 0x22FFFFFF);
            pinBtnBg.setStroke(Math.max(1, dp(1)), pinMode[0] ? 0xFFFFF4D2 : 0x48F5D078);
        };
        updatePinHeader.run();
        pinToggleBtn.setBackground(pinBtnBg);
        pinToggleBtn.setPadding(dp(16), dp(8), dp(16), dp(8));
        headerRow.addView(pinToggleBtn, new LinearLayout.LayoutParams(-2, -2));

        LinearLayout.LayoutParams hrlp = new LinearLayout.LayoutParams(-1, -2);
        hrlp.bottomMargin = dp(14);
        sheet.addView(headerRow, hrlp);

        // Modern 48dp Pill Search Bar with Search Icon & One-Tap Clear Button
        LinearLayout searchBox = new LinearLayout(this);
        searchBox.setOrientation(LinearLayout.HORIZONTAL);
        searchBox.setGravity(Gravity.CENTER_VERTICAL);
        searchBox.setMinimumHeight(dp(48));
        searchBox.setPadding(dp(16), dp(4), dp(12), dp(4));
        GradientDrawable sBg = new GradientDrawable();
        sBg.setCornerRadius(dp(24));
        sBg.setColor(0x1EFFFFFF);
        sBg.setStroke(Math.max(1, dp(1)), 0x38F5D078);
        searchBox.setBackground(sBg);

        ImageView sIcon = new ImageView(this);
        LineIcon searchLi = new LineIcon(LineIcon.SEARCH);
        searchLi.setColorFilter(0xCCFFE8B0, android.graphics.PorterDuff.Mode.SRC_IN);
        sIcon.setImageDrawable(searchLi);
        searchBox.addView(sIcon, new LinearLayout.LayoutParams(dp(20), dp(20)));

        final EditText searchInput = new EditText(this);
        searchInput.setHint("Search apps\u2026");
        searchInput.setHintTextColor(0x78FFE8B0);
        searchInput.setTextColor(0xFFFFFFFF);
        searchInput.setTextSize(TypedValue.COMPLEX_UNIT_PX, sp(16f));
        searchInput.setSingleLine(true);
        searchInput.setBackground(null);
        searchInput.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchInput.setPadding(dp(12), dp(6), dp(8), dp(6));
        searchBox.addView(searchInput, new LinearLayout.LayoutParams(0, -2, 1f));

        final TextView clearBtn = text(sp(14f), 0xCCFFE8B0, semibold);
        clearBtn.setText("\u2715");
        clearBtn.setGravity(Gravity.CENTER);
        clearBtn.setPadding(dp(10), dp(6), dp(10), dp(6));
        clearBtn.setVisibility(View.GONE);
        clearBtn.setOnClickListener(v -> searchInput.setText(""));
        searchBox.addView(clearBtn, new LinearLayout.LayoutParams(-2, -2));

        LinearLayout.LayoutParams sblp = new LinearLayout.LayoutParams(-1, -2);
        sblp.bottomMargin = dp(14);
        sheet.addView(searchBox, sblp);

        // Native Mobile Vertical Scroll List of all installed apps (48dp squircle icon + 16.5sp app name + interactive pin pill)
        final ListView list = new ListView(this);
        list.setDivider(new ColorDrawable(Color.TRANSPARENT));
        list.setDividerHeight(dp(8));
        list.setVerticalScrollBarEnabled(false);
        list.setOverScrollMode(View.OVER_SCROLL_NEVER);
        GradientDrawable listSel = new GradientDrawable();
        listSel.setCornerRadius(dp(18));
        listSel.setColor(0x24FFE8B0);
        list.setSelector(listSel);

        final int rowIconSize = dp(48);
        final int rowIconRadius = dp(13);
        final BaseAdapter adapter = new BaseAdapter() {
            @Override public int getCount() { return filteredPkgs.size(); }
            @Override public Object getItem(int pos) { return filteredPkgs.get(pos); }
            @Override public long getItemId(int pos) { return pos; }
            @Override public View getView(int pos, View convert, ViewGroup parent) {
                LinearLayout row;
                ImageView iconIv;
                TextView nameTv;
                TextView badgeTv;
                if (convert instanceof LinearLayout) {
                    row = (LinearLayout) convert;
                    FrameLayout box = (FrameLayout) row.getChildAt(0);
                    iconIv = (ImageView) box.getChildAt(0);
                    nameTv = (TextView) row.getChildAt(1);
                    badgeTv = (TextView) row.getChildAt(2);
                } else {
                    row = new LinearLayout(MainActivity.this);
                    row.setOrientation(LinearLayout.HORIZONTAL);
                    row.setGravity(Gravity.CENTER_VERTICAL);
                    row.setMinimumHeight(dp(66));
                    row.setPadding(dp(14), dp(9), dp(14), dp(9));

                    FrameLayout box = new FrameLayout(MainActivity.this);
                    GradientDrawable bBg = new GradientDrawable();
                    bBg.setCornerRadius(rowIconRadius);
                    bBg.setColor(0x380E101E);
                    box.setBackground(bBg);
                    box.setClipToOutline(true);

                    iconIv = new ImageView(MainActivity.this);
                    iconIv.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    box.addView(iconIv, new FrameLayout.LayoutParams(rowIconSize, rowIconSize));
                    row.addView(box, new LinearLayout.LayoutParams(rowIconSize, rowIconSize));

                    nameTv = text(sp(16.5f), 0xFAFFFFFF, semibold);
                    nameTv.setSingleLine(true);
                    nameTv.setEllipsize(android.text.TextUtils.TruncateAt.END);
                    nameTv.setGravity(Gravity.CENTER_VERTICAL);
                    LinearLayout.LayoutParams nlp = new LinearLayout.LayoutParams(0, -2, 1f);
                    nlp.leftMargin = dp(16);
                    nlp.rightMargin = dp(10);
                    row.addView(nameTv, nlp);

                    badgeTv = text(sp(12.5f), 0xFFFFE082, semibold);
                    badgeTv.setGravity(Gravity.CENTER);
                    badgeTv.setPadding(dp(12), dp(6), dp(12), dp(6));
                    row.addView(badgeTv, new LinearLayout.LayoutParams(-2, -2));
                }
                final String pkg = filteredPkgs.get(pos);
                String lbl = filteredLabels.get(pos);
                final boolean pinned = rowApps.contains(pkg);

                GradientDrawable rowBg = new GradientDrawable();
                rowBg.setCornerRadius(dp(18));
                rowBg.setColor(pinned ? 0x1E282014 : 0x12FFFFFF);
                rowBg.setStroke(Math.max(1, dp(1)), pinned ? 0x44FFD54F : 0x1EFFFFFF);
                row.setBackground(rowBg);

                nameTv.setText(lbl);
                nameTv.setTextColor(pinned ? 0xFFFFF8E8 : 0xF2FFFFFF);

                GradientDrawable badgeBg = new GradientDrawable();
                badgeBg.setCornerRadius(dp(14));
                if (pinned) {
                    badgeTv.setVisibility(View.VISIBLE);
                    badgeTv.setText(pinMode[0] ? "\u2605 Pinned" : "\u2605 On Home");
                    badgeTv.setTextColor(0xFFFFD54F);
                    badgeBg.setColor(0x26FFD54F);
                    badgeBg.setStroke(Math.max(1, dp(1)), 0x55FFD54F);
                } else {
                    badgeTv.setVisibility(View.VISIBLE);
                    badgeTv.setText("+ Pin");
                    badgeTv.setTextColor(pinMode[0] ? 0xFFE6FFE8 : 0x99FFE8B0);
                    badgeBg.setColor(pinMode[0] ? 0x22FFFFFF : 0x10FFFFFF);
                    badgeBg.setStroke(Math.max(1, dp(1)), pinMode[0] ? 0x44F5D078 : 0x24FFFFFF);
                }
                badgeTv.setBackground(badgeBg);
                badgeTv.setOnClickListener(v -> {
                    if (rowApps.contains(pkg)) rowApps.remove(pkg);
                    else rowApps.add(pkg);
                    saveRow();
                    buildTiles();
                    notifyDataSetChanged();
                });

                Drawable ic = iconCache.get(pkg);
                if (ic == null) {
                    try {
                        ic = pm.getApplicationIcon(pkg);
                        iconCache.put(pkg, ic);
                    } catch (Exception ignored) {}
                }
                iconIv.setImageDrawable(ic);
                return row;
            }
        };
        list.setAdapter(adapter);

        pinToggleBtn.setOnClickListener(v -> {
            pinMode[0] = !pinMode[0];
            updatePinHeader.run();
            adapter.notifyDataSetChanged();
        });

        list.setOnItemClickListener((parent, v, pos, id) -> {
            if (pos >= 0 && pos < filteredPkgs.size()) {
                String pkg = filteredPkgs.get(pos);
                if (pinMode[0]) {
                    if (rowApps.contains(pkg)) rowApps.remove(pkg);
                    else rowApps.add(pkg);
                    saveRow();
                    buildTiles();
                    adapter.notifyDataSetChanged();
                } else {
                    d.dismiss();
                    open(pkg);
                }
            }
        });

        list.setOnItemLongClickListener((parent, v, pos, id) -> {
            if (pos < 0 || pos >= filteredPkgs.size()) return false;
            final String pkg = filteredPkgs.get(pos);
            final boolean pinned = rowApps.contains(pkg);
            final boolean user = userInstalled(pkg);
            final String[] actions = user
                    ? new String[] {pinned ? "Remove from Home" : "Pin to Home", "App info", "Uninstall"}
                    : new String[] {pinned ? "Remove from Home" : "Pin to Home", "App info"};
            dropdown(v, actions, which -> {
                if (which == 0) {
                    if (pinned) rowApps.remove(pkg);
                    else rowApps.add(pkg);
                    saveRow();
                    buildTiles();
                    adapter.notifyDataSetChanged();
                } else if (which == 1) {
                    d.dismiss();
                    openAppInfo(pkg);
                } else if (which == 2) {
                    d.dismiss();
                    try { startActivity(new Intent(Intent.ACTION_DELETE, Uri.parse("package:" + pkg))); }
                    catch (Exception ignored) {}
                }
            });
            return true;
        });

        searchInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String q = s == null ? "" : s.toString().trim().toLowerCase(java.util.Locale.ROOT);
                clearBtn.setVisibility(q.isEmpty() ? View.GONE : View.VISIBLE);
                filteredLabels.clear();
                filteredPkgs.clear();
                for (int i = 0; i < allLabels.size(); i++) {
                    String lbl = allLabels.get(i);
                    String pk = allPkgs.get(i);
                    if (q.isEmpty() || lbl.toLowerCase(java.util.Locale.ROOT).contains(q)
                            || pk.toLowerCase(java.util.Locale.ROOT).contains(q)) {
                        filteredLabels.add(lbl);
                        filteredPkgs.add(pk);
                    }
                }
                updatePinHeader.run();
                adapter.notifyDataSetChanged();
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        sheet.addView(list, new LinearLayout.LayoutParams(-1, 0, 1f));
        d.setContentView(sheet);

        int sheetW = !isTvDevice() ? Math.min(screenW, dp(620)) : screenW;
        int sheetH = Math.round(screenH * 0.88f);
        Window win = d.getWindow();
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        win.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        win.setDimAmount(0.62f);
        win.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        win.setLayout(sheetW, sheetH);
        d.show();
        win.setLayout(sheetW, sheetH);
    }

    private void openAppInfo(String pkg) {
        try {
            startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + pkg))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } catch (Exception ignored) {}
    }

    // In-App Privacy Policy, Data Safety & OWASP MASVS Compliance Disclosure (Play Store Requirement)
    private void showPrivacySafetyDialog() {
        List<String> disclosures = Arrays.asList(
            "Zero Personal Data Collected (No Analytics / No Ads)",
            "App List (<queries>) Stays 100% On-Device",
            "HTTPS-Only Network Security (Weather Lookup Only)",
            "Local Preferences Stored Privately (MODE_PRIVATE)",
            "Optional Accessibility Helper (Google TV Home Only)",
            "Close"
        );
        panel("Privacy & Data Safety", disclosures, which -> {});
    }

    private String labelOf(String pkg) {
        PackageManager pm = getPackageManager();
        try { return pm.getApplicationLabel(pm.getApplicationInfo(pkg, 0)).toString(); }
        catch (Exception e) { return pkg; }
    }

    // Saves the top row as the app list, so it no longer follows apps.txt.
    private void saveRow() {
        getSharedPreferences(PREFS, 0).edit().putString("apps", android.text.TextUtils.join(",", rowApps)).apply();
    }

    // Where app number i sits in the grid (each row is horizontally centered; on phones/foldables, paged by 16 apps).
    private int gridX(int i) {
        int screenW = getResources().getDisplayMetrics().widthPixels;
        int g = gapPx > 0 ? gapPx : px(GAP);
        if (!isTvDevice()) {
            int page = i / MAX_PAGE_APPS;
            int idxInPage = i % MAX_PAGE_APPS;
            int row = idxInPage / cols;
            int col = idxInPage % cols;
            int pageStart = page * MAX_PAGE_APPS;
            int pageCount = Math.min(MAX_PAGE_APPS, Math.max(1, rowApps.size() - pageStart));
            int rowStart = row * cols;
            int countInRow = Math.min(cols, Math.max(1, pageCount - rowStart));
            int rowW = countInRow * tileW + (countInRow - 1) * g;
            int startX = page * screenW + (screenW - rowW) / 2;
            return startX + col * (tileW + g);
        }
        int row = i / cols;
        int col = i % cols;
        int rowStart = row * cols;
        int countInRow = Math.min(cols, Math.max(1, rowApps.size() - rowStart));
        int rowW = countInRow * tileW + (countInRow - 1) * g;
        int startX = (screenW - rowW) / 2;
        return startX + col * (tileW + g);
    }

    private int gridY(int i) {
        int step = rowStepPx > 0 ? rowStepPx : px(ROW_STEP);
        if (!isTvDevice()) {
            int idxInPage = i % MAX_PAGE_APPS;
            return (idxInPage / cols) * step;
        }
        return gridTop + (i / cols) * step;
    }

    private View tile(String key) {
        for (View t : tiles) if (t.getTag().equals(key)) return t;
        return null;
    }

    // Move mode: the tile gets an outline, the arrows move it through the grid, OK or Back finishes.
    private void startMove(String key) {
        View card = tile(key);
        if (card == null) return;
        movingKey = key;
        GradientDrawable outline = new GradientDrawable();
        outline.setCornerRadius(px(RADIUS + RADIUS_UP));
        outline.setStroke(px(4), Color.WHITE);
        if (android.os.Build.VERSION.SDK_INT >= 23) card.setForeground(outline);
        moveHint.setVisibility(View.VISIBLE);
        card.requestFocus();
    }

    private boolean moveKey(String key, int code, boolean ok, boolean down) {
        int step = code == KeyEvent.KEYCODE_DPAD_LEFT ? -1 : code == KeyEvent.KEYCODE_DPAD_RIGHT ? 1
                : code == KeyEvent.KEYCODE_DPAD_UP ? -cols : code == KeyEvent.KEYCODE_DPAD_DOWN ? cols : 0;
        if (step != 0) {
            int from = rowApps.indexOf(key), to = from + step;
            if (down && to >= 0 && to < rowApps.size()) {
                // Slide the tiles into their new places: no rebuild, so it stays quick on slow TVs.
                rowApps.add(to, rowApps.remove(from));
                for (int i = 0; i < rowApps.size(); i++) {
                    View wrap = (View) tile(rowApps.get(i)).getParent();
                    FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) wrap.getLayoutParams();
                    lp.leftMargin = gridX(i);
                    lp.topMargin = gridY(i);
                    wrap.setLayoutParams(lp);
                }
                if (!isTvDevice() && appPager != null) {
                    int newPage = to / MAX_PAGE_APPS;
                    if (newPage != currentAppPage) {
                        currentAppPage = newPage;
                        updatePageDots(newPage);
                        int sw = getResources().getDisplayMetrics().widthPixels;
                        appPager.smoothScrollTo(newPage * sw, 0);
                    }
                }
                // Scroll along with it once it has moved.
                final View card = tile(key);
                card.post(() -> card.requestRectangleOnScreen(
                        new android.graphics.Rect(0, 0, card.getWidth(), card.getHeight())));
            }
            return true;
        }
        if (ok || code == KeyEvent.KEYCODE_BACK || code == KeyEvent.KEYCODE_MENU) {
            if (!down) {
                movingKey = null;
                longPressed = false;
                if (isTvDevice()) {
                    GradientDrawable border = new GradientDrawable();
                    border.setCornerRadius(px(RADIUS + RADIUS_UP));
                    border.setColor(Color.TRANSPARENT);
                    border.setStroke(Math.max(1, px(3)), 0x90FFFFFF);
                    if (android.os.Build.VERSION.SDK_INT >= 23) tile(key).setForeground(border);
                } else {
                    if (android.os.Build.VERSION.SDK_INT >= 23) tile(key).setForeground(null);
                }
                moveHint.setVisibility(View.GONE);
                saveRow();
                refreshApps();
            }
            return true;
        }
        return true; // up/down and everything else stay put while moving
    }

    private static boolean isInput(String key) {
        return key.indexOf('/') > 0;
    }

    // The TV's own HDMI inputs (not devices behind them), in order, skipping the ones taken off
    // the home screen from the tile menu.
    private List<TvInputInfo> hdmiInputs() {
        List<TvInputInfo> out = new ArrayList<>();
        Set<String> hidden = hiddenInputs();
        for (TvInputInfo in : allInputs()) if (!hidden.contains(in.getId())) out.add(in);
        return out.size() > MAX_INPUTS ? out.subList(0, MAX_INPUTS) : out;
    }

    // HDMI inputs are what a home screen shows. Include parent HDMI ports even when a CEC child
    // (such as Apple TV / FancyLEDs Sync Box on HW17) causes TCL to mark the parent port hidden.
    private boolean countsAsInput(TvInputInfo in) {
        boolean fake = "1".equals(prefs().getString(PREF_FAKE_INPUTS, null));
        return (fake || in.getType() == TvInputInfo.TYPE_HDMI) && in.getParentId() == null;
    }

    // Every HDMI input the TV reports, sorted with actively connected ports (e.g. HDMI 3 FancyLEDs) first.
    private List<TvInputInfo> allInputs() {
        List<TvInputInfo> out = new ArrayList<>();
        try {
            final TvInputManager tim = (TvInputManager) getSystemService(Context.TV_INPUT_SERVICE);
            if (tim == null) return out;
            for (TvInputInfo in : tim.getTvInputList()) if (countsAsInput(in)) out.add(in);
            Collections.sort(out, (a, b) -> {
                boolean ca = false, cb = false;
                try { ca = tim.getInputState(a.getId()) == TvInputManager.INPUT_STATE_CONNECTED; } catch (Throwable ignored) {}
                try { cb = tim.getInputState(b.getId()) == TvInputManager.INPUT_STATE_CONNECTED; } catch (Throwable ignored) {}
                if (ca != cb) return ca ? -1 : 1;
                return a.getId().compareTo(b.getId());
            });
        } catch (Throwable ignored) {}
        return out;
    }

    // A TV has inputs of its own (HDMI, AV); a box that plugs into one, like NVIDIA Shield, Chromecast
    // or a Mi Box, has none. Some TVs register their HDMI inputs a while after starting (#52), so with
    // none yet it's a box only if HDMI-CEC says it's a playback device and not a TV. When unsure, it's
    // a TV, so TVs keep their menu.
    static boolean isBox(Context c) {
        try {
            TvInputManager tim = (TvInputManager) c.getSystemService(Context.TV_INPUT_SERVICE);
            if (tim != null) for (TvInputInfo in : tim.getTvInputList()) if (in.isPassthroughInput()) return false;
        } catch (Throwable ignored) {}
        List<String> types = cecTypes("ro.hdmi.device_type"), named = cecTypes("ro.hdmi.cec_device_types");
        if (types.contains("0") || named.contains("tv")) return false;
        return types.contains("4") || named.contains("playback_device");
    }

    private static List<String> cecTypes(String prop) {
        try {
            String v = String.valueOf(Class.forName("android.os.SystemProperties")
                    .getMethod("get", String.class).invoke(null, prop)).trim();
            if (!v.isEmpty()) return Arrays.asList(v.split("\\s*,\\s*"));
        } catch (Throwable ignored) {}
        return Collections.emptyList();
    }

    private TvInputInfo inputById(String id) {
        for (TvInputInfo in : allInputs()) if (in.getId().equals(id)) return in;
        return null;
    }

    // The name picked on the TV, else the one set in Settings > Inputs (like "Bedroom"), else the
    // TV's own name (like "HDMI 1").
    private String inputLabel(TvInputInfo in) {
        String custom = inputNames().get(in.getId());
        return custom != null ? Strings.t(custom) : tvInputLabel(in);
    }

    private String tvInputLabel(TvInputInfo in) {
        if (in.getId().endsWith("/HW17")) return "FANCYLEDS";
        String custom = inputLabelOrNull(in.loadCustomLabel(this));
        if (custom != null) return custom;
        String label = inputLabelOrNull(in.loadLabel(this));
        return label != null ? label : "HDMI";
    }

    private static String inputLabelOrNull(CharSequence raw) {
        if (raw == null || raw.length() == 0) return null;
        String label = withoutDeviceType(raw.toString());
        return "Philips".equalsIgnoreCase(android.os.Build.MANUFACTURER)
                && (label.isEmpty() || "null".equals(label)) ? null : label;
    }

    // Philips stores a name as "name&DEVICE_TYPE" (like "Computer&COMPUTER"), so show the name only.
    // Missing Philips names/types can be the literal "null"; a missing name uses the next fallback.
    // Only on Philips TVs: elsewhere a name like "AUDIO&VIDEO" is the name itself.
    private static String withoutDeviceType(String label) {
        if (!"Philips".equalsIgnoreCase(android.os.Build.MANUFACTURER)) return label;
        int amp = label.lastIndexOf('&');
        if (amp >= 0 && (label.endsWith("&") || label.endsWith("&null"))) return label.substring(0, amp).trim();
        return amp > 0 && label.substring(amp + 1).matches("[A-Z][A-Z0-9_]{2,}") ? label.substring(0, amp).trim() : label;
    }

    private boolean userInstalled(String pkg) {
        try { return (getPackageManager().getApplicationInfo(pkg, 0).flags & ApplicationInfo.FLAG_SYSTEM) == 0; }
        catch (PackageManager.NameNotFoundException e) { return false; }
    }

    private static Intent launchIntent(PackageManager pm, String pkg) {
        Intent i = pm.getLeanbackLaunchIntentForPackage(pkg);
        return i != null ? i : pm.getLaunchIntentForPackage(pkg);
    }

    // Xiaomi TVs ship MiLan Pro but don't register it by name; every other TV gets Roboto.
    private static void loadFonts(Context c) {
        if (regular != null) return;
        regular = fromFile("/system/fonts/MILanPro_Normal.ttf", Typeface.create("sans-serif", Typeface.NORMAL));
        semibold = fromFile("/system/fonts/MILanPro_SemiBold.ttf", Typeface.create("sans-serif-medium", Typeface.NORMAL));
        try {
            defaultThemeFont = Typeface.createFromAsset(c.getAssets(), "harryp.ttf");
        } catch (Throwable t) {
            defaultThemeFont = semibold;
        }
        try {
            cinzelFont = Typeface.createFromAsset(c.getAssets(), "cinzel.ttf");
        } catch (Throwable t) {
            cinzelFont = semibold;
        }
        String[] fontAssets = {"harryp.ttf", "naruto.ttf", "demonslayer.ttf", "starwars.ttf", "mcu.ttf", "dcu.ttf"};
        for (int i = 0; i < fontAssets.length; i++) {
            try {
                themeFonts[i] = Typeface.createFromAsset(c.getAssets(), fontAssets[i]);
            } catch (Throwable t) {
                themeFonts[i] = defaultThemeFont;
            }
        }
    }

    // Formats "| 62°F Overcast" in the Harry Potter font while styling the '°' glyph with cinzelFont
    // because harryp.ttf does not include the Unicode degree character (\u00B0).
    private void setWeatherText(String raw) {
        if (weatherView == null || raw == null || raw.isEmpty()) return;
        String clean = "|   " + raw.trim();
        SpannableString ss = new SpannableString(clean);
        int deg = clean.indexOf('\u00B0');
        if (deg >= 0 && cinzelFont != null) {
            final Typeface tf = cinzelFont;
            ss.setSpan(new MetricAffectingSpan() {
                @Override public void updateDrawState(TextPaint tp) { tp.setTypeface(tf); }
                @Override public void updateMeasureState(TextPaint tp) { tp.setTypeface(tf); }
            }, deg, deg + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        weatherView.setText(ss);
    }

    private void fetchWeatherAsync() {
        long now = System.currentTimeMillis();
        if (now - lastWeatherFetch < 15 * 60 * 1000L) return;
        lastWeatherFetch = now;
        Thread t = new Thread(() -> {
            String result = fetchWttr();
            if (result == null) result = fetchOpenMeteo();
            if (result != null && !result.isEmpty()) {
                final String w = result;
                prefs().edit().putString("weather_text", w).apply();
                runOnUiThread(() -> setWeatherText(w));
            }
        }, "weather-fetch");
        t.setDaemon(true);
        t.start();
    }

    private static String fetchWttr() {
        java.net.HttpURLConnection conn = null;
        try {
            conn = (java.net.HttpURLConnection) new java.net.URL("https://wttr.in/?format=%t+%C").openConnection();
            conn.setConnectTimeout(3500);
            conn.setReadTimeout(3500);
            conn.setRequestProperty("User-Agent", "curl/7.88.1");
            if (conn.getResponseCode() == 200) {
                java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
                String line = br.readLine();
                br.close();
                if (line != null) {
                    line = line.trim().replace("+", "").replaceAll("\\s+", " ");
                    if (!line.isEmpty() && !line.contains("Unknown") && line.length() <= 32) return line;
                }
            }
        } catch (Throwable ignored) {
        } finally {
            if (conn != null) conn.disconnect();
        }
        return null;
    }

    private static String fetchOpenMeteo() {
        java.net.HttpURLConnection conn = null;
        try {
            conn = (java.net.HttpURLConnection) new java.net.URL(
                    "https://api.open-meteo.com/v1/forecast?latitude=39.77&longitude=-86.16&current_weather=true&temperature_unit=fahrenheit")
                    .openConnection();
            conn.setConnectTimeout(3500);
            conn.setReadTimeout(3500);
            if (conn.getResponseCode() == 200) {
                java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                br.close();
                String json = sb.toString();
                int cw = json.indexOf("\"current_weather\"");
                if (cw >= 0) {
                    String sub = json.substring(cw);
                    int ti = sub.indexOf("\"temperature\":");
                    int wi = sub.indexOf("\"weathercode\":");
                    if (ti >= 0) {
                        int ts = ti + 14;
                        int te = sub.indexOf(',', ts);
                        float temp = Float.parseFloat(sub.substring(ts, te).trim());
                        int code = 0;
                        if (wi >= 0) {
                            int ws = wi + 14;
                            int we = ws;
                            while (we < sub.length() && Character.isDigit(sub.charAt(we))) we++;
                            code = Integer.parseInt(sub.substring(ws, we));
                        }
                        String cond = code == 0 ? "Clear Sky" : code <= 2 ? "Partly Cloudy" : code == 3 ? "Overcast"
                                : code <= 48 ? "Misty" : code <= 67 ? "Rain" : code <= 77 ? "Snow" : "Stormy";
                        return Math.round(temp) + "\u00B0F " + cond;
                    }
                }
            }
        } catch (Throwable ignored) {
        } finally {
            if (conn != null) conn.disconnect();
        }
        return null;
    }

    private static Typeface fromFile(String path, Typeface fallback) {
        try { return new File(path).exists() ? Typeface.createFromFile(path) : fallback; }
        catch (Exception e) { return fallback; }
    }

    // Draws a banner once into a tile-sized bitmap (center-cropped). Some apps ship huge banners
    // that Android 9 refuses to draw ("Canvas: trying to draw too large bitmap"); this also saves RAM.
    private Drawable fit(Drawable d, int w, int h) {
        try {
            Bitmap b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            int iw = d.getIntrinsicWidth(), ih = d.getIntrinsicHeight();
            if (iw <= 0 || ih <= 0) { iw = w; ih = h; }
            float s = Math.max((float) w / iw, (float) h / ih);
            int dw = Math.round(iw * s), dh = Math.round(ih * s);
            d.setBounds((w - dw) / 2, (h - dh) / 2, (w - dw) / 2 + dw, (h - dh) / 2 + dh);
            d.draw(new Canvas(b));
            return new BitmapDrawable(getResources(), b);
        } catch (Throwable t) {
            return d;
        }
    }

    private int px(int v) {
        return Math.round(v * scale);
    }

    private TextClock clock(String f12, String f24, int px, int color, Typeface tf) {
        TextClock c = new TextClock(this);
        c.setFormat12Hour(f12);
        c.setFormat24Hour(f24);
        c.setTextSize(TypedValue.COMPLEX_UNIT_PX, px);
        c.setTextColor(color);
        c.setTypeface(tf);
        return c;
    }

    private TextView text(int px, int color, Typeface tf) {
        TextView t = new TextView(this);
        t.setTextSize(TypedValue.COMPLEX_UNIT_PX, px);
        t.setTextColor(color);
        if (tf != null) t.setTypeface(tf);
        return t;
    }

    private static FrameLayout.LayoutParams at(int x, int y, int w, int h) {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(w, h);
        lp.leftMargin = x;
        lp.topMargin = y;
        return lp;
    }
}
