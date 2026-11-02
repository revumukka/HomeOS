package com.ansub.tvhome;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.accessibility.AccessibilityEvent;

// Opt-in (Settings > Accessibility > Home button): makes Home open this home screen on TVs where the
// TV's own home screen has to stay on, like Google TV and Chromecast. Nothing gets turned off, so
// switching it off puts Home straight back to the TV's own.
//
// Google TV handles the Home key before accessibility services see it, so this doesn't watch keys.
// It watches for Google TV's main screen (not its other pages) to open, after Home or a restart,
// and opens this one over it.
public class HomeButton extends AccessibilityService {
    private static final ComponentName GOOGLE_TV_HOME = new ComponentName(
            "com.google.android.apps.tv.launcherx", "com.google.android.apps.tv.launcherx.home.HomeActivity");
    private static final String SERVICE_ID = "com.ansub.tvhome/com.ansub.tvhome.HomeButton";
    // Debounce duplicate events until the activity resumes.
    private static long lastOpened = -500;
    private static volatile boolean bound = false;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        bound = true;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        bound = false;
        return super.onUnbind(intent);
    }

    // TCL's background manager only binds the accessibility service once our process is running.
    // With WRITE_SECURE_SETTINGS granted via ADB, re-trigger the bind automatically on boot/launch.
    static void ensureBound(final Context c) {
        if (bound) return;
        final Context app = c.getApplicationContext();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (bound) return;
            try {
                ContentResolver cr = app.getContentResolver();
                Settings.Secure.putString(cr, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, "");
                Settings.Secure.putInt(cr, Settings.Secure.ACCESSIBILITY_ENABLED, 0);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    try {
                        Settings.Secure.putString(cr, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, SERVICE_ID);
                        Settings.Secure.putInt(cr, Settings.Secure.ACCESSIBILITY_ENABLED, 1);
                    } catch (Throwable ignored) {}
                }, 400);
            } catch (Throwable ignored) {}
        }, 600);
    }

    public static class BootReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                context.startActivity(new Intent(context, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            } catch (Throwable ignored) {}
            ensureBound(context);
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                || !GOOGLE_TV_HOME.getPackageName().equals(String.valueOf(event.getPackageName()))
                || !String.valueOf(event.getClassName()).contains("HomeActivity") || !setupDone()) return;
        SharedPreferences prefs = prefs(this);
        if (stayingOnGoogleTv(prefs)) return;
        long now = SystemClock.elapsedRealtime();
        if (now - lastOpened < 150) return;
        lastOpened = now;
        try {
            startActivity(new Intent(this, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
        } catch (Exception ignored) {}
    }

    @Override
    public void onInterrupt() {}

    // During the TV's first setup, Home is left alone.
    private boolean setupDone() {
        return Settings.Secure.getInt(getContentResolver(), "user_setup_complete", 1) == 1;
    }

    private static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences("home_button", 0);
    }

    // Back on this home screen: Home comes here again.
    static void onHomeResumed(Context c) {
        prefs(c).edit().remove("google_tv_boot").remove("google_tv_boot_count").commit();
    }

    // This home screen opened and didn't crash. Also resets a stopped redirect loop.
    static void onHomeOk(Context c) {
        prefs(c).edit().remove("pending").remove("failures").commit();
        lastOpened = -500;
    }

    private static int bootCount(Context c) {
        return Build.VERSION.SDK_INT >= 24
                ? Settings.Global.getInt(c.getContentResolver(), Settings.Global.BOOT_COUNT, -1) : -1;
    }

    private static long bootTime() {
        return System.currentTimeMillis() - SystemClock.elapsedRealtime();
    }

    private boolean stayingOnGoogleTv(SharedPreferences prefs) {
        if (!prefs.contains("google_tv_boot")) return false;
        int boot = bootCount(this);
        int saved = prefs.getInt("google_tv_boot_count", -1);
        if (boot >= 0 && saved >= 0) return boot == saved;
        return Math.abs(bootTime() - prefs.getLong("google_tv_boot", 0)) < 5000;
    }

    // Opens Google TV's own home screen and keeps it there. False if this TV doesn't have it.
    static boolean openGoogleTv(Context c) {
        SharedPreferences prefs = prefs(c);
        // Keep the choice through process death and updates, but not a TV restart.
        if (!prefs.edit().putLong("google_tv_boot", bootTime())
                .putInt("google_tv_boot_count", bootCount(c)).commit()) return false;
        try {
            c.startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
                    .setComponent(GOOGLE_TV_HOME).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            return true;
        } catch (Exception e) {
            prefs.edit().remove("google_tv_boot").remove("google_tv_boot_count").commit();
            return false;
        }
    }
}
